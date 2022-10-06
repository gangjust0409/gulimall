package cn.bdqn.gulimall.product.service.impl;

import cn.bdqn.gulimall.product.service.CategoryBrandRelationService;
import cn.bdqn.gulimall.product.vo.Catelog2Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mysql.cj.util.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bdqn.gulimall.common.utils.PageUtils;
import cn.bdqn.gulimall.common.utils.Query;

import cn.bdqn.gulimall.product.dao.CategoryDao;
import cn.bdqn.gulimall.product.entity.CategoryEntity;
import cn.bdqn.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    // 获取所有分类菜单列表
    @Override
    public List<CategoryEntity> withCategoryTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null); // 所有的菜单

        // 获取所有父级菜单
        List<CategoryEntity> categoryEntityList = categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(menu -> {
            menu.setChildens(getChilderns(menu, categoryEntities));
            return menu;
        }).sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort())
        ).collect(Collectors.toList());

        return categoryEntityList;
    }

    @Override
    public void removeCategoryByIds(List<Long> asList) {
        baseMapper.deleteBatchIds(asList); // 逻辑删除
    }

    @Override
    public Long[] findAttrGroupLondPath(Long attrGroupId) {
        List<Long> path = new ArrayList<>();
        // 孙，子，父
        List<Long> longPath = findLongPath(attrGroupId, path);
        Collections.reverse(longPath);
        // 父，子，孙

        return longPath.toArray(new Long[longPath.size()]);
    }

