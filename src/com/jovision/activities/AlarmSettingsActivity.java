package com.jovision.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.test.JVACCOUNT;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.AlarmSettingsAdapter;
import com.jovision.bean.AlarmSettingsItemBean;
import com.jovision.commons.JVAlarmConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.ConfigUtil;

public class AlarmSettingsActivity extends BaseActivity implements
		View.OnClickListener {

	private String[] titleArray;
	private String[] tipsArray;
	private final int SWITCH_GONE = -1;
	private List<AlarmSettingsItemBean> dataList;
	private AlarmSettingsAdapter alarmAdapter;
	private ListView dataListView;
	private AlarmSettingsActivity mActivity;
	private int currentClickItemPosition = 0;// 当前点击的位置

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.alarm_settings_group_list_layout);
		InitViews();
		InitData();
	}

	private void InitViews() {
		leftBtn = (Button) findViewById(R.id.btn_left);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		leftBtn.setOnClickListener(this);
		currentMenu.setText(R.string.str_alarm_settings);
		dataListView = (ListView) findViewById(R.id.list_func);
	}

	private void InitData() {
		mActivity = this;
		dataList = new ArrayList<AlarmSettingsItemBean>();
		// 功能编号从0开始
		titleArray = getResources()
				.getStringArray(R.array.alarm_settings_title);
		tipsArray = getResources().getStringArray(R.array.alarm_settings_tips);

		try {
			// 0:消息推送tag
			AlarmSettingsItemBean item0 = new AlarmSettingsItemBean();
			item0.setIsTag(true);
			item0.setTitle(titleArray[0]);
			item0.setTips(tipsArray[0]);
			item0.setSwitch(SWITCH_GONE);
			item0.setEnabled(true);

			dataList.add(item0);

			// 1:消息推送data

			AlarmSettingsItemBean item1 = new AlarmSettingsItemBean();
			item1.setIsTag(false);
			item1.setTitle(titleArray[1]);
			item1.setTips(tipsArray[1]);
			item1.setSwitch(MySharedPreference.getBoolean(
					Consts.MORE_ALARMSWITCH, true) ? 1 : 0);
			item1.setEnabled(true);

			dataList.add(item1);

			// 2:音效tag
			AlarmSettingsItemBean item2 = new AlarmSettingsItemBean();
			item2.setIsTag(true);
			item2.setTitle(titleArray[2]);
			item2.setTips(tipsArray[2]);
			item2.setSwitch(SWITCH_GONE);
			item2.setEnabled(true);

			dataList.add(item2);

			// 3:铃声data
			AlarmSettingsItemBean item3 = new AlarmSettingsItemBean();
			item3.setIsTag(false);
			item3.setTitle(titleArray[3]);
			item3.setTips(tipsArray[3]);
			item3.setSwitch(MySharedPreference.getBoolean(
					Consts.ALARM_SETTING_SOUND, true) ? 1 : 0);
			item3.setEnabled(MySharedPreference.getBoolean(
					Consts.MORE_ALARMSWITCH, true));

			dataList.add(item3);

			// 4:振动data
			AlarmSettingsItemBean item4 = new AlarmSettingsItemBean();
			item4.setIsTag(false);
			item4.setTitle(titleArray[4]);
			item4.setTips(tipsArray[4]);
			item4.setSwitch(MySharedPreference.getBoolean(
					Consts.ALARM_SETTING_VIBRATE, true) ? 1 : 0);
			item4.setEnabled(MySharedPreference.getBoolean(
					Consts.MORE_ALARMSWITCH, true));

			dataList.add(item4);

			alarmAdapter = new AlarmSettingsAdapter(this, dataList);
			dataListView.setAdapter(alarmAdapter);

			listViewClick();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void listViewClick() {
		dataListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						currentClickItemPosition = position;
						Log.e("Alarm", "alarm setting onclick position:"
								+ currentClickItemPosition);
						AlarmSettingsItemBean itemObj = dataList
								.get(currentClickItemPosition);
						switch (position) {
						case 1: // 报警消息推送开关
							AlarmTask task = new AlarmTask();
							Integer[] params = new Integer[3];
							if (!MySharedPreference.getBoolean(
									Consts.MORE_ALARMSWITCH, true)) {// 1是关
								// 0是开
								params[0] = JVAlarmConst.ALARM_ON;// 关闭状态，去打开报警
							} else {
								params[0] = JVAlarmConst.ALARM_OFF;// 已经打开了，要去关闭
							}
							task.execute(params);
							return;
						case 3:// 铃声开关
							if (MySharedPreference.getBoolean(
									Consts.ALARM_SETTING_SOUND, true)) {
								MySharedPreference.putBoolean(
										Consts.ALARM_SETTING_SOUND, false);
							} else {
								MySharedPreference.putBoolean(
										Consts.ALARM_SETTING_SOUND, true);
							}
							itemObj.setSwitch(MySharedPreference.getBoolean(
									Consts.ALARM_SETTING_SOUND, true) ? 1 : 0);
							break;
						case 4:// 振动开关
							if (MySharedPreference.getBoolean(
									Consts.ALARM_SETTING_VIBRATE, true)) {
								MySharedPreference.putBoolean(
										Consts.ALARM_SETTING_VIBRATE, false);
							} else {
								MySharedPreference.putBoolean(
										Consts.ALARM_SETTING_VIBRATE, true);
							}
							itemObj.setSwitch(MySharedPreference.getBoolean(
									Consts.ALARM_SETTING_VIBRATE, true) ? 1 : 0);
							break;
						default:
							break;
						}
						dataList.set(currentClickItemPosition, itemObj);
						alarmAdapter.notifyDataSetChanged();
					}
				});
	}

	// 设置三种类型参数分别为String,Integer,String
	private class AlarmTask extends AsyncTask<Integer, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		private int flag = -1;// 标志开关操作

		@Override
		protected Integer doInBackground(Integer... params) {
			int switchRes = -1;
			if (JVAlarmConst.ALARM_ON == params[0]) {// 开报警
				flag = JVAlarmConst.ALARM_ON;
				switchRes = JVACCOUNT.SetCurrentAlarmFlag(
						JVAlarmConst.ALARM_ON, ConfigUtil.getIMEI(mActivity));
				if (0 == switchRes) {
					MyLog.e("ALARM--ON-", switchRes + "");
					MySharedPreference
							.putBoolean(Consts.MORE_ALARMSWITCH, true);
				}
			} else {// 关报警
				flag = JVAlarmConst.ALARM_OFF;
				switchRes = JVACCOUNT.SetCurrentAlarmFlag(
						JVAlarmConst.ALARM_OFF, ConfigUtil.getIMEI(mActivity));
				if (0 == switchRes) {
					MyLog.e("ALARM--CLOSE-", switchRes + "");
					MySharedPreference.putBoolean(Consts.MORE_ALARMSWITCH,
							false);
				}
			}

			return switchRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			mActivity.dismissDialog();
			if (result == 0) {
				AlarmSettingsItemBean itemObj = dataList
						.get(currentClickItemPosition);
				itemObj.setSwitch(MySharedPreference.getBoolean(
						Consts.MORE_ALARMSWITCH, true) ? 1 : 0);
				for (int i = 0; i < dataList.size(); i++) {
					if (i != currentClickItemPosition) {
						// 报警消息推送开关选项没有enable设置
						AlarmSettingsItemBean itemObj_tmp = dataList.get(i);
						itemObj_tmp.setEnabled(MySharedPreference.getBoolean(
								Consts.MORE_ALARMSWITCH, true));
						dataList.set(i, itemObj_tmp);
					}
				}

				dataList.set(currentClickItemPosition, itemObj);
				alarmAdapter.notifyDataSetChanged();
			} else {
				if (flag == JVAlarmConst.ALARM_ON) {
					mActivity.showTextToast(R.string.str_alarm_pushon_failed);
				} else {
					mActivity.showTextToast(R.string.str_alarm_pushoff_failed);
				}
			}

		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			mActivity.createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
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

}
