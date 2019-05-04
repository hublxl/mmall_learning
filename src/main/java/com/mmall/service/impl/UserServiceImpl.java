package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
//为了将service注入到controller
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;//通过此注解将mapper注入进来
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    //注册
    public ServerResponse<String> register(User user){
        //校验用户名，Email是否存在
        /*第1中方法
        int resultCount = userMapper.checkUsername(user.getUsername());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        resultCount = userMapper.checkEmail(user.getEmail());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在");
        }*/

        //第2中方法，复用下面的checkValid方法
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){
        /**
         * isNotBlank():如果输入的是个空格，返回false
         * isNotEmpty():如果输入的是个空格，返回true
         */
        if(StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
            return ServerResponse.createBySuccessMessage("校验成功");
        }
        else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
    }


    public ServerResponse<String> selectQuestion(String username){

        //要校验一下用户名是否存在
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空");
    }


    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            //说明问题及答案是这个用户的，并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey("token_" + username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){

        //首先校验下参数
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }

        //然后从Cache里获取token
        String token = TokenCache.getKey("token_" + username);
        //对Cache里的token也要做校验
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        //将传过来的forgettoken和缓存里的token做比较
        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            //然后做生效行数判断
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }else{
                return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
            }
        }
        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){

        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户。因为我们会查询一个count（1），如果不指定id，那么结果就是true啦count>0
        int resultCount = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        //将新密码更新到数据库
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }


    public ServerResponse<User> updateInformation(User user){

        //username是不能被更新的
        //email也要进行校验，校验新的Email是不是已经存在，并且存在的Email如果相同的话，不能是我们当前的这个用户的。
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在,请更换Email在尝试更新");
        }
        //声明updateuser对象只用于更新
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }


    public ServerResponse<User> getInformation(Integer userId){
        //从数据库中获取userId对应的信息
        User user = userMapper.selectByPrimaryKey(userId);
        //判断
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        //如果存在，要将密码置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

}
