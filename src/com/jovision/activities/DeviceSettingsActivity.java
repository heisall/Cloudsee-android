package com.jovision.activities;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.SearchManager.OnCancelListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.activities.DeviceSettingsMainFragment.OnFuncActionListener;
import com.jovision.activities.ThirdDevListActivity.TimeOutProcess;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.ConfigUtil;

public class DeviceSettingsActivity extends BaseActivity implements
		OnFuncActionListener, OnClickListener {
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dev_settings_main);
		Bundle extras = getIntent().getExtras();
		if (null == extras) {
			finish();
			return;
		}
		myHandler = new MyHandler();
		InitViews();
		if (null == waitingDialog) {
			waitingDialog = new ProgressDialog(this);
			waitingDialog.setCancelable(false);
			waitingDialog
					.setMessage(getResources().getString(R.string.waiting));
			waitingDialog.show();
			// 获取当前设置
			// 获取设备参数 -> flag = FLAG_GET_PARAM, 分析 msg?
			window = extras.getInt("window");
			Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, false, 0,
					Consts.TYPE_GET_PARAM, null);
			new Thread(new TimeOutProcess(Consts.TYPE_GET_PARAM)).start();
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
				myHandler.removeMessages(Consts.TYPE_GET_PARAM);
				String allStr = obj.toString();

				MyLog.v(TAG, "文本数据--" + allStr);
				try {
					JSONObject dataObj = new JSONObject(allStr);
					int packet_type = dataObj.getInt("packet_type");
					switch (packet_type) {
					case JVNetConst.RC_GETPARAM:
						HashMap<String, String> map = ConfigUtil
								.genMsgMap(dataObj.getString("msg"));
						String md_enable = map.get("bMDEnable");
						if (md_enable == null) {
							showTextToast(R.string.not_support_this_func);
							mdEnable = -1;
						} else {
							mdEnable = Integer.valueOf(md_enable);
						}
						String alarm_enable = map.get("bAlarmEnable");
						if (alarm_enable == null) {
							showTextToast(R.string.not_support_this_func);
							alarmEnable = -1;
						} else {
							alarmEnable = Integer.valueOf(alarm_enable);
						}
						String alarmTime0 = map.get("alarmTime0");
						if (alarm_enable == null) {
							showTextToast(R.string.not_support_this_func);
							alarmTime0 = "";
						}
						initDevParamObject = new JSONObject();
						initDevParamObject.put("bAlarmEnable", alarmEnable);
						initDevParamObject.put("bMDEnable", mdEnable);
						initDevParamObject.put("alarmTime0", alarmTime0);

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
						break;// end of JVNetConst.RC_GETPARAM
					case JVNetConst.RC_EXTEND:
						int packet_subtype = dataObj.getInt("packet_count");
						int ex_type = dataObj.optInt("extend_type");
						if (packet_subtype == JVNetConst.RC_EX_MD
								&& ex_type == JVNetConst.EX_MD_SUBMIT) {
							// 移动侦测 ok
							mainListener.onMainAction(packet_type,
									packet_subtype, ex_type);
						} else if (packet_subtype == JVNetConst.RC_EX_ALARM
								&& ex_type == JVNetConst.EX_ALARM_SUBMIT) {
							// 安全防护ok
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
			Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, true, 0x07,
					0x02,
					String.format(Consts.FORMATTER_SET_ALARM_ONLY, enabled));
			break;
		case Consts.DEV_SETTINGS_MD:// 移动侦测
			Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, true, 0x06,
					0x02, String.format(Consts.FORMATTER_SET_MDENABLE, enabled));
			break;
		default:
			break;
		}
	}

	@Override
	public void OnFuncSelected(int func_index) {
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
			transaction.replace(R.id.fragment_container, alarmTimeFragment);
			transaction.addToBackStack(null);
			fragment_tag = 1;
			// Commit the transaction
			transaction.commit();
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
				onBackPressed();
				break;
			default:
				break;
			}

			break;

		default:
			break;
		}
	}

	class MyHandler extends Handler {
		@Override
		public void dispatchMessage(Message msg) {
			String strDescString = "";
			switch (msg.what) {
			case Consts.TYPE_GET_PARAM:
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
}
