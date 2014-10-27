package com.jovision.activities;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.IpcWifiAdapter;
import com.jovision.adapters.MobileWifiAdapter;
import com.jovision.bean.Device;
import com.jovision.bean.WifiAdmin;
import com.jovision.commons.CommonInterface;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVConst;
import com.jovision.commons.JVNetConst;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.PlayUtil;
import com.jovision.views.RefreshableListView;
import com.jovision.views.RefreshableListView.OnRefreshListener;
import com.jovision.views.SearchDevicesView;

public class JVQuickSettingActivity1 extends BaseActivity implements
		CommonInterface {

	private final String TAG = "Qick_SetQick_Set";
	// private String ipcSSID;// 待设置的IPC网络名字 “IPC-H-S53530352”
	private static String ipcWifiName;// 待设置的IPC网络名字 IPC-H-S53530352
	// private String deviceNum;// 待设置的IPC云视通号 S53530352
	private String desWifiSSID;//
	private String desWifiPWD;//
	private Button back;
	private Button saveSet;
	private TextView currentMenu;
	ConnectivityManager connectivityManager = null;

	/** webview帮助 */
	private LinearLayout helpLayout;
	private ImageView helpIV;

	/** IPCwifi列表 */
	private LinearLayout ipcLayout;
	/** IPC网络 */
	private ArrayList<ScanResult> scanIpcWifiList = new ArrayList<ScanResult>();
	private RefreshableListView ipcWifiListView;
	private IpcWifiAdapter ipcAdapter;

	/** 手机wifi列表 */
	private LinearLayout mobileLayout;
	private LinearLayout mobileWifiBg;

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

	boolean hasLogout = false;// 是否已经注销

	private int errorCount = 0;
	SharedPreferences sharedPreferences = null;
	Editor editor = null;// 获取编辑器

	private Timer ipcTimer;
	private TimerTask ipcTimerTask;
	LayoutInflater layoutFlater = null;

	private ArrayList<Device> deviceList = new ArrayList<Device>();

	// private MediaPlayer mediaPlayer = null;
	private PopupWindow quickSetPop = null;// 快速设置弹出框
	private LinearLayout quickSetParentLayout = null;// 父窗体布局
	private ImageView quickSetDeviceImg = null;// 搜索动画上的小摄像头图标
	private Button quickSetBackImg = null;// 快速配置动画弹出框上的返回按钮
	private boolean isSearching = false;// 正在循环配置设备
	private WifiManager wifiManager = null;
	private SearchDevicesView searchView = null;

	@Override
	protected void initSettings() {
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.quicksetting_layout);

		deviceList = CacheUtil.getDevList();
		quickSetParentLayout = (LinearLayout) findViewById(R.id.quickSetParentLayout);
		layoutFlater = JVQuickSettingActivity1.this.getLayoutInflater();
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		wifiAdmin = new WifiAdmin(JVQuickSettingActivity1.this);

		Intent intent = getIntent();
		if (null != intent) {
			oldWifiSSID = intent.getStringExtra("OLD_WIFI");
			scanIpcWifiList = intent.getParcelableArrayListExtra("IPC_LIST");
			scanMobileWifiList = intent
					.getParcelableArrayListExtra("MOBILE_LIST");
			String comeFromMain = intent.getStringExtra("COMEFROMMAIN");
			if ("comeFromMain".equals(comeFromMain)) {// 语音提示请选择您要配置d设备，点击进入视频体验界面
				// 播放声音
				playSound(Consts.SOUNDONE);
			}
		}

		if (null != scanIpcWifiList && 0 != scanIpcWifiList.size()) {
			handler.sendMessage(handler
					.obtainMessage(JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS));
		} else {
			handler.sendMessage(handler
					.obtainMessage(JVConst.QUICK_SETTING_IPC_WIFI_FAILED));
		}

		ipcTimerTask = new TimerTask() {

			@Override
			public void run() {
				try {

					Log.v("IPC—wifi—自动刷新—", "10秒时间到");
					if (wifiAdmin.getWifiState()) {
						scanIpcWifiList = wifiAdmin.startScanIPC();
					}

					// 万一取失败了，接着再取一次
					if (null == scanIpcWifiList || 0 == scanIpcWifiList.size()) {
						scanIpcWifiList = wifiAdmin.startScanIPC();
					}

					handler.sendMessage(handler.obtainMessage(
							JVConst.QUICK_SETTING_IPC_WIFI_AUTO_SUCCESS, -1, 0,
							null));

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		};

		back = (Button) findViewById(R.id.btn_left);
		saveSet = (Button) findViewById(R.id.btn_right);
		back.setVisibility(View.VISIBLE);
		saveSet.setTextColor(Color.WHITE);
		saveSet.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.setting_save_1));
		saveSet.setText(getResources().getString(
				R.string.str_quick_setting_connect));
		saveSet.setVisibility(View.VISIBLE);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_quick_setting);
		back.setOnClickListener(onClickListener);
		saveSet.setOnClickListener(onClickListener);

		helpLayout = (LinearLayout) findViewById(R.id.helplayout);
		helpIV = (ImageView) findViewById(R.id.helpimg);
		if (ConfigUtil.getLanguage() == JVConst.LANGUAGE_ZH) {// 中文
			helpIV.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.h400_update));
		} else {
			helpIV.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.h400_update_en));
		}
		/** IPCwifi列表 */
		ipcLayout = (LinearLayout) findViewById(R.id.ipcwifilayout);
		ipcWifiListView = (RefreshableListView) findViewById(R.id.ipcwifilistview);
		LinearLayout layout = (LinearLayout) layoutFlater.inflate(
				R.layout.quiksettinglayoutheader, null);
		ipcWifiListView.addHeaderView(layout);

		ipcWifiListView.setOnRefreshListener(new WifiRefreshListener(true));
		ipcWifiListView.setOnItemClickListener(mOnItemClickListener);
		ipcAdapter = new IpcWifiAdapter(JVQuickSettingActivity1.this);
		if (null != scanIpcWifiList && 0 != scanIpcWifiList.size()) {
			ipcAdapter.setData(scanIpcWifiList, oldWifiSSID);
			ipcWifiListView.setAdapter(ipcAdapter);
		}

		/** 手机wifi列表 */
		mobileLayout = (LinearLayout) findViewById(R.id.mobilewifilayout);
		mobileWifiBg = (LinearLayout) findViewById(R.id.mobilewifibg);
		desWifiName = (EditText) findViewById(R.id.deswifiname);
		desWifiName.setText(statusHashMap.get(Consts.KEY_OLD_WIFI));
		desWifiName.setEnabled(false);

		desWifiPass = (EditText) findViewById(R.id.deswifipwd);
		destWifiEye = (ToggleButton) findViewById(R.id.deswifieye);
		desWifiPass
				.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);// 显示密码

		mobileWifiListView = (RefreshableListView) findViewById(R.id.mobilewifilistview);
		mobileWifiListView.setOnRefreshListener(new WifiRefreshListener(false));
		mobileWifiListView.setOnItemClickListener(mOnItemClickListener);
		// mobileWifiListView.setVisibility(View.GONE);
		mobileAdapter = new MobileWifiAdapter(JVQuickSettingActivity1.this);
		if (null != scanMobileWifiList && 0 != scanMobileWifiList.size()) {
			mobileAdapter.setData(scanMobileWifiList, oldWifiSSID);
			mobileWifiListView.setAdapter(mobileAdapter);
		}
		destWifiEye.setChecked(false);
		destWifiEye.setOnCheckedChangeListener(myOnCheckedChangeListener);

		showIpcLayout(true);

		initPopwindow();// 初始化快速配置弹出框所需要的内容
	}

	// onclick事件
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				backMethod();
				break;
			case R.id.btn_right:
				// 播放提示声音
				playSound(Consts.SOUNDFIVE);
				isSearching = true;
				desWifiSSID = desWifiName.getText().toString();
				if ("".equalsIgnoreCase(desWifiSSID)) {
					showTextToast(R.string.str_wifiname_not_null);
					break;
				}
				statusHashMap.put(Consts.KEY_OLD_WIFI, desWifiSSID);

				desWifiPWD = desWifiPass.getText().toString();
				new ApSetThread().start();
				// BaseApp.quickSettingHandler
				// .sendMessage(BaseApp.quickSettingHandler.obtainMessage(
				// JVConst.AP_SET_ORDER, 1, 0));

				// 开始声波配置，弹出配置对话框
				searchView.setSearching(true);
				showQuickPopWindow();
				break;
			}
		}
	};

	/**
	 * IPC设置线程
	 */

	class IpcWifiThread extends Thread {

		@Override
		public void run() {
			Looper.prepare();
			if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))
					&& !hasLogout) {// 非本地登录注销
				hasLogout = true;
				AccountUtil.userLogout();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (wifiAdmin.getWifiState()) {// 如果wifi为打开状态无需等待5秒
				statusHashMap.put(Consts.KEY_OLD_WIFI_STATE, "true");
			} else {
				statusHashMap.put(Consts.KEY_OLD_WIFI_STATE, "false");
				wifiAdmin.openWifi();// 打开wifi需要6秒左右

				boolean state = false;

				while (!state) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					state = wifiAdmin.getWifiState();
					Log.v("while判断网络状态：", state + "");
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			// 和待配置的网络一样直接连接视频
			if (null != oldWifiSSID
					&& ipcWifiName.equalsIgnoreCase(oldWifiSSID)) {// 当前连接的网路就是要配置ap网络
				handler.sendMessage(handler
						.obtainMessage(JVConst.QUICK_SETTING_AP_CON_SUCCESS));
			} else {
				// 切换到ipc 的ap 网络
				boolean ipcFlag = connectWifi(ipcWifiName, getResources()
						.getString(R.string.str_wifi_pwd));

				try {
					Thread.sleep(2500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				if (ipcFlag) {
					handler.sendMessage(handler
							.obtainMessage(JVConst.QUICK_SETTING_AP_CON_SUCCESS));
				} else {
					handler.sendMessage(handler
							.obtainMessage(JVConst.QUICK_SETTING_AP_CON_TIMEOUT));
				}

			}

			super.run();
		}

	}

	/**
	 * AP设置线程
	 */
	class ApSetThread extends Thread {

		@Override
		public void run() {
			Looper.prepare();

			// 从新连接一下 ap Wi-Fi ，确保ap 连接正常
			boolean ipcFlag = connectWifi(ipcWifiName, getResources()
					.getString(R.string.str_wifi_pwd));

			if (!ipcFlag) {
				handler.sendMessage(handler.obtainMessage(
						JVConst.IPC_WIFI_CON_FAILED, -1, 0, null));
				return;
			}
			// BaseApp.quickSettingHandler.sendMessage(BaseApp.quickSettingHandler
			// .obtainMessage(JVConst.AP_SET_ORDER, 3, 0));

			// 连接视频，为接下来ap 配置做准备
			connectDevice(getIPCDevice());

			super.run();
		}

	}

	/**
	 * 连接wifi 30s超时
	 * 
	 * @param wifi
	 * @param password
	 * @return
	 */
	public boolean connectWifi(String wifi, String password) {

		boolean flag = wifiAdmin.getWifiState(wifi);
		int result[] = wifiAdmin.getWifiAuthEnc(wifi);
		int errorCount = 0;

		while (!flag) {
			Log.v(TAG, "wifi=" + wifi + ";errorCount=" + errorCount + ";flag="
					+ flag);
			if (errorCount > 30) {
				break;
			}
			if (errorCount == 0 || errorCount == 6 || errorCount == 10
					|| errorCount == 14 || errorCount == 20 || errorCount == 24
					|| errorCount == 28) {
				WifiConfiguration desWifi = wifiAdmin.isExsits(wifi);
				if (null == desWifi) {
					int enc = 0;
					if ("".equalsIgnoreCase(password)) {
						enc = 1;
					} else {

						if (1 == result[1]) {// wep
							enc = 2;
						} else {
							enc = 3;
						}

					}
					desWifi = wifiAdmin.CreateWifiInfo(wifi, password, enc);
				}
				wifiAdmin.ConnectWifiByConfig(desWifi);
			}

			// 休息两秒钟保证连接Wi-Fi成功
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			errorCount += 2;
			Log.e("tag----------------", "errorCount: " + errorCount);
			flag = wifiAdmin.getWifiState(wifi);
		}

		return flag;
	}

	/**
	 * 两个wifi列表点击事件
	 */
	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			try {
				if (ipcLayout.getVisibility() == View.VISIBLE) {// IPC设备列表
					if (arg2 <= 0) {
						return;
					}
					if (null != scanIpcWifiList && 0 != scanIpcWifiList.size()
							&& arg2 - 1 < scanIpcWifiList.size()) {
						ipcWifiName = scanIpcWifiList.get(arg2 - 1).SSID;

						new IpcWifiThread().start();
						createDialog(R.string.str_quick_setting_alert_1);
						playSound(Consts.SOUNDTOW);
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

	// 返回方法
	public void backMethod() {

		if (View.VISIBLE == helpLayout.getVisibility()) {
			helpLayout.setVisibility(View.GONE);
			saveSet.setVisibility(View.VISIBLE);
			currentMenu.setText(R.string.str_quick_setting);
			Log.e("tags", "backMethod 1");
		} else {
			try {
				if (ipcLayout.getVisibility() == View.VISIBLE) {// 显示IPC网络信息列表（一级级wifi列表）
					Log.e("tags", "backMethod 2");
					// createDialog(R.string.str_quick_setting_alert_6);

					ChangeWifiThread cwt = new ChangeWifiThread(
							JVQuickSettingActivity1.this, false);
					cwt.start();
					// finish();
				} else {// 显示主控wifi信息列表（二级wifi列表）
					showIpcLayout(true);
					Log.e("tags", "backMethod 3");
					PlayUtil.disconnectDevice();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 单纯的切换网络线程
	 */
	class SimpleChangeWifi extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			boolean flag = changeWifi();
			if (flag) {

			}
		}

	}

	/**
	 * 密码显示隐藏
	 */
	OnCheckedChangeListener myOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
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
			createDialog(R.string.str_quick_setting_searching_wifi);
			RefreshWifiThread giwt = new RefreshWifiThread(
					JVQuickSettingActivity1.this, isIpc);
			giwt.start();
		}

	}

	/**
	 * 获取IPC待配置设备列表线程
	 * 
	 * @author Administrator
	 * 
	 */
	private class RefreshWifiThread extends Thread {
		private final WeakReference<JVQuickSettingActivity1> mActivity;
		private boolean isIpc = false;

		public RefreshWifiThread(JVQuickSettingActivity1 activity, boolean ipc) {
			mActivity = new WeakReference<JVQuickSettingActivity1>(activity);
			isIpc = ipc;
		}

		@Override
		public void run() {
			super.run();
			try {
				JVQuickSettingActivity1 activity = mActivity.get();
				if (null != activity) {
					if (isIpc) {// 刷新ipcwifi
						if (wifiAdmin.getWifiState()) {
							scanIpcWifiList = wifiAdmin.startScanIPC();
						}

						// 万一取失败了，接着再取一次
						if (null == scanIpcWifiList
								|| 0 == scanIpcWifiList.size()) {
							scanIpcWifiList = wifiAdmin.startScanIPC();
						}

						if (null != scanIpcWifiList
								&& 0 != scanIpcWifiList.size()) {
							handler.sendMessage(handler.obtainMessage(
									JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS, -1,
									0, null));
						} else {
							handler.sendMessage(handler.obtainMessage(
									JVConst.QUICK_SETTING_IPC_WIFI_FAILED, -1,
									0, null));
						}
					} else {// 刷新手机wifi
						if (wifiAdmin.getWifiState()) {
							scanMobileWifiList = wifiAdmin.startScanWifi();
						}
						// 万一取失败了，接着再取一次
						if (null == scanMobileWifiList
								|| 0 == scanMobileWifiList.size()) {
							scanMobileWifiList = wifiAdmin.startScanWifi();
						}

						if (null == scanMobileWifiList
								|| 0 == scanMobileWifiList.size()) {
							handler.sendMessage(handler.obtainMessage(
									JVConst.QUICK_SETTING_MOBILE_WIFI_FAILED,
									-1, 0, null));
						} else {
							handler.sendMessage(handler.obtainMessage(
									JVConst.QUICK_SETTING_MOBILE_WIFI_SUCC, -1,
									0, null));
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			desWifiPass.setText("");
		} else {
			ipcLayout.setVisibility(View.GONE);
			mobileLayout.setVisibility(View.VISIBLE);
			mobileWifiBg.setVisibility(View.VISIBLE);
			saveSet.setVisibility(View.VISIBLE);
		}
	}

	private class OnLineThread extends Thread {

		@Override
		public void run() {

			handler.sendMessage(handler.obtainMessage(
					JVConst.QUICK_SETTING_DEV_START_ONLINE, -1, 0, null));

			int timeCount = 0;
			int onlineState = DeviceUtil.getDevOnlineState(getIPCDevice()
					.getFullNo());

			while (0 == onlineState) {// 没上线

				if (timeCount > 40) {
					onlineState = DeviceUtil.getDevOnlineState(getIPCDevice()
							.getFullNo());
					break;
				} else {
					onlineState = DeviceUtil.getDevOnlineState(getIPCDevice()
							.getFullNo());
					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeCount += 5;
				}

			}

			if (0 == onlineState) { // 没上线
				setLineState(getIPCDevice().getFullNo(), 0);
				handler.sendMessage(handler
						.obtainMessage(JVConst.QUICK_SETTING_DEV_ONLINE_FAILED));
			} else {// 上线
				setLineState(getIPCDevice().getFullNo(), 1);
				handler.sendMessage(handler
						.obtainMessage(JVConst.QUICK_SETTING_DEV_ONLINE_SUCC));
			}
			super.run();
		}

	};

	public static void setLineState(String devNum, int online) {
		// int size = 0;
		// if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
		// size = BaseApp.deviceList.size();
		// if (0 != size) {
		// for (int i = 0; i < size; i++) {
		// if (devNum
		// .equalsIgnoreCase(BaseApp.deviceList.get(i).deviceNum)) {
		// BaseApp.deviceList.get(i).onlineState = online;
		// break;
		// }
		// }
		// }
		// }
	}

	// 判断网络连接状态，wifiadmin不准确
	public boolean getWifiState(String wifiName) {
		boolean flag = false;
		try {
			if (null == connectivityManager) {
				connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			}
			NetworkInfo net = connectivityManager.getActiveNetworkInfo();
			if (net == null) {
				Log.i("test", "无网络连接------------------");
				wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				wifiManager.setWifiEnabled(true);
			} else {
				Log.i("test", "网络连接类型为" + net.getTypeName());
				Log.i("test", "网络连接状态为" + net.getState().name());
				if ("CONNECTED".equalsIgnoreCase(net.getState().name())) {
					// 防止连上wifi，但不是连的IPCwifi
					Log.i("test", "wifiAdmin.getSSID()--" + wifiAdmin.getSSID());

					if (null != wifiAdmin && null != wifiAdmin.getSSID()
							&& !"".equalsIgnoreCase(wifiAdmin.getSSID().trim())) {
						String str1 = wifiAdmin.getSSID().replace("\"", "");
						String str2 = wifiName.replace("\"", "");

						if (str1.equalsIgnoreCase(str2)) {
							flag = true;
						} else {
							Log.e("原WIFI-SSID：", str1);
							WifiConfiguration oldWifi = wifiAdmin
									.isExsits(str1);
							if (null != oldWifi) {
								Log.e("关掉原不移除WIFI-SSID：", str1);
								wifiAdmin.disconnectWifi(oldWifi, false);// 关掉，不移除
							}
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {
		if (null != ipcTimer) {
			ipcTimer.cancel();
			ipcTimer = null;
		}
		if (null != ipcTimerTask) {
			ipcTimerTask.cancel();
			ipcTimerTask = null;
		}
	}

	public static class QuickSettingHandler extends Handler {
		private final WeakReference<JVQuickSettingActivity> mActivity;

		public QuickSettingHandler(JVQuickSettingActivity activity) {
			mActivity = new WeakReference<JVQuickSettingActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final JVQuickSettingActivity activity = mActivity.get();

			if (null != activity && !activity.isFinishing()) {
			}
		}

	}

	/**
	 * 设备升级Dialog
	 * 
	 * @param tag
	 */
	private void updateDialog() {
		PlayUtil.disconnectDevice();
		AlertDialog.Builder builder = new AlertDialog.Builder(
				JVQuickSettingActivity1.this);
		builder.setCancelable(false);

		builder.setTitle(getResources().getString(
				R.string.str_quick_setting_new_dev));
		builder.setMessage(getResources().getString(
				R.string.str_quick_setting_new_dev_tips));

		builder.setPositiveButton(R.string.str_check_now,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						helpLayout.setVisibility(View.VISIBLE);
						saveSet.setVisibility(View.GONE);
						currentMenu
								.setText(R.string.str_quick_setting_devupdate_order);
						// 暂停扫瞄器
						if (null != searchView) {
							searchView.setSearching(false);
							isSearching = false;
							searchView.stopPlayer();
						}
						if (null != mediaPlayer) {
							mediaPlayer.stop();
						}

						dismisQuickPopWindow();

					}

				});
		builder.setNegativeButton(R.string.login_str_close,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 暂停扫瞄器
						if (null != searchView) {
							searchView.setSearching(false);
							isSearching = false;
							searchView.stopPlayer();
						}
						if (null != mediaPlayer) {
							mediaPlayer.stop();
						}

						dismisQuickPopWindow();

						backMethod();
						dialog.dismiss();
					}

				});

		builder.show();
	}

	/**
	 * 切换wifi线程
	 * 
	 * @author Administrator
	 * 
	 */

	private class ChangeWifiThread extends Thread {
		private final WeakReference<JVQuickSettingActivity1> mActivity;
		private boolean addFlag = false;

		public ChangeWifiThread(JVQuickSettingActivity1 activity, boolean flag) {
			mActivity = new WeakReference<JVQuickSettingActivity1>(activity);
			addFlag = flag;
		}

		@Override
		public void run() {
			super.run();
			Looper.prepare();
			JVQuickSettingActivity1 activity = mActivity.get();
			try {
				if (null != activity && !activity.isFinishing()) {
					boolean flag = activity.changeWifi();
					int result = -1;
					int time = 0;
					Log.v("网络恢复完成", flag + "");
					if (flag) {// 网络恢复成功

						if (!Boolean.valueOf(statusHashMap
								.get(Consts.LOCAL_LOGIN))) {// 非本地登陆，重新登录
							if (activity.hasLogout) {// 已经注销过
								while (JVAccountConst.SUCCESS != result
										&& time <= 5) {
									time++;
									try {
										Thread.sleep(3 * 1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									result = AccountUtil.userLogin(
											statusHashMap
													.get(Consts.KEY_USERNAME),
											statusHashMap
													.get(Consts.KEY_PASSWORD),
											JVQuickSettingActivity1.this);
									Log.v("网络恢复完成---重新登录---"
											+ statusHashMap
													.get(Consts.KEY_USERNAME),
											result + "");
								}
							} else {
								result = JVAccountConst.SUCCESS;
							}

						}

						// if (addFlag) {
						// BaseApp.broadIP = false;
						// // 开启局域网广播，广播设备IP
						// BaseApp.IPCDEVICE.getGroupYST();
						// String group = BaseApp.IPCDEVICE.group;
						// int deviceYSTNO = BaseApp.IPCDEVICE.yst;
						// JVSUDT.JVC_QueryDevice(group, deviceYSTNO,
						// 40 * 1000);
						//
						// while (!BaseApp.broadIP) {// 未广播到IP
						// Thread.sleep(2 * 1000);
						// Log.v("未广播到IP---", "休眠2秒");
						// }
						// }

						if (!Boolean.valueOf(statusHashMap
								.get(Consts.LOCAL_LOGIN))) {// 在线
							if (JVAccountConst.SUCCESS == result && addFlag) {
								if (!Boolean.valueOf(statusHashMap
										.get(Consts.LOCAL_LOGIN))
										&& deviceList.size() >= 100) {
									handler.sendMessage(handler
											.obtainMessage(JVConst.DEVICE_MOST_COUNT));
									Thread.sleep(3000);
									return;
								} else {
									// APUtil.addDevice();
								}
							} else if (JVAccountConst.SUCCESS != result
									&& addFlag) {
								handler.sendMessage(handler
										.obtainMessage(JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL));
								return;
							}
						} else {// 本地登陆
							if (addFlag) {
								// APUtil.addDevice();
							}
						}

						if (addFlag) {
							handler.sendMessage(handler.obtainMessage(
									JVConst.QUICK_SETTING_WIFI_CHANGED_SUCC, 1));
						} else {
							handler.sendMessage(handler.obtainMessage(
									JVConst.QUICK_SETTING_WIFI_CHANGED_SUCC, 0));
						}

					} else {// 网络恢复失败
						handler.sendMessage(handler
								.obtainMessage(JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL));
					}

				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
	};

	/**
	 * 切换到原来网络
	 * 
	 * @return
	 */
	public boolean changeWifi() {

		boolean changeRes = false;
		try {
			// 断开跟连接的wifi 一样不做任何处理
			if (null != ipcWifiName
					&& !"".equalsIgnoreCase(ipcWifiName)
					&& null != statusHashMap.get(Consts.KEY_OLD_WIFI)
					&& !"".equalsIgnoreCase(statusHashMap
							.get(Consts.KEY_OLD_WIFI))
					&& ipcWifiName.equalsIgnoreCase(statusHashMap
							.get(Consts.KEY_OLD_WIFI))) {
				changeRes = true;
				return changeRes;
			} else {
				if (Boolean.getBoolean(statusHashMap
						.get(Consts.KEY_OLD_WIFI_STATE))) {// 原wifi开着的，恢复到原来的网络
					// 断开现在的wifi
					if (null != ipcWifiName
							&& !"".equalsIgnoreCase(ipcWifiName)) {
						WifiConfiguration currWifi = wifiAdmin
								.isExsits(ipcWifiName);
						if (null != currWifi) {
							Log.i("完成配置断开", ipcWifiName);
							wifiAdmin.disconnectWifi(currWifi, true);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

					// 完成配置连上原来的wifi
					if (null != statusHashMap.get(Consts.KEY_OLD_WIFI)) {
						changeRes = getWifiState(statusHashMap
								.get(Consts.KEY_OLD_WIFI));

						if (changeRes) {
							return changeRes;
						}

						WifiConfiguration oldWifi = wifiAdmin
								.isExsits(statusHashMap
										.get(Consts.KEY_OLD_WIFI));
						if (null != oldWifi) {
							Log.i("完成配置连接",
									statusHashMap.get(Consts.KEY_OLD_WIFI));
							boolean connRes = false;
							int count = 0;
							while (!connRes) {// 没连接调用连接方法
								if (count < 10) {
									count++;
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									connRes = wifiAdmin.connNetwork(oldWifi);
									Log.i("完成配置",
											statusHashMap
													.get(Consts.KEY_OLD_WIFI)
													+ "----"
													+ count
													+ "-----调用连接-----"
													+ connRes);
								} else {
									connRes = true;
									break;
								}
							}

							count = 0;
							if (connRes) {// 已连接
								while (!changeRes) {// 没连接调用连接方法
									if (count < 20) {
										count++;
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										changeRes = getWifiState(oldWifi.SSID);
										Log.i("完成配置",
												statusHashMap
														.get(Consts.KEY_OLD_WIFI)
														+ "----"
														+ count
														+ "-----连接结果-----"
														+ changeRes);
									} else {
										changeRes = false;
										break;
									}

								}
							} else {
								changeRes = false;
							}

						} else {
							// 若以前没有连接过此网络，重新连接
							boolean ipcFlag = connectWifi(
									statusHashMap.get(Consts.KEY_OLD_WIFI),
									getResources().getString(
											R.string.str_wifi_pwd));
							if (ipcFlag) {
								changeRes = true;
							} else {
								changeRes = false;
							}
						}
					} else {
						changeRes = true;
					}
				} else {// 原wifi关闭状态，关闭wifi
					Log.e("tags",
							"close wifi-------------------------------------");
					wifiAdmin.closeWifi();
					changeRes = true;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return changeRes;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (JVConst.AP_CONNECT_REQUEST == requestCode) {
			switch (resultCode) {
			case JVConst.AP_CONNECT_FINISHED:// AP检测视频完成
				showIpcLayout(false);// ipc列表切换Wi-Fi列表
				// 播放提示声音
				playSound(Consts.SOUNDFOUR);
				break;
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// backMethod();
			if (isSearching) {// 正在搜索设备是，提示是否退出
				Log.e("tags", "back isSearching");
				backDialog();
			} else if (View.VISIBLE == helpLayout.getVisibility()) {// 更新设备帮助文档
				helpLayout.setVisibility(View.GONE);
				saveSet.setVisibility(View.VISIBLE);
				currentMenu.setText(R.string.str_quick_setting);
				Log.e("tags", "back helpLayout.getVisibility()");
			} else {
				Log.e("tags", "back ");
				dismisQuickPopWindow();
				backMethod();
				// finish();

			}
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		if (null != quickSetPop) {
			quickSetPop.dismiss();
		}
		if (null != mediaPlayer && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		System.gc();
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 获取IPC设备
	 * 
	 * @return
	 */
	public static Device getIPCDevice() {
		String deviceName = ipcWifiName;
		String str0 = deviceName.replace("\"", "");
		String str = str0.substring(6, str0.length());

		Device dev = new Device(Consts.IPC_DEFAULT_IP, Consts.IPC_DEFAULT_PORT,
				"S", 52942216, Consts.IPC_DEFAULT_USER, Consts.IPC_DEFAULT_PWD,
				true, 1, 0);
		return dev;

		// BaseApp.IPCDEVICE = new Device();
		// BaseApp.IPCDEVICE.deviceNum = str;
		// BaseApp.IPCDEVICE.deviceName = str;
		// BaseApp.IPCDEVICE.getGroupYST();
		// BaseApp.IPCDEVICE.deviceLoginUser = BaseApp.IPC_DEFAULT_USER;
		// BaseApp.IPCDEVICE.deviceLoginPwd = BaseApp.IPC_DEFAULT_PWD;
		// BaseApp.IPCDEVICE.deviceLocalIp = BaseApp.IPC_DEFAULT_IP;
		// BaseApp.IPCDEVICE.deviceLocalPort = BaseApp.IPC_DEFAULT_PORT;
		// BaseApp.IPCDEVICE.devicePointCount = 1;
		//
		// ArrayList<ConnPoint> connPointList = new ArrayList<ConnPoint>();
		// if (BaseApp.IPCDEVICE.devicePointCount > 0) {
		// for (int i = 0; i < BaseApp.IPCDEVICE.devicePointCount; i++) {
		// ConnPoint connPoint = new ConnPoint();
		// connPoint.deviceID = BaseApp.IPCDEVICE.deviceOID;//
		// temObj.getInt("DeviceID");
		// connPoint.pointNum = i + 1;// temObj.getInt("PointNum");
		// connPoint.pointOwner = BaseApp.IPCDEVICE.deviceOID;//
		// temObj.getInt("Owner");
		// connPoint.pointName = BaseApp.IPCDEVICE.deviceNum + "_"
		// + (i + 1);// temObj.getString("Name");
		// connPoint.pointOID = BaseApp.IPCDEVICE.deviceOID;//
		// temObj.getInt("OID");
		// connPoint.isParent = false;
		// connPointList.add(connPoint);
		// }
		// }
		// BaseApp.IPCDEVICE.pointList = connPointList;
		//
		// return BaseApp.IPCDEVICE;
		// }
		//
		// // 添加设备
		// public void addDevice() {
		// // 判断一下是否已存在列表中
		// boolean find = false;
		// if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
		// for (int i = 0; i < BaseApp.deviceList.size(); i++) {
		// if (BaseApp.IPCDEVICE.deviceNum
		// .equalsIgnoreCase(BaseApp.deviceList.get(i).deviceNum)) {
		// find = true;
		// break;
		// }
		// }
		// }
		// if (!find) {
		// BaseApp.IPCDEVICE.getGroupYST();
		// String group = BaseApp.IPCDEVICE.group;
		// int deviceYSTNO = BaseApp.IPCDEVICE.yst;
		// if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
		// int resultount = JVSUDT.JVC_WANGetChannelCount(group,
		// deviceYSTNO, 5);
		// if (resultount > 0) {// 服务器返回通道数量
		// BaseApp.IPCDEVICE.devicePointCount = resultount;
		// } else {
		// BaseApp.IPCDEVICE.devicePointCount = 4;
		// }
		// } else {
		// if (BaseApp.isConnected(JVQuickSettingActivity1.this)) {
		// int count = JVSUDT.JVC_WANGetChannelCount(group,
		// deviceYSTNO, 5);
		// if (count > 0) {
		// BaseApp.IPCDEVICE.devicePointCount = count;
		// } else {
		// BaseApp.IPCDEVICE.devicePointCount = 4;
		// }
		// } else {
		// BaseApp.IPCDEVICE.devicePointCount = 4;
		// }
		// }
		// BaseApp.IPCDEVICE.onlineState = 1;
		// BaseApp.IPCDEVICE.deviceName = BaseApp.IPCDEVICE.deviceNum;
		// BaseApp.IPCDEVICE.deviceLoginUser = getResources().getString(
		// R.string.str_default_user);
		// BaseApp.IPCDEVICE.deviceLoginPwd = getResources().getString(
		// R.string.str_default_pass);
		// BaseApp.IPCDEVICE.devicePointCount = 1;
		// BaseApp.IPCDEVICE.deviceLocalIp = "";
		// BaseApp.IPCDEVICE.deviceLocalPort = 9101;
		// BaseApp.IPCDEVICE.hasWifi = 0;
		//
		// ArrayList<ConnPoint> connPointList = new ArrayList<ConnPoint>();
		// if (BaseApp.IPCDEVICE.devicePointCount > 0) {
		// for (int i = 0; i < BaseApp.IPCDEVICE.devicePointCount; i++) {
		// ConnPoint connPoint = new ConnPoint();
		// connPoint.deviceID = BaseApp.IPCDEVICE.deviceOID;//
		// temObj.getInt("DeviceID");
		// connPoint.pointNum = i + 1;// temObj.getInt("PointNum");
		// connPoint.pointOwner = BaseApp.IPCDEVICE.deviceOID;//
		// temObj.getInt("Owner");
		// connPoint.pointName = BaseApp.IPCDEVICE.deviceNum + "_"
		// + (i + 1);// temObj.getString("Name");
		// connPoint.pointOID = BaseApp.IPCDEVICE.deviceOID;//
		// temObj.getInt("OID");
		// connPoint.isParent = false;
		// connPointList.add(connPoint);
		// }
		// }
		// BaseApp.IPCDEVICE.pointList = connPointList;
		//
		// Device dd = BaseApp.addDeviceMethod(BaseApp.IPCDEVICE);
		// // if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))
		// // && !BaseApp.getSP(JVQuickSettingActivity1.this).getBoolean(
		// // "watchType", true)) {
		// // BaseApp.IPCDEVICE.onlineState = 1;
		// // }
		// if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
		// DeviceUtil.modifyDeviceWifi(BaseApp.IPCDEVICE.deviceNum, 1);
		// }
		// if (null != dd) {
		// BaseApp.deviceList.add(0, dd);
		// BaseApp.setHelpToDevice(dd);
		// if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
		// DeviceUtil.refreshDeviceState(LoginUtil.userName);
		// }
		// // if(Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))){
		// // JVConfigManager dbManager = BaseApp.getDbManager(this);//
		// // BaseApp.deviceList = getDataFromDB();// 从数据库中查询所有设备
		// // }else{
		// // BaseApp.deviceList.add(dd);
		// // }
		//
		// }
		// }
	}

	//
	// // 从数据库获取数据
	// public ArrayList<Device> getDataFromDB() {
	// ArrayList<Device> temList = new ArrayList<Device>();
	// ArrayList<ConnPoint> tempList = new ArrayList<ConnPoint>();
	// // 查询所有设备
	// List<JVConnectInfo> deList = BaseApp.queryAllDataList(true);
	// if (null != deList && 0 != deList.size()) {
	// int size = deList.size();
	// for (int i = 0; i < size; i++) {
	// // 数据库存储类型转换成网络获取数据类型
	// Device de = deList.get(i).toDevice();
	// if (null != de) {
	// temList.add(de);
	// }
	// }
	// }
	// // 查询所有通道
	// List<JVConnectInfo> poList = BaseApp.queryAllDataList(false);
	// if (null != poList && 0 != poList.size()) {
	// int size = poList.size();
	// for (int i = 0; i < size; i++) {
	// ConnPoint cp = poList.get(i).toConnPoint();
	// if (null != cp) {
	// tempList.add(cp);
	// }
	// }
	// }
	//
	// // 将通道关联到设备上
	// if (null != temList && 0 != temList.size() && null != tempList
	// && 0 != tempList.size()) {
	// for (int i = 0; i < temList.size(); i++) {
	// if (null == temList.get(i).pointList) {
	// temList.get(i).pointList = new ArrayList<ConnPoint>();
	// }
	// for (int j = 0; j < tempList.size(); j++) {
	// if (temList.get(i).yst == tempList.get(j).ystNum
	// && temList.get(i).group.equalsIgnoreCase(tempList
	// .get(j).group)) {
	// temList.get(i).pointList.add(tempList.get(j));
	// }
	// }
	//
	// }
	// }
	// temList = BaseApp.orderDevice(temList);
	// return temList;
	// }

	/**
	 * 网络连接失败Dialog
	 * 
	 * @param tag
	 */
	private void netErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				JVQuickSettingActivity1.this);
		builder.setCancelable(true);

		builder.setTitle(getResources().getString(
				R.string.str_quick_setting_neterror));
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
	 * 设备上线失败Dialog
	 * 
	 * @param tag
	 */
	private void onlineFailedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				JVQuickSettingActivity1.this);
		builder.setCancelable(false);

		builder.setTitle(R.string.tips);
		builder.setMessage(R.string.str_online_fail);

		builder.setPositiveButton(R.string.str_try_again,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						new OnLineThread().start();
					}

				});
		builder.setNegativeButton(R.string.str_crash_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						JVQuickSettingActivity1.this.finish();
					}

				});

		builder.create().show();
	}

	public boolean hasShowTips = false;// 是否显示过Ap配置流程

	/**
	 * 配置流程Dialog
	 * 
	 * @param tag
	 */
	// private void alertTipsDialog() {
	// hasShowTips = true;
	// AlertDialog.Builder builder = new AlertDialog.Builder(
	// JVQuickSettingActivity1.this);
	// builder.setCancelable(false);
	//
	// builder.setTitle(getResources().getString(R.string.str_device_ap_pro));
	// LayoutInflater li = JVQuickSettingActivity1.this.getLayoutInflater();
	// LinearLayout layout = (LinearLayout) li.inflate(R.layout.ap_set_tips,
	// null);
	// ToggleButton noAlert = (ToggleButton) layout.findViewById(R.id.noalert);
	// noAlert.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	//
	// @Override
	// public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
	// editor.putBoolean("AP_TIPS", arg1);
	// editor.commit();
	// }
	//
	// });
	//
	// builder.setView(layout);
	//
	// builder.setPositiveButton(R.string.str_cancel,
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// backMethod();
	// dialog.dismiss();
	// }
	//
	// });
	// builder.setNegativeButton(R.string.str_device_ap_start,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	//
	// });
	// Dialog dialog = builder.create();
	// dialog.show();
	// }

	/**
	 * 视频连接
	 * 
	 * @param device
	 */
	private void connectDevice(Device device) {
		Log.i("AP功能", "调用视频连接");
		PlayUtil.connectDevice(device);
	}

	/**
	 * 播放声音
	 */
	@Override
	public void playSound(int soundType) {
		// TODO Auto-generated method stub
		try {
			// 打开指定音乐文件
			String file = "";
			if (ConfigUtil.getLanguage() == JVConst.LANGUAGE_ZH) {
				switch (soundType) {
				case Consts.SOUNDONE:// 选择设备，并进入预览
					file = "1.mp3";
					break;
				case Consts.SOUNDTOW:// 正在连接设备
					file = "2.mp3";
					break;
				case Consts.SOUNDTHREE:// 请点击下一步，配置无线网络
					file = "3.mp3";
					break;
				case Consts.SOUNDFOUR:// 请选择无线路由，输入密码，并点击右上角声波配置按钮
					file = "4.mp3";
					break;
				case Consts.SOUNDFIVE:// 请将手机靠近设备
					file = "5.mp3";
					break;
				case Consts.SOUNDSIX:// 叮
					file = "6.mp3";
					break;
				case Consts.SOUNDSEVINE:// 配置完成，请点击图标查看设备
					file = "7.mp3";
					break;
				case Consts.SOUNDEIGHT:// 搜索声音
					file = "quicksetsound.mp3";
					break;
				default:
					break;
				}
			} else {
				switch (soundType) {
				case Consts.SOUNDONE:// 选择设备，并进入预览
					file = "1_en.mp3";
					break;
				case Consts.SOUNDTOW:// 正在连接设备
					file = "2_en.mp3";
					break;
				case Consts.SOUNDTHREE:// 请点击下一步，配置无线网络
					file = "3_en.mp3";
					break;
				case Consts.SOUNDFOUR:// 请选择无线路由，输入密码，并点击右上角声波配置按钮
					file = "4_en.mp3";
					break;
				case Consts.SOUNDFIVE:// 请将手机靠近设备
					file = "5_en.mp3";
					break;
				case Consts.SOUNDSIX:// 叮
					file = "6.mp3";
					break;
				case Consts.SOUNDSEVINE:// 配置完成，请点击图标查看设备
					file = "7_en.mp3";
					break;
				case Consts.SOUNDEIGHT:// 搜索声音
					file = "quicksetsound.mp3";
					break;
				default:
					break;
				}
			}

			int maxVolume = 100; // 最大音量值
			int curVolume = 20; // 当前音量值

			AudioManager audioMgr = null; // Audio管理器，用了控制音量
			audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			// 获取最大音乐音量
			maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			// 初始化音量大概为最大音量的1/2
			// curVolume = maxVolume / 2;
			// 每次调整的音量大概为最大音量的1/6
			audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume,
					AudioManager.FLAG_PLAY_SOUND);
			AssetManager assetMgr = this.getAssets();
			; // 资源管理器
			AssetFileDescriptor afd = assetMgr.openFd(file);
			if (null == mediaPlayer) {
				mediaPlayer = new MediaPlayer();
			}
			mediaPlayer.reset();

			// 使用MediaPlayer加载指定的声音文件。
			mediaPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			// 准备声音
			mediaPlayer.prepare();
			// 播放
			mediaPlayer.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 弹出配置搜索对话框
	 */
	private void initPopwindow() {
		try {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View view = layoutInflater
					.inflate(R.layout.quicksetsearching, null);
			Display display = getWindowManager().getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			quickSetPop = new PopupWindow(view, screenWidth, screenHeight);

			/**
			 * 为控件添加事件
			 * 
			 */
			quickSetBackImg = (Button) view.findViewById(R.id.quickSetBack);
			quickSetBackImg.setOnClickListener(quickSetOnClick);

			quickSetDeviceImg = (ImageView) view
					.findViewById(R.id.quickSetDeviceImg);
			quickSetDeviceImg.setOnClickListener(quickSetOnClick);
			searchView = (SearchDevicesView) view.findViewById(R.id.searchView);
			searchView.setWillNotDraw(false);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 显示
	 */
	private void showQuickPopWindow() {
		if (null != quickSetPop) {
			quickSetPop.showAtLocation(quickSetParentLayout, Gravity.CENTER, 0,
					0);
		}
	}

	/**
	 * 关闭对话框
	 */
	private void dismisQuickPopWindow() {
		if (null != quickSetPop && quickSetPop.isShowing()) {
			quickSetPop.dismiss();
		}
	}

	/**
	 * 配置设备弹出框单机事件
	 */
	OnClickListener quickSetOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.quickSetBack:// 返回

				break;
			case R.id.quickSetDeviceImg:
				finish();
				break;
			}
		}
	};

	public void backDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				JVQuickSettingActivity1.this);
		builder.setCancelable(false);

		// builder.setTitle(getResources().getString(
		// R.string));
		builder.setMessage(getResources().getString(R.string.sure_exit));

		builder.setPositiveButton(R.string.str_quick_setting_waiting,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}

				});
		builder.setNegativeButton(R.string.exit,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// dismisQuickPopWindow();

						// 暂停扫瞄器
						if (null != searchView) {
							searchView.setSearching(false);
							isSearching = false;
							searchView.stopPlayer();
						}
						if (null != mediaPlayer) {
							mediaPlayer.stop();
						}

						dismisQuickPopWindow();

						backMethod();
						// dialog.dismiss();

						// backMethod();
						// dialog.dismiss();
					}

				});

		builder.show();
	}

	private boolean isPressHomeKey = false;// 监听home键的专有标志位：false：点击home键；true为其它操作
	private MediaPlayer mediaPlayer;

	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
		if (!isPressHomeKey) {
			if (null != mediaPlayer) {
				mediaPlayer.stop();
				searchView.setSearching(false);
			}
		}
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

		switch (what) {
		case JVConst.QUICK_SETTING_IPC_WIFI_SUCCESS:// Ipcwifi列表获取成功
			// if (!activity.hasShowTips
			// && !activity.sharedPreferences.getBoolean(
			// "AP_TIPS", false)) {
			// activity.alertTipsDialog();
			// }
			ipcWifiListView.completeRefreshing();
			dismissDialog();

			if (null != scanIpcWifiList && 0 != scanIpcWifiList.size()) {
				ipcAdapter.setData(scanIpcWifiList, "");
				ipcWifiListView.setAdapter(ipcAdapter);
			}

			if (null == ipcTimer) {
				ipcTimer = new Timer();
				if (null != ipcTimerTask) {
					ipcTimer.schedule(ipcTimerTask, 10 * 1000, 10 * 1000);
				}
			}

			break;
		case JVConst.QUICK_SETTING_IPC_WIFI_AUTO_SUCCESS:// Ipcwifi列表获取成功
			ipcAdapter.setData(scanIpcWifiList, "");
			ipcWifiListView.setAdapter(ipcAdapter);
			break;
		case JVConst.QUICK_SETTING_IPC_WIFI_FAILED:// IPCwifi列表获取失败
			ipcWifiListView.completeRefreshing();
			dismissDialog();
			// activity.finish();
			break;
		case JVConst.QUICK_SETTING_MOBILE_WIFI_SUCC:// 快速设置获取主控WIFI信息数据
			mobileWifiListView.completeRefreshing();
			dismissDialog();

			if (null != scanIpcWifiList && 0 != scanMobileWifiList.size()) {
				mobileAdapter.setData(scanMobileWifiList, oldWifiSSID);
				mobileWifiListView.setAdapter(mobileAdapter);
			}
			break;
		case JVConst.QUICK_SETTING_MOBILE_WIFI_FAILED:// IPCwifi列表获取失败
			mobileWifiListView.completeRefreshing();
			dismissDialog();
			break;
		case JVConst.IPC_WIFI_CON_FAILED:// ipcwifi连接失败，
			dismissDialog();
			netErrorDialog();
			break;
		case JVConst.MOBILE_WIFI_CON_FAILED:// 手机wifi连接失败，
			dismissDialog();
			netErrorDialog();
			break;
		// case JVConst.AP_SET_ORDER:// AP配置流程提示
		// String message = "";
		// if (1 == arg1) {
		// message = getString(R.string.str_quick_setting_now_1);
		// } else if (2 == arg1) {
		// message = getString(R.string.str_quick_setting_now_2);
		// } else if (3 == arg1) {
		// message = getString(R.string.str_quick_setting_now_3);
		// } else if (4 == arg1) {
		// message = getString(R.string.str_quick_setting_now_4);
		// } else if (5 == arg1) {
		// message = getString(R.string.str_quick_setting_now_5);
		// }
		//
		// // createDialog(message);// + "(" + msg.arg1 +
		// // "/5)");
		// break;
		case JVConst.QUICK_SETTING_AP_CON_SUCCESS:// ipc的路由连接成功
			dismissDialog();
			getIPCDevice();
			Intent apIntent = new Intent(this, JVPlayActivity.class);
			apIntent.putExtra("PlayTag", Consts.PLAY_AP);
			startActivityForResult(apIntent, JVConst.AP_CONNECT_REQUEST);
			break;
		case JVConst.QUICK_SETTING_AP_CON_TIMEOUT:// ipc的路由连接超时
			dismissDialog();
			showTextToast(R.string.str_quick_setting_ap_net_timeout);
			break;

		// case JVConst.QUICK_SETTING_CONNECT_FAILED://
		// 快速设置设备连接失败,失败一次再连接一次，直到第十次失败停止
		// Log.v("快速设置设备连接失败", "第-----" + connectFailedCounts + "-----次");
		// if (connectFailedCounts < 10) {
		// connectFailedCounts++;
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// connectDevice(getIPCDevice());
		// } else {
		// connectFailedCounts = 0;// 连接失败次数复位
		// isSearching = false;
		// dismissDialog();
		// AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		//
		// builder1.setTitle(R.string.tips);
		// builder1.setMessage(R.string.str_host_connect_failed);
		//
		// builder1.setPositiveButton(R.string.sure,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// dialog.dismiss();
		// }
		// });
		// builder1.create().show();
		// }
		//
		// break;

		case JVConst.QUICK_SETTING_IS_IPC:// 是IPC
			// handler.sendMessage(handler.obtainMessage(JVConst.AP_SET_ORDER,
			// 4,
			// 0, null));
			Log.e("连接成功发暂停命令：", "JVN_CMD_VIDEOPAUSE");
			Jni.sendBytes(1, (byte) JVNetConst.JVN_CMD_VIDEOPAUSE, new byte[0],
					0);

			Log.e("是IPC-----------", "是IPC，发聊天请求");

			Jni.sendBytes(1, (byte) JVNetConst.JVN_REQ_TEXT, new byte[0], 8);// new
																				// byte[0]
			break;
		case JVConst.QUICK_SETTING_HOST_AGREE_TEXT:// 快速配置主控同意文本聊天
			Log.e("主控同意文本聊天-----------", "主控同意文本聊天");

			// 获取主控文本聊天信息请求
			Jni.sendTextData(1, (byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
					JVNetConst.JVN_REMOTE_SETTING);

			break;
		case JVConst.QUICK_SETTING_HOST_NOT_AGREE_TEXT:// 快速设置主控不同意聊天
			dismissDialog();
			// 断开连接
			PlayUtil.disconnectDevice();
			showTextToast(R.string.str_only_administator_use_this_function);
			break;
		case JVConst.QUICK_SETTING_HOST_TEXT:// 快速配置文本聊天数据过来
			Log.v("文本聊天数据过来-----------", "文本聊天数据过来");
			// if (null != settingMap
			// && null !=settingMap.get("DEV_VERSION")) {
			// String dvStr = settingMap.get("DEV_VERSION");
			// int dvInt = Integer.parseInt(dvStr);
			// if (dvInt == 1) {// 当前最新版本
			// } else {// 旧版
			// dismissDialog();
			// updateDialog();
			// break;
			// }
			// } else {
			// dismissDialog();
			// updateDialog();
			// break;
			// }
			proDialog.setMessage(getResources().getString(
					R.string.str_quick_setting_alert_5));
			JSONObject obj1 = new JSONObject();
			try {
				obj1.put("wifiSsid", desWifiSSID);
				obj1.put("wifiPwd", desWifiPWD);
				obj1.put("nPacketType", JVNetConst.RC_EXTEND);
				obj1.put("packetCount", JVNetConst.RC_EX_NETWORK);
				obj1.put("nType", JVNetConst.EX_WIFI_AP_CONFIG);
				int[] data = wifiAdmin.getWifiAuthEnc(desWifiSSID);
				obj1.put("wifiAuth", data[0]);
				obj1.put("wifiEncryp", data[1]);
				obj1.put("wifiIndex", 0);
				obj1.put("wifiChannel", 0);
				obj1.put("wifiRate", 0);// 0成功，其他失败
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			Log.v("配置wifi请求", obj1.toString());
			// handler.sendMessage(handler.obtainMessage(JVConst.AP_SET_ORDER,
			// 5, 0,null));

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Jni.setAccessPoint(1, (byte) JVNetConst.JVN_RSP_TEXTDATA,
					obj1.toString());

			break;
		case JVConst.QUICK_SETTING_IS_NOT_IPC:// 快速设置不是ipc
			dismissDialog();

			// 断开连接
			PlayUtil.disconnectDevice();
			AlertDialog.Builder builder2 = new AlertDialog.Builder(
					JVQuickSettingActivity1.this);

			builder2.setTitle(R.string.tips);
			builder2.setMessage(R.string.str_not_support_this_device);
			builder2.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder2.create().show();

			break;
		case JVConst.QUICK_SETTING_HOST_SET_WIFI_SUCC:// 主控wifi配置成功
			dismissDialog();
			// 断开连接
			PlayUtil.disconnectDevice();
			handler.sendMessage(handler
					.obtainMessage(JVConst.QUICK_SETTING_WIFI_FINISH_SET));
			break;
		case JVConst.QUICK_SETTING_HOST_SET_WIFI_FAIL:// 主控配置wifi失败
			dismissDialog();
			// 断开连接
			PlayUtil.disconnectDevice();
			AlertDialog.Builder builder5 = new AlertDialog.Builder(
					JVQuickSettingActivity1.this);

			builder5.setTitle(R.string.tips);
			builder5.setMessage(R.string.str_quick_setting_failed);

			builder5.setPositiveButton(R.string.str_quick_setting_reset,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							handler.sendMessage(handler
									.obtainMessage(JVConst.QUICK_SETTING_WIFI_CONTINUE_SET));
							dialog.dismiss();
						}
					});
			builder5.setNegativeButton(R.string.str_quick_setting_finish,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							handler.sendMessage(handler
									.obtainMessage(JVConst.QUICK_SETTING_WIFI_FINISH_SET));
							dialog.dismiss();
						}
					});
			AlertDialog dialog5 = builder5.create();
			dialog5.setCancelable(false);
			dialog5.show();
			break;
		case JVConst.QUICK_SETTING_WIFI_FINISH_SET:// 完成配置wifi
			ChangeWifiThread cwt = new ChangeWifiThread(
					JVQuickSettingActivity1.this, true);
			cwt.start();
			break;

		case JVConst.QUICK_SETTING_WIFI_CHANGED_SUCC:// 网络恢复成功

			if (Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {// 本地模式下添加设备
				Log.e("999999",
						"ooooooooooooooooooooooooooooooooooooooooooooo 1");
				if (1 == arg1) {
					playSound(Consts.SOUNDSIX);// 播放“叮”的一声
					searchView.setSearching(false);
					quickSetDeviceImg.setVisibility(View.VISIBLE);// 弹出设备

					// BaseApp.quickSettingHandler.postDelayed(new Thread() {//
					// 播放，配置完成声音
					//
					// @Override
					// public void run() {
					// // TODO Auto-generated method stub
					// super.run();
					// playSound(Consts.SOUNDSEVINE);
					// }
					//
					// }, 200);
					playSound(Consts.SOUNDSEVINE);

					isSearching = false;
				} else {
					dismissDialog();
					finish();
				}

			} else {
				if (1 == arg1) {// 在线模式下，上线流程
					new OnLineThread().start();
					Log.e("999999",
							"ooooooooooooooooooooooooooooooooooooooooooooo 2");
				} else {
					Log.e("999999",
							"ooooooooooooooooooooooooooooooooooooooooooooo 3");
					finish();
				}
			}

			break;
		case JVConst.QUICK_SETTING_DEV_START_ONLINE:// 设备正在努力上线
			break;
		case JVConst.QUICK_SETTING_DEV_ONLINE_FAILED:// 上线失败
			dismissDialog();
			onlineFailedDialog();
			Log.e("999999", "ooooooooooooooooooooooooooooooooooooooooooooo 4");
			break;
		case JVConst.QUICK_SETTING_DEV_ONLINE_SUCC:// 上线成功
			playSound(Consts.SOUNDSIX);
			searchView.setSearching(false);
			quickSetDeviceImg.setVisibility(View.VISIBLE);
			// BaseApp.quickSettingHandler.postDelayed(new Thread() {//
			// 播放，配置完成声音
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// super.run();
			// playSound(Consts.SOUNDSEVINE);
			// }
			//
			// }, 200);

			playSound(Consts.SOUNDSEVINE);
			Log.e("999999", "ooooooooooooooooooooooooooooooooooooooooooooo 5");
			playSound(Consts.SOUNDSEVINE);
			isSearching = false;
			break;
		case JVConst.QUICK_SETTING_WIFI_CHANGED_FAIL:
			// dismissDialog();
			showTextToast(statusHashMap.get(Consts.KEY_OLD_WIFI)
					+ getResources().getString(R.string.str_wifi_reset_fail));
			searchView.setSearching(false);
			dismisQuickPopWindow();
			finish();
			isSearching = false;
			searchView.stopPlayer();
			Log.e("999999", "ooooooooooooooooooooooooooooooooooooooooooooo 6");
			break;

		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

}