package com.jovision.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.User;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.UserUtil;

public class JVBoundEmailActivity extends BaseActivity {
	/** topBar */
	private Button back;
	private Button skip;
	private Button finish;

	private EditText userEmailEditText;
	private TextView currentMenu;

	String userName;
	String userPass;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		Intent intent = new Intent();
		switch (what) {
		case JVAccountConst.REGIST_SUCCESS_LOGIN_SUCCESS:// 注册成功登陆成功
			intent.setClass(JVBoundEmailActivity.this, JVTabActivity.class);
			JVBoundEmailActivity.this.startActivity(intent);
			JVBoundEmailActivity.this.finish();
			break;
		case JVAccountConst.BOUND_EMAIL_FAILED:// 绑定邮箱失败
			showTextToast(R.string.str_bound_email_failed);
			break;
		case JVAccountConst.BOUND_EMAIL_EXIST:// 邮箱已被绑定，绑定失败
			showTextToast(R.string.str_bound_email_exist);
			break;
		default:
			break;
		}
		dismissDialog();
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
		userName = getIntent().getStringExtra("UserName");
		userPass = getIntent().getStringExtra("UserPass");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		setContentView(R.layout.bound_email_layout);
		userEmailEditText = (EditText) findViewById(R.id.registemail);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		back = (Button) findViewById(R.id.btn_left);
		back.setVisibility(View.GONE);
		skip = (Button) findViewById(R.id.btn_right);
		currentMenu.setVisibility(View.VISIBLE);
		skip.setVisibility(View.VISIBLE);
		skip.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.setting_save));
		skip.setText(getResources().getString(R.string.str_skip));
		skip.setTextColor(Color.WHITE);
		currentMenu.setText(getResources().getString(R.string.str_bound_email));

		finish = (Button) findViewById(R.id.finish);
		skip.setOnClickListener(onClickListener);
		finish.setOnClickListener(onClickListener);

	}

	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_right:
				Intent intent = new Intent();
				MySharedPreference.putString("UserName", userName);
				MySharedPreference.putString("PassWord", userPass);

				User user = new User();
				user.setPrimaryID(System.currentTimeMillis());
				user.setUserName(statusHashMap.get(Consts.KEY_USERNAME));
				user.setUserPwd(statusHashMap.get(Consts.KEY_PASSWORD));
				user.setLastLogin(1);
				user.setJudgeFlag(1);
				UserUtil.addUser(user);
				intent.setClass(JVBoundEmailActivity.this, JVTabActivity.class);
				JVBoundEmailActivity.this.startActivity(intent);
				finish();

				// Intent intent = new Intent();
				// intent.setClass(JVBoundEmailActivity.this,
				// JVLoginActivity.class);
				// intent.putExtra("AutoLogin", true);
				// intent.putExtra("UserName", userName);
				// intent.putExtra("UserPass", userPass);
				// JVBoundEmailActivity.this.startActivity(intent);
				// JVBoundEmailActivity.this.finish();
				break;
			case R.id.finish:
				if ("".equalsIgnoreCase(userEmailEditText.getText().toString())) {
					showTextToast(R.string.login_str_loginemail_notnull);
				} else if (!AccountUtil.verifyEmail(userEmailEditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_loginemail_tips);
				} else {
					createDialog("");
					BindTask task = new BindTask();
					String[] params = new String[3];
					task.execute(params);
				}
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

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	// 设置三种类型参数分别为String,Integer,String
	class BindTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int bindRes = -1;// 0成功 2邮箱已被绑定，绑定失败 其他失败
			try {
				bindRes = AccountUtil.bindMailOrPhone(userEmailEditText
						.getText().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			MyLog.v("TAG", "bindRes=" + bindRes + ";email="
					+ userEmailEditText.getText().toString());
			return bindRes;
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
				Intent intent = new Intent();
				MySharedPreference.putString("UserName", userName);
				MySharedPreference.putString("PassWord", userPass);

				User user = new User();
				user.setPrimaryID(System.currentTimeMillis());
				user.setUserName(statusHashMap.get(Consts.KEY_USERNAME));
				user.setUserPwd(statusHashMap.get(Consts.KEY_PASSWORD));
				user.setLastLogin(1);
				user.setJudgeFlag(1);
				UserUtil.addUser(user);
				intent.setClass(JVBoundEmailActivity.this, JVTabActivity.class);
				JVBoundEmailActivity.this.startActivity(intent);
				finish();

				// Intent intent = new Intent();
				// intent.setClass(JVBoundEmailActivity.this,
				// JVLoginActivity.class);
				// intent.putExtra("AutoLogin", true);
				// intent.putExtra("UserName", userName);
				// intent.putExtra("UserPass", userPass);
				// JVBoundEmailActivity.this.startActivity(intent);
				// JVBoundEmailActivity.this.finish();// 注册成功登陆成功
			} else if (2 == result) {
				showTextToast(R.string.str_bound_email_exist);
			} else {
				showTextToast(R.string.str_bound_email_failed);
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
