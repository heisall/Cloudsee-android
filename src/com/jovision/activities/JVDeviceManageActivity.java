package com.jovision.activities;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.Device;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.DeviceUtil;

public class JVDeviceManageActivity extends BaseActivity {

	private ArrayList<Device> manageDeviceList = new ArrayList<Device>();

	private EditText manageNick;

	private EditText manageUser;

	private EditText managePassword;

	private ImageView manageNick_Cancle;

	private ImageView manageUser_Cancle;

	private ImageView managePassword_Cancle;

	private TextView manage_save;

	private Boolean localFlag;

	private int deviceIndex;

	private Button btn_left;

	private Button btn_right;

	private TextView currentmenu;

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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		manageDeviceList = CacheUtil.getDevList();
		manageNick.setText(manageDeviceList.get(deviceIndex).getNickName());
		manageUser.setText(manageDeviceList.get(deviceIndex).getUser());
		managePassword.setText(manageDeviceList.get(deviceIndex).getPwd());
	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub
		setContentView(R.layout.devicemanage_layout);

		localFlag = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));
		deviceIndex = getIntent().getIntExtra("deviceIndex", 0);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		currentmenu = (TextView) findViewById(R.id.currentmenu);
		currentmenu.setText(R.string.str_device_manage);
		manageNick = (EditText) findViewById(R.id.manage_nick);
		manageUser = (EditText) findViewById(R.id.manage_user);
		managePassword = (EditText) findViewById(R.id.manage_password);
		manageNick_Cancle = (ImageView) findViewById(R.id.manage_nick_cancle);
		manageUser_Cancle = (ImageView) findViewById(R.id.manage_user_cancle);
		managePassword_Cancle = (ImageView) findViewById(R.id.manage_password_cancle);
		manage_save = (TextView) findViewById(R.id.manage_save);

		btn_left.setOnClickListener(myOnClickListener);
		btn_right.setOnClickListener(myOnClickListener);
		manageNick_Cancle.setOnClickListener(myOnClickListener);
		manage_save.setOnClickListener(myOnClickListener);
		manageUser_Cancle.setOnClickListener(myOnClickListener);
		managePassword_Cancle.setOnClickListener(myOnClickListener);
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
			case R.id.manage_nick_cancle:

				manageNick.setText("");

				break;
			case R.id.manage_user_cancle:

				manageUser.setText("");

				break;
			case R.id.manage_password_cancle:

				managePassword.setText("");

				break;
			case R.id.manage_save:
				ModifyDevTask task = new ModifyDevTask();
				String[] strParams = new String[4];
				strParams[0] = manageDeviceList.get(deviceIndex).getNickName();
				strParams[1] = manageNick.getText().toString();
				strParams[2] = manageUser.getText().toString();
				strParams[3] = managePassword.getText().toString();
				task.execute(strParams);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	// 保存更改设备信息线程
	class ModifyDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int delRes = -1;
			try {
				manageDeviceList.get(deviceIndex).setNickName(params[1]);
				manageDeviceList.get(deviceIndex).setUser(params[2]);
				manageDeviceList.get(deviceIndex).setPwd(params[3]);
				if (localFlag) {// 本地保存修改信息
					delRes = 0;
				} else {
					String name = statusHashMap.get(Consts.KEY_USERNAME);
					delRes = DeviceUtil.modifyDevice(name, params[0],
							params[1], params[2], params[3]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return delRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			if (0 == result) {
				showTextToast(R.string.login_str_device_edit_success);
				CacheUtil.saveDevList(manageDeviceList);
			} else {
				showTextToast(R.string.login_str_device_edit_failed);
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
}
