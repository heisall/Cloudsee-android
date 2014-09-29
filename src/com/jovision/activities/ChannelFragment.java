package com.jovision.activities;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.ChannelAdapter;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.commons.MyList;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.DeviceUtil;

public class ChannelFragment extends BaseFragment {

	private String TAG = "ChannelFragment";

	/** 构造参数 */
	private int deviceIndex;
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private Device device;

	private int widthPixels;

	private GridView channelGridView;
	private ChannelAdapter channelAdapter;
	boolean localFlag = false;
	/** 弹出框 */
	private Dialog initDialog;// 显示弹出框
	private TextView dialogCancel;// 取消按钮
	private TextView dialogCompleted;// 确定按钮
	// 设备名称
	private TextView device_name;
	// 设备号
	private EditText device_numet;
	// 设备号码编辑键
	private ImageView device_numet_cancle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Bundle bundle = getArguments();
		// deviceIndex = bundle.getInt("DeviceIndex");
		// deviceList =
		// BeanUtil.stringToDevList(bundle.getString("DeviceList"));
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.channel_layout, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = (BaseActivity) getActivity();
		mParent = getView();

		channelGridView = (GridView) mParent
				.findViewById(R.id.channel_gridview);

		channelAdapter = new ChannelAdapter(this);
		MyLog.d(Consts.TAG_XX, "ChannelF.created: "
				+ device.getChannelList().toString());
		channelAdapter.setData(device.getChannelList().toList(), widthPixels);
		channelGridView.setAdapter(channelAdapter);

