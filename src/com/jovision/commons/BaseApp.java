package com.jovision.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jovision.activities.JVMoreFeatureActivity.MoreHandler;
import com.jovision.activities.JVNPDetailActivity.NewProHandler;
import com.jovision.bean.ConnPoint;
import com.jovision.bean.Device;
import com.jovision.bean.PushInfo;
import com.jovision.bean.RemoteVideo;
import com.jovision.newbean.UserBean;
import com.jovision.old.JVConnectInfo;
import com.jovision.utils.mails.MailSenderInfo;
import com.jovision.utils.mails.MyAuthenticator;
import com.jovision.views.Wifi;

public class BaseApp extends Application {
	// public static boolean isAppKilled = false;

	public static String MYTAG = "XXX";

	public static int errorCount = 0;// 错误次数

	public static boolean SAVE_LASTFRAME_SUCCESS = false;// 保存最后一帧
	public static boolean CAPTURE_BIG_SIZE = false;// 区分保存最后一帧图尺寸
	public static boolean CAPTURE_FLAG = false;// 区分抓拍和保存最后一帧图功能

	public static boolean WEBCC_INTERFACE = false;// 与webcc交互借口调用状态
	public static boolean GESPTZ = false;// 手势云台是否开启
	public static boolean finishSetting = false;// 完成配置要关闭设备管理界面
	// public static boolean NODEVICE = false;// 没设备要关掉设备管理界面

	public static final String IPC_FLAG = "IPC-";
	public static final String IPC_DEFAULT_USER = "jwifiApuser";
	public static final String IPC_DEFAULT_PWD = "^!^@#&1a**U";
	public static final String IPC_DEFAULT_IP = "10.10.0.1";
	public static final int IPC_DEFAULT_PORT = 9101;
	public static String oldWifiSSID = "";// 进程序之前连接的wifi
	public static boolean oldWifiState = false;// 进快速配置前原wifi状态
	public static ArrayList<ScanResult> wifiConfigList = new ArrayList<ScanResult>();// IPC网络列表

	public static int WEBCC_ONLINE = 1;// 与webcc交互上线标志位
	public static int WEBCC_OFFLINE = 2;// 与webcc交互下线标志位

	public static boolean PUBLIC_VERSION = false;// 公共版本标志位
	public static boolean showQRHelp = true;// 是否显示二维码扫描以及帮助图

	public static boolean LOADIMAGE = false;// 是否显示场景图片,默认设置显示场景图
	public static boolean openMap = false;// 用于防止打开多个地图
	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/";

	// public static boolean HAS_BROADCAST = false;//是否广播过

	public static boolean create_picture = false;
	public static boolean isRecord = false;
	public static boolean isInitSdk = false;
	public static boolean isInitPushSdk = false;// 推送sdk
	public static boolean isACCOUNTSdk = false;// 账号sdk
	public static boolean isRegisterSdk = false;
	public static boolean isInitDecoder = false;
	// public static JVConnectInfo loginChannelInfo;
	public static JVConfigManager dbManager;
	private static int selectChildPosition;
	private static int selectParentPosition;
	public static int lengthOfDeviceList = -1;

	public static Bitmap[] h264Bitmap = { null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null };

	public static ArrayList<PushInfo> pushList = null; // 推送消息列表
	public static NotificationManager mNotificationManager = null;// 用于清空通知栏
	public static boolean DEBUG = true;// 是否打印日志 true：打印；false：不打印
	private static String tableName = "JVConfigTemp";
	private static String captureTable = "JVCaptureTable";
	private static String userTableName = "LoginUser";
	// public static Device updateDevice;// 要修改的设备
	public static int broadcastState = JVConst.UPDATE_LIST_BROADCAST;// UPDATE_LIST_BROADCAST:更新设备列表时广播设备列表
																		// UPDATE_STATE_BROADCAST:更新设备状态时广播设备列表
																		// CHECK_STATE_BROADCAST:设置是否检查设备状态时广播设备列表
	// private String ID = "ID"; // long
	// private String srcName = "srcName"; // varchar
	// private String connType = "connType"; // int
	// private String csNumber = "csNumber"; // int
	// private String ipAddr = "ipAddr"; // varchar
	// private String port = "port"; // int
	// private String channel = "channel"; // int
	// private String user = "user"; // varchar
	// private String pass = "pass"; // varchar
	// private String byUDP = "byudp"; // int 0 false 1 true
	// private String localTry = "localtry"; // int 0 false 1 true
	// private String group = "groupName";
	// private String isParent = "isParent";
	// private String nickName = "nickName";
	static String[] clm = new String[] { "ID", "srcName", "connType",
			"csNumber", "ipAddr", "port", "channel", "user", "pass", "byUDP",
			"localTry", "isParent", "nickName", "groupName" };

	static String[] capture = new String[] { "imageId", "imageName" };

	static String[] userclm = new String[] { "userId", "userName", "userPwd",
			"lastLogin" };

	public static ArrayList<PushInfo> getPushList() {
		if (null == pushList) {
			pushList = new ArrayList<PushInfo>();
		}
		return pushList;
	}

	public static ArrayList<PushInfo> orderPushList() {
		if (null == pushList || 0 == pushList.size()) {
			pushList = new ArrayList<PushInfo>();
		} else {

			for (int i = 0; i < pushList.size(); i++) {
				PushInfo pushInfo1 = pushList.get(i);
				for (int j = i + 1; j < pushList.size(); j++) {
					PushInfo pushInfo2 = pushList.get(j);
					if (pushInfo1.alarmTime < pushInfo2.alarmTime) {
						PushInfo temInfo = pushInfo1;
						pushList.set(i, pushInfo2);
						pushList.set(j, temInfo);
					}
				}
			}
		}
		return pushList;
	}

	// // 初始化sdk,开启小助手，初始化推送sdk
	// public static void initSDK() {
	// Thread thread = new Thread() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// if (!BaseApp.isInitSdk) {
	// if (JVSUDT.JVC_InitSDK(9200, JVSUDT.getInstance())) {
	// // MyLog.v("init", "初始化成功");
	// JVSUDT.JVC_RegisterCallBack(BaseApp.SDPATH);
	// JVSUDT.JVC_EnableHelp(true, 3);
	// BaseApp.setInitSdk(true);
	// }
	// }
	// super.run();
	// }
	//
	// };
	// thread.start();
	//
	// }
	// public static boolean initSDK(Context context) {
	//
	// // if (!BaseApp.isInitPushSdk) {
	// // // 进程序调用一次
	// // // JVClient.JVInitWebcc();
	// // // JVClient.JVRegisterCallBack("android/test/JVClient",
	// // // "JVPushInfoCallBack", "JVPushChanngeCallBack");
	// // MyLog.v("进程序调用一次推送", "");
	// // BaseApp.isInitPushSdk = true;
	// // }
	//
	// // 初始化账号sdk
	// boolean result = false;
	// if (!BaseApp.isACCOUNTSdk) {
	// String sdcardPath = "";
	// if (!Environment.MEDIA_MOUNTED.equals(Environment
	// .getExternalStorageState())) {// 无sd卡
	//
	// } else {// 有sd卡
	// String filename = Environment.getExternalStorageDirectory()
	// .getPath() + File.separator + "AccSdkLog.txt";
	// MyLog.v(" 初始化账号sdk+filename=", filename);
	// File file = new File(filename);
	// if (file.exists()) {
	// file.delete();
	// }
	// sdcardPath = filename;
	// }
	//
	// MyLog.v("logPath---", sdcardPath);
	// result = JVACCOUNT.InitSDK(JVACCOUNT.getInstance(), sdcardPath);
	// String channelIp = "";
	// String onlineIp = "";
	// if (ConfigUtil.isLanZH()) {
	// channelIp = sharedPreferences.getString("ChannelIP", "");
	// onlineIp = sharedPreferences.getString("OnlineIP", "");
	// } else {
	// channelIp = sharedPreferences.getString("ChannelIP_en", "");
	// onlineIp = sharedPreferences.getString("OnlineIP_en", "");
	// }
	// if (null != channelIp && !"".equals(channelIp) && null != onlineIp
	// && !"".equals(onlineIp)) {
	// JVACCOUNT.SetServerIP(channelIp, onlineIp);
	// } else {
	// if (isConnected(context) && getNetWorkConnection()) {
	// JVACCOUNT.ConfigServerAddress(Url.SHORTSERVERIP,
	// Url.LONGSERVERIP);
	// }
	// }
	// BaseApp.isACCOUNTSdk = result;
	// MyLog.v("init", "初始化账号--sdk成功" + result);
	// }
	//
	// // if (!BaseApp.isInitSdk) {
	// // if (JVSUDT.JVC_InitSDK(9200, JVSUDT.getInstance(), BaseApp.SDPATH)) {
	// //
	// // if (!Environment.MEDIA_MOUNTED.equals(Environment
	// // .getExternalStorageState())) {// 无sd卡
	// // // JVSUDT.JVC_RegisterCallBack("");
	// // } else {// 有sd卡
	// // // JVSUDT.JVC_RegisterCallBack(BaseApp.SDPATH);
	// // }
	// // // JVSUDT.JVC_EnableHelp(true, 3);
	// // BaseApp.setInitSdk(true);
	// // MyLog.v("init", "初始化设备--sdk成功" + BaseApp.isInitSdk);
	// // }
	// // }
	//
	// // 推送接口
	// // Log.v("开始调用推送--", "-----");
	// // int res = JVACCOUNT.RegisterServerPushFunc();
	// // Log.v("推送调用完成--", "-----");
	//
	// MyLog.v("传IP--", "-----");// 192
	// // boolean openTag = JVACCOUNT.ConfigServerAddress(Url.SERVERIP);
	// // Log.v("传IP结果--", "-----" + openTag);
	// return result;
	// }

