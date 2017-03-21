<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
</head>
<body style="padding: 50px">
    选择文件
    <form action="/test/upload-file" method="post" enctype="multipart/form-data">
        <input type="file" name="file"/><br>
        <button type="submit">上传</button>
    </form>
</body>
</html>