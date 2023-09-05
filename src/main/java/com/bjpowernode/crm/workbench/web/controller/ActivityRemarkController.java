package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.enums.CodeEnum;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class ActivityRemarkController {

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/saveCreateActivityRemark.do")
    @ResponseBody
    public ReturnObject saveCreateActivityRemark(ActivityRemark activityRemark, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activityRemark.setId(UUIDUtils.getUUID());
        activityRemark.setCreateBy(user.getId());
        activityRemark.setCreateTime(DateUtils.formateDateTime(new Date()));
        activityRemark.setEditFlag(Contants.REMARK_EDIT_FLAG_NO_EDITED);
        try {
            int ret = activityRemarkService.saveCreateActivityRemark(activityRemark);
            if (ret > 0) {
                return new ReturnObject(CodeEnum.Code200.getCode(),CodeEnum.Code200.getMessage(),activityRemark);
            }else {
                return new ReturnObject(CodeEnum.Code500.getCode(),CodeEnum.Code500.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(CodeEnum.Code500.getCode(),CodeEnum.Code500.getMessage());
        }
    }

    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    @ResponseBody
    public ReturnObject deleteActivityRemarkById(String id){
        try{
            int ret = activityRemarkService.deleteActivityRemarkById(id);
            if (ret>0){
                return new ReturnObject(CodeEnum.Code200.getCode(),CodeEnum.Code200.getMessage());
            }else {
                return new ReturnObject(CodeEnum.Code500.getCode(),CodeEnum.Code500.getMessage());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnObject(CodeEnum.Code500.getCode(),CodeEnum.Code500.getMessage());
        }
    }

    @RequestMapping("/workbench/activity/updateActivityRemarkById.do")
    @ResponseBody
    public ReturnObject updateActivityRemarkById(ActivityRemark activityRemark ,HttpSession session){
        User user =(User) session.getAttribute(Contants.SESSION_USER);
        activityRemark.setEditFlag(Contants.REMARK_EDIT_FLAG_YES_EDITED);
        activityRemark.setEditTime(DateUtils.formateDateTime(new Date()));
        activityRemark.setEditBy(user.getId());
        try{
            int ret =activityRemarkService.saveEditActivityRemarkById(activityRemark);
            if (ret>0){
                return new ReturnObject(CodeEnum.Code200.getCode(),CodeEnum.Code200.getMessage(),activityRemark);
            }else {
                return new ReturnObject(CodeEnum.Code500.getCode(),CodeEnum.Code500.getMessage());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnObject(CodeEnum.Code500.getCode(),CodeEnum.Code500.getMessage());
        }
    }
}
