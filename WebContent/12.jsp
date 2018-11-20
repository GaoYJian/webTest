<html>
    <head>
           <title>第一个JSP程序</title>
    </head>
    <body>
         <p> 用户ID:<%=request.getParameter("id") %> 
         <p> 昵称:<%=request.getParameter("username")%>
	 <p> 状态:<%=request.getParameter("state")%>
	 <p> 电话:<%=request.getParameter("phonenum")%>
	 <p> 关联人数:<%=request.getParameter("count")%>
    </body>
</html>