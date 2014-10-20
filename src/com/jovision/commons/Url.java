package com.jovision.commons;

import com.jovision.utils.ConfigUtil;

public class Url {

	// 找回密码
	public static String RESET_PWD_URL = "http://webapp.afdvr.com/findpwd/index.html";// 重置密码地址
	public static String RESET_PWD_URL_EN = "http://webappen.afdvr.com/findpwd/index.html";// 重置密码地址

	// 检查软件更新地址
	public static String CHECK_UPDATE_URL = "http://wmap.yoosee.cc/MobileWeb.aspx";// 检查软件更新地址
	public static String APK_DOWNLOAD_URL = "http://wmap.yoosee.cc/";// 下载更新软件地址

	// 摇一摇了解更多地址
	public static String LEARN_MORE = "http://www.jovision.com/Products/Camera.aspx?classId=166";
	public static String LEARN_MORE_EN = "http://en.jovision.com/Products/OtherProducts.aspx?classId=22";

	// public static String SHORTSERVERIP = "58.56.19.187";
	// public static String LONGSERVERIP = "58.56.19.187";
	public static String SHORTSERVERIP = ConfigUtil.getServerLanguage() == JVConst.LANGUAGE_ZH ? "appchannel.afdvr.com"
			: "appchannelen.afdvr.com"; // "appchannel.jovecloud.com";
	public static String LONGSERVERIP = ConfigUtil.getServerLanguage() == JVConst.LANGUAGE_ZH ? "apponline.afdvr.com"
			: "apponlineen.afdvr.com"; // "apponline.jovecloud.com";
}