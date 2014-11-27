package com.jovision.activities;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import barcode.zxing.activity.MipcaActivityCapture;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.bean.Device;
import com.jovision.commons.JVConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.PlayUtil;

public class JVAddDeviceActivity extends BaseActivity {

	private static final String TAG = "JVAddDeviceActivity";

	// public static final int ADD_DEV_SUCCESS = 0x60;// 添加设备成功--
	// public static final int ADD_DEV_FAILED = 0x61;// 添加设备失败--

	/** topBar */
	private Button leftBtn;
	private TextView currentMenu;
	private Button rightBtn;

	/** add device layout */
	private EditText devNumET;
	private EditText userET;
	private EditText pwdET;
	private Button saveBtn;

	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private Device addDevice;
	private Boolean qrAdd = false;// 是否二维码扫描添加设备

	private boolean hasBroadIP = false;// 是否广播完IP
	private int onLine = 0;
	private String ip = "";
	private int port = 0;
	int channelCount = -1;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		// 广播回调
		case Consts.CALL_LAN_SEARCH: {
			MyLog.v(TAG, "CALL_LAN_SEARCH = what=" + what + ";arg1=" + arg1
					+ ";arg2=" + arg1 + ";obj=" + obj.toString());
			// MyLog.v("广播回调", "onTabAction2:what=" + what + ";arg1=" + arg1
			// + ";arg2=" + arg1 + ";obj=" + obj.toString());
			// onTabAction:what=168;arg1=0;arg2=0;obj={"count":1,"curmod":0,"gid":"A","ip":"192.168.21.238","netmod":0,"no":283827713,"port":9101,"timeout":0,"type":59162,"variety":3}
			JSONObject broadObj;
			try {
				broadObj = new JSONObject(obj.toString());
				if (0 == broadObj.optInt("timeout")) {
					String broadDevNum = broadObj.optString("gid")
							+ broadObj.optInt("no");
					if (broadDevNum.equalsIgnoreCase(devNumET.getText()
							.toString())) {// 同一个设备
					// addDevice.setOnlineState(1);
					// addDevice.setIp(broadObj.optString("ip"));
					// addDevice.setPort(broadObj.optInt("port"));
						onLine = 1;
						ip = broadObj.optString("ip");
						port = broadObj.optInt("port");
						channelCount = broadObj.optInt("count");
					}

				} else if (1 == broadObj.optInt("timeout")) {
					hasBroadIP = true;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			MyLog.e(TAG,
					"jvadddeviceactivity hasBroadIP 888888888888888888888: "
							+ what + ", " + arg1 + ", " + arg2 + ", " + obj);

			break;
		}
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		Intent intent = getIntent();
		deviceList = CacheUtil.getDevList();
		qrAdd = intent.getBooleanExtra("QR", false);
		if (qrAdd) {
			Intent openCameraIntent = new Intent(JVAddDeviceActivity.this,
					MipcaActivityCapture.class);
			openCameraIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			JVAddDeviceActivity.this
					.startActivityForResult(openCameraIntent, 0);
		}
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.adddevice_layout);
		/** top bar */
		leftBtn = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setBackgroundResource(R.drawable.qr_icon);
		currentMenu.setText(R.string.str_help1_1);
		leftBtn.setOnClickListener(mOnClickListener);
		rightBtn.setOnClickListener(mOnClickListener);
		rightBtn.setVisibility(View.GONE);

		/** add device layout */
		devNumET = (EditText) findViewById(R.id.ystnum_et);
		userET = (EditText) findViewById(R.id.user_et);
		pwdET = (EditText) findViewById(R.id.pwd_et);
		saveBtn = (Button) findViewById(R.id.save_btn);

		userET.setText(JVAddDeviceActivity.this.getResources().getString(
				R.string.str_default_user));
		pwdET.setText(JVAddDeviceActivity.this.getResources().getString(
				R.string.str_default_pass));
		saveBtn.setBackgroundResource(R.drawable.blue_bg);
		saveBtn.setOnClickListener(mOnClickListener);
	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_left: {
				CacheUtil.saveDevList(deviceList);
				JVAddDeviceActivity.this.finish();
				break;
			}
			/** 二维码扫描添加设备 */
			case R.id.btn_right: {
				Intent openCameraIntent = new Intent(JVAddDeviceActivity.this,
						MipcaActivityCapture.class);
				openCameraIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				JVAddDeviceActivity.this.startActivityForResult(
						openCameraIntent, 0);
				break;
			}
			/** 保存 */
			case R.id.save_btn: {
				String devNum = devNumET.getText().toString().toUpperCase();
				String userName = userET.getText().toString();
				String userPwd = pwdET.getText().toString();
				saveMethod(devNum, userName, userPwd);
				break;
			}
			default:
				break;
			}
		}

	};

	/**
	 * 保存设备信息
	 * 
	 * @param devNum
	 * @param userName
	 * @param userPwd
	 */
	public void saveMethod(String devNum, String userName, String userPwd) {
		if (null == deviceList) {
			deviceList = new ArrayList<Device>();
		}
		int size = deviceList.size();
		if ("".equalsIgnoreCase(devNum)) {// 云视通号不可为空
			showTextToast(R.string.login_str_device_ytnum_notnull);
			return;
		} else if (!ConfigUtil.checkYSTNum(devNum)) {// 验证云视通号是否合法
			showTextToast(R.string.increct_yst_tips);
			return;
		} else if ("".equalsIgnoreCase(userName)) {// 用户名不可为空
			showTextToast(R.string.login_str_device_account_notnull);
			return;
		} else if (!ConfigUtil.checkDeviceUsername(userName)) {// 用户名是否合法
			showTextToast(R.string.login_str_device_account_error);
			return;
		} else if (!ConfigUtil.checkDevicePwd(userPwd)) {
			showTextToast(R.string.login_str_device_pass_error);
			return;
		} else if (size >= 100
				&& !Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {// 非本地多于100个设备不让再添加
			showTextToast(R.string.str_device_most_count);
			return;
		} else {
			// 判断一下是否已存在列表中
			boolean find = false;
			if (null != deviceList && 0 != deviceList.size()) {
				for (Device dev : deviceList) {
					if (devNum.equalsIgnoreCase(dev.getFullNo())) {
						find = true;
						break;
					}
				}
			}

			if (find) {
				devNumET.setText("");
				showTextToast(R.string.str_device_exsit);
				return;
			}
		}

		AddDevTask task = new AddDevTask();
		String[] strParams = new String[3];
		strParams[0] = ConfigUtil.getGroup(devNum);
		strParams[1] = String.valueOf(ConfigUtil.getYST(devNum));
		strParams[2] = String.valueOf(2);
		task.execute(strParams);
	}

	@Override
	public void onBackPressed() {
		// Intent intent = new Intent();
		// setResult(ADD_DEV_FAILED, intent);
		CacheUtil.saveDevList(deviceList);
		JVAddDeviceActivity.this.finish();
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == JVConst.BARCODE_RESULT) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			devNumET.setText(scanResult);
		} else {
			JVAddDeviceActivity.this.finish();
		}

	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	// 设置三种类型参数分别为String,Integer,String
	class AddDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {

			int addRes = -1;
			boolean localFlag = Boolean.valueOf(statusHashMap
					.get(Consts.LOCAL_LOGIN));
			try {
				MyLog.e(TAG, "getChannelCount E = ");

				// 非3G广播获取通道数量
				if (PlayUtil.broadCast(JVAddDeviceActivity.this)) {
					while (!hasBroadIP) {
						Thread.sleep(100);
					}
				}

				MyLog.e(TAG, "getChannelCount C = " + channelCount);
				if (channelCount <= 0) {
					channelCount = Jni.getChannelCount(params[0],
							Integer.parseInt(params[1]),
							Integer.parseInt(params[2]));
				}

				MyLog.e(TAG, "getChannelCount X = " + channelCount);
				if (channelCount <= 0) {
					channelCount = 1;
				}

				addDevice = new Device("", 0, params[0],
						Integer.parseInt(params[1]), userET.getText()
								.toString(), pwdET.getText().toString(), false,
						channelCount, 0);

				// MyLog.v(TAG, "dev = " + addDev.toString());
				if (null != addDevice) {
					if (localFlag) {// 本地添加
						addRes = 0;
					} else {
						addDevice = DeviceUtil.addDevice(
								statusHashMap.get("KEY_USERNAME"), addDevice);
						if (null != addDevice) {
							addRes = 0;
						}
						if (0 <= addDevice.getChannelList().size()) {
							if (0 == DeviceUtil.addPoint(addDevice.getFullNo(),
									addDevice.getChannelList().size())) {
								addDevice.setChannelList(DeviceUtil
										.getDevicePointList(addDevice,
												addDevice.getFullNo()));
								addRes = 0;
							} else {
								DeviceUtil.unbindDevice(
										statusHashMap.get(Consts.KEY_USERNAME),
										addDevice.getFullNo());
								addRes = -1;
							}
						}
					}
				}

				if (0 == addRes) {
					addDevice.setOnlineState(onLine);
					addDevice.setIp(ip);
					addDevice.setPort(port);
					deviceList.add(0, addDevice);
					if (!localFlag) {
						DeviceUtil.refreshDeviceState(
								statusHashMap.get(Consts.KEY_USERNAME),
								deviceList);
					}
					CacheUtil.saveDevList(deviceList);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return addRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			dismissDialog();
			if (0 == result) {
				showTextToast(R.string.add_device_succ);
				// Intent intent = new Intent();
				// setResult(ADD_DEV_SUCCESS, intent);

				JVAddDeviceActivity.this.finish();

			} else {
				showTextToast(R.string.add_device_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}
}
