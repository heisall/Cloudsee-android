package com.jovision.commons;

import com.jovision.utils.ConfigUtil;

public class Url {

	// webcc接口地址
	public static String LOGIN_URL = "http://api.jovecloud.com/default.aspx?";// 获取json数据地址
	public static String UPLOAD_IMAGE_URL = "http://api.jovecloud.com/Upload.aspx";// 图片上传地址
	public static String DOWNLOAD_IMAGE_URL = "http://api.jovecloud.com";// 图片下载地址
	public static String RESET_PWD_URL = "http://webapp.afdvr.com/findpwd/index.html";// 重置密码地址
	public static String RESET_PWD_URL_EN = "http://webappen.afdvr.com/findpwd/index.html";// 重置密码地址
																							// 英文
	// http://www.jovecloud.com/ResetPwd.aspx?lang=en_us
	// http://www.jovecloud.com/ResetPwd.aspx?lang=zh_cn
	// 报警消息推送地址和端口
	public static String ALARM_PUSH_IP = "120.192.31.134";// 推送IP 192.168.15.6
	public static String ALARM_PUSH_PORT = "7100";// 推送端口
	public static final int ALARM_PUSH_APPTYPE = 1;

	// 检查软件更新地址
	public static String CHECK_UPDATE_URL = "http://wmap.yoosee.cc/MobileWeb.aspx";// 检查软件更新地址
	public static String APK_DOWNLOAD_URL = "http://wmap.yoosee.cc/";// 下载更新软件地址

	// 演示点地址
	public static String MAP_SHOW_POINT = "http://wmap.yoosee.cc/MobileWeb.aspx?";
	public static String MAP_SHOW_POINT_IMAGE = "http://wmap.yoosee.cc/";
	public static String MAP_POINT_DETAIL = "http://wmap.yoosee.cc/MobileWeb.aspx?RequestType=2&GUID=";

	// 摇一摇了解更多地址
	public static String LEARN_MORE = "http://www.jovision.com/Products/Camera.aspx?classId=166";
	public static String LEARN_MORE_EN = "http://en.jovision.com/Products/OtherProducts.aspx?classId=22";
	// public static String SERVERIP = "192.168.14.234";
	public static String SHORTSERVERIP = ConfigUtil.getLanguage() == JVConst.LANGUAGE_ZH ? "appchannel.afdvr.com"
			: "appchannelen.afdvr.com"; // "appchannel.jovecloud.com";
	public static String LONGSERVERIP = ConfigUtil.getLanguage() == JVConst.LANGUAGE_ZH ? "apponline.afdvr.com"
			: "apponlineen.afdvr.com"; // "apponline.jovecloud.com";
}