package cn.bdqn.gulimall.member.dao;

import cn.bdqn.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-05-03 09:12:43
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
