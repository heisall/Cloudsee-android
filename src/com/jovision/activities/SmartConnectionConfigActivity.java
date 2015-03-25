package com.jovision.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.WaveDevlListAdapter;
import com.jovision.bean.Device;
import com.jovision.bean.WifiAdmin;
import com.jovision.commons.MyAudio;
import com.jovision.commons.MyLog;
import com.jovision.utils.BitmapCache;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.PlayUtil;
import com.jovision.views.ProgressWheel;

public class SmartConnectionConfigActivity extends BaseActivity {

	private static final String TAG = "SmartConnectionConfigActivity";

	// String[] stepSoundCH = { "voi_info.mp3", "voi_next.mp3", "voi_send.mp3",
	// "quicksetsound.mp3", "6.mp3" };

	// String[] stepSoundCHTW = { "voi_info_zhtw.mp3", "voi_next_zhtw.mp3",
	// "voi_send_zhtw.mp3", "quicksetsound.mp3", "6.mp3" };

	// String[] stepSoundEN = { "voi_info_en.mp3", "voi_next_en.mp3",
	// "voi_send_en.mp3", "quicksetsound.mp3", "6.mp3" };
	int[] titleID = { R.string.prepare_step, R.string.prepare_set,
			R.string.wave_set, R.string.show_demo, R.string.search_list };

	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private ArrayList<Device> broadList = new ArrayList<Device>();

	protected WifiAdmin wifiAdmin;
	protected String oldWifiSSID = "";

	/** topBar */
	protected LinearLayout topBar;
	protected RelativeLayout stepLayout1;
	protected RelativeLayout stepLayout2;
	protected RelativeLayout stepLayout3;
	// protected RelativeLayout stepLayout4;
	// protected RelativeLayout stepLayout5;
	protected RelativeLayout stepLayout6;
	protected RelativeLayout.LayoutParams reParamstop2;

	private ProgressWheel pw_two;
	int progress = 0;
	private boolean isshow = false;

	protected ImageView stepImage1;
	// protected ImageView waveImage;// 声波动画按钮
	// protected ImageView pressToSendWave;// 点击发送声波按钮
	// protected ImageView instruction;//
	protected EditText desWifiName;
	protected EditText desWifiPwd;
	protected ToggleButton desPwdEye;
	protected ListView devListView;// 广播到的设备列表
	// protected ProgressBar loading;
	protected WaveDevlListAdapter wdListAdapter;// 设备列表adaper

	ArrayList<RelativeLayout> layoutList = new ArrayList<RelativeLayout>();

	protected Button nextBtn1;
	protected Button nextBtn2;
	// protected Button showDemoBtn;// 观看操作演示

	protected int currentStep = 0;

	// 播放操作步骤音频
	protected AssetManager assetMgr = null;
	protected MediaPlayer mediaPlayer = new MediaPlayer();

	// 声波
	protected int animTime = 1000;
	// protected int sendCounts = 0;
	protected String params = "";
	protected MyAudio playAudio;
	protected static int audioSampleRate = 48000;
	protected static int playBytes = 16;

	private byte AuthModeOpen = 0x00;
	private byte AuthModeShared = 0x01;
	private byte AuthModeAutoSwitch = 0x02;
	private byte AuthModeWPA = 0x03;
	private byte AuthModeWPAPSK = 0x04;
	private byte AuthModeWPANone = 0x05;
	private byte AuthModeWPA2 = 0x06;
	private byte AuthModeWPA2PSK = 0x07;
	private byte AuthModeWPA1WPA2 = 0x08;
	private byte AuthModeWPA1PSKWPA2PSK = 0x09;
	private byte mAuthMode = 0;;

