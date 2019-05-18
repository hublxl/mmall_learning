package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    //这里传入的参数是List<String>是一个集合，底层的SQL实现用mybatis的foreach来遍历
    int deleteByUserIdProductIds(@Param("userId") Integer userId,@Param("productIdList") List<String> productIdList);

    int selectOrUnselectProduct(@Param("userId") Integer userId,@Param("productId") Integer productId,@Param("checked") Integer checked);

    //这里需要注意，这是返回int类型，而SQL查询结果返回null（也就是说userid在这个表中不存在），就会冲突，所以用IFNULL(SUM(quantity),0)来解决
    //IFNULL(SUM(quantity),0)这句话的意思：如果查询出来的是null，就让他为0
    int selectCartProductCount(Integer userId);

    List<Cart> selectCheckedByuserId(Integer userId);

}