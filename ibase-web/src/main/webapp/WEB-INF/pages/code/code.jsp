<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>test verify code</title>
<script type="text/javascript" src="js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
</head>
<body>
	<input id="veryCode" name="veryCode" type="text" />
	<img id="imgObj" alt="" src="" />
	<a href="#" onclick="changeImg()">换一张</a>
</body>
<script type="text/javascript">
	function changeImg() {
		var imgSrc = $("#imgObj");
		var src = imgSrc.attr("src");
		imgSrc.attr("src", chgUrl(src));
	}
	//时间戳   
	//为了使每次生成图片不一致，即不让浏览器读缓存，所以需要加上时间戳   
	function chgUrl(url) {
		var timestamp = (new Date()).valueOf();
		url = url.substring(0, 17);
		if ((url.indexOf("&") >= 0)) {
			url = url + "×tamp=" + timestamp;
		} else {
			url = url + "?timestamp=" + timestamp;
		}
		return url;
	}
</script>
</html>