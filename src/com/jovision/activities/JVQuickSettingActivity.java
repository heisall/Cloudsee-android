package com.jovision.activities;

//
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.content.res.AssetFileDescriptor;
//import android.content.res.AssetManager;
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiConfiguration;
//import android.net.wifi.WifiManager;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.text.InputType;
//import android.util.Log;
//import android.view.Display;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//import android.widget.ToggleButton;
//
//import com.jovetech.CloudSee.temp.R;
//import com.jovision.Consts;
//import com.jovision.adapters.IpcWifiAdapter;
//import com.jovision.adapters.MobileWifiAdapter;
//import com.jovision.bean.Device;
//import com.jovision.bean.WifiAdmin;
//import com.jovision.commons.CommonInterface;
//import com.jovision.commons.JVAccountConst;
//import com.jovision.commons.JVConst;
//import com.jovision.commons.JVNetConst;
//import com.jovision.old.JVConnectInfo;
//import com.jovision.utils.AccountUtil;
//import com.jovision.utils.DeviceUtil;
//import com.jovision.views.RefreshableListView;
//import com.jovision.views.RefreshableListView.OnRefreshListener;
//import com.jovision.views.SearchDevicesView;
//
//public class JVQuickSettingActivity extends BaseActivity implements
//		CommonInterface {
//
//	private final String TAG = "Qick_SetQick_Set";
//	// private String ipcSSID;// 待设置的IPC网络名字 “IPC-H-S53530352”
//	private static String ipcWifiName;// 待设置的IPC网络名字 IPC-H-S53530352
//	// private String deviceNum;// 待设置的IPC云视通号 S53530352
//	private String desWifiSSID;//
//	private String desWifiPWD;//
//	private Button back;
//	private Button saveSet;
//	private TextView currentMenu;
//	ConnectivityManager connectivityManager = null;
//
//	/** webview帮助 */
//	private LinearLayout helpLayout;
//	private ImageView helpIV;
//
//	/** IPCwifi列表 */
//	private LinearLayout ipcLayout;
//	private ImageView ipcImage;
//	/** IPC网络 */
//	private ArrayList<ScanResult> scanIpcWifiList = new ArrayList<ScanResult>();
//	private RefreshableListView ipcWifiListView;
//	private IpcWifiAdapter ipcAdapter;
//
//	/** 手机wifi列表 */
//	private LinearLayout mobileLayout;
//	private LinearLayout mobileWifiBg;
//
//	private EditText desWifiName;
//	private EditText desWifiPass;
//	private ToggleButton destWifiEye;
//	/** 手机wifi列表（除IPC） */
//	private ArrayList<ScanResult> scanMobileWifiList = new ArrayList<ScanResult>();
//	private RefreshableListView mobileWifiListView;
//	private MobileWifiAdapter mobileAdapter;
//
//	private String oldWifiSSID;
//	private WifiAdmin wifiAdmin;
//	private int connectFailedCounts = 0;// 连接设备失败次数
//
//	boolean hasLogout = false;// 是否已经注销
//
//	private int errorCount = 0;
//	SharedPreferences sharedPreferences = null;
//	Editor editor = null;// 获取编辑器
//
//	private Timer ipcTimer;
//	private TimerTask ipcTimerTask;
//	LayoutInflater layoutFlater = null;
//
//	// private MediaPlayer mediaPlayer = null;
//	private PopupWindow quickSetPop = null;// 快速设置弹出框
//	private LinearLayout quickSetParentLayout = null;// 父窗体布局
//	private ImageView quickSetDeviceImg = null;// 搜索动画上的小摄像头图标
//	private Button quickSetBackImg = null;// 快速配置动画弹出框上的返回按钮
//	private boolean isSearching = false;// 正在循环配置设备
//	private WifiManager wifiManager = null;
//	private SearchDevicesView searchView = null;
//
//	@Override
//	protected void initSettings() {
//	}
//
//	@SuppressWarnings("deprecation")
//	@Override
//	protected void initUi() {
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		//设置全屏
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setContentView(R.layout.quicksetting_layout);
//		quickSetParentLayout = (LinearLayout) findViewById(R.id.quickSetParentLayout);
//		layoutFlater = JVQuickSettingActivity.this.getLayoutInflater();
//		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//		wifiAdmin = new WifiAdmin(JVQuickSettingActivity.this);
//
//		Intent intent = getIntent();
//		if (null != intent) {
//			oldWifiSSID = intent.getStringExtra("OLD_WIFI");
//			scanIpcWifiList = intent.getParcelableArrayListExtra("IPC_LIST");
//			scanMobileWifiList = intent
//					.getParcelableArrayListExtra("MOBILE_LIST");
//			String comeFromMain = intent.getStringExtra("COMEFROMMAIN");
//			if ("comeFromMain".equals(comeFromMain)) {// 语音提示请选择您要配置d设备，点击进入视频体验界面
//				// 播放声音
//				playSound(Consts.SOUNDONE);
//			}
//		}
//
//		if (null != BaseApp.quickSettingHandler) {
//			Message msg = BaseApp.quickSettingHandler.obtainMessage();
//			if (null != scanIpcWifiList && 0 != scanIpcWifiList.size()) {
//				msg.what = JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS;
//			} else {
//				msg.what = JVConst.QUICK_SETTING_IPC_WIFI_FAILED;
//			}
//			BaseApp.quickSettingHandler.sendMessage(msg);
//		}
//
//		ipcTimerTask = new TimerTask() {
//
//			@Override
//			public void run() {
//				try {
//
//					Log.v("IPC—wifi—自动刷新—", "10秒时间到");
//					if (wifiAdmin.getWifiState()) {
//						scanIpcWifiList = wifiAdmin.startScanIPC();
//					}
//
//					// 万一取失败了，接着再取一次
//					if (null == scanIpcWifiList || 0 == scanIpcWifiList.size()) {
//						scanIpcWifiList = wifiAdmin.startScanIPC();
//					}
//
//					BaseApp.quickSettingHandler
//							.sendMessage(BaseApp.quickSettingHandler
//									.obtainMessage(
//											JVConst.QUICK_SETTING_IPC_WIFI_AUTO_SUCCESS,
//											-1, 0));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			}
//
//		};
//
//		back = (Button) findViewById(R.id.back);
//		saveSet = (Button) findViewById(R.id.add_device);
//		back.setVisibility(View.VISIBLE);
//		saveSet.setTextColor(Color.WHITE);
//		saveSet.setBackgroundDrawable(getResources().getDrawable(
//				R.drawable.setting_save));
//		saveSet.setText(getResources().getString(
//				R.string.str_quick_setting_connect));
//		saveSet.setVisibility(View.VISIBLE);
//		currentMenu = (TextView) findViewById(R.id.currentmenu);
//		currentMenu.setText(R.string.str_quick_setting);
//		back.setOnClickListener(onClickListener);
//		saveSet.setOnClickListener(onClickListener);
//
//		helpLayout = (LinearLayout) findViewById(R.id.helplayout);
//		helpIV = (ImageView) findViewById(R.id.helpimg);
//		if (BaseApp.getLan() == JVConst.LANGUAGE_ZH) {// 中文
//			helpIV.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.h400_update));
//		} else {
//			helpIV.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.h400_update_en));
//		}
//		/** IPCwifi列表 */
//		ipcLayout = (LinearLayout) findViewById(R.id.ipcwifilayout);
//		ipcWifiListView = (RefreshableListView) findViewById(R.id.ipcwifilistview);
//		LinearLayout layout = (LinearLayout) layoutFlater.inflate(
//				R.layout.quiksettinglayoutheader, null);
//		ipcImage = (ImageView) layout.findViewById(R.id.quicksetting_tips_img);
//		// ipcImage.setImageDrawable(getResources().getDrawable(
//		// R.drawable.ipc_banner));
//		ipcWifiListView.addHeaderView(layout);
//
//		ipcWifiListView.setOnRefreshListener(new WifiRefreshListener(true));
//		ipcWifiListView.setOnItemClickListener(mOnItemClickListener);
//		ipcAdapter = new IpcWifiAdapter(JVQuickSettingActivity.this);
//		if (null != scanIpcWifiList && 0 != scanIpcWifiList.size()) {
//			ipcAdapter.setData(scanIpcWifiList, oldWifiSSID);
//			ipcWifiListView.setAdapter(ipcAdapter);
//		}
//
//		/** 手机wifi列表 */
//		mobileLayout = (LinearLayout) findViewById(R.id.mobilewifilayout);
//		mobileWifiBg = (LinearLayout) findViewById(R.id.mobilewifibg);
//		desWifiName = (EditText) findViewById(R.id.deswifiname);
//		desWifiName.setText(BaseApp.oldWifiSSID);
//		desWifiName.setEnabled(false);
//
//		desWifiPass = (EditText) findViewById(R.id.deswifipwd);
//		destWifiEye = (ToggleButton) findViewById(R.id.deswifieye);
//		desWifiPass
//				.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);// 显示密码
//
//		mobileWifiListView = (RefreshableListView) findViewById(R.id.mobilewifilistview);
//		mobileWifiListView.setOnRefreshListener(new WifiRefreshListener(false));
//		mobileWifiListView.setOnItemClickListener(mOnItemClickListener);
//		// mobileWifiListView.setVisibility(View.GONE);
//		mobileAdapter = new MobileWifiAdapter(JVQuickSettingActivity.this);
//		if (null != scanMobileWifiList && 0 != scanMobileWifiList.size()) {
//			mobileAdapter.setData(scanMobileWifiList, oldWifiSSID);
//			mobileWifiListView.setAdapter(mobileAdapter);
//		}
//		destWifiEye.setChecked(false);
//		destWifiEye.setOnCheckedChangeListener(myOnCheckedChangeListener);
//
//		showIpcLayout(true);
//
//		initPopwindow();// 初始化快速配置弹出框所需要的内容
//	}
//
//	// onclick事件
//	OnClickListener onClickListener = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.btn_left:
//				backMethod();
//				break;
//			case R.id.btn_right:
//				// 播放提示声音
//				playSound(Consts.SOUNDFIVE);
//				isSearching = true;
//				desWifiSSID = desWifiName.getText().toString();
//				if ("".equalsIgnoreCase(desWifiSSID)) {
//					showTextToast(R.string.str_wifiname_not_null);
//					break;
//				}
//				BaseApp.oldWifiSSID = desWifiSSID;
//
//				desWifiPWD = desWifiPass.getText().toString();
//				new ApSetThread().start();
//				// BaseApp.quickSettingHandler
//				// .sendMessage(BaseApp.quickSettingHandler.obtainMessage(
//				// JVConst.AP_SET_ORDER, 1, 0));
//
//				// 开始声波配置，弹出配置对话框
//				searchView.setSearching(true);
//				showQuickPopWindow();
//				break;
//			}
//		}
//	};
//
//	/**
//	 * IPC设置线程
//	 */
//
//	class IpcWifiThread extends Thread {
//
//		@Override
//		public void run() {
//			Looper.prepare();
//
//			if (!BaseApp.LOCAL_LOGIN_FLAG && !hasLogout) {// 非本地登录注销
//				hasLogout = true;
//				AccountUtil.userLogout();
//			}
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			if (wifiAdmin.getWifiState()) {// 如果wifi为打开状态无需等待5秒
//				BaseApp.oldWifiState = true;
//			} else {
//				BaseApp.oldWifiState = false;
//				wifiAdmin.openWifi();// 打开wifi需要6秒左右
//
//				boolean state = false;
//
//				while (!state) {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e1) {
//						e1.printStackTrace();
//					}
//					state = wifiAdmin.getWifiState();
//					Log.v("while判断网络状态：", state + "");
//				}
//
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e1) {
//					e1.printStackTrace();
//				}
//			}
//
//			// 和待配置的网络一样直接连接视频
//			if (null != oldWifiSSID
//					&& ipcWifiName.equalsIgnoreCase(oldWifiSSID)) {// 当前连接的网路就是要配置ap网络
//				BaseApp.quickSettingHandler
//						.sendMessage(BaseApp.quickSettingHandler
//								.obtainMessage(JVConst.QUICK_SETTING_AP_CON_SUCCESS));
//			} else {
//				// 切换到ipc 的ap 网络
//				boolean ipcFlag = connectWifi(ipcWifiName, getResources()
//						.getString(R.string.str_wifi_pwd));
//
//				try {
//					Thread.sleep(2500);
//				} catch (InterruptedException e1) {
//					e1.printStackTrace();
//				}
//
//				if (ipcFlag) {
//					BaseApp.quickSettingHandler
//							.sendMessage(BaseApp.quickSettingHandler
//									.obtainMessage(JVConst.QUICK_SETTING_AP_CON_SUCCESS));
//				} else {
//					BaseApp.quickSettingHandler
//							.sendMessage(BaseApp.quickSettingHandler
//									.obtainMessage(JVConst.QUICK_SETTING_AP_CON_TIMEOUT));
//				}
//
//			}
//
//			super.run();
//		}
//
//	}
//
//	/**
//	 * AP设置线程
//	 */
//	class ApSetThread extends Thread {
//
//		@Override
//		public void run() {
//			Looper.prepare();
//			// // 和待配置的网络一样关闭网络并移除
//			// if (null != oldWifiSSID
//			// && desWifiSSID.equalsIgnoreCase(oldWifiSSID)) {
//			// WifiConfiguration oldWifi = wifiAdmin.isExsits(oldWifiSSID);
//			// if (null != oldWifi) {
//			// wifiAdmin.disconnectWifi(oldWifi, true);// 关掉，移除,不移除会记住原来的密码
//			// wifiAdmin.disconnectWifi(oldWifi, true);// 关掉，移除,不移除会记住原来的密码
//			// }
//			// }
//			//
//			// if (null != desWifiSSID && !"".equalsIgnoreCase(desWifiSSID)) {
//			// WifiConfiguration desWifi = wifiAdmin.isExsits(desWifiSSID);
//			// if (null != desWifi) {
//			// wifiAdmin.disconnectWifi(desWifi, true);// 关掉，移除,不移除会记住原来的密码
//			// wifiAdmin.disconnectWifi(desWifi, true);// 关掉，移除,不移除会记住原来的密码
//			// }
//			// }
//			//
//			// try {
//			// Thread.sleep(2 * 1000);
//			// } catch (InterruptedException e) {
//			// // TODO Auto-generated catch block
//			// e.printStackTrace();
//			// }
//			// boolean mobileFlag = connectWifi(desWifiSSID, desWifiPWD);
//			//
//			// if (!mobileFlag) {
//			// BaseApp.quickSettingHandler
//			// .sendMessage(BaseApp.quickSettingHandler.obtainMessage(
//			// JVConst.MOBILE_WIFI_CON_FAILED, -1, 0));
//			// return;
//			// }
//			// BaseApp.oldWifiSSID = desWifiSSID;
//			//
//			// BaseApp.quickSettingHandler.sendMessage(BaseApp.quickSettingHandler
//			// .obtainMessage(JVConst.AP_SET_ORDER, 2, 0));
//
//			// 从新连接一下 ap Wi-Fi ，确保ap 连接正常
//			boolean ipcFlag = connectWifi(ipcWifiName, getResources()
//					.getString(R.string.str_wifi_pwd));
//
//			if (!ipcFlag) {
//				BaseApp.quickSettingHandler
//						.sendMessage(BaseApp.quickSettingHandler.obtainMessage(
//								JVConst.IPC_WIFI_CON_FAILED, -1, 0));
//				return;
//			}
//			// BaseApp.quickSettingHandler.sendMessage(BaseApp.quickSettingHandler
//			// .obtainMessage(JVConst.AP_SET_ORDER, 3, 0));
//
//			// 连接视频，为接下来ap 配置做准备
//			connectDevice(getIPCDevice());
//
//			super.run();
//		}
//
//	}
//
//	/**
//	 * 连接wifi 30s超时
//	 * 
//	 * @param wifi
//	 * @param password
//	 * @return
//	 */
//	public boolean connectWifi(String wifi, String password) {
//
//		boolean flag = wifiAdmin.getWifiState(wifi);
//		int result[] = wifiAdmin.getWifiAuthEnc(wifi);
//		int errorCount = 0;
//
//		while (!flag) {
//			Log.v(TAG, "wifi=" + wifi + ";errorCount=" + errorCount + ";flag="
//					+ flag);
//			if (errorCount > 30) {
//				break;
//			}
//			if (errorCount == 0 || errorCount == 6 || errorCount == 10
//					|| errorCount == 14 || errorCount == 20 || errorCount == 24
//					|| errorCount == 28) {
//				WifiConfiguration desWifi = wifiAdmin.isExsits(wifi);
//				if (null == desWifi) {
//					int enc = 0;
//					if ("".equalsIgnoreCase(password)) {
//						enc = 1;
//					} else {
//
//						if (1 == result[1]) {// wep
//							enc = 2;
//						} else {
//							enc = 3;
//						}
//
//					}
//					desWifi = wifiAdmin.CreateWifiInfo(wifi, password, enc);
//				}
//				wifiAdmin.ConnectWifiByConfig(desWifi);
//			}
//
//			// 休息两秒钟保证连接Wi-Fi成功
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			errorCount += 2;
//			Log.e("tag----------------", "errorCount: " + errorCount);
//			flag = wifiAdmin.getWifiState(wifi);
//		}
//
//		return flag;
//	}
//
//	/**
//	 * 两个wifi列表点击事件
//	 */
//	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			try {
//				if (ipcLayout.getVisibility() == View.VISIBLE) {// IPC设备列表
//					if (arg2 <= 0) {
//						return;
//					}
//					if (null != scanIpcWifiList && 0 != scanIpcWifiList.size()
//							&& arg2 - 1 < scanIpcWifiList.size()) {
//						ipcWifiName = scanIpcWifiList.get(arg2 - 1).SSID;
//						// .replace("\"", "");
//						// String str1 = ipcWifiName.replace("\"", "");
//						// String str2 = str1.substring(6, str1.length());
//						// deviceNum = str2;
//
//						new IpcWifiThread().start();
//						createDialog(R.string.str_quick_setting_alert_1);
//						playSound(Consts.SOUNDTOW);
//					}
//				} else {// 手机wifi信息列表
//					if (null != scanMobileWifiList
//							&& 0 != scanMobileWifiList.size()) {
//						desWifiName.setText(scanMobileWifiList.get(arg2).SSID);
//
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//	};
//
//	// 返回方法
//	public void backMethod() {
//
//		if (View.VISIBLE == helpLayout.getVisibility()) {
//			helpLayout.setVisibility(View.GONE);
//			saveSet.setVisibility(View.VISIBLE);
//			currentMenu.setText(R.string.str_quick_setting);
//			Log.e("tags", "backMethod 1");
//		} else {
//			try {
//				if (ipcLayout.getVisibility() == View.VISIBLE) {// 显示IPC网络信息列表（一级级wifi列表）
//					Log.e("tags", "backMethod 2");
//					 createDialog(R.string.str_quick_setting_alert_6);
//					 
//					 ChangeWifiThread cwt = new ChangeWifiThread(
//					 JVQuickSettingActivity.this, false);
//					 cwt.start();
////					finish();
//				} else {// 显示主控wifi信息列表（二级wifi列表）
//					showIpcLayout(true);
//					Log.e("tags", "backMethod 3");
//					APUtil.disConnectVideo();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 * 单纯的切换网络线程
//	 */
//	class SimpleChangeWifi extends Thread{
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			super.run();
//			boolean flag = changeWifi();
//			if(flag){
//				
//			}
//		}
//		
//	}
//	
//	/**
//	 * 密码显示隐藏
//	 */
//	OnCheckedChangeListener myOnCheckedChangeListener = new OnCheckedChangeListener() {
//
//		@Override
//		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//			if (arg1) {
//				desWifiPass
//						.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);// 显示密码
//			} else {
//				desWifiPass.setInputType(InputType.TYPE_CLASS_TEXT
//						| InputType.TYPE_TEXT_VARIATION_PASSWORD);// 隐藏密码
//			}
//			desWifiPass.setSelection(desWifiPass.getText().toString().length());
//			destWifiEye.setChecked(arg1);
//		}
//
//	};
//
//	/**
//	 * wifi列表刷新事件
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//	private class WifiRefreshListener implements OnRefreshListener {
//		boolean isIpc = false;
//
//		public WifiRefreshListener(boolean b) {
//			isIpc = b;
//		}
//
//		@Override
//		public void onRefresh(RefreshableListView listView) {
//			createDialog(R.string.str_quick_setting_searching_wifi);
//			RefreshWifiThread giwt = new RefreshWifiThread(
//					JVQuickSettingActivity.this, isIpc);
//			giwt.start();
//		}
//
//	}
//
//	/**
//	 * 获取IPC待配置设备列表线程
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//	private class RefreshWifiThread extends Thread {
//		private final WeakReference<JVQuickSettingActivity> mActivity;
//		private boolean isIpc = false;
//
//		public RefreshWifiThread(JVQuickSettingActivity activity, boolean ipc) {
//			mActivity = new WeakReference<JVQuickSettingActivity>(activity);
//			isIpc = ipc;
//		}
//
//		@Override
//		public void run() {
//			super.run();
//			try {
//				JVQuickSettingActivity activity = mActivity.get();
//				if (null != activity) {
//					if (isIpc) {// 刷新ipcwifi
//						if (wifiAdmin.getWifiState()) {
//							scanIpcWifiList = wifiAdmin.startScanIPC();
//						}
//
//						// 万一取失败了，接着再取一次
//						if (null == scanIpcWifiList
//								|| 0 == scanIpcWifiList.size()) {
//							scanIpcWifiList = wifiAdmin.startScanIPC();
//						}
//
//						if (null != scanIpcWifiList
//								&& 0 != scanIpcWifiList.size()) {
//							BaseApp.quickSettingHandler
//									.sendMessage(BaseApp.quickSettingHandler
//											.obtainMessage(
//													JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS,
//													-1, 0));
//						} else {
//							BaseApp.quickSettingHandler
//									.sendMessage(BaseApp.quickSettingHandler
//											.obtainMessage(
//													JVConst.QUICK_SETTING_IPC_WIFI_FAILED,
//													-1, 0));
//						}
//					} else {// 刷新手机wifi
//						if (wifiAdmin.getWifiState()) {
//							scanMobileWifiList = wifiAdmin.startScanWifi();
//						}
//						// 万一取失败了，接着再取一次
//						if (null == scanMobileWifiList
//								|| 0 == scanMobileWifiList.size()) {
//							scanMobileWifiList = wifiAdmin.startScanWifi();
//						}
//
//						if (null == scanMobileWifiList
//								|| 0 == scanMobileWifiList.size()) {
//							BaseApp.quickSettingHandler
//									.sendMessage(BaseApp.quickSettingHandler
//											.obtainMessage(
//													JVConst.QUICK_SETTING_MOBILE_WIFI_FAILED,
//													-1, 0));
//						} else {
//							BaseApp.quickSettingHandler
//									.sendMessage(BaseApp.quickSettingHandler
//											.obtainMessage(
//													JVConst.QUICK_SETTING_MOBILE_WIFI_SUCC,
//													-1, 0));
//						}
//					}
//
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 * 显示ipc或手机界面
//	 * 
//	 * @param show
//	 */
//	private void showIpcLayout(boolean show) {
//		if (show) {
//			ipcLayout.setVisibility(View.VISIBLE);
//			mobileLayout.setVisibility(View.GONE);
//			saveSet.setVisibility(View.GONE);
//			desWifiPass.setText("");
//		} else {
//			ipcLayout.setVisibility(View.GONE);
//			mobileLayout.setVisibility(View.VISIBLE);
//			mobileWifiBg.setVisibility(View.VISIBLE);
//			saveSet.setVisibility(View.VISIBLE);
//		}
//	}
//
//	public static class OnLineThread extends Thread {
//
//		@Override
//		public void run() {
//			Message msgTryOnline = BaseApp.quickSettingHandler.obtainMessage();
//			msgTryOnline.what = JVConst.QUICK_SETTING_DEV_START_ONLINE;
//			BaseApp.quickSettingHandler.sendMessage(msgTryOnline);
//
//			int timeCount = 0;
//			int onlineState = DeviceUtil
//					.getDevOnlineState(getIPCDevice().deviceNum);
//
//			while (0 == onlineState) {// 没上线
//
//				if (timeCount > 40) {
//					onlineState = DeviceUtil
//							.getDevOnlineState(getIPCDevice().deviceNum);
//					break;
//				} else {
//					onlineState = DeviceUtil
//							.getDevOnlineState(getIPCDevice().deviceNum);
//					try {
//						Thread.sleep(5 * 1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					timeCount += 5;
//				}
//
//			}
//
//			Message msgOnline = BaseApp.quickSettingHandler.obtainMessage();
//			if (0 == onlineState) { // 没上线
//				setLineState(getIPCDevice().deviceNum, 0);
//				msgOnline.what = JVConst.QUICK_SETTING_DEV_ONLINE_FAILED;
//			} else {// 上线
//				setLineState(getIPCDevice().deviceNum, 1);
//				msgOnline.what = JVConst.QUICK_SETTING_DEV_ONLINE_SUCC;
//			}
//
//			BaseApp.quickSettingHandler.sendMessage(msgOnline);
//			super.run();
//		}
//
//	};
//
//	public static void setLineState(String devNum, int online) {
//		int size = 0;
//		if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
//			size = BaseApp.deviceList.size();
//			if (0 != size) {
//				for (int i = 0; i < size; i++) {
//					if (devNum
//							.equalsIgnoreCase(BaseApp.deviceList.get(i).deviceNum)) {
//						BaseApp.deviceList.get(i).onlineState = online;
//						break;
//					}
//				}
//			}
//		}
//	}
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
//				Log.i("test", "无网络连接------------------");
//				wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//				wifiManager.setWifiEnabled(true);
//			} else {
//				Log.i("test", "网络连接类型为" + net.getTypeName());
//				Log.i("test", "网络连接状态为" + net.getState().name());
//				if ("CONNECTED".equalsIgnoreCase(net.getState().name())) {
//					// 防止连上wifi，但不是连的IPCwifi
//					Log.i("test", "wifiAdmin.getSSID()--" + wifiAdmin.getSSID());
//
//					if (null != wifiAdmin && null != wifiAdmin.getSSID()
//							&& !"".equalsIgnoreCase(wifiAdmin.getSSID().trim())) {
//						String str1 = wifiAdmin.getSSID().replace("\"", "");
//						String str2 = wifiName.replace("\"", "");
//
//						if (str1.equalsIgnoreCase(str2)) {
//							flag = true;
//						} else {
//							Log.e("原WIFI-SSID：", str1);
//							WifiConfiguration oldWifi = wifiAdmin
//									.isExsits(str1);
//							if (null != oldWifi) {
//								Log.e("关掉原不移除WIFI-SSID：", str1);
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
//	@Override
//	protected void saveSettings() {
//
//	}
//
//	@Override
//	protected void freeMe() {
//		if (null != ipcTimer) {
//			ipcTimer.cancel();
//			ipcTimer = null;
//		}
//		if (null != ipcTimerTask) {
//			ipcTimerTask.cancel();
//			ipcTimerTask = null;
//		}
//	}
//
//	public static class QuickSettingHandler extends Handler {
//		private final WeakReference<JVQuickSettingActivity> mActivity;
//
//		public QuickSettingHandler(JVQuickSettingActivity activity) {
//			mActivity = new WeakReference<JVQuickSettingActivity>(activity);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			final JVQuickSettingActivity activity = mActivity.get();
//
//			if (null != activity && !activity.isFinishing()) {
//
//				switch (msg.what) {
//				case JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS:// Ipcwifi列表获取成功
//					// if (!activity.hasShowTips
//					// && !activity.sharedPreferences.getBoolean(
//					// "AP_TIPS", false)) {
//					// activity.alertTipsDialog();
//					// }
//					activity.ipcWifiListView.completeRefreshing();
//					activity.dismissDialog();
//
//					if (null != activity.scanIpcWifiList
//							&& 0 != activity.scanIpcWifiList.size()) {
//						activity.ipcAdapter.setData(activity.scanIpcWifiList,
//								"");
//						activity.ipcWifiListView
//								.setAdapter(activity.ipcAdapter);
//					}
//
//					if (null == activity.ipcTimer) {
//						activity.ipcTimer = new Timer();
//						if (null != activity.ipcTimerTask) {
//							activity.ipcTimer.schedule(activity.ipcTimerTask,
//									10 * 1000, 10 * 1000);
//						}
//					}
//
//					break;
//				case JVConst.QUICK_SETTING_IPC_WIFI_AUTO_SUCCESS:// Ipcwifi列表获取成功
//					activity.ipcAdapter.setData(activity.scanIpcWifiList, "");
//					activity.ipcWifiListView.setAdapter(activity.ipcAdapter);
//					break;
//				case JVConst.QUICK_SETTING_IPC_WIFI_FAILED:// IPCwifi列表获取失败
//					activity.ipcWifiListView.completeRefreshing();
//					activity.dismissDialog();
//					// activity.finish();
//					break;
//				case JVConst.QUICK_SETTING_MOBILE_WIFI_SUCC:// 快速设置获取主控WIFI信息数据
//					activity.mobileWifiListView.completeRefreshing();
//					activity.dismissDialog();
//
//					if (null != activity.scanIpcWifiList
//							&& 0 != activity.scanMobileWifiList.size()) {
//						activity.mobileAdapter.setData(
//								activity.scanMobileWifiList,
//								activity.oldWifiSSID);
//						activity.mobileWifiListView
//								.setAdapter(activity.mobileAdapter);
//					}
//					break;
//				case JVConst.QUICK_SETTING_MOBILE_WIFI_FAILED:// IPCwifi列表获取失败
//					activity.mobileWifiListView.completeRefreshing();
//					activity.dismissDialog();
//					break;
//				case JVConst.IPC_WIFI_CON_FAILED:// ipcwifi连接失败，
//					activity.dismissDialog();
//					activity.netErrorDialog();
//					break;
//				case JVConst.MOBILE_WIFI_CON_FAILED:// 手机wifi连接失败，
//					activity.dismissDialog();
//					activity.netErrorDialog();
//					break;
//				case JVConst.AP_SET_ORDER:// AP配置流程提示
//					String message = "";
//					if (1 == msg.arg1) {
//						message = activity
//								.getString(R.string.str_quick_setting_now_1);
//					} else if (2 == msg.arg1) {
//						message = activity
//								.getString(R.string.str_quick_setting_now_2);
//					} else if (3 == msg.arg1) {
//						message = activity
//								.getString(R.string.str_quick_setting_now_3);
//					} else if (4 == msg.arg1) {
//						message = activity
//								.getString(R.string.str_quick_setting_now_4);
//					} else if (5 == msg.arg1) {
//						message = activity
//								.getString(R.string.str_quick_setting_now_5);
//					}
//
//					// activity.createDialog(message);// + "(" + msg.arg1 +
//					// "/5)");
//					break;
//				case JVConst.QUICK_SETTING_AP_CON_SUCCESS:// ipc的路由连接成功
//					activity.dismissDialog();
//					getIPCDevice();
//					BaseApp.SCREEN = BaseApp.SINGLE_SCREEN;
//					Intent apIntent = new Intent(activity, JVPlayActivity.class);
//					apIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					apIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					apIntent.putExtra("PlayTag", JVConst.AP_PLAY_FLAG);
//					activity.startActivityForResult(apIntent,
//							JVConst.AP_CONNECT_REQUEST);
//					break;
//				case JVConst.QUICK_SETTING_AP_CON_TIMEOUT:// ipc的路由连接超时
//					activity.dismissDialog();
//					BaseApp.showTextToast(activity,
//							R.string.str_quick_setting_ap_net_timeout);
//					break;
//
//				case JVConst.QUICK_SETTING_CONNECT_FAILED:// 快速设置设备连接失败,失败一次再连接一次，直到第十次失败停止
//					Log.v("快速设置设备连接失败", "第-----" + activity.connectFailedCounts
//							+ "-----次");
//					if (activity.connectFailedCounts < 10) {
//						activity.connectFailedCounts++;
//						try {
//							Thread.sleep(2000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						activity.connectDevice(activity.getIPCDevice());
//					} else {
//						activity.connectFailedCounts = 0;// 连接失败次数复位
//						activity.isSearching = false;
//						activity.dismissDialog();
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
//										dialog.dismiss();
//									}
//								});
//						builder1.create().show();
//					}
//
//					break;
//
//				case JVConst.QUICK_SETTING_IS_IPC:// 是IPC
//					BaseApp.quickSettingHandler
//							.sendMessage(BaseApp.quickSettingHandler
//									.obtainMessage(JVConst.AP_SET_ORDER, 4, 0));
//					Log.e("连接成功发暂停命令：", "JVN_CMD_VIDEOPAUSE");
//
//					JVSUDT.JVC_SendData(1,
//							(byte) JVNetConst.JVN_CMD_VIDEOPAUSE, new byte[0],
//							0);
//
//					Log.e("是IPC-----------", "是IPC，发聊天请求");
//
//					JVSUDT.JVC_SendData(1, (byte) JVNetConst.JVN_REQ_TEXT,
//							new byte[0], 8);// new
//											// byte[0]
//					break;
//				case JVConst.QUICK_SETTING_HOST_AGREE_TEXT:// 快速配置主控同意文本聊天
//					Log.e("主控同意文本聊天-----------", "主控同意文本聊天");
//
//					JVSUDT.sendTextData = false;
//					activity.errorCount = 0;
//					new Thread() {
//						public void run() {
//							// 回调函数未过来
//							while (!JVSUDT.sendTextData) {
//								activity.errorCount++;
//								JVSUDT.sendTextData = false;
//								Log.e("获取主控文本信息-----" + activity.errorCount
//										+ "次------", activity.errorCount
//										+ "****");
//								// 获取主控文本聊天信息请求
//								JVSUDT.JVC_SendTextData(1,
//										(byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
//										JVNetConst.JVN_REMOTE_SETTING);
//								if (activity.errorCount == 3) {
//									break;
//								}
//								try {
//									Thread.sleep(5 * 1000);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//							}
//						};
//					}.start();
//
//					break;
//				case JVConst.QUICK_SETTING_HOST_NOT_AGREE_TEXT:// 快速设置主控不同意聊天
//					activity.dismissDialog();
//					// 断开连接
//					APUtil.disConnectVideo();
//					BaseApp.showTextToast(activity,
//							R.string.str_only_administator_use_this_function);
//					break;
//				case JVConst.QUICK_SETTING_HOST_TEXT:// 快速配置文本聊天数据过来
//					Log.v("文本聊天数据过来-----------", "文本聊天数据过来");
//					if (null != BaseApp.settingMap
//							&& null != BaseApp.settingMap.get("DEV_VERSION")) {
//						String dvStr = BaseApp.settingMap.get("DEV_VERSION");
//						int dvInt = Integer.parseInt(dvStr);
//						if (dvInt == 1) {// 当前最新版本
//						} else {// 旧版
//							activity.dismissDialog();
//							activity.updateDialog();
//							break;
//						}
//					} else {
//						activity.dismissDialog();
//						activity.updateDialog();
//						break;
//					}
//					activity.proDialog.setMessage(activity.getResources()
//							.getString(R.string.str_quick_setting_alert_5));
//					JSONObject obj = new JSONObject();
//					try {
//						obj.put("wifiSsid", activity.desWifiSSID);
//						obj.put("wifiPwd", activity.desWifiPWD);
//						obj.put("nPacketType", JVNetConst.RC_EXTEND);
//						obj.put("packetCount", JVNetConst.RC_EX_NETWORK);
//						obj.put("nType", JVNetConst.EX_WIFI_AP_CONFIG);
//						int[] data = activity.wifiAdmin
//								.getWifiAuthEnc(activity.desWifiSSID);
//						obj.put("wifiAuth", data[0]);
//						obj.put("wifiEncryp", data[1]);
//						obj.put("wifiIndex", 0);
//						obj.put("wifiChannel", 0);
//						obj.put("wifiRate", 0);// 0成功，其他失败
//					} catch (JSONException e1) {
//						e1.printStackTrace();
//					}
//					Log.v("配置wifi请求", obj.toString());
//					BaseApp.quickSettingHandler
//							.sendMessage(BaseApp.quickSettingHandler
//									.obtainMessage(JVConst.AP_SET_ORDER, 5, 0));
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					JVSUDT.JVC_ManageAP(1, (byte) JVNetConst.JVN_RSP_TEXTDATA,
//							obj.toString());
//
//					break;
//				case JVConst.QUICK_SETTING_IS_NOT_IPC:// 快速设置不是ipc
//					activity.dismissDialog();
//					JVSUDT.QUICK_SETTING_FLAG = false;
//
//					// 断开连接
//					APUtil.disConnectVideo();
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
//				case JVConst.QUICK_SETTING_HOST_SET_WIFI_SUCC:// 主控wifi配置成功
//					activity.dismissDialog();
//					// 断开连接
//					APUtil.disConnectVideo();
//					JVSUDT.QUICK_SETTING_FLAG = false;
//
//					Message msg222 = BaseApp.quickSettingHandler
//							.obtainMessage();
//					msg222.what = JVConst.QUICK_SETTING_WIFI_FINISH_SET;
//					BaseApp.quickSettingHandler.sendMessage(msg222);
//
//					// AlertDialog.Builder builder4 = new AlertDialog.Builder(
//					// activity);
//					//
//					// builder4.setTitle(R.string.tips);
//					// builder4.setMessage(R.string.str_quick_setting_success);
//					//
//					// // // 本地登录才有继续配置
//					// // if (BaseApp.LOCAL_LOGIN_FLAG) {
//					// // builder4.setPositiveButton(
//					// // R.string.str_quick_setting_goon,
//					// // new DialogInterface.OnClickListener() {
//					// // @Override
//					// // public void onClick(DialogInterface dialog,
//					// // int which) {
//					// // Message msg = BaseApp.quickSettingHandler
//					// // .obtainMessage();
//					// // msg.what = JVConst.QUICK_SETTING_WIFI_CONTINUE_SET;
//					// // BaseApp.quickSettingHandler
//					// // .sendMessage(msg);
//					// // if (null != activity
//					// // && !activity.isFinishing()
//					// // && null != dialog) {
//					// // dialog.dismiss();
//					// // }
//					// // }
//					// // });
//					// // }
//					//
//					// builder4.setNegativeButton(
//					// R.string.str_quick_setting_finish,
//					// new DialogInterface.OnClickListener() {
//					// @Override
//					// public void onClick(DialogInterface dialog,
//					// int which) {
//					//
//					// Message msg = BaseApp.quickSettingHandler
//					// .obtainMessage();
//					// msg.what = JVConst.QUICK_SETTING_WIFI_FINISH_SET;
//					// BaseApp.quickSettingHandler
//					// .sendMessage(msg);
//					// if (null != activity
//					// && !activity.isFinishing()
//					// && null != dialog) {
//					// dialog.dismiss();
//					// }
//					// }
//					// });
//					// if (null != activity && !activity.isFinishing()) {
//					// AlertDialog dialog4 = builder4.create();
//					// dialog4.setCancelable(false);
//					// dialog4.show();
//					// }
//					break;
//				case JVConst.QUICK_SETTING_HOST_SET_WIFI_FAIL:// 主控配置wifi失败
//					activity.dismissDialog();
//					// 断开连接
//					APUtil.disConnectVideo();
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
//				// case JVConst.QUICK_SETTING_WIFI_CONTINUE_SET:// 继续配置wifi
//				// // 添加设备线程
//				// Thread thread1 = new Thread() {
//				// public void run() {
//				// if (!BaseApp.LOCAL_LOGIN_FLAG
//				// && BaseApp.deviceList.size() >= 100) {
//				// Message msg = BaseApp.quickSettingHandler
//				// .obtainMessage();
//				// msg.what = JVConst.DEVICE_MOST_COUNT;
//				// BaseApp.quickSettingHandler.sendMessage(msg);
//				// } else {
//				// activity.addDevice();
//				// }
//				// }
//				// };
//				// thread1.start();
//				//
//				// activity.backMethod();
//				// break;
//				case JVConst.QUICK_SETTING_WIFI_FINISH_SET:// 完成配置wifi
//
//					BaseApp.finishSetting = true;
////					 activity.createDialog(R.string.str_quick_setting_alert_6);
//					ChangeWifiThread cwt = new ChangeWifiThread(activity, true);
//					cwt.start();
//					break;
//				// case JVConst.QUICK_SETTING_HOST_SET_WIFI_SUCC:// 主控wifi配置成功
//				// activity.dismissDialog();
//				// if (null != BaseApp.channelMap
//				// && BaseApp.channelMap.size() > 0) {
//				// BaseApp.channelMap.get(0).stopPrimaryChannel(0);
//				// }
//				// JVSUDT.QUICK_SETTING_FLAG = false;
//				//
//				// AlertDialog.Builder builder4 = new AlertDialog.Builder(
//				// activity);
//				//
//				// builder4.setTitle(R.string.tips);
//				// builder4.setMessage(R.string.str_quick_setting_success);
//				//
//				// builder4.setNegativeButton(
//				// R.string.str_quick_setting_finish,
//				// new DialogInterface.OnClickListener() {
//				// @Override
//				// public void onClick(DialogInterface dialog,
//				// int which) {
//				//
//				// Message msg = BaseApp.quickSettingHandler
//				// .obtainMessage();
//				// msg.what = JVConst.QUICK_SETTING_WIFI_FINISH_SET;
//				// BaseApp.quickSettingHandler
//				// .sendMessage(msg);
//				// if (null != activity
//				// && !activity.isFinishing()
//				// && null != dialog) {
//				// dialog.dismiss();
//				// }
//				// }
//				// });
//				// if (null != activity && !activity.isFinishing()) {
//				// AlertDialog dialog4 = builder4.create();
//				// dialog4.setCancelable(false);
//				// dialog4.show();
//				// }
//				//
//				// break;
//				// case JVConst.QUICK_SETTING_HOST_SET_WIFI_FAIL:// 主控配置wifi失败
//				// activity.dismissDialog();
//				// if (null != BaseApp.channelMap
//				// && BaseApp.channelMap.size() > 0) {
//				// BaseApp.channelMap.get(0).stopPrimaryChannel(0);
//				// }
//				// JVSUDT.QUICK_SETTING_FLAG = false;
//				//
//				// AlertDialog.Builder builder5 = new AlertDialog.Builder(
//				// activity);
//				//
//				// builder5.setTitle(R.string.tips);
//				// builder5.setMessage(R.string.str_quick_setting_failed);
//				//
//				// builder5.setPositiveButton(
//				// R.string.str_quick_setting_reset,
//				// new DialogInterface.OnClickListener() {
//				// @Override
//				// public void onClick(DialogInterface dialog,
//				// int which) {
//				// Message msg = BaseApp.quickSettingHandler
//				// .obtainMessage();
//				// msg.what = JVConst.QUICK_SETTING_WIFI_CONTINUE_SET;
//				// BaseApp.quickSettingHandler
//				// .sendMessage(msg);
//				// if (null != activity
//				// && !activity.isFinishing()
//				// && null != dialog) {
//				// dialog.dismiss();
//				// }
//				// }
//				// });
//				// builder5.setNegativeButton(
//				// R.string.str_quick_setting_finish,
//				// new DialogInterface.OnClickListener() {
//				// @Override
//				// public void onClick(DialogInterface dialog,
//				// int which) {
//				//
//				// Message msg = BaseApp.quickSettingHandler
//				// .obtainMessage();
//				// msg.what = JVConst.QUICK_SETTING_WIFI_FINISH_SET;
//				// BaseApp.quickSettingHandler
//				// .sendMessage(msg);
//				// if (null != activity
//				// && !activity.isFinishing()
//				// && null != dialog) {
//				// dialog.dismiss();
//				// }
//				// }
//				// });
//				// if (null != activity && !activity.isFinishing()) {
//				// AlertDialog dialog5 = builder5.create();
//				// dialog5.setCancelable(false);
//				// dialog5.show();
//				// }
//				// break;
//				case JVConst.QUICK_SETTING_WIFI_CHANGED_SUCC:// 网络恢复成功
//					// BaseApp.showTextToast(
//					// activity,
//					// BaseApp.oldWifiSSID
//					// + activity.getResources().getString(
//					// R.string.str_wifi_reset_succ));
//
//					if (BaseApp.LOCAL_LOGIN_FLAG) {// 本地模式下添加设备
//						Log.e("999999",
//								"ooooooooooooooooooooooooooooooooooooooooooooo 1");
//						if(1 == msg.arg1){
//							activity.playSound(JVConst.SOUNDSIX);// 播放“叮”的一声
//							activity.searchView.setSearching(false);
//							activity.quickSetDeviceImg.setVisibility(View.VISIBLE);// 弹出设备
//							BaseApp.quickSettingHandler.postDelayed(new Thread() {// 播放，配置完成声音
//
//										@Override
//										public void run() {
//											// TODO Auto-generated method stub
//											super.run();
//											activity.playSound(JVConst.SOUNDSEVINE);
//										}
//
//									}, 200);
//
//							activity.isSearching = false;
//						}else{
//							activity.dismissDialog();
//							activity.finish();
//						}
//						
//					} else {
//						if (1 == msg.arg1) {// 在线模式下，上线流程
//							new OnLineThread().start();
//							Log.e("999999",
//									"ooooooooooooooooooooooooooooooooooooooooooooo 2");
//						} else {
//							Log.e("999999",
//									"ooooooooooooooooooooooooooooooooooooooooooooo 3");
////							activity.playSound(JVConst.SOUNDSIX);
////							activity.searchView.setSearching(false);
////							activity.quickSetDeviceImg
////									.setVisibility(View.VISIBLE);
////							activity.playSound(JVConst.SOUNDSEVINE);
//							activity.finish();
//						}
//					}
//
//					break;
//				case JVConst.QUICK_SETTING_DEV_START_ONLINE:// 设备正在努力上线
//					// activity.createDialog(R.string.str_dev_try_online);
//					break;
//				case JVConst.QUICK_SETTING_DEV_ONLINE_FAILED:// 上线失败
////					activity.dismisQuickPopWindow();
//					activity.dismissDialog();
//					activity.onlineFailedDialog();
////					activity.searchView.setSearching(false);
////					activity.isSearching = false;
//					Log.e("999999",
//							"ooooooooooooooooooooooooooooooooooooooooooooo 4");
//					break;
//				case JVConst.QUICK_SETTING_DEV_ONLINE_SUCC:// 上线成功
//					activity.playSound(JVConst.SOUNDSIX);
//					activity.searchView.setSearching(false);
//					activity.quickSetDeviceImg.setVisibility(View.VISIBLE);
//					BaseApp.quickSettingHandler.postDelayed(new Thread() {// 播放，配置完成声音
//
//								@Override
//								public void run() {
//									// TODO Auto-generated method stub
//									super.run();
//									activity.playSound(Consts.SOUNDSEVINE);
//								}
//
//							}, 200);
//					Log.e("999999",
//							"ooooooooooooooooooooooooooooooooooooooooooooo 5");
//					activity.playSound(JVConst.SOUNDSEVINE);
//					activity.isSearching = false;
//					break;
//				case JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL:
//					// activity.dismissDialog();
//					BaseApp.showTextToast(
//							activity,
//							BaseApp.oldWifiSSID
//									+ activity.getResources().getString(
//											R.string.str_wifi_reset_fail));
//					activity.searchView.setSearching(false);
//					activity.dismisQuickPopWindow();
//					activity.finish();
//					activity.isSearching = false;
//					activity.searchView.stopPlayer();
//					Log.e("999999",
//							"ooooooooooooooooooooooooooooooooooooooooooooo 6");
//					break;
//
//				}
//			}
//		}
//
//	}
//
//	/**
//	 * 设备升级Dialog
//	 * 
//	 * @param tag
//	 */
//	private void updateDialog() {
//		APUtil.disConnectVideo();
//		AlertDialog.Builder builder = new AlertDialog.Builder(
//				JVQuickSettingActivity.this);
//		builder.setCancelable(false);
//
//		builder.setTitle(getResources().getString(
//				R.string.str_quick_setting_new_dev));
//		builder.setMessage(getResources().getString(
//				R.string.str_quick_setting_new_dev_tips));
//
//		builder.setPositiveButton(R.string.str_check_now,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						helpLayout.setVisibility(View.VISIBLE);
//						saveSet.setVisibility(View.GONE);
//						currentMenu
//								.setText(R.string.str_quick_setting_devupdate_order);
//						//暂停扫瞄器
//						if(null != searchView){
//							searchView.setSearching(false);
//							isSearching = false;
//							searchView.stopPlayer();
//						}
//						if(null != BaseApp.mediaPlayer){
//							BaseApp.mediaPlayer.stop();
//						}
//						
//						dismisQuickPopWindow();
//						
//					}
//
//				});
//		builder.setNegativeButton(R.string.login_str_close,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						//暂停扫瞄器
//						if(null != searchView){
//							searchView.setSearching(false);
//							isSearching = false;
//							searchView.stopPlayer();
//						}
//						if(null != BaseApp.mediaPlayer){
//							BaseApp.mediaPlayer.stop();
//						}
//						
//						dismisQuickPopWindow();
//						
//						backMethod();
//						dialog.dismiss();
//					}
//
//				});
//
//		builder.show();
//	}
//
//	/**
//	 * 切换wifi线程
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//
//	private static class ChangeWifiThread extends Thread {
//		private final WeakReference<JVQuickSettingActivity> mActivity;
//		private boolean addFlag = false;
//
//		public ChangeWifiThread(JVQuickSettingActivity activity, boolean flag) {
//			mActivity = new WeakReference<JVQuickSettingActivity>(activity);
//			addFlag = flag;
//		}
//
//		@Override
//		public void run() {
//			super.run();
//			Looper.prepare();
//			JVQuickSettingActivity activity = mActivity.get();
//			try {
//				if (null != activity && !activity.isFinishing()) {
//					boolean flag = activity.changeWifi();
//					int result = -1;
//					int time = 0;
//					Log.v("网络恢复完成", flag + "");
//					if (flag) {// 网络恢复成功
//
//						if (!BaseApp.LOCAL_LOGIN_FLAG) {// 非本地登陆，重新登录
//							if (activity.hasLogout) {// 已经注销过
//								while (JVAccountConst.SUCCESS != result
//										&& time <= 5) {
//									time++;
//									try {
//										Thread.sleep(3 * 1000);
//									} catch (InterruptedException e) {
//										e.printStackTrace();
//									}
//									result = AccountUtil.userLogin(
//											LoginUtil.userName,
//											LoginUtil.passWord);
//									Log.v("网络恢复完成---重新登录---"
//											+ LoginUtil.userName, result + "");
//								}
//							} else {
//								result = JVAccountConst.SUCCESS;
//							}
//
//						}
//
//						if (addFlag) {
//							BaseApp.broadIP = false;
//							// 开启局域网广播，广播设备IP
//							BaseApp.IPCDEVICE.getGroupYST();
//							String group = BaseApp.IPCDEVICE.group;
//							int deviceYSTNO = BaseApp.IPCDEVICE.yst;
//							JVSUDT.JVC_QueryDevice(group, deviceYSTNO,
//									40 * 1000);
//
//							while (!BaseApp.broadIP) {// 未广播到IP
//								Thread.sleep(2 * 1000);
//								Log.v("未广播到IP---", "休眠2秒");
//							}
//						}
//
//						if (!BaseApp.LOCAL_LOGIN_FLAG) {// 在线
//							if (JVAccountConst.SUCCESS == result && addFlag) {
//								if (!BaseApp.LOCAL_LOGIN_FLAG
//										&& BaseApp.deviceList.size() >= 100) {
//									Message msg = BaseApp.quickSettingHandler
//											.obtainMessage();
//									msg.what = JVConst.DEVICE_MOST_COUNT;
//									BaseApp.quickSettingHandler
//											.sendMessage(msg);
//									Thread.sleep(3000);
//									return;
//								} else {
//									APUtil.addDevice();
//								}
//							} else if (JVAccountConst.SUCCESS != result
//									&& addFlag) {
//								Message msg = BaseApp.quickSettingHandler
//										.obtainMessage();
//								msg.what = JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL;
//								BaseApp.quickSettingHandler.sendMessage(msg);
//								return;
//							}
//						} else {// 本地登陆
//							if (addFlag) {
//								APUtil.addDevice();
//							}
//						}
//
//						Message msg = BaseApp.quickSettingHandler
//								.obtainMessage();
//						if (addFlag) {
//							msg.arg1 = 1;
//						}
//
//						msg.what = JVConst.QUICK_SETTING_WIFI_CHANGED_SUCC;
//						BaseApp.quickSettingHandler.sendMessage(msg);
//
//					} else {// 网络恢复失败
//						Message msg = BaseApp.quickSettingHandler
//								.obtainMessage();
//						msg.what = JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL;
//						BaseApp.quickSettingHandler.sendMessage(msg);
//					}
//
//				}
//
//				// if (!BaseApp.LOCAL_LOGIN_FLAG && flag) {// 非本地登录而且网络恢复成功
//				// while (JVAccountConst.SUCCESS != result && time <= 5) {
//				// time++;
//				// try {
//				// Thread.sleep(3 * 1000);
//				// } catch (InterruptedException e) {
//				// e.printStackTrace();
//				// }
//				// result = AccountUtil.userLogin(LoginUtil.userName,
//				// LoginUtil.passWord);
//				// Log.v("网络恢复完成---重新登录---" + LoginUtil.userName,
//				// result + "");
//				// }
//				// if (JVAccountConst.SUCCESS == result && addFlag) {
//				// if (!BaseApp.LOCAL_LOGIN_FLAG
//				// && BaseApp.deviceList.size() >= 100) {
//				// Message msg = BaseApp.quickSettingHandler
//				// .obtainMessage();
//				// msg.what = JVConst.DEVICE_MOST_COUNT;
//				// BaseApp.quickSettingHandler.sendMessage(msg);
//				// Thread.sleep(3000);
//				// } else {
//				// APUtil.addDevice();
//				// }
//				// } else if (JVAccountConst.SUCCESS != result && addFlag) {
//				// Message msg = BaseApp.quickSettingHandler
//				// .obtainMessage();
//				// msg.what = JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL;
//				// BaseApp.quickSettingHandler.sendMessage(msg);
//				// return;
//				// }
//				// } else if (BaseApp.LOCAL_LOGIN_FLAG && addFlag) {// 添加设备标识位
//				// APUtil.addDevice();
//				// }
//				//
//				// if (null != BaseApp.quickSettingHandler) {
//				// Message msg = BaseApp.quickSettingHandler
//				// .obtainMessage();
//				// if (flag) {
//				// msg.what = JVConst.QUICK_SETTING_WIFI_CHANGED_SUCC;
//				// } else {
//				// msg.what = JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL;
//				// }
//				// BaseApp.quickSettingHandler.sendMessage(msg);
//				// }
//				//
//				// }
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//
//		}
//
//	};
//
//	/**
//	 * 切换到原来网络
//	 * 
//	 * @return
//	 */
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
//							Log.i("完成配置断开", ipcWifiName);
//							wifiAdmin.disconnectWifi(currWifi, true);
//							try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//
//					// 完成配置连上原来的wifi
//					if (null != BaseApp.oldWifiSSID) {
//						changeRes = getWifiState(BaseApp.oldWifiSSID);
//
//						if (changeRes) {
//							return changeRes;
//						}
//
//						WifiConfiguration oldWifi = wifiAdmin
//								.isExsits(BaseApp.oldWifiSSID);
//						if (null != oldWifi) {
//							Log.i("完成配置连接", BaseApp.oldWifiSSID);
//							boolean connRes = false;
//							int count = 0;
//							while (!connRes) {// 没连接调用连接方法
//								if (count < 10) {
//									count++;
//									try {
//										Thread.sleep(1000);
//									} catch (InterruptedException e) {
//										e.printStackTrace();
//									}
//									connRes = wifiAdmin.connNetwork(oldWifi);
//									Log.i("完成配置", BaseApp.oldWifiSSID + "----"
//											+ count + "-----调用连接-----"
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
//											e.printStackTrace();
//										}
//										changeRes = getWifiState(oldWifi.SSID);
//										Log.i("完成配置", BaseApp.oldWifiSSID
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
//						} else {
//							// 若以前没有连接过此网络，重新连接
//							boolean ipcFlag = connectWifi(
//									BaseApp.oldWifiSSID,
//									getResources().getString(
//											R.string.str_wifi_pwd));
//							if (ipcFlag) {
//								changeRes = true;
//							} else {
//								changeRes = false;
//							}
//						}
//					} else {
//						changeRes = true;
//					}
//				} else {// 原wifi关闭状态，关闭wifi
//					Log.e("tags",
//							"close wifi-------------------------------------");
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
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//		if (JVConst.AP_CONNECT_REQUEST == requestCode) {
//			switch (resultCode) {
//			case JVConst.AP_CONNECT_FINISHED:// AP检测视频完成
//				showIpcLayout(false);// ipc列表切换Wi-Fi列表
//				// 播放提示声音
//				playSound(JVConst.SOUNDFOUR);
//				break;
//			}
//		}
//
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		super.onKeyDown(keyCode, event);
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			// backMethod();
//			if (isSearching) {// 正在搜索设备是，提示是否退出
//				Log.e("tags", "back isSearching");
//				backDialog();
//			} else if(View.VISIBLE == helpLayout.getVisibility()){//更新设备帮助文档
//				helpLayout.setVisibility(View.GONE);
//				saveSet.setVisibility(View.VISIBLE);
//				currentMenu.setText(R.string.str_quick_setting);
//				Log.e("tags", "back helpLayout.getVisibility()");
//			}else{
//				Log.e("tags", "back ");
//				dismisQuickPopWindow();
//				backMethod();
////				finish();
//
//			}
//			return false;
//		}
//		return true;
//	}
//
//	@Override
//	protected void onDestroy() {
//		if (null != LoginUtil.activityList
//				&& 0 != LoginUtil.activityList.size()) {
//			LoginUtil.activityList.remove(JVQuickSettingActivity.this);
//		}
//		if (null != quickSetPop) {
//			quickSetPop.dismiss();
//		}
//		if (null != BaseApp.mediaPlayer && BaseApp.mediaPlayer.isPlaying()) {
//			BaseApp.mediaPlayer.stop();
//		}
//		System.gc();
//		super.onDestroy();
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//	}
//
//	/**
//	 * 获取IPC设备
//	 * 
//	 * @return
//	 */
//	public static Device getIPCDevice() {
//		String deviceName = ipcWifiName;
//		String str0 = deviceName.replace("\"", "");
//		String str = str0.substring(6, str0.length());
//
//		BaseApp.IPCDEVICE = new Device();
//		BaseApp.IPCDEVICE.deviceNum = str;
//		BaseApp.IPCDEVICE.deviceName = str;
//		BaseApp.IPCDEVICE.getGroupYST();
//		BaseApp.IPCDEVICE.deviceLoginUser = BaseApp.IPC_DEFAULT_USER;
//		BaseApp.IPCDEVICE.deviceLoginPwd = BaseApp.IPC_DEFAULT_PWD;
//		BaseApp.IPCDEVICE.deviceLocalIp = BaseApp.IPC_DEFAULT_IP;
//		BaseApp.IPCDEVICE.deviceLocalPort = BaseApp.IPC_DEFAULT_PORT;
//		BaseApp.IPCDEVICE.devicePointCount = 1;
//
//		ArrayList<ConnPoint> connPointList = new ArrayList<ConnPoint>();
//		if (BaseApp.IPCDEVICE.devicePointCount > 0) {
//			for (int i = 0; i < BaseApp.IPCDEVICE.devicePointCount; i++) {
//				ConnPoint connPoint = new ConnPoint();
//				connPoint.deviceID = BaseApp.IPCDEVICE.deviceOID;// temObj.getInt("DeviceID");
//				connPoint.pointNum = i + 1;// temObj.getInt("PointNum");
//				connPoint.pointOwner = BaseApp.IPCDEVICE.deviceOID;// temObj.getInt("Owner");
//				connPoint.pointName = BaseApp.IPCDEVICE.deviceNum + "_"
//						+ (i + 1);// temObj.getString("Name");
//				connPoint.pointOID = BaseApp.IPCDEVICE.deviceOID;// temObj.getInt("OID");
//				connPoint.isParent = false;
//				connPointList.add(connPoint);
//			}
//		}
//		BaseApp.IPCDEVICE.pointList = connPointList;
//
//		return BaseApp.IPCDEVICE;
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
//				if (BaseApp.isConnected(JVQuickSettingActivity.this)) {
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
//	/**
//	 * 网络连接失败Dialog
//	 * 
//	 * @param tag
//	 */
//	private void netErrorDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(
//				JVQuickSettingActivity.this);
//		builder.setCancelable(true);
//
//		builder.setTitle(getResources().getString(
//				R.string.str_quick_setting_neterror));
//		builder.setMessage(R.string.net_errors);
//
//		builder.setPositiveButton(R.string.str_sure,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						dialog.dismiss();
//					}
//
//				});
//		builder.create().show();
//	}
//
//	/**
//	 * 设备上线失败Dialog
//	 * 
//	 * @param tag
//	 */
//	private void onlineFailedDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(
//				JVQuickSettingActivity.this);
//		builder.setCancelable(false);
//
//		builder.setTitle(R.string.tips);
//		builder.setMessage(R.string.str_online_fail);
//
//		builder.setPositiveButton(R.string.str_try_again,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						new OnLineThread().start();
//					}
//
//				});
//		builder.setNegativeButton(R.string.str_crash_cancel,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						dialog.dismiss();
//						JVQuickSettingActivity.this.finish();
//					}
//
//				});
//
//		builder.create().show();
//	}
//
//	public boolean hasShowTips = false;// 是否显示过Ap配置流程
//
//	/**
//	 * 配置流程Dialog
//	 * 
//	 * @param tag
//	 */
//	// private void alertTipsDialog() {
//	// hasShowTips = true;
//	// AlertDialog.Builder builder = new AlertDialog.Builder(
//	// JVQuickSettingActivity.this);
//	// builder.setCancelable(false);
//	//
//	// builder.setTitle(getResources().getString(R.string.str_device_ap_pro));
//	// LayoutInflater li = JVQuickSettingActivity.this.getLayoutInflater();
//	// LinearLayout layout = (LinearLayout) li.inflate(R.layout.ap_set_tips,
//	// null);
//	// ToggleButton noAlert = (ToggleButton) layout.findViewById(R.id.noalert);
//	// noAlert.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//	//
//	// @Override
//	// public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//	// editor.putBoolean("AP_TIPS", arg1);
//	// editor.commit();
//	// }
//	//
//	// });
//	//
//	// builder.setView(layout);
//	//
//	// builder.setPositiveButton(R.string.str_cancel,
//	// new DialogInterface.OnClickListener() {
//	// @Override
//	// public void onClick(DialogInterface dialog, int which) {
//	// backMethod();
//	// dialog.dismiss();
//	// }
//	//
//	// });
//	// builder.setNegativeButton(R.string.str_device_ap_start,
//	// new DialogInterface.OnClickListener() {
//	//
//	// @Override
//	// public void onClick(DialogInterface dialog, int which) {
//	// dialog.dismiss();
//	// }
//	//
//	// });
//	// Dialog dialog = builder.create();
//	// dialog.show();
//	// }
//
//	/**
//	 * 视频连接
//	 * 
//	 * @param device
//	 */
//	private void connectDevice(Device device) {
//		Log.i("AP功能", "调用视频连接");
//		APUtil.connect(device);
//	}
//
//	/**
//	 * 播放声音
//	 */
//	@Override
//	public void playSound(int soundType) {
//		// TODO Auto-generated method stub
//		try {
//			// 打开指定音乐文件
//			String file = "";
//			if (BaseApp.getLan() == JVConst.LANGUAGE_ZH) {
//				switch (soundType) {
//				case JVConst.SOUNDONE:// 选择设备，并进入预览
//					file = "1.mp3";
//					break;
//				case JVConst.SOUNDTOW:// 正在连接设备
//					file = "2.mp3";
//					break;
//				case JVConst.SOUNDTHREE:// 请点击下一步，配置无线网络
//					file = "3.mp3";
//					break;
//				case JVConst.SOUNDFOUR:// 请选择无线路由，输入密码，并点击右上角声波配置按钮
//					file = "4.mp3";
//					break;
//				case JVConst.SOUNDFIVE:// 请将手机靠近设备
//					file = "5.mp3";
//					break;
//				case JVConst.SOUNDSIX:// 叮
//					file = "6.mp3";
//					break;
//				case JVConst.SOUNDSEVINE:// 配置完成，请点击图标查看设备
//					file = "7.mp3";
//					break;
//				case JVConst.SOUNDEIGHT:// 搜索声音
//					file = "quicksetsound.mp3";
//					break;
//				default:
//					break;
//				}
//			} else {
//				switch (soundType) {
//				case JVConst.SOUNDONE:// 选择设备，并进入预览
//					file = "1_en.wav";
//					break;
//				case JVConst.SOUNDTOW:// 正在连接设备
//					file = "2_en.wav";
//					break;
//				case JVConst.SOUNDTHREE:// 请点击下一步，配置无线网络
//					file = "3_en.wav";
//					break;
//				case JVConst.SOUNDFOUR:// 请选择无线路由，输入密码，并点击右上角声波配置按钮
//					file = "4_en.wav";
//					break;
//				case JVConst.SOUNDFIVE:// 请将手机靠近设备
//					file = "5_en.wav";
//					break;
//				case JVConst.SOUNDSIX:// 叮
//					file = "6.mp3";
//					break;
//				case JVConst.SOUNDSEVINE:// 配置完成，请点击图标查看设备
//					file = "7_en.wav";
//					break;
//				case JVConst.SOUNDEIGHT:// 搜索声音
//					file = "quicksetsound.mp3";
//					break;
//				default:
//					break;
//				}
//			}
//
//			int maxVolume = 100; // 最大音量值
//			int curVolume = 20; // 当前音量值
//
//			AudioManager audioMgr = null; // Audio管理器，用了控制音量
//			audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//			// 获取最大音乐音量
//			maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//			// 初始化音量大概为最大音量的1/2
//			// curVolume = maxVolume / 2;
//			// 每次调整的音量大概为最大音量的1/6
//			audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume,
//					AudioManager.FLAG_PLAY_SOUND);
//			AssetManager assetMgr = this.getAssets();
//			; // 资源管理器
//			AssetFileDescriptor afd = assetMgr.openFd(file);
//			if (null == BaseApp.mediaPlayer) {
//				BaseApp.mediaPlayer = new MediaPlayer();
//			}
//			BaseApp.mediaPlayer.reset();
//
//			// 使用MediaPlayer加载指定的声音文件。
//			BaseApp.mediaPlayer.setDataSource(afd.getFileDescriptor(),
//					afd.getStartOffset(), afd.getLength());
//			// 准备声音
//			BaseApp.mediaPlayer.prepare();
//			// 播放
//			BaseApp.mediaPlayer.start();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 弹出配置搜索对话框
//	 */
//	private void initPopwindow() {
//		try {
//			LayoutInflater layoutInflater = LayoutInflater.from(this);
//			View view = layoutInflater
//					.inflate(R.layout.quicksetsearching, null);
//			Display display = getWindowManager().getDefaultDisplay();
//			int screenWidth = display.getWidth();
//			int screenHeight = display.getHeight();
//			quickSetPop = new PopupWindow(view, screenWidth, screenHeight);
//
//			/**
//			 * 为控件添加事件
//			 * 
//			 */
//			quickSetBackImg = (Button) view.findViewById(R.id.quickSetBack);
//			quickSetBackImg.setOnClickListener(quickSetOnClick);
//
//			quickSetDeviceImg = (ImageView) view
//					.findViewById(R.id.quickSetDeviceImg);
//			quickSetDeviceImg.setOnClickListener(quickSetOnClick);
//			searchView = (SearchDevicesView) view.findViewById(R.id.searchView);
//			searchView.setWillNotDraw(false);
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * 显示
//	 */
//	private void showQuickPopWindow() {
//		if (null != quickSetPop) {
//			quickSetPop.showAtLocation(quickSetParentLayout, Gravity.CENTER, 0,
//					0);
//		}
//	}
//
//	/**
//	 * 关闭对话框
//	 */
//	private void dismisQuickPopWindow() {
//		if (null != quickSetPop && quickSetPop.isShowing()) {
//			quickSetPop.dismiss();
//		}
//	}
//
//	/**
//	 * 配置设备弹出框单机事件
//	 */
//	OnClickListener quickSetOnClick = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.quickSetBack:// 返回
//
//				break;
//			case R.id.quickSetDeviceImg:
//				finish();
//				break;
//			}
//		}
//	};
//
//	public void backDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(
//				JVQuickSettingActivity.this);
//		builder.setCancelable(false);
//
//		// builder.setTitle(getResources().getString(
//		// R.string));
//		builder.setMessage(getResources().getString(R.string.str_sureExit));
//
//		builder.setPositiveButton(R.string.str_quick_setting_waiting,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//
//					}
//
//				});
//		builder.setNegativeButton(R.string.str_quick_back,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
////						dismisQuickPopWindow();
//						
//						//暂停扫瞄器
//						if(null != searchView){
//							searchView.setSearching(false);
//							isSearching = false;
//							searchView.stopPlayer();
//						}
//						if(null != BaseApp.mediaPlayer){
//							BaseApp.mediaPlayer.stop();
//						}
//						
//						dismisQuickPopWindow();
//						
//						backMethod();
////						dialog.dismiss();
//						
////						backMethod();
////						dialog.dismiss();
//					}
//
//				});
//
//		builder.show();
//	}
//
//	private boolean isPressHomeKey = false;// 监听home键的专有标志位：false：点击home键；true为其它操作
//
//	@Override
//	protected void onUserLeaveHint() {
//		// TODO Auto-generated method stub
//		super.onUserLeaveHint();
//		if (!isPressHomeKey) {
//			if (null != BaseApp.mediaPlayer) {
//				BaseApp.mediaPlayer.stop();
//				searchView.setSearching(false);
//			}
//		}
//	}
//
//	@Override
//	public void onHandler(int what, int arg1, int arg2, Object obj) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onNotify(int what, int arg1, int arg2, Object obj) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Looper;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.IpcWifiAdapter;
import com.jovision.adapters.MobileWifiAdapter;
import com.jovision.bean.WifiAdmin;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVConst;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.views.RefreshableListView;
import com.jovision.views.RefreshableListView.OnRefreshListener;

public class JVQuickSettingActivity extends ShakeActivity {
	private final String TAG = "Qick_SetQick_Set";
	private String setIpcName;// 待设置的IPC
	private String deviceNum;// 待设置的IPC云视通号
	private String desWifiSSID;//
	private String desWifiPWD;//
	private Button back;
	private Button saveSet;
	private TextView currentMenu;

	/** IPCwifi列表 */
	private LinearLayout ipcLayout;
	private ImageView ipcImage;
	/** IPC网络 */
	private ArrayList<ScanResult> scanIpcWifiList = new ArrayList<ScanResult>();
	private RefreshableListView ipcWifiListView;
	private IpcWifiAdapter ipcAdapter;

	/** 手机wifi列表 */
	private LinearLayout mobileLayout;
	private EditText desWifiName;
	private EditText desWifiPass;
	private ToggleButton destWifiEye;
	/** 手机wifi列表（除IPC） */
	private ArrayList<ScanResult> scanMobileWifiList = new ArrayList<ScanResult>();
	private RefreshableListView mobileWifiListView;
	private MobileWifiAdapter mobileAdapter;

	private String oldWifiSSID;
	private WifiAdmin wifiAdmin;
	private int connectFailedCounts = 0;// 连接设备失败次数

	@Override
	protected void initSettings() {
		// 首次提示设置步骤
		if (!MySharedPreference.getBoolean("AP_TIPS")) {
			alertTipsDialog();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		setContentView(R.layout.quicksetting_layout);
		Intent intent = getIntent();
		if (null != intent) {
			oldWifiSSID = intent.getStringExtra("OLD_WIFI");
			scanIpcWifiList = intent.getParcelableArrayListExtra("IPC_LIST");
			scanMobileWifiList = intent
					.getParcelableArrayListExtra("MOBILE_LIST");
		}

		wifiAdmin = new WifiAdmin(JVQuickSettingActivity.this);

		back = (Button) findViewById(R.id.back);
		saveSet = (Button) findViewById(R.id.btn_right);
		back.setVisibility(View.VISIBLE);
		saveSet.setTextColor(Color.WHITE);
		saveSet.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.setting_save));
		saveSet.setText(getResources().getString(
				R.string.str_quick_setting_connect));
		saveSet.setVisibility(View.VISIBLE);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_quick_setting);
		back.setOnClickListener(onClickListener);
		saveSet.setOnClickListener(onClickListener);

		/** IPCwifi列表 */
		ipcLayout = (LinearLayout) findViewById(R.id.ipcwifilayout);
		ipcWifiListView = (RefreshableListView) findViewById(R.id.ipcwifilistview);
		ipcImage = new ImageView(JVQuickSettingActivity.this);
		ipcImage.setImageDrawable(getResources().getDrawable(
				R.drawable.ipc_banner));
		ipcWifiListView.addHeaderView(ipcImage);
		ipcWifiListView.setOnRefreshListener(new WifiRefreshListener(true));
		ipcWifiListView.setOnItemClickListener(mOnItemClickListener);
		ipcAdapter = new IpcWifiAdapter(JVQuickSettingActivity.this);
		if (null != scanIpcWifiList && 0 != scanIpcWifiList.size()) {
			ipcAdapter.setData(scanIpcWifiList, oldWifiSSID);
			ipcWifiListView.setAdapter(ipcAdapter);
		}

		/** 手机wifi列表 */
		mobileLayout = (LinearLayout) findViewById(R.id.mobilewifilayout);
		desWifiName = (EditText) findViewById(R.id.deswifiname);
		desWifiPass = (EditText) findViewById(R.id.deswifipwd);
		destWifiEye = (ToggleButton) findViewById(R.id.deswifieye);
		mobileWifiListView = (RefreshableListView) findViewById(R.id.mobilewifilistview);
		mobileWifiListView.setOnRefreshListener(new WifiRefreshListener(false));
		mobileWifiListView.setOnItemClickListener(mOnItemClickListener);
		mobileAdapter = new MobileWifiAdapter(JVQuickSettingActivity.this);
		if (null != scanMobileWifiList && 0 != scanMobileWifiList.size()) {
			mobileAdapter.setData(scanMobileWifiList, oldWifiSSID);
			mobileWifiListView.setAdapter(mobileAdapter);
		}
		destWifiEye.setChecked(false);
		destWifiEye.setOnCheckedChangeListener(myOnCheckedChangeListener);

		showIpcLayout(true);
	}

	/**
	 * wifi列表刷新事件
	 * 
	 * @author Administrator
	 * 
	 */
	private class WifiRefreshListener implements OnRefreshListener {
		boolean isIpc = false;

		public WifiRefreshListener(boolean b) {
			isIpc = b;
		}

		@Override
		public void onRefresh(RefreshableListView listView) {
			// TODO Auto-generated method stub
			createDialog(R.string.str_quick_setting_alert);
			RefreshWifiThread giwt = new RefreshWifiThread(
					JVQuickSettingActivity.this, isIpc);
			giwt.start();
		}

	}

	/**
	 * 显示ipc或手机界面
	 * 
	 * @param show
	 */
	private void showIpcLayout(boolean show) {
		if (show) {
			ipcLayout.setVisibility(View.VISIBLE);
			mobileLayout.setVisibility(View.GONE);
			saveSet.setVisibility(View.GONE);
			desWifiName.setText("");
			desWifiPass.setText("");
		} else {
			ipcLayout.setVisibility(View.GONE);
			mobileLayout.setVisibility(View.VISIBLE);
			saveSet.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 密码显示隐藏
	 */
	OnCheckedChangeListener myOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				desWifiPass
						.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);// 显示密码
			} else {
				desWifiPass.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);// 隐藏密码
			}
			desWifiPass.setSelection(desWifiPass.getText().toString().length());
			destWifiEye.setChecked(arg1);
		}

	};

	/**
	 * 连接wifi 30s超时
	 * 
	 * @param wifi
	 * @param password
	 * @return
	 */
	public boolean connectWifi(String wifi, String password) {

		boolean flag = wifiAdmin.getWifiState(wifi);
		int errorCount = 0;
		while (!flag) {
			MyLog.v(TAG, "wifi=" + wifi + ";errorCount=" + errorCount
					+ ";flag=" + flag);
			if (errorCount > 30) {
				break;
			}
			if (errorCount == 0 || errorCount == 10 || errorCount == 20) {
				WifiConfiguration desWifi = wifiAdmin.isExsits(wifi);
				if (null == desWifi) {
					int enc = 0;
					if ("".equalsIgnoreCase(password)) {
						enc = 1;
					} else {
						enc = 3;
					}
					desWifi = wifiAdmin.CreateWifiInfo(wifi, password, enc);
				}

				wifiAdmin.ConnectWifiByConfig(desWifi);
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			errorCount += 2;

			flag = wifiAdmin.getWifiState(wifi);
		}

		return flag;
	}

	/**
	 * AP设置线程
	 */
	Thread apSetThread = new Thread() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			// 非本地登录，调用注销
			if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
				// AccountUtil.userLogout();
			}
			handler.sendMessage(handler.obtainMessage(JVConst.AP_SET_ORDER, 2,
					0));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 和待配置的网络一样关闭网络并移除
			if (null != oldWifiSSID
					&& desWifiSSID.equalsIgnoreCase(oldWifiSSID)) {
				WifiConfiguration oldWifi = wifiAdmin.isExsits(oldWifiSSID);
				if (null != oldWifi) {
					wifiAdmin.disconnectWifi(oldWifi, true);// 关掉，移除,不移除会记住原来的密码
				}
			}
			handler.sendMessage(handler.obtainMessage(JVConst.AP_SET_ORDER, 3,
					0));

			boolean mobileFlag = connectWifi(desWifiSSID, desWifiPWD);

			if (!mobileFlag) {
				handler.sendMessage(handler.obtainMessage(
						JVConst.MOBILE_WIFI_CON_FAILED, -1, 0));
				return;
			}
			handler.sendMessage(handler.obtainMessage(JVConst.AP_SET_ORDER, 4,
					0));

			boolean ipcFlag = connectWifi(setIpcName,
					getResources().getString(R.string.str_wifi_pwd));

			if (!ipcFlag) {
				handler.sendMessage(handler.obtainMessage(
						JVConst.IPC_WIFI_CON_FAILED, -1, 0));
				return;
			}
			handler.sendMessage(handler.obtainMessage(JVConst.AP_SET_ORDER, 5,
					0));

			connectDevice();

			super.run();
		}

	};

	/**
	 * 连接设备
	 * 
	 * @param ipcWifi
	 */
	private void connectDevice() {

		Jni.connect(Consts.CHANNEL_JY, 0, JVConst.IPC_DEFAULT_IP,
				JVConst.IPC_DEFAULT_PORT, JVConst.IPC_DEFAULT_USER,
				JVConst.IPC_DEFAULT_PWD, ConfigUtil.getYST(deviceNum),
				ConfigUtil.getGroup(deviceNum), true, 1, true, 6, null, false);
	}

	/**
	 * 断开视频
	 */
	private void disconnectDevice() {
		Jni.disconnect(Consts.CHANNEL_JY);
	}

	/**
	 * 获取IPC待配置设备列表线程
	 * 
	 * @author Administrator
	 * 
	 */
	private class RefreshWifiThread extends Thread {
		private final WeakReference<JVQuickSettingActivity> mActivity;
		private boolean isIpc = false;

		public RefreshWifiThread(JVQuickSettingActivity activity, boolean ipc) {
			mActivity = new WeakReference<JVQuickSettingActivity>(activity);
			isIpc = ipc;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				JVQuickSettingActivity activity = mActivity.get();
				if (null != activity) {
					if (isIpc) {// 刷新ipcwifi
						if (wifiAdmin.getWifiState()) {
							scanIpcWifiList = wifiAdmin.startScanIPC();
						}
						if (null == scanIpcWifiList
								|| 0 == scanIpcWifiList.size()) {
							scanIpcWifiList = wifiAdmin.startScanIPC();
						}

						if (null != scanIpcWifiList
								&& 0 != scanIpcWifiList.size()) {
							handler.sendMessage(handler.obtainMessage(
									JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS, -1,
									0));
						} else {
							handler.sendMessage(handler.obtainMessage(
									JVConst.QUICK_SETTING_IPC_WIFI_FAILED, -1,
									0));
						}
					} else {// 刷新手机wifi
						if (wifiAdmin.getWifiState()) {
							scanMobileWifiList = wifiAdmin.startScanWifi();
						}
						if (null == scanMobileWifiList
								|| 0 == scanMobileWifiList.size()) {
							scanMobileWifiList = wifiAdmin.startScanWifi();
						}
						handler.sendMessage(handler.obtainMessage(
								JVConst.QUICK_SETTING_MOBILE_WIFI_SUCC, -1, 0));
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 配置流程Dialog
	 * 
	 * @param tag
	 */
	private void alertTipsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				JVQuickSettingActivity.this);
		builder.setCancelable(false);

		builder.setTitle(getResources().getString(R.string.str_device_ap_pro));
		LayoutInflater li = JVQuickSettingActivity.this.getLayoutInflater();
		LinearLayout layout = (LinearLayout) li.inflate(R.layout.ap_set_tips,
				null);
		ToggleButton noAlert = (ToggleButton) layout.findViewById(R.id.noalert);
		noAlert.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				MySharedPreference.putBoolean("AP_TIPS", arg1);
			}

		});

		builder.setView(layout);

		builder.setPositiveButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// backMethod(R.string.str_quick_setting_exsit);
						dialog.dismiss();
					}

				});
		builder.setNegativeButton(R.string.str_device_ap_start,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}

				});
		Dialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 网络连接失败Dialog
	 * 
	 * @param tag
	 */
	private void netErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				JVQuickSettingActivity.this);
		builder.setCancelable(true);

		builder.setTitle(getResources().getString(
				R.string.str_quick_setting_alert2));
		builder.setMessage(R.string.net_errors);

		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}

				});
		builder.create().show();
	}

	/**
	 * onclick事件
	 */
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				backMethod();
				break;
			case R.id.btn_right:// 保存按钮

				desWifiSSID = desWifiName.getText().toString();

				if ("".equalsIgnoreCase(desWifiSSID)) {
					showTextToast(R.string.str_wifiname_not_null);
					break;
				}
				desWifiPWD = desWifiPass.getText().toString();

				handler.sendMessage(handler.obtainMessage(JVConst.AP_SET_ORDER,
						1, 0));

				apSetThread.start();
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backMethod();
		}
		return true;
	}

	/**
	 * 返回方法
	 * 
	 * @param id
	 */
	public void backMethod() {
		try {
			if (ipcLayout.getVisibility() == View.VISIBLE) {// 显示IPC网络信息列表（一级级wifi列表）
				createDialog(R.string.str_quick_setting_alert_6);
				ChangeWifiThread cwt = new ChangeWifiThread(
						JVQuickSettingActivity.this, false);
				cwt.start();
			} else {// 显示主控wifi信息列表（二级wifi列表）
				showIpcLayout(true);
				disconnectDevice();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 两个wifi列表点击事件
	 */
	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			try {
				if (ipcLayout.getVisibility() == View.VISIBLE) {// IPC设备列表
					if (arg2 <= 0) {
						return;
					}
					if (null != scanIpcWifiList && 0 != scanIpcWifiList.size()
							&& arg2 - 1 < scanIpcWifiList.size()) {
						setIpcName = scanIpcWifiList.get(arg2 - 1).SSID
								.replace("\"", "");
						showIpcLayout(false);

						String str1 = setIpcName.replace("\"", "");
						String str2 = str1.substring(6, str1.length());
						deviceNum = str2;
					}
				} else {// 手机wifi信息列表
					if (null != scanMobileWifiList
							&& 0 != scanMobileWifiList.size()) {
						desWifiName.setText(scanMobileWifiList.get(arg2).SSID);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		MyLog.e(TAG, "onHandler: " + what + ", " + arg1 + ", " + arg2 + ", "
				+ obj);
		switch (what) {
		case JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS:// 获取IPC 列表成功

			ipcWifiListView.completeRefreshing();
			dismissDialog();

			if (null != scanIpcWifiList && 0 != scanIpcWifiList.size()) {
				ipcAdapter.setData(scanIpcWifiList, oldWifiSSID);
				ipcWifiListView.setAdapter(ipcAdapter);
			}
			break;
		case JVConst.QUICK_SETTING_IPC_WIFI_FAILED:// 获取IPC 列表失败
			ipcWifiListView.completeRefreshing();
			dismissDialog();
			finish();
			break;
		case JVConst.QUICK_SETTING_MOBILE_WIFI_SUCC:// 快速设置获取主控WIFI信息数据
			mobileWifiListView.completeRefreshing();
			dismissDialog();

			if (null != scanIpcWifiList && 0 != scanMobileWifiList.size()) {
				mobileAdapter.setData(scanMobileWifiList, oldWifiSSID);
				mobileWifiListView.setAdapter(mobileAdapter);
			}
			break;
		case JVConst.IPC_WIFI_CON_FAILED:// ipcwifi连接失败，
			dismissDialog();
			netErrorDialog();
			break;
		case JVConst.MOBILE_WIFI_CON_FAILED:// 手机wifi连接失败，
			dismissDialog();
			netErrorDialog();
			break;
		case JVConst.AP_SET_ORDER:// AP配置流程提示
			createDialog(getString(R.string.str_quick_setting_alert1) + "("
					+ arg1 + "/9)");
			break;
		case JVConst.AP_SET_SUCCESS:// AP配置成功
			oldWifiSSID = desWifiSSID;
			dismissDialog();
			AlertDialog.Builder builder4 = new AlertDialog.Builder(
					JVQuickSettingActivity.this);

			builder4.setTitle(R.string.tips);
			builder4.setMessage(R.string.str_quick_setting_success);

			builder4.setNegativeButton(R.string.str_quick_setting_finish,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							// Message msg = BaseApp.quickSettingHandler
							// .obtainMessage();
							// msg.what = JVConst.QUICK_SETTING_WIFI_FINISH_SET;
							// BaseApp.quickSettingHandler
							// .sendMessage(msg);
							// if (null != activity
							// && !activity.isFinishing()
							// && null != dialog) {
							// dialog.dismiss();
							// }
						}
					});
			AlertDialog dialog4 = builder4.create();
			dialog4.setCancelable(false);
			dialog4.show();

			break;
		case JVConst.AP_SET_FAILED:// AP配置失败
			dismissDialog();
			AlertDialog.Builder builder5 = new AlertDialog.Builder(
					JVQuickSettingActivity.this);

			builder5.setTitle(R.string.tips);
			builder5.setMessage(R.string.str_quick_setting_failed);

			builder5.setPositiveButton(R.string.str_quick_setting_reset,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Message msg = BaseApp.quickSettingHandler
							// .obtainMessage();
							// msg.what =
							// JVConst.QUICK_SETTING_WIFI_CONTINUE_SET;
							// BaseApp.quickSettingHandler
							// .sendMessage(msg);
							// if (null != activity
							// && !activity.isFinishing()
							// && null != dialog) {
							// dialog.dismiss();
							// }
						}
					});
			builder5.setNegativeButton(R.string.str_quick_setting_finish,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							// Message msg = BaseApp.quickSettingHandler
							// .obtainMessage();
							// msg.what = JVConst.QUICK_SETTING_WIFI_FINISH_SET;
							// BaseApp.quickSettingHandler
							// .sendMessage(msg);
							// if (null != activity
							// && !activity.isFinishing()
							// && null != dialog) {
							// dialog.dismiss();
							// }
						}
					});
			AlertDialog dialog5 = builder5.create();
			dialog5.setCancelable(false);
			dialog5.show();
			break;

		case JVConst.QUICK_SETTING_CONNECT_FAILED:// 快速设置设备连接失败,失败一次再连接一次，直到第十次失败停止
			MyLog.v("快速设置设备连接失败", "第-----" + connectFailedCounts + "-----次");
			if (connectFailedCounts < 10) {
				connectFailedCounts++;
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				connectDevice();
			} else {
				connectFailedCounts = 0;// 连接失败次数复位
				dismissDialog();
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						JVQuickSettingActivity.this);

				builder1.setTitle(R.string.tips);
				builder1.setMessage(R.string.str_host_connect_failed);

				builder1.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder1.create().show();
			}

			break;

		case JVConst.DEVICE_MOST_COUNT:// 设备添加超过最大数
			showTextToast(R.string.str_device_most_count);
			break;
		case JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL:// 切换到原来的wifi失败,回到登陆界面
			showTextToast(R.string.str_wifi_reset_fail);
			dismissDialog();
			Intent intent = new Intent(JVQuickSettingActivity.this,
					JVLoginActivity.class);
			JVQuickSettingActivity.this.startActivity(intent);
			JVQuickSettingActivity.this.finish();
			break;
		case Consts.CALL_CONNECT_CHANGE: // 连接回调
			MyLog.e(TAG, "CONNECT_CHANGE: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);
			switch (arg1) {
			case JVNetConst.CONNECT_OK:// 1 -- 连接成功
				handler.sendMessage(handler.obtainMessage(JVConst.AP_SET_ORDER,
						6, 0));
				// 暂停视频
				Jni.sendBytes(Consts.CHANNEL_JY, JVNetConst.JVN_CMD_VIDEOPAUSE,
						new byte[0], 8);
				// 请求文本聊天
				Jni.sendBytes(Consts.CHANNEL_JY, JVNetConst.JVN_REQ_TEXT,
						new byte[0], 8);
				break;
			case JVNetConst.DISCONNECT_OK:// 2 -- 断开连接成功

				break;
			case JVNetConst.CONNECT_FAILED:// 4 -- 连接失败
				handler.sendMessage(handler.obtainMessage(
						JVConst.QUICK_SETTING_CONNECT_FAILED, 0, 0));
				break;
			case JVNetConst.ABNORMAL_DISCONNECT:// 6 -- 连接异常断开

				break;
			default:
				break;
			}

			break;
		case Consts.CALL_TEXT_DATA:// 文本回调
			MyLog.e(TAG, "TEXT_DATA: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);
			switch (arg1) {
			case JVNetConst.JVN_RSP_TEXTACCEPT:// 同意文本聊天
				handler.sendMessage(handler.obtainMessage(JVConst.AP_SET_ORDER,
						7, 0));
				// 获取基本文本信息
				Jni.sendTextData(Consts.CHANNEL_JY,
						JVNetConst.JVN_RSP_TEXTDATA, 8,
						JVNetConst.JVN_REMOTE_SETTING);
				break;
			case JVNetConst.JVN_CMD_TEXTSTOP:// 不同意文本聊天

				break;

			case JVNetConst.JVN_RSP_TEXTDATA:// 文本数据
				try {
					JSONArray dataArray = new JSONArray(obj.toString());
					int size = dataArray.length();
					JSONObject dataObj = null;
					if (size > 0) {//
						dataObj = dataArray.getJSONObject(0);

						switch (dataObj.getInt("flag")) {
						// 远程配置请求，获取到配置文本数据
						case JVNetConst.JVN_REMOTE_SETTING: {
							handler.sendMessage(handler.obtainMessage(
									JVConst.AP_SET_ORDER, 8, 0));
							saveWifi();
							// // 获取主控码流信息请求
							// Jni.sendTextData(1,
							// JVNetConst.JVN_RSP_TEXTDATA, 8,
							// JVNetConst.JVN_STREAM_INFO);

							break;
						}
						case JVNetConst.JVN_WIFI_INFO:// 2-- AP,WIFI热点请求
							// 获取主控码流信息请求
							Jni.sendTextData(Consts.CHANNEL_JY,
									JVNetConst.JVN_RSP_TEXTDATA, 8,
									JVNetConst.JVN_STREAM_INFO);
							break;
						case JVNetConst.JVN_STREAM_INFO:// 3-- 码流配置请求

							break;
						case JVNetConst.EX_WIFI_AP_CONFIG:// 11 ---新wifi配置流程
							handler.sendMessage(handler.obtainMessage(
									JVConst.AP_SET_ORDER, 9, 0));
							if (0 == dataObj.getInt("result")) {
								handler.sendMessage(handler.obtainMessage(
										JVConst.AP_SET_SUCCESS, 0, 0));
							} else {
								handler.sendMessage(handler.obtainMessage(
										JVConst.AP_SET_FAILED, 0, 0));
							}

							break;
						case JVNetConst.JVN_WIFI_SETTING_SUCCESS:// 4--
																	// wifi配置成功
							handler.sendMessage(handler.obtainMessage(
									JVConst.AP_SET_SUCCESS, 0, 0));
							break;
						case JVNetConst.JVN_WIFI_SETTING_FAILED:// 5--
																// WIFI配置失败
							handler.sendMessage(handler.obtainMessage(
									JVConst.AP_SET_FAILED, 0, 0));
							break;
						case JVNetConst.JVN_WIFI_IS_SETTING:// -- 6 正在配置wifi

							break;

						default:
							break;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			default:
				break;
			}
			break;

		}
	}

	/**
	 * 切换wifi需要时间
	 * 
	 * @author Administrator
	 * 
	 */
	private static class ChangeWifiThread extends Thread {
		private final WeakReference<JVQuickSettingActivity> mActivity;
		private boolean addFlag = false;

		public ChangeWifiThread(JVQuickSettingActivity activity, boolean flag) {
			mActivity = new WeakReference<JVQuickSettingActivity>(activity);
			addFlag = flag;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Looper.prepare();
			JVQuickSettingActivity activity = mActivity.get();
			try {
				if (null != activity && !activity.isFinishing()) {
					boolean flag = activity.wifiAdmin.changeWifi(
							activity.setIpcName, activity.oldWifiSSID,
							oldWifiState);
					int result = -1;
					int time = 0;
					MyLog.v("网络恢复完成", flag + "");
					if (!Boolean.valueOf(activity.statusHashMap
							.get(Consts.LOCAL_LOGIN)) && flag) {// 非本地登录而且网络恢复成功
						while (JVAccountConst.SUCCESS != result && time <= 5) {
							time++;
							try {
								Thread.sleep(3 * 1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							result = AccountUtil
									.userLogin(activity.statusHashMap
											.get(Consts.KEY_USERNAME),
											activity.statusHashMap
													.get(Consts.KEY_PASSWORD),
											activity);
							MyLog.v("网络恢复完成---重新登录---"
									+ activity.statusHashMap
											.get(Consts.KEY_USERNAME), result
									+ "");
						}
						if (JVAccountConst.SUCCESS == result && addFlag) {
							if (!Boolean.valueOf(activity.statusHashMap
									.get(Consts.LOCAL_LOGIN))) {
								// && BaseApp.deviceList.size() >= 100) {

								activity.handler
										.sendMessage(activity.handler
												.obtainMessage(
														JVConst.DEVICE_MOST_COUNT,
														0, 0));
								Thread.sleep(3000);
							} else {
								// activity.addDevice();
							}
						} else if (JVAccountConst.SUCCESS != result && addFlag) {
							activity.handler
									.sendMessage(activity.handler
											.obtainMessage(
													JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL,
													0, 0));
							return;
						}
					} else if (Boolean.valueOf(activity.statusHashMap
							.get(Consts.LOCAL_LOGIN)) && flag && addFlag) {// 添加设备标识位
						// activity.addDevice();
					}

					if (flag) {
						activity.handler
								.sendMessage(activity.handler
										.obtainMessage(
												JVConst.QUICK_SETTING_WIFI_CHANGED_SUCC,
												0, 0));
					} else {
						activity.handler
								.sendMessage(activity.handler
										.obtainMessage(
												JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL,
												0, 0));
					}

				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

	};

	/**
	 * 保存wifi配置信息
	 */
	public void saveWifi() {
		JSONObject newObj = new JSONObject();
		try {
			newObj.put("wifiSsid", desWifiSSID);
			newObj.put("wifiPwd", desWifiPWD);
			newObj.put("nPacketType", JVNetConst.RC_EXTEND);
			newObj.put("packetCount", JVNetConst.RC_EX_NETWORK);
			newObj.put("nType", JVNetConst.EX_WIFI_AP_CONFIG);
			int[] data = wifiAdmin.getWifiAuthEnc(desWifiSSID);
			newObj.put("wifiAuth", data[0]);
			newObj.put("wifiEncryp", data[1]);
			newObj.put("wifiIndex", 0);
			newObj.put("wifiChannel", 0);
			newObj.put("wifiRate", 0);// 0成功，其他失败
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Jni.setAccessPoint(Consts.CHANNEL_JY, JVNetConst.JVN_RSP_TEXTDATA,
				newObj.toString());
		MyLog.e("配置wifi请求", newObj.toString());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
