package com.jovision.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.test.JVACCOUNT;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;

public class ResetPwdInputAccountActivity extends BaseActivity implements
		OnClickListener {
	private EditText edtAccount;
	private Button nextButton;
	private String strAccount;
	private ProgressDialog pd;
	private TextView tipTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reset_passwd_input_username_first);
		initViews();
	}

	private void initViews() {
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout)findViewById(R.id.alarmnet);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		leftBtn.setOnClickListener(this);
		edtAccount = (EditText) findViewById(R.id.edt_account);
		edtAccount.addTextChangedListener(new EditChangedListener());
		nextButton = (Button) findViewById(R.id.btn_next);
		nextButton.setOnClickListener(this);
		currentMenu.setText(R.string.reset_passwd_title1);
		tipTv = (TextView) findViewById(R.id.tv_check_user_name_tips);
		tipTv.setVisibility(View.INVISIBLE);

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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_next:
			strAccount = edtAccount.getText().toString().trim();
			if (strAccount == null || strAccount.equals("")) {
				showTextToast(R.string.reset_passwd_tips1);
				break;
			} else {
				// if (!ConfigUtil.checkDeviceUsername(strAccount)) {
				// showTextToast(R.string.reset_passwd_tips2);
				// break;
				// }
				// 调用接口
				CheckUserInfoTask task = new CheckUserInfoTask();
				task.execute(strAccount);
			}
			break;
		case R.id.btn_left:
			finish();
			break;
		default:
			break;
		}
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

	class CheckUserInfoTask extends AsyncTask<String, Integer, Integer> {
		String account = "";
		byte[] response = null;

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			account = params[0];
			response = new byte[1024];
			int ret = 0x100;
			ret = JVACCOUNT.GetMailPhoneNoSession(account, response);
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
			if (result == 0)// ok
			{
				String strPhone, strMail;

				try {
					JSONObject resObject = new JSONObject(new String(response));
					strPhone = resObject.optString("phone");
					strMail = resObject.optString("mail");
					Log.i("TAG", "获取到的手机号" + strPhone);
					if (strPhone.equals("") || null == strPhone) {
						// 走之前的web找回密码
						if (strMail.equals("") || null == strMail) {
							// showTextToast(R.string.str_not_bind_phone_tips2);
							tipTv.setText(R.string.str_not_bind_phone_tips2);
							tipTv.setVisibility(View.VISIBLE);
						} else {
							// showTextToast(R.string.str_not_bind_phone_tips1);
							tipTv.setText(R.string.str_not_bind_phone_tips1);
							tipTv.setVisibility(View.VISIBLE);
						}
					} else {
						// 跳转到验证码界面
						Intent intent = new Intent(
								ResetPwdInputAccountActivity.this,
								ResetPwdIdentifyNumActivity.class);
						intent.putExtra("phone", strPhone);
						intent.putExtra("account", strAccount);
						startActivity(intent);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// 重置失败
				if (result == 6) {
					// 账号不存在
					// showTextToast(R.string.str_not_bind_phone_tips3);
					tipTv.setText(R.string.str_not_bind_phone_tips3);
					tipTv.setVisibility(View.VISIBLE);
				} else if (result == -6) {
					showTextToast(R.string.str_query_account_failed1);
				} else {
					showTextToast(R.string.str_query_account_failed);
					// tipTv.setText(R.string.str_query_account_failed);
				}
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

	class EditChangedListener implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			tipTv.setVisibility(View.INVISIBLE);
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};
}
