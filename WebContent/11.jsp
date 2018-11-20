<%@ page language="java" import="java.lang.*" pageEncoding="UTF-8"%>
<html>
<body>
<% String s = "123";
   String ss = "中文";
%>
<a href="12.jsp?id=<%=s%>&&username=<%=ss%>">传参数测试</a>

</body>
</html>