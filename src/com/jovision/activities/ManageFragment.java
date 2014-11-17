package com.jovision.activities;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.ManageAdapter;
import com.jovision.bean.Device;
import com.jovision.commons.JVDeviceConst;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.PlayUtil;
import com.jovision.views.AlarmDialog;
import com.tencent.stat.StatService;

@SuppressLint("ValidFragment")
public class ManageFragment extends BaseFragment {

	private String TAG = "ManageFragment";

	DisplayMetrics disMetrics;

	/** 构造参数 */
	private int deviceIndex;
	private ArrayList<Device> deviceList;
	private Device device;
	public static int mScreenWidth;

	private GridView manageGridView;
	private ManageAdapter manageAdapter;
	boolean localFlag = false;
	private int isDevice;
	private Bundle bundle;

	int devType = 0;
	private boolean isConnected;
	private Message connectMsg;

	public ManageFragment() {
		deviceList = new ArrayList<Device>();
	}

	// public ManageFragment(ArrayList<Device> deviceList) {
	// this.deviceList = deviceList;
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		bundle = getArguments();
		deviceIndex = bundle.getInt("DeviceIndex");
		deviceList = CacheUtil.getDevList();
		device = deviceList.get(deviceIndex);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.manage_layout, null);
		return view;
	}

	public void setDevIndex(int index) {
		try {
			deviceIndex = index;
			if (null == deviceList || 0 == deviceList.size()) {
				deviceList = CacheUtil.getDevList();
			}
			device = deviceList.get(deviceIndex);
			if (null != device && null != manageAdapter) {
				manageAdapter.setData(disMetrics.widthPixels, deviceIndex,
						device, localFlag);
				manageGridView.setAdapter(manageAdapter);
				manageAdapter.notifyDataSetChanged();
			}

		} catch (Exception e) {
			MyLog.e(TAG, "setDevIndex=device" + device + "--" + localFlag);
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = (BaseActivity) getActivity();
		mParent = getView();
		disMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
		manageGridView = (GridView) mParent.findViewById(R.id.manage_gridview);
		mScreenWidth = mActivity.disMetrics.widthPixels;
		manageGridView.setHorizontalSpacing(50);
		manageGridView.setVerticalSpacing(50);
		if (mScreenWidth == 480 || mScreenWidth == 540) {
			manageGridView.setHorizontalSpacing(20);
			manageGridView.setVerticalSpacing(20);
		}
		if (mScreenWidth == 1080) {
			manageGridView.setHorizontalSpacing(60);
			manageGridView.setVerticalSpacing(65);
		}

		localFlag = Boolean.valueOf(((BaseActivity) mActivity).statusHashMap
				.get(Consts.LOCAL_LOGIN));
		manageAdapter = new ManageAdapter(this);
		try {
			if (null != device) {
				manageAdapter.setData(disMetrics.widthPixels, deviceIndex,
						device, localFlag);
				manageGridView.setAdapter(manageAdapter);
				manageAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.CALL_CONNECT_CHANGE: {
			switch (arg2) {
			case JVNetConst.CONNECT_OK: {
				isConnected = true;
				fragHandler.sendMessage(fragHandler.obtainMessage(what, arg1,
						arg2, obj));
				break;
			}
			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK:
				// 4 -- 连接失败
			case JVNetConst.CONNECT_FAILED:
				// 6 -- 连接异常断开
			case JVNetConst.ABNORMAL_DISCONNECT:
				// 7 -- 服务停止连接，连接断开
			case JVNetConst.SERVICE_STOP:
				connectMsg = fragHandler.obtainMessage(what, arg1, arg2, obj);
				break;
			case Consts.BAD_NOT_CONNECT: {
				isConnected = false;
				if (null != connectMsg) {
					fragHandler.sendMessage(connectMsg);
				}
				break;
			}
			default:
				fragHandler.sendMessage(fragHandler.obtainMessage(what, arg1,
						arg2, obj));
				break;
			}

		}
		default:
			fragHandler.sendMessage(fragHandler.obtainMessage(what, arg1, arg2,
					obj));
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		deviceList = CacheUtil.getDevList();
		mActivity = (BaseActivity) getActivity();
		// isDevice = deviceList.get(deviceIndex).getIsDevice();
		// device = deviceList.get(deviceIndex);
		// MyLog.e("远程设置--setIndex",
		// "index="+deviceIndex+";device="+device.toString());
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		MyLog.i("ManageFragment", "onTabAction:what=" + what + ";arg1=" + arg1
				+ ";arg2=" + arg1);
		switch (what) {
		case Consts.MANAGE_ITEM_CLICK: {// adapter item 单击事件
			JVDeviceManageFragment.deviceIndex = arg2;
			device = deviceList.get(JVDeviceManageFragment.deviceIndex);
			switch (arg1) {
			case 0: {// 远程设置
				StatService.trackCustomEvent(mActivity, "onCreat", "welcome");
				if (2 == isDevice) {
					mActivity.showTextToast(R.string.ip_add_notallow);
				} else {
					mActivity.createDialog("");
					mActivity.proDialog.setCancelable(false);
					PlayUtil.connectDevice(device);
				}
				break;
			}
			case 1: {// 设备管理
				StatService.trackCustomEvent(mActivity, "DeviceManage", "设备管理");
				devType = 0;
				Intent deviceIntent = new Intent(mActivity,
						JVDeviceManageActivity.class);
				deviceIntent.putExtra("deviceIndex", deviceIndex);
				startActivity(deviceIntent);
				break;
			}
			case 2: {// 连接模式
				StatService.trackCustomEvent(mActivity, "Ipconnect", "连接模式");
				if (2 == isDevice) {
					mActivity.showTextToast(R.string.ip_add_notallow);
				} else {
					Intent intent = new Intent(mActivity,
							JVIpconnectActivity.class);
					intent.putExtra("deviceIndex", deviceIndex);
					startActivity(intent);
				}

				break;
			}
			case 3: {// 通道管理
				StatService.trackCustomEvent(mActivity, "ChannelList", "通道管理");
				deviceIndex = bundle.getInt("DeviceIndex");
				Intent channerIntent = new Intent(mActivity,
						JVChannelListActivity.class);
				channerIntent.putExtra("deviceIndex", deviceIndex);
				startActivity(channerIntent);
				break;
			}
			case 4: {// 立即观看
				// if (0 == deviceList.get(deviceIndex).getOnlineState()
				// && !Boolean
				// .valueOf(((BaseActivity) mActivity).statusHashMap
				// .get(Consts.LOCAL_LOGIN))) {
				// mActivity.showTextToast(R.string.offline_not_play);
				// } else {
				StatService.trackCustomEvent(mActivity, "Play", "立即观看");
				if (0 == deviceList.get(deviceIndex).getChannelList().size()) {// 0个通道直接播放
					mActivity.showTextToast(R.string.selectone_to_connect);
				} else {
					ArrayList<Device> playList = PlayUtil.prepareConnect(
							deviceList, deviceIndex);

					Intent intentPlay = new Intent(mActivity,
							JVPlayActivity.class);
					intentPlay.putExtra(Consts.KEY_PLAY_NORMAL,
							playList.toString());
					intentPlay.putExtra("DeviceIndex", deviceIndex);
					intentPlay.putExtra("ChannelofChannel", device
							.getChannelList().toList().get(0).getChannel());
					intentPlay.putExtra("PlayFlag", Consts.PLAY_NORMAL);

					mActivity.startActivity(intentPlay);
				}
				// }

				break;
			}
			case 5: {// 添加设备
				StatService.trackCustomEvent(mActivity, "ThirdDevList", "添加设备");
				// Intent addIntent = new Intent();
				// addIntent.setClass(mActivity, JVAddDeviceActivity.class);
				// addIntent.putExtra("QR", false);
				// mActivity.startActivity(addIntent);
				// [lkp]改成报警管理
				Intent playIntent = new Intent();
				// playIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				playIntent.setClass(mActivity, ThirdDevListActivity.class);
				playIntent.putExtra("dev_index", deviceIndex);
				mActivity.startActivity(playIntent);
				break;
			}

			case 6: {// 一键升级
				if (JVDeviceConst.DEVICE_SERVER_ONLINE == device
						.getServerState()) {
					StatService.trackCustomEvent(mActivity, "DeviceUpdate",
							"一键升级");
					Intent deviceIntent = new Intent(mActivity,
							JVDeviceUpdateActivity.class);
					deviceIntent.putExtra("deviceIndex", deviceIndex);
					startActivity(deviceIntent);
				} else {
					mActivity.showTextToast(R.string.device_offline);
				}
				break;
			}

			case 7: {// 安全防护开关
				mActivity.createDialog("");
				AlarmSwitchTask task = new AlarmSwitchTask();
				String[] params = new String[3];
				task.execute(params);
				break;
			}
			}
			break;
		}
		case Consts.CALL_STAT_REPORT: {// 统计
			// connect[1], has connected!!
			MyLog.i(TAG, "CALL_STAT_REPORT: " + what + ", " + arg1 + ", "
					+ arg2 + ", " + obj);
			break;
		}
		case Consts.CALL_NORMAL_DATA: {
			try {
				JSONObject jobj;
				jobj = new JSONObject(obj.toString());
				if (null != jobj) {
					devType = jobj.optInt("device_type");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (Consts.DEVICE_TYPE_IPC == devType) {// 是IPC
				// // 暂停视频
				// Jni.sendBytes(Consts.CHANNEL_JY,
				// JVNetConst.JVN_CMD_VIDEOPAUSE,
				// new byte[0], 8);
				// 请求文本聊天
				Jni.sendBytes(1, JVNetConst.JVN_REQ_TEXT, new byte[0], 8);
			} else { // 不是IPC
				PlayUtil.disconnectDevice();
				while (isConnected) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mActivity.dismissDialog();
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						mActivity);

				builder1.setTitle(R.string.tips);
				builder1.setMessage(R.string.str_not_support_this_device);
				builder1.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder1.create().show();
			}

			break;
		}
		case Consts.CALL_CONNECT_CHANGE: { // 连接回调
			MyLog.i(TAG, "CONNECT_CHANGE: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);

			if (Consts.BAD_NOT_CONNECT == arg2) {
				mActivity.dismissDialog();
			} else if (JVNetConst.CONNECT_OK != arg2
					&& JVNetConst.DISCONNECT_OK != arg2) {// IPC连接失败才提示(非连接成功和断开连接)
				mActivity.dismissDialog();
				try {
					JSONObject connectObj = new JSONObject(obj.toString());
					String errorMsg = connectObj.getString("msg");
					if ("password is wrong!".equalsIgnoreCase(errorMsg)
							|| "pass word is wrong!".equalsIgnoreCase(errorMsg)) {// 密码错误时提示身份验证失败
						mActivity.showTextToast(R.string.connfailed_auth);
					} else if ("channel is not open!"
							.equalsIgnoreCase(errorMsg)) {// 无该通道服务
						mActivity
								.showTextToast(R.string.connfailed_channel_notopen);
					} else if ("connect type invalid!"
							.equalsIgnoreCase(errorMsg)) {// 连接类型无效
						mActivity
								.showTextToast(R.string.connfailed_type_invalid);
					} else if ("client count limit!".equalsIgnoreCase(errorMsg)) {// 超过主控最大连接限制
						mActivity.showTextToast(R.string.connfailed_maxcount);
					} else if ("connect timeout!".equalsIgnoreCase(errorMsg)) {//
						mActivity.showTextToast(R.string.connfailed_timeout);
					} else {
						mActivity.showTextToast(R.string.connect_failed);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			break;
		}
		case Consts.CALL_TEXT_DATA: {// 文本回调
			MyLog.i(TAG, "TEXT_DATA: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);
			switch (arg2) {
			case JVNetConst.JVN_RSP_TEXTACCEPT:// 同意文本聊天
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// 获取基本文本信息
				Jni.sendTextData(1, JVNetConst.JVN_RSP_TEXTDATA, 8,
						JVNetConst.JVN_REMOTE_SETTING);

				break;
			case JVNetConst.JVN_CMD_TEXTSTOP:// 不同意文本聊天
				PlayUtil.disconnectDevice();
				while (isConnected)
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				mActivity.dismissDialog();
				mActivity
						.showTextToast(R.string.str_only_administator_use_this_function);
				break;

			case JVNetConst.JVN_RSP_TEXTDATA:// 文本数据
				String allStr = obj.toString();
				try {
					JSONObject dataObj = new JSONObject(allStr);
					// MyLog.v(TAG, "文本数据--"+obj.toString());
					switch (dataObj.getInt("flag")) {
					// 远程配置请求，获取到配置文本数据
					case JVNetConst.JVN_REMOTE_SETTING: {

						String settingJSON = dataObj.getString("msg");

						mActivity.dismissDialog();
						Intent intent = new Intent(mActivity,
								JVRemoteSettingActivity.class);
						intent.putExtra("SettingJSON", settingJSON);
						device = deviceList
								.get(JVDeviceManageFragment.deviceIndex);
						MyLog.v("远程设置--", "remoteIndex="
								+ JVDeviceManageFragment.deviceIndex
								+ ";device=" + device.toString());
						intent.putExtra("Device", device.toString());
						mActivity.startActivity(intent);
						break;
					}
					case JVNetConst.JVN_WIFI_INFO:// 2-- AP,WIFI热点请求
						// 获取主控码流信息请求
						Jni.sendTextData(1, JVNetConst.JVN_RSP_TEXTDATA, 8,
								JVNetConst.JVN_STREAM_INFO);
						break;
					case JVNetConst.JVN_STREAM_INFO:// 3-- 码流配置请求

						break;
					case JVNetConst.EX_WIFI_AP_CONFIG:// 11 ---新wifi配置流程
						break;
					case JVNetConst.JVN_WIFI_SETTING_SUCCESS:// 4-- wifi配置成功
						break;
					case JVNetConst.JVN_WIFI_SETTING_FAILED:// 5--WIFI配置失败
						break;
					case JVNetConst.JVN_WIFI_IS_SETTING:// -- 6 正在配置wifi

						break;

					default:
						break;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}
			break;
		}
		case Consts.PUSH_MESSAGE:
			// 弹出对话框
			if (null != mActivity) {
				new AlarmDialog(mActivity).Show(obj);
			} else {
				MyLog.e("Alarm",
						"onHandler mActivity is null ,so dont show the alarm dialog");
			}
			break;

		default:
			break;
		}
	};

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {

			default:
				break;
			}
		}
	};

	// 设置三种类型参数分别为String,Integer,String
	class AlarmSwitchTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int switchRes = -1;// 0成功， 1失败，1000离线
			try {
				if (null == device) {
					device = deviceList.get(deviceIndex);
				}
				if (JVDeviceConst.DEVICE_SERVER_ONLINE == device
						.getServerState()) {
					int sendTag = 0;
					if (JVDeviceConst.DEVICE_SWITCH_OPEN == device
							.getAlarmSwitch()) {
						sendTag = JVDeviceConst.DEVICE_SWITCH_CLOSE;
					} else if (JVDeviceConst.DEVICE_SWITCH_CLOSE == device
							.getAlarmSwitch()) {
						sendTag = JVDeviceConst.DEVICE_SWITCH_OPEN;
					}
					switchRes = DeviceUtil.saveSettings(
							JVDeviceConst.DEVICE_ALARTM_SWITCH, sendTag,
							mActivity.statusHashMap.get(Consts.KEY_USERNAME),
							device.getFullNo());
				} else {
					switchRes = 1000;
				}

			} catch (Exception e) {
				e.printStackTrace();
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
			if (0 == result) {
				StatService.trackCustomEvent(mActivity, "Alarm", "安全防护");
				if (JVDeviceConst.DEVICE_SWITCH_OPEN == device.getAlarmSwitch()) {
					device.setAlarmSwitch(JVDeviceConst.DEVICE_SWITCH_CLOSE);
					mActivity.showTextToast(R.string.protect_close_succ);
				} else if (JVDeviceConst.DEVICE_SWITCH_CLOSE == device
						.getAlarmSwitch()) {
					device.setAlarmSwitch(JVDeviceConst.DEVICE_SWITCH_OPEN);
					mActivity.showTextToast(R.string.protect_open_succ);
				}
				manageAdapter.setData(disMetrics.widthPixels, deviceIndex,
						device, localFlag);
				manageAdapter.notifyDataSetChanged();
				// [{"fullNo":"S52942216","port":0,"hasWifi":1,"isDevice":0,"no":52942216,"is05":false,"onlineState":-1182329167,"channelList":[{"channel":1,"channelName":"S52942216_1","index":0}],"isHomeProduct":false,"ip":"","pwd":"123","nickName":"S52942216","deviceType":2,"alarmSwitch":1,"gid":"S","user":"abc","serverState":1,"doMain":""}]
				CacheUtil.saveDevList(deviceList);
			} else if (1000 == result) {
				mActivity.showTextToast(R.string.device_offline);
			} else {
				if (JVDeviceConst.DEVICE_SWITCH_OPEN == device.getAlarmSwitch()) {
					mActivity.showTextToast(R.string.protect_close_fail);
				} else if (JVDeviceConst.DEVICE_SWITCH_CLOSE == device
						.getAlarmSwitch()) {
					mActivity.showTextToast(R.string.protect_open_fail);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			mActivity.createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。

		}
	}

}