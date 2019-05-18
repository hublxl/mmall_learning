package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 保存产品,如果传了id就是修改，如果不传id就是新增
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，填充增加产品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);//如果传了id就是修改，如果不传id就是新增
        }else{
            return ServerResponse.createByErrorMessage("不是管理员无权限操作");
        }
    }



    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员无权限操作");
        }
    }


    /**
     * 获取产品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，填充业务
            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员无权限操作");
        }
    }



    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，填充业务
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员无权限操作");
        }
    }

    /**
     * 后台产品搜索
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，填充业务
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员无权限操作");
        }
    }


    /**
     * 上传文件
     * @param session
     * @param file:springMVC的 文件上传
     * @param request：根据servelet上下文去动态的创建一个相对路径出来
     * @return
     */
    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，填充业务
            String path = request.getSession().getServletContext().getRealPath("upload");//上传文件的文件夹就叫upload，上传之后该文件夹会在webapp下跟WEB-INF和index.jsp同级
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;//完整的url就是网址前缀加上文件名，这里注意在前缀的最后加了个/，如果不加就是错误的

            //这里用map来承载信息，这样就不用再创建新对象来承载这些信息，最后将这些信息返回回去
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员无权限操作");
        }
    }




    //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }

    @RequestMapping(value = "rich_img_upload.do")
    @ResponseBody
    public Map richImgUpload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){

        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //校验一下用户是否是管理员,将其写在userservice里，然后将其注入进来
        ServerResponse resultRespose = iUserService.checkAdminRole(user);
        if(resultRespose.isSuccess()){
            //是管理员，填充业务
            String path = request.getSession().getServletContext().getRealPath("upload");//上传文件的文件夹就叫upload，上传之后该文件夹会在webapp下跟WEB-INF和index.jsp同级
            String targetFileName = iFileService.upload(file,path);

            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }

            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;//完整的url就是网址前缀加上文件名
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");

            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","不是管理员无权限操作");
            return resultMap;
        }
    }



}
