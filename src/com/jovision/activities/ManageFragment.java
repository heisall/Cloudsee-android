package com.jovision.activities;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.PlayUtil;

public class ManageFragment extends BaseFragment {

	private String TAG = "ManageFragment";
	DisplayMetrics disMetrics;

	/** 构造参数 */
	public int deviceIndex;
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private Device device;

	private GridView manageGridView;
	private ManageAdapter manageAdapter;
	boolean localFlag = false;
	private int isDevice;

	int devType = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		deviceIndex = JVDeviceManageFragment.deviceIndex;
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = (BaseActivity) getActivity();
		mParent = getView();
		disMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
		manageGridView = (GridView) mParent.findViewById(R.id.manage_gridview);
		localFlag = Boolean.valueOf(((BaseActivity) mActivity).statusHashMap
				.get(Consts.LOCAL_LOGIN));
		manageAdapter = new ManageAdapter(this);
		try {
			if (null != device) {
				manageAdapter.setData(disMetrics.widthPixels);
				manageGridView.setAdapter(manageAdapter);
				manageAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.MANAGE_ITEM_CLICK: {// adapter item 单击事件
			switch (arg1) {
			case 0: {// 远程设置
				mActivity.createDialog("");
				PlayUtil.connectDevice(deviceList.get(deviceIndex));
				break;
			}
			case 1: {// 设备管理

				break;
			}
			case 2: {// 连接模式
				isDevice = deviceList.get(deviceIndex).getIsDevice();
				Intent intent = new Intent();
				intent.putExtra("deviceIndex", deviceIndex);
				intent.putExtra("isDevice", isDevice);
				startActivity(new Intent(mActivity, JVIpconnectActivity.class));
				break;
			}
			case 3: {// 通道管理

				break;
			}
			case 4: {// 立即观看
				PlayUtil.prepareConnect(deviceList, deviceIndex);
				Intent intentPlay = new Intent(mActivity, JVPlayActivity.class);
				intentPlay.putExtra("DeviceIndex", deviceIndex);
				intentPlay.putExtra("ChannelIndex", device.getChannelList()
						.toList().get(0).getChannel());
				intentPlay.putExtra("PlayFlag", Consts.PLAY_NORMAL);

				mActivity.startActivity(intentPlay);
				break;
			}
			case 5: {// 添加设备
				Intent addIntent = new Intent();
				addIntent.setClass(mActivity, JVAddDeviceActivity.class);
				addIntent.putExtra("QR", false);
				mActivity.startActivity(addIntent);
				break;
			}

			}
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
			MyLog.v(TAG, obj.toString());
			if (Consts.DEVICE_TYPE_IPC == devType) {// 是IPC
				// 暂停视频
				Jni.sendBytes(Consts.CHANNEL_JY, JVNetConst.JVN_CMD_VIDEOPAUSE,
						new byte[0], 8);
				// 请求文本聊天
				Jni.sendBytes(Consts.CHANNEL_JY, JVNetConst.JVN_REQ_TEXT,
						new byte[0], 8);
			} else { // 不是IPC
				PlayUtil.disconnectDevice();
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

			MyLog.v("NORMALDATA", obj.toString());
			break;
		}
		case Consts.CALL_CONNECT_CHANGE: { // 连接回调
			MyLog.e(TAG, "CONNECT_CHANGE: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);
			if (1 != arg1) {// IPC连接失败才提示
				if (0 == devType || Consts.DEVICE_TYPE_IPC == devType) {// 没取到设备类型，或者IPC才显示连接结果
					mActivity.dismissDialog();
					try {
						JSONObject connectObj = new JSONObject(obj.toString());
						String errorMsg = connectObj.getString("msg");
						if ("pass word is wrong!".equalsIgnoreCase(errorMsg)
								|| "pass word is wrong!"
										.equalsIgnoreCase(errorMsg)) {// 密码错误时提示身份验证失败
							mActivity.showTextToast(R.string.connfailed_auth);
						} else if ("channel is not open!"
								.equalsIgnoreCase(errorMsg)) {// 无该通道服务
							mActivity
									.showTextToast(R.string.connfailed_channel_notopen);
						} else if ("connect type invalid!"
								.equalsIgnoreCase(errorMsg)) {// 连接类型无效
							mActivity
									.showTextToast(R.string.connfailed_type_invalid);
						} else if ("client count limit!"
								.equalsIgnoreCase(errorMsg)) {// 超过主控最大连接限制
							mActivity
									.showTextToast(R.string.connfailed_maxcount);
						} else if ("connect timeout!"
								.equalsIgnoreCase(errorMsg)) {//
							mActivity
									.showTextToast(R.string.connfailed_timeout);
						} else {
							mActivity.showTextToast(R.string.connect_failed);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
			break;
		}
		case Consts.CALL_TEXT_DATA: {// 文本回调
			MyLog.e(TAG, "TEXT_DATA: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);
			switch (arg1) {
			case JVNetConst.JVN_RSP_TEXTACCEPT:// 同意文本聊天
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// 获取基本文本信息
				Jni.sendTextData(Consts.CHANNEL_JY,
						JVNetConst.JVN_RSP_TEXTDATA, 8,
						JVNetConst.JVN_REMOTE_SETTING);

				break;
			case JVNetConst.JVN_CMD_TEXTSTOP:// 不同意文本聊天
				PlayUtil.disconnectDevice();
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
						intent.putExtra("Device", device.toString());
						mActivity.startActivity(intent);
						break;
					}
					case JVNetConst.JVN_WIFI_INFO:// 2-- AP,WIFI热点请求
						// 获取主控码流信息请求
						Jni.sendTextData(Consts.CHANNEL_JY,
								JVNetConst.JVN_RSP_TEXTDATA, 8,
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
		default:
			break;
		}
	};

	@Override
	public void onPause() {
		CacheUtil.saveDevList(deviceList);
		super.onPause();
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {

			default:
				break;
			}
		}
	};

}