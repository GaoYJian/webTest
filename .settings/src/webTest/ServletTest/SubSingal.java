package webTest.ServletTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import basicLogic.HttpRequest;
import dataBase.weixinuser;

/**
 * Servlet implementation class SubSingal
 */
@WebServlet("/SubSingal")
public class SubSingal extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubSingal() {
        super();
        // TODO Auto-generated constructor stub
    }

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
		//String openid = request.getParameter("openid");
		//response.getWriter().append(openid);
		String openid = request.getParameter("openid");
		String state = request.getParameter("state");
		String phone = request.getParameter("phone");
		String nick = request.getParameter("nick");
		
		HttpSession session = request.getSession();
		ServletContext scx = session.getServletContext();
		
        weixinuser w = HttpRequest.GetWeixinUser(openid);//本人信息
        w.State = Integer.parseInt(state);
        w.Reserver1 = nick;
        w.Reserver2 = phone;
		// 将集合对象存储在应用上下文中
        boolean isC = HttpRequest.Updateweixinuser(w);
        String s ="";
        if(isC){
        	s= "true";
        }else{
        	s= "false";
        }
        scx.setAttribute("isSuccess", s);
		//scx.setAttribute("openid", w.openid);
		//scx.setAttribute("nickname", w.nickname);
		//scx.setAttribute("count", wl.size());
		//response.sendRedirect("4.jsp");
		
		// if(ToDataBase(hm)){
		// s += "成功";
		// }else{
		// s += "失败";
		// }


	}

}
