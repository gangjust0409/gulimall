package cn.bdqn.gulimall.product.entity;

import cn.bdqn.gulimall.common.validator.group.AddGroup;
import cn.bdqn.gulimall.common.validator.group.UpdateGroup;
import cn.bdqn.gulimall.common.validator.group.UpdateStatusGroup;
import cn.bdqn.gulimall.product.anon.Status;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author just
 * @email just@gmail.com
 * @date 2022-04-08 12:20:21
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改时id不能为空", groups = {UpdateGroup.class, UpdateStatusGroup.class})
	@Null(message = "添加时id必须为空", groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名必须填写", groups = {AddGroup.class, UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(groups = AddGroup.class)
	@URL(message = "品牌logo地址必须是地址", groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@Status(vals={0, 1}, groups = AddGroup.class, message = "状态值必须是0-1")
	private Integer showStatus;
	/**
	 * 检索首字母
	 * java的正则表达式，不用加 /
	 */
	@NotEmpty(groups = AddGroup.class)
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是a-z或A-Z", groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups = AddGroup.class)
	@Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
