package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.enums.CodeEnum;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin() {
        //请求转发到登录页面
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/login.do")
    @ResponseBody
    public ReturnObject login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        //封装参数
        Map<String, Object> map = new HashMap();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);
        //调用service方法,查询用户信息
        User user = userService.queryUserByLoginActAndPwd(map);
        //根据查询结果,生成响应信息
        if (user == null) {
            //登录失败,用户名或密码错误
            return new ReturnObject(CodeEnum.Code500.getCode(), "用户名或密码错误");
        } else {
            //进一步判断账号是否合法
            if (DateUtils.formateDateTime(new Date()).compareTo(user.getExpireTime()) > 0) {
                //登录失败,账号时间过期
                return new ReturnObject(CodeEnum.Code500.getCode(), "账号时间过期");
            } else if ("0".equals(user.getLockState())) {
                //登录失败,状态被锁定
                return new ReturnObject(CodeEnum.Code500.getCode(), "账号状态被锁定");
//            }else if (!user.getAllowIps().contains(request.getRemoteAddr())){
//                //登录失败,ip受限
//                return new ReturnObject("500","IP受限");
            } else {
                //把User信息保存在session中
                session.setAttribute(Contants.SESSION_USER, user);

                //如果需要记住密码,则往外写cookie
                if (isRemPwd.equals("true")) {
                    Cookie c1 = new Cookie("loginAct", user.getLoginAct());
                    c1.setMaxAge(10 * 24 * 60 * 60);
                    response.addCookie(c1);
                    Cookie c2 = new Cookie("loginPwd", user.getLoginPwd());
                    c2.setMaxAge(10 * 24 * 60 * 60);
                    response.addCookie(c2);
                } else {
                    //把没有过期的cookie删除
                    Cookie c1 = new Cookie("loginAct", null);
                    c1.setMaxAge(0);
                    response.addCookie(c1);
                    Cookie c2 = new Cookie("loginPwd", null);
                    c2.setMaxAge(0);
                    response.addCookie(c2);
                }

                //登录成功
                return new ReturnObject(CodeEnum.Code200.getCode(), "登录成功");

            }
        }
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response, HttpSession session) {
        //清空Cookie
        Cookie c1 = new Cookie("loginAct", null);
        c1.setMaxAge(0);
        response.addCookie(c1);
        Cookie c2 = new Cookie("loginPwd", null);
        c2.setMaxAge(0);
        response.addCookie(c2);
        //销毁Session
        session.invalidate();
        //跳转到首页,重定向到首页
        return "redirect:/";
    }
}
