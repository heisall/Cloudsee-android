package com.jovision.commons;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.JVEditOldUserInfoActivity;
import com.jovision.activities.JVLoginActivity;
import com.jovision.bean.User;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.UserUtil;
import com.tencent.stat.StatService;

//获取演示点 设置三种类型参数分别为String,Integer,String
public class LoginTask extends AsyncTask<String, Integer, Integer> {
	private Context mContext;
	private int loginRes1 = 0;
	private Application application;
	private HashMap<String, String> statusHashMap;
	private View alarmnet;
	private boolean firstLogin;// 首页登陆

	public LoginTask(boolean loginFlag, Context con, Application app,
			HashMap<String, String> map, View view) {
		firstLogin = loginFlag;
		mContext = con;
		application = app;
		statusHashMap = map;
		alarmnet = view;
	}

	@Override
	protected Integer doInBackground(String... params) {
		if (!ConfigUtil.isConnected(mContext)) {
			loginRes1 = JVAccountConst.LOGIN_FAILED_1;
		} else {
			MyLog.v("BaseA", "LOGIN---E");
			if ("false".equals(statusHashMap.get(Consts.KEY_INIT_ACCOUNT_SDK))) {
				MyLog.e("Login", "初始化账号SDK失败,重新初始化");
				ConfigUtil.initAccountSDK(application);// 初始化账号SDK
			}

			MyLog.e("Ignore Login",
					"user=" + statusHashMap.get(Consts.KEY_USERNAME) + ";pass="
							+ statusHashMap.get(Consts.KEY_PASSWORD));

			String strRes = "";

			int needOnline = 0;// 1:起心跳 0：不需要起心跳

			if (firstLogin) {
				needOnline = 1; // 第一次登陆需要起心跳
			}
			strRes = AccountUtil.onLoginProcessV2(mContext,
					statusHashMap.get(Consts.KEY_USERNAME),
					statusHashMap.get(Consts.KEY_PASSWORD), Url.SHORTSERVERIP,
					Url.LONGSERVERIP, needOnline);
			JSONObject respObj = null;
			try {
				respObj = new JSONObject(strRes);
				loginRes1 = respObj.optInt("arg1", 1);
				// {"arg1":8,"arg2":0,"data":{"channel_ip":"210.14.156.66","online_ip":"210.14.156.66"},"desc":"after the judge and longin , begin the big switch...","result":0}
				String data = respObj.optString("data");
				if (null != data && !"".equalsIgnoreCase(data)) {
					JSONObject dataObj = new JSONObject(data);
					String channelIp = dataObj.optString("channel_ip");
					String onlineIp = dataObj.optString("online_ip");
					if (Consts.LANGUAGE_ZH == ConfigUtil.getServerLanguage()) {
						MySharedPreference.putString("ChannelIP", channelIp);
						MySharedPreference.putString("OnlineIP", onlineIp);
						MySharedPreference.putString("ChannelIP_en", "");
						MySharedPreference.putString("OnlineIP_en", "");
					} else {
						MySharedPreference.putString("ChannelIP_en", channelIp);
						MySharedPreference.putString("OnlineIP_en", onlineIp);
						MySharedPreference.putString("ChannelIP", "");
						MySharedPreference.putString("OnlineIP", "");
					}
				}

			} catch (JSONException e) {
				loginRes1 = JVAccountConst.LOGIN_FAILED_2;
				e.printStackTrace();
			}
			MyLog.v("BaseA", "LOGIN---X--res=" + loginRes1);
		}

		return loginRes1;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Integer result) {
		// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
		((BaseActivity) mContext).dismissDialog();

		switch (result) {
		case JVAccountConst.LOGIN_SUCCESS: {
			MySharedPreference.putBoolean(Consts.FIRST_LOGIN, false);
			StatService.trackCustomEvent(mContext, "onlinelogin", mContext
					.getResources().getString(R.string.census_onlinelogin));
			MySharedPreference.putString("UserName",
					statusHashMap.get(Consts.KEY_USERNAME));
			MySharedPreference.putString("PassWord",
					statusHashMap.get(Consts.KEY_PASSWORD));
			// 重置手动注销标志，离线报警使用，如果为手动注销账号，不接收离线报警
			MySharedPreference.putBoolean(Consts.MANUAL_LOGOUT_TAG, false);
			User user = new User();
			user.setPrimaryID(System.currentTimeMillis());
			user.setUserName(statusHashMap.get(Consts.KEY_USERNAME));
			user.setUserPwd(statusHashMap.get(Consts.KEY_PASSWORD));
			user.setLastLogin(1);
			user.setJudgeFlag(1);
			UserUtil.addUser(user);
			MyLog.v("BaseA", "LoginSuccess");
			statusHashMap.put(Consts.ACCOUNT_ERROR,
					String.valueOf(Consts.WHAT_ACCOUNT_NORMAL));
			if (null != alarmnet) {
				alarmnet.setVisibility(View.GONE);
			}
			break;
		}
		case JVAccountConst.RESET_NAME_AND_PASS: {
			if (firstLogin) {
				MySharedPreference.putBoolean(Consts.FIRST_LOGIN, false);
				Intent intent = new Intent();
				intent.setClass(mContext, JVEditOldUserInfoActivity.class);
				mContext.startActivity(intent);
			}
			break;
		}

		case JVAccountConst.PASSWORD_ERROR: {
			if (firstLogin) {
				MySharedPreference.putBoolean(Consts.FIRST_LOGIN, false);
				UserUtil.resetAllUser();
				((BaseActivity) mContext)
						.showTextToast(R.string.str_userpass_error);
				Intent intent = new Intent();
				intent.putExtra("AutoLogin", false);
				intent.putExtra("UserName",
						statusHashMap.get(Consts.KEY_USERNAME));
				intent.putExtra("UserPass", "");
				intent.setClass(mContext, JVLoginActivity.class);
				mContext.startActivity(intent);
				statusHashMap.put(Consts.KEY_USERNAME, "");
				statusHashMap.put(Consts.KEY_PASSWORD, "");
			}
			break;
		}
		case JVAccountConst.SESSION_NOT_EXSIT: {
			if (firstLogin) {
				MySharedPreference.putBoolean(Consts.FIRST_LOGIN, false);
				((BaseActivity) mContext)
						.showTextToast(R.string.str_session_not_exist);
				Intent intent = new Intent();
				intent.putExtra("AutoLogin", false);
				intent.putExtra("UserName",
						statusHashMap.get(Consts.KEY_USERNAME));
				intent.setClass(mContext, JVLoginActivity.class);
				mContext.startActivity(intent);
				statusHashMap.put(Consts.KEY_USERNAME, "");
				statusHashMap.put(Consts.KEY_PASSWORD, "");
			}
			break;
		}
		case JVAccountConst.USER_HAS_EXIST: {

			break;
		}
		case JVAccountConst.USER_NOT_EXIST: {
			if (firstLogin) {
				MySharedPreference.putBoolean(Consts.FIRST_LOGIN, false);
				UserUtil.resetAllUser();
				((BaseActivity) mContext)
						.showTextToast(R.string.str_user_not_exist);
				Intent intent = new Intent();
				intent.setClass(mContext, JVLoginActivity.class);
				mContext.startActivity(intent);
				statusHashMap.put(Consts.KEY_USERNAME, "");
				statusHashMap.put(Consts.KEY_PASSWORD, "");
			}
			break;
		}
		case JVAccountConst.LOGIN_FAILED_1: {
			if (firstLogin) {
				MySharedPreference.putBoolean(Consts.FIRST_LOGIN, false);
				if (MySharedPreference.getBoolean(Consts.MORE_REMEMBER, false)) {// 自动登陆，离线登陆
					statusHashMap.put(Consts.ACCOUNT_ERROR,
							String.valueOf(Consts.WHAT_HAS_NOT_LOGIN));
				}
			}
			break;
		}
		case JVAccountConst.LOGIN_FAILED_2: {
			if (firstLogin) {
				MySharedPreference.putBoolean(Consts.FIRST_LOGIN, false);
				if (MySharedPreference.getBoolean(Consts.MORE_REMEMBER, false)) {// 自动登陆，离线登陆
					statusHashMap.put(Consts.ACCOUNT_ERROR,
							String.valueOf(Consts.WHAT_HAS_NOT_LOGIN));
				}
			}
			break;
		}
		}
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
		((BaseActivity) mContext).createDialog("", true);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
	}

}
