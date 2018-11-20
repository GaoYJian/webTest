package ServletTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import QRCode.QrGenerate;
import basicLogic.HttpRequest;
import dataBase.weixinuser;
import dataBase.role;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		// String openid = request.getParameter("openid");
		// response.getWriter().append(openid);
		String openid = "";
		String code = request.getParameter("code");

		code = HttpRequest.OAuth2_Step2(code);
		HashMap hm = HttpRequest.FormatOut(code);
		openid = hm.get("\"openid\"").toString();
		openid = openid.substring(1, openid.length() - 1);

		HttpSession session = request.getSession();
		ServletContext scx = session.getServletContext();

		weixinuser w = HttpRequest.GetWeixinUser(openid);// 本人信息
		List<weixinuser> wl = HttpRequest.GetUserBind(openid);// 下级列表
		w.Reserver3 = wl.size() + "";
		role r = HttpRequest.GetRole(w.RoleId+"");
		//response.sendRedirect("error.jsp");
		try {
			QrGenerate.encode(w.UserId + "", "", "./webapps/CouponQr", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0 ; i < wl.size(); i++){
			wl.get(i).Reserver3 = HttpRequest.GetUserBind(wl.get(i).openid).size() + "";
		}
		// 将集合对象存储在应用上下文中
		scx.setAttribute("Owner", w);
		scx.setAttribute("SubList", wl);
		scx.setAttribute("title", r.RoleName);
		scx.setAttribute("subTitle", r.DownName);
		// 平台管理员 platformList1.jsp
		// 平台业务员 platformList2.jsp
		// 业务负责人 businessList1.jsp
		// 业务管理员 businessList2.jsp
		// 业务员 businessList3.jsp
		// 推荐人管理 refereeList.jsp
		
		switch (w.RoleId) {
		case 1:
			response.sendRedirect("platformList1.jsp");
			break;
		case 2:
			response.sendRedirect("platformList1.jsp");
			break;
		case 3:
			response.sendRedirect("platformList1.jsp");
			break;
		case 4:
			response.sendRedirect("platformList1.jsp");
			break;
		case 5:
			response.sendRedirect("platformList1.jsp");
			break;
		case 6:
			response.sendRedirect("platformList1.jsp");
			break;
		case -1:
			response.sendRedirect("error.jsp");
			break;
		}
		// scx.setAttribute("openid", w.openid);
		// scx.setAttribute("nickname", w.nickname);
		// scx.setAttribute("count", wl.size());
		// response.sendRedirect("4.jsp");

		// if(ToDataBase(hm)){
		// s += "成功";
		// }else{
		// s += "失败";
		// }

	}

}
