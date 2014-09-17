package com.jovision.thread;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.Url;
import com.jovision.utils.AccountUtil;

/**
 * 登录线程
 * 
 * @author suifupeng
 * 
 */
public class LoginThread extends Thread {
	/** 上下文 */
	private Context mContext;
	private HashMap<String, String> statusHashMap = null;

	public LoginThread(Context con) {
		mContext = con;
		statusHashMap = ((MainApplication) mContext.getApplicationContext())
				.getStatusHashMap();
	}

	@Override
	public void run() {
		String strRes = AccountUtil.onLoginProcess(mContext,
				statusHashMap.get(Consts.KEY_USERNAME),
				statusHashMap.get(Consts.KEY_PASSWORD), Url.SHORTSERVERIP,
				Url.LONGSERVERIP);
		JSONObject respObj = null;
		int arg1 = 0, arg2 = 0;
		try {
			respObj = new JSONObject(strRes);
			arg1 = respObj.optInt("arg1", 1);
			arg2 = respObj.optInt("arg2", 0);
		} catch (JSONException e) {
			arg1 = JVAccountConst.LOGIN_FAILED_2;
			arg2 = 0;
			e.printStackTrace();
		}
		((MainApplication) mContext.getApplicationContext()).onNotify(
				Consts.LOGIN_FUNCTION, arg1, arg2, null);

		// int result = AccountUtil.judgeUser(
		// statusHashMap.get(Consts.KEY_USERNAME),
		// statusHashMap.get(Consts.KEY_PASSWORD));
		// if (JVAccountConst.SUCCESS == result) {
		// String ip = JVACCOUNT.GetServerIP();
		// String channelIp = ip.split(";")[0];
		// String onlineIp = ip.split(";")[1];
		// if (0 == BaseApp.getLanguage()) {
		// MySharedPreference.putString("ChannelIP", channelIp);
		// MySharedPreference.putString("OnlineIP", onlineIp);
		// MySharedPreference.putString("ChannelIP_en", "");
		// MySharedPreference.putString("OnlineIP_en", "");
		// } else {
		// MySharedPreference.putString("ChannelIP_en", channelIp);
		// MySharedPreference.putString("OnlineIP_en", onlineIp);
		// MySharedPreference.putString("ChannelIP", "");
		// MySharedPreference.putString("OnlineIP", "");
		// }
		// ((MainApplication) mContext.getApplicationContext()).onNotify(
		// Consts.LOGIN_FUNCTION, JVAccountConst.LOGIN_SUCCESS, 0,
		// null);
		// } else if (JVAccountConst.RESET_NAME_AND_PASS == result) {
		// ((MainApplication) mContext.getApplicationContext()).onNotify(
		// Consts.LOGIN_FUNCTION, JVAccountConst.RESET_NAME_AND_PASS,
		// 0, null);
		// } else if (JVAccountConst.RESET_PASSWORD == result) {
		// JVACCOUNT.ResetUserPassword(statusHashMap.get(Consts.KEY_PASSWORD),
		// statusHashMap.get(Consts.KEY_USERNAME));
		// int results = AccountUtil.reportClientPlatformInfo();
		// if (0 == results) {
		// results = JVACCOUNT.RegisterServerPushFunc();
		// }
		// if (0 == results) {
		// results = AccountUtil.userOnline();
		// }
		// if (0 == results) {
		// ((MainApplication) mContext.getApplicationContext()).onNotify(
		// Consts.LOGIN_FUNCTION,
		// JVAccountConst.RESET_PASSWORD_SUCCESS, 0, null);
		// } else {
		// ((MainApplication) mContext.getApplicationContext()).onNotify(
		// Consts.LOGIN_FUNCTION, JVAccountConst.LOGIN_FAILED_1,
		// -6, null);
		// }
		// } else if (JVAccountConst.PASSWORD_ERROR == result) {
		// ((MainApplication) mContext.getApplicationContext()).onNotify(
		// Consts.LOGIN_FUNCTION, JVAccountConst.PASSWORD_ERROR, 0,
		// null);
		// } else if (JVAccountConst.SESSION_NOT_EXSIT == result) {
		// ((MainApplication) mContext.getApplicationContext()).onNotify(
		// Consts.LOGIN_FUNCTION, JVAccountConst.SESSION_NOT_EXSIT, 0,
		// null);
		// } else if (JVAccountConst.USER_HAS_EXIST == result) {
		// ((MainApplication) mContext.getApplicationContext()).onNotify(
		// Consts.LOGIN_FUNCTION, JVAccountConst.USER_HAS_EXIST, 0,
		// null);
		// } else if (JVAccountConst.USER_NOT_EXIST == result) {
		// ((MainApplication) mContext.getApplicationContext()).onNotify(
		// Consts.LOGIN_FUNCTION, JVAccountConst.USER_NOT_EXIST, 0,
		// null);
		// } else {
		// if (result > 0) {
		// ((MainApplication) mContext.getApplicationContext()).onNotify(
		// Consts.LOGIN_FUNCTION, JVAccountConst.LOGIN_FAILED_2,
		// 0, null);
		// } else {
		// if (-5 == result || -6 == result || -7 == result) {
		// JVACCOUNT.ConfigServerAddress(Url.SHORTSERVERIP,
		// Url.LONGSERVERIP);
		// }
		// ((MainApplication) mContext.getApplicationContext()).onNotify(
		// Consts.LOGIN_FUNCTION, JVAccountConst.LOGIN_FAILED_1,
		// result, null);
		// }
		// }
		super.run();
	}
}
