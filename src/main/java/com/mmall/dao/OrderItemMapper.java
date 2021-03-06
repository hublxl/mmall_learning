package com.mmall.dao;

import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectByUserIdOrderNo(@Param("userId") Integer userId,@Param("orderNo") Long orderNo);

    //mybatis批量插入,传入的参数是集合.
    //注意！！！这里的@Param注解必须要加上，不然会出错
    void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);

    List<OrderItem> getByOrderNo(@Param("orderNo")Long orderNo);
}