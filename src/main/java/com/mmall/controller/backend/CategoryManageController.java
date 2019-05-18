package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 分类管理模块
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 增加节点
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "add_category.do",method = RequestMethod.GET)
    @ResponseBody//使返回值自动使用json序列化
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){

        //判断登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，然后增加分类的处理逻辑,在service层实现
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员无权限操作");
        }
    }

    /**
     * 更新节点名称
     * @param session
     * @param categoryName
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "set_category_name.do",method = RequestMethod.GET)
    @ResponseBody//使返回值自动使用json序列化
    public ServerResponse setCategoryName(HttpSession session,String categoryName,Integer categoryId){

        //还是要判断是否登录，是否是管理员
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，然后更新分类名称,在service层实现
            return iCategoryService.updateCategoryName(categoryName,categoryId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员无权限操作");
        }
    }

    /**
     * 获取节点的平级的子节点（不递归）
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_category.do",method = RequestMethod.GET)
    @ResponseBody//使返回值自动使用json序列化
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){

        //还是要判断是否登录，是否是管理员
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，然后查询子节点的category信息，并且不递归，保持平级。在service层实现
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员无权限操作");
        }
    }

    /**
     * 获取节点的子节点（递归）
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.GET)
    @ResponseBody//使返回值自动使用json序列化
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){

        //还是要判断是否登录，是否是管理员
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，然后查询当前节点的id和递归子节点的id，并且递归。在service层实现
            //0-->10000-->100000
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员无权限操作");
        }
    }


}
