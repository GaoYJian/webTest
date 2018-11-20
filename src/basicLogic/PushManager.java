package basicLogic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import QRCode.QrGenerate;
import dataBase.role;
import dataBase.weixinuser;

public class PushManager {

	public String PushManageXml(InputStream is) throws JDOMException {

		String returnStr = ""; // 反回Servlet字符串
		String toName = ""; // 开发者微信号
		String fromName = ""; // 发送方帐号（一个OpenID）
		String type = ""; // 请求类型
		String con = ""; // 消息内容(接收)
		String event = ""; // 自定义按钮事件请求
		String eKey = ""; // 事件请求key值
		String scanresult = "";
		String scantype = "";

		try {

			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build(is);
			// 获得文件的根元素
			Element root = doc.getRootElement();

			// 获得根元素的第一级子节点
			List list = root.getChildren();
			for (int j = 0; j < list.size(); j++) {
				// 获得结点
				Element first = (Element) list.get(j);

				if (first.getName().equals("ToUserName")) {
					toName = first.getValue().trim();
				} else if (first.getName().equals("FromUserName")) {
					fromName = first.getValue().trim();
				} else if (first.getName().equals("MsgType")) {
					type = first.getValue().trim();
				} else if (first.getName().equals("Content")) {
					con = first.getValue().trim();
				} else if (first.getName().equals("Event")) {
					event = first.getValue().trim();
				} else if (first.getName().equals("EventKey")) {
					eKey = first.getValue().trim();
				} else if (first.getName().equals("ScanCodeInfo")) {
					Element e = first.getChild("ScanResult");
					scanresult = e.getValue().trim();
				}
			}
		} catch (IOException e) {
			// 异常
		}

		if (type.equals("event")) { // 此为事件
			if (event.equals("subscribe")) {// 此为 关注事件
				con = fromName;
				con = HttpRequest.GetUserByOpenId(con);
				HashMap hm = HttpRequest.FormatOut(con);
				con = HttpRequest.ToDataBase(hm) + "";
				returnStr = getBackXMLTypeText(toName, fromName, con);
			} else if (event.equals("unsubscribe")) { // 此为取消关注事件

			} else if (event.equals("CLICK")) { // 此为 自定义菜单按钮点击事件
				// 以下为自定义按钮事件
				if (eKey.equals("Button_benefit")) { // Button_benefit
					returnStr = getBackXMLTypeImg(toName, fromName, "图文", "这是一个图文消息",
							"http:////cx.jk0086.com//webTest//3.jsp", "http://cx.jk0086.com/webTest/pic01.jpg");
				} // else if (eKey.equals("Button_Manager")) { // Button_Manager
					// returnStr = getBackXMLTypeText(toName, fromName, "点赞成功");
					// return "http://www.baidu.com";
					// }
			} else if (event.equals("SCAN")) {
				 con = eKey;//con
				 weixinuser w = HttpRequest.GetWeixinUser(con+"");
				 con = HttpRequest.BindUser(con+"", fromName);
				 con += "\n "+ eKey + "\\ "+ w.UserId;
				 returnStr = getBackXMLTypeText(toName,fromName,con);
			} else if (event.equals("scancode_waitmsg")) {
				// returnStr = getBackXMLTypeText(toName,fromName,scanresult +
				// "/" + fromName);
				weixinuser w = HttpRequest.GetWeixinUser(scanresult + "");
				role r = HttpRequest.GetRole(w.RoleId + "");
				if (r.RoleId == -1) {
					con = "上级未有定义层级";
				} else {
					if (r.DownLevel == -1) {
						con = "优惠券号为:" + scanresult;
					} else {
						con = HttpRequest.BindUser(w.UserId + "", fromName);
						con += "\n " + fromName + "\\ " + w.UserId;
						returnStr = getBackXMLTypeText(toName, fromName, con);
					}
				}
			}
		} else if (type.equals("text")) { // 此为 文本信息
			if (con.compareTo("AT") == 0) {
				con = HttpRequest.get_access_token();
			} else if (con.compareTo("OAUTH") == 0) {
				con = HttpRequest.OAuth2_Step1();
				returnStr = getBackXMLTypeImg(toName, fromName, "认证", "网页认证", con,
						"http://cx.jk0086.com/webTest/pic01.jpg");
				return returnStr;
			} else if (con.compareTo("OAUTH_TXT") == 0) {
				con = HttpRequest.OAuth2_Step1();
				try {
					// QrGenerate.encode(con, "", "./TestQr", true);//二维码生成
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (con.compareTo("Ticket") == 0) {// 上传卡券LOGO
				// con = HttpRequest.UpLoadTicketLogo();
			} else if (con.compareTo("QR") == 0) {// 生成二维码
				weixinuser w = HttpRequest.GetWeixinUser(fromName);
				try {
					con = HttpRequest.GeneralQR(w.UserId+"");
					con = HttpRequest.GetQR(con);
					//QrGenerate.encode(w.UserId + "", "", "./webapps/CouponQr", true);
					
					con = HttpRequest.UpLoadPic(con, w.UserId + "");
					HashMap hm = HttpRequest.FormatOut(con);
					con = hm.get("\"media_id\"").toString();
					con = con.substring(1, con.length() - 1);
					// returnStr = content;
					return getBackXMLSingalImg(toName, fromName, con);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					con = "生成关联码失败";
				}
				// weixinuser w = HttpRequest.GetWeixinUser(fromName);
				// con = HttpRequest.GeneralQR(w.UserId+"");
				// con = HttpRequest.GetQR(con);
				// con = HttpRequest.UpLoadPic(con,w.UserId+"");
				// HashMap hm = HttpRequest.FormatOut(con);
				// con = hm.get("\"media_id\"").toString();
				// con = con.substring(1,con.length()-1);
				// return getBackXMLSingalImg(toName, fromName, con);
			} else if (con.compareTo("DList") == 0) {
				List<weixinuser> re = HttpRequest.GetUserBind(fromName);
				con = "总人数:" + re.size();
				for (int i = 0; i < re.size(); i++) {
					con += "\n openid:" + re.get(i).openid;
					con += "\n nickname:" + re.get(i).nickname;
					con += "\n";
				}
				// List<String> re = HttpRequest.GetUserBind(fromName);
				// con = "总人数: " + re.size();
				// for(int i = 0 ; i < re.size() ; i++){
				// con += "\n";
				// con += re.get(i) + "\n";
				// }
			} else if (con.compareTo("Coupon") == 0) {
				weixinuser w = HttpRequest.GetWeixinUser(fromName);
				try {
					QrGenerate.encode(w.UserId + "", "", "./CouponQr", true);
					con = HttpRequest.UpLoadPic(null, w.UserId + "");
					HashMap hm = HttpRequest.FormatOut(con);
					con = hm.get("\"media_id\"").toString();
					con = con.substring(1, con.length() - 1);
					// returnStr = content;
					return getBackXMLSingalImg(toName, fromName, con);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					con = "生成优惠券失败";
				}
			} else if (con.compareTo("UA") == 0) {
				HttpRequest.UpdateDataBase();
				con = "结束";
			} else if (con.compareTo("UP") == 0) {
				weixinuser w = HttpRequest.GetWeixinUser(fromName);
				w.Reserver1 = "1";
				w.Reserver2 = "1";
				w.State = 0;
				boolean re = HttpRequest.Updateweixinuser(w);
				if (re) {
					con = "True";
				} else {
					con = "False";
				}
			}
			returnStr = getBackXMLTypeText(toName, fromName, con);
		}

		return returnStr;
	}

	/**
	 * 编译文本信息
	 * 
	 * 
	 */
	private String getBackXMLTypeText(String toName, String fromName, String content) {

		String returnStr = "";

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String times = format.format(new Date());

		Element rootXML = new Element("xml");

		rootXML.addContent(new Element("ToUserName").setText(fromName));
		rootXML.addContent(new Element("FromUserName").setText(toName));
		rootXML.addContent(new Element("CreateTime").setText(times));
		rootXML.addContent(new Element("MsgType").setText("text"));
		rootXML.addContent(new Element("Content").setText(content));

		Document doc = new Document(rootXML);

		XMLOutputter XMLOut = new XMLOutputter();
		returnStr = XMLOut.outputString(doc);

		return returnStr;
	}

	/**
	 * 编译图片信息(单图模式)
	 * 
	 * 
	 */
	private String getBackXMLTypeImg(String toName, String fromName, String title, String content, String url,
			String pUrl) {

		String returnStr = "";

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String times = format.format(new Date());

		Element rootXML = new Element("xml");

		rootXML.addContent(new Element("ToUserName").setText(fromName));
		rootXML.addContent(new Element("FromUserName").setText(toName));
		rootXML.addContent(new Element("CreateTime").setText(times));
		rootXML.addContent(new Element("MsgType").setText("news"));
		rootXML.addContent(new Element("ArticleCount").setText("1"));

		Element fXML = new Element("Articles");
		Element mXML = null;

		mXML = new Element("item");
		mXML.addContent(new Element("Title").setText(title));
		mXML.addContent(new Element("Description").setText(content));
		mXML.addContent(new Element("PicUrl").setText(pUrl));
		mXML.addContent(new Element("Url").setText(url));
		fXML.addContent(mXML);
		rootXML.addContent(fXML);

		Document doc = new Document(rootXML);

		XMLOutputter XMLOut = new XMLOutputter();
		returnStr = XMLOut.outputString(doc);

		return returnStr;
	}

	/**
	 * 编译图片信息(无图模式)
	 * 
	 *
	 */
	private String getBackXMLTypeImg(String toName, String fromName, String title, String content, String url) {

		String returnStr = "";

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String times = format.format(new Date());

		Element rootXML = new Element("xml");

		rootXML.addContent(new Element("ToUserName").setText(fromName));
		rootXML.addContent(new Element("FromUserName").setText(toName));
		rootXML.addContent(new Element("CreateTime").setText(times));
		rootXML.addContent(new Element("MsgType").setText("news"));
		rootXML.addContent(new Element("ArticleCount").setText("1"));

		Element fXML = new Element("Articles");
		Element mXML = null;

		// String url = "";
		String ss = "";
		mXML = new Element("item");
		mXML.addContent(new Element("Title").setText(title));
		mXML.addContent(new Element("Description").setText(content));
		mXML.addContent(new Element("PicUrl").setText(ss));
		mXML.addContent(new Element("Url").setText(url));
		fXML.addContent(mXML);
		rootXML.addContent(fXML);

		Document doc = new Document(rootXML);

		XMLOutputter XMLOut = new XMLOutputter();
		returnStr = XMLOut.outputString(doc);

		return returnStr;
	}

	/**
	 * 编译音乐信息
	 * 
	 * 
	 */
	@SuppressWarnings("unused")
	private String getBackXMLTypeMusic(String toName, String fromName, String content) {

		String returnStr = "";

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String times = format.format(new Date());

		Element rootXML = new Element("xml");

		rootXML.addContent(new Element("ToUserName").setText(fromName));
		rootXML.addContent(new Element("FromUserName").setText(toName));
		rootXML.addContent(new Element("CreateTime").setText(times));
		rootXML.addContent(new Element("MsgType").setText("music"));

		Element mXML = new Element("Music");

		mXML.addContent(new Element("Title").setText("音乐"));
		mXML.addContent(new Element("Description").setText("音乐让人心情舒畅！"));
		mXML.addContent(new Element("MusicUrl").setText(content));
		mXML.addContent(new Element("HQMusicUrl").setText(content));

		rootXML.addContent(mXML);

		Document doc = new Document(rootXML);

		XMLOutputter XMLOut = new XMLOutputter();
		returnStr = XMLOut.outputString(doc);

		return returnStr;
	}

	/*
	 * 发单图片
	 */
	private String getBackXMLSingalImg(String toName, String fromName, String content) {
		String returnStr = "";

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String times = format.format(new Date());

		Element rootXML = new Element("xml");

		rootXML.addContent(new Element("ToUserName").setText(fromName));
		rootXML.addContent(new Element("FromUserName").setText(toName));
		rootXML.addContent(new Element("CreateTime").setText(times));
		rootXML.addContent(new Element("MsgType").setText("image"));
		Element mXML = new Element("Image");
		mXML.addContent(new Element("MediaId").setText(content));
		rootXML.addContent(mXML);

		Document doc = new Document(rootXML);

		XMLOutputter XMLOut = new XMLOutputter();
		returnStr = XMLOut.outputString(doc);

		return returnStr;
	}
}