//    @CacheEvict(value = "category",key = "'selectBaseMenus'")
    /*  1. 批量删除key
        @Caching(evict = {
            @CacheEvict(value = "category",key = "'selectBaseMenus'"),
            @CacheEvict(value = "category",key = "'searchLevelMenus'")
        })*/
    // 2. 直接批量删除 category 分区的所有内容  开启批量删除 allEntries

    /**
     * springcache 的不足
     * 1：读模式
     *      缓存穿透：查询一个null数据。 解决：缓存空数据 cache-null-value=true
     *      缓存击穿：大量并发发进来同时查询一个正好过期的数据。 解决：加锁，默认是不加锁的，@Cacheable 注解上加 async
     *      缓存雪崩：大量key同时过期。解决：加随机时间。 加上过期时间
     *  2;写模式（缓存与数据库一致）
     *      读写加锁、引入canal，感知到MySQL的更新数据库、读多写多，直接去数据库查询就行
     *  总结：
     *      常规数据（读多写少，即时性，一致性要求不高的数据）；完全可以使用springcache
     *      特殊数据：特殊设计
     *
     * @param category
     */
    @CacheEvict(value = {"category"},allEntries = true)
    @Override
    public void updateDetil(CategoryEntity category) {
        // 先自身修改
        this.updateById(category);
        // 在修改关联的
        if (!StringUtils.isNullOrEmpty(category.getName())) {
            categoryBrandRelationService.findUpdateCategory(category.getCatId(), category.getName());
        }
    }

    /**
     * 每一个需要缓存的数据我们都来指定要放到哪个名字的缓存【缓存的分区（按照业务类型分）】
     * @Cacheable({"category"})
     *   代表当前方法的结果需要缓存，如果缓存中有，方法不用调用
     *   如果缓存中没有，会调用方法，最后方法的结果放入缓存
     * 默认行为
     *      如果缓存中有，方法不用调用
     *      key 默认自动生成 category::SimpleKey []（自主生成的key值）
     *      缓存的value的值。默认使用jdk序列化机制，将序列化后的数据到redis
     *      默认ttl时间 -1
     *   自定义
     *      指定生成的缓存的使用key   以每个方法名作为每个缓存的key
     *          使用的 spel 表达式
     *      指定缓存的数据存活时间   通过配置
     *
     *      将数据保存为json 格式
     * @return
     */
    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public List<CategoryEntity> selectBaseMenus() {
        System.out.println("selectBaseMenus");
        // 查询所有的 一级菜单
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> searchLevelMenus(){
        System.out.println("查询数据库！");
        // 优化方案一，减少查询数据库，只查询一次
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 先查询一级分类的
        List<CategoryEntity> categoryEntities = getParent_cid(selectList, 0L);

        Map<String, List<Catelog2Vo>> collect = categoryEntities.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 在查询二级分类
            List<CategoryEntity> categoryL2s = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> parent_cid = null;
            if (categoryL2s != null) {
                parent_cid = categoryL2s.stream().map(l2 -> {
                    // 封装 vo
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 在查询三级分类数据
                    List<CategoryEntity> categoryL3s = getParent_cid(selectList, l2.getCatId());
                    if (categoryL3s != null) {
                        List<Catelog2Vo.CateLog3Vo> cateL3List = categoryL3s.stream().map(l3 -> {
                            Catelog2Vo.CateLog3Vo cateLog3Vo = new Catelog2Vo.CateLog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return cateLog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(cateL3List);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return parent_cid;
        }));

        return collect;
    }

    //TODD 内存溢出 OutOfDirectMemoryError

    /**
     * springbbot2.0 以后使用lettuce 作为 redis 的客户端，它使用netty进行网络通信
     * lettuce 的bug导致netty堆外内容溢出 netty 如果没有指定推外内存，默认使用-Xmx300m
     * 可以通过-Dio.netty.maxDirectMenmory 进行设置
     * 解决方案
     *  1 升级lettuce 客户端 2 使用jedis客户端
     * @return
     */
//    @Override
    public Map<String, List<Catelog2Vo>> searchLevelMenus1() {
        // 解决缓存穿透 将null结果缓存
        // 解决缓存雪崩 设置原有的失效时间基础上加上随机数
        // 解决缓存击穿 加锁

        // 使用 reids 改业务
        String catelogJSON = redisTemplate.opsForValue().get("catelogJSON");
        // 判断 redis 中是否有数据
        if (org.springframework.util.StringUtils.isEmpty(catelogJSON)) {
            System.out.println("缓存未击中，查询数据库");
            // 没有数据，需要在db中查找
            Map<String, List<Catelog2Vo>> catelogMap = searchLevelMenusDbWithRedissonLock();
            return catelogMap;
        }
        System.out.println("缓存击中");
        // 序列化与反序列化：把对象转为 json 把json转成对象
        Map<String, List<Catelog2Vo>> cateMap = JSON.parseObject(catelogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        return cateMap;
    }

    /**
     * 解决缓存一致性问题：
     *  双写模式，设置过期时间
     *  失效模式，写数据库，删除缓存。缓存数据都设置了过期时间，数据过期下一次查询触发更新，读写的时候，加入分布式读写锁。
     *              读多写少对系统没有影响
     *  canel 阿里巴巴的，监控MySQL数据是否更改，如果更改则更改缓存
     * @return
     */
    // 分布式锁redisson
    public Map<String, List<Catelog2Vo>> searchLevelMenusDbWithRedissonLock() {
        // 设置分布式锁
        RLock lock = redisson.getLock("catelogjson-lock");
        Map<String, List<Catelog2Vo>> listMap;
        try{
            // 上锁成功
            lock.lock();
            listMap = getDataFormDb();
        } finally {
            // 释放锁
            lock.unlock();
        }
        return listMap;
    }

    // 分布式锁
    public Map<String, List<Catelog2Vo>> searchLevelMenusDbWithRedisLock() {

        // 设置分布式锁，去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功...");
            Map<String, List<Catelog2Vo>> listMap;
            try{
                // 上锁成功
                listMap = getDataFormDb();
            } finally {
                // 不管错误还是正确，都必须关锁
                // 这里需要解锁，lua 脚本
                String redisScript = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Long res = redisTemplate.execute(new DefaultRedisScript<Long>(redisScript, Long.class), Arrays.asList("lock"), uuid);
            }
            return listMap;
        } else {
            System.out.println("获取分布式锁失败...休眠了200s");
            // 上锁失败，重试
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 自旋
           return searchLevelMenusDbWithRedisLock();
        }

    }

    private Map<String, List<Catelog2Vo>> getDataFormDb() {
        // 查询数据库前在查询redis中是否有当前key
        String catelogJSON = redisTemplate.opsForValue().get("catelogJSON");
        if (!org.springframework.util.StringUtils.isEmpty(catelogJSON)) {
            Map<String, List<Catelog2Vo>> cateMap = JSON.parseObject(catelogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return cateMap;
        }
        System.out.println("查询数据库！");
        // 优化方案一，减少查询数据库，只查询一次
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 先查询一级分类的
        List<CategoryEntity> categoryEntities = getParent_cid(selectList, 0L);

        Map<String, List<Catelog2Vo>> collect = categoryEntities.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 在查询二级分类
            List<CategoryEntity> categoryL2s = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> parent_cid = null;
            if (categoryL2s != null) {
                parent_cid = categoryL2s.stream().map(l2 -> {
                    // 封装 vo
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 在查询三级分类数据
                    List<CategoryEntity> categoryL3s = getParent_cid(selectList, l2.getCatId());
                    if (categoryL3s != null) {
                        List<Catelog2Vo.CateLog3Vo> cateL3List = categoryL3s.stream().map(l3 -> {
                            Catelog2Vo.CateLog3Vo cateLog3Vo = new Catelog2Vo.CateLog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return cateLog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(cateL3List);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return parent_cid;
        }));

        // 设置进 redis，保证在释放锁之前只查询一次数据库
        String s = JSON.toJSONString(collect);
        redisTemplate.opsForValue().set("catelogJSON", s, 1, TimeUnit.DAYS);
        return collect;
    }

    // 本地锁
    public Map<String, List<Catelog2Vo>> searchLevelMenusDbWithLocalLock() {

        // 本地锁 this JUC lock
        synchronized (this) {
            // 查询数据库前在查询redis中是否有当前key
            return getDataFormDb();
        }
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long catId) {
        List<CategoryEntity> collect = selectList.stream().filter(f -> f.getParentCid().equals(catId)).collect(Collectors.toList());
        return collect;
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
    }

    private List<Long> findLongPath(Long id, List<Long> path) {
        CategoryEntity categoryEntity = this.getById(id);
        // 先把当前菜单存入集合
        if (id != 0) {
            path.add(id);
            findLongPath(categoryEntity.getParentCid(), path);
        }
        return path;
    }

    /**
     * 使用递归遍历所有的子集菜单
     * @param category
     * @param all
     * @return
     */
    private List<CategoryEntity> getChilderns(CategoryEntity category, List<CategoryEntity> all) {
        // categoryEntity.getParentCid().equals(category.getCatId() 必须用equals 判断不然新添加的二级菜单新添加的三级菜单无法刷新
        List<CategoryEntity> list = all.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(category.getCatId())
        ).map(menu -> {
            menu.setChildens(getChilderns(menu, all));
            return menu;
        }).sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort())
        ).collect(Collectors.toList());

        return list;

    }


}