package basicLogic;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;

import dataBase.DBHandler;
import dataBase.role;
import dataBase.weixinuser;
import net.sf.json.JSONObject;

public class HttpRequest {
	public static String appID = "wx269daabea88bf930";
	public static String appsecret = "03dcf40a66f11f34a77c896bd673fecc";
	private static String access_token = null;
	private static String OAuth_Url = "http://cx.jk0086.com/webTest/1.jsp";
	private static String OAuth_Url_Non = "https://cx.jk0086.com/webTest/3.jsp";

	/*
	 * 获得access_token
	 */
	public static String get_access_token() {
		DBHandler dbh = new DBHandler();
		if (dbh.CheckAccess() == null) {
			String line = "";
			if (!isNullOrEmpty(appID) && !isNullOrEmpty(appsecret)) {
				String url = "https://api.weixin.qq.com/cgi-bin/token";
				url += "?grant_type=client_credential&" + "appid=" + appID + "&secret=" + appsecret;
				line = ExecUrl_Get(url);
			}
			HashMap hm = FormatOut(line);
			access_token = hm.get("\"access_token\"").toString();
			access_token = access_token.substring(1, access_token.length() - 1);
			dbh.UpdateAccess(access_token);
		} else {
			access_token = dbh.CheckAccess();
		}
		return access_token;
	}

	private static boolean isNullOrEmpty(String s) {
		if (s == null)
			return true;
		if (s.trim() == "")
			return true;
		return false;
	}

	// 格式化JSON串
	public static HashMap FormatOut(String s) {
		HashMap<String, String> dic = new HashMap<String, String>();
		String tmp1 = s.substring(1, s.length() - 1);
		String[] Rows = tmp1.split(",");

		for (int i = 0; i < Rows.length; i++) {
			String[] cols = Rows[i].split(":");
			String value = "";
			for (int j = 1; j < cols.length; j++) {
				if (j == 1) {
					value += cols[j];
				} else {
					value += ":" + cols[j];
				}
			}
			dic.put(cols[0], value);
		}

		return dic;
	}

