<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>登录</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/public/css/loginStyles.css">
</head>
<body>
       <!--  <div class="ysec"><img src="${pageContext.request.contextPath}/public/img/login/logo.png" ></div>--> 
		<div class="container">
			<div class="welcome">
			<div style="width:170px;"><img src="${pageContext.request.contextPath}/public/img/login/logo.png" ></div>
			<form id="loginForm" class="form">
				<div style="margin-top:100px">
					<input type="text" name="userName" placeholder="请输入用户名">
				</div>
				<div>
					<input type="password" name="password" placeholder="请输入密码">
				</div>
				<div>
					<button type="submit" id="login-button" style="float:left;margin-left:150px;">登录</button>
					<span id="msg" style="color:red;float:left;margin-top:50px;margin-left:10px;"></span>
				</div>
			</form>
			</div>
		</div>

<script src="${pageContext.request.contextPath}/public/js/jquery-2.1.1.min.js" type="text/javascript"></script>
	<script>
		$('#login-button').click(function (event) {
			event.preventDefault();
			$.post('${pageContext.request.contextPath}/loginValidate.do', $("#loginForm").serializeArray(), function(data){
                if(data){
                    if(data.result){
                        window.location.href = "${pageContext.request.contextPath}/admin/index.html";
                    }else{
                        $('#msg').html(data.message);
                    }
                }else{
                    $('#msg').html("登录失败");
                }
            });
			
		});
	</script>
	
</body>
</html>