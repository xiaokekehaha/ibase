<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户列表 </title>
<!-- 引入静态文件, 需要在user-servlet.xml配置映射关系 -->
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/main.css" type="text/css">
</head>
<body>
<a href="add">添加</a>
<br/>
	<c:forEach items="${users}" var="usermap">
		<a href="${usermap.value.username}">${usermap.value.username} -- ${usermap.value.password}</a>
		-- <a href="${usermap.value.username}/update">修改</a>
		-- <a href="${usermap.value.username}/delete">删除</a><br/>
	</c:forEach>
</body>
</html>