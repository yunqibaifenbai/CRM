<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<base href="<%=basePath%>">
<head>
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css">
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>
    <script type="text/javascript">

        $(function () {
            //给创建按钮添加单击事件
            $("#createActivityBtn").click(function () {
                //弹出创建市场活动的模态窗口
                $("#createActivityModal").modal("show");
            });

            //给保存按钮添加单机事件
            $("#saveCreateActivityBtn").click(function () {
                //收集参数
                var owner = $("#create-marketActivityOwner").val();
                var name = $.trim($("#create-marketActivityName").val());
                var startDate = $("#create-startDate").val();
                var endDate = $("#create-endDate").val();
                var cost = $.trim($("#create-cost").val());
                var description = $.trim($("#create-description").val());
                //表单验证
                if (name == "") {
                    alert("名称不能为空");
                    return;
                }
                if (owner == "") {
                    alert("所有者不能为空");
                    return;
                }
                if (startDate != "" && endDate != "") {
                    //比较时间先后大小
                    if (endDate < startDate) {
                        alert("结束日期不能比开始日期小");
                        return;
                    }
                }

                //发送请求
                $.ajax({
                    url: 'workbench/activity/saveCreateActivity.do',
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        owner: owner,
                        name: name,
                        startDate: startDate,
                        endDate: endDate,
                        cost: cost,
                        description: description
                    },
                    success: function (data) {
                        if (data.code == "200") {
                            //关闭模态窗口
                            $("#createActivityModal").modal("hide");
                            //重置表单
                            $("#createActivityForm").get(0).reset();
                            //刷新市场活动列,显示第一页数据,保持每页显示条数不变
                            queryActivityCondidtionForPage(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
                        } else {
                            alert(data.message)
                            $("#createActivityModal").modal("show");
                        }
                    }
                })
            });
            $(".mydate").datetimepicker({
                language: 'zh-CN',   //语言
                format: 'yyyy-mm-dd',//日期的格式
                minView: 'month',      //可以选择的最小视图
                initialDate: new Date(),//初始化显示的日期
                autoclose: true,      //设置选择完日期或者时间之后,是否自动关闭日历
                todayBtn: true,
                clearBtn: true
            });
            //当市场活动的主页面加载完成,查询所有数据的第一页,以及所有数据的总条数
            queryActivityCondidtionForPage(1, 10);

            //给查询按钮添加单机事件
            $("#queryActivityBtn").click(function () {
                //查询所有符合主页面加载完成,查询所有数据的第一页以及所有数据的总条数,默认每页显示10条
                queryActivityCondidtionForPage(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
            });

            //给全选按钮添加单击事件
            $("#checkAll").click(function () {
                $("#tbody input[type='checkbox']").prop("checked", this.checked);
            });

            // $("#tbody input[type='checkbox']").click(function () {
            //     //如果列表中所有的checkbox都选中,则"全选"按钮也选中
            //     if ($("#tbody input[type='checkbox']").size() == $("#tbody input[type='checkbox']:checked").size()) {
            //         $("#checkAll").prop("checked", true)
            //         console.log("aaa")
            //     } else {
            //         //如果列表中所有checkbox至少有一个没有选中,则全选取消选中
            //         $("#checkAll").prop("checked", false)
            //         console.log("bbb")
            //     }
            // });
            $("#tbody").on("click", "input[type='checkbox']", function () {
                //如果列表中所有的checkbox都选中,则"全选"按钮也选中
                if ($("#tbody input[type='checkbox']").size() == $("#tbody input[type='checkbox']:checked").size()) {
                    $("#checkAll").prop("checked", true)
                } else {
                    //如果列表中所有checkbox至少有一个没有选中,则全选取消选中
                    $("#checkAll").prop("checked", false)
                }
            });

            //给删除按钮添加单机事件
            $("#deleteActivityBtn").click(function () {
                //收集参数
                var checkedIds = $("#tbody input[type='checkbox']:checked")
                if (checkedIds.size() == 0) {
                    alert("请选择要删除的市场活动")
                    return;
                }
                if (window.confirm("确定删除吗?")) {
                    //收集参数
                    var ids = "";
                    $.each(checkedIds, function (index, obj) {
                        ids += this.value + ",";
                    });
                    ids = ids.substr(0, ids.length - 1);
                    //发送请求
                    $.ajax({
                        url: 'workbench/activity/deleteActivityByIds.do',
                        dataType: 'json',
                        type: 'POST',
                        data: {
                            ids: ids
                        },
                        success: function (data) {
                            if (data.code == "200") {
                                queryActivityCondidtionForPage(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
                            } else {
                                alert(data.message)
                            }
                        }
                    });
                }
            });

            //给修改添加单击事件
            $("#updateActivityBtn").click(function () {
                //校验
                if ($("#tbody input[type='checkbox']:checked").size() == 1) {
                    //收集参数
                    var Id = $("#tbody input[type='checkbox']:checked").val()
                    //发送请求
                    $.ajax({
                        url: 'workbench/activity/queryActivityById.do',
                        dataType: 'json',
                        data: {
                            id: Id
                        },
                        type: 'POST',
                        success: function (data) {
                            if (data.code == "200") {
                                console.log(data.retData)
                                $("#edit-id").val(data.retData.id);
                                $("#edit-marketActivityOwner").val(data.retData.owner);
                                $("#edit-marketActivityName").val(data.retData.name);
                                $("#edit-startTime").val(data.retData.startDate);
                                $("#edit-endTime").val(data.retData.endDate);
                                $("#edit-cost").val(data.retData.cost);
                                $("#edit-describe").val(data.retData.description);
                                $("#editActivityModal").modal("show")
                            } else {
                                alert(data.message)
                            }
                        }
                    })
                } else {
                    alert("请选择一个进行修改")
                    return;
                }
            })

            //给更新按钮添加事件
            $("#saveEditActivity").click(function () {
                //收集参数
                var id = $("#edit-id").val();
                var owner = $("#edit-marketActivityOwner").val();
                var name = $("#edit-marketActivityName").val();
                var startDate = $("#edit-startTime").val();
                var endDate = $("#edit-endTime").val();
                var cost = $("#edit-cost").val();
                var description = $("#edit-describe").val();

                //表单验证
                if (name == "") {
                    alert("名称不能为空");
                    return;
                }
                if (owner == "") {
                    alert("所有者不能为空");
                    return;
                }
                if (startDate != "" && endDate != "") {
                    //比较时间先后大小
                    if (endDate < startDate) {
                        alert("结束日期不能比开始日期小");
                        return;
                    }
                }

                var regExp = /^(([1-9]\d*)|0)$/;
                if (!regExp.test(cost)) {
                    alert("成本只能是非负正数");
                    return;
                }

                //发送请求
                $.ajax({
                    url: 'workbench/activity/saveEditActivity.do',
                    dataType: 'json',
                    type: 'POST',
                    data: {
                        id: id,
                        owner: owner,
                        name: name,
                        startDate: startDate,
                        endDate: endDate,
                        cost: cost,
                        description: description
                    },
                    success: function (data) {
                        if (data.code == "200") {
                            console.log("更新成功!")
                            $("#editActivityModal").modal("hide")
                            //刷新市场活动列表,保持页号和每页显示条数都不变
                            queryActivityCondidtionForPage($("#demo_pag1").bs_pagination('getOption', 'currentPage'), $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
                        } else {
                            alert(data.message);
                        }
                    }
                });
            });

            //给批量导出按钮添加单击事件
            $("#exportActivityAllBtn").click(function () {
                //发送同步请求
                window.location.href = "workbench/activity/Activitys.xls";
            });

            //给导入按钮添加单击事件
            $("#importActivityBtn").click(function () {
                //收集参数
                var activityFileName = $("#activityFile").val();
                var suffix = activityFileName.substr(activityFileName.lastIndexOf(".") + 1).toLocaleLowerCase();
                if (suffix != 'xls') {
                    alert("只支持xls文件");
                    return;
                }
                //拿到DOM对象里面的文件
                var activityFile = $("#activityFile")[0].files[0]
                if (activityFile.size > 5 * 1024 * 1024) {
                    alert("文件大小不能超过5MB");
                    return;
                }

                //FormData是ajax提供的接口,可以提交文本数据,也可以提交二进制数据
                var formData = new FormData();
                formData.append("activityFile", activityFile)
                //发送请求
                $.ajax({
                    url: 'workbench/activity/importActivity.do',
                    data: formData,
                    type: 'POST',
                    processData: false,
                    contentType: false,
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == "200") {
                            alert("成功导入" + data.message + "条记录");
                            //关闭模态窗口
                            $("#importActivityModal").modal("hide");
                            //刷新市场活动列表,显示第一页数据
                            queryActivityCondidtionForPage(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
                        } else {
                            alert(data.message)
                            $("#importActivityModal").modal("show");
                        }
                    }
                })
            })
        });


        function queryActivityCondidtionForPage(pageNo, pageSize) {
            //收集参数
            var name = $("#query-name").val();
            var owner = $("#query-owner").val();
            var startTime = $("#query-startTime").val();
            var endTime = $("#query-endTime").val();
            // var pageNo = 1 ;
            // var pageSize = 10;
            //发送请求
            $.ajax({
                url: 'workbench/activity/queryActivityByConditionForPage.do',
                type: 'POST',
                dataType: 'json',
                data: {
                    name: name,
                    owner: owner,
                    startTime: startTime,
                    endTime: endTime,
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                success: function (data) {
                    //显示总条数
                    // $("#totalRowsB").text(data.retData.totalRows);
                    var htmlStr = "";
                    //显示市场活动的列表
                    $.each(data.retData.activityList, function (index, obj) {
                        htmlStr+="<tr class=\"active\">";
                        htmlStr+="<td><input type=\"checkbox\" value=\""+obj.id+"\"/></td>";
                        htmlStr+="<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/activity/detailActivity.do?id="+obj.id+"'\">"+obj.name+"</a></td>";
                        htmlStr+="<td>"+obj.owner+"</td>";
                        htmlStr+="<td>"+obj.startDate+"</td>";
                        htmlStr+="<td>"+obj.endDate+"</td>";
                        htmlStr+="</tr>";
                    });
                    $("#tbody").html(htmlStr);
                    //初始化全选
                    $("#checkAll").prop("checked", false);

                    totalPages = data.retData.totalRows % pageSize == 0 ? data.retData.totalRows / pageSize : parseInt(data.retData.totalRows / pageSize) + 1
                    //对容器调用bs_pagination工具函数,显示翻页信息
                    $("#demo_pag1").bs_pagination({
                        currentPage: pageNo,//当前页号,相当于pageNo
                        rowsPerPage: pageSize,//每页显示条数,相当于pageSize
                        totalRows: data.retData.totalRows,//总条数
                        totalPages: totalPages,//总页数
                        visiblePageLinks: 5,//最多可以显示的卡片数
                        showGoToPage: true,//是否显示"跳转到"部分,默认true
                        showRowsPerPage: true,//是否显示每页显示条数
                        showRowsInfo: true,//是否显示记录的信息
                        showRowsDefaultInfo: true,
                        //用户每次切换页号,都会触发本函数,
                        //每次返回pageSize和pageNO
                        onChangePage: function (event, pageObj) {
                            queryActivityCondidtionForPage(pageObj.currentPage, pageObj.rowsPerPage);
                        }
                    })
                }
            });
        }
    </script>
</head>
<body>

<!-- 创建市场活动的模态窗口 -->
<div class="modal fade" id="createActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
            </div>
            <div class="modal-body">

                <form class="form-horizontal" id="createActivityForm" role="form">

                    <div class="form-group">
                        <label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="create-marketActivityOwner">
                                <c:forEach items="${userList}" var="u">
                                    <option value="${u.id}">${u.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-marketActivityName">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="create-startDate" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" readonly class="form-control mydate" id="create-startDate">
                        </div>
                        <label for="create-endDate" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" readonly class="form-control mydate" id="create-endDate">
                        </div>
                    </div>
                    <div class="form-group">

                        <label for="create-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-cost">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="create-description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="create-description"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="saveCreateActivityBtn">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 修改市场活动的模态窗口 -->
<div class="modal fade" id="editActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
            </div>
            <div class="modal-body">

                <form class="form-horizontal" role="form">
                    <input type="hidden" id="edit-id">
                    <div class="form-group">
                        <label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-marketActivityOwner">
                                <c:forEach items="${userList}" var="u">
                                    <option value="${u.id}">${u.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-marketActivityName">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" readonly class="form-control mydate" id="edit-startTime">
                        </div>
                        <label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" readonly class="form-control mydate" id="edit-endTime">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-cost">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="edit-describe"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="saveEditActivity">更新</button>
            </div>
        </div>
    </div>
</div>

<!-- 导入市场活动的模态窗口 -->
<div class="modal fade" id="importActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
            </div>
            <div class="modal-body" style="height: 350px;">
                <div style="position: relative;top: 20px; left: 50px;">
                    请选择要上传的文件：<small style="color: gray;">[仅支持.xls]</small>
                </div>
                <div style="position: relative;top: 40px; left: 50px;">
                    <input type="file" id="activityFile">
                </div>
                <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;">
                    <h3>重要提示</h3>
                    <ul>
                        <li>操作仅针对Excel，仅支持后缀名为XLS的文件。</li>
                        <li>给定文件的第一行将视为字段名。</li>
                        <li>请确认您的文件大小不超过5MB。</li>
                        <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                        <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                        <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                        <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                    </ul>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
            </div>
        </div>
    </div>
</div>


<div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>市场活动列表</h3>
        </div>
    </div>
</div>
<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
    <div style="width: 100%; position: absolute;top: 5px; left: 10px;">

        <div class="btn-toolbar" role="toolbar" style="height: 80px;">
            <form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">名称</div>
                        <input class="form-control" type="text" id="query-name">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">所有者</div>
                        <input class="form-control" type="text" id="query-owner">
                    </div>
                </div>


                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">开始日期</div>
                        <input class="form-control" type="text" id="query-startTime"/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">结束日期</div>
                        <input class="form-control" type="text" id="query-endTime">
                    </div>
                </div>

                <button type="button" class="btn btn-default" id="queryActivityBtn">查询</button>

            </form>
        </div>
        <div class="btn-toolbar" role="toolbar"
             style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-primary" id="createActivityBtn" data-target="#createActivityModal">
                    <span class="glyphicon glyphicon-plus"></span> 创建
                </button>
                <button type="button" class="btn btn-default" id="updateActivityBtn"
                        data-target="#editActivityModal"><span
                        class="glyphicon glyphicon-pencil"></span> 修改
                </button>
                <button type="button" class="btn btn-danger" id="deleteActivityBtn"><span
                        class="glyphicon glyphicon-minus"></span> 删除
                </button>
            </div>
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal">
                    <span class="glyphicon glyphicon-import"></span> 上传列表数据（导入）
                </button>
                <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span
                        class="glyphicon glyphicon-export"></span> 下载列表数据（批量导出）
                </button>
                <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span
                        class="glyphicon glyphicon-export"></span> 下载列表数据（选择导出）
                </button>
            </div>
        </div>
        <div style="position: relative;top: 10px;">
            <table class="table table-hover">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td><input type="checkbox" id="checkAll"/></td>
                    <td>名称</td>
                    <td>所有者</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                </tr>
                </thead>
                <tbody id="tbody">
                <%--                <tr class="active">--%>
                <%--                    <td><input type="checkbox"/></td>--%>
                <%--                    <td><a style="text-decoration: none; cursor: pointer;"--%>
                <%--                           onclick="window.location.href='detail.jsp';">发传单</a></td>--%>
                <%--                    <td>zhangsan</td>--%>
                <%--                    <td>2020-10-10</td>--%>
                <%--                    <td>2020-10-20</td>--%>
                <%--                </tr>--%>
                <%--                <tr class="active">--%>
                <%--                    <td><input type="checkbox"/></td>--%>
                <%--                    <td><a style="text-decoration: none; cursor: pointer;"--%>
                <%--                           onclick="window.location.href='detail.jsp';">发传单</a></td>--%>
                <%--                    <td>zhangsan</td>--%>
                <%--                    <td>2020-10-10</td>--%>
                <%--                    <td>2020-10-20</td>--%>
                <%--                </tr>--%>
                </tbody>
            </table>
            <div id="demo_pag1"></div>
        </div>
        <%--        <div style="height: 50px; position: relative;top: 30px;">--%>
        <%--            <div>--%>
        <%--                <button type="button" class="btn btn-default" style="cursor: default;">共<b id="totalRowsB"></b>条记录--%>
        <%--                </button>--%>
        <%--            </div>--%>
        <%--            <div class="btn-group" style="position: relative;top: -34px; left: 110px;">--%>
        <%--                <button type="button" class="btn btn-default" style="cursor: default;">显示</button>--%>
        <%--                <div class="btn-group">--%>
        <%--                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">--%>
        <%--                        10--%>
        <%--                        <span class="caret"></span>--%>
        <%--                    </button>--%>
        <%--                    <ul class="dropdown-menu" role="menu">--%>
        <%--                        <li><a href="#">20</a></li>--%>
        <%--                        <li><a href="#">30</a></li>--%>
        <%--                    </ul>--%>
        <%--                </div>--%>
        <%--                <button type="button" class="btn btn-default" style="cursor: default;">条/页</button>--%>
        <%--            </div>--%>
        <%--            <div style="position: relative;top: -88px; left: 285px;">--%>
        <%--                <nav>--%>
        <%--                    <ul class="pagination">--%>
        <%--                        <li class="disabled"><a href="#">首页</a></li>--%>
        <%--                        <li class="disabled"><a href="#">上一页</a></li>--%>
        <%--                        <li class="active"><a href="#">1</a></li>--%>
        <%--                        <li><a href="#">2</a></li>--%>
        <%--                        <li><a href="#">3</a></li>--%>
        <%--                        <li><a href="#">4</a></li>--%>
        <%--                        <li><a href="#">5</a></li>--%>
        <%--                        <li><a href="#">下一页</a></li>--%>
        <%--                        <li class="disabled"><a href="#">末页</a></li>--%>
        <%--                    </ul>--%>
        <%--                </nav>--%>
        <%--            </div>--%>
        <%--        </div>--%>

    </div>

</div>
</body>
</html>