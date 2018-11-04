package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TbBrandMapper {
    int countByExample(TbBrandExample example);//按条件计数

    int deleteByExample(TbBrandExample example);//按条件删除

    int deleteByPrimaryKey(Long id);//按主键删除

    int insert(TbBrand record);//插入(返回值为id值)

    int insertSelective(TbBrand record);//插入不为null的字段 选择性(Selective)保存数据  主键是自动添加的，默认插入为空

    List<TbBrand> selectByExample(TbBrandExample example);//按条件查询

    TbBrand selectByPrimaryKey(Long id);//按主键查询

    int updateByExampleSelective(@Param("record") TbBrand record, @Param("example") TbBrandExample example);//按条件更新值不为null的字段

    int updateByExample(@Param("record") TbBrand record, @Param("example") TbBrandExample example);//按条件更新

    int updateByPrimaryKeySelective(TbBrand record);//按主键更新值不为null的字段  

    int updateByPrimaryKey(TbBrand record);//按主键更新

	List<Map> selectOptionList();
}