	// public
	public static boolean isCreate_picture() {
		return create_picture;
	}

	public static void setCreate_picture(boolean create_picture) {
		BaseApp.create_picture = create_picture;
	}

	public static boolean isRecord() {
		return isRecord;
	}

	public static void setRecord(boolean isRecord) {
		BaseApp.isRecord = isRecord;
	}

	public static boolean isInitSdk() {
		return isInitSdk;
	}

	public static void setInitSdk(boolean isInitSdk) {
		BaseApp.isInitSdk = isInitSdk;
	}

	public static boolean isRegisterSdk() {
		return isRegisterSdk;
	}

	public static void setRegisterSdk(boolean isRegisterSdk) {
		BaseApp.isRegisterSdk = isRegisterSdk;
	}

	public static boolean isInitDecoder() {
		return isInitDecoder;
	}

	public static void setInitDecoder(boolean isInitDecoder) {
		BaseApp.isInitDecoder = isInitDecoder;
	}

	public static JVConfigManager getDbManager(Context con) {

		if (dbManager == null) {
			dbManager = new JVConfigManager(con, JVConst.JVCONFIG_DATABASE,
					null, JVConst.JVCONFIG_DB_VER);
		}

		return dbManager;
	}

	public static void setDbManager(JVConfigManager dbManager) {
		BaseApp.dbManager = dbManager;
	}

	public static String IMEI = "";

	// /**
	// * 唯一的设备ID： 　　* GSM手机的 IMEI 和 CDMA手机的 MEID. 　　* Return null if device ID
	// is
	// * not available. IMEI --International Mobile Equipment Identity
	// * EMID---Mobile Equipment IDentifier 都是用来唯一标示移动终端，MEID主要分配给CDMA制式的手机
	// * 于是你可以参考EMID_百度百科 自我感觉两者有点类似ipv4与ipv6的关系，都是资源枯竭导致的
	// *
	// * 　　
	// */
	// public static String getIMEI(Context context) {
	// if (!"".equalsIgnoreCase(IMEI)) {
	// return IMEI;
	// } else {
	// TelephonyManager telephonyManager = (TelephonyManager) context
	// .getSystemService(Context.TELEPHONY_SERVICE);
	// IMEI = telephonyManager.getDeviceId();
	// }
	// if (null == IMEI || "".equalsIgnoreCase(IMEI)) {
	// StringBuffer deviceId = new StringBuffer();
	// // wifi mac地址
	// WifiManager wifi = (WifiManager) context
	// .getSystemService(Context.WIFI_SERVICE);
	// WifiInfo info = wifi.getConnectionInfo();
	// String wifiMac = info.getMacAddress();
	// if (!"".equalsIgnoreCase(wifiMac)) {
	// deviceId.append(wifiMac);
	// }
	// IMEI = deviceId.toString();
	// }
	//
	// MyLog.v("IMEI", IMEI);
	// return IMEI;
	// }

	// db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (" + this.ID
	// + " VARCHAR PRIMARY KEY,"
	// + this.srcName + " VARCHAR,"
	// + this.connType + " INTEGER,"
	// + this.csNumber + " INTEGER ,"
	// + this.ipAddr + " VARCHAR,"
	// + this.port + " INTEGER,"
	// + this.channel + " INTEGER,"
	// + this.user + " VARCHAR,"
	// + this.pass + " VARCHAR,"
	// + this.byUDP + " INTEGER,"
	// + this.localTry + " INTEGER,"
	// + this.isParent + " INTEGER,"
	// + this.nickName + " VARCHAR,"
	// + this.group + " VARCHAR" + ")");

