<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
</head>
<body onload="changeImg()">
	<h2>Hello World123!</h2>
	<h1 id="high"></h1>
	<!-- <img id="imgObj" alt="验证码" src="/code/index" onclick="changeImg()"/>
	<script type="text/javascript">
		function changeImg() {
			//为了使每次生成图片不一致，即不让浏览器读缓存，所以需要加上时间戳   
			var timestamp = (new Date()).valueOf();
			$("#imgObj").attr("src", "/code/index?timestamp="+timestamp);
		}
	</script> -->
	<script type="text/javascript" src="/plugins/jquery/jquery-2.2.1.min.js"></script>
	<script type="text/javascript">
		$(function(){
			$.ajax({
				url : "/index",
				type: "post",
				cache : false,
				async: true,
				success: function(data){
					var data = eval('(' + data + ')');
					var str = "";
					for(var i in data){
						str += data[i].name+"***";
					}
					$("#high").html(str);
				}
			});
		})
	</script>
</body>
</html>
