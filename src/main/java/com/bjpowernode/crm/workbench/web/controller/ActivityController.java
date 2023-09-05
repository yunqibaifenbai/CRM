package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.enums.CodeEnum;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.HSSFUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {
    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request) {
        //调用Service层方法,查询所有用户
        List<User> userList = userService.queryAllUsers();
        //把数据保存到request中
        request.setAttribute("userList", userList);
        //请求转发到市场活动的主页面
        return "workbench/activity/index";
    }

    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    @ResponseBody
    public ReturnObject saveCreateActivity(Activity activity, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        //封装参数
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtils.formateDateTime(new Date()));
        activity.setCreateBy(user.getId());

        try {
            //调用Service方法,保存创建的市场活动
            int ret = activityService.saveCreateActivity(activity);
            if (ret > 0) {
                return new ReturnObject(CodeEnum.Code200.getCode(), "创建成功");
            } else {
                return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
        }
    }

    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    @ResponseBody
    public ReturnObject queryActivityConditionForPage(String name, String owner, String startDate, String endDate,
                                                      Integer pageNo, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("beginNo", pageNo);
        map.put("pageSize", pageSize);
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        //调用service层方法，查询数据
        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int totalRows = activityService.queryCountOfActivityByCondition(map);
        //根据查询结果结果，生成响应信息
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("activityList", activityList);
        retMap.put("totalRows", totalRows);
        return new ReturnObject(retMap);
    }

    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    @ResponseBody
    public ReturnObject deleteActivityByIds(String[] ids) {
        try {
            int ret = activityService.deleteActivityByIds(ids);
            if (ret > 0) {
                return new ReturnObject(CodeEnum.Code200.getCode(), "删除成功!");
            } else {
                return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
        }
    }

    @RequestMapping("/workbench/activity/queryActivityById.do")
    @ResponseBody
    public ReturnObject queryActivityById(String id) {
        Activity activity = activityService.queryActivityById(id);
        if (activity == null) {
            return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
        }
        return new ReturnObject(CodeEnum.Code200.getCode(), CodeEnum.Code200.getMessage(), activity);
    }

    @RequestMapping("/workbench/activity/saveEditActivity.do")
    @ResponseBody
    public ReturnObject saveEditActivity(Activity activity, HttpSession session) {
        String dateStr = DateUtils.formateDate(new Date());
        activity.setEditTime(dateStr);
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activity.setEditBy(user.getName());
        try {
            int ret = activityService.saveEditActivity(activity);
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

    @RequestMapping("/workbench/activity/Activitys.xls")
    public void Activitys(HttpServletResponse response) throws Exception {
        List<Activity> activityList = activityService.queryAllActivity();
        //创建excel文件,把activityList写入到excel文件中
        //1.创建excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        //2.创建页
        HSSFSheet sheet = wb.createSheet("市场活动列表");
        //3.创建行
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始日期");
        cell = row.createCell(4);
        cell.setCellValue("结束日期");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");
        if (activityList != null && activityList.size() > 0) {
            Activity activity = null;
            for (int i = 0; i < activityList.size(); i++) {
                activity = activityList.get(i);
                row = sheet.createRow(i + 1);
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }
        //根据wb对象生成excel文件
//        OutputStream os = new FileOutputStream("D:\\springmvc\\resource\\activity.xls");


        //把生成的文件下载到客户端
        response.setContentType("application/octet-stream;charset=UTF-8");
        OutputStream out = response.getOutputStream();
//        InputStream is = new FileInputStream("D:\\springmvc\\resource\\activity.xls");
//        byte[] red = new byte[256];
//        int let = 0;
//        while ((let = is.read(red)) != -1) {
//            out.write(red, 0, let);
//        }
        //关闭流
//        is.close();
//        os.close();
        wb.write(out);
        out.flush();
        wb.close();
    }


    @RequestMapping("/workbench/activity/importActivity.do")
    public ReturnObject importActivity(MultipartFile activityFile, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
//        String fileName = UUIDUtils.getUUID();
//        String newFileName = fileName + ".xls";
//        String url = "D:\\springmvc\\resource\\" + newFileName;
//        File file = new File(url);
        try {
//            activityFile.transferTo(file);
            //解析excel文件,获取文件中的数据,并且封装成activityList
            InputStream is = activityFile.getInputStream();
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row = null;
            HSSFCell cell = null;
            Activity activity = null;
            List<Activity> activityList = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                activity = new Activity();
                activity.setId(UUIDUtils.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formateDate(new Date()));
                activity.setCreateBy(user.getId());

                for (int j = 0; j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);

                    //获取列中的数据
                    String cellValue = HSSFUtils.getCellValue(cell);
                    if (j == 0) {
                        activity.setName(cellValue);
                    } else if (j == 1) {
                        activity.setStartDate(cellValue);
                    } else if (j == 2) {
                        activity.setEndDate(cellValue);
                    } else if (j == 3) {
                        activity.setCost(cellValue);
                    } else if (j == 4) {
                        activity.setDescription(cellValue);
                    }
                }

                //每一行中所有的列都封装完成之后
                activityList.add(activity);
            }
            //调用service方法,保存市场活动
            int ret = activityService.saveCreateActivityByList(activityList);
            String msg = String.valueOf(ret);
            return new ReturnObject(CodeEnum.Code200.getCode(), msg);
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(CodeEnum.Code500.getCode(), CodeEnum.Code500.getMessage());
        }
    }

    @RequestMapping("/workbench/activity/detailActivity.do")
    public String detailActivity(String id,HttpServletRequest request) {
        List<ActivityRemark> activityRemarkList = activityRemarkService.queryActivityRemarkForDetailByActivityId(id);
        Activity activity = activityService.queryActivityForDetailById(id);
        request.setAttribute("activity",activity);
        request.setAttribute("activityRemarkList",activityRemarkList);
        //请求转发
        return "workbench/activity/detail";
    }

}
