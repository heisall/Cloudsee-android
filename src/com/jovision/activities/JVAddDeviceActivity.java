package com.jovision.activities;

import java.util.ArrayList;

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
import com.jovision.commons.JVConst;
import com.jovision.commons.MySharedPreference;
import com.jovision.newbean.BeanUtil;
import com.jovision.newbean.Device;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;

public class JVAddDeviceActivity extends BaseActivity {

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

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initSettings() {
		Intent intent = getIntent();
		String devJsonString = intent.getStringExtra("DeviceList");
		deviceList = BeanUtil.stringToDevList(devJsonString);
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

		/** add device layout */
		devNumET = (EditText) findViewById(R.id.ystnum_et);
		userET = (EditText) findViewById(R.id.user_et);
		pwdET = (EditText) findViewById(R.id.pwd_et);
		saveBtn = (Button) findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(mOnClickListener);
	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_left: {
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
	 */
	public void saveMethod(String devNum, String userName, String userPwd) {

		if ("".equalsIgnoreCase(devNum)) {// 云视通号不可为空
			showTextToast(R.string.login_str_device_ytnum_notnull);
		} else if (!ConfigUtil.checkYSTNum(devNum)) {// 验证云视通号是否合法
			showTextToast(R.string.increct_yst_tips);
		} else if ("".equalsIgnoreCase(userName)) {// 用户名不可为空
			showTextToast(R.string.login_str_device_account_notnull);
		} else if (!ConfigUtil.checkDeviceUsername(userName)) {// 用户名是否合法
			showTextToast(R.string.login_str_device_account_error);
		} else if (!ConfigUtil.checkDevicePwd(userPwd)) {
			showTextToast(R.string.login_str_device_pass_error);
		} else {
			// 判断一下是否已存在列表中
			boolean find = false;
			if (null != deviceList && 0 != deviceList.size()) {
				for (int i = 0; i < deviceList.size(); i++) {
					if (devNum.equalsIgnoreCase(deviceList.get(i).getFullNo())) {
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
		strParams[2] = String.valueOf(2 * 1000);
		task.execute(strParams);

		// LoginUtil.deviceName = devNumET.getText().toString().toUpperCase();//
		// // 云视通号将用户输入的小写均转成大写
		// LoginUtil.deviceNum = devNumET.getText().toString().toUpperCase();//
		// // 云视通号将用户输入的小写均转成大写
		// LoginUtil.deviceLoginUser = userET.getText().toString();
		// LoginUtil.deviceLoginPass = pwdET.getText().toString();
		// devNumET.setText("");
		// userET.setText(getResources().getString(R.string.str_default_user));
		// pwdET.setText(getResources().getString(R.string.str_default_pass));
		// // 添加设备线程
		// // centerLoading.setVisibility(View.VISIBLE);
		// if (null == dialog) {
		// dialog = new ProgressDialog(JVMainActivity.this);
		// dialog.setCancelable(false);
		// }
		//
		// dialog.setMessage(getResources().getString(
		// R.string.str_loading_adddevice));
		// dialog.show();
		// BaseApp.ADDDEVICE = new Device();
		// BaseApp.ADDDEVICE.deviceNum = LoginUtil.deviceNum;
		// BaseApp.ADDDEVICE.deviceLoginUser = LoginUtil.deviceLoginUser;
		// BaseApp.ADDDEVICE.deviceLoginPwd = LoginUtil.deviceLoginPass;
		//
		// AddDeviceThread adThread1 = new AddDeviceThread(JVMainActivity.this);
		// adThread1.start();
		// // requested = false;
		//
		// // 3G添加设备不广播，走两个流程
		// if (!BaseApp.LOCAL_LOGIN_FLAG) {
		// // Thread thread = new Thread() {
		// //
		// // @Override
		// // public void run() {
		// // // TODO Auto-generated method stub
		// // addDevice2Way();
		// // super.run();
		// // }
		// //
		// // };
		// // thread.start();
		// addDevice.setBackgroundResource(R.drawable.adddevice);
		// addDeviceLayout.setVisibility(View.GONE);
		// }
		// // else {
		// // BaseApp.ADDDEVICE.devicePointCount = 4;
		// // }
		//
		// // if (BaseApp.is3G( false)) {
		// // MyLog.v("3G不广播", "直接添加");
		// // Thread thread = new Thread() {
		// //
		// // @Override
		// // public void run() {
		// // // TODO Auto-generated method stub
		// // addDevice2Way();
		// // super.run();
		// // }
		// //
		// // };
		// // thread.start();
		// // addDevice.setBackgroundResource(R.drawable.adddevice);
		// // addDeviceLayout.setVisibility(View.GONE);
		// // } else {// wifi添加设备走三个流程
		// // if (!JVSUDT.IS_BROADCASTING) {// 没有正在广播中
		// // // requested = true;
		// //
		// // // 添加设备广播搜索通道数量
		// // BaseApp.initBroadCast();
		// // JVSUDT.ADD_DEVICE = true;// 添加设备广播
		// // BaseApp.sendBroadCast();
		// //
		// // addDevice.setBackgroundResource(R.drawable.adddevice);
		// // addDeviceLayout.setVisibility(View.GONE);
		// // } else {// 如果有正在广播的，否则通过从服务器上获取通道和通过连接-1通道获取
		// // addDevice.setBackgroundResource(R.drawable.adddevice);
		// // addDeviceLayout.setVisibility(View.GONE);
		// // Thread thread = new Thread() {
		// //
		// // @Override
		// // public void run() {
		// // // TODO Auto-generated method stub
		// // addDevice2Way();
		// // super.run();
		// // }
		// //
		// // };
		// // thread.start();
		// // }
		// //
		// // }
		//
		// // mTimer = new Timer(true);
		// // mTimerTask = new BroadCastTask(JVMainActivity.this);
		// // mTimer.schedule(mTimerTask, 1000, 1000);
		// }
	}

	@Override
	public void onBackPressed() {
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
		}

	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	// 设置三种类型参数分别为String,Integer,String
	class AddDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int channelCount = -1;
			int addRes = -1;
			boolean localFlag = Boolean.valueOf(statusHashMap
					.get(Consts.LOCAL_LOGIN));
			try {
				channelCount = Jni.getChannelCount(params[0],
						Integer.parseInt(params[1]),
						Integer.parseInt(params[2]));

				if (channelCount <= 0) {
					channelCount = 4;
				}

				Device addDev = new Device("", 0, params[0],
						Integer.parseInt(params[1]), userET.getText()
								.toString(), pwdET.getText().toString(), false,
						channelCount, 0);

				if (null != addDev) {
					deviceList.add(addDev);
					if (localFlag) {// 本地添加
						MySharedPreference.putString(Consts.LOCAL_DEVICE_LIST,
								deviceList.toString());
						addRes = 0;
					} else {
						addRes = DeviceUtil.addDevice(
								statusHashMap.get("KEY_USERNAME"), addDev);
					}
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

}
