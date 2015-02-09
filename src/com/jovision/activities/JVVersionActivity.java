package com.jovision.activities;

import java.util.ArrayList;

import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.test.JVACCOUNT;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.utils.ConfigUtil;

public class JVVersionActivity extends BaseActivity {

	private TextView configInfo;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.version_layout);
		leftBtn = (Button) findViewById(R.id.btn_left);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.device_version_info);
		configInfo = (TextView) findViewById(R.id.configinfo);
		leftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				JVVersionActivity.this.finish();
			}
		});

		ArrayList<String> configList = new ArrayList<String>();
		try {
			configList.add("Version" + "="
					+ ConfigUtil.getVersion(JVVersionActivity.this) + "\n");

			configList.add("AppName=" + Consts.APP_NAME);
			configList.add("VersionCode="
					+ String.valueOf(this.getPackageManager().getPackageInfo(
							this.getPackageName(), 0).versionCode) + "\n");

			configList.add("PRODUCT_TYPE="
					+ String.valueOf(Consts.PRODUCT_TYPE));
			configList
					.add("Update_Version=" + Consts.APP_UPDATE_VERSION + "\n");

			configList.add("Country=" + ConfigUtil.getCountry() + "("
					+ ConfigUtil.getServerLanguage() + ")\n");

			configList.add(this.getResources().getString(
					R.string.census_accounts)
					+ "=" + ConfigUtil.ACCOUNT_VERSION);
			configList.add(this.getResources().getString(
					R.string.census_appaccount)
					+ "=" + JVACCOUNT.GetVersion(0) + "\n");

			configList.add(this.getResources().getString(
					R.string.census_play_version)
					+ "=" + ConfigUtil.PLAY_VERSION);
			configList.add(this.getResources().getString(
					R.string.census_appplay_version)
					+ "=" + ConfigUtil.GETPLAY_VERSION + "\n");

			configList.add(this.getResources().getString(
					R.string.census_network_version)
					+ "=" + ConfigUtil.NETWORK_VERSION);
			configList.add(this.getResources().getString(
					R.string.census_appnetwork_version)
					+ "=" + ConfigUtil.GETNETWORK_VERSION + "\n");

			configList.add("SinaRes" + "=" + ConfigUtil.SINA_COUNTRY + "\n");

			if (ConfigUtil.ACCOUNT_VERSION.equalsIgnoreCase(JVACCOUNT
					.GetVersion(0))
					&& ConfigUtil.PLAY_VERSION
							.equalsIgnoreCase(ConfigUtil.GETPLAY_VERSION)
					&& ConfigUtil.NETWORK_VERSION
							.equalsIgnoreCase(ConfigUtil.GETNETWORK_VERSION)) {
				configList.add(0, "库没用错(Yes)" + "\n");
				configInfo.setTextColor(Color.BLACK);
			} else {
				configList.add(0, "库用错了(Oh,No)" + "\n");
				configInfo.setTextColor(Color.RED);
			}
			StringBuffer showMsg = new StringBuffer();
			for (int i = 0; i < configList.size(); i++) {
				showMsg.append(configList.get(i) + "\n");
			}
			configInfo.setText(showMsg.toString());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onBackPressed() {
		JVVersionActivity.this.finish();
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

}
