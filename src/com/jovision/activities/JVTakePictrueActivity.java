package com.jovision.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.MyLog;
import com.jovision.commons.Url;
import com.jovision.utils.ConfigUtil;

public class JVTakePictrueActivity extends Activity implements OnClickListener {
	private RelativeLayout rlyMailWay, rlyPhoneWay, rlyCance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.take_pictrue_choice);

		rlyMailWay = (RelativeLayout) findViewById(R.id.rly_mail_verify);
		rlyPhoneWay = (RelativeLayout) findViewById(R.id.rly_phone_verify);
		rlyCance = (RelativeLayout) findViewById(R.id.rly_cancel);

		rlyMailWay.setOnClickListener(this);
		rlyPhoneWay.setOnClickListener(this);
		rlyCance.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rly_mail_verify:// 邮箱
			Intent intentFP = new Intent(JVTakePictrueActivity.this,
					JVWebViewActivity.class);
			String findUrl = "";
			if (Consts.LANGUAGE_ZH == ConfigUtil
					.getLanguage2(JVTakePictrueActivity.this)) {// 中文简体
				findUrl = Url.RESET_PWD_URL_ZH;
			} else if (Consts.LANGUAGE_ZHTW == ConfigUtil
					.getLanguage2(JVTakePictrueActivity.this)) {// 中文繁体
				findUrl = Url.RESET_PWD_URL_ZHT;
			} else {// 英文
				findUrl = Url.RESET_PWD_URL_EN;
			}
			MyLog.e("findUrl", findUrl);
			intentFP.putExtra("URL", findUrl);
			intentFP.putExtra("title", R.string.str_find_pass);
			JVTakePictrueActivity.this.startActivity(intentFP);
			break;
		case R.id.rly_phone_verify:// 手机
			// 跳转到验证码界面
			Intent intent = new Intent(JVTakePictrueActivity.this,
					ResetPwdInputAccountActivity.class);
			startActivity(intent);
			break;
		case R.id.rly_cancel:
			finish();
			return;
		default:
			break;
		}
		finish();
	}
}
