package com.jovision.activities;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.util.Log;
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
import com.jovision.bean.Device;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.DeviceUtil;


public class JVIpconnectActivity extends BaseActivity {
	// ip连接形式的RadioButton
	private RadioButton ipconnnect_ip;
	// 云视通号连接形式的RadioButton
	private RadioButton ipconnnect_cloud;
	// 输入ip地址的布局
	private LinearLayout addressLayout;
	// 输入云视通号的布局
	private LinearLayout couldnumLayout;
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
	private boolean isturn = false;
	// 返回按钮
	private Button back;
	// 添加按钮
	private Button plus_btu;

	private TextView cloud_number;

	private RadioGroup change;

	private int deviceIndex;

	private int isDevice;

	private ArrayList<Device> deviceList = new ArrayList<Device>();

	private Device device;

	private Device deviceedit;

	private String ipString;
	private String portString;
	private String userString;
	private String pwdString;

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub
		setContentView(R.layout.ipconnect_layout);

		deviceIndex = getIntent().getIntExtra("deviceIndex", 0);
		isDevice = getIntent().getIntExtra("isDevice", 0);
		deviceList = CacheUtil.getDevList();
		device = deviceList.get(deviceIndex);
		deviceedit = new Device();
		change = (RadioGroup) findViewById(R.id.change);
		mContainer = (LinearLayout) findViewById(R.id.mContainer);
		cloud_number = (TextView) findViewById(R.id.cloudnumber_text);
		ipconnnect_ip = (RadioButton) findViewById(R.id.ipconnect_ip);
		ipconnnect_cloud = (RadioButton) findViewById(R.id.ipconnect_cloud);
		addressLayout = (LinearLayout) findViewById(R.id.Addresslayout);
		couldnumLayout = (LinearLayout) findViewById(R.id.NumberLayout);
		portLayout = (LinearLayout) findViewById(R.id.portlayout);
		ipconnect_address = (EditText) findViewById(R.id.ipconnnect_address);
		ipconnect_port = (EditText) findViewById(R.id.ipconnect_port);
		ipconnect_user = (EditText) findViewById(R.id.ipconnect_username);
		ipconnect_pwd = (EditText) findViewById(R.id.ipconnect_pwd);
		editsave = (Button) findViewById(R.id.editsave);
		back = (Button) findViewById(R.id.btn_left);
		plus_btu = (Button) findViewById(R.id.btn_right);

		if (isDevice == 0) {
			change.check(R.id.ipconnect_cloud);
			isturn = false;
			addressLayout.setVisibility(View.GONE);
			couldnumLayout.setVisibility(View.VISIBLE);
			portLayout.setVisibility(View.GONE);
		} else {
			change.check(R.id.ipconnect_ip);
			isturn = true;
			addressLayout.setVisibility(View.VISIBLE);
			couldnumLayout.setVisibility(View.GONE);
			portLayout.setVisibility(View.VISIBLE);
		}

		ipconnect_address.setText(device.getIp());
		ipconnect_port.setText(device.getPort() + "");
		ipconnect_user.setText(device.getUser());
		ipconnect_pwd.setText(device.getPwd());