	@SuppressWarnings("deprecation")
	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_WHEEL_DISMISS:
			pw_two.setVisibility(View.GONE);
			stepLayout6.setVisibility(View.GONE);
			isshow = false;
			break;
		case Consts.WHAT_BROAD_FINISHED: {// 广播超时
			if (2 != currentStep) {
				break;
			}
			dismissDialog();
			if (null == broadList || 0 == broadList.size()) {
				showTextToast(R.string.broad_zero);
			} else {
				wdListAdapter = new WaveDevlListAdapter(
						SmartConnectionConfigActivity.this);
				wdListAdapter.setData(broadList);
				devListView.setAdapter(wdListAdapter);
			}
			playSoundStep(4);
			rightBtn.setVisibility(View.VISIBLE);
			break;
		}
		case Consts.WHAT_BROAD_DEVICE: {// 广播到一个设备
			if (null != broadList) {
				wdListAdapter = new WaveDevlListAdapter(
						SmartConnectionConfigActivity.this);
				wdListAdapter.setData(broadList);
				devListView.setAdapter(wdListAdapter);
			}
			break;
		}
		case Consts.WHAT_ADD_DEVICE: {// 添加设备
			alertAddDialog(arg1);
			break;
		}

		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		// 广播回调
		case Consts.CALL_QUERY_DEVICE: {// nNetMod 设备是否带wifi nCurMod
										// 设备是否正在使用wifi

			MyLog.v(TAG, "CALL_LAN_SEARCH = what=" + what + ";arg1=" + arg1
					+ ";arg2=" + arg1 + ";obj=" + obj.toString());

			// * @param nNetMod
			// * 设备是否带wifi 有无wifi
			// * @param nCurMod
			// * 设备是否正在使用wifi
			// CALL_LAN_SEARCH =
			// what=168;arg1=0;arg2=0;obj={"count":1,"curmod":0,"gid":"S","ip":"192.168.7.139","netmod":1,"no":224356522,"port":9101,"timeout":0,"type":59162,"variety":3}

			JSONObject broadObj;
			try {
				broadObj = new JSONObject(obj.toString());
				if (0 == broadObj.optInt("timeout")) {
					String gid = broadObj.optString("gid");
					int no = broadObj.optInt("no");
					String ip = broadObj.optString("ip");
					int port = broadObj.optInt("port");
					int channelCount = broadObj.optInt("count");
					int count = channelCount > 0 ? channelCount : 1;
					String broadDevNum = gid + no;
					int netmod = broadObj.optInt("netmod");
					// 防止广播到设备没ip
					if (!"".equalsIgnoreCase(ip) && 0 != port) {
						Boolean hasAdded = PlayUtil.hasDev(deviceList,
								broadDevNum, ip, port, netmod);
						if (1 == broadObj.optInt("netmod")) {// && !hasAdded)
																// {//
																// 带wifi设备且不在设备列表里面
							Device addDev = new Device(ip, port, gid, no,
									Consts.DEFAULT_USERNAME,
									Consts.DEFAULT_PASSWORD, false, count, 0,
									null);
							addDev.setHasAdded(hasAdded);
							if (!PlayUtil.addDev(broadList, addDev)) {
								broadList.add(addDev);
								handler.sendMessage(handler
										.obtainMessage(Consts.WHAT_BROAD_DEVICE));
							}
						}

					}

				} else if (1 == broadObj.optInt("timeout")) {
					CacheUtil.saveDevList(deviceList);
					handler.sendMessage(handler
							.obtainMessage(Consts.WHAT_BROAD_FINISHED));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		default: {
			handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
			break;
		}
		}

	}

	@Override
	protected void initSettings() {
		deviceList = CacheUtil.getDevList();
		assetMgr = this.getAssets();
		playAudio = MyAudio.getIntance(Consts.WHAT_PLAY_AUDIO_WHAT,
				SmartConnectionConfigActivity.this, audioSampleRate);
	}

	private void setCurrentWifi() {
		if (null == wifiAdmin) {
			wifiAdmin = new WifiAdmin(SmartConnectionConfigActivity.this);
		}
		// wifi打开的前提下,获取oldwifiSSID
		if (wifiAdmin.getWifiState()) {
			if (null != wifiAdmin.getSSID()) {
				oldWifiSSID = wifiAdmin.getSSID().replace("\"", "");
			}
		}
		desWifiName.setText(oldWifiSSID);
		// TODO
		WifiManager mWifiManager;
		String mConnectedSsid;
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (mWifiManager.isWifiEnabled()) {
			WifiInfo WifiInfo = mWifiManager.getConnectionInfo();
			mConnectedSsid = WifiInfo.getSSID();
			int iLen = mConnectedSsid.length();

			if (iLen == 0) {
				return;
			}

			if (mConnectedSsid.startsWith("\"")
					&& mConnectedSsid.endsWith("\"")) {
				mConnectedSsid = mConnectedSsid.substring(1, iLen - 1);
			}
			List<ScanResult> ScanResultlist = mWifiManager.getScanResults();
			for (int i = 0, len = ScanResultlist.size(); i < len; i++) {
				ScanResult AccessPoint = ScanResultlist.get(i);

				if (AccessPoint.SSID.equals(mConnectedSsid)) {
					boolean WpaPsk = AccessPoint.capabilities
							.contains("WPA-PSK");
					boolean Wpa2Psk = AccessPoint.capabilities
							.contains("WPA2-PSK");
					boolean Wpa = AccessPoint.capabilities.contains("WPA-EAP");
					boolean Wpa2 = AccessPoint.capabilities
							.contains("WPA2-EAP");

					if (AccessPoint.capabilities.contains("WEP")) {
						mAuthMode = AuthModeOpen;
						break;
					}

					if (WpaPsk && Wpa2Psk) {
						mAuthMode = AuthModeWPA1PSKWPA2PSK;
						break;
					} else if (Wpa2Psk) {
						mAuthMode = AuthModeWPA2PSK;
						break;
					} else if (WpaPsk) {
						mAuthMode = AuthModeWPAPSK;
						break;
					}

					if (Wpa && Wpa2) {
						mAuthMode = AuthModeWPA1WPA2;
						break;
					} else if (Wpa2) {
						mAuthMode = AuthModeWPA2;
						break;
					} else if (Wpa) {
						mAuthMode = AuthModeWPA;
						break;
					}

					mAuthMode = AuthModeOpen;

				}
			}
		}
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.smart_connection_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		topBar = (LinearLayout) findViewById(R.id.top_bar);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		rightBtn = (Button) findViewById(R.id.btn_right);
		currentMenu.setText(R.string.prepare_step);
		rightBtn.setText(getResources().getString(R.string.try_again));
		rightBtn.setTextColor(getResources().getColor(R.color.white));
		rightBtn.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.feedback_bg));
		rightBtn.setVisibility(View.GONE);
		reParamstop2 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		reParamstop2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		reParamstop2.addRule(RelativeLayout.CENTER_VERTICAL);
		reParamstop2.setMargins(0, 0, 30, 0);
		rightBtn.setLayoutParams(reParamstop2);

