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
				String allStr = obj.toString();
				try {
					JSONArray dataArray = new JSONArray(allStr);
					int size = dataArray.length();
					JSONObject dataObj = null;
					if (size > 0) {//
						dataObj = dataArray.getJSONObject(0);
//						MyLog.v(TAG, "文本数据--"+obj.toString());
						switch (dataObj.getInt("flag")) {
						// 远程配置请求，获取到配置文本数据
						case JVNetConst.JVN_REMOTE_SETTING: {
							
							String settingJSON = dataObj.getString("msg");
//							文本数据--[{"flag":1,"msg":"CONFIG_VER=1;[ALL];YSTID=52942216;YSTGROUP=83;YSTSTATUS=1;nTimeFormat=0;MobileCH=2;Version=V1.1.0.341;DevName=HD IPC;nLanguage=0;nPosition=0;SN=19711;bSntp=0;sntpInterval=24;ntpServer=ntp.fudan.edu.cn;PhoneServer=0;viWidth=1280;viHeight=720;maxFramerate=30;YSTPort=9101;WebServer=0;WebPort=80;nAlarmDelay=10;acMailSender=ipcmail@163.com;acSMTPServer=smtp.163.com;acSMTPUser=ipcmail;acSMTPPasswd=ipcam71a;acReceiver0=;acReceiver1=;acReceiver2=;acReceiver3=;acSMTPPort=25;acSMTPCrypto=;GpioAlarmMode=0;WebSrvPort=80;bOnvif=1;OnvifPort=8099;alarmSrvAddr=120.192.19.4;alarmSrvPort=7070;alarmSrvType=0;ETH_GW=192.168.15.1;ETH_IP=192.168.15.4;ETH_NM=255.255.255.0;ACTIVED=0;bDHCP=1;ETH_DNS=202.102.128.68;ETH_MAC=e0:62:90:c1:18:d6;WIFI_IP=0.0.0.0;WIFI_GW=0.0.0.0;WIFI_NM=0.0.0.0;WIFI_DNS=202.102.128.68;WIFI_MAC=00:00:00:00:00:00;WIFI_ID=neiwang-2G;WIFI_PW=0123456789;WIFI_AUTH=4;WIFI_ENC=3;WIFI_Q=80;WIFI_ON=1;YSTID=52942216;YSTGROUP=83;YSTSTATUS=1;DEV_VERSION=1;","type":81}]
							// // 获取主控码流信息请求
							// Jni.sendTextData(1,
							// JVNetConst.JVN_RSP_TEXTDATA, 8,
							// JVNetConst.JVN_STREAM_INFO);
							
//							String textString1 = new String(pBuffer);
//							// Log.v("pBuffer", "pBuffer:"+ pBuffer.length);
//							Log.v("远程配置请求", "textString1:" + textString1);
//							if (!textString1.equalsIgnoreCase("")) {
//								String[] arrayStr = textString1.split(";");
//								if (null != arrayStr) {
//									for (int i = 0; i < arrayStr.length; i++) {
//										if (arrayStr[i].contains("=")) {
//											String[] arrayStr1 = arrayStr[i].split("=");
//											if (null == BaseApp.settingMap) {
//												BaseApp.settingMap = new HashMap<String, String>();
//											}
//											if (arrayStr1.length == 1) {
//												BaseApp.settingMap
//														.put(arrayStr1[0], "");
//											} else {
//												BaseApp.settingMap.put(arrayStr1[0],
//														arrayStr1[1]);
//											}
//
//										}
//									}
//								}
//							}
							
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

}