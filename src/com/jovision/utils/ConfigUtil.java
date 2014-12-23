package com.jovision.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.test.JVACCOUNT;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.MainApplication;
import com.jovision.bean.Wifi;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.Url;
import com.jovision.utils.mails.MailSenderInfo;
import com.jovision.utils.mails.MyAuthenticator;

public class ConfigUtil {
	private final static String TAG = "ConfigUtil";
	public final static String ACCOUNT_VERSION = "";
	public final static String PLAY_VERSION = "0.9[b2407bf][2014-12-23]";
	public final static String NETWORK_VERSION = "v2.0.76.3.20[private:v2.0.75.13 201401208.2]";

	public static String GETPLAY_VERSION = "";
	public static String GETNETWORK_VERSION = "";

	private final static String CHINA_JSON = "{\"country\":\"\u4e2d\u56fd\"}";
	// /**
	// * 获取本地数据库管理对象的引用
	// *
	// * @param con 上下文
	// * @return 数据库管理对象的引用
	// */
	// public static JVConfigManager getDbManager(Context con) {
	// JVConfigManager dbManager = new JVConfigManager(con,
	// JVConst.JVCONFIG_DATABASE, null, JVConst.JVCONFIG_DB_VER);
	// return dbManager;
	// }

	public static String version = "";

