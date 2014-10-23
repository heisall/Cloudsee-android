package com.jovision.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.ChannelListAdapter;
import com.jovision.bean.Channel;
import com.jovision.bean.ChannellistBean;
import com.jovision.bean.Device;
import com.jovision.commons.MyList;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.DeviceUtil;

public class JVChannelListActivity extends BaseActivity {

	private ListView channel_listView;
	private ChannelListAdapter adapter;
	private MyList<Channel> channelList = new MyList<Channel>(1);
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private ArrayList<ChannellistBean> channelbeanList = new ArrayList<ChannellistBean>();
	private int deviceIndex;
	private Button btn_left;
	private Button btn_right;
	private TextView currentmenu;
	private boolean localFlag;
	private Device device;
	private int myindex = -1;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case 1:
			ModifyDevTask task = new ModifyDevTask();
			String[] strParams = new String[4];
			strParams[0] = channelbeanList.get(arg1).getCloudnum();
			strParams[1] = channelbeanList.get(arg1).getChannelnum() + "";
			strParams[2] = obj.toString();
			strParams[3] = arg1 + "";
			task.execute(strParams);
			break;
		case 2:
			myindex = arg1;
			dialog(channelbeanList.get(arg1).getChannelnum());
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

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		deviceList = CacheUtil.getDevList();
		device = deviceList.get(deviceIndex);
		channelList = deviceList.get(deviceIndex).getChannelList();
		initChannelBean();
		adapter.setData(channelbeanList);
		channel_listView.setAdapter(adapter);
	}

	@Override
	public void onPause() {
		CacheUtil.saveDevList(deviceList);
		super.onPause();
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
		deviceIndex = getIntent().getIntExtra("deviceIndex", 0);

		btn_left.setOnClickListener(myOnClickListener);
		btn_right.setOnClickListener(myOnClickListener);

	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	private void initChannelBean() {
		for (int i = 0; i < channelList.size()+1; i++) {
			ChannellistBean bean = new ChannellistBean();
			
			if (channelList.get(i) != null) {
				Log.i("TAG", channelList.get(i).getChannelName());
			}
			if (null != channelList.get(i)&&i != channelList.size()+1) {
				bean.setChannelName(channelList.get(i).getChannelName());
				bean.setCloudnum(channelList.get(i).getParent().getFullNo());
				bean.setChannelnum(channelList.get(i).getChannel());
				bean.setIspull(false);
				channelbeanList.add(bean);
			}
		}
		Log.i("TAG", channelList.size() + "ddddddddd" + channelbeanList.size());
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
					deviceList.get(deviceIndex).getChannelList()
							.get(position + 1).setChannelName(params[2]);
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
			CacheUtil.saveDevList(deviceList);
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
							ChannellistBean bean = new ChannellistBean();
							bean.setChannelName(device.getFullNo() + "_"
									+ target);
							bean.setCloudnum(device.getFullNo());
							bean.setChannelnum(target);
							bean.setIspull(false);
							channelbeanList.add(bean);
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
				Comparator comparator = new MyComparator();// 重要部分
				Collections.sort(channelbeanList, comparator);
				adapter.setData(channelbeanList);
				adapter.notifyDataSetChanged();
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
				channelbeanList.remove(myindex);
				adapter.setData(channelbeanList);
				adapter.notifyDataSetChanged();
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

	class MyComparator implements Comparator<ChannellistBean> {

		public int compare(ChannellistBean s1, ChannellistBean s2) {
			if (s1.getChannelnum() > s2.getChannelnum()) {
				return 1;
			} else if (s1.getChannelnum() < s2.getChannelnum()) {
				return -1;
			}
			return 0;
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