		localFlag = Boolean.valueOf(((BaseActivity) mActivity).statusHashMap
				.get(Consts.LOCAL_LOGIN));
	}

	public ChannelFragment(int devIndex, ArrayList<Device> devList,
			int widthPixels) {
		deviceIndex = devIndex;
		deviceList = devList;
		this.widthPixels = widthPixels;

		device = deviceList.get(devIndex);
	}

	@Override
	public void onPause() {
		CacheUtil.saveDevList(deviceList);
		super.onPause();
	}

	// // 设置data
	// public void setData(int devIndex, ArrayList<Device> devList, int
	// widthPixels) {
	// deviceIndex = devIndex;
	// deviceList = devList;
	// this.widthPixels = widthPixels;
	//
	// device = deviceList.get(devIndex);
	// if (null != channelAdapter && null != device
	// && null != device.getChannelList()) {
	// channelAdapter.setData(device.getChannelList().toList(),
	// widthPixels);
	// }
	// }

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.CHANNEL_ITEM_CLICK: {// 通道单击事件
			boolean changeRes = channelAdapter.setShowDelete(false);
			if (changeRes) {
				channelAdapter.notifyDataSetChanged();
			} else {
				// [Neo] TODO 多设备模式？
				boolean multiDeviceMode = false;
				ArrayList<Channel> clist = new ArrayList<Channel>();

				if (multiDeviceMode) {
					for (Device device : deviceList) {
						clist.addAll(device.getChannelList().toList());
					}
				} else {
					clist.addAll(deviceList.get(deviceIndex).getChannelList()
							.toList());
				}

				int size = clist.size();
				for (int i = 0; i < size; i++) {
					// [Neo] 循环利用播放数组，我 tm 就是个天才
					clist.get(i).setIndex(i % Consts.MAX_CHANNEL_CONNECTION);
				}

				((BaseActivity) mActivity).showTextToast(arg1 + "");
				Intent intentPlay = new Intent(mActivity, JVPlayActivity.class);

				// [Neo] 应该从文件中重新读取，不需传递
				// String devJsonString = Device.listToString(deviceList);
				// intentPlay.putExtra("DeviceList", devJsonString);

				intentPlay.putExtra("DeviceIndex", deviceIndex);
				// [Neo] 实际上是 int channel
				intentPlay.putExtra("ChannelIndex", arg1);

				mActivity.startActivity(intentPlay);
			}
			break;
		}
		case Consts.CHANNEL_ITEM_LONG_CLICK: {// 通道长按事件
			channelAdapter.setShowDelete(true);
			channelAdapter.notifyDataSetChanged();
			break;
		}
		case Consts.CHANNEL_ITEM_DEL_CLICK: {// 通道删除事件
			DelChannelTask task = new DelChannelTask();
			String[] strParams = new String[3];
			strParams[0] = String.valueOf(deviceIndex);// 设备index
			strParams[1] = String.valueOf(arg1);// 通道index
			task.execute(strParams);
			break;
		}
		case Consts.CHANNEL_ADD_CLICK: { // 通道添加
			AddChannelTask task = new AddChannelTask();
			String[] strParams = new String[3];
			strParams[0] = String.valueOf(deviceIndex);// 设备index
			strParams[1] = String.valueOf(arg1);// 通道index
			task.execute(strParams);
			break;
		}
		case Consts.CHANNEL_EDIT_CLICK: {// 通道编辑
			Channel channel = device.getChannelList().get(arg1);
			String name = channel.channelName;
			initSummaryDialog(name, arg1);
			break;
		}
		}
	}

	/** 弹出框初始化 */
	private void initSummaryDialog(String user, final int arg1) {
		initDialog = new Dialog(mActivity, R.style.mydialog);
		View view = LayoutInflater.from(mActivity).inflate(
				R.layout.dialog_channal, null);
		initDialog.setContentView(view);
		dialogCancel = (TextView) view.findViewById(R.id.dialog_img_cancel);
		dialogCompleted = (TextView) view
				.findViewById(R.id.dialog_img_completed);
		device_name = (TextView) view.findViewById(R.id.device_name);
		device_numet = (EditText) view.findViewById(R.id.device_numet);
		device_numet_cancle = (ImageView) view
				.findViewById(R.id.device_numet_cancle);
		device_numet_cancle.setOnClickListener(myOnClickListener);
		device_numet.setText(user);
		device_name.setText(user);
		initDialog.show();
		device_name.setFocusable(true);
		device_name.setFocusableInTouchMode(true);
		dialogCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initDialog.dismiss();
			}
		});
		dialogCompleted.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ModifyChannelTask task = new ModifyChannelTask();
				String[] strParams = new String[3];
				strParams[0] = String.valueOf(deviceIndex);
				strParams[1] = String.valueOf(arg1);// 通道index
				strParams[2] = device_numet.getText().toString();
				task.execute(strParams);
			}
		});
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.device_numet_cancle: {
				device_numet.setText("");
				break;
			}
			default:
				break;
			}
		}
	};

	// 设置三种类型参数分别为String,Integer,String
	class ModifyChannelTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int modRes = -1;
			int modDevIndex = Integer.parseInt(params[0]);
			int modChannelIndex = Integer.parseInt(params[1]);
			String newName = params[2];

			try {
				if (localFlag) {// 本地保存更改
					device.getChannelList().get(modChannelIndex)
							.setChannelName(newName);
					modRes = 0;
				} else {
					modRes = DeviceUtil.modifyPointName(modDevIndex + "",
							modChannelIndex, newName);
				}
				if (0 == modRes) {
					// [Neo] TODO 修改通道还是设备
					// device.setNickName(newName);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return modRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			((BaseActivity) mActivity).dismissDialog();
			if (0 == result) {

				((BaseActivity) mActivity)
						.showTextToast(R.string.login_str_point_edit_success);
				channelAdapter.notifyDataSetChanged();
			} else {
				((BaseActivity) mActivity)
						.showTextToast(R.string.login_str_point_edit_failed);
			}
			initDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			((BaseActivity) mActivity).createDialog("");
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
				if (localFlag) {// 本地删除
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
								((BaseActivity) mActivity).statusHashMap
										.get("KEY_USERNAME"), device
										.getFullNo());
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
			((BaseActivity) mActivity).dismissDialog();
			if (0 == result) {
				((BaseActivity) mActivity)
						.showTextToast(R.string.del_channel_succ);
				channelAdapter.setShowDelete(false);
				channelAdapter.setData(device.getChannelList().toList(),
						widthPixels);
			} else if (1 == result) {
				// [Neo] 删除最后一个应该退出通过管理界面
				((BaseActivity) mActivity)
						.showTextToast(R.string.del_channel_succ);
				((BaseActivity) mActivity).finish();
			} else {
				((BaseActivity) mActivity)
						.showTextToast(R.string.del_channel_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			((BaseActivity) mActivity).createDialog("");
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
			int result = -1;
			int addIndex = Integer.parseInt(params[0]);
			// int delChannelIndex = Integer.parseInt(params[1]);

			try {
				if (localFlag) {// 本地添加
					int target = -1;
					Channel channel = null;
					MyList<Channel> list = device.getChannelList();

					for (int i = 0; i < 4; i++) {
						channel = new Channel();
						target = list.precheck();
						channel.setChannel(target);
						channel.setChannelName(device.getFullNo() + "_"
								+ target);
						list.add(channel);
					}

					result = 0;

				} else {
					// [Neo] TODO 服务器

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			((BaseActivity) mActivity).dismissDialog();
			if (0 == result) {
				((BaseActivity) mActivity)
						.showTextToast(R.string.add_channel_succ);
				channelAdapter.setShowDelete(false);
				channelAdapter.setData(device.getChannelList().toList(),
						widthPixels);
			} else {
				((BaseActivity) mActivity)
						.showTextToast(R.string.add_channel_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			((BaseActivity) mActivity).createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

}