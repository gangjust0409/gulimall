package cn.bdqn.gulimall.product.web;

import cn.bdqn.gulimall.product.entity.CategoryEntity;
import cn.bdqn.gulimall.product.service.CategoryService;
import cn.bdqn.gulimall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;

    // 首页
    @GetMapping({"/","/index.html"})
    public String indexPage(Model model) {
        // 查询出所有的 一级菜单
        List<CategoryEntity> categoryEntities = categoryService.selectBaseMenus();
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    // 渲染二级三级菜单
    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> levelMenus() {
        Map<String, List<Catelog2Vo>> map = categoryService.searchLevelMenus();
        return map;
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello(){
        // lock可重入锁  获取锁，只要名字一样就是同一把锁
        RLock lock = redisson.getLock("my-lock");
//        lock.lock(); // 阻塞时等待，默认锁时间是30s

        // 不设置过期时间，有关门狗续期过期时间默认30s
        // 设置过期时间，没有关门狗续期过期时间
        // 推荐使用设置过期时间
        lock.lock(10, TimeUnit.SECONDS);
        try {
            // 锁的自动续期，如果业务超长，那么运行期间自动给锁设置30s，
            // 加锁代码执行完毕，就不会给当前锁续期了，如果不手动解锁，也会在30s之后解锁
            System.out.println("加锁成功！执行业务。。。"+Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e){

        } finally {
            // 关锁，如果解锁代码不运行，那么会自动解锁
            System.out.println("关锁"+Thread.currentThread().getId());
            lock.unlock();
        }

        return "hello";
    }

    // 修改期间，保证得到最新数据，写锁是一个排他锁（互斥锁、独享锁），读锁是一个共享锁
    // 写锁没有释放，读锁就得等待。

    /**
     * 读写 有读写，写需要等待
     * 写写  阻塞方式
     * 只要写存在，都需要等待
     * 读读 相当于无锁，只会reis记录好，都会同时加锁
     * @return
     */
    @GetMapping("/write")
    @ResponseBody
    public String writeFun(){
        // 读写锁的使用，改数据加写锁，读数据加读锁
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock lock = readWriteLock.writeLock();
        String s = "";
        try {
            s = UUID.randomUUID().toString();
            lock.lock();
            System.out.println("写加锁成功！"+Thread.currentThread().getId());
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue", s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("写释放锁成功！"+Thread.currentThread().getId());
        }
        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String readFun(){
        // 读写锁的使用，改数据加写锁，读数据加读锁
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock lock = readWriteLock.readLock();
        String s = "";
        try {
            lock.lock();
            System.out.println("读加锁成功！"+Thread.currentThread().getId());
            Thread.sleep(30000);
            s = redisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("读释放锁成功！"+Thread.currentThread().getId());
        }
        return s;
    }

    // 信号量  假设redis中有3个信号量（车位），可以做限流工作
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.acquire();  // 阻塞方法，如果没有信号量，则一直等待

        return "ok";
    }

    @GetMapping("/go")
    @ResponseBody
    public String go(){
        RSemaphore park = redisson.getSemaphore("park");
        park.release();
        return "ok";
    }

    // 闭锁
    @GetMapping("/lockCDL")
    @ResponseBody
    public String bisuoTest() throws InterruptedException {
        RCountDownLatch countDownLatch = redisson.getCountDownLatch("lock-cdl");
        countDownLatch.trySetCount(5); // 满足5次后释放锁
        countDownLatch.await();  // 等待锁

        return "放假了，回家了！";
    }

    // gogo
    @GetMapping("/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable("id") Long id){
        RCountDownLatch countDownLatch = redisson.getCountDownLatch("lock-cdl");
        // 减少一下redis中次数，达到 trySetCount 设置的值后，将释放锁
        countDownLatch.countDown();

        return id + "班走了。。。";
    }

}
