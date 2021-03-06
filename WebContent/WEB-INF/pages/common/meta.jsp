<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<meta charset="utf-8"/>
<meta name="renderer" content="webkit">
<base href="/">
<title>CCYDSZ-SSC</title>
<%
    String contextPath = request.getContextPath();
    if (contextPath.equals("/"))
        request.getSession().setAttribute("root", "");
    else
        request.getSession().setAttribute("root", contextPath);
    
    request.setAttribute("mailReg", "^([a-zA-Z0-9]+[_|\\-|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\-|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$");
    request.setAttribute("userRealNameReg", "^[\\u4E00-\\u9FA5]+$");
    request.setAttribute("telephoneReg", "^(0[1-9]\\d{1,2}\\-)?[1-9]\\d{6,7}$");
    request.setAttribute("mobileReg", "^1[3|4|5|8][0-9]\\d{8}$");
    request.setAttribute("userIdReg", "^[0-9a-zA-Z]{6,20}$");
    request.setAttribute("passwordReg", "^.{6,18}$");
    String str = request.getHeader("User-Agent");
    if(str.indexOf("Android")>0 || str.indexOf("iPad")>0 || str.indexOf("Macintosh")>0){
        request.setAttribute("systype", "pad");
    }else{
        request.setAttribute("systype", "pc");
    }
    if(str.indexOf("Android")>0){
        request.setAttribute("phoneType", "Android");
    }else if(str.indexOf("iPad")>0 || str.indexOf("Macintosh")>0){
        request.setAttribute("phoneType", "ios");
    }else{
        request.setAttribute("phoneType", "all");
    }
%>
<!-- Jquery -->
<script src="${root}/public/js/jquery.js" type="text/javascript"></script>
<link type="text/css" rel="stylesheet" href="${root}/public/assets/css/sprflat-theme/jquery.ui.all.css" media="screen">

<!-- 新模板部分 -->
<link type="text/css" rel="stylesheet" href="${root}/public/newCss/bootstrap.min.css" media="screen">
<link type="text/css" rel="stylesheet" href="${root}/public/newCss/style.css" media="screen">
<link type="text/css" rel="stylesheet" href="${root}/public/newCss/bootstrap-responsive.min.css" media="screen">
<link type="text/css" rel="stylesheet" href="${root}/public/newCss/pages/dashboard.css" media="screen">
<link type="text/css" rel="stylesheet" href="${root}/public/newCss/font-awesome.css" media="screen">
<script src="${root}/public/newCss/base.js" type="text/javascript"></script>

<!-- Angular部分 -->
<script src="${root}/public/js/angular/angular.js" type="text/javascript"></script> 
<script src="${root}/public/js/angular/angular-ui-router.js" type="text/javascript"></script> 
<script src="${root}/public/js/angular/angular-animate.js" type="text/javascript"></script> 
<script src="${root}/public/js/angular/angular-locale_zh-cn.js" type="text/javascript"></script> 
<script src="${root}/public/js/angular-auto-validate-master/jcs-auto-validate.js" type="text/javascript"></script>
<%-- <script src="${root}/public/js/angular/angular-sanitize.js"></script> --%>
<!-- <script src="https://cdn.bootcss.com/angular-sanitize/1.6.4/angular-sanitize.js"></script> -->
<!-- bootstrap部分 -->
<script src="${root}/public/js/bootstrap/js/bootstrap.js" type="text/javascript"></script> 
<script src="${root}/public/js/bootstrap/ui-bootstrap-tpls.js" type="text/javascript"></script> 
<!-- 日期选择控件 -->
<link href="${root}/public/css/bootstrap-datetimepicker.min.css" rel="stylesheet" media="screen">
<script src="${root}/public/js/bootstrap/bootstrap-datetimepicker.min.js" type="text/javascript"></script> 
<script src="${root}/public/js/bootstrap/bootstrap-datetimepicker.zh-CN.js" type="text/javascript"></script> 
<!-- jquery-ui -->
<script src="${root}/public/js/angular-datepicker/moment.js"></script>
<script src="${root}/public/js/angular-datepicker/mbdatepicker.js"></script>
<link href="${root}/public/js/angular-datepicker/mbdatepicker.css" rel="stylesheet" type="text/css" />
<script src="${root}/public/js/jquery-ui/jquery-ui.js" type="text/javascript"></script> 
<link type="text/css" rel="stylesheet" href="${root}/public/js/jquery-ui/jquery-ui.css" media="screen">

<!-- 打印插件 -->
<script src="${root}/public/js/print/jQuery.print.js" type="text/javascript"></script> 
<script src="${root}/public/js/basiccheck.js" type="text/javascript"></script>
<script src="${root}/public/upload/uploadfile.js" type="text/javascript"></script>
<script src="${root}/public/js/extend.js" type="text/javascript"></script>
<script src="${root}/public/js/litewin.js" type="text/javascript"></script>
<script src="${root}/public/js/basiccheck.js" type="text/javascript"></script>
<script src="${root}/public/js/splitpage.js" type="text/javascript"></script>
<script src="${root}/public/js/common.js" type="text/javascript"></script>

<link type="text/css" rel="stylesheet" href="${root}/public/calendar/skin/WdatePicker.css" media="screen">
<script src="${root}/public/calendar/WdatePicker.js" type="text/javascript"></script>

<!-- 弹出框 -->
<script src="${root}/public/js/sweetAlert/sweet-alert.min.js" type="text/javascript"></script>
<link type="text/css" rel="stylesheet" href="${root}/public/css/sweetAlert/sweet-alert.css" media="screen">

<script type="text/javascript">
    var ROOT =  '${root}';
    $(document).on('ajaxComplete', function (jqXHR, xhr) {<%-- 判断ajax请求连接是否登录 --%>
        var responseText = xhr.responseText;
        if(typeof(responseText)!="undefined"){
	        var start = responseText.indexOf("<script>top.location='");
	        if(start>-1 && start < 10){
	            top.location="${root}/login.html";
	        }
    	}
    });
    
    
    function HTMLDecode(text) { 
        var temp = document.createElement("div"); 
        temp.innerHTML = text; 
        var output = temp.innerText || temp.textContent; 
        temp = null; 
        return output; 
    } 
    
    function HTMLEncode(html) {
        var temp = document.createElement("div");
        (temp.textContent != null) ? (temp.textContent = html) : (temp.innerText = html);
        var output = temp.innerHTML;
        temp = null;
        return output;
    }
</script>
