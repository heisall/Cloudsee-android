package com.jovision.activities;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.jovetech.CloudSee.temp.R;
import com.jovision.bean.Device;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;

public class JVAddIpDeviceActivity extends BaseActivity {

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

	private ArrayList<Device> deviceList = new ArrayList<Device>();

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {
		deviceList = CacheUtil.getDevList();
	}

	@Override
	protected void initUi() {
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
		btn_right.setVisibility(View.GONE);
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
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
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_ip_adress_notnull);
				} else if (!ConfigUtil.checkIPAdress(ipString)) {
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_ipadress_format_err);
				} else if ("".equalsIgnoreCase(portString)) {
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_port_notnull);
				} else if (!ConfigUtil.checkPortNum(portString)) {
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_port_format_err);
				} else if ("".equalsIgnoreCase(userString)) {
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_device_account_notnull);
				} else if (!ConfigUtil.checkDeviceUsername(userString)) {
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_device_account_error);
				} else if ("".equalsIgnoreCase(pwdString)) {
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_device_pass_notnull);
				} else if (!ConfigUtil.checkDevicePwd(pwdString)) {
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_device_pass_error);
				} else {
					createDialog("");
					AddDevTask task = new AddDevTask();
					String[] params = new String[3];
					task.execute(params);
				}
				break;

			default:
				break;
			}
		}
	};
	String ip = "";

	// 设置三种类型参数分别为String,Integer,String
	class AddDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int addRes = -1;// 0:成功，其他失败
			try {
				ip = ConfigUtil.getInetAddress(ipString);
				Device dev = new Device(ip, Integer.valueOf(portString),
						ipString, -1, userString, pwdString, false, 4, 0);
				dev.setIsDevice(2);
				dev.setDoMain(ipString);
				deviceList.add(dev);
				addRes = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}

			CacheUtil.saveDevList(deviceList);
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
				showTextToast(getResources()
						.getString(R.string.add_device_succ) + ip);
				JVAddIpDeviceActivity.this.finish();
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
