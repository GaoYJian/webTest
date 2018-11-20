<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<html>
  <head>
    
    <title>My JSP</title>
    
  </head>
  
 <body style="background-color:lightblue">

        <%request.setCharacterEncoding("GB2312");%>

        <jsp:forward page="getParam.jsp">
            <jsp:param name="name" value="hiwins"/>
            <jsp:param name="password" value="123"/>
        </jsp:forward>

    </body>
</html>
