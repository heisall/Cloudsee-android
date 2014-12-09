package com.jovision.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.ChannelListAdapter;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.commons.MyList;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.views.AlarmDialog;

public class JVChannelListActivity extends BaseActivity {

	private ListView channel_listView;
	private ChannelListAdapter adapter;
	private ArrayList<Channel> channelList = new ArrayList<Channel>(1);
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private int deviceIndex;
	private Button btn_left;
	private Button btn_right;
	private TextView currentmenu;
	private boolean localFlag;
	private Device device;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case 1:
			ModifyDevTask task = new ModifyDevTask();
			String[] strParams = new String[4];
			strParams[0] = device.getFullNo();
			strParams[1] = channelList.get(arg1).getChannel() + "";
			strParams[2] = obj.toString();
			strParams[3] = arg1 + "";
			task.execute(strParams);
			break;
		case 1222:
			dialog(channelList.get(arg1).getChannel());
			break;
		case Consts.PUSH_MESSAGE:
			// 弹出对话框
			new AlarmDialog(this).Show(obj);
			break;
		default:
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		deviceList = CacheUtil.getDevList();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// MyActivityManager.getActivityManager().pushAlarmActivity(this);
	// getWindow().addFlags(
	// WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
	// WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
	// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	// }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (null != deviceList && 0 != deviceList.size()) {
			deviceIndex = getIntent().getIntExtra("deviceIndex", 0);
			device = deviceList.get(deviceIndex);
			channelList = device.getChannelList().toList();
			for (int i = 1; i < channelList.size(); i++) {
				channelList.get(i).setIspull(false);
			}
			adapter.setData(device.getChannelList().toList());
			channel_listView.setAdapter(adapter);
		}

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.channellist_layout);
		localFlag = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		currentmenu = (TextView) findViewById(R.id.currentmenu);
		currentmenu.setText(R.string.channalmanager);
		channel_listView = (ListView) findViewById(R.id.channel_listView);
		adapter = new ChannelListAdapter(JVChannelListActivity.this);

		btn_left.setOnClickListener(myOnClickListener);
		btn_right.setOnClickListener(myOnClickListener);

	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_right:
				AddChannelTask task = new AddChannelTask();
				String[] strParams = new String[1];
				strParams[0] = String.valueOf(deviceIndex);// 设备index
				task.execute(strParams);
				break;

			default:
				break;
			}
		}
	};

	// 保存更改设备信息线程
	class ModifyDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int delRes = -1;
			try {
				int num = Integer.valueOf(params[1]);
				int position = Integer.valueOf(params[3]);

				if (localFlag) {// 本地保存修改信息
					delRes = 0;
				} else {
					delRes = DeviceUtil.modifyPointName(params[0], num,
							params[2]);
				}
				if (delRes == 0) {
					deviceList.get(deviceIndex).getChannelList().toList()
							.get(position).setChannelName(params[2]);
					CacheUtil.saveDevList(deviceList);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return delRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。

		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	// 设置三种类型参数分别为String,Integer,String
	private class AddChannelTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int addRes = -1;
			int addIndex = Integer.parseInt(params[0]);

			int target = -1;
			int left2Add = -1;

			Channel channel = null;
			MyList<Channel> list = device.getChannelList();

			left2Add = Consts.MAX_DEVICE_CHANNEL_COUNT - list.size();

			if (left2Add > Consts.DEFAULT_ADD_CHANNEL_COUNT) {
				left2Add = Consts.DEFAULT_ADD_CHANNEL_COUNT;
			}

			if (left2Add > 0) {
				try {
					if (Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {// 本地添加
						addRes = 0;
					} else {
						addRes = DeviceUtil.addPoint(device.getFullNo(),
								left2Add);
					}

					if (0 == addRes) {
						for (int i = 0; i < left2Add; i++) {
							channel = new Channel();
							target = list.precheck();
							channel.setChannel(target);
							channel.setChannelName(device.getFullNo() + "_"
									+ target);
							list.add(channel);
						}
						device.setChannelList(list);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				addRes = 9999;
			}
			return addRes;
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
				showTextToast(R.string.add_channel_succ);
				channelList = device.getChannelList().toList();
				adapter.setData(channelList);
				adapter.notifyDataSetChanged();
				CacheUtil.saveDevList(deviceList);
			} else if (9999 == result) {
				showTextToast(R.string.channel_full);
			} else {
				showTextToast(R.string.add_channel_failed);
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

	// 设置三种类型参数分别为String,Integer,String
	class DelChannelTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int delRes = -1;
			int delDevIndex = Integer.parseInt(params[0]);
			int delChannelIndex = Integer.parseInt(params[1]);

			try {
				if (Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {// 本地删除
					if (1 == device.getChannelList().size()) {// 删设备
						device = null;
						deviceList.remove(delDevIndex);
						delRes = 1;
					} else {// 删通道
						device.getChannelList().remove(delChannelIndex);
						delRes = 0;
					}
				} else {
					if (1 == device.getChannelList().size()) {// 删设备
						delRes = DeviceUtil.unbindDevice(
								statusHashMap.get("KEY_USERNAME"),
								device.getFullNo());
						if (0 == delRes) {
							device = null;
							deviceList.remove(delDevIndex);
							delRes = 1;
						}
					} else {// 删通道
						delRes = DeviceUtil.deletePoint(device.getFullNo(),
								delChannelIndex);
						if (0 == delRes) {
							device.getChannelList().remove(delChannelIndex);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return delRes;
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
				showTextToast(R.string.del_channel_succ);
				channelList = device.getChannelList().toList();
				adapter.setData(channelList);
				adapter.notifyDataSetChanged();
				CacheUtil.saveDevList(deviceList);
			} else if (1 == result) {
				// [Neo] 删除最后一个应该退出通过管理界面
				showTextToast(R.string.del_channel_succ);
				finish();
			} else {
				showTextToast(R.string.del_channel_failed);
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

	protected void dialog(final int index) {
		String okString = JVChannelListActivity.this.getResources().getString(
				R.string.ok);
		String delectString = JVChannelListActivity.this.getResources()
				.getString(R.string.str_delete_sure);
		String warmString = JVChannelListActivity.this.getResources()
				.getString(R.string.str_delete_tip);
		String cancleString = JVChannelListActivity.this.getResources()
				.getString(R.string.str_crash_cancel);
		AlertDialog.Builder builder = new Builder(JVChannelListActivity.this);
		builder.setMessage(delectString);
		builder.setTitle(warmString);
		builder.setPositiveButton(okString,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						DelChannelTask task = new DelChannelTask();
						String[] strParams = new String[3];
						strParams[0] = String.valueOf(deviceIndex);// 设备index
						strParams[1] = String.valueOf(index);// 通道index
						task.execute(strParams);
					}
				});
		builder.setNegativeButton(cancleString,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}
