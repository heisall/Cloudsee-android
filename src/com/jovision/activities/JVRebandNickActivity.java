package com.jovision.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.test.JVACCOUNT;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.commons.MySharedPreference;

public class JVRebandNickActivity extends BaseActivity{

	private Button rebandNickSaveButton;
	private EditText inputNickname;
	
	private String username;
	private String email;
	private String phone;
	
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
		
		username = getIntent().getStringExtra("username");
		phone = getIntent().getStringExtra("phone");
		email = getIntent().getStringExtra("email");
		
	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub
		setContentView(R.layout.reband_nick_layout);

		leftBtn = (Button)findViewById(R.id.btn_left);
		rightBtn = (Button)findViewById(R.id.btn_right);
		currentMenu = (TextView)findViewById(R.id.currentmenu);
		rebandNickSaveButton = (Button)findViewById(R.id.reband_nick_save);
		inputNickname = (EditText)findViewById(R.id.inputnickname);
		
		currentMenu.setText(getResources().getString(R.string.rebindcontactnickname));
		rightBtn.setVisibility(View.GONE);
		rebandNickSaveButton.setOnClickListener(myOnClickListener);
		leftBtn.setOnClickListener(myOnClickListener);

	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.reband_nick_save:
				if ("".equals(inputNickname.getText().toString())) {
					showTextToast(R.string.str_nikename_notnull);
				} else {
					JSONObject resObject = new JSONObject();
					try {
						resObject.put("user", username);
						resObject.put("phone", phone);
						resObject.put("mail", email);
						resObject.put("nick", inputNickname.getText()
								.toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SetAccountInfoTask task = new SetAccountInfoTask();
					String params[] = new String[3];
					params[0] = resObject.toString();
					task.execute(params);
					Log.i("TAG", params[0]);
				}
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
	class SetAccountInfoTask extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			createDialog("", false);
			int ret = -1;
			ret = JVACCOUNT.SetAccountInfo(params[0]);
			return ret;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 0)// ok
			{
				MySharedPreference.putString("NICKNAMEBBS", inputNickname.getText().toString());
				dismissDialog();
				finish();
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
		}
	}
}
