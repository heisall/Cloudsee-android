package com.jovision.activities;

import java.util.ArrayList;

import org.json.JSONArray;
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
	private int deviceIndex;
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private Device device;

	private GridView manageGridView;
	private ManageAdapter manageAdapter;
	boolean localFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MyLog.v(TAG, "onCreate");
		Bundle bundle = getArguments();
		deviceIndex = bundle.getInt("DeviceIndex");
		deviceList = CacheUtil.getDevList();
		device = deviceList.get(deviceIndex);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		MyLog.v(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.manage_layout, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MyLog.v(TAG, "onActivityCreated");
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
	public void onResume() {
		MyLog.v(TAG, "onResume");
		super.onResume();
	}

	// public ManageFragment(int devIndex, ArrayList<Device> devList) {
	// deviceIndex = devIndex;
	// deviceList = devList;
	// device = deviceList.get(devIndex);
	// MyLog.v(TAG, "ManageFragment: device=" + device);
	// }

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
				mActivity.dismissDialog();
				PlayUtil.disconnectDevice();
				break;
			}
			case 2: {// 连接模式

				break;
			}
			case 3: {// 通道管理

				break;
			}
			case 4: {// 立即观看
				((BaseActivity) mActivity).showTextToast(arg1 + "");
				Intent intentPlay = new Intent(mActivity, JVPlayActivity.class);
				String devJsonString = Device.listToString(deviceList);
				intentPlay.putExtra("DeviceList", devJsonString);
				intentPlay.putExtra("DeviceIndex", deviceIndex);
				intentPlay.putExtra("ChannelIndex", 0);
				mActivity.startActivity(intentPlay);
				break;
			}
			case 5: {// 添加设备
				Intent intent = new Intent();
				intent.setClass(mActivity, JVAddDeviceActivity.class);
				mActivity.startActivity(intent);
				break;
			}

			}
			break;
		}

		case Consts.CALL_NORMAL_DATA: {
			int devType = 0;
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
			if (3 == devType) {// 是IPC
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
				builder1.setNegativeButton(R.string.str_cancel,
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
			if(1 != arg1){
				mActivity.dismissDialog();
				mActivity.showTextToast(R.string.str_host_connect_timeout);
			}
			
//			switch (arg1) {
//			// 1 -- 连接成功
//			case JVNetConst.CONNECT_OK: {
////				mActivity.showTextToast(R.string.str_host_connect_timeout);
//				break;
//			}
//
//			// 2 -- 断开连接成功
//			case JVNetConst.DISCONNECT_OK: {
//				mActivity.showTextToast(R.string.str_host_connect_timeout);
//				break;
//			}
//
//			// 3 -- 不必要重复连接
//			case JVNetConst.NO_RECONNECT: {
//				mActivity.showTextToast(R.string.str_host_connect_timeout);
//				break;
//			}
//
//			// 4 -- 连接失败
//			case JVNetConst.CONNECT_FAILED: {
//				mActivity.showTextToast(R.string.str_host_connect_timeout);
//				break;
//			}
//
//			// 5 -- 没有连接
//			case JVNetConst.NO_CONNECT: {
//				mActivity.showTextToast(R.string.str_host_connect_timeout);
//				break;
//			}
//
//			// 6 -- 连接异常断开
//			case JVNetConst.ABNORMAL_DISCONNECT: {
//				mActivity.showTextToast(R.string.str_host_connect_timeout);
//				break;
//			}
//
//			// 7 -- 服务停止连接，连接断开
//			case JVNetConst.SERVICE_STOP: {
//				mActivity.showTextToast(R.string.str_host_connect_timeout);
//				break;
//			}
//
//			// 8 -- 断开连接失败
//			case JVNetConst.DISCONNECT_FAILED: {
//				mActivity.showTextToast(R.string.str_host_connect_timeout);
//				break;
//			}
//
//			// 9 -- 其他错误
//			case JVNetConst.OHTER_ERROR: {
//				mActivity.showTextToast(R.string.str_host_connect_timeout);
//				break;
//			}
//			default:
//				break;
//			}

			break;
		}
		case Consts.CALL_TEXT_DATA: {// 文本回调
			MyLog.e(TAG, "TEXT_DATA: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);
			switch (arg1) {
			case JVNetConst.JVN_RSP_TEXTACCEPT:// 同意文本聊天
				// 获取基本文本信息
				Jni.sendTextData(Consts.CHANNEL_JY,
						JVNetConst.JVN_RSP_TEXTDATA, 8,
						JVNetConst.JVN_REMOTE_SETTING);
				break;
			case JVNetConst.JVN_CMD_TEXTSTOP:// 不同意文本聊天
				PlayUtil.disconnectDevice();
				mActivity.dismissDialog();
				mActivity.showTextToast(R.string.str_only_administator_use_this_function);
				break;

			case JVNetConst.JVN_RSP_TEXTDATA:// 文本数据
				try {
					JSONArray dataArray = new JSONArray(obj.toString());
					int size = dataArray.length();
					JSONObject dataObj = null;
					if (size > 0) {//
						dataObj = dataArray.getJSONObject(0);

						switch (dataObj.getInt("flag")) {
						// 远程配置请求，获取到配置文本数据
						case JVNetConst.JVN_REMOTE_SETTING: {
							// // 获取主控码流信息请求
							// Jni.sendTextData(1,
							// JVNetConst.JVN_RSP_TEXTDATA, 8,
							// JVNetConst.JVN_STREAM_INFO);
							mActivity.dismissDialog();
							Intent intent = new Intent(mActivity,
									JVRemoteSettingActivity.class);;
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

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {

			default:
				break;
			}
		}
	};

	// @Override
	// public void onDetach() {
	// super.onDetach();
	// try {
	// Field childFragmentManager = Fragment.class
	// .getDeclaredField("mChildFragmentManager");
	// childFragmentManager.setAccessible(true);
	// childFragmentManager.set(this, null);
	//
	// } catch (NoSuchFieldException e) {
	// throw new RuntimeException(e);
	// } catch (IllegalAccessException e) {
	// throw new RuntimeException(e);
	// }
	//
	// }
	// http://blog.csdn.net/leewenjin/article/details/19410949
}