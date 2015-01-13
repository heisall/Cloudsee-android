package com.jovision.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.MyActivityManager;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.UserUtil;

public class JVEditPassActivity extends BaseActivity {
	private Button back;// 左侧返回按钮
	private Button rightButton;
	private TextView currentMenu;// 当前页面名称
	// private EditText userName = null;
	private EditText userOldPass = null;
	private EditText userNewPass = null;
	private EditText userEnsurePass = null;
	private Button finishEdit = null;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.editpass_layout);
		back = (Button) findViewById(R.id.btn_left);
		rightButton = (Button) findViewById(R.id.btn_right);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		// if (!TextUtils.isEmpty(statusHashMap.get(Consts.KEY_USERNAME))) {
		currentMenu.setText(R.string.more_modifypwd);
		// }
		rightButton.setVisibility(View.GONE);
		back.setOnClickListener(onClickListener);
		userOldPass = (EditText) findViewById(R.id.editoldpass);
		userNewPass = (EditText) findViewById(R.id.editnewpass);
		userEnsurePass = (EditText) findViewById(R.id.editcommitpass);
		finishEdit = (Button) findViewById(R.id.editfinish);
		finishEdit.setOnClickListener(onClickListener);

	}

	// onclick事件
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.editfinish:
				if ("".equalsIgnoreCase(userOldPass.getText().toString().trim())) {
					showTextToast(R.string.str_oldpass_notnull);
				} else if (!statusHashMap.get(Consts.KEY_PASSWORD).equals(
						userOldPass.getText().toString().trim())) {
					showTextToast(R.string.str_edit_accoutorpass_error);
					userOldPass.requestFocus();
				} else if ("".equalsIgnoreCase(userNewPass.getText().toString()
						.trim())) {
					showTextToast(R.string.str_newpass_notnull);
				} else if ("".equalsIgnoreCase(userEnsurePass.getText()
						.toString().trim())) {
					showTextToast(R.string.str_ensurenewpass_notnull);
				} else if (userOldPass.getText().toString()
						.equals(userNewPass.getText().toString())) {
					showTextToast(R.string.login_str_password_same);
					userNewPass.requestFocus();
					userNewPass.setSelection(userNewPass.getText().toString()
							.length());
				} else if (!userNewPass.getText().toString()
						.equalsIgnoreCase(userEnsurePass.getText().toString())) {
					showTextToast(R.string.login_str_loginpass_notsame);
				} else if (!AccountUtil.verifyPass(userNewPass.getText()
						.toString())) {
					showTextToast(R.string.login_str_password_tips1);
					userNewPass.requestFocus();
					userNewPass.setSelection(userNewPass.getText().toString()
							.length());
				} else {
					ModifyPwdTask task = new ModifyPwdTask();
					String[] strParams = new String[2];
					strParams[0] = userOldPass.getText().toString();
					strParams[1] = userNewPass.getText().toString();
					task.execute(strParams);
				}
				break;
			}
		}
	};

	// 保存更改设备信息线程
	class ModifyPwdTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int editRes = -1;
			try {
				editRes = DeviceUtil.modifyUserPassword(params[0], params[1]);
				if (0 == editRes) {
					if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
						AccountUtil.userLogout();
						MySharedPreference.putString(Consts.DEVICE_LIST, "");
					}
					ConfigUtil.logOut();
					UserUtil.resetAllUser();
					statusHashMap.put(Consts.HAG_GOT_DEVICE, "false");

					MySharedPreference
							.putString(Consts.KEY_PASSWORD, params[1]);
				}
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
				showTextToast(R.string.edit_succ);
				MyActivityManager.getActivityManager().popAllActivityExceptOne(
						JVLoginActivity.class);
				Intent intent = new Intent(JVEditPassActivity.this,
						JVLoginActivity.class);

				String userName = statusHashMap.get(Consts.KEY_USERNAME);
				// intent.putExtra("UserName", userName);
				statusHashMap.put(Consts.KEY_LAST_LOGIN_USER, userName);
				startActivity(intent);
				JVEditPassActivity.this.finish();
			} else {
				showTextToast(R.string.edit_failed);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
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
