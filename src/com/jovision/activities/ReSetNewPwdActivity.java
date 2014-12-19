package com.jovision.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.test.JVACCOUNT;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.utils.AccountUtil;

public class ReSetNewPwdActivity extends BaseActivity implements
		OnClickListener {

	private Button backBtn;
	private Button rightBtn;
	public TextView titleTv;
	private EditText edtIdentifyNum;
	private Button btnSubmit;

	private EditText edtNewPwd, edtConfirmPwd;
	private TextView tvAccount;
	private String account, newPwd, confirmPwd;
	private ProgressDialog pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reset_passwd_enter_new_passwd);
		Bundle extras = getIntent().getExtras();
		if (null == extras) {
			finish();
			return;
		}
		account = extras.getString("account");
		initViews();
	}

	private void initViews() {
		backBtn = (Button) findViewById(R.id.btn_left);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		titleTv = (TextView) findViewById(R.id.currentmenu);
		backBtn.setOnClickListener(this);

		btnSubmit = (Button) findViewById(R.id.btn_submit);
		btnSubmit.setOnClickListener(this);
		edtNewPwd = (EditText) findViewById(R.id.edt_new_pwd);
		edtConfirmPwd = (EditText) findViewById(R.id.edt_confirm_pwd);
		tvAccount = (TextView) findViewById(R.id.tv_account);
		tvAccount.setText(account);
		titleTv.setText(getResources().getString(R.string.str_set_new_pwd));

		pd = new ProgressDialog(this);
		pd.setCancelable(true);
		pd.setMessage(getResources().getString(R.string.reset_passwd_tips3));
	}

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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_submit:
			newPwd = edtNewPwd.getText().toString().trim();
			confirmPwd = edtConfirmPwd.getText().toString().trim();

			if ("".equalsIgnoreCase(newPwd)) {
				showTextToast(R.string.login_str_loginpass1_notnull);
			} else if ("".equalsIgnoreCase(confirmPwd)) {
				showTextToast(R.string.login_str_loginpass2_notnull);
			} else if (!newPwd.equals(confirmPwd)) {
				showTextToast(R.string.login_str_loginpass_notsame);
				edtNewPwd.setText("");
				edtConfirmPwd.setText("");
			} else if (!AccountUtil.verifyPass(newPwd)) {
				showTextToast(R.string.login_str_password_tips1);
			} else {
				// 调用重置接口
				SetNewpwdTask setNewpwdTask = new SetNewpwdTask();
				String[] param = new String[4];
				param[0] = account;
				param[1] = newPwd;
				setNewpwdTask.execute(param);
			}
			break;

		default:
			break;
		}
	}

	class SetNewpwdTask extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			int ret = -1;
			ret = JVACCOUNT.ResetUserPasswordNoSession(newPwd, account);
			return ret;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			if (result == 0)// ok,直接登录
			{
				showTextToast(R.string.str_set_new_pwd_ok);
				//为了让用户加强印象，不直接登录，跳转到登录界面
				Intent intent = new Intent();
				intent.setClass(ReSetNewPwdActivity.this,
						JVLoginActivity.class);
				intent.putExtra("AutoLogin", false);
				intent.putExtra("UserName", account);
				intent.putExtra("UserPass", "");		
				startActivity(intent);
				exit();				
			} else {
				// 重置失败
				showTextToast(R.string.reset_passwd_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			pd.show();
		}
	}
}