	public static void getJNIVersion() {
		// remoteVerStr={"jni":"0.8[9246b6f][2014-11-03]","net":"v2.0.76.3.7[private:v2.0.75.13 201401030.2.d]"}
		try {
			String remoteVer = Jni.getVersion();
			JSONObject obj = new JSONObject(remoteVer);
			String playVersion = obj.optString("jni");
			GETPLAY_VERSION = playVersion;
			String netVersion = obj.optString("net");
			GETNETWORK_VERSION = netVersion;
			if (PLAY_VERSION.equalsIgnoreCase(playVersion)
					&& NETWORK_VERSION.equalsIgnoreCase(netVersion)) {
				MyLog.v(TAG, "Same:localVer=" + PLAY_VERSION + "--"
						+ NETWORK_VERSION + ";\nremoteVer=" + remoteVer);
			} else {
				MyLog.e(TAG, "Not-Same:localVer=" + PLAY_VERSION + "--"
						+ NETWORK_VERSION + ";\nremoteVerStr=" + remoteVer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * playversion
	 * 
	 * @return
	 */
	public static String getVersion(Context context) {
		try {
			HashMap<String, String> statusHashMap = ((MainApplication) context
					.getApplicationContext()).getStatusHashMap();
			if ("".equalsIgnoreCase(version)) {
				String verName = "";
				String pkName = context.getPackageName();
				verName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
				if ("true".equalsIgnoreCase(statusHashMap
						.get(Consts.NEUTRAL_VERSION))) {
					version = verName;
				} else {
					version = verName;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return version;
	}

	/**
	 * 获取设备的云视通组
	 * 
	 * @param deviceNum
	 */
	public static String getGroup(String deviceNum) {

		StringBuffer groupSB = new StringBuffer();
		if (!"".equalsIgnoreCase(deviceNum)) {
			for (int i = 0; i < deviceNum.length(); i++) {
				if (Character.isLetter(deviceNum.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
					groupSB = groupSB.append(deviceNum.charAt(i));
				}
			}
		}
		return groupSB.toString();
	}

	/**
	 * 获取设备的云视通组和号码
	 * 
	 * @param deviceNum
	 */
	public static int getYST(String deviceNum) {
		int yst = 0;

		StringBuffer ystSB = new StringBuffer();
		if (!"".equalsIgnoreCase(deviceNum)) {
			for (int i = 0; i < deviceNum.length(); i++) {
				if (Character.isDigit(deviceNum.charAt(i))) {
					ystSB = ystSB.append(deviceNum.charAt(i));
				}
			}
		}

		if ("".equalsIgnoreCase(ystSB.toString())) {
			yst = 0;
		} else {
			yst = Integer.parseInt(ystSB.toString());
		}
		return yst;
	}

	private static String country = "";

	/**
	 * 获取手机IP
	 * 
	 * @return
	 */
	// //
	// http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js
	// URL whatismyip = new URL(Url.COUNTRY_URL);
	// in = new BufferedReader(new InputStreamReader(
	// whatismyip.openStream(), "GBK"));
	// // var remote_ip_info =
	// //
	// {"ret":1,"start":-1,"end":-1,"country":"\u4e2d\u56fd","province":"\u5c71\u4e1c","city":"\u6d4e\u5357","district":"","isp":"","type":"","desc":""};
	// requestRes = in.readLine();
	public static String getCountry() {
		MyLog.v(TAG, "getCountry---E");
		if ("".equalsIgnoreCase(country)) {
			String requestRes = "China";
			BufferedReader in = null;
			try {
				requestRes = JSONUtil.getRequest3(Url.COUNTRY_URL);
				MyLog.v("getCountry--requestRes", requestRes);
				String jsonStr = requestRes.substring(requestRes.indexOf("{"),
						requestRes.indexOf("}") + 1);
				MyLog.v("getCountry--jsonStr", jsonStr);
				JSONObject obj = new JSONObject(jsonStr);
				country = obj.getString("country") + "-"
						+ obj.getString("province") + "-"
						+ obj.getString("city");
			} catch (Exception e) {
				country = "China";
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		MyLog.v("getCountry", country);
		MyLog.v(TAG, "getCountry---X");
		return country;
	}

	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = Base64.encodeToString(b, Base64.NO_WRAP);
		}
		return s;
	}

	public static String encodeStr(String str) {
		// 将Unicode字符串转换为汉字输出
		String s[] = str.split("\\\\u");
		String t = "";
		for (int j = 1; j < s.length; j++) {
			int ab = Integer.valueOf(s[j], 16);// 先将16进制转换为整数
			char ac = (char) ab;// 再将整数转换为字符
			System.out.println(ac);
			t = t + ac;
		}
		return t;
	}

	public static int lan = -1;

	// 中文 1 英文2
	public static int getServerLanguage() {
		String china = "";
		try {
			JSONObject chinaObj = new JSONObject(CHINA_JSON);
			china = chinaObj.getString("country");
			MyLog.v("local-country", china);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (-1 == lan) {
			String country = getCountry();
			MyLog.v("country",
					"flag1-" + country.contains(china) + "-flag2-"
							+ country.contains("China") + "-flag3-"
							+ country.contains("china"));
			if (country.contains(china) || country.contains("China")
					|| country.contains("china")) {
				lan = Consts.LANGUAGE_ZH;
			} else {
				lan = Consts.LANGUAGE_EN;
			}
		}
		MyLog.v("country", "lan=" + lan + ";中文 1 英文2");
		return lan;
	}

	/**
	 * 获取系统语言
	 * 
	 * @return 1:中文 2:英文
	 */
	public static int getLanguage() {
		int lan = Consts.LANGUAGE_ZH;
		String language = Locale.getDefault().getLanguage();
		if (language.equalsIgnoreCase("zh")) {// 中文
			lan = Consts.LANGUAGE_ZH;
		} else {// 英文
			lan = Consts.LANGUAGE_EN;
		}
		return lan;
	}

	/**
	 * 获取系统语言
	 * 
	 * @return true:中文false:英文
	 * @return
	 */
	public static boolean isLanZH() {
		boolean lanFlag = false;
		int lan = Consts.LANGUAGE_ZH;
		String language = Locale.getDefault().getLanguage();
		if (language.equalsIgnoreCase("zh")) {// 中文
			lan = Consts.LANGUAGE_ZH;
			lanFlag = true;
		} else {// 英文
			lan = Consts.LANGUAGE_EN;
			lanFlag = false;
		}
		return lanFlag;
	}

	/**
	 * 唯一的设备ID： 　　* GSM手机的 IMEI 和 CDMA手机的 MEID. 　　* Return null if device ID is
	 * not available. IMEI --International Mobile Equipment Identity
	 * EMID---Mobile Equipment IDentifier 都是用来唯一标示移动终端，MEID主要分配给CDMA制式的手机
	 * 于是你可以参考EMID_百度百科 自我感觉两者有点类似ipv4与ipv6的关系，都是资源枯竭导致的
	 * 
	 * 　　
	 */
	public static String getIMEI(Context context) {
		String IMEI = "";
		if (!"".equalsIgnoreCase(IMEI)) {
			return IMEI;
		} else {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			IMEI = telephonyManager.getDeviceId();
		}
		if (null == IMEI || "".equalsIgnoreCase(IMEI)) {
			StringBuffer deviceId = new StringBuffer();
			// wifi mac地址
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String wifiMac = info.getMacAddress();
			if (!"".equalsIgnoreCase(wifiMac)) {
				deviceId.append(wifiMac);
			}
			IMEI = deviceId.toString();
		}

		MyLog.v("IMEI", IMEI);
		return IMEI;
	}

	/**
	 * 自定义加载progressDialog
	 * 
	 * @author suifupeng
	 * @param context
	 * @param msg
	 *            为null时不显示文字
	 * @param cancelable
	 *            是否可以取消
	 */
	public static Dialog createLoadingDialog(Context context, String msg,
			boolean cancelable) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		if (null != msg) {
			tipTextView.setVisibility(View.VISIBLE);
			tipTextView.setText(msg);// 设置加载信息
		} else {
			tipTextView.setVisibility(View.GONE);
		}

		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		loadingDialog.setCancelable(cancelable);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return loadingDialog;
	}

	/**
	 * 初始化sdk
	 * 
	 * @param context
	 *            上下文
	 * @return 初始化sdk是否成功
	 */
	public static boolean initAccountSDK(Context context) {
		boolean result = false;
		HashMap<String, String> statusHashMap = ((MainApplication) context
				.getApplicationContext()).getStatusHashMap();
		if ("false".equals(statusHashMap.get(Consts.KEY_INIT_ACCOUNT_SDK))) {
			int init_try_count = 5;
			do {
				result = JVACCOUNT.InitSDK(context, Consts.ACCOUNT_PATH);
				if (!result) {
					init_try_count--;
					if (init_try_count == 0) {
						break;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} while (!result);
			statusHashMap.put(Consts.KEY_INIT_ACCOUNT_SDK,
					String.valueOf(result));
			MyLog.e(TAG, "1>>>>>初始化账号--sdk--res=" + String.valueOf(result));
			if (!result) {
				Toast.makeText(context, "初始化账号SDK失败，请重新运行程序", Toast.LENGTH_LONG)
						.show();
				return result;
			}
			MyLog.i("AccoutSDK", "Version:" + JVACCOUNT.GetVersion(0));
			String channelIp = "";
			String onlineIp = "";
			if (Consts.LANGUAGE_ZH == ConfigUtil.getServerLanguage()) {
				channelIp = MySharedPreference.getString("ChannelIP");
				onlineIp = MySharedPreference.getString("OnlineIP");
			} else {
				channelIp = MySharedPreference.getString("ChannelIP_en");
				onlineIp = MySharedPreference.getString("OnlineIP_en");
			}
			if (null != channelIp && !"".equals(channelIp) && null != onlineIp
					&& !"".equals(onlineIp)) {
				JVACCOUNT.SetServerIP(channelIp, onlineIp);
			} else {
				if (getNetWorkConnection(context)) {

					MyLog.v("initAccountSDK", Url.SHORTSERVERIP + "--"
							+ Url.LONGSERVERIP);
					JVACCOUNT.ConfigServerAddress(Url.SHORTSERVERIP,
							Url.LONGSERVERIP);
					// String ip = JVACCOUNT.GetServerIP();
					// if(ip.length() < 5){//无ip
					// JVACCOUNT.ConfigServerAddress(Url.SHORTSERVERIP,
					// Url.LONGSERVERIP);
					// }
				}
			}

			// statusHashMap.put(Consts.KEY_INIT_ACCOUNT_SDK,
			// String.valueOf(result));

			MyLog.v(TAG, "初始化账号--sdk--res=" + String.valueOf(result));
		}
		return result;
	}

	/**
	 * 开启广播
	 */
	public static int openBroadCast() {
		int res = -1;
		if (MySharedPreference.getBoolean("BROADCASTSHOW", true)) {
			MyLog.v(Consts.TAG_APP, "enable  broad = " + true);
			res = Jni.searchLanServer(9400, 6666);
		} else {
			MyLog.v(Consts.TAG_APP, "unEnable  broad = " + false);
		}
		return res;
	}

	/**
	 * 停止广播
	 */
	public static void stopBroadCast() {
		if (MySharedPreference.getBoolean("BROADCASTSHOW", true)) {
			Jni.stopSearchLanServer();
		}
	}

	/**
	 * 初始化sdk
	 * 
	 * @param context
	 *            上下文
	 * @return 初始化sdk是否成功
	 */
	public static boolean initCloudSDK(Context context) {
		boolean result = false;
		HashMap<String, String> statusHashMap = ((MainApplication) context
				.getApplicationContext()).getStatusHashMap();
		if ("false".equals(statusHashMap.get(Consts.KEY_INIT_CLOUD_SDK))) {
			result = Jni.init(context, 9200, Consts.LOG_PATH);
			Jni.enableLog(true);
			Jni.setThumb(320, 90);
			Jni.setStat(true);
			if (MySharedPreference.getBoolean("LITTLEHELP", true)) {
				Jni.enableLinkHelper(true, 3, 10);// 开小助手
				MyLog.v(Consts.TAG_APP, "enable  helper = " + true);
			} else {
				MyLog.v(Consts.TAG_APP, "unEnable  helper = " + false);
			}

			int res = openBroadCast();// 开广播

			statusHashMap
					.put(Consts.KEY_INIT_CLOUD_SDK, String.valueOf(result));

			MyLog.v(Consts.TAG_APP, "init sdk = " + String.valueOf(result));
		} else {
			MyLog.e(Consts.TAG_APP, "no need to init sdk");
		}
		return result;
	}

	private static boolean flag = true;

	/**
	 * 判断网络是否真正可用
	 * 
	 * @return 是否可用
	 */
	public static boolean getNetWorkConnection(Context context) {
		flag = false;

		ConnectivityManager cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			int type = info.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
			} else {
			}
			flag = true;
		} else {
			flag = false;
		}

		// new Thread() {
		// public void run() {
		// String serverURL = "";
		// if (isLanZH()) {
		// serverURL = "http://www.baidu.com/";
		// } else {
		// serverURL = "http://www.google.com/";
		// }
		// HttpGet httpRequest = new HttpGet(serverURL);// 建立http get联机
		// // HttpResponse httpResponse;
		// try {
		// new DefaultHttpClient().execute(httpRequest);
		// flag = true;
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// };
		// }.start();
		// for (int i = 0; i < 3; i++) {
		// if (!flag) {
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		return flag;
	}

	/**
	 * 判断当前 手机是否联网
	 * 
	 * @param context
	 *            Context上下文对象
	 */
	public static boolean isConnected(Context context) {
		ConnectivityManager cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			int type = info.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
			} else {
			}
			return true;
		} else
			return false;
	}

	/**
	 * 是否是3G网络环境
	 * 
	 * @param context
	 *            上下文
	 * @param alert
	 *            是否弹出提示
	 * @return 是否是3G网络
	 */
	public static boolean is3G(Context context, boolean alert) {
		ConnectivityManager cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			int type = info.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				return false;
			} else {
				if (alert) {
					Toast.makeText(context, R.string.tips_3g, Toast.LENGTH_LONG)
							.show();
				}
				return true;
			}
		} else
			return false;
	}

	/**
	 * 注销方法
	 */
	public static void logOut() {
		// Jni.stopSearchLanServer();
		// Jni.enableLinkHelper(false, 3, 0);
		// Jni.deinitAudioEncoder();
		// Jni.deinit();
	}

	/**
	 * 验证云通号
	 * 
	 * @param ystEdit
	 * @return
	 */
	public static boolean checkYSTNum(String ystEdit) {
		boolean flag = true;

		try {
			int kkk;
			for (kkk = 0; kkk < ystEdit.length(); kkk++) {
				char c = ystEdit.charAt(kkk);
				if (c <= '9' && c >= '0') {
					break;
				}
			}
			String group = ystEdit.substring(0, kkk);
			String yst = ystEdit.substring(kkk);
			for (int mm = 0; mm < group.length(); mm++) {
				char c = ystEdit.charAt(mm);
				if (mm == 0
						&& ((c == 'A' || c == 'a' || c == 'B' || c == 'b'
								|| c == 'S' || c == 's'))) {

				} else {
					flag = false;
				}
			}

			for (int i = 0; i < yst.length(); i++) {
				char c = yst.charAt(i);
				if ((c >= '0' && c <= '9')) {

				} else {
					flag = false;
				}
			}
			int ystValue = "".equals(yst) ? 0 : Integer.parseInt(yst);
			if (kkk >= 4 || kkk <= 0 || ystValue <= 0) {
				flag = false;
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}

		return flag;

	}

	/**
	 * 检查ip地址格式是否正确
	 * 
	 * @author suifupeng
	 * @param ipAdress
	 * @return
	 */
	public static boolean checkIPAdress(String ipAddress) {
		// if (ipAddress
		// .matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$"))
		// {
		// return true;
		// } else {
		// return false;
		// }
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$");
		Matcher matcher = pattern.matcher(ipAddress);
		if (matcher.find()) {
			return true;
		} else {
			if (ipAddress
					.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$")) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 检查端口号格式是否正确
	 * 
	 * @author suifupeng
	 * @param portNum
	 * @return
	 */
	public static boolean checkPortNum(String portNum) {
		if (portNum.length() < 1 || portNum.length() > 5) {
			return false;
		}
		for (int i = 0; i < portNum.length(); i++) {
			char c = portNum.charAt(i);
			if (c > '9' || c < '0') {
				return false;
			}
		}
		if (Integer.valueOf(portNum).intValue() <= 0
				&& Integer.valueOf(portNum).intValue() > 65535) {
			return false;
		}
		return true;
	}

	// 验证昵称
	public static boolean checkNickName(String str) {
		boolean flag = false;
		try {
			byte[] b = str.getBytes("UTF-8");
			str = new String(b, "UTF-8");
			Pattern pattern = Pattern
					.compile("^[A-Za-z0-9_.()\\+\\-\\u4e00-\\u9fa5]{1,20}$");
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches() && 20 >= str.getBytes().length) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (UnsupportedEncodingException e) {
			flag = false;
		}
		return flag;
	}

	// 验证设备用户名
	public static boolean checkDeviceUsername(String str) {
		boolean flag = false;
		try {
			byte[] b = str.getBytes("UTF-8");
			str = new String(b, "UTF-8");
			Pattern pattern = Pattern.compile("^[A-Za-z0-9_\\-]{1,16}$");
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (UnsupportedEncodingException e) {
			flag = false;
		}
		// if(0 < str.length() && 16 > str.length()){
		// flag = true;
		// }

		return flag;

	}

	// 验证设备密码
	public static boolean checkDevicePwd(String str) {
		boolean flag = false;
		// try {
		// byte[] b = str.getBytes("UTF-8");
		// str = new String(b, "UTF-8");
		// Pattern pattern = Pattern.compile("^[A-Za-z0-9_\\-]{0,15}$");
		// Matcher matcher = pattern.matcher(str);
		// if (matcher.matches()) {
		// flag = true;
		// } else {
		// flag = false;
		// }
		// } catch (UnsupportedEncodingException e) {
		// flag = false;
		// }
		if (0 <= str.length() && 16 > str.length()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 发送邮件给多个接收者
	 * 
	 * @param mailInfo
	 *            带发送邮件的信息
	 * @return
	 */
	public static boolean sendMailtoMultiReceiver(MailSenderInfo mailInfo) {
		MyAuthenticator authenticator = null;
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		Session sendMailSession = Session.getInstance(mailInfo.getProperties(),
				authenticator);
		try {
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address[] tos = null;
			String[] receivers = mailInfo.getReceivers();
			if (receivers != null) {
				// 为每个邮件接收者创建一个地址
				tos = new InternetAddress[receivers.length + 1];
				tos[0] = new InternetAddress(mailInfo.getToAddress());
				for (int i = 0; i < receivers.length; i++) {
					tos[i + 1] = new InternetAddress(receivers[i]);
				}
			} else {
				tos = new InternetAddress[1];
				tos[0] = new InternetAddress(mailInfo.getToAddress());
			}
			// 将所有接收者地址都添加到邮件接收者属性中
			mailMessage.setRecipients(Message.RecipientType.TO, tos);

			mailMessage.setSubject(mailInfo.getSubject());
			mailMessage.setSentDate(new Date());
			// 设置邮件内容
			Multipart mainPart = new MimeMultipart();
			BodyPart html = new MimeBodyPart();
			html.setContent(mailInfo.getContent(), "text/html; charset=GBK");
			mainPart.addBodyPart(html);
			mailMessage.setContent(mainPart);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	// 获取当前系统时间
	public static String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("MM月 dd日  ahh:mm");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String time = formatter.format(curDate);
		return time;
	}

	// 获取当前系统时间
	public static String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String time = formatter.format(curDate);
		return time;
	}

	/**
	 * 特定 json 转 HashMap
	 * 
	 * @param json
	 * @param keyOfMsg
	 *            消息的键名
	 * @return
	 */
	public static HashMap<String, String> genMsgMap(String msg) {
		HashMap<String, String> map = new HashMap<String, String>();

		if (null == msg || "".equalsIgnoreCase(msg)) {
			return null;
		}
		Matcher matcher = Pattern.compile("([^=;]+)=([^=;]+)").matcher(msg);
		while (matcher.find()) {
			map.put(matcher.group(1), matcher.group(2));
		}
		return map;
	}

	/**
	 * 特定 json 转 HashMap 不会覆盖
	 * 
	 * @param json
	 * @param keyOfMsg
	 *            消息的键名
	 * @return
	 */
	public static HashMap<String, String> genMsgMap1(String msg) {
		HashMap<String, String> map = new HashMap<String, String>();

		if (null == msg || "".equalsIgnoreCase(msg)) {
			return null;
		}
		Matcher matcher = Pattern.compile("([^=;]+)=([^=;]+)").matcher(msg);
		while (matcher.find()) {
			if (null != map.get(matcher.group(1))
					&& !"".equalsIgnoreCase(matcher.group(1))) {

			} else {
				map.put(matcher.group(1), matcher.group(2));
			}

		}
		return map;
	}

	// public static HashMap<String, String> getCH1(String key,String msg){
	// HashMap<String, String> map = new HashMap<String, String>();
	// String ch1Str = "";
	// String[] array = msg.split("CH1");
	// int length = array.length;
	// MyLog.v("splitStr", msg);
	// String splitStr = "";
	// for(int i = 0 ; i < length ; i++){
	// splitStr = array[i];
	// if(splitStr.contains(";[")){
	// splitStr = array[i].subSequence(0,
	// array[i].lastIndexOf(";[")).toString();
	// }
	// MyLog.v("splitStr", splitStr);
	// if(splitStr.contains("width")
	// && array[i].contains("height")
	// && array[i].contains("framerate")
	// && array[i].contains("nMBPH")){
	// break;
	// }
	// }
	//
	// map = genMsgMap(splitStr);
	//
	// return map;
	// }
	//
	/**
	 * JSON 数组转成对象列表
	 * 
	 * @param msg
	 * @return
	 */
	public static ArrayList<Wifi> genWifiList(String msg) {
		ArrayList<Wifi> wifiList = new ArrayList<Wifi>();
		JSONArray array;
		try {
			array = new JSONArray(msg);
			if (null != array && 0 != array.length()) {
				int length = array.length();

				for (int i = 0; i < length; i++) {
					Wifi wifi = new Wifi();
					JSONObject obj = (JSONObject) array.get(i);
					wifi.wifiAuth = obj.getString("auth");
					wifi.wifiEnc = obj.getString("enc");
					wifi.wifiKeyStat = obj.getInt("keystat");
					wifi.wifiUserName = obj.getString("name");
					wifi.wifiPassWord = obj.getString("pwd");
					wifi.wifiQuality = obj.getInt("quality");
					wifiList.add(wifi);
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return wifiList;
	}

	public static String getIpAddress(String host) {
		String IPAddress = "";
		InetAddress ReturnStr1 = null;
		try {
			ReturnStr1 = java.net.InetAddress.getByName(host);
			IPAddress = ReturnStr1.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		MyLog.v(TAG, "解析域名=" + host + ";IP=" + IPAddress);
		return IPAddress;
	}

	// 调用第三方分享功能
	public static void shareTo(Context context, String imagePath) {
		if (null == imagePath || "".equalsIgnoreCase(imagePath)) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送到属性
		// 纯文本
		// intent.setType("text/plain");
		// 图片分享
		intent.setType("image/jpg");
		File f = new File(imagePath);
		// 文件不存在return
		if (!f.exists()) {
			return;
		}
		Uri uri = Uri.fromFile(f);
		// Intent.createChooser(intentItem, "分享")
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享的主题"); // 分享的主题
		intent.putExtra(Intent.EXTRA_TEXT, "分享的内容 "); // 分享的内容
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 允许intent启动新的activity
		context.startActivity(Intent.createChooser(intent, "分享")); // //目标应用选择对话框的标题
	}

	public static String sendPostRequest(String url, List<NameValuePair> params) {
		String result = "";
		/* 建立HTTP Post连线 */
		HttpPost httpRequest = new HttpPost(url);
		// Post运作传送变数必须用NameValuePair[]阵列储存
		// 传参数 服务端获取的方法为request.getParameter("name")
		try {
			// 发出HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 取得HTTP response
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			// 若状态码为200 ok
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 取出回应字串
				result = EntityUtils.toString(httpResponse.getEntity());
			} else {
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static int getInt(JSONObject object, String key) {
		int value = 0;
		if (!object.isNull(key)) {
			try {
				value = object.getInt(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	public static String getString(JSONObject object, String key) {
		String value = "";
		if (!object.isNull(key)) {
			try {
				value = object.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	public static boolean getBoolean(JSONObject object, String key) {
		boolean value = false;
		if (!object.isNull(key)) {
			try {
				value = object.getBoolean(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * 删除设备场景图
	 * 
	 * @param devName
	 * @return
	 */
	public static boolean deleteSceneFile(String devName) {
		boolean deleteRes = false;
		String savePath = Consts.SCENE_PATH;
		MobileUtil.createDirectory(new File(savePath));
		String fullPath = savePath + devName + Consts.IMAGE_PNG_KIND;
		File devImgFile = new File(fullPath);
		BitmapCache.getInstance().bitmapRefs.remove(fullPath);

		if (devImgFile.exists()) {
			devImgFile.delete();
			deleteRes = true;
		}
		return deleteRes;
	}

	// /**
	// * 获取当前ip地址
	// *
	// * @param context
	// * @return
	// */
	// public static String getLocalIpAddress(Context context) {
	// String ip = "";
	// try {
	// for (Enumeration<NetworkInterface> en = NetworkInterface
	// .getNetworkInterfaces(); en.hasMoreElements();) {
	// NetworkInterface intf = en.nextElement();
	// for (Enumeration<InetAddress> enumIpAddr = intf
	// .getInetAddresses(); enumIpAddr.hasMoreElements();) {
	// InetAddress inetAddress = enumIpAddr.nextElement();
	// if (!inetAddress.isLoopbackAddress()) {
	// ip = inetAddress.getHostAddress().toString();
	// }
	// }
	// }
	// } catch (Exception ex) {
	// ip = "";
	// ex.printStackTrace();
	// }
	// MyLog.v("LocalIpAddress", ip+"");
	// return ip;
	// }

}
