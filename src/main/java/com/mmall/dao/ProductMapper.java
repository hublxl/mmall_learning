package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    //多个参数要加@Param注解
    List<Product> selectByNameAndProductId(@Param("productName") String productName,@Param("productId") Integer productId);

    //这里传入的参数是List<Integer>是一个集合，底层的SQL实现用mybatis的foreach来遍历
    List<Product> selectByNameAndCategoryIds(@Param("productName") String productName,@Param("categoryIdList") List<Integer> categoryIdList);
}