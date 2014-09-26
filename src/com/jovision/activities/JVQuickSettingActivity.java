package com.jovision.activities;

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

		builder.setPositiveButton(R.string.str_cancel,
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

		builder.setPositiveButton(R.string.str_sure,
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

				builder1.setPositiveButton(R.string.str_sure,
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
		case JVConst.CONNECT_CHANGE: // 连接回调
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
		case JVConst.TEXT_DATA:// 文本回调
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
