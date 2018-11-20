package dataBase;

import java.util.Date;

public class weixinuser {
	public int UserId = 0;//用户ID
	public String openid;//微信Openid
	public String nickname;//微信用户名
	public int Sex;//性别  0 1
	public String language;//语言
	public String city;//城市
	public String province;//省份
	public String country;//国籍
	public String headimgurl;//头像图片地址
	public String remark;//备注
	public int State;//状态 1 0
	public String Reserver1;//昵称
	public String Reserver2;//电话号
	public String Reserver3;//未用
	public Date LastEditTime;//最后修改时间
	public int RoleId;//层级Id
	
	public weixinuser(){}
}
