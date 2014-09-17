package com.jovision.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.test.JVACCOUNT;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.JVBoundEmailActivity;
import com.jovision.activities.JVEditOldUserInfoActivity;
import com.jovision.activities.JVLoginActivity;
import com.jovision.activities.JVTabActivity;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MySharedPreference;
import com.jovision.newbean.UserBean;
import com.jovision.utils.SqlLiteUtil;

/**
 * 登录处理Handler
 * 
 * @author suifupeng
 * 
 */
public class LoginHandler extends Handler {
	private Context context;
	/** 是否跳转到登录界面（登录界面不必跳转） */
	private boolean jump;

	public LoginHandler(Context con, boolean isJump) {
		context = con;
		jump = isJump;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		Intent intent = new Intent();
		switch (msg.arg1) {
		case JVAccountConst.LOGIN_SUCCESS: {
			MySharedPreference.putString("UserName",
					((BaseActivity) context).statusHashMap
							.get(Consts.KEY_USERNAME));
			MySharedPreference.putString("PassWord",
					((BaseActivity) context).statusHashMap
							.get(Consts.KEY_PASSWORD));
			((BaseActivity) context).dismissDialog();
			UserBean user = new UserBean();
			user.setPrimaryID(System.currentTimeMillis());
			user.setUserName(((BaseActivity) context).statusHashMap
					.get(Consts.KEY_USERNAME));
			user.setUserPwd(((BaseActivity) context).statusHashMap
					.get(Consts.KEY_PASSWORD));
			SqlLiteUtil.addUser(user);
			intent.setClass(context, JVTabActivity.class);
			context.startActivity(intent);
			((BaseActivity) context).finish();
			break;
		}
		case JVAccountConst.RESET_NAME_AND_PASS: {
			((BaseActivity) context).dismissDialog();
			intent.setClass(context, JVEditOldUserInfoActivity.class);
			context.startActivity(intent);
			break;
		}
		case JVAccountConst.RESET_PASSWORD_SUCCESS: {
			UserBean user1 = new UserBean();
			user1.setPrimaryID(System.currentTimeMillis());
			user1.setUserName(((BaseActivity) context).statusHashMap
					.get(Consts.KEY_USERNAME));
			user1.setUserPwd(((BaseActivity) context).statusHashMap
					.get(Consts.KEY_PASSWORD));
			((BaseActivity) context).statusHashMap.put(Consts.LOCAL_LOGIN,
					"true");
			SqlLiteUtil.addUser(user1);
			((BaseActivity) context).dismissDialog();
			String result = JVACCOUNT.GetAccountInfo();
			JSONObject json = null;
			try {
				json = new JSONObject(result);
			} catch (JSONException e) {
				json = null;
			}
			if (null != json && null != json.optString("mail")
					&& !"".equals(json.optString("mail"))) {
				intent.setClass(context, JVBoundEmailActivity.class);
				context.startActivity(intent);
				((BaseActivity) context).finish();
			} else {
				intent.setClass(context, JVTabActivity.class);
				context.startActivity(intent);
				((BaseActivity) context).finish();
			}
			break;
		}
		case JVAccountConst.PASSWORD_ERROR: {
			((BaseActivity) context).dismissDialog();
			((BaseActivity) context).showTextToast(R.string.str_userpass_error);
			if (jump) {
				intent.setClass(context, JVLoginActivity.class);
				context.startActivity(intent);
				((BaseActivity) context).finish();
			}
			break;
		}
		case JVAccountConst.SESSION_NOT_EXSIT: {
			((BaseActivity) context).dismissDialog();
			((BaseActivity) context)
					.showTextToast(R.string.str_session_not_exist);
			if (jump) {
				intent.setClass(context, JVLoginActivity.class);
				context.startActivity(intent);
				((BaseActivity) context).finish();
			}
			break;
		}
		case JVAccountConst.USER_HAS_EXIST: {

			break;
		}
		case JVAccountConst.USER_NOT_EXIST: {
			((BaseActivity) context).dismissDialog();
			((BaseActivity) context).showTextToast(R.string.str_user_not_exist);
			break;
		}
		case JVAccountConst.LOGIN_FAILED_1: {
			((BaseActivity) context).dismissDialog();
			if (-5 == msg.arg2) {
				((BaseActivity) context)
						.showTextToast(R.string.str_error_code_5);
			} else if (-6 == msg.arg2) {
				((BaseActivity) context)
						.showTextToast(R.string.str_error_code_6);
			} else {
				((BaseActivity) context)
						.showTextToast(context.getResources().getString(
								R.string.str_error_code)
								+ (msg.arg2 - 1000));
			}
			if (jump) {
				intent.setClass(context, JVLoginActivity.class);
				context.startActivity(intent);
				((BaseActivity) context).finish();
			}
			break;
		}
		case JVAccountConst.LOGIN_FAILED_2: {
			((BaseActivity) context).dismissDialog();
			((BaseActivity) context).showTextToast(R.string.str_other_error);
			if (jump) {
				intent.setClass(context, JVLoginActivity.class);
				context.startActivity(intent);
				((BaseActivity) context).finish();
			}
			break;
		}
		}
	}
}
