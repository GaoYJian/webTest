<%@ page language="java" import="java.lang.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="dataBase.weixinuser" pageEncodeing="UTF-8" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>中文测试</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

</head>

<body>
	<%
		String openid = (String) application.getAttribute("openid");
		String nickname = (String) application.getAttribute("nickname");
		int count = (int) application.getAttribute("count");
	%>

	<p>
		<%=openid%>
	</p>
	<p>
		<%=nickname%>
	</p>
	<p>
		<%=count%>
	</p>
</body>
</html>
