package webTest.ServletTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.JDOMException;

import basicLogic.PushManager;

/**
 * Servlet implementation class WeChatServlet
 */
@WebServlet("/WeChatServlet")
public class WeChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String Token;
	private String echostr;

	public WeChatServlet() {
		super();
	}

	@Override
	public void init() throws ServletException {
		Token = "Test";
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		accessing(request, response);
		if (this.echostr != null && this.echostr != "") {
			response.getWriter().print(this.echostr);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			InputStream is = request.getInputStream();
			PushManager push = new PushManager();
			String getXml = push.PushManageXml(is);
			out.print(getXml);
		} catch (JDOMException e) {
			e.printStackTrace();
			out.print("");
		} catch (Exception e) {
			out.print("");
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	private boolean accessing(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");

		if (signature == null || signature == "") {
			return false;
		}
		if (nonce == null || nonce == "") {
			return false;
		}
		if (echostr == null || echostr == "") {
			return false;
		}
		String[] ArrTmp = { Token, timestamp, nonce };
		Arrays.sort(ArrTmp);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ArrTmp.length; i++) {
			sb.append(ArrTmp[i]);
		}
		String pwd = Encrypt(sb.toString());

		if (pwd.trim().equals(signature.trim())) {
			this.echostr = echostr;
			return true;
		} else {
			return false;
		}
	}

	private String Encrypt(String strSrc) {
		MessageDigest md = null;
		String strDes = null;

		byte[] bt = strSrc.getBytes();
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(bt);
			strDes = bytes2Hex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Invalid algorithm.");
			return null;
		}
		return strDes;
	}

	private String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

}
