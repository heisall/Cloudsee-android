package com.jovision.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.test.JVACCOUNT;
import android.util.Log;

import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.bean.ClientBean;
import com.jovision.bean.User;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVAlarmConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;

public class AccountUtil {

	/********************************************** 接口 ***************************************************************/

	/**
	 * 判断用户是否存在 2:存在 3:不存在 -15:手机号格式不正确
	 * 
	 * @param userName用户名
	 * @return
	 */
	public static int isUserExsit(String userName) {
		int res = -1;
		res = JVACCOUNT.IsUserExists(userName);
		MyLog.v("isUserExsit--", userName + "-----|||||" + res + "");
		return res;
	}

	/**
	 * 用户注册
	 * 
	 * @param user
	 *            用户基本信息（目前只包括用户名和邮箱），用户密码（由上层直接传入加密的密码）
	 * @return
	 */
	public static int userRegister(User user) {
		int res = -1;
		res = JVACCOUNT.UserRegister(user.getUserName(), user.getUserPwd(),
				Consts.APP_NAME);
		MyLog.v("userRegister--", "-----|||||" + res + "");

		return res;
	}

	public static String onLoginProcess(Context mContext, String userName,
			String pwd, String urlLgServ, String urlStServ) {
		int res = -1;
		JSONObject mainObj = new JSONObject();

		JSONObject reqObj = new JSONObject();
		try {
			mainObj.put("id", 1);
			reqObj.put("user", userName);
			reqObj.put("passwd", pwd);
			reqObj.put("plattype", 1);
			if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(mContext)) {
				reqObj.put("locales", Consts.LANGUAGE_ZH);
			} else {
				reqObj.put("locales", Consts.LANGUAGE_EN);
			}

			reqObj.put("devuuid", ConfigUtil.getIMEI(mContext));
			reqObj.put("longservurl", urlLgServ);
			reqObj.put("shortservurl", urlStServ);

			mainObj.put("data", reqObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String strRes = JVACCOUNT.onLoginProcess(0, mainObj.toString());

		JSONObject respObj = null;
		try {
			respObj = new JSONObject(strRes);

			// 登陆成功，汇报报警状态
			int loginRes1 = respObj.optInt("arg1", 1);
			if (JVAccountConst.LOGIN_SUCCESS == loginRes1) {

				if (MySharedPreference
						.getBoolean(Consts.MORE_ALARMSWITCH, true)) {
					JVACCOUNT.SetCurrentAlarmFlag(JVAlarmConst.ALARM_ON,
							ConfigUtil.getIMEI(mContext));
				} else {
					JVACCOUNT.SetCurrentAlarmFlag(JVAlarmConst.ALARM_OFF,
							ConfigUtil.getIMEI(mContext));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.e("liukp", "resp_result:" + strRes);
		res = respObj.optInt("result", 100);
		return strRes;
	}

	public static String onLoginProcessV2(Context mContext, String userName,
			String pwd, String urlLgServ, String urlStServ, int startHeartBeat) {// 1:起心跳
																					// 0：不需要起心跳

		HashMap<String, String> statusHashMap = ((MainApplication) mContext
				.getApplicationContext()).getStatusHashMap();
		if ("false".equals(statusHashMap.get(Consts.KEY_INIT_ACCOUNT_SDK))) {
			// Toast.makeText(mContext, "初始化账号SDK失败，请重新运行程序", Toast.LENGTH_LONG)
			// .show();
			// return "";
			MyLog.e("Login", "初始化账号SDK失败");
		}
		JSONObject mainObj = new JSONObject();
		JSONObject reqObj = new JSONObject();
		try {
			mainObj.put("id", 1);
			reqObj.put("user", userName);
			reqObj.put("passwd", pwd);
			reqObj.put("plattype", 1);
			reqObj.put("producttype", Consts.PRODUCT_TYPE); // 0-CloudSEE
															// 1-NVSIP 2-HITVIS
															// 3-TONGFANG
			reqObj.put("heartbeat", startHeartBeat);// 是否需要起心跳

			reqObj.put("locales", ConfigUtil.getLanguage2(mContext) - 1);

			// if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(mContext)) {
			// reqObj.put("locales", Consts.LANGUAGE_ZH);
			// } else {
			// reqObj.put("locales", Consts.LANGUAGE_EN);
			// }

			reqObj.put("devuuid",
					MySharedPreference.getString(Consts.KEY_DEV_TOKEN));
			boolean alarmSwitch = MySharedPreference.getBoolean(
					Consts.MORE_ALARMSWITCH, true);
			reqObj.put("alarmflag", alarmSwitch ? 0 : 1);
			User dstUser = UserUtil.getUserByName(userName);
			if (dstUser == null) {
				// 新用户，没有缓存
				reqObj.put("cacheuser", 0);
			} else {
				reqObj.put("cacheuser", dstUser.getJudgeFlag());
			}
			reqObj.put("longservurl", urlLgServ);
			reqObj.put("shortservurl", urlStServ);

			mainObj.put("data", reqObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String strRes = JVACCOUNT.onLoginProcessV2(0, mainObj.toString());

		// JSONObject respObj = null;
		// try {
		// respObj = new JSONObject(strRes);

		// // 登陆成功，汇报报警状态
		// int loginRes1 = respObj.optInt("arg1", 1);
		// if (JVAccountConst.LOGIN_SUCCESS == loginRes1) {
		//
		// if (MySharedPreference.getBoolean(Consts.MORE_ALARMSWITCH)) {
		// JVACCOUNT.SetCurrentAlarmFlag(JVAlarmConst.ALARM_ON,
		// ConfigUtil.getIMEI(mContext));
		// } else {
		// JVACCOUNT.SetCurrentAlarmFlag(JVAlarmConst.ALARM_OFF,
		// ConfigUtil.getIMEI(mContext));
		// }
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		Log.e("onLoginProcessV2", "resp_result:" + strRes);
		// res = respObj.optInt("result", 100);
		return strRes;
	}

	/**
	 * 判断用户名密码是否符合规则
	 * 
	 * @param userName用户名
	 * @param pwd密码
	 * @param deviceType客户端相关信息
	 * @return 0:成功 其他失败
	 */
	// public static int judgeUser(String userName, String pwd, Context con) {
	// int res = -1;
	// res = JVACCOUNT.JudgeUserPasswordStrength(userName);
	// if (3 == res) {
	// return res;
	// } else if (118 == res) {
	// res = JVACCOUNT.OldUserLogin(userName, pwd);
	// return res;
	// } else if (119 == res) {
	// return userLogin(userName, pwd, con);
	// } else {
	// return res;
	// }
	// }

	/**
	 * 用户登录
	 * 
	 * @param userName用户名
	 * @param pwd密码
	 * @param deviceType客户端相关信息
	 * @return 0:成功 其他失败
	 */
	// public static int userLogin(String userName, String pwd, Context con) {
	// int res = -1;
	// res = JVACCOUNT.UserLogin(userName, pwd);
	// MyLog.v("userLogin--", "-----||||||" + res + "");
	// // 汇报设备类型
	// if (0 == res) {
	// res = reportClientPlatformInfo(con);
	// }
	//
	// boolean alarmSwitch =
	// MySharedPreference.getBoolean(Consts.MORE_ALARMSWITCH,
	// true);
	//
	// if (0 == res) {
	// JVACCOUNT.SetCurrentAlarmFlag(alarmSwitch ? 0 : 1,
	// ConfigUtil.getIMEI(con));
	// }
	//
	// if (0 == res) {
	// JVACCOUNT.RegisterServerPushFunc();
	// }
	//
	// if (res == 0) {// 登陆成功必须调online
	// userOnline();
	// }
	// return res;
	// }

	/**
	 * 用户登录 汇报设备类型
	 * 
	 * @param userName用户名
	 * @param pwd密码
	 * @param deviceType客户端相关信息
	 * @return 0:成功 其他失败
	 */
	public static int reportClientPlatformInfo(Context con) {
		int res = -1;

		ClientBean cb = new ClientBean();
		cb.setPlatformType(1);
		if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(con)) {
			cb.setLanguageType(Consts.LANGUAGE_ZH - 1);
		} else {
			cb.setLanguageType(Consts.LANGUAGE_EN - 1);
		}
		cb.setDeviceUUID(MySharedPreference.getString(Consts.KEY_DEV_TOKEN));
		boolean alarmSwitch = MySharedPreference.getBoolean(
				Consts.MORE_ALARMSWITCH, true);
		cb.setAlarmFlag(alarmSwitch ? 0 : 1);
		res = JVACCOUNT.ReportClientPlatformInfo(cb);

		return res;
	}

	/**
	 * 用户上线
	 * 
	 * @return 0 成功 其他失败
	 */
	public static int userOnline() {
		int res = -1;
		res = JVACCOUNT.Online();
		MyLog.v("Online--", "-----||||||" + res + "");
		return res;
	}

	/**
	 * 修改用户密码
	 * 
	 * @param oldPwd旧密码
	 * @param newPwd新密码
	 * @return
	 */
	public static int modifyUserPassword(String oldPwd, String newPwd) {
		int res = -1;
		res = JVACCOUNT.ModifyUserPassword(oldPwd, newPwd);
		MyLog.v("modifyUserPassword--", "-----||||||" + res + "");
		return res;
	}

	/**
	 * 退出程序
	 * 
	 * @param context
	 *            上下文
	 */
	public static void signOut(Context context) {

		// SharedPreferences sharedPreferences = context.getSharedPreferences(
		// "JVCONFIG", Context.MODE_PRIVATE);
		// Editor editor = sharedPreferences.edit();// 获取编辑器
		// // 将设备列表信息缓存到本地
		// BaseApp.saveDeviceToLocal(sharedPreferences, editor,
		// BaseApp.LOCAL_LOGIN_FLAG);
		// // 清除缓存
		// AsyncImageLoader.getInstance().imageCache.clear();
		// // 销毁保持在线服务
		// Intent serviceIntent = new Intent(context.getResources().getString(
		// R.string.str_offline_class_name));
		// context.stopService(serviceIntent);
		// if (null != BaseApp.pushList) {
		// BaseApp.pushList.clear();
		// }
		//
		// // 关掉通知栏
		// if (null != BaseApp.mNotificationManager) {
		// BaseApp.mNotificationManager.cancel(0);
		// }
		// BaseApp.isInitSdk = false;
		// BaseApp.isInitPushSdk = false;
		// BaseApp.isACCOUNTSdk = false;
		// MyLog.e("注销6", "卡在销毁activity");
		// if (!BaseApp.LOCAL_LOGIN_FLAG) {
		// MyLog.e("注销7", "开始--威哥注销方法");
		// int res = userLogout();
		// MyLog.e("注销8", "结束--威哥注销方法" + res);
		// }
		// BaseApp.LOCAL_LOGIN_FLAG = false;
	}

	/**
	 * 注销用户
	 * 
	 * @return
	 */
	public static int userLogout() {
		int res = -1;
		res = JVACCOUNT.UserLogout();
		MyLog.v("userLogout--", "-----||||||" + res + "");
		return res;
	}

	/********************************************* 本地方法 ************************************************************/

	/**
	 * 正则验证用户注册用户名
	 * 
	 * @param userName
	 *            用户名
	 * @return int 0：用户名是邮箱 1：用户名是手机号 2：用户名是用户名 -1：用户名长度不合法 -2：邮箱格式不正确 -3:用户名全数字
	 *         -4：用户名不符合规则
	 */
	public static int VerifyUserName(Context context, String userName) {
		if (null == userName) {
			return -1;
		}
		// int res = JVAccountConst.VALIDATIONUSERNAMETYPE_S;// 默认成功
		// Pattern pattern = Pattern.compile("^.{4,18}$"); //
		// 输入任意字符，但是必须要在（4～18）位之间
		// Pattern pattern1 = Pattern.compile("^([+-]?)\\d*\\.?\\d+$");// 全是数字
		// Pattern pattern2 = Pattern.compile("^[A-Za-z0-9_\\-]+$");//
		// 用户名只能由英文、数字及“_”、“-”组成；
		//
		// Matcher lengthError = pattern.matcher(userName);
		// Matcher lengthError1 = pattern1.matcher(userName);
		// Matcher lengthError2 = pattern2.matcher(userName);
		// if (!lengthError.matches()) {// 长度不在范围内
		// res = JVAccountConst.VALIDATIONUSERNAMETYPE_LENGTH_E;
		// } else if (lengthError1.matches()) {// 全是数字
		// res = JVAccountConst.VALIDATIONUSERNAMETYPE_NUMBER_E;
		// } else if (!lengthError2.matches()) {// 含有非法字符
		// res = JVAccountConst.VALIDATIONUSERNAMETYPE_OTHER_E;
		// } else {
		//
		// }
		int res = -1;
		Pattern pattern = Pattern
				.compile("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
		Pattern pattern1 = Pattern.compile("^[1][3578]\\d{9}$");
		Pattern pattern2 = Pattern.compile("^([+-]?)\\d*\\.?\\d+$");// 全是数字
		Pattern pattern3 = Pattern.compile("^[A-Za-z0-9_\\-]+$");// 用户名只能由英文、数字及"."“_”、“-”组成；
		Matcher lengthError = pattern.matcher(userName);
		Matcher lengthError1 = pattern1.matcher(userName);
		Matcher lengthError2 = pattern2.matcher(userName);
		Matcher lengthError3 = pattern3.matcher(userName);
		if (userName.length() < 4 || userName.length() > 28) {
			res = -1;
		} else if (lengthError1.matches()) {
			if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(context)) {// 中文
				res = 1;
			}
		} else if (userName.contains("@")) {
			if (lengthError.matches()) {
				res = 0;
			} else {
				res = -2;
			}
		} else {
			if (lengthError2.matches()) {
				res = -3;
			} else if (!lengthError3.matches()) {
				res = -4;
			} else {
				res = 2;
			}
		}
		MyLog.v("验证用户名--res：", res + "");
		return res;
	}

	/**
	 * 正则验证用户注册密码
	 * 
	 * @param pass
	 *            密码
	 * @return
	 */
	public static boolean verifyPass(String pass) {

		boolean flag = false;
		// Pattern pattern = Pattern.compile("^[A-Za-z0-9_\\-]{6,20}$"); //
		// 输入任意字符，但是必须要在（6～20）位之间
		//
		// Matcher isPass = pattern.matcher(pass);
		// if (isPass.matches()) {
		// flag = true;
		// } else {
		// flag = false;
		// }
		// MyLog.e("验证密码：", flag + "");
		if (pass.length() >= 6 && pass.length() <= 20) {
			flag = true;
		}

		return flag;
	}

	/**
	 * 正则验证邮箱格式
	 * 
	 * @param strEmail
	 *            邮箱
	 * @return
	 */
	public static boolean verifyEmail(String strEmail) {
		boolean res = false;
		String strPattern = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$"; // 输入任意字符，但是必须要在（4～20）位之间

		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		res = m.matches();
		if (res) {
			if (strEmail.length() <= 28) {
				res = true;
			} else {
				res = false;
			}
		}
		return res;
	}

	/******************************************************* 工具方法 ******************************************************/
	/**
	 * 不能全是相同的数字或者字母（如：000000、111111、aaaaaa） 全部相同返回true
	 * 
	 * @param numOrStr
	 *            用户名
	 * @return
	 */
	public static boolean equalStr(String numOrStr) {
		boolean flag = true;
		char str = numOrStr.charAt(0);
		for (int i = 0; i < numOrStr.length(); i++) {
			if (str != numOrStr.charAt(i)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	/**
	 * 不能是连续的数字--递增（如：123456、12345678）连续数字返回true
	 * 
	 * @param numOrStr
	 *            用户名
	 * @return
	 */
	public static boolean isOrderNumeric(String numOrStr) {
		boolean flag = true;// 如果全是连续数字返回true
		boolean isNumeric = true;// 如果全是数字返回true
		for (int i = 0; i < numOrStr.length(); i++) {
			if (!Character.isDigit(numOrStr.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {// 如果全是数字则执行是否连续数字判断
			for (int i = 0; i < numOrStr.length(); i++) {
				if (i > 0) {// 判断如123456
					int num = Integer.parseInt(numOrStr.charAt(i) + "");
					int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") + 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 不能是连续的字符--递增（如：abcdef）连续字符返回true
	 * 
	 * @param numOrStr
	 *            用户名
	 * @return
	 */
	public static boolean isOrderChar(String numOrStr) {
		boolean flag = true;// 如果全是连续字符返回true
		for (int i = 0; i < numOrStr.length(); i++) {
			if (i > 0) {// 判断如abcdef
				int num = (int) numOrStr.charAt(i);
				int num_ = (int) numOrStr.charAt(i - 1) + 1;
				if (num != num_) {
					flag = false;
					break;
				}
			}
		}

		return flag;
	}

	/**
	 * 不能是连续的数字--递减（如：987654、876543）连续数字返回true
	 * 
	 * @param numOrStr
	 *            用户名
	 * @return
	 */
	public static boolean isOrderNumeric_(String numOrStr) {
		boolean flag = true;// 如果全是连续数字返回true
		boolean isNumeric = true;// 如果全是数字返回true
		for (int i = 0; i < numOrStr.length(); i++) {
			if (!Character.isDigit(numOrStr.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {// 如果全是数字则执行是否连续数字判断
			for (int i = 0; i < numOrStr.length(); i++) {
				if (i > 0) {// 判断如654321
					int num = Integer.parseInt(numOrStr.charAt(i) + "");
					int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") - 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;

	}

	/**
	 * 注册完绑定邮箱手机
	 * 
	 * @param name
	 *            用户名
	 * @param email
	 *            邮箱
	 * @return
	 */
	public static int bindMailOrPhone(String email) {
		int res = -1;
		res = JVACCOUNT.BindMailOrPhone(email);
		MyLog.v("bindMailOrPhone--", "-----|||||" + res + "");
		return res;
	}
}
