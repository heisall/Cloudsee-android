package com.jovision.activities;

//package com.jovision.activities;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.wifi.WifiConfiguration;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.test.JVSUDT;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.jovetech.CloudSee.temp.R;
//import com.jovision.adapters.WifiAdapter;
//import com.jovision.bean.ConnPoint;
//import com.jovision.bean.Device;
//import com.jovision.bean.JVConnectInfo;
//import com.jovision.bean.JVSChannel;
//import com.jovision.bean.WifiAdmin;
//import com.jovision.commons.BaseApp;
//import com.jovision.commons.JVAccountConst;
//import com.jovision.commons.JVCSocketUDP;
//import com.jovision.commons.JVConst;
//import com.jovision.commons.JVNetConst;
//import com.jovision.commons.MyLog;
//import com.jovision.utils.AccountUtil;
//import com.jovision.utils.DeviceUtil;
//import com.jovision.utils.LoginUtil;
//import com.jovision.views.RefreshableListView;
//import com.jovision.views.RefreshableListView.OnRefreshListener;
//
//public class CopyOfJVQuickSettingActivity extends Activity {
//
//	// private int errorCount = 0;
//
//	private ProgressDialog proDialog = null;
//	private Button back;// 左侧返回按钮
//	private TextView currentMenu;// 当前页面名称
//	private Button connectBtn;
//
//	// 主控wifi信息
//	private ImageView ipcImage;
//	private LinearLayout wifiDetailLayout;
//	private EditText wifiName;
//	private EditText wifiPass;
//	private LinearLayout connectLayout;
//	private ProgressBar loadingWifi;
//
//	int wifiIndex = 0;
//
//	private LinearLayout ipcWifiListLayout;
//	private LinearLayout wifiListLayout;
//
//	private RefreshableListView ipcWifiListView;
//	private RefreshableListView wifiListView;
//
//	// private RefreshableListView wifiListView;
//	private WifiAdapter wifiAdapter;
//	WifiAdmin wifiAdmin;
//
//	private String ipcWifiName;// IPC网络名称
//	// ArrayList<ScanResult> wifiConfigList = new ArrayList<ScanResult>();//
//	// IPC网络列表
//
//	private int TIME_SPAN = 2 * 1000;// 时间间隔
//	private int timerCount = 0;
//	private Timer connectTimer = null;
//	private ConnectTask connectTask = null;
//
//	private boolean reConnect = false;// 重复连接
//	private boolean connecting = false;// 正在连接网络
//	// private String oldWifiSSID = null;// 进程序之前连接的wifi
//	// private boolean oldWifiState = false;//进程序之前wifi的开启状态，原来关闭状态：false
//	// 开启状态：true
//
//	private int connFailedCount = 0;// 连接失败次数
//
//	ConnectivityManager connectivityManager = null;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		LoginUtil.activityList.add(CopyOfJVQuickSettingActivity.this);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.quicksetting_layout);
//
//		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//		initViews();
//
//		JVSUDT.DEVICE_MANAGE_FLAG = false;
//		JVSUDT.QUICK_SETTING_FLAG = false;
//
//		createDialog(R.string.str_quick_setting_alert);
//		// 非本地登录先掉注销
//		Thread logOutThread = new Thread() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				if (!BaseApp.LOCAL_LOGIN_FLAG) {// 非本地登录
//					AccountUtil.userLogout();
//				}
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if (null != BaseApp.quickSettingHandler) {
//					Message msg = BaseApp.quickSettingHandler.obtainMessage();
//					if (null != BaseApp.wifiConfigList
//							&& 0 != BaseApp.wifiConfigList.size()) {
//						msg.what = JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS;
//					} else {
//						msg.what = JVConst.QUICK_SETTING_IPC_WIFI_FAILED;
//					}
//					BaseApp.quickSettingHandler.sendMessage(msg);
//				}
//				super.run();
//			}
//		};
//		logOutThread.start();
//		// if (null != BaseApp.oldWifiSSID) {
//		// MyLog.e("原WIFI-SSID：", BaseApp.oldWifiSSID);
//		// WifiConfiguration oldWifi = wifiAdmin.isExsits(BaseApp.oldWifiSSID);
//		// if (null != oldWifi) {
//		// MyLog.e("关掉原WIFI-SSID：", BaseApp.oldWifiSSID);
//		// wifiAdmin.disconnectWifi(oldWifi, false);// 关掉，不移除
//		// }
//		// }
//		// if (null != BaseApp.quickSettingHandler) {
//		// Message msg = BaseApp.quickSettingHandler.obtainMessage();
//		// if (null != BaseApp.wifiConfigList
//		// && 0 != BaseApp.wifiConfigList.size()) {
//		// msg.what = JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS;
//		// } else {
//		// msg.what = JVConst.QUICK_SETTING_IPC_WIFI_FAILED;
//		// }
//		// BaseApp.quickSettingHandler.sendMessage(msg);
//		// }
//
//	}
//
//	public void initViews() {
//		wifiAdmin = new WifiAdmin(CopyOfJVQuickSettingActivity.this);
//		BaseApp.quickSettingHandler = new QuickSettingHandler(
//				CopyOfJVQuickSettingActivity.this);
//		back = (Button) findViewById(R.id.back);
//		currentMenu = (TextView) findViewById(R.id.currentmenu);
//		currentMenu.setText(R.string.str_quick_setting);
//		back.setOnClickListener(onClickListener);
//		connectBtn = (Button) findViewById(R.id.add_device);
//		connectBtn.setTextColor(Color.WHITE);
//		connectBtn.setBackgroundDrawable(getResources().getDrawable(
//				R.drawable.setting_save));
//		connectBtn.setVisibility(View.VISIBLE);
//		connectBtn.setText(getResources().getString(
//				R.string.str_quick_setting_connect));
//		connectBtn.setOnClickListener(onClickListener);
//		connectBtn.setVisibility(View.GONE);
//		loadingWifi = (ProgressBar) findViewById(R.id.loadingwifi);
//
//		wifiDetailLayout = (LinearLayout) findViewById(R.id.wifiinfolayout);
//		wifiName = (EditText) findViewById(R.id.wifiname);
//		wifiPass = (EditText) findViewById(R.id.wifipwd);
//		connectLayout = (LinearLayout) findViewById(R.id.connectlayout);
//
//		wifiListView = (RefreshableListView) findViewById(R.id.wifilist);
//		wifiListView.setOnItemClickListener(mOnItemClickListener);
//
//		ipcWifiListView = (RefreshableListView) findViewById(R.id.ipcwifilist);
//		ipcWifiListView.setOnItemClickListener(mOnItemClickListener);
//
//		wifiDetailLayout.setVisibility(View.GONE);
//		connectLayout.setVisibility(View.GONE);
//		wifiListLayout = (LinearLayout) findViewById(R.id.wifilistbg);
//		ipcWifiListLayout = (LinearLayout) findViewById(R.id.ipcwifilistbg);
//
//		wifiListLayout.setVisibility(View.GONE);
//		ipcWifiListLayout.setVisibility(View.VISIBLE);
//		// 第一个界面样式
//		ipcWifiListView.setOnRefreshListener(new OnRefreshListener() {
//			@Override
//			public void onRefresh(RefreshableListView listView) {
//				createDialog(R.string.str_quick_setting_alert);
//				GetWifiThread gwt = new GetWifiThread(
//						CopyOfJVQuickSettingActivity.this);
//				gwt.start();
//
//			}
//		});
//
//		// 第 二个界面样式
//		wifiListView.setOnRefreshListener(new OnRefreshListener() {
//			@Override
//			public void onRefresh(RefreshableListView listView) {
//				try {
//					boolean flag = getWifiState(ipcWifiName);
//					if (flag) {
//						loadingWifi.setVisibility(View.VISIBLE);
//						createDialog(R.string.str_quick_setting_alert_4);
//
//						resetConnTimer();
//						MyLog.v("AP功能", "下拉刷新wifi列表");
//						// 连接50秒超时
//
//						connTimer = new Timer();
//						connTimer.schedule(new CoonTask(), 50 * 1000);
//
//						// errorCount = 0;
//						// JVSUDT.sendTextData = false;
//						// //回调函数未过来
//						// while(!JVSUDT.sendTextData){
//						// errorCount++;
//						// JVSUDT.sendTextData = false;
//						// // 获取主控AP信息请求
//						// JVSUDT.JVC_SendTextData(JVConst.AP_CONNECT,
//						// (byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
//						// JVNetConst.JVN_WIFI_INFO);
//						// if(errorCount == 3){
//						// break;
//						// }
//						// try {
//						// Thread.sleep(5*1000);
//						// } catch (InterruptedException e) {
//						// // TODO Auto-generated catch block
//						// e.printStackTrace();
//						// }
//						// }
//
//						// 获取主控AP信息请求
//						JVSUDT.JVC_SendTextData(1,
//								(byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
//								JVNetConst.JVN_WIFI_INFO);
//					} else {
//						wifiDisDialog();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			}
//		});
//
//		ipcImage = new ImageView(CopyOfJVQuickSettingActivity.this);
//		ipcImage.setImageDrawable(getResources().getDrawable(
//				R.drawable.ipc_banner));
//		ipcWifiListView.addHeaderView(ipcImage);
//	}
//
//	// 2秒判断一次网络状态
//	private static class ConnectTask extends TimerTask {
//		private final WeakReference<CopyOfJVQuickSettingActivity> mActivity;
//		private boolean reco = false;
//
//		public ConnectTask(CopyOfJVQuickSettingActivity activity, boolean flag) {// 重复连接标志位
//			mActivity = new WeakReference<CopyOfJVQuickSettingActivity>(activity);
//			reco = flag;
//		}
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//
//			CopyOfJVQuickSettingActivity activity = mActivity.get();
//			if (null != activity) {
//
//				if (null != BaseApp.quickSettingHandler) {
//					Message msg = BaseApp.quickSettingHandler.obtainMessage();
//					activity.reConnect = reco;
//					activity.timerCount = activity.timerCount + 2;// 加2秒
//
//					MyLog.v("正在连接状态：", activity.connecting + "");
//					// 如果正在连接
//					if (activity.connecting) {// 到第20秒和第40秒还没连上再连接两次
//						if (20 == activity.timerCount
//								|| 40 == activity.timerCount) {
//							WifiConfiguration wc = activity.wifiAdmin
//									.CreateWifiInfo(
//											activity.ipcWifiName,
//											activity.getResources().getString(
//													R.string.str_wifi_pwd), 3);
//							boolean res = activity.wifiAdmin
//									.ConnectWifiByConfig(wc);
//							if (res) {
//								activity.connecting = true;
//							}
//						}
//					} else {
//
//						WifiConfiguration wc = activity.wifiAdmin
//								.CreateWifiInfo(
//										activity.ipcWifiName,
//										activity.getResources().getString(
//												R.string.str_wifi_pwd), 3);
//						boolean res = activity.wifiAdmin
//								.ConnectWifiByConfig(wc);
//						if (res) {
//							activity.connecting = true;
//						}
//					}
//					boolean flag = activity.getWifiState(activity.ipcWifiName);
//					MyLog.e("2秒时间到：", "判断网络连接状态：" + flag + "--timer:"
//							+ activity.timerCount);
//					if (activity.timerCount < 70) {
//						if (flag) {// AP连接成功
//							activity.connecting = false;
//							String deviceName = activity.ipcWifiName;
//							String str0 = deviceName.replace("\"", "");
//							String str = str0.substring(6, str0.length());
//							MyLog.v("AP连接成功：", str);
//
//							BaseApp.IPCDEVICE = new Device();
//							BaseApp.IPCDEVICE.deviceNum = str;
//							BaseApp.IPCDEVICE.deviceName = str;
//							BaseApp.IPCDEVICE.getGroupYST();
//							BaseApp.IPCDEVICE.deviceLoginUser = BaseApp.IPC_DEFAULT_USER;
//							BaseApp.IPCDEVICE.deviceLoginPwd = BaseApp.IPC_DEFAULT_PWD;
//							BaseApp.IPCDEVICE.deviceLocalIp = BaseApp.IPC_DEFAULT_IP;
//							BaseApp.IPCDEVICE.deviceLocalPort = BaseApp.IPC_DEFAULT_PORT;
//							activity.connect(BaseApp.IPCDEVICE);
//						} else {
//							MyLog.v("AP连接失败：", activity.timerCount + "");
//						}
//
//					} else {
//
//						MyLog.v("AP连接超时：", activity.timerCount + "");
//						activity.connecting = false;
//						// 超时
//						msg.what = JVConst.QUICK_SETTING_AP_CON_TIMEOUT;
//						BaseApp.quickSettingHandler.sendMessage(msg);
//					}
//
//				}
//
//			}
//
//		}
//
//	}
//
//	// 获取wifi列表线程
//	static class GetWifiThread extends Thread {
//		private final WeakReference<CopyOfJVQuickSettingActivity> mActivity;
//
//		public GetWifiThread(CopyOfJVQuickSettingActivity activity) {
//			mActivity = new WeakReference<CopyOfJVQuickSettingActivity>(activity);
//		}
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			super.run();
//			CopyOfJVQuickSettingActivity activity = mActivity.get();
//			if (null != activity) {
//				boolean openFlag = false;
//
//				if (activity.wifiAdmin.getWifiState()) {// 如果wifi为打开状态无需等待5秒
//					BaseApp.oldWifiState = true;
//					openFlag = true;
//				} else {
//					BaseApp.oldWifiState = false;
//					openFlag = activity.wifiAdmin.openWifi();// 打开wifi需要6秒左右
//
//					boolean state = false;
//
//					while (!state) {
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//						state = activity.wifiAdmin.getWifiState();
//						MyLog.v("while判断网络状态：", state + "");
//					}
//
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				}
//
//				if (openFlag) {
//					BaseApp.wifiConfigList = activity.wifiAdmin.startScan();
//				}
//
//				while (null == BaseApp.wifiConfigList
//						|| 0 == BaseApp.wifiConfigList.size()) {
//					MyLog.v("while判断网络列表：", BaseApp.wifiConfigList.size() + "");
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//
//					BaseApp.wifiConfigList = activity.wifiAdmin.startScan();
//				}
//
//				if (null != BaseApp.quickSettingHandler) {
//					Message msg = BaseApp.quickSettingHandler.obtainMessage();
//					if (null != BaseApp.wifiConfigList
//							&& 0 != BaseApp.wifiConfigList.size()) {
//						msg.what = JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS;
//					} else {
//						msg.what = JVConst.QUICK_SETTING_IPC_WIFI_FAILED;
//					}
//					BaseApp.quickSettingHandler.sendMessage(msg);
//				}
//			}
//		}
//	}
//
//	// // wifi连接线程,wifi连接成功后发广播搜索设备，修改设备IP
//	// static class ConnThread extends Thread {
//	// private final WeakReference<JVQuickSettingActivity> mActivity;
//	// private String wifiName;
//	//
//	// public ConnThread(JVQuickSettingActivity activity, String name) {
//	// mActivity = new WeakReference<JVQuickSettingActivity>(activity);
//	// wifiName = name;
//	// }
//	//
//	// @Override
//	// public void run() {
//	// // TODO Auto-generated method stub
//	// super.run();
//	// JVQuickSettingActivity activity = mActivity.get();
//	// if (null != activity) {
//	// MyLog.v("连接的wifi名字", wifiName);
//	// // http://lszdb1983.blog.163.com/blog/static/20426348201241175717181/
//	// activity.ipcWifiName = wifiName;
//	// WifiConfiguration wc = activity.wifiAdmin.CreateWifiInfo(
//	// wifiName,
//	// activity.getResources()
//	// .getString(R.string.str_wifi_pwd), 3);
//	// activity.wifiAdmin.ConnectWifiByConfig(wc);
//	// boolean flag = activity.getWifiState();
//	//
//	// // 无线连接成功
//	// if (flag) {
//	// String str0 = wifiName.replace("\"", "");
//	// String str = str0.substring(6, str0.length());
//	// Log.i("取出来的设备名：", str);
//	// BaseApp.IPCDEVICE = new Device();
//	// BaseApp.IPCDEVICE.deviceNum = str;
//	// BaseApp.IPCDEVICE.deviceName = str;
//	// BaseApp.IPCDEVICE.getGroupYST();
//	// BaseApp.IPCDEVICE.deviceLoginUser = activity.IPC_DEFAULT_USER;
//	// BaseApp.IPCDEVICE.deviceLoginPwd = activity.IPC_DEFAULT_PWD;
//	// BaseApp.IPCDEVICE.deviceLocalIp = activity.IPC_DEFAULT_IP;
//	// BaseApp.IPCDEVICE.deviceLocalPort = activity.IPC_DEFAULT_PORT;
//	//
//	// activity.connect(BaseApp.IPCDEVICE);
//	// // //发广播搜索设备ip
//	// // activity.broadCast();
//	// } else {
//	// Message msg = BaseApp.quickSettingHandler.obtainMessage();
//	// msg.what = JVConst.QUICK_SETTING_IPC_WIFI_CON_FAILED;
//	// BaseApp.quickSettingHandler.sendMessage(msg);
//	// }
//	//
//	// }
//	// }
//	// }
//
//	// // 判断网络连接状态，wifiadmin不准确
//	// public boolean getWifiState(String wifiName) {
//	// boolean flag = false;
//	// if (null == connectivityManager) {
//	// connectivityManager = (ConnectivityManager)
//	// getSystemService(Context.CONNECTIVITY_SERVICE);
//	// }
//	// NetworkInfo net = connectivityManager.getActiveNetworkInfo();
//	// if (net == null) {
//	// Log.i("test", "无网络连接");
//	// } else {
//	// Log.i("test", "网络连接类型为" + net.getTypeName());
//	// Log.i("test", "网络连接状态为" + net.getState().name());
//	// if ("CONNECTED".equalsIgnoreCase(net.getState().name())) {
//	// // 防止连上wifi，但不是连的IPCwifi
//	// Log.i("test", "wifiAdmin.getSSID()--" + wifiAdmin.getSSID());
//	// Log.i("test", "ipcWifiName-----------" + ipcWifiName);
//	//
//	// if (null != wifiAdmin.getSSID()) {
//	// String str1 = wifiAdmin.getSSID().replace("\"", "");
//	// String str2 = wifiName.replace("\"", "");
//	//
//	// if (str1.equalsIgnoreCase(str2)) {
//	// flag = true;
//	// }
//	// }
//	//
//	// }
//	// }
//	//
//	// return flag;
//	// }
//
//	// 判断网络连接状态，wifiadmin不准确
//	public boolean getWifiState(String wifiName) {
//		boolean flag = false;
//		try {
//			if (null == connectivityManager) {
//				connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//			}
//			NetworkInfo net = connectivityManager.getActiveNetworkInfo();
//			if (net == null) {
//				MyLog.v("test", "无网络连接");
//			} else {
//				MyLog.v("test", "网络连接类型为" + net.getTypeName());
//				MyLog.v("test", "网络连接状态为" + net.getState().name());
//				if ("CONNECTED".equalsIgnoreCase(net.getState().name())) {
//					// 防止连上wifi，但不是连的IPCwifi
//					MyLog.v("test",
//							"wifiAdmin.getSSID()--" + wifiAdmin.getSSID());
//					MyLog.v("test", "ipcWifiName-----------" + ipcWifiName);
//
//					if (null != wifiAdmin && null != wifiAdmin.getSSID()
//							&& !"".equalsIgnoreCase(wifiAdmin.getSSID().trim())) {
//						String str1 = wifiAdmin.getSSID().replace("\"", "");
//						String str2 = wifiName.replace("\"", "");
//
//						if (str1.equalsIgnoreCase(str2)) {
//							flag = true;
//						} else {
//							MyLog.e("原WIFI-SSID：", str1);
//							WifiConfiguration oldWifi = wifiAdmin
//									.isExsits(str1);
//							if (null != oldWifi) {
//								MyLog.e("关掉原不移除WIFI-SSID：", str1);
//								wifiAdmin.disconnectWifi(oldWifi, false);// 关掉，不移除
//							}
//						}
//					}
//
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return flag;
//	}
//
//	// //发广播搜索IP
//	// public void broadCast(){
//	// resetTimer();
//	// JVSUDT.FIVE_BROADCAST_FLAG = false;
//	// JVSUDT.BROADCAST_DEVICELIST_FLAG = false;
//	// JVSUDT.ADD_DEVICE = false;
//	// JVSUDT.BROADCAST_SEARCH_DEVICE_FLAG = false;
//	// JVSUDT.IS_BROADCASTING = true;
//	// JVSUDT.BROADCAST_IPC_FLAG = true;
//	// JVSUDT.JVC_StartLANSerchServer(9400, 6666);
//	// JVSUDT.JVC_LANSerchDevice("", 0, 0, 0, "", 2000);// 55296
//	// }
//
//	// 连接任务
//	class CoonTask extends TimerTask {
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//
//			if (null != BaseApp.quickSettingHandler) {
//				Message msg = BaseApp.quickSettingHandler.obtainMessage();
//				msg.what = JVConst.QUICK_SETTING_HOST_WIFI_TEXT_TIMEOUT;
//				BaseApp.quickSettingHandler.sendMessage(msg);
//			}
//		}
//
//	}
//
//	private Timer connTimer = null;
//	private CoonTask coonTask = null;
//
//	// 连接方法
//	private void connect(Device devs) {
//		resetTimer();
//		resetConnTimer();
//		MyLog.v("AP功能", "调用视频连接");
//		// 连接50秒超时
//		try {
//			connTimer = new Timer();
//			connTimer.schedule(new CoonTask(), 50 * 1000);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		JVSUDT.QUICK_SETTING_FLAG = true;
//		// TODO Auto-generated method stub
//		JVConnectInfo info = new JVConnectInfo();
//		info.setAction(false);
//		// info.setBackLight(false);
//		info.setByUDP(true);
//		info.setCsNumber(devs.yst);
//		// info.setDeleNum("-1,");
//		info.setChannel(1);
//		// info.setHaveSearchChannel(1);
//		info.setRemoteIp(devs.deviceLocalIp);
//		info.setPort(devs.deviceLocalPort);
//		// info.setSavePasswd(true);
//		// info.setChannelCount(-1);
//		info.setConnType(JVConst.JV_CONNECT_CS);
//		// info.setChannelRealCount(-1);
//		info.setGroup(devs.group);
//		info.setLocalTry(true);
//		info.setSrcName("1");
//		info.setUserName(devs.deviceLoginUser);
//		info.setPasswd(devs.deviceLoginPwd);
//
//		if (null == BaseApp.channelMap) {
//			BaseApp.channelMap = new HashMap<Integer, JVSChannel>();
//		}
//
//		JVCSocketUDP cs = null;
//		try {
//			cs = new JVCSocketUDP(CopyOfJVQuickSettingActivity.this,
//					JVConst.URL_HEADER + info.getGroup() + JVConst.URL_TRAIL,
//					null, 0, info);//
//			cs.setMyInfo(info);
//			cs.setConncting(true);
//			cs.sendMessage = false;
//			BaseApp.channelMap.put(0, cs);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (!"127.0.0.1".equals(info.getRemoteIp())
//				&& !"".equals(info.getRemoteIp())) {
//			MyLog.v("tags ............IP直连", ",windowIndex:" + 0
//					+ ",info.getChannel():" + info.getChannel()
//					+ ",info.getRemoteIp():" + info.getRemoteIp()
//					+ ",info.getPort():" + info.getPort()
//					+ ",info.getUserName():" + info.getUserName()
//					+ ",info.getPasswd():" + info.getPasswd()
//					+ ",info.getGroup():" + info.getGroup()
//					+ ",info.isLocalTry():" + info.isLocalTry()
//					+ ",JVConst.JVN_TRYTURN:" + JVConst.JVN_TRYTURN);
//			synchronized (JVSUDT.disconnect) {
//				JVSUDT.JVC_Connect(1, info.getChannel(), info.getRemoteIp(),
//						info.getPort(), info.getUserName(), info.getPasswd(),
//						-1, info.getGroup().toUpperCase(), info.isLocalTry(),
//						JVConst.JVN_TRYTURN, true, JVNetConst.TYPE_3GMO_UDP,
//						null);
//			}
//
//		} else if (info.getConnType() == JVConst.JV_CONNECT_CS) {// 通过号码连接
//			MyLog.v("tags............云视通号连接", "csNumber=" + info.getCsNumber()
//					+ ",channel=" + info.getChannel());
//			synchronized (JVSUDT.disconnect) {
//				JVSUDT.JVC_Connect(1, info.getChannel(), "", info.getPort(),
//						info.getUserName(), info.getPasswd(),
//						info.getCsNumber(), info.getGroup().toUpperCase(),
//						info.isLocalTry(), JVConst.JVN_TRYTURN, true,
//						JVNetConst.TYPE_3GMO_UDP, null);
//				MyLog.v("clientSize", "connect----------------");
//			}
//		}
//
//	}
//
//	// wifi列表点击事件
//	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// TODO Auto-generated method stub
//
//			if (wifiDetailLayout.getVisibility() == View.GONE) {// IPC网络
//				if (arg2 <= 0) {
//					return;
//				}
//				if (null != BaseApp.wifiConfigList
//						&& 0 != BaseApp.wifiConfigList.size()
//						&& arg2 - 1 < BaseApp.wifiConfigList.size()) {
//
//					createDialog(R.string.str_quick_setting_alert_1);
//
//					ipcWifiName = BaseApp.wifiConfigList.get(arg2 - 1).SSID
//							.replace("\"", "");
//					try {
//						connectTimer = new Timer();
//						connectTask = new ConnectTask(
//								CopyOfJVQuickSettingActivity.this, false);
//						connectTimer.schedule(connectTask, 0, TIME_SPAN);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			} else {// 主控wifi信息
//				if (null != BaseApp.wifiList && 0 != BaseApp.wifiList.size()
//						&& arg2 < BaseApp.wifiList.size()) {
//					wifiIndex = arg2;
//					wifiName.setText(BaseApp.wifiList.get(arg2).wifiUserName);
//				}
//			}
//		}
//
//	};
//	// onclick事件
//	OnClickListener onClickListener = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.back:
//				backMethod();
//				break;
//			case R.id.add_device:
//				resetConnTimer();
//				if ("".equalsIgnoreCase(wifiName.getText().toString().trim())) {
//					BaseApp.showTextToast(CopyOfJVQuickSettingActivity.this,
//							R.string.str_wifiname_not_null);
//				} else {
//
//					MyLog.v("重复连接标志位：", reConnect + "");
//
//					createDialog(R.string.connecting1);
//
//					// 非重复连接
//					if (!reConnect) {
//						String auth = "";
//						String enc = "";
//						String wifiname = wifiName.getText().toString();
//						String wifipwd = wifiPass.getText().toString();
//
//						if (null != BaseApp.wifiList
//								&& 0 != BaseApp.wifiList.size()
//								&& wifiIndex < BaseApp.wifiList.size()) {
//							auth = String.valueOf(BaseApp.wifiList
//									.get(wifiIndex).wifiAuth);
//							enc = String.valueOf(BaseApp.wifiList
//									.get(wifiIndex).wifiEnc);
//						}
//						// Toast.makeText(JVQuickSettingActivity.this,
//						// "auth:"+auth + "---enc:"+enc, 6000).show();
//
//						MyLog.v("保存wifi信息：", "auth:" + auth + "---enc:" + enc);
//
//						JVSUDT.JVC_SaveWifi(1,
//								(byte) JVNetConst.JVN_RSP_TEXTDATA, wifiname,
//								wifipwd, 2, 9, auth, enc);
//
//						try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						// 设置wifi完断开连接
//						// 断开连接
//						disConnectVideo();
//						// 主动断开手机端的wifi
//						WifiConfiguration wc = wifiAdmin
//								.CreateWifiInfo(ipcWifiName, getResources()
//										.getString(R.string.str_wifi_pwd), 3);
//
//						wifiAdmin.disconnectWifi(wc, false);
//					}
//					try {
//						connectTimer = new Timer();
//						connectTask = new ConnectTask(
//								CopyOfJVQuickSettingActivity.this, true);// 重复连接
//						connectTimer
//								.schedule(connectTask, TIME_SPAN, TIME_SPAN);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//
//				break;
//			}
//		}
//	};
//
//	// 返回方法
//	public void backMethod() {
//		try {
//			if (wifiDetailLayout.getVisibility() == View.GONE) {// 显示IPC网络信息列表（一级级wifi列表）
//				createDialog(R.string.str_quick_setting_alert_6);
//				BaseApp.finishSetting = true;
//				ChangeWifiThread cwt = new ChangeWifiThread(
//						CopyOfJVQuickSettingActivity.this, false);
//				cwt.start();
//			} else {// 显示主控wifi信息列表（二级wifi列表）
//				ipcImage = new ImageView(CopyOfJVQuickSettingActivity.this);
//				ipcImage.setImageDrawable(getResources().getDrawable(
//						R.drawable.ipc_banner));
//				ipcImage.setVisibility(View.VISIBLE);
//
//				wifiName.setText("");
//				wifiPass.setText("");
//				resetTimer();
//				resetConnTimer();
//				reConnect = false;
//				connectLayout.setVisibility(View.GONE);
//				wifiDetailLayout.setVisibility(View.GONE);
//				// 显示第一个界面IPC样式
//
//				connectBtn.setVisibility(View.GONE);
//				wifiListLayout.setVisibility(View.GONE);
//				ipcWifiListLayout.setVisibility(View.VISIBLE);
//				wifiAdapter.setData2(BaseApp.wifiConfigList, 2);
//				ipcWifiListView.setAdapter(wifiAdapter);
//				wifiAdapter.notifyDataSetChanged();
//				// 断开连接
//				disConnectVideo();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	// 断开视频连接
//	public void disConnectVideo() {
//		if (null != BaseApp.channelMap && BaseApp.channelMap.size() > 0) {
//			BaseApp.channelMap.get(0).stopPrimaryChannel(0);
//		}
//	}
//
//	public static class QuickSettingHandler extends Handler {
//		private final WeakReference<CopyOfJVQuickSettingActivity> mActivity;
//
//		public QuickSettingHandler(CopyOfJVQuickSettingActivity activity) {
//			mActivity = new WeakReference<CopyOfJVQuickSettingActivity>(activity);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			final CopyOfJVQuickSettingActivity activity = mActivity.get();
//
//			if (null != activity && !activity.isFinishing()) {
//				switch (msg.what) {
//				case JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS:
//					activity.ipcWifiListView.completeRefreshing();
//					activity.dismissDialog(activity.proDialog);
//					// 只取出IPC路由
//					if (null != BaseApp.wifiConfigList
//							&& 0 != BaseApp.wifiConfigList.size()) {
//						for (int i = 0; i < BaseApp.wifiConfigList.size(); i++) {
//
//							String name = BaseApp.wifiConfigList.get(i).SSID
//									.replace("\"", "");
//
//							if (!name.startsWith(BaseApp.IPC_FLAG)) {
//								BaseApp.wifiConfigList.remove(i);
//								i--;
//							}
//						}
//					}
//
//					activity.wifiAdapter = new WifiAdapter(activity);
//					activity.wifiAdapter.setData2(BaseApp.wifiConfigList, 2);
//					activity.ipcWifiListView.setAdapter(activity.wifiAdapter);
//
//					break;
//				case JVConst.QUICK_SETTING_IPC_WIFI_FAILED:
//					activity.ipcWifiListView.completeRefreshing();
//					activity.dismissDialog(activity.proDialog);
//					activity.finish();
//					break;
//				case JVConst.QUICK_SETTING_IPC_WIFI_CON_SUCCESS:// ipcwifi连接成功，
//
//					break;
//				case JVConst.QUICK_SETTING_IPC_WIFI_CON_FAILED:// ipcwifi连接失败，
//					activity.resetTimer();
//					activity.resetConnTimer();
//					activity.dismissDialog(activity.proDialog);
//					AlertDialog.Builder builder = new AlertDialog.Builder(
//							activity);
//
//					builder.setTitle(R.string.tips);
//					builder.setMessage(R.string.str_quick_setting_ipc_net_failed);
//
//					builder.setPositiveButton(R.string.str_sure,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									if (null != activity
//											&& !activity.isFinishing()
//											&& null != dialog) {
//										dialog.dismiss();
//									}
//								}
//							});
//					if (null != activity && !activity.isFinishing()) {
//						builder.create().show();
//					}
//					break;
//				case JVConst.QUICK_SETTING_CONNECT_FAILED:// 快速设置设备连接失败,失败一次再连接一次，直到第十次失败停止
//					// 停止Ap网络连接判断
//					activity.resetTimer();
//					MyLog.v("快速设置设备连接失败", "第-----" + activity.connFailedCount
//							+ "-----次");
//					if (activity.connFailedCount < 10) {
//						activity.connFailedCount++;
//						try {
//							Thread.sleep(2000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						activity.connect(BaseApp.IPCDEVICE);
//					} else {
//						activity.connFailedCount = 0;// 连接失败次数复位
//						activity.dismissDialog(activity.proDialog);
//						AlertDialog.Builder builder1 = new AlertDialog.Builder(
//								activity);
//
//						builder1.setTitle(R.string.tips);
//						builder1.setMessage(R.string.str_host_connect_failed);
//
//						builder1.setPositiveButton(R.string.str_sure,
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										if (null != activity
//												&& !activity.isFinishing()
//												&& null != dialog) {
//											dialog.dismiss();
//										}
//									}
//								});
//						if (null != activity && !activity.isFinishing()) {
//							builder1.create().show();
//						}
//					}
//
//					break;
//				case JVConst.QUICK_SETTING_IS_IPC:
//					activity.connFailedCount = 0;// 连接失败次数复位
//					// MyLog.e("连接成功发暂停命令：", "JVN_CMD_VIDEOPAUSE");
//					// JVSUDT.JVC_SendData(1,
//					// (byte)JVNetConst.JVN_CMD_VIDEOPAUSE, new byte[0], 0);
//
//					activity.proDialog.setMessage(activity.getResources()
//							.getString(R.string.str_quick_setting_alert_3));
//					MyLog.e("是IPC-----------", "是IPC，发聊天请求");
//
//					// activity.errorCount = 0;
//					// JVSUDT.sendTextData = false;
//					// //回调函数未过来
//					// while(!JVSUDT.sendTextData){
//					// activity.errorCount++;
//					// JVSUDT.sendTextData = false;
//					// JVSUDT.JVC_SendData(JVConst.AP_CONNECT,
//					// (byte) JVNetConst.JVN_REQ_TEXT, new byte[0], 8);// new
//					// // byte[0]
//					// if(activity.errorCount == 3){
//					// break;
//					// }
//					// try {
//					// Thread.sleep(5*1000);
//					// } catch (InterruptedException e) {
//					// // TODO Auto-generated catch block
//					// e.printStackTrace();
//					// }
//					// }
//
//					JVSUDT.JVC_SendData(1, (byte) JVNetConst.JVN_REQ_TEXT,
//							new byte[0], 8);// new
//											// byte[0]
//					break;
//				case JVConst.QUICK_SETTING_HOST_AGREE_TEXT:// 主控同意文本聊天
//					MyLog.e("主控同意文本聊天-----------", "主控同意文本聊天");
//					try {
//						Thread.sleep(500);
//					} catch (Exception e) {
//						// TODO: handle exception
//					}
//					if (activity.reConnect) {
//						activity.proDialog.setMessage(activity.getResources()
//								.getString(R.string.str_quick_setting_alert_5));
//						// activity.proDialog.dismiss();
//						// 获取主控wifi配置结果
//						JVSUDT.JVC_SetWifi(1,
//								(byte) JVNetConst.JVN_RSP_TEXTDATA,
//								activity.ipcWifiName, activity.getResources()
//										.getString(R.string.str_wifi_pwd), 2,
//								10);
//					} else {
//						activity.proDialog.setMessage(activity.getResources()
//								.getString(R.string.str_quick_setting_alert_4));
//
//						// activity.errorCount = 0;
//						// JVSUDT.sendTextData = false;
//						// //回调函数未过来
//						// while(!JVSUDT.sendTextData){
//						// activity.errorCount++;
//						// JVSUDT.sendTextData = false;
//						// // 获取主控AP信息请求
//						// JVSUDT.JVC_SendTextData(JVConst.AP_CONNECT,
//						// (byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
//						// JVNetConst.JVN_WIFI_INFO);
//						// if(activity.errorCount == 3){
//						// break;
//						// }
//						// try {
//						// Thread.sleep(5*1000);
//						// } catch (InterruptedException e) {
//						// // TODO Auto-generated catch block
//						// e.printStackTrace();
//						// }
//						// }
//
//						// 获取主控AP信息请求
//						JVSUDT.JVC_SendTextData(1,
//								(byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
//								JVNetConst.JVN_WIFI_INFO);
//					}
//
//					break;
//				case JVConst.QUICK_SETTING_HOST_NOT_AGREE_TEXT:// 快速设置主控不同意聊天
//					activity.resetTimer();
//					activity.resetConnTimer();
//					activity.dismissDialog(activity.proDialog);
//					// 主控不同意断开连接
//					// 断开连接
//					activity.disConnectVideo();
//					BaseApp.showTextToast(activity,
//							R.string.str_only_administator_use_this_function);
//					break;
//				case JVConst.QUICK_SETTING_HOST_WIFI_TEXT:// 快速设置获取主控WIFI信息数据
//					activity.resetConnTimer();
//					activity.loadingWifi.setVisibility(View.GONE);
//					activity.connectLayout.setVisibility(View.VISIBLE);
//					activity.wifiDetailLayout.setVisibility(View.VISIBLE);
//
//					activity.wifiListLayout.setVisibility(View.VISIBLE);
//					activity.ipcWifiListLayout.setVisibility(View.GONE);
//
//					// 第二个界面样式
//					activity.connectBtn.setVisibility(View.VISIBLE);
//					activity.ipcImage = null;
//					activity.wifiAdapter = new WifiAdapter(activity);
//					activity.wifiAdapter.setData1(BaseApp.wifiList, 1, false);
//					activity.wifiListView.setAdapter(activity.wifiAdapter);
//					activity.dismissDialog(activity.proDialog);
//					activity.wifiListView.completeRefreshing();
//					break;
//
//				case JVConst.QUICK_SETTING_HOST_WIFI_TEXT_TIMEOUT:// 快速设置获取主控WIFI信息数据超时
//
//					activity.disConnectVideo();
//					activity.resetConnTimer();
//					activity.dismissDialog(activity.proDialog);
//
//					if (activity.reConnect) {
//						BaseApp.showTextToast(activity,
//								R.string.str_quick_setting_failed);
//					} else {
//						BaseApp.showTextToast(activity,
//								R.string.str_quick_setting_alert_7);
//					}
//
//					break;
//				case JVConst.QUICK_SETTING_IS_NOT_IPC:// 快速设置不是ipc
//					activity.resetTimer();
//					activity.resetConnTimer();
//					activity.dismissDialog(activity.proDialog);
//					JVSUDT.QUICK_SETTING_FLAG = false;
//
//					// 断开连接
//					activity.disConnectVideo();
//					AlertDialog.Builder builder2 = new AlertDialog.Builder(
//							activity);
//
//					builder2.setTitle(R.string.tips);
//					builder2.setMessage(R.string.str_not_support_this_device);
//					builder2.setNegativeButton(R.string.str_cancel,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									if (null != activity
//											&& !activity.isFinishing()
//											&& null != dialog) {
//										dialog.dismiss();
//									}
//								}
//							});
//					if (null != activity && !activity.isFinishing()) {
//						builder2.create().show();
//					}
//
//					break;
//				// case JVConst.QUICK_SETTING_IPC_BROAD_SUCCESS://广播IPC设备IP成功
//				// activity.proDialog.setMessage(activity.getResources().getString(R.string.str_quick_setting_alert_2));
//				// //连接
//				// JVSUDT.QUICK_SETTING_FLAG = true;
//				// activity.connect(BaseApp.IPCDEVICE);
//				//
//				// break;
//				// case JVConst.QUICK_SETTING_IPC_BROAD_FAILED://广播IPC设备IP失败
//				// if(null != activity.proDialog &&
//				// activity.proDialog.isShowing()){
//				// activity.proDialog.dismiss();
//				// }
//				// JVSUDT.IS_BROADCASTING = false;
//				// JVSUDT.BROADCAST_IPC_FLAG = false;
//				// AlertDialog.Builder builder3 = new AlertDialog.Builder(
//				// activity);
//				//
//				// builder3.setTitle(R.string.tips);
//				// builder3.setMessage(R.string.str_host_connect_failed);
//				//
//				// builder3.setPositiveButton(R.string.str_sure,
//				// new DialogInterface.OnClickListener() {
//				// @Override
//				// public void onClick(DialogInterface dialog, int which) {
//				// dialog.dismiss();
//				// }
//				// });
//				//
//				// builder3.create().show();
//				// break;
//				case JVConst.QUICK_SETTING_AP_CON_FAILED:// ipc的路由连接成功
//					activity.resetTimer();
//
//					activity.dismissDialog(activity.proDialog);
//					BaseApp.showTextToast(activity,
//							R.string.str_quick_setting_ap_net_failed);
//					break;
//				case JVConst.QUICK_SETTING_AP_CON_TIMEOUT:// ipc的路由连接超时
//					activity.resetTimer();
//					activity.resetConnTimer();
//					activity.dismissDialog(activity.proDialog);
//					BaseApp.showTextToast(activity,
//							R.string.str_quick_setting_ap_net_timeout);
//					break;
//				case JVConst.QUICK_SETTING_HOST_SET_WIFI_SUCC:// 主控wifi配置成功
//					if (activity.reConnect) {
//						activity.dismissDialog(activity.proDialog);
//					}
//					// 断开连接
//					activity.disConnectVideo();
//					activity.reConnect = false;
//					JVSUDT.QUICK_SETTING_FLAG = false;
//
//					AlertDialog.Builder builder4 = new AlertDialog.Builder(
//							activity);
//
//					builder4.setTitle(R.string.tips);
//					builder4.setMessage(R.string.str_quick_setting_success);
//
//					// 本地登录才有继续配置
//					if (BaseApp.LOCAL_LOGIN_FLAG) {
//						builder4.setPositiveButton(
//								R.string.str_quick_setting_goon,
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										Message msg = BaseApp.quickSettingHandler
//												.obtainMessage();
//										msg.what = JVConst.QUICK_SETTING_WIFI_CONTINUE_SET;
//										BaseApp.quickSettingHandler
//												.sendMessage(msg);
//										if (null != activity
//												&& !activity.isFinishing()
//												&& null != dialog) {
//											dialog.dismiss();
//										}
//									}
//								});
//					}
//
//					builder4.setNegativeButton(
//							R.string.str_quick_setting_finish,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//
//									Message msg = BaseApp.quickSettingHandler
//											.obtainMessage();
//									msg.what = JVConst.QUICK_SETTING_WIFI_FINISH_SET;
//									BaseApp.quickSettingHandler
//											.sendMessage(msg);
//									if (null != activity
//											&& !activity.isFinishing()
//											&& null != dialog) {
//										dialog.dismiss();
//									}
//								}
//							});
//					if (null != activity && !activity.isFinishing()) {
//						AlertDialog dialog4 = builder4.create();
//						dialog4.setCancelable(false);
//						dialog4.show();
//					}
//					break;
//				case JVConst.QUICK_SETTING_HOST_SET_WIFI_FAIL:// 主控配置wifi失败
//					if (activity.reConnect) {
//						activity.dismissDialog(activity.proDialog);
//					}
//					// 断开连接
//					activity.disConnectVideo();
//					activity.reConnect = false;
//					JVSUDT.QUICK_SETTING_FLAG = false;
//
//					AlertDialog.Builder builder5 = new AlertDialog.Builder(
//							activity);
//
//					builder5.setTitle(R.string.tips);
//					builder5.setMessage(R.string.str_quick_setting_failed);
//
//					builder5.setPositiveButton(
//							R.string.str_quick_setting_reset,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									Message msg = BaseApp.quickSettingHandler
//											.obtainMessage();
//									msg.what = JVConst.QUICK_SETTING_WIFI_CONTINUE_SET;
//									BaseApp.quickSettingHandler
//											.sendMessage(msg);
//									if (null != activity
//											&& !activity.isFinishing()
//											&& null != dialog) {
//										dialog.dismiss();
//									}
//								}
//							});
//					builder5.setNegativeButton(
//							R.string.str_quick_setting_finish,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//
//									Message msg = BaseApp.quickSettingHandler
//											.obtainMessage();
//									msg.what = JVConst.QUICK_SETTING_WIFI_FINISH_SET;
//									BaseApp.quickSettingHandler
//											.sendMessage(msg);
//									if (null != activity
//											&& !activity.isFinishing()
//											&& null != dialog) {
//										dialog.dismiss();
//									}
//								}
//							});
//					if (null != activity && !activity.isFinishing()) {
//						AlertDialog dialog5 = builder5.create();
//						dialog5.setCancelable(false);
//						dialog5.show();
//					}
//					break;
//				case JVConst.QUICK_SETTING_HOST_SETTING_WIFI:// 主控正在配置wifi
//					MyLog.v("WIFI正在配置", "休息两秒继续获取配置结果");
//					// 休息两秒继续获取配置结果
//					try {
//						Thread.sleep(2000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					// 获取主控wifi配置结果
//					JVSUDT.JVC_SetWifi(1, (byte) JVNetConst.JVN_RSP_TEXTDATA,
//							activity.ipcWifiName, activity.getResources()
//									.getString(R.string.str_wifi_pwd), 2, 10);
//					break;
//				case JVConst.QUICK_SETTING_WIFI_CONTINUE_SET:// 继续配置wifi
//					// 添加设备线程
//					Thread thread1 = new Thread() {
//						public void run() {
//							if (!BaseApp.LOCAL_LOGIN_FLAG
//									&& BaseApp.deviceList.size() >= 100) {
//								Message msg = BaseApp.quickSettingHandler
//										.obtainMessage();
//								msg.what = JVConst.DEVICE_MOST_COUNT;
//								BaseApp.quickSettingHandler.sendMessage(msg);
//							} else {
//								activity.addDevice();
//							}
//						}
//					};
//					thread1.start();
//
//					activity.backMethod();
//					break;
//				case JVConst.QUICK_SETTING_WIFI_FINISH_SET:// 完成配置wifi
//					BaseApp.finishSetting = true;
//					activity.createDialog(R.string.str_quick_setting_alert_6);
//					activity.resetConnTimer();
//					ChangeWifiThread cwt = new ChangeWifiThread(activity, true);
//					cwt.start();
//					break;
//				case JVConst.QUICK_SETTING_WIFI_CHANGED_SUCC:
//
//					BaseApp.showTextToast(activity,
//							R.string.str_wifi_reset_succ);
//					activity.dismissDialog(activity.proDialog);
//					activity.finish();
//					break;
//				case JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL:
//					BaseApp.showTextToast(activity,
//							R.string.str_wifi_reset_fail);
//
//					activity.dismissDialog(activity.proDialog);
//					if (!BaseApp.LOCAL_LOGIN_FLAG) {
//						if (null != LoginUtil.activityList
//								&& 0 != LoginUtil.activityList.size()) {
//							for (int i = 0; i < LoginUtil.activityList.size(); i++) {
//								if (!LoginUtil.activityList.get(i).equals(
//										activity)) {// 非当前activity
//									LoginUtil.activityList.get(i).finish();
//								}
//							}
//						}
//						Intent intent = new Intent(activity,
//								JVLoginActivity.class);
//						activity.startActivity(intent);
//					}
//					activity.finish();
//					break;
//				case JVConst.DEVICE_MOST_COUNT:
//					BaseApp.showTextToast(activity,
//							R.string.str_device_most_count);
//					break;
//				}
//				super.handleMessage(msg);
//			}
//
//		}
//
//	}
//
//	// 添加设备
//	public void addDevice() {
//		// 判断一下是否已存在列表中
//		boolean find = false;
//		if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
//			for (int i = 0; i < BaseApp.deviceList.size(); i++) {
//				if (BaseApp.IPCDEVICE.deviceNum
//						.equalsIgnoreCase(BaseApp.deviceList.get(i).deviceNum)) {
//					find = true;
//					break;
//				}
//			}
//		}
//		if (!find) {
//			BaseApp.IPCDEVICE.getGroupYST();
//			String group = BaseApp.IPCDEVICE.group;
//			int deviceYSTNO = BaseApp.IPCDEVICE.yst;
//			if (!BaseApp.LOCAL_LOGIN_FLAG) {
//				int resultount = JVSUDT.JVC_WANGetChannelCount(group,
//						deviceYSTNO, 5);
//				if (resultount > 0) {// 服务器返回通道数量
//					BaseApp.IPCDEVICE.devicePointCount = resultount;
//				} else {
//					BaseApp.IPCDEVICE.devicePointCount = 4;
//				}
//			} else {
//				if (BaseApp.isConnected(CopyOfJVQuickSettingActivity.this)) {
//					int count = JVSUDT.JVC_WANGetChannelCount(group,
//							deviceYSTNO, 5);
//					if (count > 0) {
//						BaseApp.IPCDEVICE.devicePointCount = count;
//					} else {
//						BaseApp.IPCDEVICE.devicePointCount = 4;
//					}
//				} else {
//					BaseApp.IPCDEVICE.devicePointCount = 4;
//				}
//			}
//			BaseApp.IPCDEVICE.onlineState = 1;
//			BaseApp.IPCDEVICE.deviceName = BaseApp.IPCDEVICE.deviceNum;
//			BaseApp.IPCDEVICE.deviceLoginUser = getResources().getString(
//					R.string.str_default_user);
//			BaseApp.IPCDEVICE.deviceLoginPwd = getResources().getString(
//					R.string.str_default_pass);
//			BaseApp.IPCDEVICE.devicePointCount = 1;
//			BaseApp.IPCDEVICE.deviceLocalIp = "";
//			BaseApp.IPCDEVICE.deviceLocalPort = 9101;
//			BaseApp.IPCDEVICE.hasWifi = 0;
//
//			ArrayList<ConnPoint> connPointList = new ArrayList<ConnPoint>();
//			if (BaseApp.IPCDEVICE.devicePointCount > 0) {
//				for (int i = 0; i < BaseApp.IPCDEVICE.devicePointCount; i++) {
//					ConnPoint connPoint = new ConnPoint();
//					connPoint.deviceID = BaseApp.IPCDEVICE.deviceOID;// temObj.getInt("DeviceID");
//					connPoint.pointNum = i + 1;// temObj.getInt("PointNum");
//					connPoint.pointOwner = BaseApp.IPCDEVICE.deviceOID;// temObj.getInt("Owner");
//					connPoint.pointName = BaseApp.IPCDEVICE.deviceNum + "_"
//							+ (i + 1);// temObj.getString("Name");
//					connPoint.pointOID = BaseApp.IPCDEVICE.deviceOID;// temObj.getInt("OID");
//					connPoint.isParent = false;
//					connPointList.add(connPoint);
//				}
//			}
//			BaseApp.IPCDEVICE.pointList = connPointList;
//
//			Device dd = BaseApp.addDeviceMethod(BaseApp.IPCDEVICE);
//			// if (!BaseApp.LOCAL_LOGIN_FLAG
//			// && !BaseApp.getSP(JVQuickSettingActivity.this).getBoolean(
//			// "watchType", true)) {
//			// BaseApp.IPCDEVICE.onlineState = 1;
//			// }
//			if (!BaseApp.LOCAL_LOGIN_FLAG) {
//				DeviceUtil.modifyDeviceWifi(BaseApp.IPCDEVICE.deviceNum, 1);
//			}
//			if (null != dd) {
//				BaseApp.deviceList.add(0, dd);
//				BaseApp.setHelpToDevice(dd);
//				if (!BaseApp.LOCAL_LOGIN_FLAG) {
//					DeviceUtil.refreshDeviceState(LoginUtil.userName);
//				}
//				// if(BaseApp.LOCAL_LOGIN_FLAG){
//				// JVConfigManager dbManager = BaseApp.getDbManager(this);//
//				// BaseApp.deviceList = getDataFromDB();// 从数据库中查询所有设备
//				// }else{
//				// BaseApp.deviceList.add(dd);
//				// }
//
//			}
//		}
//	}
//
//	// 从数据库获取数据
//	public ArrayList<Device> getDataFromDB() {
//		ArrayList<Device> temList = new ArrayList<Device>();
//		ArrayList<ConnPoint> tempList = new ArrayList<ConnPoint>();
//		// 查询所有设备
//		List<JVConnectInfo> deList = BaseApp.queryAllDataList(true);
//		if (null != deList && 0 != deList.size()) {
//			int size = deList.size();
//			for (int i = 0; i < size; i++) {
//				// 数据库存储类型转换成网络获取数据类型
//				Device de = deList.get(i).toDevice();
//				if (null != de) {
//					temList.add(de);
//				}
//			}
//		}
//		// 查询所有通道
//		List<JVConnectInfo> poList = BaseApp.queryAllDataList(false);
//		if (null != poList && 0 != poList.size()) {
//			int size = poList.size();
//			for (int i = 0; i < size; i++) {
//				ConnPoint cp = poList.get(i).toConnPoint();
//				if (null != cp) {
//					tempList.add(cp);
//				}
//			}
//		}
//
//		// 将通道关联到设备上
//		if (null != temList && 0 != temList.size() && null != tempList
//				&& 0 != tempList.size()) {
//			for (int i = 0; i < temList.size(); i++) {
//				if (null == temList.get(i).pointList) {
//					temList.get(i).pointList = new ArrayList<ConnPoint>();
//				}
//				for (int j = 0; j < tempList.size(); j++) {
//					if (temList.get(i).yst == tempList.get(j).ystNum
//							&& temList.get(i).group.equalsIgnoreCase(tempList
//									.get(j).group)) {
//						temList.get(i).pointList.add(tempList.get(j));
//					}
//				}
//
//			}
//		}
//		temList = BaseApp.orderDevice(temList);
//		return temList;
//	}
//
//	// 销毁timer
//	private void resetTimer() {
//		try {
//			if (null != connectTimer) {
//				connectTimer.cancel();
//			}
//			connectTimer = null;
//
//			if (null != connectTask) {
//				connectTask.cancel();
//			}
//			connectTask = null;
//			timerCount = 0;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	// 销毁连接timer
//	private void resetConnTimer() {
//		try {
//			if (null != connTimer) {
//				connTimer.cancel();
//			}
//			connTimer = null;
//
//			if (null != coonTask) {
//				coonTask.cancel();
//			}
//			coonTask = null;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			backMethod();
//		}
//		return true;
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		if (null != LoginUtil.activityList
//				&& 0 != LoginUtil.activityList.size()) {
//			LoginUtil.activityList.remove(CopyOfJVQuickSettingActivity.this);
//		}
//
//		resetTimer();
//		resetConnTimer();
//
//		System.gc();
//		super.onDestroy();
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		// TODO Auto-generated method stub
//		super.onConfigurationChanged(newConfig);
//	}
//
//	protected void createDialog(int id) {
//		if (null != CopyOfJVQuickSettingActivity.this
//				&& !CopyOfJVQuickSettingActivity.this.isFinishing()) {
//			if (null == proDialog) {
//				proDialog = new ProgressDialog(CopyOfJVQuickSettingActivity.this);
//			}
//			proDialog.setMessage(CopyOfJVQuickSettingActivity.this.getString(id));
//			proDialog.setCancelable(false);
//		}
//
//		if (null != CopyOfJVQuickSettingActivity.this
//				&& !CopyOfJVQuickSettingActivity.this.isFinishing()
//				&& null != proDialog) {
//			proDialog.show();
//		}
//
//	}
//
//	public void dismissDialog(Dialog dialog) {
//		if (null != CopyOfJVQuickSettingActivity.this
//				&& !CopyOfJVQuickSettingActivity.this.isFinishing() && null != dialog
//				&& dialog.isShowing()) {
//			dialog.dismiss();
//			dialog = null;
//		}
//	}
//
//	// ipc网络已断开提示
//	public void wifiDisDialog() {
//		AlertDialog.Builder builder5 = new AlertDialog.Builder(
//				CopyOfJVQuickSettingActivity.this);
//
//		builder5.setTitle(R.string.tips);
//		builder5.setMessage(ipcWifiName
//				+ CopyOfJVQuickSettingActivity.this.getResources().getString(
//						R.string.str_quick_setting_ap_net_disconnect));
//
//		builder5.setPositiveButton(R.string.str_quick_setting_reset,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						if (wifiDetailLayout.getVisibility() == View.GONE) {
//
//						} else {
//							ipcImage = new ImageView(
//									CopyOfJVQuickSettingActivity.this);
//							ipcImage.setImageDrawable(getResources()
//									.getDrawable(R.drawable.ipc_banner));
//							ipcImage.setVisibility(View.VISIBLE);
//
//							wifiName.setText("");
//							wifiPass.setText("");
//							connectLayout.setVisibility(View.GONE);
//							wifiDetailLayout.setVisibility(View.GONE);
//							// 显示第一个界面IPC样式
//
//							connectBtn.setVisibility(View.GONE);
//							wifiListLayout.setVisibility(View.GONE);
//							ipcWifiListLayout.setVisibility(View.VISIBLE);
//							wifiAdapter.setData2(BaseApp.wifiConfigList, 2);
//							ipcWifiListView.setAdapter(wifiAdapter);
//							wifiAdapter.notifyDataSetChanged();
//						}
//
//						resetTimer();
//						resetConnTimer();
//						reConnect = false;
//
//						// 断开连接
//						// 断开连接
//						disConnectVideo();
//						if (null != CopyOfJVQuickSettingActivity.this
//								&& !CopyOfJVQuickSettingActivity.this.isFinishing()
//								&& null != dialog) {
//							dialog.dismiss();
//						}
//					}
//				});
//
//		if (null != CopyOfJVQuickSettingActivity.this
//				&& !CopyOfJVQuickSettingActivity.this.isFinishing()) {
//			AlertDialog dialog5 = builder5.create();
//			dialog5.setCancelable(false);
//			dialog5.show();
//		}
//	}
//
//	// 切换wifi需要时间
//	private static class ChangeWifiThread extends Thread {
//		private final WeakReference<CopyOfJVQuickSettingActivity> mActivity;
//		private boolean addFlag = false;
//
//		public ChangeWifiThread(CopyOfJVQuickSettingActivity activity, boolean flag) {
//			mActivity = new WeakReference<CopyOfJVQuickSettingActivity>(activity);
//			addFlag = flag;
//		}
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			super.run();
//			Looper.prepare();
//			CopyOfJVQuickSettingActivity activity = mActivity.get();
//			try {
//				if (null != activity && !activity.isFinishing()) {
//					boolean flag = activity.changeWifi();
//					int result = -1;
//					int time = 0;
//					MyLog.v("网络恢复完成", flag + "");
//					if (!BaseApp.LOCAL_LOGIN_FLAG && flag) {// 非本地登录而且网络恢复成功
//						while (JVAccountConst.SUCCESS != result && time <= 5) {
//							time++;
//							try {
//								Thread.sleep(3 * 1000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							result = AccountUtil.userLogin(LoginUtil.userName,
//									LoginUtil.passWord);
//							MyLog.v("网络恢复完成---重新登录---" + LoginUtil.userName,
//									result + "");
//						}
//						if (JVAccountConst.SUCCESS == result && addFlag) {
//							if (!BaseApp.LOCAL_LOGIN_FLAG
//									&& BaseApp.deviceList.size() >= 100) {
//								Message msg = BaseApp.quickSettingHandler
//										.obtainMessage();
//								msg.what = JVConst.DEVICE_MOST_COUNT;
//								BaseApp.quickSettingHandler.sendMessage(msg);
//								Thread.sleep(3000);
//							} else {
//								activity.addDevice();
//							}
//						} else if (JVAccountConst.SUCCESS != result && addFlag) {
//							Message msg = BaseApp.quickSettingHandler
//									.obtainMessage();
//							msg.what = JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL;
//							BaseApp.quickSettingHandler.sendMessage(msg);
//							return;
//						}
//					} else if (BaseApp.LOCAL_LOGIN_FLAG && flag && addFlag) {// 添加设备标识位
//						activity.addDevice();
//					}
//
//					if (null != BaseApp.quickSettingHandler) {
//						Message msg = BaseApp.quickSettingHandler
//								.obtainMessage();
//						if (flag) {
//							msg.what = JVConst.QUICK_SETTING_WIFI_CHANGED_SUCC;
//						} else {
//							msg.what = JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL;
//						}
//						BaseApp.quickSettingHandler.sendMessage(msg);
//					}
//
//				}
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//
//		}
//
//	};
//
//	// 切换到原来网络
//	public boolean changeWifi() {
//
//		boolean changeRes = false;
//		try {
//			// 断开跟连接的wifi 一样不做任何处理
//			if (null != ipcWifiName && !"".equalsIgnoreCase(ipcWifiName)
//					&& null != BaseApp.oldWifiSSID
//					&& !"".equalsIgnoreCase(BaseApp.oldWifiSSID)
//					&& ipcWifiName.equalsIgnoreCase(BaseApp.oldWifiSSID)) {
//				changeRes = true;
//				return changeRes;
//			} else {
//				if (BaseApp.oldWifiState) {// 原wifi开着的，恢复到原来的网络
//					// 断开现在的wifi
//					if (null != ipcWifiName
//							&& !"".equalsIgnoreCase(ipcWifiName)) {
//						WifiConfiguration currWifi = wifiAdmin
//								.isExsits(ipcWifiName);
//						if (null != currWifi) {
//							MyLog.v("完成配置断开", ipcWifiName);
//							wifiAdmin.disconnectWifi(currWifi, true);
//							try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}
//
//					// 完成配置连上原来的wifi
//					if (null != BaseApp.oldWifiSSID) {
//						WifiConfiguration oldWifi = wifiAdmin
//								.isExsits(BaseApp.oldWifiSSID);
//						if (null != oldWifi) {
//							MyLog.v("完成配置连接", BaseApp.oldWifiSSID);
//							boolean connRes = false;
//							int count = 0;
//							while (!connRes) {// 没连接调用连接方法
//								if (count < 10) {
//									count++;
//									try {
//										Thread.sleep(1000);
//									} catch (InterruptedException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//									connRes = wifiAdmin.connNetwork(oldWifi);
//									MyLog.v("完成配置", BaseApp.oldWifiSSID
//											+ "----" + count + "-----调用连接-----"
//											+ connRes);
//								} else {
//									connRes = true;
//									break;
//								}
//							}
//
//							count = 0;
//							if (connRes) {// 已连接
//								while (!changeRes) {// 没连接调用连接方法
//									if (count < 20) {
//										count++;
//										try {
//											Thread.sleep(1000);
//										} catch (InterruptedException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
//										changeRes = getWifiState(oldWifi.SSID);
//										MyLog.v("完成配置", BaseApp.oldWifiSSID
//												+ "----" + count
//												+ "-----连接结果-----" + changeRes);
//									} else {
//										changeRes = false;
//										break;
//									}
//
//								}
//							} else {
//								changeRes = false;
//							}
//
//						}
//					} else {
//						changeRes = true;
//					}
//				} else {// 原wifi关闭状态，关闭wifi
//					wifiAdmin.closeWifi();
//					changeRes = true;
//				}
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		return changeRes;
//
//	}
//
// }