		stepLayout1 = (RelativeLayout) findViewById(R.id.step_layout1);
		stepLayout2 = (RelativeLayout) findViewById(R.id.step_layout2);
		stepLayout3 = (RelativeLayout) findViewById(R.id.step_layout3);

		stepLayout6 = (RelativeLayout) findViewById(R.id.step_layout6);

		stepImage1 = (ImageView) findViewById(R.id.step_img1);
		stepImage1.setImageResource(R.drawable.reset_bg);

		layoutList.add(0, stepLayout1);
		layoutList.add(1, stepLayout2);
		layoutList.add(2, stepLayout3);

		desWifiName = (EditText) findViewById(R.id.deswifiname);
		desWifiPwd = (EditText) findViewById(R.id.deswifipwd);
		setCurrentWifi();
		desPwdEye = (ToggleButton) findViewById(R.id.despwdeye);
		devListView = (ListView) findViewById(R.id.devlistview);
		pw_two = (ProgressWheel) findViewById(R.id.progressBarTwo);

		desPwdEye.setChecked(true);
		desPwdEye.setOnCheckedChangeListener(myOnCheckedChangeListener);

		nextBtn1 = (Button) findViewById(R.id.step_btn1);
		nextBtn2 = (Button) findViewById(R.id.step_btn2);

		stepLayout6.setOnClickListener(myOnClickListener);
		rightBtn.setOnClickListener(myOnClickListener);
		leftBtn.setOnClickListener(myOnClickListener);
		nextBtn1.setOnClickListener(myOnClickListener);
		nextBtn2.setOnClickListener(myOnClickListener);

