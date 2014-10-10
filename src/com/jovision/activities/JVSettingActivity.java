package com.jovision.activities;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.test.JVACCOUNT;
import android.util.Log;
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
import com.jovision.commons.JVAlarmConst;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.ConfigUtil;

public class JVSettingActivity extends BaseActivity {

	/** topBar */
	private Button leftBtn;
	private TextView currentMenu;
	private Button rightBtn;

	private ListView settingListView;
	private SettingAdapter settingAdapter;
	private ArrayList<SettingBean> dataArrayList;
	private String[] settingArray;

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
		setContentView(R.layout.setting);

		leftBtn = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.more_feather);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		settingArray = JVSettingActivity.this.getResources().getStringArray(
				R.array.array_setting);
		initData();
		initShare();
		settingListView = (ListView) findViewById(R.id.setting_listView);
		settingAdapter = new SettingAdapter(JVSettingActivity.this,
				dataArrayList);
		settingListView.setAdapter(settingAdapter);
		itemClick();
		leftBtn.setOnClickListener(myOnClickListener);
	}

	@Override
	protected void saveSettings() {
	}

	@Override
	protected void freeMe() {

	}

	private void initData() {
		dataArrayList = new ArrayList<SettingBean>();
		for (int i = 0; i < settingArray.length; i++) {
			SettingBean bean = new SettingBean();
			bean.setSetting_name(settingArray[i]);
			bean.setIsclick(false);
			dataArrayList.add(bean);
		}
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
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
		settingListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (dataArrayList.get(position).getIsclick()) {
					dataArrayList.get(position).setIsclick(false);
					setShare(position, false);
				} else {
					dataArrayList.get(position).setIsclick(true);
					setShare(position, true);
				}
				settingAdapter.notifyDataSetChanged();
			}
		});
	}

	private void setShare(int pos, Boolean flag) {
		switch (pos) {
		case 0:
			MySharedPreference.putBoolean("SCENE", flag);
			break;
		case 1:
			MySharedPreference.putBoolean("HELP", flag);
			break;
		case 2:// 报警开关
			AlarmTask task = new AlarmTask();
			Integer[] params = new Integer[3];
			if (flag) {// 1是关 0是开
				params[0] = JVAlarmConst.ALARM_ON;// 关闭状态，去打开报警
			} else {
				params[0] = JVAlarmConst.ALARM_OFF;// 已经打开了，要去关闭
			}
			task.execute(params);
			break;
		case 3:
			MySharedPreference.putBoolean("PlayDeviceMode", flag);
			break;
		default:
			break;
		}
	}

	private void initShare() {
		if (MySharedPreference.getBoolean("SCENE")) {
			dataArrayList.get(0).setIsclick(true);
		}
		if (MySharedPreference.getBoolean("HELP")) {
			dataArrayList.get(1).setIsclick(true);
		}
		if (MySharedPreference.getBoolean("AlarmSwitch")) {
			dataArrayList.get(2).setIsclick(true);
		}
		if (MySharedPreference.getBoolean("PlayDeviceMode")) {
			dataArrayList.get(3).setIsclick(true);
		}
	}

	// 设置三种类型参数分别为String,Integer,String
	private class AlarmTask extends AsyncTask<Integer, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(Integer... params) {
			int switchRes = -1;
			if (JVAlarmConst.ALARM_ON == params[0]) {// 开报警
				switchRes = JVACCOUNT.SetCurrentAlarmFlag(
						JVAlarmConst.ALARM_ON,
						ConfigUtil.getIMEI(JVSettingActivity.this));
				if (0 == switchRes) {
					MySharedPreference.putBoolean("AlarmSwitch", true);
					dataArrayList.get(2).setIsclick(true);
				}
			} else {// 关报警
				switchRes = JVACCOUNT.SetCurrentAlarmFlag(
						JVAlarmConst.ALARM_OFF,
						ConfigUtil.getIMEI(JVSettingActivity.this));
				if (0 == switchRes) {
					MySharedPreference.putBoolean("AlarmSwitch", false);
					dataArrayList.get(2).setIsclick(false);
				}
			}
			Log.e("JVAlarmConst.ALARM_ON---", switchRes + "");
			return switchRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			dismissDialog();
			settingAdapter.notifyDataSetChanged();
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
