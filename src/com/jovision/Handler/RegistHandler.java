package com.jovision.Handler;

import android.R;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jovision.activities.BaseActivity;
import com.jovision.commons.JVAccountConst;
import com.jovision.thread.LoginThread;

/**
 * 注册处理Handler
 * 
 * @author suifupeng
 * 
 */
public class RegistHandler extends Handler {
	private Context context;

	public RegistHandler(Context con) {
		context = con;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		((BaseActivity) context).dismissDialog();
		switch (msg.arg1) {
		case JVAccountConst.REGIST_SUCCESS:// 注册成功
			((BaseActivity) context)
					.showTextToast(R.string.login_str_regist_success);
			LoginThread loginThread = new LoginThread(context);
			loginThread.start();
			break;
		case JVAccountConst.HAVE_REGISTED:// 账号已注册
			((BaseActivity) context).showTextToast(R.string.str_user_has_exist);
			break;
		case JVAccountConst.HAVE_REGISTED2:// 邮箱已注册
			((BaseActivity) context)
					.showTextToast(R.string.str_phone_num_error);
			break;
		case JVAccountConst.REGIST_FAILED:// 注册失败
			if (0 < msg.arg2) {
				if (JVAccountConst.PASSWORD_ERROR == msg.arg2) {
					((BaseActivity) context)
							.showTextToast(R.string.str_user_password_error);
				} else if (JVAccountConst.SESSION_NOT_EXSIT == msg.arg2) {
					((BaseActivity) context)
							.showTextToast(R.string.str_session_not_exist);
				} else if (JVAccountConst.USER_HAS_EXIST == msg.arg2) {
					((BaseActivity) context)
							.showTextToast(R.string.str_user_has_exist);
				} else if (JVAccountConst.USER_NOT_EXIST == msg.arg2) {
					((BaseActivity) context)
							.showTextToast(R.string.str_user_not_exist2);
				} else {
					((BaseActivity) context)
							.showTextToast(R.string.str_other_error);
				}
			} else {
				if (-5 == msg.arg2) {
					((BaseActivity) context)
							.showTextToast(R.string.str_error_code_5);
				} else if (-6 == msg.arg2) {
					((BaseActivity) context)
							.showTextToast(R.string.str_error_code_6);
				} else {
					((BaseActivity) context).showTextToast(context
							.getResources().getString(R.string.str_error_code)
							+ (msg.arg2 - 1000));
				}
			}
			break;
		}
	}
}
