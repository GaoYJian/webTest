<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String nickname=request.getParameter("nickname");

%>

<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css">
    <script src="http://code.jquery.com/jquery-2.2.3.min.js"></script>
    <script src="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
    
    <title>个人信息页面</title>

  </head>
  

  
  
<body>
<div data-role="page" id="page" data-fullscreen="false">
  <div data-role="header" data-position="fixed">
    <h1>个人信息<br></h1>
</div>
<div data-role="content">
 <form method="get" action="">
<div data-role="fieldcontain">
 <label>真实姓名：</label>
 <input type="text" name="text" id="name" value="廖伟殷" placeholder="你的姓名是？"><br>
 <label>联系电话：</label>
 <input type="text" name="text" id="number" value="" placeholder="联系电话？"><br>
 <label>微信昵称：<%=nickname %></label>
 <hr style="height:1px; border-top:1px dashed #C3C3C3;"/> 
 <label>发展人数：2</label>
 <hr style="height:1px; border-top:1px dashed #C3C3C3;"/>
 <label>添加时间：2016-05-23 11:35</label>
 <hr style="height:1px; border-top:1px dashed #C3C3C3;"/>
 <label>账号状态：正常</label>
 <hr style="height:1px; border-top:1px dashed #C3C3C3;"/>
   <label for="switch">开启/停用：</label>
   <select name="switch" id="switch" data-role="slider">
   <option value="off">开启</option>
   <option value="on">停用</option>
   </select>
 <hr style="height:1px; border-top:1px dashed #C3C3C3;"/>
 <label for="textarea">备注：</label>
 <textarea cols="40" rows="8" name="textarea3" id="textarea3"></textarea><br>
</div>
 <br>
 <input type="submit" value="确认修改">
</form>


  
  </div>
       
 <div data-role="footer" data-position="fixed">
  <div data-role="navbar">
   <ul>
     <li><a href="Untitled-4.html" data-icon="home" data-transition="fade">首页</a></li>
</div>
</body>
</html>
