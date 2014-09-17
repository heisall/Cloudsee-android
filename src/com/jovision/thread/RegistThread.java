package com.jovision.thread;

import java.util.HashMap;

import android.content.Context;

import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.commons.JVAccountConst;
import com.jovision.newbean.UserBean;
import com.jovision.utils.AccountUtil;

/**
 * 注册线程
 * 
 * @author suifupeng
 * 
 */
public class RegistThread extends Thread {
	private Context context;
	private HashMap<String, String> statusHashMap = null;

	public RegistThread(Context con) {
		context = con;
		statusHashMap = ((MainApplication) context.getApplicationContext())
				.getStatusHashMap();
	}

	@Override
	public void run() {
		int nameExists = AccountUtil.isUserExsit(statusHashMap
				.get(Consts.KEY_USERNAME));
		if (JVAccountConst.USER_HAS_EXIST == nameExists) {
			((MainApplication) context.getApplicationContext()).onNotify(
					Consts.REGIST_FUNCTION, JVAccountConst.HAVE_REGISTED, 0,
					null);
			return;
		} else if (JVAccountConst.PHONE_NOT_TRUE == nameExists) {
			((MainApplication) context.getApplicationContext()).onNotify(
					Consts.REGIST_FUNCTION, JVAccountConst.HAVE_REGISTED2, 0,
					null);
			return;
		}
		UserBean user = new UserBean();
		user.setUserName(statusHashMap.get(Consts.KEY_USERNAME));
		user.setUserPwd(statusHashMap.get(Consts.KEY_PASSWORD));
		int result = AccountUtil.userRegister(user);
		if (JVAccountConst.SUCCESS == result) {
			((MainApplication) context.getApplicationContext()).onNotify(
					Consts.REGIST_FUNCTION, JVAccountConst.REGIST_SUCCESS, 0,
					null);
		} else {// 注册失败
			((MainApplication) context.getApplicationContext()).onNotify(
					Consts.REGIST_FUNCTION, JVAccountConst.REGIST_FAILED,
					result, null);
		}
		super.run();
	}
}
