package com.jovision.activities;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.Device;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.views.AlarmDialog;

public class JVIpconnectActivity extends BaseActivity {
	// ip连接形式的RadioButton
	private RadioButton ipconnnect_ip;
	// 云视通号连接形式的RadioButton
	private RadioButton ipconnnect_cloud;
	// 输入ip地址的布局
	private LinearLayout addressLayout;
	// 输入云视通号的布局
	private LinearLayout couldnumLayout;
	// 选择连接模式的布局
	private LinearLayout tcpLayout;
	// 输入接口的布局
	private LinearLayout portLayout;
	// 输入ip地址的edittext
	private EditText ipconnect_address;
	// 输入端口号的edittext
	private EditText ipconnect_port;
	// 输入用户名的edittext
	private EditText ipconnect_user;
	// 输入密码的edittext
	private EditText ipconnect_pwd;
	// 保存按钮
	private Button editsave;
	// 旋转的外层包裹布局
	private LinearLayout mContainer;
	// 更改形式的标志位
	private boolean isTurn = false;
	// 返回按钮
	private ImageView tcpImageView;

	private boolean isTcpchose;

	private TextView cloud_number;

	private RadioGroup change;

	private int deviceIndex;

	private int isDevice;

	private ArrayList<Device> deviceList = new ArrayList<Device>();

	// private Device device;

	private Device editDevice;

	private String ipString;
	private String portString;
	private String userString;
	private String pwdString;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_PUSH_MESSAGE:
			// 弹出对话框
			new AlarmDialog(this).Show(obj);
			break;
		default:
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// MyActivityManager.getActivityManager().pushAlarmActivity(this);//保存需要弹出报警的Activity
	// getWindow().addFlags(
	// WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
	// WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
	// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	// }
	@Override
	protected void initSettings() {
		deviceIndex = getIntent().getIntExtra("deviceIndex", 0);
		deviceList = CacheUtil.getDevList();
		if (null != deviceList && 0 != deviceList.size()
				&& deviceIndex < deviceList.size()) {
			editDevice = deviceList.get(deviceIndex);
			isDevice = editDevice.getIsDevice();
		}
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.ipconnect_layout);

