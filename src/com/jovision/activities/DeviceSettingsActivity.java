package com.jovision.activities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.activities.DevSettingsAlarmTimeFragment.OnAlarmTimeActionListener;
import com.jovision.activities.DeviceSettingsMainFragment.OnFuncActionListener;
import com.jovision.bean.Device;
import com.jovision.commons.JVDeviceConst;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.tencent.stat.StatService;

public class DeviceSettingsActivity extends BaseActivity implements
		OnFuncActionListener, OnClickListener, OnAlarmTimeActionListener {
	protected boolean bConnectedFlag = true;
	private ProgressDialog waitingDialog;
	private DeviceSettingsMainFragment deviceSettingsMainFragment;
	private int fragment_tag = 0;// 0，设置主界面； 1设置时间
	private int alarmEnable = -1;
	private int mdEnable = -1;
	private int window = 0;
	private OnMainListener mainListener;
	private Button backBtn;
	private Button rightBtn;
	public TextView titleTv;
	private JSONObject initDevParamObject;
	private MyHandler myHandler;
	private String startTimeSaved, endTimeSaved;
	private String startTimeSetting, endTimeSetting;
	private String alarmTime0;
	private int alarm_way_flag = -1; // 报警的方式，走云视通还是报警服务器 0:报警服务器 1:云视通
	private Device device;
	private ArrayList<Device> deviceList;
	private int deviceIndex = 0;
	private DeviceSettingsActivity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dev_settings_main);
		Bundle extras = getIntent().getExtras();
		activity = this;
		if (null == extras) {
			finish();
			return;
		}
		myHandler = new MyHandler();
		window = extras.getInt("window");
		deviceIndex = extras.getInt("deviceIndex");
		deviceList = CacheUtil.getDevList();
		device = deviceList.get(deviceIndex);
		InitViews();
		if (null == waitingDialog) {
			waitingDialog = new ProgressDialog(this);
			waitingDialog.setCancelable(false);
			waitingDialog
					.setMessage(getResources().getString(R.string.waiting));
			waitingDialog.show();
			// 获取当前设置
			// 获取设备参数 -> flag = FLAG_GET_PARAM, 分析 msg?
			// Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, false, 0,
			// JVNetConst.JVN_STREAM_INFO, null);
			Jni.sendTextData(window, JVNetConst.JVN_RSP_TEXTDATA, 8,
					JVNetConst.JVN_STREAM_INFO);
			new Thread(new TimeOutProcess(JVNetConst.JVN_STREAM_INFO)).start();
		}

	}

	private void InitViews() {
		backBtn = (Button) findViewById(R.id.btn_left);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		titleTv = (TextView) findViewById(R.id.currentmenu);
		backBtn.setOnClickListener(this);
		titleTv.setText(R.string.str_audio_monitor);
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		switch (what) {
		// 连接结果
		case Consts.CALL_CONNECT_CHANGE:
			switch (arg2) {
			case JVNetConst.DISCONNECT_OK: {

				bConnectedFlag = false;

			}
				break;
			// 4 -- 连接失败
			case JVNetConst.CONNECT_FAILED: {
				bConnectedFlag = false;
				if (waitingDialog != null && waitingDialog.isShowing())
					waitingDialog.dismiss();
				try {
					JSONObject connectObj = new JSONObject(obj.toString());
					String errorMsg = connectObj.getString("msg");
					if ("password is wrong!".equalsIgnoreCase(errorMsg)
							|| "pass word is wrong!".equalsIgnoreCase(errorMsg)) {// 密码错误时提示身份验证失败
						showTextToast(R.string.connfailed_auth);
					} else if ("channel is not open!"
							.equalsIgnoreCase(errorMsg)) {// 无该通道服务
						showTextToast(R.string.connfailed_channel_notopen);
					} else if ("connect type invalid!"
							.equalsIgnoreCase(errorMsg)) {// 连接类型无效
						showTextToast(R.string.connfailed_type_invalid);
					} else if ("client count limit!".equalsIgnoreCase(errorMsg)) {// 超过主控最大连接限制
						showTextToast(R.string.connfailed_maxcount);
					} else if ("connect timeout!".equalsIgnoreCase(errorMsg)) {//
						showTextToast(R.string.connfailed_timeout);
					} else {// "Connect failed!"
						showTextToast(R.string.connect_failed);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
				break;
			case JVNetConst.ABNORMAL_DISCONNECT:
			case JVNetConst.SERVICE_STOP:
				if (waitingDialog != null && waitingDialog.isShowing())
					waitingDialog.dismiss();
				bConnectedFlag = false;
				showTextToast(R.string.str_alarm_connect_except);
				finish();
				break;
			default:
				if (waitingDialog != null && waitingDialog.isShowing())
					waitingDialog.dismiss();
				bConnectedFlag = false;
				break;
			}
			;
			break;
		case Consts.CALL_TEXT_DATA:
			MyLog.i(TAG, "CALL_TEXT_DATA: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);
			switch (arg2) {
			case JVNetConst.JVN_RSP_TEXTDATA:// 文本数据
				if (waitingDialog != null && waitingDialog.isShowing()) {
					waitingDialog.dismiss();
				}
				myHandler.removeMessages(JVNetConst.JVN_STREAM_INFO);
				String allStr = obj.toString();

				MyLog.v(TAG, "文本数据--" + allStr);
				try {
					JSONObject dataObj = new JSONObject(allStr);
					int flag = dataObj.getInt("flag");
					switch (flag) {
					case JVNetConst.JVN_STREAM_INFO:
						HashMap<String, String> map = ConfigUtil
								.genMsgMap(dataObj.getString("msg"));
						// 先判断MobileQuality
						String MobileQuality = map.get("MobileQuality");
						String MobileCH = null;
						if (null == MobileQuality) {// 没这个字段说明是老设备，再判断MobileCH是否为2
							MobileCH = map.get("MobileCH");
							if (MobileCH != null)// 这种情况，直接不让进设备设置界面
							{
								if (!(MobileCH.equals("2"))) {
									showTextToast(R.string.not_support_this_func);
									this.finish();
									return;
								} else {
									// 只有安全防护，走设备服务器
									alarm_way_flag = 0;
								}
							} else {
								showTextToast(R.string.not_support_this_func);
								this.finish();
								return;
							}
						} else {
							// 如果有，走云视通
							alarm_way_flag = 1;
						}
						if (alarm_way_flag == 1) {
							String alarm_enable = map.get("bAlarmEnable");
							if (alarm_enable == null) {
								alarmEnable = -1;
							} else {
								alarmEnable = Integer.valueOf(alarm_enable);
							}

							String md_enable = map.get("bMDEnable");
							if (md_enable == null) {
								// showTextToast(R.string.not_support_this_func);
								mdEnable = -1;
							} else {
								mdEnable = Integer.valueOf(md_enable);
							}

							alarmTime0 = map.get("alarmTime0");// alarmTime0
							if (alarm_enable == null) {
								// showTextToast(R.string.not_support_this_func);
								alarmTime0 = "";
							}
						} else {
							alarmEnable = device.getAlarmSwitch();
						}
						initDevParamObject = new JSONObject();
						initDevParamObject.put("bAlarmEnable", alarmEnable);
						initDevParamObject.put("bMDEnable", mdEnable);
						initDevParamObject.put("alarmTime0", alarmTime0);
						initDevParamObject.put("alarmWay", alarm_way_flag);

						// Check that the activity is using the layout version
						// with
						// the fragment_container FrameLayout
						if (findViewById(R.id.fragment_container) != null) {

							// However, if we're being restored from a previous
							// state,
							// then we don't need to do anything and should
							// return or else
							// we could end up with overlapping fragments.
							// if (savedInstanceState != null) {
							// return;
							// }
							deviceSettingsMainFragment = new DeviceSettingsMainFragment();
							Bundle bundle1 = new Bundle();
							bundle1.putString("KEY_PARAM",
									initDevParamObject.toString());
							deviceSettingsMainFragment.setArguments(bundle1);
							FragmentManager fm = getSupportFragmentManager();
							FragmentTransaction ft = getSupportFragmentManager()
									.beginTransaction();
							ft.add(R.id.fragment_container,
									deviceSettingsMainFragment)
									.commitAllowingStateLoss();
							fragment_tag = 0;
						}
						break;

					default:
						break;
					}
					int packet_type = dataObj.getInt("packet_type");
					switch (packet_type) {
					// case JVNetConst.RC_GETPARAM:
					//
					// break;// end of JVNetConst.RC_GETPARAM
					case JVNetConst.RC_EXTEND:
						int packet_subtype = dataObj.getInt("packet_count");
						int ex_type = dataObj.optInt("extend_type");
						if (packet_subtype == JVNetConst.RC_EX_MD
								&& ex_type == JVNetConst.EX_MD_SUBMIT) {
							// 移动侦测 ok
							initDevParamObject.put("bMDEnable", mdEnable);
							mainListener.onMainAction(packet_type,
									packet_subtype, ex_type);
						} else if (packet_subtype == JVNetConst.RC_EX_ALARM
								&& ex_type == JVNetConst.EX_ALARM_SUBMIT) {
							// 安全防护或者设置安全防护时间ok
							initDevParamObject.put("bAlarmEnable", alarmEnable);
							mainListener.onMainAction(packet_type,
									packet_subtype, ex_type);
						}
						break;
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub

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
	public void OnFuncEnabled(int func_index, int enabled) {
		// TODO Auto-generated method stub
		waitingDialog.show();
		switch (func_index) {
		case Consts.DEV_SETTINGS_ALARM:// 安全防护
			alarmEnable = enabled;
			if (alarm_way_flag == 1) {
				Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, true, 0x07,
						0x02,
						String.format(Consts.FORMATTER_SET_ALARM_ONLY, enabled));
			} else {
				AlarmSwitchTask task = new AlarmSwitchTask();
				String[] params = new String[3];
				task.execute(params);
			}
			break;
		case Consts.DEV_SETTINGS_MD:// 移动侦测
			mdEnable = enabled;
			Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, true, 0x06,
					0x02, String.format(Consts.FORMATTER_SET_MDENABLE, enabled));
			break;
		default:
			break;
		}
	}

	// 设置三种类型参数分别为String,Integer,String
	class AlarmSwitchTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int switchRes = -1;// 0成功， 1失败，1000离线
			try {
				if (null == device) {
					device = deviceList.get(deviceIndex);
				}
				if (JVDeviceConst.DEVICE_SERVER_ONLINE == device
						.getServerState()) {
					int sendTag = 0;
					if (JVDeviceConst.DEVICE_SWITCH_OPEN == device
							.getAlarmSwitch()) {
						sendTag = JVDeviceConst.DEVICE_SWITCH_CLOSE;
					} else if (JVDeviceConst.DEVICE_SWITCH_CLOSE == device
							.getAlarmSwitch()) {
						sendTag = JVDeviceConst.DEVICE_SWITCH_OPEN;
					}
					switchRes = DeviceUtil.saveSettings(
							JVDeviceConst.DEVICE_ALARTM_SWITCH, sendTag,
							activity.statusHashMap.get(Consts.KEY_USERNAME),
							device.getFullNo());
				} else {
					switchRes = 1000;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return switchRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			waitingDialog.dismiss();
			if (0 == result) {
				try {
					activity.initDevParamObject
							.put("bAlarmEnable", alarmEnable);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mainListener.onMainAction(JVNetConst.RC_EXTEND,
						JVNetConst.RC_EX_ALARM, JVNetConst.EX_ALARM_SUBMIT);
				StatService.trackCustomEvent(activity, "Alarm", activity
						.getResources().getString(R.string.str_alarm2));
				if (JVDeviceConst.DEVICE_SWITCH_OPEN == device.getAlarmSwitch()) {
					device.setAlarmSwitch(JVDeviceConst.DEVICE_SWITCH_CLOSE);
					showTextToast(R.string.protect_close_succ);
				} else if (JVDeviceConst.DEVICE_SWITCH_CLOSE == device
						.getAlarmSwitch()) {
					device.setAlarmSwitch(JVDeviceConst.DEVICE_SWITCH_OPEN);
					showTextToast(R.string.protect_open_succ);
				}

				// [{"fullNo":"S52942216","port":0,"hasWifi":1,"isDevice":0,"no":52942216,"is05":false,"onlineState":-1182329167,"channelList":[{"channel":1,"channelName":"S52942216_1","index":0}],"isHomeProduct":false,"ip":"","pwd":"123","nickName":"S52942216","deviceType":2,"alarmSwitch":1,"gid":"S","user":"abc","serverState":1,"doMain":""}]
				CacheUtil.saveDevList(deviceList);
			} else if (1000 == result) {
				showTextToast(R.string.device_offline);
			} else {
				if (JVDeviceConst.DEVICE_SWITCH_OPEN == device.getAlarmSwitch()) {
					showTextToast(R.string.protect_close_fail);
				} else if (JVDeviceConst.DEVICE_SWITCH_CLOSE == device
						.getAlarmSwitch()) {
					showTextToast(R.string.protect_open_fail);
				}
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

	@Override
	public void OnFuncSelected(int func_index, String params) {
		// TODO Auto-generated method stub
		switch (func_index) {
		case Consts.DEV_SETTINGS_ALARMTIME:// 防护时间段
			titleTv.setText(R.string.str_protected_time);
			DevSettingsAlarmTimeFragment alarmTimeFragment = new DevSettingsAlarmTimeFragment();
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			// Replace whatever is in the fragment_container
			// view
			// with this
			// fragment,
			// and add the transaction to the back stack so the
			// user
			// can
			// navigate back
			JSONObject paramObject = null;
			try {
				paramObject = new JSONObject(params);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (paramObject == null) {
					paramObject = new JSONObject();
				}
				try {
					paramObject.put("st", "00:00");
					paramObject.put("et", "23:59");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
			Bundle bundle1 = new Bundle();
			bundle1.putString("START_TIME",
					paramObject.optString("st", "00:00"));
			bundle1.putString("END_TIME", paramObject.optString("et", "23:59"));
			alarmTimeFragment.setArguments(bundle1);
			transaction.replace(R.id.fragment_container, alarmTimeFragment);
			// transaction.addToBackStack(null);
			transaction.remove(deviceSettingsMainFragment);
			fragment_tag = 1;
			// Commit the transaction
			transaction.commitAllowingStateLoss();
			break;
		default:
			break;
		}
	}

	// 绑定接口
	@Override
	public void onAttachFragment(Fragment fragment) {
		try {
			mainListener = (OnMainListener) fragment;
		} catch (Exception e) {
			throw new ClassCastException(this.toString()
					+ " must implement OnMainListener");
		}
		super.onAttachFragment(fragment);
	}

	public interface OnMainListener {
		public void onMainAction(int packet_type, int packet_subtype,
				int ex_type);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_left:
			switch (fragment_tag) {
			case 0:
				finish();
				break;
			case 1:
				titleTv.setText(R.string.str_audio_monitor);
				// onBackPressed();
				deviceSettingsMainFragment = new DeviceSettingsMainFragment();
				Bundle bundle1 = new Bundle();
				bundle1.putString("KEY_PARAM", initDevParamObject.toString());
				deviceSettingsMainFragment.setArguments(bundle1);
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				ft.replace(R.id.fragment_container, deviceSettingsMainFragment)
						.commitAllowingStateLoss();
				fragment_tag = 0;
				break;
			default:
				break;
			}

			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			switch (fragment_tag) {
			case 0:
				finish();
				break;
			case 1:
				titleTv.setText(R.string.str_audio_monitor);
				// onBackPressed();
				deviceSettingsMainFragment = new DeviceSettingsMainFragment();
				Bundle bundle1 = new Bundle();
				bundle1.putString("KEY_PARAM", initDevParamObject.toString());
				deviceSettingsMainFragment.setArguments(bundle1);
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				ft.replace(R.id.fragment_container, deviceSettingsMainFragment)
						.commitAllowingStateLoss();
				fragment_tag = 0;
				break;
			default:
				break;
			}
		}
		return true;
	}

	class MyHandler extends Handler {
		@Override
		public void dispatchMessage(Message msg) {
			String strDescString = "";
			switch (msg.what) {
			case JVNetConst.JVN_STREAM_INFO:
				if (waitingDialog != null && waitingDialog.isShowing())
					waitingDialog.dismiss();
				strDescString = getResources().getString(
						R.string.str_getdev_params_timeout);
				showTextToast(strDescString);
				finish();
				break;
			default:
				break;
			}
		}
	}

	class TimeOutProcess implements Runnable {
		private int tag;

		public TimeOutProcess(int arg1) {
			tag = arg1;
		}

		@Override
		public void run() {
			myHandler.sendEmptyMessageDelayed(tag, 12000);
		}
	}

	@Override
	public void OnAlarmTimeSaved(String startTime, String endTime) {
		// TODO Auto-generated method stub
		startTimeSetting = startTime;
		endTimeSetting = endTime;
		waitingDialog.show();
		if (startTime.equals("00:00") && endTime.equals("23:59")) {
			// 全天
			Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, true, 0x07,
					0x02, String.format(Consts.FORMATTER_SET_ALARM_TIME,
							"00:00:00-23:59:59"));
			new Thread(new TimeOutProcess(JVNetConst.JVN_STREAM_INFO)).start();
		} else {
			String alarmTime0 = String
					.format("%s:00-%s:00", startTime, endTime);
			MyLog.e("Alarm", "alarmTime0:" + alarmTime0);
			Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, true, 0x07,
					0x02,
					String.format(Consts.FORMATTER_SET_ALARM_TIME, alarmTime0));
			new Thread(new TimeOutProcess(JVNetConst.JVN_STREAM_INFO)).start();
		}
	}

	@Override
	public void OnAlarmTimeSavedResult(int ret) {
		// TODO Auto-generated method stub
		startTimeSaved = startTimeSetting;
		endTimeSaved = endTimeSetting;

		Bundle bundle1 = new Bundle();
		try {
			initDevParamObject.put("alarmTime0", startTimeSaved + "-"
					+ endTimeSaved);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bundle1.putString("KEY_PARAM", initDevParamObject.toString());
		deviceSettingsMainFragment.setArguments(bundle1);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_container, deviceSettingsMainFragment)
				.commitAllowingStateLoss();
		fragment_tag = 0;
	}
}
