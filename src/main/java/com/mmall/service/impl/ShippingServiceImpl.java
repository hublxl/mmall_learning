package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;


    public ServerResponse<Map> add(Integer userId, Shipping shipping){

        if(shipping == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //这句必须要有，也是避免横向越权
        shipping.setUserId(userId);
        //在这个insert的xml实现中添加useGeneratedKeys="true" keyProperty="id"，可以使id自动添加到shipping对象中，然后通过shipping.getId获取
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            //增加之后，把shippingId返回前端,用Map来承载这个数据
            Map map = Maps.newHashMap();
            map.put("shippingId",shipping.getId());

            return ServerResponse.createBySuccess("新建地址成功",map);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }


    //这里要注意横向越权问题，避免方法：将userid和shippingid绑在一起去数据库中删除，这样就不会删除别的用户的shipping
    public ServerResponse<String> del(Integer userId,Integer shippingId){

        int rowCount = shippingMapper.deleteByUserIdShippingId(userId,shippingId);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }


    //这里也要注意越权问题
    public ServerResponse<String> update(Integer userId,Shipping shipping){
        //这句必须要有，也是避免横向越权.
        // 同时后面的SQL里面是通过这个shipping对象里面的userid来进行判断更改的，
        // 所以后面的dao层的update方法就不需要传入userid，直接传入shipping对象（里面已经包含userid）.
        //后面的SQL里面是没有修改userid的，也不允许修改
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }


    //也要判断越权问题
    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){

        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId,shippingId);
        if(shipping != null){
            return ServerResponse.createBySuccess("查询地址成功",shipping);
        }
        return ServerResponse.createByErrorMessage("无法查询该地址");
    }


    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){

        PageHelper.startPage(pageNum,pageSize);

        //根据用户查询他下面的所有地址
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);

    }

}
