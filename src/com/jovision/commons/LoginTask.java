package com.jovision.commons;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
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

	public LoginTask(Context con, Application app, HashMap<String, String> map,
			View view) {
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
			String strRes = "";
				strRes = AccountUtil.onLoginProcessV2(mContext,
						statusHashMap.get(Consts.KEY_USERNAME),
						statusHashMap.get(Consts.KEY_PASSWORD),
						Url.SHORTSERVERIP, Url.LONGSERVERIP);
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
