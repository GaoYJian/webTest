<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
    //String str = request.getparameter("nickname");
    //String str2 = request.getparameter("telephone");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP </title>
    
  </head>
  
  <body style="background-color:lightblue">
        <%
            String name=request.getParameter("name");
            out.print("name:"+name);
        %>
        <br/>
        <%
            out.print("password:"+request.getParameter("password"));
        %>
    </body>
</html>
