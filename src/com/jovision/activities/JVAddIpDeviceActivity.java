package com.jovision.activities;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.Device;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.PlayUtil;

public class JVAddIpDeviceActivity extends BaseActivity {

	/** topBar */
	private EditText ipAddressEdt;
	private EditText portEdt;
	private EditText nickNameEdt;
	private EditText userNameEdt;
	private EditText passwordEdt;
	private Button saveButton;
	private String ipString;
	private String nickString;
	private String portString;
	private String userString;
	private String pwdString;
	private boolean hasBroadIP = false;// 是否广播完IP
	private String resolvedIp = "";// 解析出来的IP
	private int broadChannelCount = -1;// 广播到的通道数量

	private ArrayList<Device> deviceList = new ArrayList<Device>();

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		// 广播回调
		case Consts.CALL_LAN_SEARCH: {
			MyLog.v("广播回调", "onTabAction2:what=" + what + ";arg1=" + arg1
					+ ";arg2=" + arg1 + ";obj=" + obj.toString());
			JSONObject broadObj;
			try {
				broadObj = new JSONObject(obj.toString());
				if (0 == broadObj.optInt("timeout")) {
					String broadIp = broadObj.optString("ip");
					if (resolvedIp.equalsIgnoreCase(broadIp)) {// 同一个设备
						broadChannelCount = broadObj.optInt("count");
					}

				} else if (1 == broadObj.optInt("timeout")) {
					hasBroadIP = true;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

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
		deviceList = CacheUtil.getDevList();
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.addipcdevice_layout);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		currentMenu.setText(R.string.str_help1_1);
		rightBtn = (Button) findViewById(R.id.btn_right);
		ipAddressEdt = (EditText) findViewById(R.id.addipconnnect_address);
		portEdt = (EditText) findViewById(R.id.addipconnect_port);
		nickNameEdt = (EditText) findViewById(R.id.addipconnect_nickname);
		userNameEdt = (EditText) findViewById(R.id.addipconnect_username);
		passwordEdt = (EditText) findViewById(R.id.addipconnect_pwd);
		saveButton = (Button) findViewById(R.id.addeditsave);

		portEdt.setText(Consts.DEFAULT_PORT);
		userNameEdt.setText(Consts.DEFAULT_USERNAME);
		passwordEdt.setText(Consts.DEFAULT_PASSWORD);
		saveButton.setOnClickListener(myOnClickListener);
		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn.setOnClickListener(myOnClickListener);
		rightBtn.setVisibility(View.GONE);
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
				nickString = nickNameEdt.getText().toString();
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
				} else if (!"".equals(nickString)
						&& !ConfigUtil.checkNickName(nickString)) {
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_nike_name_order);
				} else if ("".equalsIgnoreCase(userString)) {
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_device_account_notnull);
				} else if (!ConfigUtil.checkDeviceUsername(userString)) {
					JVAddIpDeviceActivity.this
							.showTextToast(R.string.login_str_device_account_error);
				} else if (hasDev(ipString)) {// 已经添加过该设备
					ipAddressEdt.setText("");
					showTextToast(R.string.str_device_exsit);
				} else {
					createDialog("", true);
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

	/**
	 * 判断设备是否在设备列表里
	 * 
	 * @param devNum
	 * @return
	 */
	public boolean hasDev(String devDomain) {
		boolean has = false;
		for (Device dev : deviceList) {
			if (devDomain.equalsIgnoreCase(dev.getDoMain())) {
				has = true;
				break;
			}
		}
		return has;
	}

	// 设置三种类型参数分别为String,Integer,String
	class AddDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int addRes = -1;// 0:成功，其他失败
			try {
				hasBroadIP = false;
				resolvedIp = ConfigUtil.getIpAddress(ipString);

				// 非3G广播获取通道数量
				if (PlayUtil.broadCast(JVAddIpDeviceActivity.this)) {
					int errorCount = 0;
					while (!hasBroadIP) {
						Thread.sleep(1000);
						errorCount++;
						if (errorCount >= 10) {
							break;
						}
					}
				}
				int count = broadChannelCount > 0 ? broadChannelCount : 4;
				Device dev = new Device(resolvedIp,
						Integer.valueOf(portString), ipString, -1, userString,
						pwdString, false, count, 0);
				dev.setIsDevice(2);
				dev.setDoMain(ipString);
				if (!"".equals(nickString)) {
					dev.setNickName(nickString);
				}
				deviceList.add(0, dev);
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
				// showTextToast(getResources()
				// .getString(R.string.add_device_succ)
				// + resolvedIp
				// + ";count:" + broadChannelCount);
				JVAddIpDeviceActivity.this.finish();
			} else {
				showTextToast(R.string.add_device_failed);
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
}
