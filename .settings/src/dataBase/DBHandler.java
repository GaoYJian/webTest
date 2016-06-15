package dataBase;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHandler {
	public static String DBurl = "";
	public static String user = "";
	public static String password = "";
	SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public DBHandler() {
		this.DBurl = "jdbc:mysql://cx.jk0086.com:3306/medical?";
		this.user = "root";
		this.password = "P!ssw0rd";
	}

	public DBHandler(String url, String user, String password) {
		this.DBurl = url;
		this.user = user;
		this.password = password;
	}

	public boolean Connect() {
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();

				String sql = "select * from idgeneral";
				ResultSet re = statement.executeQuery(sql);
				IdGeneral ig = new IdGeneral();
				while (re.next()) {
					ig.id = re.getInt("id");
					ig.tableName = re.getString("TableName");
					ig.tableid = re.getInt("TableId");
					ig.lastedittime = re.getTimestamp("LastEditTime");
				}

				sql = "update idgeneral set TableId =" + (++ig.tableid) + " where id =" + ig.id;
				statement.execute(sql);

				sql = "insert into idgeneral values(2,'Test',0,'" + sFormat.format(new Date()) + "')";
				statement.execute(sql);

				sql = "delete from idgeneral where id = 2";
				statement.execute(sql);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {

				}
			}
		}
		return false;
	}

	// ��ѯAccess_Token�Ƿ����,�����򷵻�null,����ֱ�ӷ���access_token
	public String CheckAccess() {
		int count = 0;
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();

				String sql = "select * from config where ConfigKey = 'Access_Token'";
				ResultSet bn = statement.executeQuery(sql);
				if (bn.next()) {
					Date d1 = bn.getTimestamp("LastEditTime");
					Date d2 = new Date();
					if ((d2.getTime() - d1.getTime()) / 1000 < 7150) {// ʮ�뻺��
						return bn.getString("ConfigValue");
					} else
						return null;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}

				} catch (Exception e) {

				}
			}
		}
		return null;
	}

	// ����Access_Token
	public boolean UpdateAccess(String tk) {
		int count = 0;
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();

				String sql = "update config set ConfigValue = '" + tk + "',LastEditTime = '"
						+ sFormat.format(new Date()) + "' where ConfigKey = 'Access_Token'";
				if (statement.executeUpdate(sql) > 0) {
					return true;
				}
				return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}

				} catch (Exception e) {

				}
			}
		}
		return false;
	}

	/*
	 * ���ݱ����������ID
	 */
	public int GetNewId(String EntityName) {
		int result = -1;
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();

				String sql = "select * from idgeneral where TableName = '" + EntityName + "' limit 1";
				ResultSet re = statement.executeQuery(sql);
				IdGeneral ig = null;
				while (re.next()) {
					ig = new IdGeneral();
					ig.id = re.getInt("id");
					ig.tableName = re.getString("TableName");
					ig.tableid = re.getInt("TableId");
					ig.lastedittime = re.getTimestamp("LastEditTime");
				}
				if (ig == null) {
					throw new Exception();
				}
				sql = "update idgeneral set TableId =" + (ig.tableid + 1) + ",LastEditTime ='"
						+ sFormat.format(new Date()) + "' where id =" + ig.id;
				if (statement.executeUpdate(sql) > 0) {
					result = ig.tableid + 1;
				}
				// sql = "insert into idgeneral values(2,'Test',0,'" +
				// sFormat.format(new Date()) + "')";
				// statement.execute(sql);
				// sql = "delete from idgeneral where id = 2";
				// statement.execute(sql);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}
					return result;
				} catch (Exception e) {

				}
			}
		}
		return result;
	}

	/*
	 * �û���Ϣ���¼,b �޸�״̬ -1ɾ 0�� 1��
	 */
	public boolean EntityChanges(weixinuser a, int b) {
		boolean ret = false;
		String sql = "";
		if (a == null) {
			return ret;
		}
		if (GetWeiXinUser(a.UserId + "").UserId != 0) {
			b = 0;
		}

		switch (b) {
		case -1:
			// sql = "delete from weixinuser where UserId = " + a.UserId;
			break;
		case 0:
			sql = "update weixinuser set openid = '" + a.openid + "',nickname = '" + a.nickname + "',sex = " + a.Sex
					+ ",language = '" + a.language + "',city = '" + a.city + "',province = '" + a.province
					+ "',country = '" + a.country + "',headimgurl = '" + a.headimgurl + "',State =" + a.State
					+ ",LastEditTime = '" + sFormat.format(new Date()) + "' where UserId =" + a.UserId;
			break;
		case 1:
			sql = "insert into weixinuser values(" + a.UserId + ",'" + a.openid + "','" + a.nickname + "'," + a.Sex
					+ ",'" + a.language + "','" + a.city + "','" + a.province + "','" + a.country + "','" + a.headimgurl
					+ "','" + a.remark + "'," + a.State + ",'" + a.nickname + "','" + a.Reserver2 + "','" + a.Reserver3
					+ "','" + sFormat.format(new Date()) + "',-1)";
			break;
		}
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();
				ret = statement.executeUpdate(sql) > 0 ? true : false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ret;
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}
					return ret;
				} catch (Exception e) {

				}
			}
		}

		return ret;
	}

	/*
	 * �Ƿ��Ѵ���
	 */
	public boolean EntitySearch(weixinuser a) {
		int count = 0;
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();

				String sql = "select count(*) as total from weixinuser where openid = '" + a.openid + "'";
				ResultSet bn = statement.executeQuery(sql);
				while (bn.next()) {
					count = bn.getInt("total");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}

				} catch (Exception e) {

				}
			}
		}
		return count > 0 ? true : false;
	}

	/*
	 * ����ID��OpenId��ȡ�û���Ϣ
	 */
	public weixinuser GetWeiXinUser(String ID) {
		weixinuser wxu = new weixinuser();
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();

				String sql = "select * from weixinuser where openid = '" + ID + "'";
				ResultSet bn = statement.executeQuery(sql);
				if (bn.next()) {
					wxu.UserId = bn.getInt("UserId");
					wxu.openid = bn.getString("openid");
					wxu.nickname = bn.getString("nickname");
					wxu.Sex = bn.getInt("sex");
					wxu.language = bn.getString("language");
					wxu.city = bn.getString("city");
					wxu.province = bn.getString("province");
					wxu.country = bn.getString("country");
					wxu.headimgurl = bn.getString("headimgurl");
					wxu.remark = bn.getString("remark");
					wxu.State = bn.getInt("State");
					wxu.LastEditTime = bn.getDate("LastEditTime");
					wxu.RoleId = bn.getInt("RoleId");
				} else {
					sql = "select * from weixinuser where UserId = '" + ID + "'";
					bn = statement.executeQuery(sql);
					if (bn.next()) {
						wxu.UserId = bn.getInt("UserId");
						wxu.openid = bn.getString("openid");
						wxu.nickname = bn.getString("nickname");
						wxu.Sex = bn.getInt("sex");
						wxu.language = bn.getString("language");
						wxu.city = bn.getString("city");
						wxu.province = bn.getString("province");
						wxu.country = bn.getString("country");
						wxu.headimgurl = bn.getString("headimgurl");
						wxu.remark = bn.getString("remark");
						wxu.State = bn.getInt("State");
						wxu.LastEditTime = bn.getDate("LastEditTime");
						wxu.RoleId = bn.getInt("RoleId");
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}

				} catch (Exception e) {

				}
			}
		}
		return wxu;
	}

	/*
	 * ����ID��ò㼶��Ϣ
	 */
	public role GetRole(String ID) {
		role r = new role();
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();

				String sql = "select * from role where RoleId = '" + ID + "'";
				ResultSet bn = statement.executeQuery(sql);
				if (bn.next()) {
					r.RoleId = bn.getInt("RoleId");
					r.RoleName = bn.getString("RoleName");
					r.UpLevel = bn.getInt("UpLevel");
					r.UpName = bn.getString("UpName");
					r.DownLevel = bn.getInt("DownLevel");
					r.DownName = bn.getString("DownName");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}

				} catch (Exception e) {

				}
			}
		}
		return r;
	}

	/*
	 * �������¼�
	 */
	public String CreateRelation(String UpId, String DownId) {
		weixinuser uw = GetWeiXinUser(UpId);
		weixinuser dw = GetWeiXinUser(DownId);
		relation rl = new relation();
		if (uw.UserId == 0 || dw.UserId == 0) {// �û���Ϣ��ȫ
			return "�û���Ϣ������";
		}
		if (uw.UserId == dw.UserId) {
			return "�����Լ������Լ�";
		}
		if (uw.RoleId == -1) {// ���û���û�ּ�
			return "�û�δ�зּ�";
		}
		role r = GetRole(uw.RoleId + "");
		if (r.RoleId == -1 || r.DownLevel == -1) {// �����ڲ㼶�����Ѿ������¼�
			return "�㼶�����ڻ��Ѿ�����ͼ�";
		}
		rl.UpperId = uw.UserId;
		rl.UpperOpenId = uw.openid;
		rl.LowerId = dw.UserId;
		rl.LowerOpenId = dw.openid;
		rl.Remark = "����";
		rl.LastEditTime = new Date();
		rl.UpperRoleId = r.RoleId;
		rl.UpperRole = r.RoleName;
		rl.LowerRoleId = r.DownLevel;
		rl.LowerRole = r.DownName;
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();

				String sql = "select * from relation where LowerId = '" + dw.UserId + "'";
				ResultSet bn = statement.executeQuery(sql);
				if (bn.next()) {
					return "�Ѿ����ϼ�����";
				} else {// δ���
					sql = "insert into relation values(" + rl.UpperId + ",'" + rl.UpperOpenId + "'," + rl.LowerId + ",'"
							+ rl.LowerOpenId + "','" + rl.Remark + "','" + sFormat.format(rl.LastEditTime) + "','','',"
							+ rl.UpperRoleId + ",'" + rl.UpperRole + "'," + rl.LowerRoleId + ",'" + rl.LowerRole + "')";

					if (statement.executeUpdate(sql) > 0) {
						sql = "update weixinuser set RoleId =" + rl.LowerRoleId + " where UserId =" + dw.UserId;
						statement.executeUpdate(sql);
						return "�ɹ�";
					}
					return "����ʧ��";
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "δ֪����";
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}

				} catch (Exception e) {

				}
			}
		}
		return "������Ϣ����";
	}

	/*
	 * ��ȡ�������б�
	 */
	public List<weixinuser> GetUserBind(String sId) {
		List<weixinuser> re = new ArrayList<weixinuser>();

		weixinuser wu = GetWeiXinUser(sId);
		if (wu.UserId == 0) {
			return re;
		}
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();

				int count = 0;
				String sql = "select * from relation where UpperId = " + wu.UserId;
				ResultSet bn = statement.executeQuery(sql);
				String r = "";
				while (bn.next()) {
					r = bn.getString("LowerId");
					wu = GetWeiXinUser(r);
					if (wu.UserId != 0 && wu.State == 1) {
						re.add(wu);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}

				} catch (Exception e) {

				}
			}
		}
		return re;
	}

	/*
	 * ��ȡ��̨openid
	 */
	public List<String> GetWeixinuserList(int n) {
		List<String> re = new ArrayList<String>();

		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();

				int count = 0;
				String sql = "select * from weixinuser";
				if (n > 0) {
					sql += "where RoleId = " + n;
				}
				ResultSet bn = statement.executeQuery(sql);
				String r = "";
				while (bn.next()) {
					r = bn.getString("openid");
					re.add(r);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}

				} catch (Exception e) {

				}
			}
		}
		return re;
	}

	/*
	 * ����״̬��ά����Ϣ
	 */
	public boolean Updateweixinuser(weixinuser w) {
		boolean ret = false;
		weixinuser t_w = GetWeiXinUser(w.UserId + "");
		if (t_w.UserId == 0) {
			return ret;
		}
		String sql = "update weixinuser set State =" + w.State + ",Reserver1 = '" + w.Reserver1 + "',Reserver2 = '"
				+ w.Reserver2 + "',LastEditTime = '" + sFormat.format(new Date()) + "' where UserId =" + w.UserId;
		if (DBurl.length() > 0 && user.length() > 0 && password.length() > 0) {
			Connection conn = null;
			Statement statement = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(
						DBurl + "user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF8");
				statement = conn.createStatement();
				ret = statement.executeUpdate(sql) > 0 ? true : false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ret;
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (conn != null) {
						conn.close();
					}
					return ret;
				} catch (Exception e) {

				}
			}
		}

		return ret;
	}
}
