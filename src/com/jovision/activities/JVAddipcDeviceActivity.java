package com.jovision.activities;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.jovetech.CloudSee.temp.R;
import com.jovision.utils.ConfigUtil;

public class JVAddipcDeviceActivity extends BaseActivity {

	private EditText ipAddressEdt;
	private EditText portEdt;
	private EditText userNameEdt;
	private EditText passwordEdt;
	private Button saveButton;
	private Button btn_left;
	private Button btn_right;
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
		setContentView(R.layout.addipcdevice_layout);

		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		ipAddressEdt = (EditText) findViewById(R.id.addipconnnect_address);
		portEdt = (EditText) findViewById(R.id.addipconnect_port);
		userNameEdt = (EditText) findViewById(R.id.addipconnect_username);
		passwordEdt = (EditText) findViewById(R.id.addipconnect_pwd);
		saveButton = (Button) findViewById(R.id.addeditsave);

		saveButton.setOnClickListener(myOnClickListener);
		btn_left.setOnClickListener(myOnClickListener);
		btn_right.setOnClickListener(myOnClickListener);
	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_right:

				break;
			case R.id.addeditsave:
				ipString = ipAddressEdt.getText().toString();
				portString = portEdt.getText().toString();
				userString = userNameEdt.getText().toString();
				pwdString = passwordEdt.getText().toString();
				if ("".equalsIgnoreCase(ipString)) {
					JVAddipcDeviceActivity.this
							.showTextToast(R.string.login_str_ip_adress_notnull);
				} else if (!ConfigUtil.checkIPAdress(ipString)) {
					JVAddipcDeviceActivity.this
							.showTextToast(R.string.login_str_ipadress_format_err);
				} else if ("".equalsIgnoreCase(portString)) {
					JVAddipcDeviceActivity.this
							.showTextToast(R.string.login_str_port_notnull);
				} else if (!ConfigUtil.checkPortNum(portString)) {
					JVAddipcDeviceActivity.this
							.showTextToast(R.string.login_str_port_format_err);
				} else if ("".equalsIgnoreCase(userString)) {
					JVAddipcDeviceActivity.this
							.showTextToast(R.string.login_str_device_account_notnull);
				} else if (!ConfigUtil.checkDeviceUsername(userString)) {
					JVAddipcDeviceActivity.this
							.showTextToast(R.string.login_str_device_account_error);
				} else if ("".equalsIgnoreCase(pwdString)) {
					JVAddipcDeviceActivity.this
							.showTextToast(R.string.login_str_device_pass_notnull);
				} else if (!ConfigUtil.checkDevicePwd(pwdString)) {
					JVAddipcDeviceActivity.this
							.showTextToast(R.string.login_str_device_pass_error);
				} else {

				}
				break;

			default:
				break;
			}
		}
	};
}