	// public static int addItem(JVConnectInfo info) {
	//
	// SQLiteDatabase database = dbManager.getWritableDatabase();
	// int i = 0;
	// try {
	// if (database.isOpen()) {
	// ContentValues cv = new ContentValues();
	// cv.put(clm[0], Long.toString(info.getPrimaryID()));
	// cv.put(clm[1], info.getSrcName());
	// cv.put(clm[2], info.getConnType());
	// cv.put(clm[3], info.getCsNumber());
	// if (1 == info.isDevice) {
	// cv.put(clm[4], info.getRemoteIp());
	// cv.put(clm[5], info.getPort());
	// } else {
	// cv.put(clm[4], "");
	// cv.put(clm[5], 0);
	// }
	// cv.put(clm[6], info.getChannel());
	// cv.put(clm[7], info.getUserName());
	// cv.put(clm[8], info.getPasswd());
	// cv.put(clm[9], info.isByUDP());
	// cv.put(clm[10], info.isLocalTry());
	// cv.put(clm[11], info.isParent() == true ? 1 : 0);
	// cv.put(clm[12], info.getNickName());
	// cv.put(clm[13], info.getGroup());
	// i = (int) database.insert(tableName, "ID", cv);
	//
	// } else {
	// i = 0;
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// i = 0;
	// } finally {
	// try {
	// if (database != null && database.isOpen()) {
	// database.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// return i;
	// }
	//
	// // 直接插入通道列表
	// public static int addJVCInfos(ArrayList<JVConnectInfo> infoList) {
	//
	// SQLiteDatabase database = dbManager.getWritableDatabase();
	// int res = 0;
	// try {
	// if (database.isOpen()) {
	// ContentValues cv = new ContentValues();
	//
	// for (int i = 0; i < infoList.size(); i++) {
	// JVConnectInfo info = infoList.get(i);
	//
	// cv.put(clm[0], Long.toString(info.getPrimaryID() + i));
	// cv.put(clm[1], info.getSrcName());
	// cv.put(clm[2], info.getConnType());
	// cv.put(clm[3], info.getCsNumber());
	// cv.put(clm[4], info.getRemoteIp());
	// cv.put(clm[5], info.getPort());
	// cv.put(clm[6], info.getChannel());
	// cv.put(clm[7], info.getUserName());
	// cv.put(clm[8], info.getPasswd());
	// cv.put(clm[9], info.isByUDP());
	// cv.put(clm[10], info.isLocalTry());
	// if (info.isParent()) {
	// cv.put(clm[11], 1);
	// } else {
	// cv.put(clm[11], 0);
	// }
	//
	// // cv.put(clm[11], info.isParent() == true ? 1 : 0);
	// cv.put(clm[12], info.getNickName());
	// cv.put(clm[13], info.getGroup());
	// res = (int) database.insert(tableName, "ID", cv);
	//
	// if (res == 0) {// 插入失败了，再插入一次
	// cv.put(clm[0], Long.toString(info.getPrimaryID()));
	// cv.put(clm[1], info.getSrcName());
	// cv.put(clm[2], info.getConnType());
	// cv.put(clm[3], info.getCsNumber());
	// cv.put(clm[4], info.getRemoteIp());
	// cv.put(clm[5], info.getPort());
	// cv.put(clm[6], info.getChannel());
	// cv.put(clm[7], info.getUserName());
	// cv.put(clm[8], info.getPasswd());
	// cv.put(clm[9], info.isByUDP());
	// cv.put(clm[10], info.isLocalTry());
	// if (info.isParent()) {
	// cv.put(clm[11], 1);
	// } else {
	// cv.put(clm[11], 0);
	// }
	// // cv.put(clm[11], info.isParent() == true ? 1 : 0);
	// cv.put(clm[12], info.getNickName());
	// cv.put(clm[13], info.getGroup());
	// res = (int) database.insert(tableName, "ID", cv);
	// }
	// }
	// } else {
	// res = 0;
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// // i = 0;
	// } finally {
	// try {
	// if (database != null && database.isOpen()) {
	// database.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// return res;
	// }
	//
	// // public static boolean queryYstByNumber(int ystNum, String group) {
	// // SQLiteDatabase readableDatabase = dbManager.getReadableDatabase();
	// // Cursor c = null;
	// // boolean b = false;
	// // try {
	// // if (readableDatabase.isOpen()) {
	// // c = readableDatabase.rawQuery("select count(*) from '"
	// // + tableName + "' where csNumber=? and groupName=?",
	// // new String[] { String.valueOf(ystNum), group });
	// // if (c != null) {
	// // if (c.moveToFirst()) {
	// // int count = c.getInt(0);
	// // if (count > 0) {
	// // b = true;
	// // }
	// // }
	// // c.close();
	// //
	// // }
	// //
	// // }
	// // } catch (Exception e) {
	// // // TODO: handle exception
	// // e.printStackTrace();
	// //
	// // b = false;
	// // } finally {
	// // try {
	// // if (readableDatabase != null && readableDatabase.isOpen()) {
	// // readableDatabase.close();
	// // }
	// // } catch (Exception e2) {
	// // // TODO: handle exception
	// // }
	// // }
	// //
	// // return b;
	// // }
	//
	// // 查询单个通道实体对象
	// public static JVConnectInfo queryChannelByYst(JVConnectInfo info) {
	// SQLiteDatabase readableDatabase = dbManager.getReadableDatabase();
	// Cursor c = null;
	// JVConnectInfo resultInfo = null;
	// try {
	// if (readableDatabase.isOpen()) {
	// c = readableDatabase.query(
	// tableName,
	// clm,
	// "csNumber=? and groupName=? and isParent=0",
	// new String[] { String.valueOf(info.getCsNumber()),
	// info.getGroup() }, null, null, null);
	// if (c == null) {
	// return null;
	// } else {
	// boolean t = c.moveToFirst();
	// if (t) {
	// resultInfo = new JVConnectInfo(
	// System.currentTimeMillis());
	// resultInfo.setPrimaryID(Long.parseLong(c.getString(0)));
	// resultInfo.setSrcName(c.getString(1));
	// resultInfo.setConnType(c.getInt(2));
	// resultInfo.setCsNumber(c.getInt(3));
	// resultInfo.setRemoteIp(c.getString(4));
	// resultInfo.setPort(c.getInt(5));
	// resultInfo.setChannel(c.getInt(6));
	// resultInfo.setUserName(c.getString(7));
	// resultInfo.setPasswd(c.getString(8));
	// resultInfo.setByUDP(c.getInt(9) == 1 ? true : false);
	// resultInfo
	// .setLocalTry(c.getInt(10) == 1 ? true : false);
	// resultInfo.setParent(c.getInt(11) == 1 ? true : false);
	// resultInfo.setNickName(c.getString(12));
	// resultInfo.setGroup(c.getString(13));
	// }
	// c.close();
	//
	// }
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// System.out.print("异常原因异常原因异常原因异常原因" + e);
	// resultInfo = null;
	// } finally {
	// try {
	// if (readableDatabase != null && readableDatabase.isOpen()) {
	// readableDatabase.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// return resultInfo;
	// }
	//
	// // 修改通道实体端口号和IP
	// public static int modifyChannelIPPORT(JVConnectInfo info) {
	// SQLiteDatabase writableDatabase = null;
	// int ret = 0;
	// try {
	// writableDatabase = dbManager.getWritableDatabase();
	// ContentValues cv = new ContentValues();
	// cv.put("ipAddr", info.getRemoteIp());
	// cv.put("port", info.getPort());
	//
	// if (writableDatabase.isOpen()) {
	// ret = writableDatabase.update(
	// tableName,
	// cv,
	// "csNumber=? and groupName=? ",
	// new String[] { String.valueOf(info.getCsNumber()),
	// info.getGroup() });// .delete(tableName,"csNumber=? AND channel=?"
	// // ,new
	// // String[]{"361","1"});
	// // MyLog.v("tags",
	// // "delete ok "
	// // +
	// // ret);
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// } finally {
	// try {
	// if (writableDatabase != null && writableDatabase.isOpen()) {
	// writableDatabase.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// return ret;
	// }
	//
	// // 修改所有通道实体端口号和IP
	// public static int modifyAllIPPORT() {
	// SQLiteDatabase writableDatabase = null;
	// int ret = 0;
	// try {
	// writableDatabase = dbManager.getWritableDatabase();
	// ContentValues cv = new ContentValues();
	// cv.put("ipAddr", "");
	// cv.put("port", 9101);
	//
	// if (writableDatabase.isOpen()) {
	// ret = writableDatabase.update(tableName, cv, "", null);//
	// .delete(tableName,"csNumber=? AND channel=?"
	// // ,new
	// // String[]{"361","1"});
	// // MyLog.v("tags",
	// // "delete ok "
	// // +
	// // ret);
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// } finally {
	// try {
	// if (writableDatabase != null && writableDatabase.isOpen()) {
	// writableDatabase.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// return ret;
	// }
	//
	// // 修改通道实体用户名密码
	// public static int modifyChannelInfo(JVConnectInfo info) {
	// SQLiteDatabase writableDatabase = null;
	// int ret = 0;
	// try {
	// writableDatabase = dbManager.getWritableDatabase();
	// ContentValues cv = new ContentValues();
	// cv.put("pass", info.getPasswd());
	// cv.put("user", info.getUserName());
	//
	// if (writableDatabase.isOpen()) {
	// ret = writableDatabase.update(
	// tableName,
	// cv,
	// "csNumber=? and groupName=? ",
	// new String[] { String.valueOf(info.getCsNumber()),
	// info.getGroup() });// .delete(tableName,"csNumber=? AND channel=?"
	// // ,new
	// // String[]{"361","1"});
	// // MyLog.v("tags",
	// // "delete ok "
	// // +
	// // ret);
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// } finally {
	// try {
	// if (writableDatabase != null && writableDatabase.isOpen()) {
	// writableDatabase.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// return ret;
	// }
	//
	// public static int modifyDeviceInfo(JVConnectInfo info) {
	// SQLiteDatabase writableDatabase = null;
	// int ret = 0;
	// try {
	// writableDatabase = dbManager.getWritableDatabase();
	// ContentValues cv = new ContentValues();
	// cv.put("ipAddr", info.getRemoteIp());
	// cv.put("port", info.getPort());
	// cv.put("nickName", info.getNickName());
	// cv.put("user", info.getUserName());
	// cv.put("pass", info.getPasswd());
	// if (writableDatabase.isOpen()) {
	// ret = writableDatabase.update(
	// tableName,
	// cv,
	// "csNumber=? and groupName=? and isParent=1",
	// new String[] { String.valueOf(info.getCsNumber()),
	// info.getGroup() });
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// } finally {
	// try {
	// if (writableDatabase != null && writableDatabase.isOpen()) {
	// writableDatabase.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// return ret;
	// }
	//
	// // 修改昵称
	// public static int modifyChannelInfoPort(JVConnectInfo info) {
	// SQLiteDatabase writableDatabase = null;
	// int ret = 0;
	// try {
	// writableDatabase = dbManager.getWritableDatabase();
	// ContentValues cv = new ContentValues();
	// cv.put("nickName", info.getNickName());
	// if (writableDatabase.isOpen()) {
	//
	// if (info.isParent()) {// 修改设备
	// cv.put("user", info.getUserName());
	// cv.put("pass", info.getPasswd());
	// ret = writableDatabase.update(tableName, cv,
	// "csNumber=? and groupName=? and isParent=1",
	// new String[] { String.valueOf(info.getCsNumber()),
	// info.getGroup() });// .delete(tableName,"csNumber=? AND channel=?"
	// // ,new
	// // String[]{"361","1"});
	// } else {
	// ret = writableDatabase
	// .update(tableName,
	// cv,
	// "csNumber=? and groupName=? and channel =? and isParent=0",
	// new String[] {
	// String.valueOf(info.getCsNumber()),
	// info.getGroup(),
	// String.valueOf(info.getChannel()) });//
	// .delete(tableName,"csNumber=? AND channel=?"
	// // ,new
	// // String[]{"361","1"});
	// }
	//
	// MyLog.v("tags", "delete ok " + ret);
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// } finally {
	// try {
	// if (writableDatabase != null && writableDatabase.isOpen()) {
	// writableDatabase.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// return ret;
	// }
	//
	// // 删除设备信息
	// // public static int deleteChannel(JVConnectInfo info) {
	// // SQLiteDatabase writableDatabase = null;
	// // int ret = 0;
	// // try {
	// // writableDatabase = dbManager.getWritableDatabase();
	// // ContentValues cv = new ContentValues();
	// // // cv.put("deleNum", info.getDeleNum() + deleNum + ",");//更新表,修改删除字段
	// // if (writableDatabase.isOpen()) {
	// // ret = writableDatabase.update(
	// // tableName,
	// // cv,
	// // "csNumber=? and groupName=? and isParent=?",
	// // new String[] { String.valueOf(info.getCsNumber()),
	// // info.getGroup() });// .delete(tableName,"csNumber=? AND channel=?"
	// // // ,new
	// // // String[]{"361","1"});
	// // // MyLog.v("deleteChannel", "delete ok " + ret);
	// // }
	// // } catch (Exception e) {
	// // // TODO: handle exception
	// // } finally {
	// // try {
	// // if (null != writableDatabase && writableDatabase.isOpen()) {
	// // writableDatabase.close();
	// // }
	// // } catch (Exception e2) {
	// // // TODO: handle exception
	// // }
	// //
	// // }
	// //
	// // // writableDatabase.close();
	// // return ret;
	// // }
	//
	// // 彻底删除，删除通道
	// public static int completeDeleteChannel(JVConnectInfo info) {
	// SQLiteDatabase writableDatabase = null;
	// int ret = 0;
	// try {
	// writableDatabase = dbManager.getWritableDatabase();
	// if (writableDatabase.isOpen()) {
	// if (info.isParent()) {// 删除设备
	// ret = writableDatabase.delete(tableName,
	// "csNumber=? and groupName = ? ",// and
	// // isParent=1",
	// new String[] { String.valueOf(info.getCsNumber()),
	// info.getGroup() });
	// } else {
	//
	// MyLog.v("删除：",
	// info.getGroup() + ""
	// + String.valueOf(info.getCsNumber()) + "_"
	// + String.valueOf(info.getChannel()) + "");
	// ret = writableDatabase
	// .delete(tableName,
	// "csNumber=? and groupName = ? and channel=? and isParent=0",
	// new String[] {
	// String.valueOf(info.getCsNumber()),
	// info.getGroup(),
	// String.valueOf(info.getChannel()) });
	// }
	//
	// MyLog.v("completeDeleteChannel", "delete ok " + ret);
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// } finally {
	// try {
	// if (null != writableDatabase && writableDatabase.isOpen()) {
	// writableDatabase.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// return ret;
	// }
	//
	// // 动态添加通道号
	// public static int addChannel(JVConnectInfo info) {
	// SQLiteDatabase writableDatabase = null;
	// int ret = 0;
	// try {
	// writableDatabase = dbManager.getWritableDatabase();
	// ContentValues cv = new ContentValues();
	// // cv.put("channelCount", info.getChannelCount());
	// if (writableDatabase.isOpen()) {
	// ret = writableDatabase.update(
	// tableName,
	// cv,
	// "csNumber=? and groupName=?",
	// new String[] { String.valueOf(info.getCsNumber()),
	// info.getGroup() });
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// } finally {
	// try {
	// if (null != writableDatabase && writableDatabase.isOpen()) {
	// writableDatabase.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	//
	// }
	//
	// return ret;
	// }
	//
	// // 恢复删除
	// public static int recoverDeleteChannel(JVConnectInfo info) {
	// SQLiteDatabase writableDatabase = null;
	// int ret = 0;
	// try {
	// writableDatabase = dbManager.getWritableDatabase();
	// ContentValues cv = new ContentValues();
	// // cv.put("deleNum", info.getDeleNum());
	// if (writableDatabase.isOpen()) {
	// ret = writableDatabase.update(
	// tableName,
	// cv,
	// "csNumber=? and groupName=?",
	// new String[] { String.valueOf(info.getCsNumber()),
	// info.getGroup() });
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// } finally {
	// try {
	// if (null != writableDatabase && writableDatabase.isOpen()) {
	// writableDatabase.close();
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// return ret;
	// }
	//
	// 查询通道或设备数据,flag = true 查询设备；flag = false 查询通道
	public static List<JVConnectInfo> queryAllDataList(boolean flag) {
		List<JVConnectInfo> list = new ArrayList<JVConnectInfo>();
		JVConnectInfo info = null;
		SQLiteDatabase readDB = null;
		Cursor c = null;
		try {
			readDB = dbManager.getReadableDatabase();
			MyLog.v("String.valueOf(flag==true?1:0)-----",
					String.valueOf(flag == true ? 1 : 0));
			if (readDB.isOpen()) {
				// c = readDB.query(tableName, clm, null, null, null, null,
				// null);
				c = readDB.rawQuery("select * from '" + tableName
						+ "' where isParent=?" + " order by ID desc",// 从数据库里查询按降序查询，最新添加的在最上面显示
						new String[] { String.valueOf(flag == true ? 1 : 0)
								+ "" });
			} else {
				return null;
			}

			if (c == null)
				return null;
			boolean t = c.moveToFirst();
			while (t) {
				info = new JVConnectInfo();
				info.setPrimaryID(Long.parseLong(c.getString(0)));
				info.setSrcName(c.getString(1));
				info.setConnType(c.getInt(2));
				info.setCsNumber(c.getInt(3));
				info.setRemoteIp(c.getString(4));
				info.setPort(c.getInt(5));
				info.setChannel(c.getInt(6));
				info.setUserName(c.getString(7));
				info.setPasswd(c.getString(8));
				info.setByUDP(c.getInt(9) == 1 ? true : false);
				info.setLocalTry(c.getInt(10) == 1 ? true : false);
				info.setOnlineState(1);
				info.setParent(c.getInt(11) == 1 ? true : false);
				info.setNickName(c.getString(12));
				info.setGroup(c.getString(13));
				if ("null".equalsIgnoreCase(info.getRemoteIp())) {
					info.setRemoteIp("");
				}
				if (info.isParent()) {
					if (null == info.getRemoteIp()
							|| "".equals(info.getRemoteIp())
							|| "null".equalsIgnoreCase(info.getRemoteIp())) {
						// info.setDevice(0);
						MyLog.v("sss", "falsedevice");
						MyLog.v("sss", info.getRemoteIp());
					} else {
						// info.setDevice(1);
						MyLog.v("sss", "truedevice");
						MyLog.v("sss", info.getRemoteIp());
					}
				}
				if (info.isParent() == flag) {
					list.add(info);
				}
				MyLog.v("tags	",
						"yst: " + c.getInt(3)
								+ ", group: "
								+ c.getString(13)
								+ ", isParent: "
								+ c.getInt(11)
								+ ", channel: "
								+
								// c.getInt(6)+"");
								// i++;
								// MyLog.v("tags",
								//
								"name=" + c.getString(1) + ",ystNumber="
								+ c.getInt(3) + ",channel=" + c.getInt(6));
				t = c.moveToNext();
			}
			// myList.get(0).setAction(true);
			c.close();

		} catch (Exception e) {
			// TODO: handle exception
			list = null;
		} finally {
			try {
				if (readDB != null && readDB.isOpen()) {
					readDB.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return list;
	}

	// 查询所有用户信息
	public static ArrayList<UserBean> queryAllUserList() {
		ArrayList<UserBean> list = new ArrayList<UserBean>();

		SQLiteDatabase readDB = null;
		Cursor c = null;
		try {
			readDB = dbManager.getReadableDatabase();
			if (readDB.isOpen()) {
				// c = readDB.query(tableName, clm, null, null, null, null,
				// null);
				c = readDB.rawQuery("select * from '" + userTableName
						+ "' order by userId desc",// 从数据库里查询按降序查询，最新添加的在最上面显示
						null);
			} else {
				return null;
			}

			if (c == null)
				return null;
			boolean t = c.moveToFirst();
			while (t) {
				UserBean user = new UserBean();
				user.setPrimaryID(c.getLong(0));
				user.setUserName(c.getString(1));
				user.setUserPwd(c.getString(2));
				user.setLastLogin(c.getInt(3));
				list.add(user);
				t = c.moveToNext();
			}
			c.close();

		} catch (Exception e) {
			list = null;
		} finally {
			try {
				if (readDB != null && readDB.isOpen()) {
					readDB.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}

	// 查询指定用户是否存在
	public static UserBean queryUser(UserBean user) {
		UserBean userRes = null;
		SQLiteDatabase readDB = null;
		Cursor c = null;
		try {
			readDB = dbManager.getReadableDatabase();
			if (readDB.isOpen()) {
				// c = readDB.query(tableName, clm, null, null, null, null,
				// null);
				// c = readDB.rawQuery("select count(*) from '"
				// + userTableName +
				// "' where userName=?",//从数据库里查询按降序查询，最新添加的在最上面显示
				// new String[] { user.getUserName()});
				c = readDB.rawQuery("select * from '" + userTableName
						+ "' where userName = '" + user.getUserName()
						+ "' COLLATE NOCASE", null);

			} else {
				return userRes;
			}
			if (c == null)
				return userRes;
			if (c != null) {
				boolean t = c.moveToFirst();
				while (t) {
					userRes = new UserBean();
					userRes.setPrimaryID(c.getLong(0));
					userRes.setUserName(c.getString(1));
					userRes.setUserPwd(c.getString(2));
					userRes.setLastLogin(c.getInt(3));
					t = c.moveToNext();
				}
				c.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			userRes = null;
		} finally {
			try {
				if (readDB != null && readDB.isOpen()) {
					readDB.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return userRes;
	}

	// 查询上次登录用户
	public static UserBean queryLoginUser() {
		ArrayList<UserBean> list = new ArrayList<UserBean>();
		SQLiteDatabase readDB = null;
		Cursor c = null;
		UserBean user = null;
		try {
			readDB = dbManager.getReadableDatabase();
			if (readDB.isOpen()) {
				// c = readDB.query(tableName, clm, null, null, null, null,
				// null);
				c = readDB.rawQuery("select * from " + userTableName
						+ " where lastLogin=1",// 从数据库里查询lastLogin
												// =
												// 1的user
						null);

			} else {
				return user;
			}

			if (c != null) {
				int count = c.getCount();
				c.moveToFirst();
				for (int i = 0; i < count; i++) {
					user = new UserBean();
					user.setPrimaryID(c.getLong(0));
					user.setUserName(c.getString(1));
					user.setUserPwd(c.getString(2));
					user.setLastLogin(c.getInt(3));
					list.add(user);
					c.moveToNext();
				}
				c.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return user;
		} finally {
			try {
				if (readDB != null && readDB.isOpen()) {
					readDB.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return user;
	}

	// 注销时将最后登录标志位都置为0
	public static int resetUser() {

		SQLiteDatabase writableDatabase = null;
		int ret = 0;
		try {
			writableDatabase = dbManager.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("lastLogin", 0);

			if (writableDatabase.isOpen()) {
				ret = writableDatabase.update(userTableName, cv, "", null);// .delete(tableName,"csNumber=? AND channel=?"
																			// ,new
																			// String[]{"361","1"});
				// MyLog.v("tags", "delete ok " + ret);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (writableDatabase != null && writableDatabase.isOpen()) {
					writableDatabase.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return ret;
	}

	// 修改登录用户名密码
	public static int modifyUserInfo(UserBean user) {
		SQLiteDatabase writableDatabase = null;
		int ret = 0;
		try {
			writableDatabase = dbManager.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("userPwd", user.getUserPwd());

			if (writableDatabase.isOpen()) {
				ret = writableDatabase.update(userTableName, cv, "userName=? ",
						new String[] { user.getUserName() });// .delete(tableName,"csNumber=? AND channel=?"
				// ,new
				// String[]{"361","1"});
				// MyLog.v("tags", "delete ok " + ret);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (writableDatabase != null && writableDatabase.isOpen()) {
					writableDatabase.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return ret;
	}

	// 修改登录状态
	public static int modifyUserLoginInfo(UserBean user) {
		SQLiteDatabase writableDatabase = null;
		int ret = 0;
		try {
			writableDatabase = dbManager.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("lastLogin", user.getLastLogin());
			cv.put("userId", user.getPrimaryID());

			if (writableDatabase.isOpen()) {
				ret = writableDatabase.update(userTableName, cv, "userName=? ",
						new String[] { user.getUserName() });// .delete(tableName,"csNumber=? AND channel=?"
				// ,new
				// String[]{"361","1"});
				// MyLog.v("tags", "delete ok " + ret);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (writableDatabase != null && writableDatabase.isOpen()) {
					writableDatabase.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return ret;
	}

	// 删除登录用户信息
	public static int deleteUser(UserBean user) {
		SQLiteDatabase writableDatabase = null;
		int ret = 0;
		try {
			writableDatabase = dbManager.getWritableDatabase();
			if (writableDatabase.isOpen()) {
				ret = writableDatabase.delete(userTableName, "userName=?",// and
																			// isParent=1",
						new String[] { user.getUserName() });
				// MyLog.v("deleteUser", "delete ok " + ret);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (null != writableDatabase && writableDatabase.isOpen()) {
					writableDatabase.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return ret;
	}

	public static List<String> queryCaptureDataList() {
		SQLiteDatabase readableDatabase = null;
		List<String> list = new ArrayList<String>();
		try {
			readableDatabase = dbManager.getReadableDatabase();
			Cursor c = null;
			if (readableDatabase.isOpen()) {
				c = readableDatabase.query(captureTable, capture, null, null,
						null, null, null);
				if (c == null) {
					return null;
				} else {
					boolean t = c.moveToFirst();
					while (t) {
						list.add(c.getString(1));
						t = c.moveToNext();
					}
					c.close();

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (null != readableDatabase && readableDatabase.isOpen()) {
					readableDatabase.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return list;
	}

	// public static JVConnectInfo getLoginChannelInfo() {
	// return loginChannelInfo;
	// }
	//
	// public static void setLoginChannelInfo(JVConnectInfo loginChannelInfo) {
	// BaseApp.loginChannelInfo = loginChannelInfo;
	// }

	/**
	 * 进度条
	 */
	public static ProgressDialog createProgressBar(Context c) {
		ProgressDialog progressDlg = new ProgressDialog(c);
		progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progressDlg.setMessage(c.getResources().getString(
		// R.string.searchDeviceInfo));
		progressDlg.setProgress(100);
		progressDlg.setCancelable(true);
		progressDlg.show();
		return progressDlg;
	}

	// 没有网络提示
	public static void alertNetDialog(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(R.string.str_setting_network);
		builder.setTitle(R.string.tips);
		builder.create().show();
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

	// public static boolean loginDialog(Context context) {
	//
	// LayoutInflater factory = LayoutInflater.from(context);
	// final View textEntryView = factory.inflate(R.layout.login_dialog, null);
	//
	// final AlertDialog login = new AlertDialog.Builder(context)
	// // .setIcon(R.drawable.btn_login_icon)
	// // .setIcon(android.R.drawable.ic_menu_more)
	// // .setTitle("登录")
	// .setView(textEntryView)
	// .show();
	//
	// // Button btn_confirm = (Button)
	// textEntryView.findViewById(R.id.btn_confirm);
	// // btn_confirm.setOnClickListener(new OnClickListener(){
	// //
	// // @Override
	// // public void onClick(View arg0) {
	// // // TODO Auto-generated method stub
	// // EditText edt = (EditText)textEntryView.findViewById(R.id.username);
	// // String name = edt.getText().toString();
	// // edt = (EditText)textEntryView.findViewById(R.id.password);
	// // String pass = edt.getText().toString();
	// // HashMap<String, String> parms = new HashMap<String, String>();
	// // parms.put("username", name);
	// // parms.put("password", pass);
	// // CommonAccount c = loginRegister(Url.URL_LOGIN, parms);
	// // if (c!=null&&c.isStatus()) {
	// // Method.getInstance().addAcccounts(parms);
	// // result = true ;
	// // if (isSetting) {
	// // ((TextView)((Activity)
	// context).findViewById(R.id.state_text)).setText("已登录");
	// //
	// ((ImageButton)((Activity)context).findViewById(R.id.btn_log)).setBackgroundResource(R.drawable.btn_logout);
	// //
	// (((Activity)context).findViewById(R.id.nickname)).setVisibility(View.VISIBLE);
	// //
	// //
	// ((TextView)((Activity)context).findViewById(R.id.tv_nickname)).setText(CommonAccount.getInstance().getName());
	// //
	// (((Activity)context).findViewById(R.id.line2)).setVisibility(View.VISIBLE);
	// // }
	// // Toast.makeText(context,"登录成功", Toast.LENGTH_SHORT).show();
	// // login.dismiss();
	// // } else {
	// // Toast.makeText(context,"登录失败,请检查账号密码是否正确！", Toast.LENGTH_LONG).show();
	// // }
	// // }
	// //
	// // });
	// //
	// // Button btn_cancel = (Button)
	// textEntryView.findViewById(R.id.btn_cancel);
	// // btn_cancel.setOnClickListener(new OnClickListener(){
	// //
	// // @Override
	// // public void onClick(View v) {
	// // // TODO Auto-generated method stub
	// // login.dismiss() ;
	// // registerDialog();
	// // }
	// //
	// // });
	// //
	// //// TextView tv=(TextView)textEntryView.findViewById(R.id.toRegister);
	// //// tv.setOnClickListener(new OnClickListener() {
	// ////
	// //// @Override
	// //// public void onClick(View v) {
	// //// login.dismiss() ;
	// //// registerDialog();
	// //// }
	// ////
	// //// });
	// //
	// return true ;
	//
	// }

	// public static Integer[] getImageList() {
	// return IMAGE_LIST;
	// }

	// public static Bitmap parseBitmap(byte[] buff, int len, int offset, int
	// index) {
	//
	// return BitmapFactory.decodeByteArray(buff, offset, len);
	// }

	public static Bitmap parseBitmap(int index, ByteBuffer buffer, int w, int h) {

		if (h264Bitmap[index] == null || h264Bitmap[index].isRecycled()
				|| w != h264Bitmap[index].getWidth()
				|| h != h264Bitmap[index].getHeight()) {

			// if (null!=h264Bitmap[index]&& !h264Bitmap[index].isRecycled()) {
			// h264Bitmap[index].recycle();
			// }

			h264Bitmap[index] = null;
			Bitmap bit = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
			// SoftReference<Bitmap> softReference = new
			// SoftReference<Bitmap>(bit);
			h264Bitmap[index] = bit;
			// MyLog.v("tags", "destroy h264Bitmap[index]");
		}
		// MyLog.v("tags", "width: "+h264Bitmap[index].getWidth()+", heigt"
		// +h264Bitmap[index].getHeight()+"mdesty: "
		// +h264Bitmap[index].getDensity());
		h264Bitmap[index].copyPixelsFromBuffer(buffer);
		buffer.position(0);

		return h264Bitmap[index];
	}

	// public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
	// // 获得图片的宽高
	// int width = bm.getWidth();
	// int height = bm.getHeight();
	// // 计算缩放比例
	// float scaleWidth = ((float) newWidth);
	// float scaleHeight = ((float) newHeight);
	// // 取得想要缩放的matrix参数
	// Matrix matrix = new Matrix();
	// matrix.postScale(scaleWidth, scaleHeight);
	// // 得到新的图片
	// Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
	// true);
	// return newbm;
	// }

	// public static Bitmap parseBitmapImg(int index, Bitmap bit, int w, int h)
	// {
	//
	// if (h264BitmapImg[index] == null || h264BitmapImg[index].isRecycled()) {
	// h264BitmapImg[index] = null;
	// h264BitmapImg[index] = Bitmap.createScaledBitmap(bit, w, h, true);
	// }
	//
	// return h264Bitmap[index];
	// }

	public static void destryBitmap(Bitmap bm) {
		if (bm != null && !bm.isRecycled()) {
			bm.recycle();
			bm = null;
			System.gc();
		}
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
			// State mobile =
			// cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			// State wifi =
			// cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			int type = info.getType();
			SharedPreferences sp = context.getSharedPreferences("JVN_RPEF",
					MODE_PRIVATE);
			if (type == ConnectivityManager.TYPE_WIFI) {
				// MyLog.v("tags", "wifi :"+type);
				sp.edit().putBoolean("isWifi", true).commit();
				// Toast.makeText(context,
				// context.getString(R.string.str_test_3G), 1000*20).show();
			} else {
				// MyLog.v("tags", "not wifi :"+type);
				sp.edit().putBoolean("isWifi", false).commit();
				// Toast.makeText(context,
				// context.getString(R.string.str_test_3G),
				// Toast.LENGTH_SHORT).show();
			}
			return true;
		} else
			return false;
	}

	// 查询拍照图片集合
	public static List<String> loadImgDataList(List<String> dblist, String url) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return null;
		}
		List<String> fileList = new ArrayList<String>();
		try {
			File f = new File(url);

			if (!f.isDirectory()) {

				f.mkdir();

			}
			File[] files = f.listFiles();

			/* 将所有文件添加ArrayList中 */
			int fileLen = files.length;
			for (int i = 0; i < dblist.size(); i++) {
				String filePath = dblist.get(i);
				for (int j = 0; j < fileLen; j++) {
					File file = files[j];
					if (file.getPath().equals(filePath)) {
						fileList.add(filePath);
					}

				}
			}

			// 删除那些已经不用的图片
			if (fileLen > 0) {
				if (null == dblist || dblist.size() == 0) {
					// f.delete();
					for (int i = 0; i < fileLen; i++) {
						boolean result = files[i].delete();
						// MyLog.v("tags", "dele: " + i + "  result=: " +
						// result);
					}
				} else if (null != dblist && dblist.size() > 0) {
					for (int i = 0; i < files.length; i++) {
						boolean find = false;
						for (int j = 0; j < dblist.size(); j++) {
							String filePath = dblist.get(j);
							if (files[i].getPath().equals(filePath)) {
								find = true;
								break;
							}
						}
						if (!find) {
							files[i].delete();
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return fileList;
	}

	// // 查询拍照图片集合
	// public static ArrayList<String> getCaptureImage() {
	// if (!Environment.MEDIA_MOUNTED.equals(Environment
	// .getExternalStorageState())) {
	// return null;
	// }
	// ArrayList<String> fileList = new ArrayList<String>();
	// try {
	// File f = new File(JVSUDT.PICTURE_SAVE_PATH);
	// if (!f.isDirectory()) {
	// f.mkdir();
	// }
	// File[] files = f.listFiles();
	//
	// /* 将所有文件添加ArrayList中 */
	// int fileLen = files.length;
	// for (int j = 0; j < fileLen; j++) {
	// File file = files[j];
	// if (file.getPath().equals(file.getAbsolutePath())) {
	// fileList.add(file.getAbsolutePath());
	// }
	//
	// }
	//
	// // // 删除那些已经不用的图片
	// // if (fileLen > 0) {
	// // if (null == dblist || dblist.size() == 0) {
	// // // f.delete();
	// // for (int i = 0; i < fileLen; i++) {
	// // boolean result = files[i].delete();
	// // MyLog.v("tags", "dele: " + i + "  result=: " + result);
	// // }
	// // } else if (null != dblist && dblist.size() > 0) {
	// // for (int i = 0; i < files.length; i++) {
	// // boolean find = false;
	// // for (int j = 0; j < dblist.size(); j++) {
	// // String filePath = dblist.get(j);
	// // if (files[i].getPath().equals(filePath)) {
	// // find = true;
	// // break;
	// // }
	// // }
	// // if (!find) {
	// // files[i].delete();
	// // }
	// // }
	// // }
	// // }
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// return fileList;
	// }

	// 删除照片
	public static int deleteCaptureImg(String imgUrl) {
		SQLiteDatabase writableDatabase = null;
		int ret = 0;
		try {
			writableDatabase = dbManager.getWritableDatabase();
			if (writableDatabase.isOpen()) {
				ret = writableDatabase.delete(captureTable, "imageName=?",
						new String[] { imgUrl });
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (null != writableDatabase && writableDatabase.isOpen()) {
					writableDatabase.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return ret;
	}

	public static int getSelectChildPosition() {
		return selectChildPosition;
	}

	public static void setSelectChildPosition(int selectChildPosition) {
		BaseApp.selectChildPosition = selectChildPosition;
	}

	public static int getSelectParentPosition() {
		return selectParentPosition;
	}

	public static void setSelectParentPosition(int selectParentPosition) {
		BaseApp.selectParentPosition = selectParentPosition;
	}

	// 获取当前系统时间
	public static String getCurrentTime() {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");// 可以方便地修改日期格式
		String time = dateFormat.format(now);
		return time;
	}

	// 获取当前系统时间
	public static String getCurrentTime1() {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH-mm-ss");// 可以方便地修改日期格式
		String time = dateFormat.format(now);
		return time;
	}

	// 解绑所有view
	public static void unbindViews(View view) {
		if (view == null)
			return;
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View childView = ((ViewGroup) view).getChildAt(i);
				if (childView instanceof ViewGroup)
					unbindViews(childView);
			}
			try {
				((ViewGroup) view).removeAllViews();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 在线设备在前的排序
	 * 
	 * @author suifupeng
	 * @param temList
	 * @return 排序成功后的列表
	 */
	public static ArrayList<Device> orderByOnlineState(ArrayList<Device> temList) {
		ArrayList<Device> onlineDevice = null;
		ArrayList<Device> offlineDevice = null;
		if (null != temList && 0 != temList.size()) {
			onlineDevice = new ArrayList<Device>();
			offlineDevice = new ArrayList<Device>();
			for (int k = 0; k < temList.size(); k++) {
				if (0 == temList.get(k).onlineState) {
					offlineDevice.add(temList.get(k));
				} else {
					onlineDevice.add(temList.get(k));
				}
			}
			temList.clear();
			temList.addAll(onlineDevice);
			temList.addAll(offlineDevice);
		}
		return temList;
	}

	/**
	 * 根据设备是否广播排序，广播的排在前面
	 * 
	 * @author suifupeng
	 * @param temList
	 * @return 排序成功后的列表
	 */
	public static ArrayList<Device> orderByBroadcast(ArrayList<Device> temList) {
		ArrayList<Device> broadcastDevice = null;
		ArrayList<Device> noBroadcastDevice = null;
		if (null != temList && 0 != temList.size()) {
			broadcastDevice = new ArrayList<Device>();
			noBroadcastDevice = new ArrayList<Device>();
			for (int k = 0; k < temList.size(); k++) {
				if (!temList.get(k).isBroadcast) {
					noBroadcastDevice.add(temList.get(k));
				} else {
					broadcastDevice.add(temList.get(k));
				}
			}
			temList.clear();
			temList.addAll(broadcastDevice);
			temList.addAll(noBroadcastDevice);
		}
		return temList;
	}

	/**
	 * 在线设备在前的排序
	 * 
	 * @author suifupeng
	 * @param temList
	 * @return 排序成功后的列表
	 */
	public static ArrayList<Device> allDeviceOnline(ArrayList<Device> temList) {
		if (null != temList && 0 != temList.size()) {
			for (int k = 0; k < temList.size(); k++) {
				temList.get(k).onlineState = 1;
			}
		}
		return temList;
	}

	public static boolean AUDIO_FLAG = false;// 音频播放flag
	public static boolean VOICE_CALL_FLAG = false;// 语音喊话flag

	// jy窗口下标为当前窗口的位置，pointIndex（通道下标）%SCREEN（分屏数），通道下标对分屏数取余
	// 单窗口的windowIndex也是0,1,2,3 （为了保持四路连接）
	// 四窗口，九窗口windowIndex是窗口下标0-3,0-8

	public static int windowIndex = 0;// 窗口下标：0,1,2,3,主控断开连接根据窗口下标断开
	public static int SCREEN = 1;// 1,4,9分屏数
	public static int coonNum = 4;// 保持连接数
	public static int pageCount = 0;// 总页数
	public static int currentPage = 0;// 当前页，从0开始

	// 分屏常量
	public static int SINGLE_SCREEN = 1;
	public static int FOUR_SCREEN = 4;
	public static int NINE_SCREEN = 9;
	public static int SIXTEEN_SCREEN = 16;
	// public static int TWENTY_FIVE_SCREEN = 25;//暂时不支持25屏
	public static ArrayList<Integer> screenList = new ArrayList<Integer>();// 分屏下拉选择列表
	public static int SELECT_SCREEN = 4;// 下拉选择的屏幕分屏
	// public static HashMap<Integer, JVSChannel> channelMap = new
	// HashMap<Integer, JVSChannel>();// 窗口下标，channel对象
	public static int playTag = 1; // 播放tag 1：正常播放 2：演示点播放 3:局域网广播设备连接
	public static boolean BIGSCREEN = false;
	public static int screenWidth = 0;
	public static int screenHeight = 0;
	public static final float playProportion = 0.75f;// 播放比例

	// JVDeviceManageActivity
	// public static ArrayList<Device> broadList = new
	// ArrayList<Device>();//快速观看广播到的设备列表

	public static Device ADDDEVICE = new Device();// 添加设备

	// 以下两个集合必须分开，在播放时有后台广播，广播会修改deviceList数据，如果都用着一个集合播放过程中数据变了会导致播放界面混乱
	public static ArrayList<Device> deviceList = new ArrayList<Device>();// 设备列表数据
	public static ArrayList<Device> tempList = new ArrayList<Device>();// 设备管理列表广播出的新设备临时列表
	public static ArrayList<Device> onLineDeviceList = new ArrayList<Device>();// 在线播放设备列表数据

	public static ArrayList<ConnPoint> allConnPointList = new ArrayList<ConnPoint>();// 所有通道列表

	// JVMoreFeatureActivity
	public static MoreHandler moreHandler;

	// JVRemotePlayBackActivity
	public static ArrayList<RemoteVideo> videoList;
	public static int year = 0;
	public static int month = 0;
	public static int day = 0;

	// JVRemoteSettingActivity
	// 设置map
	public static HashMap<String, String> settingMap = new HashMap<String, String>();
	public static String[] array = null;
	public static String[] array1 = null;
	public static String[] array2 = null;
	public static ArrayList<Wifi> wifiList = new ArrayList<Wifi>();// wifi数据列表

	// LeftFragment
	public static ExpandableListView deviceEListView = null;
	public static ArrayList<Device> leftDeviceList = new ArrayList<Device>();

	public static String publicAccount = "";

	// 新品详情
	public static NewProHandler npHandler;

	// 设备管理修改设备
	public static Device IPCDEVICE = null;// IPC设备快速设置

	public static Device addDeviceMethod(Device device1) {
		// // 用户列表中没有，添加到数据库中，或者提交到服务器
		// if (BaseApp.LOCAL_LOGIN_FLAG) {// 本地将数据添加到数据库中
		// if (null == device1.pointList) {
		// device1.pointList = new ArrayList<ConnPoint>();
		// }
		// if ("".equalsIgnoreCase(device1.deviceName)) {
		// device1.deviceName = device1.deviceNum;
		// }
		//
		// JVConnectInfo jvc = device1.toJVConnectInfo();
		// JVConnectInfo resultInfo = BaseApp.queryChannelByYst(jvc);
		// if (null == resultInfo) {
		// jvc.setParent(true);
		// int res = BaseApp.addItem(jvc);
		//
		// if (res > 0) {
		// ArrayList<JVConnectInfo> infoList = new ArrayList<JVConnectInfo>();
		// for (int k = 0; k < device1.pointList.size(); k++) {
		// // device1.newTag = false;
		// // device1.getGroupYST();
		// // ConnPoint connPoint = new ConnPoint();
		// // connPoint.deviceID = 0;
		// // connPoint.pointNum =
		// // device1.pointList.get(k).pointNum;
		// // connPoint.pointOwner = 0;
		// // connPoint.pointName =
		// // device1.pointList.get(k).pointName;
		// // connPoint.pointOID = 0;
		// // connPoint.ystNum = device1.pointList.get(k).ystNum;
		// // connPoint.group = device1.pointList.get(k).group;
		//
		// JVConnectInfo pointInfo = (JVConnectInfo) jvc.clone();
		// pointInfo.setChannel(k + 1);
		// pointInfo
		// .setNickName(device1.pointList.get(k).pointName);
		// pointInfo.setParent(false);
		// pointInfo.setPrimaryID(System.currentTimeMillis());
		// infoList.add(pointInfo);
		// // BaseApp.addItem(jvc); //添加通道
		// }
		// // 直接添加
		// if (null != infoList && 0 != infoList.size()) {
		// BaseApp.addJVCInfos(infoList);
		// }
		// }
		// }
		// } else {// 发请求添加设备
		// if ("".equalsIgnoreCase(device1.deviceName)) {
		// device1.deviceName = device1.deviceNum;
		// }
		// LoginUtil.deviceName = device1.deviceName;
		// LoginUtil.deviceNum = device1.deviceNum;
		// LoginUtil.deviceLoginUser = device1.deviceLoginUser;
		// LoginUtil.deviceLoginPass = device1.deviceLoginPwd;
		// LoginUtil.deviceHasWifi = String.valueOf(device1.hasWifi);
		//
		// int res = DeviceUtil.addDevice(LoginUtil.userName, device1);
		// // if (null == rDevice) {
		// // rDevice = LoginUtil.createDevice(LoginUtil.deviceName,
		// // LoginUtil.deviceNum, LoginUtil.deviceLoginUser,
		// // LoginUtil.deviceLoginPass, LoginUtil.deviceHasWifi);
		// // }
		// // if (0 == res && null != device1.pointList && 0 !=
		// // device1.pointList.size())
		// if (0 == res) {
		// // device1.newTag = false;
		// // device1.deviceOID = device1.deviceOID;
		// // for (int m = 0; m < device1.pointList.size(); m++) {
		// // if(0 == DeviceUtil.addPoint(device1.deviceNum,
		// // device1.pointList.size())){
		// // device1.pointList =
		// // DeviceUtil.getDevicePointList(device1.deviceNum);
		// // }else{
		// // DeviceUtil.unbindDevice(LoginUtil.userName,
		// // device1.deviceNum);
		// // device1 = null;
		// // }
		// // }
		// // device1.pointList =
		// // DeviceUtil.getDevicePointList(device1.deviceNum);
		// if (0 <= device1.pointList.size()) {
		// if (0 == DeviceUtil.addPoint(device1.deviceNum,
		// device1.pointList.size())) {
		// device1.pointList = DeviceUtil
		// .getDevicePointList(device1.deviceNum);
		// } else {
		// DeviceUtil.unbindDevice(LoginUtil.userName,
		// device1.deviceNum);
		// device1 = null;
		// }
		// }
		// } else {
		// device1 = null;
		// }
		// }
		return device1;
		// BaseApp.deviceList.add(device1);
	}

	// 给通道排序
	public static ArrayList<Device> orderDevice(ArrayList<Device> temList) {
		if (null != temList && 0 != temList.size()) {
			for (int k = 0; k < temList.size(); k++) {
				if (null != temList.get(k) && null != temList.get(k).pointList
						&& 0 != temList.get(k).pointList.size()) {
					// 冒泡排序
					for (int i = 0; i < temList.get(k).pointList.size(); i++) {
						for (int j = i + 1; j < temList.get(k).pointList.size(); j++) {
							if (temList.get(k).pointList.get(j).pointNum < temList
									.get(k).pointList.get(i).pointNum) {
								MyLog.v("交换", "");
								ConnPoint tem = new ConnPoint();
								tem = temList.get(k).pointList.get(i);

								temList.get(k).pointList.set(i,
										temList.get(k).pointList.get(j));
								temList.get(k).pointList.set(j, tem);
							}
						}
					}
				}

			}
		}
		return temList;
	}

	// 广播设备列表初始化参数
	public static void broadDeviceList() {
		// JVSUDT.IS_BROADCASTING = true;
		// JVSUDT.ADD_DEVICE = false;// 添加设备广播
		// JVSUDT.FIVE_BROADCAST_FLAG = false;// 五分钟广播
		// JVSUDT.BROADCAST_DEVICELIST_FLAG = true;// 广播设备列表
		// JVSUDT.BROADCAST_SEARCH_DEVICE_FLAG = false;// 快速观看，发广播搜索局域网设备
		// JVSUDT.BROADCAST_IPC_FLAG = false;// 广播IPC设备IP
	}

	public JSONObject getJSON(String arg1, String arg2, String arg3) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("Type", arg1);
			jsObj.put("MobileID", arg2);
			jsObj.put("AppType", arg3);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsObj;
	}

	// // 将设备保存到本地
	// public static void saveDeviceToLocal(SharedPreferences sharedPreferences,
	// Editor editor) {
	// if (null != sharedPreferences && null != editor) {
	// if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
	// editor.remove("DeviceJSON");
	// editor.commit();
	// JSONArray array = new JSONArray();
	// for (int i = 0; i < BaseApp.deviceList.size(); i++) {
	// Device dev = BaseApp.deviceList.get(i);
	// JSONObject devObj = new JSONObject();
	// try {
	// devObj.put("DeviceNum", dev.deviceNum);
	// devObj.put("DeviceUser", dev.deviceLoginUser);
	// devObj.put("DevicePass", dev.deviceLoginPwd);
	// array.put(i, devObj);
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// MyLog.v("保存到本地的JSON--", array.toString());
	// editor.putString("DeviceJSON", array.toString());
	// editor.commit();
	// }
	// }
	// }

	// 解析域名的IP地址
	public static String getServerIP(String areaName) {
		String areaIP = "";
		InetAddress myServer = null;
		try {
			myServer = InetAddress.getByName(areaName);// "www.jovetech.com");
			if (null != myServer) {
				areaIP = myServer.toString().replace(areaName + "/", "");
				MyLog.v("areaIP---", areaIP + "");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return areaIP;
	}

	// 发广播前初始化参数
	public static void initBroadCast() {
		// JVSUDT.IS_BROADCASTING = true;
		// JVSUDT.ADD_DEVICE = false;// 添加设备广播
		// JVSUDT.FIVE_BROADCAST_FLAG = false;// 五分钟广播
		// JVSUDT.BROADCAST_DEVICELIST_FLAG = false;// 广播设备列表
		// JVSUDT.BROADCAST_SEARCH_DEVICE_FLAG = false;// 快速观看，发广播搜索局域网设备
		// JVSUDT.BROADCAST_IPC_FLAG = false;// 广播IPC设备IP
	}

	// 发广播方法
	public static void sendBroadCast() {
		// JVSUDT.JVC_StartLANSerchServer(9400, 6666);
		// JVSUDT.JVC_MOLANSerchDevice("", 0, 0, 0, "", 2000);// 55296
	}

	/**
	 * 创建文件目录
	 * 
	 * @author suifupeng
	 * @param path
	 *            要创建的目录路径
	 */
	public static boolean createDirectory(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return file.mkdirs();
		}
		return true;
	}

	// // 停止录像
	// public static void stopRecord(JVSChannel jvsChannel) {
	// // // 正在录像暂停录像
	// // if (jvsChannel.isPkt) {
	// // int res = JVSUDT.StopRecordMP4(BaseApp.windowIndex + 1);
	// // MyLog.v("停止录像接口---", res + "");
	// // /* 停止录像接口 */
	// // if (JVSUDT.StopRecordMP4(BaseApp.windowIndex + 1) == 0) {
	// // jvsChannel.isPkt = false;
	// // }
	// // }
	// }

	public static int CURRENT_LAN = -1;

	// 是否接收报警信息
	public static boolean pushMessage = false;

	// /**
	// * 2014-05-10 获取当前语音
	// *
	// * @return
	// */
	// public static int getLan() {
	// if (-1 != CURRENT_LAN) {
	// return CURRENT_LAN;
	// }
	// CURRENT_LAN = JVConst.LANGUAGE_EN;
	// String language = Locale.getDefault().getLanguage();
	// if (language.equalsIgnoreCase("zh")) {// 中文
	// CURRENT_LAN = JVConst.LANGUAGE_ZH;
	// } else {// 英文
	// CURRENT_LAN = JVConst.LANGUAGE_EN;
	// }
	// return CURRENT_LAN;
	// }
	//
	// // 中文 0 英文 1
	// public static int getLanguage() {
	// int lan = 1;
	// String language = Locale.getDefault().getLanguage();
	// if (language.equalsIgnoreCase("zh")) {// 中文
	// lan = 0;
	// } else {// 英文
	// lan = 1;
	// }
	// return lan;
	// }

	// 通用获取编辑器方法
	public static SharedPreferences sharedPreferences = null;
	public static Editor editor = null;

	public static SharedPreferences getSP(Context con) {
		if (null != sharedPreferences) {
			return sharedPreferences;
		}
		sharedPreferences = con.getSharedPreferences("JVCONFIG",
				Context.MODE_PRIVATE);

		return sharedPreferences;
	}

	public static Editor getEditor(Context con) {
		if (null != sharedPreferences && null != editor) {
			return editor;
		}
		getSP(con);
		editor = sharedPreferences.edit();
		return editor;
	}

	/**
	 * 自定义加载progressDialog
	 * 
	 * @author suifupeng
	 * @param context
	 * @param msg
	 *            为null时不显示文字
	 */
	public static Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
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

		loadingDialog.setCancelable(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
		return loadingDialog;

	}

	/**
	 * 获取手机IP
	 * 
	 * @return
	 */
	public static String getIP() {
		String ip = "";
		BufferedReader in = null;
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));
			ip = in.readLine();

		} catch (Exception e) {

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ip;
	}

	/**
	 * 收集手机信息
	 */
	public static String mobileInfo(Context context) {
		String mobileInfo = "";
		try {
			String model = android.os.Build.MODEL;
			String version = android.os.Build.VERSION.RELEASE;
			String fingerprint = android.os.Build.FINGERPRINT;
			String ip = getIP();
			String cpu = Build.CPU_ABI;
			String softwareVersion = context.getResources().getString(
					R.string.app_name)
					+ context.getResources().getString(
							R.string.str_current_version);
			mobileInfo = "--[1-MODEL]=" + model + "--" + "[2-VERSION]="
					+ version + "--" + "[3-FINGERPRINT]=" + fingerprint + "--"
					+ "[4-IP]=" + ip + "--" + "[5-CPU]=" + cpu + "--"
					+ "[6-SOFTVERSION]=" + softwareVersion;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mobileInfo;
	}

	/**
	 * 将设备保存到本地
	 * 
	 * @param sharedPreferences
	 * @param editor
	 * @param localLogin
	 */
	public static void saveDeviceToLocal(SharedPreferences sharedPreferences,
			Editor editor, Boolean localLogin) {
		String saveKey = "";
		// if (localLogin) {// 本地登陆
		// Log.v("本地登陆", "保存到本地");
		// saveKey = "LocalDevice";
		// } else {
		// Log.v("在线登陆", "保存到本地");
		// saveKey = "OnlineDevice";
		// }
		saveKey = "saveDevice";
		if (null != sharedPreferences && null != editor) {
			if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
				editor.remove(saveKey);
				editor.commit();
				JSONArray array = new JSONArray();
				for (int i = 0; i < BaseApp.deviceList.size(); i++) {
					if (i >= 10) {
						break;
					}
					Device dev = BaseApp.deviceList.get(i);
					JSONObject devObj = new JSONObject();
					try {
						devObj.put("DeviceNum", dev.deviceNum);
						devObj.put("DeviceUser", dev.deviceLoginUser);
						devObj.put("DevicePass", dev.deviceLoginPwd);
						array.put(i, devObj);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				MyLog.v("保存的JSON--" + saveKey, array.toString());
				editor.putString(saveKey, array.toString());
				editor.commit();

			}

		}
	}

	/**
	 * 给设备添加小助手
	 * 
	 * @param device
	 */
	public static void setHelpToDevice(Device device) {
		// 设置小助手
		byte allByte[] = new byte[528];
		device.getGroupYST();
		JVLittleTipsPacket packet = new JVLittleTipsPacket(256 * 2 + 16);
		packet.setChGroup(device.group);
		packet.setnYSTNO(device.yst);
		packet.setnChannel(1);
		packet.setChPName(device.deviceLoginUser);
		packet.setChPWord(device.deviceLoginPwd);
		packet.setnConnectStatus(0);
		System.arraycopy(packet.pack().data, 0, allByte, 0, packet.getLen());
		// boolean res = JVSUDT.JVC_SetHelpYSTNO(allByte, allByte.length);
		allByte = null;
	}

	/**
	 * 获取设备小助手状态
	 * 
	 * @param device
	 */
	public static int getHelpToDevice(Device device) {
		// 设置小助手
		byte allByte[] = new byte[528];
		device.getGroupYST();
		JVLittleTipsPacket packet = new JVLittleTipsPacket(256 * 2 + 16);
		packet.setChGroup(device.group);
		packet.setnYSTNO(device.yst);
		packet.setnChannel(1);
		packet.setChPName(device.deviceLoginUser);
		packet.setChPWord(device.deviceLoginPwd);
		packet.setnConnectStatus(0);
		System.arraycopy(packet.pack().data, 0, allByte, 0, packet.getLen());
		int res = 0;// JVSUDT.JVC_GetHelpYSTNO(allByte, allByte.length);
		allByte = null;
		return res;
	}

	/**
	 * 给设备设置小助手
	 * 
	 * @param device
	 */
	public static void setHelp(Device device) {
		// 设置小助手
		byte allByte[] = new byte[528];
		device.getGroupYST();
		JVLittleTipsPacket packet = new JVLittleTipsPacket(256 * 2 + 16);
		packet.setChGroup(device.group);
		packet.setnYSTNO(device.yst);
		packet.setnChannel(1);
		packet.setChPName(device.deviceLoginUser);
		packet.setChPWord(device.deviceLoginPwd);
		packet.setnConnectStatus(0);
		System.arraycopy(packet.pack().data, 0, allByte, 0, packet.getLen());

		MyLog.v("设置小助手--" + device, device.deviceNum);
		// JVSUDT.JVC_SetHelpYSTNO(allByte, allByte.length);
		allByte = null;
	}

	private static boolean flag = true;
	//
	// /**
	// * 判断网络是否真正可用
	// *
	// * @return 是否可用
	// */
	// public static boolean getNetWorkConnection() {
	// flag = false;
	// new Thread() {
	// public void run() {
	// String serverURL = "";
	// if (ConfigUtil.isLanZH()) {
	// serverURL = "http://www.baidu.com/";
	// } else {
	// serverURL = "http://www.google.com/";
	// }
	// HttpGet httpRequest = new HttpGet(serverURL);// 建立http get联机
	// HttpResponse httpResponse;
	// try {
	// httpResponse = new DefaultHttpClient().execute(httpRequest);
	// flag = true;
	// } catch (Exception e) {
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
	// return flag;
	// }
}