		if (null == editDevice) {
			finish();
			return;
		}
		change = (RadioGroup) findViewById(R.id.change);
		mContainer = (LinearLayout) findViewById(R.id.mContainer);
		cloud_number = (TextView) findViewById(R.id.cloudnumber_text);
		ipconnnect_ip = (RadioButton) findViewById(R.id.ipconnect_ip);
		ipconnnect_cloud = (RadioButton) findViewById(R.id.ipconnect_cloud);
		addressLayout = (LinearLayout) findViewById(R.id.Addresslayout);
		couldnumLayout = (LinearLayout) findViewById(R.id.NumberLayout);
		tcpLayout = (LinearLayout) findViewById(R.id.tcp_layout);
		tcpImageView = (ImageView) findViewById(R.id.tcp_img);
		portLayout = (LinearLayout) findViewById(R.id.portlayout);
		ipconnect_address = (EditText) findViewById(R.id.ipconnnect_address);
		ipconnect_port = (EditText) findViewById(R.id.ipconnect_port);
		ipconnect_user = (EditText) findViewById(R.id.ipconnect_username);
		ipconnect_pwd = (EditText) findViewById(R.id.ipconnect_pwd);
		editsave = (Button) findViewById(R.id.editsave);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_connect_mode);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);

		if (isDevice == 0) {
			change.check(R.id.ipconnect_cloud);
			isTurn = false;
			addressLayout.setVisibility(View.GONE);
			tcpLayout.setVisibility(View.GONE);
			couldnumLayout.setVisibility(View.VISIBLE);
			portLayout.setVisibility(View.GONE);
			cloud_number.setText(editDevice.getFullNo());
			ipconnect_user.setText(editDevice.getUser());
			ipconnect_pwd.setText(editDevice.getPwd());
		} else {
			change.check(R.id.ipconnect_ip);
			isTurn = true;
			addressLayout.setVisibility(View.VISIBLE);
			tcpLayout.setVisibility(View.VISIBLE);
			couldnumLayout.setVisibility(View.GONE);
			portLayout.setVisibility(View.VISIBLE);
			ipconnect_address.setText(editDevice.getIp());
			if (editDevice.getEnableTcpConnect() == 0) {
				isTcpchose = false;
				tcpImageView.setImageResource(R.drawable.ipc_normal);
			} else {
				isTcpchose = true;
				tcpImageView.setImageResource(R.drawable.ipc_selector);
			}
			if (0 == editDevice.getPort()) {
				ipconnect_port.setText("9101");
			} else {
				ipconnect_port.setText(editDevice.getPort() + "");
			}
			ipconnect_user.setText(editDevice.getUser());
			ipconnect_pwd.setText(editDevice.getPwd());
		}

		tcpLayout.setOnClickListener(myOnClickListener);
		ipconnect_address.setFocusable(true);
		ipconnect_address.setFocusableInTouchMode(true);
		change.setOnCheckedChangeListener(mylistener);
		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn.setOnClickListener(myOnClickListener);
		ipconnnect_ip.setOnClickListener(myOnClickListener);
		ipconnnect_cloud.setOnClickListener(myOnClickListener);
		editsave.setOnClickListener(myOnClickListener);
	}

	OnCheckedChangeListener mylistener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.ipconnect_ip:
				change.check(R.id.ipconnect_ip);
				Animation ani = AnimationUtils.loadAnimation(
						JVIpconnectActivity.this, R.anim.rotate_out);
				ani.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						isTurn = true;
						addressLayout.setVisibility(View.VISIBLE);
						tcpLayout.setVisibility(View.VISIBLE);
						couldnumLayout.setVisibility(View.GONE);
						portLayout.setVisibility(View.VISIBLE);

						ipconnect_address.setText(editDevice.getIp());
						if (0 == editDevice.getPort()) {
							ipconnect_port.setText("9101");
						} else {
							ipconnect_port.setText(editDevice.getPort() + "");
						}
						ipconnect_user.setText(editDevice.getUser());
						ipconnect_pwd.setText(editDevice.getPwd());
						mContainer.startAnimation(AnimationUtils.loadAnimation(
								JVIpconnectActivity.this, R.anim.rotate_in));
					}
				});
				mContainer.startAnimation(ani);
				break;
			case R.id.ipconnect_cloud:
				change.check(R.id.ipconnect_cloud);
				Animation ani2 = AnimationUtils.loadAnimation(
						JVIpconnectActivity.this, R.anim.rotate_out);
				ani2.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						isTurn = false;
						addressLayout.setVisibility(View.GONE);
						tcpLayout.setVisibility(View.GONE);
						couldnumLayout.setVisibility(View.VISIBLE);
						portLayout.setVisibility(View.GONE);

						cloud_number.setText(editDevice.getFullNo());
						ipconnect_user.setText(editDevice.getUser());
						ipconnect_pwd.setText(editDevice.getPwd());

						userString = ipconnect_user.getText().toString();
						pwdString = ipconnect_pwd.getText().toString();

						mContainer.startAnimation(AnimationUtils.loadAnimation(
								JVIpconnectActivity.this, R.anim.rotate_in));
					}
				});
				mContainer.startAnimation(ani2);
				break;
			default:
				break;
			}
		}
	};
	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.editsave:
				if (isTurn) {
					ipString = ipconnect_address.getText().toString();
					portString = ipconnect_port.getText().toString();
					userString = ipconnect_user.getText().toString();
					pwdString = ipconnect_pwd.getText().toString();
					if ("".equalsIgnoreCase(ipString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_ip_adress_notnull);
					} else if (!ConfigUtil.checkIPAdress(ipString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_ipadress_format_err);
					} else if ("".equalsIgnoreCase(portString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_port_notnull);
					} else if (!ConfigUtil.checkPortNum(portString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_port_format_err);
					} else if ("".equalsIgnoreCase(userString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_device_account_notnull);
					} else if (!ConfigUtil.checkDeviceUsername(userString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_device_account_error);
					} else {
						EditDevTask task = new EditDevTask();
						String[] params = new String[3];
						params[0] = "1";
						task.execute(params);

						// if (!Boolean.valueOf(statusHashMap
						// .get(Consts.LOCAL_LOGIN))) {
						// editDevice.setIp(ConfigUtil
						// .getInetAddress(ipString));
						// editDevice.setPort(Integer.valueOf(portString));
						// editDevice.setUser(userString);
						// editDevice.setPwd(pwdString);
						// editDevice.setIsDevice(1);
						// DeviceUtil.editDeviceConnType(editDevice,
						// statusHashMap.get(Consts.KEY_USERNAME));
						// } else {
						// deviceList.get(deviceIndex).setIp(
						// ConfigUtil.getInetAddress(ipString));
						// deviceList.get(deviceIndex).setPort(
						// Integer.valueOf(portString));
						// deviceList.get(deviceIndex).setUser(userString);
						// deviceList.get(deviceIndex).setPwd(pwdString);
						// deviceList.get(deviceIndex).setIsDevice(1);
						// CacheUtil.saveDevList(deviceList);
						// }
					}
				} else {
					userString = ipconnect_user.getText().toString();
					pwdString = ipconnect_pwd.getText().toString();

					if ("".equalsIgnoreCase(userString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_device_account_notnull);
					} else if (!ConfigUtil.checkDeviceUsername(userString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_device_account_error);
					}
					// else if ("".equalsIgnoreCase(pwdString)) {
					// JVIpconnectActivity.this
					// .showTextToast(R.string.login_str_device_pass_notnull);
					// } else if (!ConfigUtil.checkDevicePwd(pwdString)) {
					// JVIpconnectActivity.this
					// .showTextToast(R.string.login_str_device_pass_error);
					// }
					else {

						EditDevTask task = new EditDevTask();
						String[] params = new String[3];
						params[0] = "0";
						task.execute(params);
						// if (!Boolean.valueOf(statusHashMap
						// .get(Consts.LOCAL_LOGIN))) {
						// editDevice.setIp("");
						// editDevice.setPort(0);
						// editDevice.setUser(userString);
						// editDevice.setPwd(pwdString);
						// DeviceUtil.editDeviceConnType(editDevice,
						// statusHashMap.get(Consts.KEY_USERNAME));
						// } else {
						// deviceList.get(deviceIndex).setIp("");
						// deviceList.get(deviceIndex).setPort(0);
						// deviceList.get(deviceIndex).setUser(userString);
						// deviceList.get(deviceIndex).setPwd(pwdString);
						// CacheUtil.saveDevList(deviceList);
						// }
					}
				}

				break;
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_right:

				break;
			case R.id.tcp_layout:
				if (!isTcpchose) {
					tcpImageView.setImageResource(R.drawable.ipc_selector);
					isTcpchose = true;
				} else {
					tcpImageView.setImageResource(R.drawable.ipc_normal);
					isTcpchose = false;
				}
				break;
			default:
				break;
			}
		}
	};

	// 保存更改设备信息线程
	class EditDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int editRes = -1;
			try {
				int isDevice = Integer.parseInt(params[0]);

				if (1 == isDevice) {// IP
					if (isTcpchose) {
						editDevice.setEnableTcpConnect(1);
					} else {
						editDevice.setEnableTcpConnect(0);
					}
					editDevice.setIp(ConfigUtil.getIpAddress(ipString));
					editDevice.setPort(Integer.valueOf(portString));
					editDevice.setUser(userString);
					editDevice.setPwd(pwdString);
					editDevice.setIsDevice(1);// Ip设备
				} else {
					// editDevice.setIp("");
					// editDevice.setPort(0);
					editDevice.setUser(userString);
					editDevice.setPwd(pwdString);
					editDevice.setIsDevice(0);// 云视通
				}
				if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
					editRes = DeviceUtil.editDeviceConnType(editDevice,
							statusHashMap.get(Consts.KEY_USERNAME));
				} else {
					editRes = 0;
				}

				if (0 == editRes) {
					if (1 == isDevice) {// IP
						if (isTcpchose) {
							deviceList.get(deviceIndex)
									.setEnableTcpConnect("1");
						} else {
							deviceList.get(deviceIndex)
									.setEnableTcpConnect("0");
							deviceList.get(deviceIndex).setEnableTcpConnect(1);
						}else {
							deviceList.get(deviceIndex).setEnableTcpConnect(0);
						}
						deviceList.get(deviceIndex).setIp(
								ConfigUtil.getIpAddress(ipString));
						deviceList.get(deviceIndex).setPort(
								Integer.valueOf(portString));
						deviceList.get(deviceIndex).setUser(userString);
						deviceList.get(deviceIndex).setPwd(pwdString);
						deviceList.get(deviceIndex).setIsDevice(1);// Ip设备
					} else {
						deviceList.get(deviceIndex).setIp("");
						deviceList.get(deviceIndex).setPort(0);
						deviceList.get(deviceIndex).setUser(userString);
						deviceList.get(deviceIndex).setPwd(pwdString);
						deviceList.get(deviceIndex).setIsDevice(0);// 云视通
					}

					CacheUtil.saveDevList(deviceList);
				}

				// if (!Boolean.valueOf(statusHashMap
				// .get(Consts.LOCAL_LOGIN))) {
				// editDevice.setIp("");
				// editDevice.setPort(0);
				// editDevice.setUser(userString);
				// editDevice.setPwd(pwdString);
				// DeviceUtil.editDeviceConnType(editDevice,
				// statusHashMap.get(Consts.KEY_USERNAME));
				// } else {
				// deviceList.get(deviceIndex).setIp("");
				// deviceList.get(deviceIndex).setPort(0);
				// deviceList.get(deviceIndex).setUser(userString);
				// deviceList.get(deviceIndex).setPwd(pwdString);
				// CacheUtil.saveDevList(deviceList);
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
			return editRes;
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
				showTextToast(R.string.login_str_device_edit_success);
				JVIpconnectActivity.this.finish();
			} else {
				showTextToast(R.string.login_str_device_edit_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

}