		showLayoutAtIndex(currentStep);

	}

	/**
	 * 40秒倒计时
	 * **/
	final Runnable r = new Runnable() {
		public void run() {
			while (progress < 361) {
				pw_two.incrementProgress();
				progress++;
				if (progress == 361) {
					handler.sendEmptyMessage(Consts.WHAT_WHEEL_DISMISS);
				}
				try {
					Thread.sleep(110);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	/**
	 * 密码显示隐藏
	 */
	OnCheckedChangeListener myOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if (arg1) {
				desWifiPwd
						.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);// 显示密码
			} else {
				desWifiPwd.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);// 隐藏密码
			}
			desWifiPwd.setSelection(desWifiPwd.getText().toString().length());
			desPwdEye.setChecked(arg1);
		}

	};

	/**
	 * 显示哪个布局
	 * 
	 * @param showIndex
	 */
	@SuppressWarnings("deprecation")
	private void showLayoutAtIndex(int showIndex) {
		if (showIndex < 0) {
			SmartConnectionConfigActivity.this.finish();
		} else {
			currentMenu.setText(titleID[showIndex]);
			int length = layoutList.size();

			for (int i = 0; i < length; i++) {
				RelativeLayout layout = layoutList.get(i);
				if (null != layout) {
					if (i == showIndex) {
						layout.setVisibility(View.VISIBLE);
					} else {
						layout.setVisibility(View.GONE);
					}
				}

			}
			if (showIndex >= 0 && showIndex < 3) {
				playSoundStep(showIndex);
			}
		}

		if (2 == showIndex) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(desWifiPwd, InputMethodManager.SHOW_FORCED);
			imm.hideSoftInputFromWindow(desWifiPwd.getWindowToken(), 0); // 强制隐藏键盘
		}

	}

	private void backMethod() {
		if (currentStep == (layoutList.size() - 1)) {
			SmartConnectionConfigActivity.this.finish();
		} else {
			showLayoutAtIndex(--currentStep);
		}

	}

	@Override
	public void onBackPressed() {
		backMethod();
	}

	/**
	 * 点击事件
	 */
	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				backMethod();
				break;
			case R.id.step_btn1:
				currentStep = 1;
				showLayoutAtIndex(currentStep);
				break;
			case R.id.step_btn2:
				currentStep = 2;
				showLayoutAtIndex(currentStep);
				// break;
			case R.id.btn_right:// 发局域网广播搜索局域网设备
				// case R.id.step_btn3:// 发局域网广播搜索局域网设备
				// createDialog("", false);
				isshow = true;
				pw_two.setVisibility(View.VISIBLE);
				stepLayout6.setVisibility(View.VISIBLE);
				progress = 0;
				pw_two.resetCount();
				Thread s = new Thread(r);
				s.start();
				playSoundStep(3);
				broadList.clear();
				Jni.queryDevice("", 0, 40 * 1000);
				// currentStep = 4;
				// showLayoutAtIndex(currentStep);
				break;
			case R.id.step_layout6:
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		setCurrentWifi();
		BitmapCache.getInstance().clearCache();
	}

	@Override
	protected void onPause() {
		super.onPause();
		BitmapCache.getInstance().clearCache();
		CacheUtil.saveDevList(deviceList);
		if (null != mediaPlayer) {
			mediaPlayer.stop();
		}
	}

	@Override
	protected void freeMe() {
		mediaPlayer.release();
	}

	private void playSoundStep(int index) {
		// try {
		// String file = "";
		// if (Consts.LANGUAGE_ZH == ConfigUtil
		// .getLanguage2(SmartConnectionConfigActivity.this)) {
		// file = stepSoundCH[index];
		// } else if (Consts.LANGUAGE_ZHTW == ConfigUtil
		// .getLanguage2(SmartConnectionConfigActivity.this)) {
		// file = stepSoundCH[index];
		// } else {
		// file = stepSoundEN[index];
		// }
		//
		// AssetFileDescriptor afd = assetMgr.openFd(file);
		// mediaPlayer.reset();
		//
		// // 使用MediaPlayer加载指定的声音文件。
		// mediaPlayer.setDataSource(afd.getFileDescriptor(),
		// afd.getStartOffset(), afd.getLength());
		// // 准备声音
		// mediaPlayer.prepare();
		// // 播放
		// mediaPlayer.start();
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	// 设置三种类型参数分别为String,Integer,String
	class AddDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int index = Integer.parseInt(params[0]);
			Device addDevice = broadList.get(index);
			String ip = addDevice.getIp();
			int port = addDevice.getPort();
			int addRes = -1;

			try {
				for (Device dev : deviceList) {
					if (dev.getFullNo() == addDevice.getFullNo()) {
						addRes = 0;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (0 != addRes) {
				boolean localFlag = Boolean.valueOf(statusHashMap
						.get(Consts.LOCAL_LOGIN));
				try {
					if (null != addDevice) {
						if (localFlag) {// 本地添加
							addRes = 0;
						} else {
							addDevice = DeviceUtil.addDevice2(addDevice,
									statusHashMap.get(Consts.KEY_USERNAME),
									addDevice.getNickName());
							if (null != addDevice) {
								addRes = 0;
							}
						}
					}

					if (0 == addRes) {
						broadList.remove(index);
						handler.sendMessage(handler
								.obtainMessage(Consts.WHAT_BROAD_DEVICE));
						addDevice.setOnlineStateLan(1);
						addDevice.setIp(ip);
						addDevice.setPort(port);
						addDevice.setHasAdded(true);
						deviceList.add(0, addDevice);
						CacheUtil.saveDevList(deviceList);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return addRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			dismissDialog();
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			if (0 == result) {
				showTextToast(R.string.add_device_succ);

				if (!hasNewDevice()) {
					finish();
				}

			} else {
				showTextToast(R.string.add_device_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	/**
	 * 检查是否还有新设备
	 * 
	 * @return
	 */
	public boolean hasNewDevice() {
		boolean hasNewDev = false;
		if (null == broadList || 0 == broadList.size()) {
		} else {
			for (Device dev : broadList) {
				if (!dev.isHasAdded()) {
					hasNewDev = true;
					break;
				}
			}
		}
		return hasNewDev;
	}

	/**
	 * 弹出添加设备界面
	 * */
	public void alertAddDialog(final int index) {
		// 提示对话框
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.tips)
				.setMessage(
						getResources().getString(R.string.wave_add_dev) + "   "
								+ broadList.get(index).getFullNo())
				.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								createDialog("", false);
								dialog.dismiss();
								AddDevTask task = new AddDevTask();
								String[] params = new String[3];
								params[0] = String.valueOf(index);
								task.execute(params);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {// && event.getRepeatCount() == 0
			if (!isshow) {
				backMethod();
			}
			return true;
		} else {
			return false;
		}
	}
}