	// 网页授权第一步
	public static String OAuth2_Step1() {
		String line = "";
		if (!isNullOrEmpty(appID)) {
			try {
				line += "https://open.weixin.qq.com/connect/oauth2/authorize?" + "appid=" + appID + "&redirect_uri="
						+ java.net.URLEncoder.encode(OAuth_Url_Non, "UTF8")
						+ "&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ExecUrl(line);
		}
		return line;
	}
	
	//静默授权
	public static String OAuth2_Step1_Non(){
		String line = "";
		if (!isNullOrEmpty(appID)) {
			try {
				line += "https://open.weixin.qq.com/connect/oauth2/authorize?" + "appid=" + appID + "&redirect_uri="
						+ java.net.URLEncoder.encode(OAuth_Url, "UTF8")
						+ "&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ExecUrl(line);
		}
		return line;
	}

	// 网页授权第二步
	public static String OAuth2_Step2(String code) {
		String line = "";
		get_access_token();
		if (!isNullOrEmpty(appID) && !isNullOrEmpty(appsecret) && !isNullOrEmpty(code)) {
			line += "https://api.weixin.qq.com/sns/oauth2/access_token?" + "appid=" + appID + "&secret=" + appsecret
					+ "&code=" + code + "&grant_type=authorization_code";
			line = ExecUrl_Get(line);
			HashMap hm = FormatOut(line);
			// return line + '\n' +hm.get("\"access_token\"").toString();
			return OAuth2_Step3(hm);
		}
		return line;
	}

	// 网页授权第三步
	public static String OAuth2_Step3(HashMap hm) {
		String line = "";
		String AK = hm.get("\"access_token\"").toString();
		AK = AK.substring(1, AK.length() - 1);
		String OI = hm.get("\"openid\"").toString();
		OI = OI.substring(1, OI.length() - 1);
		String RT = hm.get("\"refresh_token\"").toString();
		RT = RT.substring(1, RT.length() - 1);
		line += "https://api.weixin.qq.com/sns/userinfo?" + "access_token=" + AK + "&openid=" + OI + "&lang=zh_CN";
		// line += "https://api.weixin.qq.com/sns/auth?access_token="+AK
		// +"&openid="+ OI;
		// line +=
		// "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+appID+"&grant_type=refresh_token&refresh_token="+RT;
		line = ExecUrl_Get(line);
		return line;
	}

	// 直接用OpenId获取用户信息
	public static String GetUserByOpenId(String openid) {
		get_access_token();
		String line = "https://api.weixin.qq.com/cgi-bin/user/info?" + "access_token=" + access_token + "&openid="
				+ openid + "&lang=zh_CN";

		line = ExecUrl_Get(line);
		return line;
	}

	// 上传卡券LOGO
	public static String UpLoadTicketLogo() {
		// String url = "http://file.api.weixin.qq.com/cgi-bin/media/upload?";
		String url = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?";
		get_access_token();
		url += "access_token=" + access_token;
		// url += "&type=image";
		return send(url, "./G.jpg");
	}

	// 上传图片
	public static String UpLoadPic(String picUrl, String Id) {
		String line = "";
		String url = "https://api.weixin.qq.com/cgi-bin/media/upload?";
		get_access_token();
		url += "access_token=" + access_token;
		url += "&type=image";
		if (picUrl != null) {
			getURLResource("QRCode/" + Id + ".jpg", picUrl);
			return send(url, "./webapps/QRCode/" + Id + ".jpg");
		}else{
			return send(url,"./webapps/CouponQr/" + Id+ ".jpg");
		}
	}

	// 申请二维码
	public static String GeneralQR(String scenId) {
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?";
		String param = "{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\":"
				+ scenId + "}}}";
		get_access_token();
		url += "access_token=" + access_token;
		// url += "&type=image";
		HashMap hm = FormatOut(sendJson(url, param));
		String ticket = hm.get("\"ticket\"").toString();
		ticket = ticket.substring(1, ticket.length() - 1);
		return ticket;
	}

	// 获得二维码
	public static String GetQR(String ticket) {
		String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
		url += URLEncoder.encode(ticket);
		return url;
		// return ExecUrl_Get(url);
	}

	/*
	 * 发出GET请求
	 */
	private static String ExecUrl_Get(String url) {
		String line = "";
		try {
			URL reUrl = new URL(url);
			HttpsURLConnection httpsConn = (HttpsURLConnection) reUrl.openConnection();

			httpsConn.setRequestMethod("GET");
			httpsConn.setDoInput(true);

			Reader reader = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
			BufferedReader br = new BufferedReader(reader);
			String s = null;
			do {
				s = br.readLine();
				if (s != null) {
					line += s;
				}
			} while (s != null);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line;
	}

	/*
	 * 发出POST/Form请求
	 */
	public static String send(String url, String filePath) {

		String result = null;
		try {
			File file = new File(filePath);
			if (!file.exists() || !file.isFile()) {
				throw new IOException("文件不存在");
			}

			/**
			 * 第一部分
			 */
			URL urlObj = new URL(url);
			// 连接
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

			/**
			 * 设置关键值
			 */
			con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false); // post方式不能使用缓存

			// 设置请求头信息
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");

			// 设置边界
			String BOUNDARY = "----------" + System.currentTimeMillis();
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			// 请求正文信息

			// 第一部分：
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");

			byte[] head = sb.toString().getBytes("utf-8");

			// 获得输出流
			OutputStream out = new DataOutputStream(con.getOutputStream());
			// 输出表头
			out.write(head);

			// 文件正文部分
			// 把文件已流文件的方式 推入到url中
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();

			// 结尾部分
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线

			out.write(foot);

			out.flush();
			out.close();

			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = null;
			try {
				// 定义BufferedReader输入流来读取URL的响应
				reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					// System.out.println(line);
					buffer.append(line);
				}
				if (result == null) {
					result = buffer.toString();
				}
			} catch (IOException e) {
				System.out.println("发送POST请求出现异常！" + e);
				e.printStackTrace();
				throw new IOException("数据读取异常");
			} finally {
				if (reader != null) {
					reader.close();
				}

			}
		} catch (Exception e) {

		}
		return result;
		// JSONObject jsonObj = JSONObject.fromObject(result);
		// String mediaId = jsonObj.getString("media_id");
		// return mediaId;
	}

	/*
	 * 发送POST/Json请求
	 */
	public static String sendJson(String url, String param) {
		try {
			URL reurl = new URL(url);
			HttpURLConnection urlCon = (HttpURLConnection) reurl.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			urlCon.setInstanceFollowRedirects(true);
			urlCon.setRequestMethod("POST");
			urlCon.setRequestProperty("Content-Type", "application/json");
			urlCon.connect();
			OutputStreamWriter out = new OutputStreamWriter(urlCon.getOutputStream(), "UTF-8"); // utf-8编码
			out.append(param);
			out.flush();
			out.close();

			int length = (int) urlCon.getContentLength();// 获取长度
			InputStream is = urlCon.getInputStream();
			if (length != -1) {
				byte[] data = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, data, destPos, readLen);
					destPos += readLen;
				}
				String result = new String(data, "UTF-8");
				return result;
			}
			return "0";

		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			try {

			} catch (Exception ex) {
				return "0";
			}
		}
	}

	/*
	 * 1新增,0新增失败,2已存在
	 */
	public static int ToDataBase(HashMap hm) {
		weixinuser wxu = new weixinuser();
		DBHandler dbh = new DBHandler();

		String tmp = hm.get("\"openid\"").toString();
		wxu.openid = tmp.substring(1, tmp.length() - 1);
		tmp = hm.get("\"nickname\"").toString();
		wxu.nickname = tmp.substring(1, tmp.length() - 1);
		tmp = hm.get("\"sex\"").toString();
		wxu.Sex = Integer.parseInt(tmp);
		tmp = hm.get("\"language\"").toString();
		wxu.language = tmp.substring(1, tmp.length() - 1);
		tmp = hm.get("\"city\"").toString();
		wxu.city = tmp.substring(1, tmp.length() - 1);
		tmp = hm.get("\"province\"").toString();
		wxu.province = tmp.substring(1, tmp.length() - 1);
		tmp = hm.get("\"country\"").toString();
		wxu.country = tmp.substring(1, tmp.length() - 1);
		tmp = hm.get("\"headimgurl\"").toString();
		tmp = tmp.replace("\\", "");
		wxu.headimgurl = tmp.substring(1, tmp.length() - 1);
		
		weixinuser t_wxu = dbh.GetWeiXinUser(wxu.openid);
		if(t_wxu.UserId != 0){
			wxu.UserId = t_wxu.UserId;
			wxu.State = 1;
			return dbh.EntityChanges(wxu, 0) == true ? 1:0;
		}
		else{
			int tmpid = dbh.GetNewId("weixinuser");
			if (tmpid == -1) {
				return 0;
			}
			wxu.UserId = tmpid;
			wxu.State = 1;
			return dbh.EntityChanges(wxu, 1) == true ? 1 : 0;
		}
	}

	// 从网址中获得图片
	public static void getURLResource(String outputFile, String urlStr) {
		try {
			URL url = new URL(urlStr);
			// 打开连接
			URLConnection con = url.openConnection();
			// 设置请求超时为5s
			con.setConnectTimeout(5 * 1000);
			// 输入流
			InputStream is = con.getInputStream();

			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			File sf = new File("./");
			if (!sf.exists()) {
				sf.mkdirs();
			}
			OutputStream os = new FileOutputStream(sf.getPath() + "\\" + outputFile);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			// 完毕，关闭所有链接
			os.close();
			is.close();
		} catch (Exception e) {

		}
	}

	// 获取微信用户
	public static weixinuser GetWeixinUser(String s) {
		DBHandler db = new DBHandler();
		return db.GetWeiXinUser(s);
	}

	// 绑定上下级
	public static String BindUser(String high, String low) {
		DBHandler db = new DBHandler();
		return db.CreateRelation(high, low);
	}

	// 获得绑定列表
	public static List<weixinuser> GetUserBind(String Id) {
		DBHandler db = new DBHandler();
		return db.GetUserBind(Id);
	}

	//更新数据库用户信息
	public static void UpdateDataBase(){
		DBHandler db = new DBHandler();
		String s = "";
		List<String> re = db.GetWeixinuserList(0);
		String t_open = "";
		for(int i = 0 ; i < re.size() ; i++){
			t_open = GetUserByOpenId(re.get(i));
			HashMap hm = FormatOut(t_open);
			ToDataBase(hm);
		}
	}

	//更新用户信息
	public static boolean Updateweixinuser(weixinuser w){
		DBHandler dbh = new DBHandler();
		return dbh.Updateweixinuser(w);
	}
	
	//获得层级信息
	public static role GetRole(String ID){
		DBHandler dbh = new DBHandler();
		return dbh.GetRole(ID);
	}
}
