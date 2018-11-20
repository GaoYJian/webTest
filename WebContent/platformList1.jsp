<%@ page import="dataBase.weixinuser"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String title = (String) application.getAttribute("title");
	weixinuser owner = (weixinuser)application.getAttribute("Owner");
	String nickname = (String) application.getAttribute("nickname");
	
%>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css">
<script src="http://code.jquery.com/jquery-2.2.4.min.js"></script>
<script
	src="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
<script>
	$(document).on("scrollstart", function() {

	});
</script>
<script>
	function mm() {
		var text = document.getElementById("btnShow").value
        var UserId = <%=owner.UserId%>
	if ("显示" == text) {
		document.getElementById("picShow").style.display = "block";
        document.getElementById("btnShow").value = "隐藏";
		document.getElementById("picShow").src = "http://cx.jk0086.com/CouponQr/" + UserId + ".jpg"
	} else {
		document.getElementById("picShow").style.display = "none";
		document.getElementById("btnShow").value = "显示";
			}
	}
</script>

<title><%=title%></title>

</head>
<h1><%=title%></h1>

<body>

<table width="100%" border="0">

<%    
 		ArrayList wl = (ArrayList) application.getAttribute("SubList");
 		for (int i = wl.size() - 1; i >= 0; i--) {
 		weixinuser w = (weixinuser) wl.get(i);
	   	String ss = "停用";
		if(w.State == 1){
                    ss = "启用";
                }else{
		    ss = "停用";
		}
%>
	
	<tbody>
	<tr>
	<td rowspan="4" align="center" name="headimgurl"><img src="<%=w.headimgurl%>"
		width="60" height="60" alt="" /></td>
	<td align="left" scope="row">姓名：</td>
	<td align="right" name="nickname"><%=w.nickname%></td>
        
	</tr>
	<tr>
	<td align="left" scope="row">电话：</td>
	<td align="right" scope="row" name="telephone"><%=w.Reserver2%></td>
	</tr>
	<tr>
	<td align="left" scope="row">状态：</td>
	<td align="right" scope="row"><%=ss%></td>
	</tr>
        <tr>
        <td align="lef" scope="row"><a href="12.jsp?id=<%=w.UserId%>&&username=<%=w.nickname%>&&state=<%=ss%>&&phonenum=<%=w.Reserver2%>&&count=<%=w.Reserver3%>">详细信息</a></td>
        </tr>
	</tbody>
		 
	
<%
 	}
%>
</table>					
</a>			

		<div>
			<input id="btnShow" type="button" value="显示" onclick="mm()">
			<div align="center"><img style.display="none" id="picShow" ></div>
		</div>
		</div>

		<div data-role="footer" data-position="fixed">
			<div data-role="navbar">
				<ul>
					<li><a href="error.jsp" data-icon="home" data-transition="fade">首页</a></li>
				</ul>
			</div>
		</div>
	</div>
</body>
</html>
