package com.jovision.activities;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.adapters.SettingAdapter;
import com.jovision.bean.SettingBean;
import com.jovision.commons.MySharedPreference;

public class JVSettingActivity extends BaseActivity {

	/** topBar */
	private Button leftBtn;
	private TextView currentMenu;
	private Button rightBtn;

	private ListView setting_listView;
	private SettingAdapter settingAdapter;
	private ArrayList<SettingBean> dataArrayList;
	private String[] setting_array;

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
		setContentView(R.layout.setting);

		leftBtn = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.more_feather);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		setting_array = JVSettingActivity.this.getResources().getStringArray(
				R.array.array_setting);
		initData();
		initShare();
		setting_listView = (ListView) findViewById(R.id.setting_listView);
		settingAdapter = new SettingAdapter(JVSettingActivity.this,
				dataArrayList);
		setting_listView.setAdapter(settingAdapter);
		itemClick();
		leftBtn.setOnClickListener(myOnClickListener);
	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	private void initData() {
		dataArrayList = new ArrayList<SettingBean>();
		for (int i = 0; i < setting_array.length; i++) {
			SettingBean bean = new SettingBean();
			bean.setSetting_name(setting_array[i]);
			bean.setIsclick(false);
			dataArrayList.add(bean);
		}
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;

			default:
				break;
			}
		}
	};

	private void itemClick() {
		setting_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (dataArrayList.get(position).getIsclick()) {
					dataArrayList.get(position).setIsclick(false);
					setShare(position, "select");
				} else {
					dataArrayList.get(position).setIsclick(true);
					setShare(position, "selected");
				}
				settingAdapter.notifyDataSetChanged();
			}
		});
	}

	private void setShare(int pos, String setting) {
		switch (pos) {
		case 0:
			MySharedPreference.putString("SCENE", setting);
			break;
		case 1:
			MySharedPreference.putString("HELP", setting);
			break;
		case 2:
			MySharedPreference.putString("ALERT", setting);
			break;
		case 3:
			MySharedPreference.putString("SEE_MODEL", setting);
			break;
		default:
			break;
		}
	}

	private void initShare() {
		if (("selected").equals(MySharedPreference.getString("SCENE"))) {
			dataArrayList.get(0).setIsclick(true);
		}
		if (("selected").equals(MySharedPreference.getString("HELP"))) {
			dataArrayList.get(1).setIsclick(true);
		}
		if (("selected").equals(MySharedPreference.getString("ALERT"))) {
			dataArrayList.get(2).setIsclick(true);
		}
		if (("selected").equals(MySharedPreference.getString("SEE_MODEL"))) {
			dataArrayList.get(3).setIsclick(true);
		}
	}
}
