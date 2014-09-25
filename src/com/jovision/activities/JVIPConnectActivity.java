package com.jovision.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.Device;
import com.jovision.commons.JVConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.LoginUtil;

/**
 * ip直连界面
 * 
 * @author suifupeng
 */
@SuppressLint("InlinedApi")
public class JVIPConnectActivity extends BaseActivity implements
		OnClickListener {
	private Button back; // 返回键
	private TextView currentMenu;// 当前页面名称
	private TextView ipAddrText;
	private TextView portText;
	private TextView userNameText;
	private TextView pwdText;
	private EditText ipAddress;
	private EditText portNum;
	private EditText userName = null;
	private EditText pwd = null;
	private LinearLayout ipconLinear;
	private Button save;
	private RadioGroup change; // 切换ip直连或云视通号连接
	private RadioButton changeToIP;
	private RadioButton changeToYst;
	private Intent intent;
	private boolean isEnterActivity = true;
	private LinearLayout portLayout;

	private Device editDevice;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case JVConst.EDIT_DEVICE_SUCCESS:
			showTextToast(R.string.login_str_device_edit_success);
			dismissDialog();
			break;
		case JVConst.EDIT_DEVICE_FAILED:
			showTextToast(R.string.login_str_device_edit_failed);
			dismissDialog();
			break;
		default:
			break;
		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.handleMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.ipconnect_layout);
		editDevice = (Device) getIntent().getSerializableExtra("EditDevice");
		ipconLinear = (LinearLayout) findViewById(R.id.ipconLinear);
		change = (RadioGroup) findViewById(R.id.change);
		changeToIP = (RadioButton) findViewById(R.id.changetoIP);
		changeToYst = (RadioButton) findViewById(R.id.changetoyst);
		change.setOnCheckedChangeListener(onCheckedChangeListener);
		back = (Button) findViewById(R.id.back);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_connect_mode);
		portText = (TextView) findViewById(R.id.port);
		userNameText = (TextView) findViewById(R.id.userName);
		pwdText = (TextView) findViewById(R.id.pwd);
		portText = (TextView) findViewById(R.id.port);
		ipAddrText = (TextView) findViewById(R.id.ipAddress);
		ipAddress = (EditText) findViewById(R.id.ipAddress_edit);
		portLayout = (LinearLayout) findViewById(R.id.portlayout);
		portNum = (EditText) findViewById(R.id.port_edit);
		userName = (EditText) findViewById(R.id.userName_edit);
		pwd = (EditText) findViewById(R.id.pwd_edit);
		save = (Button) findViewById(R.id.editsave);
		// 获取当前语言
		if (ConfigUtil.isLanZH()) {// 中文
			ipAddrText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			userNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			pwdText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			portText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			changeToIP.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			changeToYst.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

		} else {// 英文或其他
			ipAddrText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			userNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			pwdText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			portText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			changeToIP.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			changeToYst.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		}
		back.setOnClickListener(this);
		save.setOnClickListener(this);
		intent = getIntent();
		if (null != intent) {
			if (editDevice != null) {
				if (1 == editDevice.isDevice) {// 设备为ip直连
					MyLog.v("sss", "ip true");
					change.check(R.id.changetoIP);
					ipAddrText.setText(getString(R.string.login_str_ipaddress));
					portLayout.setVisibility(View.VISIBLE);
					ipAddress.setText(editDevice.deviceLocalIp);
					ipAddress.setEnabled(true);
					ipAddress.setBackgroundResource(R.drawable.regist_edittext);
					ipAddress.setHint("192.168.1.1");
					ipAddress.setSelection(editDevice.deviceLocalIp.length());
					if (null == editDevice.deviceLocalIp
							|| "".equals(editDevice.deviceLocalIp)) {
						portNum.setText("9101");
						portNum.setSelection("9101".length());
					} else {
						portNum.setText(String
								.valueOf(editDevice.deviceLocalPort));
						portNum.setSelection(String.valueOf(
								editDevice.deviceLocalPort).length());
					}
				} else {// 设备为云视通设备
					MyLog.v("sss", "device true");
					change.check(R.id.changetoyst);
					ipAddrText.setText(getString(R.string.login_str_yst_num));
					portLayout.setVisibility(View.GONE);
					ipAddress.setText(editDevice.deviceNum);
					ipAddress.setEnabled(false);
					ipAddress
							.setBackgroundResource(R.drawable.regist_edittext_gray);
					ipAddress.setHint(null);
				}
				userName.setText(editDevice.deviceLoginUser);
				userName.setSelection(editDevice.deviceLoginUser.length());
				pwd.setText(editDevice.deviceLoginPwd);
				pwd.setSelection(editDevice.deviceLoginPwd.length());
			}
		}

	}

	// onCheckedChanged事件
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.changetoIP:
				if (!isEnterActivity) {
					Animation ani = AnimationUtils.loadAnimation(
							JVIPConnectActivity.this, R.anim.rotate_out);
					ani.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							ipAddrText
									.setText(getString(R.string.login_str_ipaddress));
							portLayout.setVisibility(View.VISIBLE);
							ipAddress.setText(editDevice.deviceLocalIp);
							ipAddress.setEnabled(true);
							ipAddress
									.setBackgroundResource(R.drawable.regist_edittext);
							ipAddress.setHint("192.168.1.1");
							ipAddress.setSelection(editDevice.deviceLocalIp
									.length());
							if (null == editDevice.deviceLocalIp
									|| "".equals(editDevice.deviceLocalIp)) {
								portNum.setText("9101");
								portNum.setSelection("9101".length());
							} else {
								portNum.setText(String
										.valueOf(editDevice.deviceLocalPort));
								portNum.setSelection(String.valueOf(
										editDevice.deviceLocalPort).length());
							}
							ipconLinear.startAnimation(AnimationUtils
									.loadAnimation(JVIPConnectActivity.this,
											R.anim.rotate_in));
						}
					});
					ipconLinear.startAnimation(ani);
				}
				break;
			case R.id.changetoyst:
				if (!isEnterActivity) {
					Animation ani2 = AnimationUtils.loadAnimation(
							JVIPConnectActivity.this, R.anim.rotate_out);
					ani2.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							ipAddrText
									.setText(getString(R.string.login_str_yst_num));
							portLayout.setVisibility(View.GONE);
							ipAddress.setText(editDevice.deviceNum);
							ipAddress.setEnabled(false);
							ipAddress
									.setBackgroundResource(R.drawable.regist_edittext_gray);
							ipAddress.setHint(null);
							ipconLinear.startAnimation(AnimationUtils
									.loadAnimation(JVIPConnectActivity.this,
											R.anim.rotate_in));
						}
					});
					ipconLinear.startAnimation(ani2);
				}
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editsave: // 保存按钮
			createDialog("");
			if (changeToIP.isChecked()) {
				if ("".equalsIgnoreCase(ipAddress.getText().toString())) {
					showTextToast(R.string.login_str_ip_adress_notnull);
					dismissDialog();
					break;
				}
				if ("".equalsIgnoreCase(portNum.getText().toString())) {
					showTextToast(R.string.login_str_port_notnull);
					dismissDialog();
					break;
				}
				if (!LoginUtil.checkIPAdress(ipAddress.getText().toString())) {
					showTextToast(R.string.login_str_ipadress_format_err);
					dismissDialog();
					break;
				}
				if (!LoginUtil.checkPortNum(portNum.getText().toString())) {
					showTextToast(R.string.login_str_port_format_err);
					dismissDialog();
					break;
				}
			} else {
				if ("".equalsIgnoreCase(ipAddress.getText().toString())) {
					showTextToast(R.string.login_str_device_ytnum_notnull);
					dismissDialog();
					break;
				}
				if (!LoginUtil.checkNickName(ipAddress.getText().toString())) {
					showTextToast(R.string.increct_yst_tips);
					dismissDialog();
					break;
				}
			}
			if ("".equalsIgnoreCase(userName.getText().toString())) {
				showTextToast(R.string.login_str_device_account_notnull);
				dismissDialog();
				break;
			} else if (!LoginUtil.checkDeviceUsername(userName.getText()
					.toString())) {
				showTextToast(R.string.login_str_device_account_error);
				dismissDialog();
				break;
			} else if (!LoginUtil.checkDevicePwd(pwd.getText().toString())) {
				showTextToast(R.string.login_str_device_pass_error);
				dismissDialog();
				break;
			}
			if (Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
				if (changeToIP.isChecked()) {// ip连接
					editDevice.deviceLocalIp = ipAddress.getText().toString();
					editDevice.deviceLocalPort = Integer.valueOf(
							portNum.getText().toString()).intValue();
					editDevice.isDevice = 1;
				} else {// 云视通
					editDevice.deviceLocalIp = "";
					editDevice.deviceLocalPort = 0;
					editDevice.isDevice = 0;
				}
				editDevice.deviceLoginUser = userName.getText().toString();
				editDevice.deviceLoginPwd = pwd.getText().toString();
				// JVConnectInfo jvc = editDevice.toJVConnectInfo();
				// int res = BaseApp.modifyDeviceInfo(jvc);
				// Message msg = handler.obtainMessage();
				// if (res > 0) {
				// msg.what = JVConst.EDIT_DEVICE_SUCCESS;
				// } else {
				// msg.what = JVConst.EDIT_DEVICE_FAILED;
				// }
				// handler.sendMessage(msg);
			} else {
				// if (changeToIP.isChecked()) {
				// editDevice.deviceLocalIp = ipAddress.getText().toString();
				// editDevice.deviceLocalPort = Integer.parseInt(portNum
				// .getText().toString());
				// editDevice.connectType = 2;
				// editDevice.isDevice = 1;
				// } else {
				// editDevice.deviceLocalIp = "";
				// editDevice.deviceLocalPort = 0;
				// editDevice.connectType = 1;
				// editDevice.isDevice = 0;
				// }
				IPConnThread ipconnThread = new IPConnThread();
				ipconnThread.start();
			}
			break;
		case R.id.back:
			JVIPConnectActivity.this.finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 非本地登陆，提交修改信息的线程
	 * 
	 * @author suifupeng
	 * 
	 */
	class IPConnThread extends Thread {
		@Override
		public void run() {
			super.run();
			int flag = 0;
			// if (changeToIP.isChecked()) {
			// flag = DeviceUtil.editDeviceConnType(editDevice,
			// statusHashMap.get(Consts.KEY_USERNAME));
			// } else {
			// flag = DeviceUtil.editDeviceConnType(editDevice,
			// statusHashMap.get(Consts.KEY_PASSWORD));
			// }
			if (0 == flag) {
				if (changeToIP.isChecked()) {
					editDevice.isDevice = 1;
				} else {
					editDevice.isDevice = 0;
				}
				handler.sendMessage(handler
						.obtainMessage(JVConst.EDIT_DEVICE_SUCCESS));
			} else {
				handler.sendMessage(handler
						.obtainMessage(JVConst.EDIT_DEVICE_FAILED));
			}
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

	@Override
	protected void onResume() {
		super.onResume();
	}
}