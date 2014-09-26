package com.jovision.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.test.JVACCOUNT;
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
import com.jovision.commons.JVConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.Url;
import com.jovision.utils.mails.MailSenderInfo;
import com.jovision.utils.mails.MyAuthenticator;

public class ConfigUtil {
	private final static String TAG = "ConfigUtil";
	private final static String JNI_VERSION = "{\"jni\":\"0.5[5377484][2014-08-19]\",\"net\":\"v2.0.76.3.3[private:v2.0.75.13 20140709.1]\"}";

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

	public static void getJNIVersion() {
		String remoteVer = Jni.getVersion();
		if (JNI_VERSION.equalsIgnoreCase(remoteVer)) {
			MyLog.e(TAG, "Same:localVer=" + JNI_VERSION + ";\nremoteVer="
					+ remoteVer);
		} else {
			MyLog.e(TAG, "Not-Same:localVer=" + JNI_VERSION
					+ ";\nremoteVerStr=" + remoteVer);
		}
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

	/**
	 * 获取系统语言
	 * 
	 * @return 0:中文 1:英文
	 */
	public static int getLanguage() {
		int lan = JVConst.LANGUAGE_ZH;
		String language = Locale.getDefault().getLanguage();
		if (language.equalsIgnoreCase("zh")) {// 中文
			lan = JVConst.LANGUAGE_ZH;
		} else {// 英文
			lan = JVConst.LANGUAGE_EN;
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
		int lan = JVConst.LANGUAGE_ZH;
		String language = Locale.getDefault().getLanguage();
		if (language.equalsIgnoreCase("zh")) {// 中文
			lan = JVConst.LANGUAGE_ZH;
			lanFlag = true;
		} else {// 英文
			lan = JVConst.LANGUAGE_EN;
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

			result = JVACCOUNT.InitSDK(context, Consts.ACCOUNT_PATH);
			String channelIp = "";
			String onlineIp = "";
			if (ConfigUtil.isLanZH()) {
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
				if (getNetWorkConnection()) {
					JVACCOUNT.ConfigServerAddress(Url.SHORTSERVERIP,
							Url.LONGSERVERIP);
				}
			}
			statusHashMap.put(Consts.KEY_INIT_ACCOUNT_SDK,
					String.valueOf(result));

			MyLog.v(TAG, "初始化账号--sdk--res=" + String.valueOf(result));
		}
		return result;
	}

	/**
	 * 开启广播
	 */
	public static void startBroadCast() {
		Jni.searchLanServer(9400, 6666);
	}

	/**
	 * 停止广播
	 */
	public static void stopBroadCast() {
		Jni.stopSearchLanServer();
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
			Jni.enableLinkHelper(true, 3, 20);// 开小助手
			Jni.searchLanServer(9400, 6666);// 开广播
			statusHashMap
					.put(Consts.KEY_INIT_CLOUD_SDK, String.valueOf(result));

			MyLog.e(Consts.TAG_APP, "init sdk = " + String.valueOf(result));
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
	public static boolean getNetWorkConnection() {
		flag = false;
		new Thread() {
			public void run() {
				String serverURL = "";
				if (isLanZH()) {
					serverURL = "http://www.baidu.com/";
				} else {
					serverURL = "http://www.google.com/";
				}
				HttpGet httpRequest = new HttpGet(serverURL);// 建立http get联机
				// HttpResponse httpResponse;
				try {
					new DefaultHttpClient().execute(httpRequest);
					flag = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
		for (int i = 0; i < 3; i++) {
			if (!flag) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
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
	 * 注销方法
	 */
	public static void logOut() {
		Jni.stopSearchLanServer();
		Jni.enableLinkHelper(false, 3, 0);
		Jni.deinitAudioEncoder();
		Jni.deinit();
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
					Toast.makeText(context,
							context.getString(R.string.str_test_3G),
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		} else
			return false;
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
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {

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
		if (ipAddress
				.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$")) {
			return true;
		} else {
			return false;
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
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");// 可以方便地修改日期格式
		String time = dateFormat.format(now);
		return time;
	}
}
