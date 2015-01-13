package com.jovision.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.Device;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.views.AlarmDialog;

public class JVDeviceManageActivity extends BaseActivity {

	private ArrayList<Device> manageDeviceList = new ArrayList<Device>();

	private EditText manageNick;

	private EditText manageUser;

	private EditText managePassword;

	private ImageView manageNick_Cancle;

	private ImageView manageUser_Cancle;

	private ImageView managePassword_Cancle;

	private TextView manage_save;

	private TextView manage_delect;

	private Boolean localFlag;

	private int deviceIndex;

	private TextView cloudnumber_text;

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

	@Override
	protected void initSettings() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		manageDeviceList = CacheUtil.getDevList();
		if (null != manageDeviceList && 0 != manageDeviceList.size()) {
			manageNick.setText(manageDeviceList.get(deviceIndex).getNickName());
			manageUser.setText(manageDeviceList.get(deviceIndex).getUser());
			managePassword.setText(manageDeviceList.get(deviceIndex).getPwd());
			cloudnumber_text.setText(manageDeviceList.get(deviceIndex)
					.getFullNo());
		}
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.devicemanage_layout);

		localFlag = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));
		deviceIndex = getIntent().getIntExtra("deviceIndex", 0);

		cloudnumber_text = (TextView) findViewById(R.id.cloudnumber_text);
		if (Consts.LANGUAGE_ZH == ConfigUtil
				.getLanguage2(JVDeviceManageActivity.this)
				|| Consts.LANGUAGE_ZHTW == ConfigUtil
						.getLanguage2(JVDeviceManageActivity.this)) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(20, 0, 0, 0);
			cloudnumber_text.setPadding(10, 13, 0, 13);
			cloudnumber_text.setLayoutParams(lp);
		} else {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(8, 0, 0, 0);
			cloudnumber_text.setPadding(10, 10, 0, 10);
			cloudnumber_text.setLayoutParams(lp);
		}
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		rightBtn = (Button) findViewById(R.id.btn_right);
		currentMenu = (TextView) findViewById(R.id.currentmenu);

		currentMenu.setText(R.string.str_device_manage);
		manageNick = (EditText) findViewById(R.id.manage_nick);
		manageUser = (EditText) findViewById(R.id.manage_user);
		managePassword = (EditText) findViewById(R.id.manage_password);
		manageNick_Cancle = (ImageView) findViewById(R.id.manage_nick_cancle);
		manageUser_Cancle = (ImageView) findViewById(R.id.manage_user_cancle);
		managePassword_Cancle = (ImageView) findViewById(R.id.manage_password_cancle);
		manage_save = (TextView) findViewById(R.id.manage_save);
		manage_delect = (TextView) findViewById(R.id.manage_delect);

		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn.setOnClickListener(myOnClickListener);
		manageNick_Cancle.setOnClickListener(myOnClickListener);
		manage_save.setOnClickListener(myOnClickListener);
		manage_delect.setOnClickListener(myOnClickListener);
		manageUser_Cancle.setOnClickListener(myOnClickListener);
		managePassword_Cancle.setOnClickListener(myOnClickListener);
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				JVDeviceManageActivity.this.finish();
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
				// 设备昵称不为空
				if ("".equalsIgnoreCase(manageNick.getText().toString())) {
					JVDeviceManageActivity.this
							.showTextToast(R.string.str_nikename_notnull);
				}
				// 设备昵称验证
				else if (!ConfigUtil.checkNickName(manageNick.getText()
						.toString())) {
					JVDeviceManageActivity.this
							.showTextToast(R.string.login_str_nike_name_order);
				}
				// 设备用户名不为空
				else if ("".equalsIgnoreCase(manageUser.getText().toString())) {
					JVDeviceManageActivity.this
							.showTextToast(R.string.login_str_device_account_notnull);
				}
				// 设备用户名验证
				else if (!ConfigUtil.checkDeviceUsername(manageUser.getText()
						.toString())) {
					JVDeviceManageActivity.this
							.showTextToast(R.string.login_str_device_account_error);
				} else if (!ConfigUtil.checkDevicePwd(managePassword.getText()
						.toString())) {
					JVDeviceManageActivity.this
							.showTextToast(R.string.login_str_device_pass_error);
				} else {
					ModifyDevTask task = new ModifyDevTask();
					String[] strParams = new String[4];
					strParams[0] = manageDeviceList.get(deviceIndex)
							.getFullNo();
					strParams[1] = manageNick.getText().toString();
					strParams[2] = manageUser.getText().toString();
					strParams[3] = managePassword.getText().toString();
					task.execute(strParams);
				}
				break;
			case R.id.manage_delect:
				dialog(deviceIndex);
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
				finish();
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

	// 删除设备线程
	class DelDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int delRes = -1;
			boolean localFlag = Boolean
					.valueOf(JVDeviceManageActivity.this.statusHashMap
							.get(Consts.LOCAL_LOGIN));
			try {
				int delIndex = Integer.parseInt(params[0]);
				if (localFlag) {// 本地删除
					delRes = 0;
				} else {
					delRes = DeviceUtil.unbindDevice(
							(JVDeviceManageActivity.this.statusHashMap
									.get("KEY_USERNAME")), manageDeviceList
									.get(delIndex).getFullNo());
				}

				if (0 == delRes) {
					ConfigUtil.deleteSceneFolder(manageDeviceList.get(delIndex)
							.getFullNo());
					manageDeviceList.remove(delIndex);
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
			dismissDialog();
			if (0 == result) {
				JVDeviceManageActivity.this
						.showTextToast(R.string.del_device_succ);
				CacheUtil.saveDevList(manageDeviceList);
				finish();
			} else {
				JVDeviceManageActivity.this
						.showTextToast(R.string.del_device_failed);
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

	protected void dialog(final int position) {
		String okString = JVDeviceManageActivity.this.getResources().getString(
				R.string.ok);
		String delectString = JVDeviceManageActivity.this.getResources()
				.getString(R.string.str_delete_sure);
		String warmString = JVDeviceManageActivity.this.getResources()
				.getString(R.string.str_delete_tip);
		String cancleString = JVDeviceManageActivity.this.getResources()
				.getString(R.string.str_crash_cancel);
		AlertDialog.Builder builder = new Builder(JVDeviceManageActivity.this);
		builder.setMessage(delectString);
		builder.setTitle(warmString);
		builder.setPositiveButton(okString,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						DelDevTask task = new DelDevTask();
						String[] strParams = new String[1];
						strParams[0] = deviceIndex + "";
						task.execute(strParams);
					}
				});
		builder.setNegativeButton(cancleString,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}
