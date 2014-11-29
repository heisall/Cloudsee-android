package com.jovision.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;

public class DeviceSettingsActivity extends BaseActivity {
	protected boolean bConnectedFlag = true;
	private ProgressDialog waitingDialog;
	private DeviceSettingsMainFragment deviceSettingsMainFragment;
	private int fragment_tag = 0;// 0，设置主界面； 1设置时间

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dev_settings_main_fragment);
		Bundle extras = getIntent().getExtras();
		if (null == extras) {
			finish();
			return;
		}
		if (null == waitingDialog) {
			waitingDialog = new ProgressDialog(this);
			waitingDialog.setCancelable(false);
			waitingDialog
					.setMessage(getResources().getString(R.string.waiting));
			waitingDialog.show();
			// 获取当前设置
			// 获取设备参数 -> flag = FLAG_GET_PARAM, 分析 msg?
			int window = extras.getInt("window");
			Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, false, 0,
					Consts.TYPE_GET_PARAM, null);
		}

		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		// if (findViewById(R.id.fragment_container) != null) {
		//
		// // However, if we're being restored from a previous state,
		// // then we don't need to do anything and should return or else
		// // we could end up with overlapping fragments.
		// if (savedInstanceState != null) {
		// return;
		// }
		// deviceSettingsMainFragment = new DeviceSettingsMainFragment();
		// Bundle bundle1 = new Bundle();
		// bundle1.putString("ALARM_TIME", "08:00-10:00");
		// deviceSettingsMainFragment.setArguments(bundle1);
		// FragmentManager fm = getSupportFragmentManager();
		// FragmentTransaction ft = getSupportFragmentManager()
		// .beginTransaction();
		// ft.add(R.id.fragment_container, deviceSettingsMainFragment)
		// .commitAllowingStateLoss();
		// fragment_tag = 0;
		// }
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
				String allStr = obj.toString();

				MyLog.v(TAG, "文本数据--" + allStr);
				try {
					JSONObject dataObj = new JSONObject(allStr);

					switch (dataObj.getInt("flag")) {

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

}
