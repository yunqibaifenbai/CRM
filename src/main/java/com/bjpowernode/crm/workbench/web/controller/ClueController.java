package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.enums.CodeEnum;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicValueService;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;
import com.bjpowernode.crm.workbench.domain.ClueRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueActivityRelationService;
import com.bjpowernode.crm.workbench.service.ClueRemarkService;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ClueController {
    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClueService clueService;

    @Autowired
    private ClueRemarkService clueRemarkService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ClueActivityRelationService clueActivityRelationService;

    @RequestMapping("/workbench/clue/index.do")
    public String index(HttpServletRequest request) {
        List<User> userList = userService.queryAllUsers();
        List<DicValue> applicationList = dicValueService.queryDicValueByTypeCode("application");
        List<DicValue> clueStateList = dicValueService.queryDicValueByTypeCode("clueState");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        request.setAttribute("userList", userList);
        request.setAttribute("applicationList", applicationList);
        request.setAttribute("clueStateList", clueStateList);
        request.setAttribute("sourceList", sourceList);
        return "workbench/clue/index";
    }

    @RequestMapping("/workbench/clue/saveCreateClue.do")
    @ResponseBody
    public ReturnObject saveCreateClue(Clue clue, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        //封装参数
        clue.setId(UUIDUtils.getUUID());
        clue.setCreateTime(DateUtils.formateDateTime(new Date()));
        clue.setCreateBy(user.getId());
        try {
            int ret = clueService.saveCreateClue(clue);
            if (ret > 0) {
                return new ReturnObject(CodeEnum.Code200.getCode(), CodeEnum.Code200.getMessage());
            } else {
                return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
        }
    }

    @RequestMapping("/workbench/clue/queryClueByConditionForPage.do")
    @ResponseBody
    public ReturnObject queryClueByConditionForPage(String fullName, String owner, String company,
                                                    String phone, String mphone, String source, String state, Integer pageNo, Integer pageSize) {
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("fullName", fullName);
        map.put("owner", owner);
        map.put("company", company);
        map.put("phone", phone);
        map.put("mphone", mphone);
        map.put("source", source);
        map.put("state", state);
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);

        List<Clue> clueList = clueService.queryClueByConditionForPage(map);
        int totalRows = clueService.queryCountOfClueByCondition(map);
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("clueList", clueList);
        retMap.put("totalRows", totalRows);
        return new ReturnObject(retMap);
    }

    @RequestMapping("workbench/Clue/detailClue.do")
    public String detailClue(String clueId, HttpServletRequest request) {
        Clue clue = clueService.queryClueForDetailById(clueId);
        List<Activity> activityList = activityService.queryActivityForDetailByClueId(clueId);
        List<ClueRemark> remarkList = clueRemarkService.queryClueRemarkForDetailByClueId(clueId);

        request.setAttribute("clue", clue);
        request.setAttribute("activityList", activityList);
        request.setAttribute("remarkList", remarkList);
        return "workbench/clue/detail";
    }

    @RequestMapping("/workbench/clue/queryActivityForDetailByNameClueId.do")
    @ResponseBody
    public ReturnObject queryActivityForDetailByNameClueId(String activityName, String clueId) {
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("activityName", activityName);
        map.put("clueId", clueId);
        List<Activity> activityList = activityService.queryActivityForDetailByNameClueId(map);
        return new ReturnObject(activityList);
    }

    @RequestMapping("/workbench/clue/saveBund.do")
    @ResponseBody
    public ReturnObject saveBund(String[] activityId, String clueId) {
        ClueActivityRelation car = null;
        List<ClueActivityRelation> clueActivityRelations= new ArrayList<>();
        for (String ai : activityId) {
            car = new ClueActivityRelation();
            car.setId(UUIDUtils.getUUID());
            car.setClueId(clueId);
            car.setActivityId(ai);
            clueActivityRelations.add(car);
        }
        try {
            int ret =clueActivityRelationService.saveCreateClueActivityRelationByList(clueActivityRelations);
            if (ret > 0) {
                List<Activity> activityList=activityService.queryActivityForDetailByIds(activityId);
                return new ReturnObject(CodeEnum.Code200.getCode(), activityList);
            } else {
                return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
        }
    }

    @RequestMapping("/workbench/clue/saveUnbund.do")
    @ResponseBody
    public ReturnObject saveUnbund(ClueActivityRelation clueActivityRelation){
        try{
            int ret= clueActivityRelationService.deleteClueActivityRelationByClueIdActivityId(clueActivityRelation);
            if (ret > 0) {
                return new ReturnObject(CodeEnum.Code200.getCode(), CodeEnum.Code200.getMessage());
            } else {
                return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
        }
    }

    @RequestMapping("/workbench/clue/toConvert.do")
    public String toConvert(String clueId,HttpServletRequest request){
        Clue clue= clueService.queryClueForDetailById(clueId);
        List<DicValue> stageList=dicValueService.queryDicValueByTypeCode("stage");
        request.setAttribute("clue",clue);
        request.setAttribute("stageList",stageList);
        return "workbench/clue/convert";
    }

    @RequestMapping("workbench/clue/queryActivityForConvertByNameClueId.do")
    @ResponseBody
    public ReturnObject queryActivityForConvertByNameClueId(String activityName,String clueId){
        Map<String,Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);
        List<Activity> activityList= activityService.queryActivityForConvertByNameClueId(map);
        return new ReturnObject(activityList);
    }
    @RequestMapping("workbench/clue/convertClue.do")
    @ResponseBody
    public ReturnObject convertClue(String clueId,String money,String name,String expectedDate,String stage ,String activityId,String isCreateTran,HttpSession session){
        Map<String,Object> map= new HashMap<>();
        map.put("clueId",clueId);
        map.put("money",money);
        map.put("name",name);
        map.put("expectedDate",expectedDate);
        map.put("stage",stage);
        map.put("activityId",activityId);
        map.put("isCreateTran",isCreateTran);
        map.put(Contants.SESSION_USER,session.getAttribute(Contants.SESSION_USER));
        try{
            clueService.saveConvertClue(map);
            return new ReturnObject(CodeEnum.Code200.getCode(), CodeEnum.Code200.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
        }
    }
}