		ipconnect_address.setFocusable(true);
		ipconnect_address.setFocusableInTouchMode(true);
		change.setOnCheckedChangeListener(mylistener);
		back.setOnClickListener(myOnClickListener);
		plus_btu.setOnClickListener(myOnClickListener);
		ipconnnect_ip.setOnClickListener(myOnClickListener);
		ipconnnect_cloud.setOnClickListener(myOnClickListener);
		editsave.setOnClickListener(myOnClickListener);
	}

	OnCheckedChangeListener mylistener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
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
						isturn = true;
						addressLayout.setVisibility(View.VISIBLE);
						couldnumLayout.setVisibility(View.GONE);
						portLayout.setVisibility(View.VISIBLE);

						ipconnect_address.setText(device.getIp());
						ipconnect_port.setText(device.getPort() + "");
						ipconnect_user.setText(device.getUser());
						ipconnect_pwd.setText(device.getPwd());
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
						isturn = false;
						addressLayout.setVisibility(View.GONE);
						couldnumLayout.setVisibility(View.VISIBLE);
						portLayout.setVisibility(View.GONE);

						cloud_number.setText(device.getFullNo());
						ipconnect_user.setText(device.getUser());
						ipconnect_pwd.setText(device.getPwd());

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
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.editsave:
				if (isturn) {
					ipString = ipconnect_address.getText().toString();
					portString = ipconnect_port.getText().toString();
					userString = ipconnect_user.getText().toString();
					pwdString = ipconnect_pwd.getText().toString();
					if ("".equals(ipString)) {
						Log.i("TAG", R.string.login_str_ip_adress_notnull + "f");
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_ip_adress_notnull);
					} else if ("".equals(portString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_port_notnull);
					} else if ("".equals(userString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_device_account_notnull);
					} else if ("".equals(pwdString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_device_pass_notnull);
					} else {
						if (!JVMyDeviceFragment.localFlag) {
							deviceedit.setIp(getInetAddress(ipString));
							deviceedit.setPort(Integer.valueOf(portString));
							deviceedit.setUser(userString);
							deviceedit.setPwd(pwdString);
							if (device.getIsDevice() == 0) {
								deviceedit.setIsDevice(1);
							}
							DeviceUtil.editDeviceConnType(deviceedit,
									JVMyDeviceFragment.devicename);
						} else {
							deviceList.get(deviceIndex).setIp(
									getInetAddress(ipString));
							deviceList.get(deviceIndex).setPort(
									Integer.valueOf(portString));
							deviceList.get(deviceIndex).setUser(userString);
							deviceList.get(deviceIndex).setPwd(pwdString);
							deviceList.get(deviceIndex).setIsDevice(1);
							CacheUtil.saveDevList(deviceList);
						}
					}
				} else {
					userString = ipconnect_user.getText().toString();
					pwdString = ipconnect_pwd.getText().toString();

					if ("".equals(userString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_device_account_notnull);
					} else if ("".equals(pwdString)) {
						JVIpconnectActivity.this
								.showTextToast(R.string.login_str_device_pass_notnull);
					} else {
						if (!JVMyDeviceFragment.localFlag) {
							deviceedit.setIp("");
							deviceedit.setPort(0);
							deviceedit.setUser(userString);
							deviceedit.setPwd(pwdString);
							DeviceUtil.editDeviceConnType(deviceedit,
									JVMyDeviceFragment.devicename);
						} else {
							deviceList.get(deviceIndex).setIp("");
							deviceList.get(deviceIndex).setPort(0);
							deviceList.get(deviceIndex).setUser(userString);
							deviceList.get(deviceIndex).setPwd(pwdString);
							CacheUtil.saveDevList(deviceList);
						}
					}
				}

				break;
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_right:

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
	protected void freeMe() {

	}

	public static String getInetAddress(String host) {
		String IPAddress = "";
		InetAddress ReturnStr1 = null;
		try {
			ReturnStr1 = java.net.InetAddress.getByName(host);
			IPAddress = ReturnStr1.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return IPAddress;
		}
		return IPAddress;
	}

	// 动画旋转显示或隐藏的布局
	private final class SwapViews implements Runnable {
		public SwapViews(int position) {
		}

		public void run() {
			final float centerX = mContainer.getWidth() / 2.0f;
			final float centerY = mContainer.getHeight() / 2.0f;
			Rotate3dUtil rotation;
			rotation = new Rotate3dUtil(270, 360, centerX, centerY, 310.0f,
					false);
			rotation.setDuration(200);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());
			mContainer.startAnimation(rotation);
			if (!isturn) {
				addressLayout.setVisibility(View.VISIBLE);
				couldnumLayout.setVisibility(View.GONE);
				portLayout.setVisibility(View.VISIBLE);
			} else {
				addressLayout.setVisibility(View.GONE);
				couldnumLayout.setVisibility(View.VISIBLE);
				portLayout.setVisibility(View.GONE);
			}
		}
	}
}