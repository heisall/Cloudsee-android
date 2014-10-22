package com.jovision.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.test.JVACCOUNT;
import android.util.Log;

import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.commons.JVDeviceConst;
import com.jovision.commons.MyList;
import com.jovision.commons.MyLog;

public class DeviceUtil {

	/**
	 * 查询账号下设备列表
	 * 
	 * @param userName
	 *            用户名
	 * @return ArrayList<Device> 设备列表
	 */
	public static ArrayList<Device> getUserDeviceList(String userName) {

		ArrayList<Device> deviceList = null;
		// 参数例子：{"mt":2003,"pv":"1.0","lpt":1,"username":"homeuser@jovemail.com"}
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_USER_DEVICES);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);
			jObj.put(JVDeviceConst.JK_USERNAME, userName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getUserDeviceList---request", jObj.toString());

		// 返回值范例：{"pv":"1.0","lpt":1,"username":"zhangs","mt":2003,"mid":1}
		/**
		 * {"mt":2004,"rt":0,"mid":1,"dlist":
		 * [{"dguid":"ABCD0001","dtype":2,"dname":"qiqidevice"},
		 * {"dguid":"ABCD0002","dtype":2,"dname":"zhangsdevice"}]}
		 */

		// 接收返回数据
		byte[] resultStr = new byte[1024 * 30];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("getUserDeviceList---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);

						if (0 != rt) {// 获取失败
							deviceList = null;
						} else {// 获取成功
							deviceList = new ArrayList<Device>();
							// int mid =
							// temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
							JSONArray dlist = new JSONArray(
									temObj.optString(JVDeviceConst.JK_DEVICE_LIST));
							if (null != dlist && 0 != dlist.length()) {
								for (int i = 0; i < dlist.length(); i++) {
									JSONObject obj = dlist.getJSONObject(i);
									if (null != obj) {
										Device dev = new Device();
										dev.setFullNo(obj
												.optString(JVDeviceConst.JK_DEVICE_GUID));
										dev.setGid(ConfigUtil.getGroup(obj
												.optString(JVDeviceConst.JK_DEVICE_GUID)));
										dev.setNo(ConfigUtil.getYST(obj
												.optString(JVDeviceConst.JK_DEVICE_GUID)));
										dev.setDeviceType(obj
												.optInt(JVDeviceConst.JK_DEVICE_TYPE));
										dev.setNickName(obj
												.optString(JVDeviceConst.JK_DEVICE_NAME));
										dev.setUser(obj
												.optString(JVDeviceConst.JK_DEVICE_VIDEO_USERNAME));
										dev.setPwd(obj
												.optString(JVDeviceConst.JK_DEVICE_VIDEO_PASSWORD));
										dev.setIp(obj
												.optString(JVDeviceConst.JK_DEVICE_VIDEO_IP));
										dev.setPort(obj
												.optInt(JVDeviceConst.JK_DEVICE_VIDEO_PORT));
										if (dev.getIp() == null
												|| "".equals(dev.getIp())
												|| "null".equalsIgnoreCase(dev
														.getIp())) {
											dev.setIsDevice(0);
										} else {
											dev.setIsDevice(1);
										}

										dev.setDeviceType(obj
												.optInt(JVDeviceConst.JK_DEVICE_TYPE));
										dev.setServerState(obj
												.optInt(JVDeviceConst.JK_DEVICE_IM_ONLINE_STATUS));
										deviceList.add(dev);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (null != deviceList && 0 != deviceList.size()) {
			refreshDeviceState(userName, deviceList);
		}
		return deviceList;
	}

	/**
	 * 获取设备通道列表
	 * 
	 * @param dGuid
	 *            设备云视通号
	 * @return ArrayList<ConnPoint> 设备通道列表
	 */
	public static MyList<Channel> getDevicePointList(Device parentDevice,
			String dGuid) {
		MyList<Channel> pointList = new MyList<Channel>(1);
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_DEVICE_CHANNEL);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getDevicePointList---request", jObj.toString());
		byte[] resultStr = new byte[1024 * 30];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);

		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("getDevicePointList---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);

						if (0 != rt) {// 获取失败
							pointList = null;
						} else {// 获取成功
							pointList = new MyList<Channel>(1);
						}
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
						JSONArray plist = new JSONArray(
								temObj.optString(JVDeviceConst.JK_CHANNEL_LIST));
						if (null != plist && 0 != plist.length()) {
							for (int i = 0; i < plist.length(); i++) {
								JSONObject obj = plist.getJSONObject(i);
								if (null != obj) {
									Channel cl = new Channel();
									cl.setParent(parentDevice);
									cl.setChannel(obj
											.optInt(JVDeviceConst.JK_DEVICE_CHANNEL_NO));
									cl.setChannelName(obj
											.optString(JVDeviceConst.JK_DEVICE_CHANNEL_NAME));// BaseApp.UnicodeToString(obj.optString(JVDeviceConst.JK_DEVICE_NAME));
									pointList.add(cl);
								}
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return pointList;
	}

	/**
	 * 修改用户密码
	 * 
	 * @param oldPwd旧密码
	 * @param newPwd新密码
	 * @return
	 */
	public static int modifyUserPassword(String oldPwd, String newPwd) {
		int res = -1;
		res = JVACCOUNT.ModifyUserPassword(oldPwd, newPwd);
		Log.v("modifyUserPassword--", "-----||||||" + res + "");
		return res;
	}

	/**
	 * 修改通道名称
	 * 
	 * @param dGuid
	 *            设备云视通号
	 * @param pNum
	 *            通道号
	 * @param pNewName
	 *            要修改的通道名
	 * @return 修改结果 0：成功 其他：失败
	 */
	public static int modifyPointName(String dGuid, int pNum, String pNewName) {
		int res = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.MODIFY_DEVICE_CHANNEL_NAME);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);
			jObj.put(JVDeviceConst.JK_DEVICE_CHANNEL_NO, pNum);
			jObj.put(JVDeviceConst.JK_DEVICE_CHANNEL_NAME, pNewName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("modifyPointName--request", jObj.toString());
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("modifyPointName---result", result);
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);//
						// (USER_BIND_DEVICE_RESPONSE
						// 2016)
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// (0-正确,其他为错误码)
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	/**
	 * 设备添加通道
	 * 
	 * @param dGuid
	 *            设备云视通号
	 * @param pNum
	 *            通道号
	 * @param pNewName
	 *            要修改的通道名
	 * @return 修改结果 0：成功 其他：失败
	 */
	public static int addPoint(String dGuid, int sum) {
		int res = -1;
		// ArrayList<Channel> pointList = null;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.ADD_DEVICE_CHANNEL);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);
			jObj.put(JVDeviceConst.JK_DEVICE_CHANNEL_SUM, sum);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("addPoint---request", jObj.toString());
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);

		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("addPoint---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);//
						// (USER_BIND_DEVICE_RESPONSE
						// 2016)
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// (0-正确,其他为错误码)
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	/**
	 * 设备删除通道
	 * 
	 * @param dGuid
	 *            设备云视通号
	 * @param pNum
	 *            通道号
	 * @param pNewName
	 *            要修改的通道名
	 * @return 修改结果 0：成功 其他：失败
	 */
	public static int deletePoint(String dGuid, int pNum) {
		int res = -1;
		// ArrayList<Channel> pointList = null;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.DELETE_DEVICE_CHANNEL);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);
			jObj.put(JVDeviceConst.JK_DEVICE_CHANNEL_NO, pNum);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("deletePoint---request", jObj.toString());
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);

		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("deletePoint---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);//
						// (USER_BIND_DEVICE_RESPONSE
						// 2016)
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// (0-正确,其他为错误码)
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	/**
	 * 2014-02-26 根据dguid获取(用户的)单个设备详细信息，包括连接模式信息，暂时用这个接口
	 * 
	 * @param deviceGuid
	 */
	// public static Device getUserDeviceDetail(String deviceGuid,String
	// userName){
	// //参数例子：{"mt":2005,"pv":"1.0","lpt":1,"dguid":"ABCD0002","mid":1}
	// //{"mid":3,"mt":2019,"pv":"1.0","lpt":1,"username":"zhangs","dguid":"ABCD0008"}
	// Device dev = null;
	// JSONObject jObj = new JSONObject();
	// try {
	// jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
	// JVDeviceConst.GET_USER_DEVICE_INFO);
	// jObj.put(JVDeviceConst.JK_PROTO_VERSION, JVDeviceConst.PROTO_VERSION);
	// jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
	// JVDeviceConst.DEV_INFO_PRO);
	// jObj.put(JVDeviceConst.JK_USERNAME, userName);
	// jObj.put(JVDeviceConst.JK_DEVICE_GUID, deviceGuid);
	// } catch (Exception e1) {
	// e1.printStackTrace();
	// }
	//
	// MyLog.v("getDeviceDetail", jObj.toString());
	//
	//
	// //返回值范例：{"mt":2006,"rt":0,"mid":1,"dinfo":{"dtype":2,"dname":"qiqidevice"}}
	//
	// //接收返回数据
	// byte []resultStr = new byte[1024*2];
	// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(jObj.toString(),resultStr);
	// String result = new String(resultStr);
	//
	// // String result =
	// "{\"mt\":2006,\"rt\":0,\"mid\":1,\"dinfo\":{\"dtype\":2,\"dname\":\"qiqidevice\"}}";
	// if(null != result && !"".equalsIgnoreCase(result)){
	// try {
	// JSONObject temObj = new JSONObject(result);
	// if(null != temObj){
	// int mt = temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
	// int rt = temObj.optInt(JVDeviceConst.JK_RESULT);//0正确,其他为错误码
	// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
	// String temStr = temObj.optString(JVDeviceConst.JK_DEVICE_INFO);
	// if(null != temStr && !"".equalsIgnoreCase(temStr)){
	// JSONObject obj = new JSONObject(temStr);
	// if(null != obj){
	// dev = new Device();
	// dev.deviceNum = deviceGuid;
	// dev.deviceType = obj.optInt(JVDeviceConst.JK_DEVICE_TYPE);
	// dev.deviceName = obj.optString(JVDeviceConst.JK_DEVICE_NAME);
	// dev.useWifi = obj.optInt(JVDeviceConst.JK_DEVICE_NET_STATE);
	// dev.netStorageSwitch = obj.optInt(JVDeviceConst.JK_NET_STORAGE_SWITCH);
	// dev.tfStorageSwitch = obj.optInt(JVDeviceConst.JK_TF_STORAGE_SWITCH);
	// dev.alarmSwitch = obj.optInt(JVDeviceConst.JK_ALARM_SWITCH);
	// dev.alarmVideoFtpUrl = obj.optString(JVDeviceConst.JK_ALARM_VIDEO_FTP);
	// dev.alarmSnapFtpUrl = obj.optString(JVDeviceConst.JK_ALARM_SNAP_FTP);
	// dev.alarmFtpAcc = obj.optString(JVDeviceConst.JK_ALARM_FTP_ACC);
	// dev.alarmFtpPwd = obj.optString(JVDeviceConst.JK_ALARM_FTP_PWD);
	// dev.alarmTime = obj.optString(JVDeviceConst.JK_ALARM_TIME);
	// dev.jpegFtpUrlBig = obj.optString(JVDeviceConst.JK_PIC_FTP_BIG);
	// dev.jpegFtpUrlSmall = obj.optString(JVDeviceConst.JK_PIC_FTP_SMALL);
	// dev.jpegFtpAcc = obj.optString(JVDeviceConst.JK_PIC_FTP_ACC);
	// dev.jpegFtpPwd = obj.optString(JVDeviceConst.JK_PIC_FTP_PWD);
	// dev.jpegUploadTiming = obj.optInt(JVDeviceConst.JK_PIC_UPLOAD_TIMEING);
	// dev.videoFluency = obj.optInt(JVDeviceConst.JK_VIDEO_FLUENCY);
	// dev.deviceLoginUser =
	// obj.optString(JVDeviceConst.JK_DEVICE_VIDEO_USERNAME);
	// dev.deviceLoginPwd =
	// obj.optString(JVDeviceConst.JK_DEVICE_VIDEO_PASSWORD);
	// dev.deviceLocalIp = obj.optString(JVDeviceConst.JK_DEVICE_VIDEO_IP);
	// dev.deviceLocalPort = obj.optInt(JVDeviceConst.JK_DEVICE_VIDEO_PORT);
	// dev.babyMode = obj.optInt(JVDeviceConst.JK_DEVICE_BABY_MODE);
	// dev.fullAlarmMode = obj.optInt(JVDeviceConst.JK_DEVICE_FULL_ALARM_MODE);
	// dev.deviceModel = obj.optString(JVDeviceConst.JK_DEVICE_SUB_TYPE);
	// dev.deviceVersion = obj.optString(JVDeviceConst.JK_DEVICE_SOFT_VERSION);
	// dev.deviceVersionNum = obj.optInt(JVDeviceConst.JK_DEVICE_SUB_TYPE_INT);
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	// return dev;
	//
	// }

	/**
	 * 2014-2-20 修改视频连接模式信息业务：
	 */
	public static int editDeviceConnType(Device device, String userName) {
		int res = -1;
		// 请求参数示例
		/**
		 * {"mt":2013, "pv":"1.0", "lpt":1, "dguid":"ABCD0005", "dvlt":1,(0-云视通
		 * 1-IP端口) "dvusername":"henghenghahei", "dvpassword":"henghengpwd",
		 * "dvip":"158.568.65.36", "dvport":7777}
		 */

		// {"username":"refactor","dvlt":1,"mt":2013,"dvpassword":"123",
		// "dvusername":"abc","lpt":1,"pv":"1.0","dvport":9101,"dvip":"192.168.15.4"}
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.MODIFY_DEVICE_INFO_VIDEO_LINK);// mt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, device.getFullNo());// dguid
			jObj.put(JVDeviceConst.JK_VIDEO_LINK_TYPE, device.getIsDevice());// dvlt
			jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_USERNAME, device.getUser());// dvusername
			jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_PASSWORD, device.getPwd());// dvpassword
			jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_IP, device.getIp());// dvip
			jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_PORT, device.getPort());// dvport
			jObj.put(JVDeviceConst.JK_USERNAME, userName);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// {"username":"refactor","dvlt":1,"mt":2013,
		// "dvpassword":"123","dvusername":"abc","lpt":1,"pv":"1.0",
		// "dvport":9101,"dvip":"192.168.1.5"}
		MyLog.v("editDeviceConnType-request", jObj.toString());

		// 返回参数示例
		// {"mt":2014,"rt":0,"mid":1}

		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);

		if (0 == error) {
			String result = new String(resultStr);
			// String result = "{\"mt\":2014,\"rt\":0,\"mid\":1}";
			MyLog.v("editDeviceConnType-result", result);
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);// 0正确,其他为错误码
						res = rt;
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	/**
	 * 用户绑定设备业务
	 */
	public static int addDevice(String loginUserName, Device device) {
		int res = -1;

		// 请求参数示例
		/**
		 * {"mt":2015, "pv":"1.0", "lpt":1, "username":"zhangs",
		 * "dguid":"ABCD0005", "dvusername":"henghenghahei",
		 * "dvpassword":"henghengpwd"}
		 */
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.USER_BIND_DEVICE);// mt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, device.getFullNo());
			jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_USERNAME, device.getUser());
			jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_PASSWORD, device.getPwd());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("addDevice---request", jObj.toString());

		// 返回参数示例
		// {"mt":2016,"rt":0,"mid":1}
		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);

		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("addDevice---result", result);
			// String result = "{\"mt\":2016,\"rt\":0,\"mid\":1}";
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);//
						// (USER_BIND_DEVICE_RESPONSE
						// 2016)
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);// (0-正确,其他为错误码MY_SQL_ERROR,FAILD,PASSWORD_ERROR)
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
						// int drf =
						// temObj.optInt(JVDeviceConst.JK_DEVICE_RESET_FLAG);
						res = rt;
						// res[1] = drf;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	/**
	 * 用户解绑设备业务
	 */
	public static int unbindDevice(String loginUserName, String deviceGuid) {
		int res = -1;

		// 请求参数示例
		/**
		 * {"mt":2017, "pv":"1.0", "lpt":1, "username":"zhangs",
		 * "dguid":"ABCD0005"}
		 */
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.USER_REMOVE_BIND_DEVICE);// mt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, deviceGuid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("unbindDevice---request", jObj.toString());

		// 返回参数示例
		// {"mt":2018,"rt":0,"mid":1}
		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);

		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("unbindDevice---result", result);
			// String result = "{\"mt\":2018,\"rt\":0,\"mid\":1}";
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);//
						// (USER_BIND_DEVICE_RESPONSE
						// 2016)
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);// (0-正确,其他为错误码)
						res = rt;
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	/**
	 * 2014-02-27 刷新设备状态信息
	 * 
	 * @param userName
	 */
	public static boolean refreshDeviceState(String userName,
			ArrayList<Device> deviceList) {
		boolean res = false;
		// ArrayList<Device> deviceList = new ArrayList<Device>();
		// 参数例子：{"mt":2023,"pv":"1.0","lpt":1,"username":"zhangs"}
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_USER_DEVICES_STATUS_INFO);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);
			jObj.put(JVDeviceConst.JK_USERNAME, userName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("refreshDeviceState--request:", jObj.toString());

		// 返回值范例：//
		// {"mt":2024,"rt":0,"mid":8,"dlist":[{"dnst":0,"netss":0,"tfss":0,"aswitch":0,"dsls":1,"dts":"2014-02-27 10:59:40","dtem":22.990875,"dhum":39.074707},{"dnst":1,"netss":1,"tfss":1,"aswitch":1,"dsls":0,"dts":"1900-01-00 00:00:00","dtem":0,"dhum":0},{"dnst":1,"netss":1,"tfss":1,"aswitch":1,"dsls":0,"dts":"1900-01-00 00:00:00","dtem":0,"dhum":0}]}
		// 接收返回数据
		byte[] resultStr = new byte[1024 * 30];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("refreshDeviceState--result:", result);
			// {"username":"aaasss","lpt":1,"pv":"1.0","mt":2025}
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
						if (0 == rt) {
							res = true;
						}
						if (null != temObj
								.optString(JVDeviceConst.JK_DEVICE_LIST)
								&& !"".equalsIgnoreCase(temObj
										.optString(JVDeviceConst.JK_DEVICE_LIST))) {
							JSONArray dlist = new JSONArray(
									temObj.optString(JVDeviceConst.JK_DEVICE_LIST));
							if (null != dlist && 0 != dlist.length()
									&& null != deviceList
									&& 0 != deviceList.size()) {
								boolean[] flag = new boolean[deviceList.size()];
								for (int j = 0; j < flag.length; j++) {
									flag[j] = false;
								}
								for (int i = 0; i < dlist.length(); i++) {
									JSONObject obj = dlist.getJSONObject(i);
									if (null != obj) {// 找到列表中的设备修改设备属性
										if (null != deviceList
												&& 0 != deviceList.size()) {
											for (int k = 0; k < deviceList
													.size(); k++) {
												if (deviceList
														.get(k)
														.getFullNo()
														.equalsIgnoreCase(
																obj.optString(JVDeviceConst.JK_DEVICE_GUID))) {
													Device dev = deviceList
															.get(k);
													dev.setOnlineState(obj
															.optInt(JVDeviceConst.JK_DEVICES_ONLINE_STATUS));// dsls
													dev.setHasWifi(obj
															.optInt(JVDeviceConst.JK_DEVICE_WIFI_FLAG));// dsls
													MyLog.v("刷新:"
															+ dev.getFullNo(),
															"在线状态："
																	+ dev.getOnlineState());
													dev.setDeviceType(obj
															.optInt(JVDeviceConst.JK_DEVICE_TYPE));

													if (2 == dev
															.getDeviceType()) {// 家用设备
														dev.setAlarmSwitch(obj
																.optInt(JVDeviceConst.JK_ALARM_SWITCH));
													}
													dev.setServerState(obj
															.optInt(JVDeviceConst.JK_DEVICE_IM_ONLINE_STATUS));
													flag[k] = true;
												}
											}
										}

									}
								}
							} else {
								if (null == deviceList) {
									deviceList = new ArrayList<Device>();
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	/**
	 * 修改设备信息接口
	 * 
	 * @param userName
	 * @param devGuid
	 * @param devName
	 * @param devUserName
	 * @param devUserPass
	 * @return
	 */
	public static int modifyDevice(String loginUserName, String devYstnum,
			String devName, String devUserName, String devUserPass) {
		int res = -1;
		// 请求参数示例
		/**
		 * "{mid":8, "mt":2007, "pv":"1.0", "lpt":1, "username":"zhangs",
		 * "dguid":"ABCD0001", "dname":"gailename", "dvusername":"dvgailename",
		 * "dvpassword":"dvgailepwd"}
		 */

		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.MODIFY_DEVICE_CONF_INFO);// mt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);// loginUserName
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, devYstnum);// devguid
			jObj.put(JVDeviceConst.JK_DEVICE_NAME, devName);// devname
			jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_USERNAME, devUserName);// devusername
			jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_PASSWORD, devUserPass);// devuserpass
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("modifyDevice---request", jObj.toString());

		// 返回参数示例
		// {"mt":2008,"rt":0,"mid":8}

		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("modifyDevice---result", result);
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);//
						// (USER_BIND_DEVICE_RESPONSE
						// 2016)
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// (0-正确,其他为错误码)
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	/**
	 * 设置出厂设备的用户名密码
	 * 
	 * @param devGuid
	 * @param devUserName
	 * @param devUserPass
	 * @return
	 */
	public static int initDevice(String devYstnum, String devUserName,
			String devUserPass) {
		int res = -1;
		// 请求参数示例
		/**
		 * "{mid":8, "mt":3022, "pv":"1.0", "lpt":7, "dguid":"ABCD0001",
		 * "dvusername":"dvgailename", "dvpassword":"dvgailepwd"}
		 */

		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.PUSH_DEVICE_MODIFY_PASSWORD);// mt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.IM_SERVER_RELAY);// lpt
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, devYstnum);// devguid
			jObj.put(JVDeviceConst.JK_DEVICE_USERNAME, devUserName);// devusername
			jObj.put(JVDeviceConst.JK_DEVICE_PASSWORD, devUserPass);// devuserpass
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("initDevice---request", jObj.toString());

		// 返回参数示例
		// {"mt":2008,"rt":0,"mid":8}

		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServer(
						jObj.toString(), resultStr);

		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("initDevice---result", result);
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);//
						// (USER_BIND_DEVICE_RESPONSE
						// 2016)
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// (0-正确,其他为错误码)
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (0 == res) {
			res = saveDeviceToDB(devYstnum, devUserName, devUserPass);
		}
		return res;
	}

	/**
	 * 将出厂设备的用户名密码写入数据库
	 * 
	 * @param devUserName
	 * @param devUserPass
	 * @return
	 */
	public static int saveDeviceToDB(String devYstnum, String devUserName,
			String devUserPass) {
		int res = -1;
		// 请求参数示例
		/**
		 * "{mid":8, "mt":2035, "pv":"1.0", "lpt":1, "dvusername":"dvgailename",
		 * "dvpassword":"dvgailepwd"}
		 */

		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.MODIFY_DEVICE_PASSWORD);// mt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, devYstnum);// devguid
			jObj.put(JVDeviceConst.JK_DEVICE_USERNAME, devUserName);// devusername
			jObj.put(JVDeviceConst.JK_DEVICE_PASSWORD, devUserPass);// devuserpass
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("saveDeviceToDB---request", jObj.toString());

		// 返回参数示例
		// {"mt":2008,"rt":0,"mid":8}

		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("saveDeviceToDB---result", result);
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);//
						// (USER_BIND_DEVICE_RESPONSE
						// 2016)
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// (0-正确,其他为错误码)
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	/**
	 * 2014-03-10 保存配置（未写到数据库） 告警时段规则：以-分隔 0-23 开始时间,结束时间 比如 12-1 开始时间中午12点
	 * 结束时间半夜1点
	 * 
	 * @param devGuid
	 */
	// public static final int DEVICE_ALARTM_SWITCH = 0; //安全防护
	// public static final int DEVICE_ALARTM_TIME_SWITCH = 1; //防护的时间
	// public static final int DEVICE_TF_SWITCH = 2; //TF卡存储
	// public static final int DEVICE_ALARTM_ANYTIME_SWITCH = 3;//全监控
	// public static final int DEVICE_ALARTM_BABY_SWITCH = 4; //baby模式
	public static int saveSettings(int settingTag, int state,
			String loginUserName, String dGuid) {
		// 参数例子：{"mid":4,"mt":3006,"pv":"1.0","lpt":7,"sid":"5c9995471e60dad582a253feef0b2e98","username":"juyang","dguid":"A228142816","dinfo":{"tfss":0}}
		// {"username":"juyang","lpt":1,"pv":"1.0","dinfo":{"tfss":0},"mt":3006,"dguid":"A228142816"}
		JSONObject paramObj = new JSONObject();
		String saveKey = "";
		int saveValue = state;
		try {
			switch (settingTag) {
			case JVDeviceConst.DEVICE_ALARTM_SWITCH:// 安全防护
				saveKey = JVDeviceConst.JK_ALARM_SWITCH;
				// 安全防护开关
				paramObj.put(JVDeviceConst.JK_ALARM_SWITCH, state);// 0关 1开
				break;
			case JVDeviceConst.DEVICE_ALARTM_TIME_SWITCH:// 防护的时间

				break;
			case JVDeviceConst.DEVICE_TF_SWITCH:// TF卡存储
				saveKey = JVDeviceConst.JK_TF_STORAGE_SWITCH;
				// TF卡开关
				paramObj.put(JVDeviceConst.JK_TF_STORAGE_SWITCH, state);// 0关 1开
				break;
			case JVDeviceConst.DEVICE_ALARTM_ANYTIME_SWITCH:// 全监控
				saveKey = JVDeviceConst.JK_DEVICE_FULL_ALARM_MODE;
				// TF卡开关
				paramObj.put(JVDeviceConst.JK_DEVICE_FULL_ALARM_MODE, state);// 0关
																				// 1开
				break;
			case JVDeviceConst.DEVICE_ALARTM_BABY_SWITCH:// baby模式
				saveKey = JVDeviceConst.JK_DEVICE_BABY_MODE;
				// TF卡开关
				paramObj.put(JVDeviceConst.JK_DEVICE_BABY_MODE, state);// 0关 1开
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		int res = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.PUSH_DEVICE_MODIFY_INFO);// mt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.IM_SERVER_RELAY);// lpt
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);
			jObj.put(JVDeviceConst.JK_DEVICE_INFO, paramObj);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("saveSettings---request", jObj.toString());

		// 返回值范例：
		/**
		 * {"mt":3007,"rt":5,"mid":1}
		 */

		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServer(
						jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("saveSettings---result", result);
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// 0：成功
																		// ，其他失败
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (res == 0) {
			saveToDB(loginUserName, dGuid, saveKey, saveValue);
		}
		return res;
	}

	/**
	 * 2014-03-10 修改昵称 长链接（未写到数据库）
	 * 
	 * @param devGuid
	 */
	// public static final int DEVICE_NICK_NAME_SWITCH = 5; // 修改昵称
	public static int saveSettings(int settingTag, String dName,
			String loginUserName, String dGuid) {
		// 参数例子：{"mid":4,"mt":3006,"pv":"1.0","lpt":7,"sid":"5c9995471e60dad582a253feef0b2e98","username":"juyang","dguid":"A228142816","dinfo":{"tfss":0}}
		// {"username":"juyang","lpt":1,"pv":"1.0","dinfo":{"tfss":0},"mt":3006,"dguid":"A228142816"}
		JSONObject paramObj = new JSONObject();
		String saveKey = "";
		try {
			switch (settingTag) {
			case JVDeviceConst.DEVICE_NICK_NAME_SWITCH:// 修改昵称
				// 安全防护开关
				paramObj.put(JVDeviceConst.JK_DEVICE_NAME, dName);// 0关 1开
				break;
			case JVDeviceConst.DEVICE_ALARTM_TIME_SWITCH:// 防护时间段
				saveKey = JVDeviceConst.JK_ALARM_TIME;
				// 安全防护开关
				paramObj.put(JVDeviceConst.JK_ALARM_TIME, dName);// 0关 1开
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		int res = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.PUSH_DEVICE_MODIFY_INFO);// mt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.IM_SERVER_RELAY);// lpt
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);
			jObj.put(JVDeviceConst.JK_DEVICE_INFO, paramObj);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("saveSettings---request", jObj.toString());

		// 返回值范例：
		/**
		 * {"mt":3007,"rt":5,"mid":1}
		 */

		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServer(
						jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("saveSettings--result--", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// 0：成功
																		// ，其他失败
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (res == 0 && !"".equals(saveKey)) {
			saveToDB(loginUserName, dGuid, saveKey, dName);
		}
		return res;
	}

	/**
	 * 2014-03-10 保存配置（写到数据库）
	 */
	public static int saveToDB(String loginUserName, String dGuid, String key,
			int value) {
		// 参数例子：{"mid":3,"mt":2031,"dguid":"ABCD0008","lpt":1,"pv":"1.0","dnst":1,"tfss":1,"aswitch":1,"atime":"6,18","dpicut":10,"dvfluency":0}
		int res = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.MODIFY_DEVICE_INFO_ADVANCED);// mt
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);// dguid
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(key, value);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("saveToDB1---request", jObj.toString());

		// 返回值范例：
		/**
		 * {"mt":3007,"rt":5,"mid":1}
		 */

		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("saveToDB1---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// 0：成功
																		// ，其他失败
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	/**
	 * 2014-03-10 保存配置（写到数据库）(重写)
	 */
	public static int saveToDB(String loginUserName, String dGuid, String key,
			String value) {
		// 参数例子：{"mid":3,"mt":2031,"dguid":"ABCD0008","lpt":1,"pv":"1.0","dnst":1,"tfss":1,"aswitch":1,"atime":"6,18","dpicut":10,"dvfluency":0}
		int res = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.MODIFY_DEVICE_INFO_ADVANCED);// mt
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);// dguid
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(key, value);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("saveToDB2---request", jObj.toString());

		// 返回值范例：
		/**
		 * {"mt":3007,"rt":5,"mid":1}
		 */

		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("saveToDB2---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// 0：成功
																		// ，其他失败
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	/**
	 * 绑定wifi
	 * 
	 */
	public static int modifyDeviceWifi(String deviceGuid, int hasWifi) {
		int res = -1;

		// 请求参数示例
		/**
		 * {"mt":2017, "pv":"1.0", "lpt":1, "username":"zhangs",
		 * "dguid":"ABCD0005"}
		 */
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.MODIFY_DEVICE_WIFI_FLAG);// mt
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, deviceGuid);
			jObj.put(JVDeviceConst.JK_DEVICE_WIFI_FLAG, hasWifi);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("modifyDeviceWifi---request", jObj.toString());

		// 返回参数示例
		// {"mt":2018,"rt":0,"mid":1}
		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("modifyDeviceWifi---result", result);
			// String result = "{\"mt\":2018,\"rt\":0,\"mid\":1}";
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);//
						// (USER_BIND_DEVICE_RESPONSE
						// 2016)
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// (0-正确,其他为错误码)
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	/**
	 * 添加设备
	 * 
	 * @param device1
	 *            要添加的设备
	 * @return 返回null表示添加失败
	 */
	public static Device addDeviceMethod(boolean local, Device device1,
			String userName) {
		// 用户列表中没有，添加到数据库中，或者提交到服务器
		if (local) {// 本地将数据添加到数据库中
			if ("".equalsIgnoreCase(device1.getNickName())) {
				device1.setNickName(device1.getFullNo());
			}
		} else {// 发请求添加设备
			if ("".equalsIgnoreCase(device1.getNickName())) {
				device1.setNickName(device1.getFullNo());
			}
			int res = DeviceUtil.addDevice(userName, device1);
			if (0 != res) {
				device1 = null;
			}
		}
		return device1;
	}

	/**
	 * 2014-9-5 接口说明：根据设备云视通号，获取此设备的在线状态。
	 * 
	 */
	public static int getDevOnlineState(String deviceGuid) {
		int res = -1;

		// 请求参数示例
		/**
		 * {"mt":2017, "pv":"1.0", "lpt":1, "username":"zhangs",
		 * "dguid":"ABCD0005"}
		 */
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_DEVICE_ONLINE_STATE);// mt 2009
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, deviceGuid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getDevOnlineState---request", jObj.toString());

		// 返回参数示例
		// {"pv":"1.0","lpt":1,"mt":2009,"mid":42,"dguid":"S79808869"}
		// 接收返回数据
		byte[] resultStr = new byte[512];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);
		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("getDevOnlineState---result", result);

			// String result = "{"mt":2010,"rt":0,"mid":42,"dsls":1}";
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);//
						// (USER_BIND_DEVICE_RESPONSE
						// 2016)
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);// 0正确；-10请求格式错误；-1云视通通信失败。
						if (0 == rt) {
							res = temObj
									.optInt(JVDeviceConst.JK_DEVICES_ONLINE_STATUS);// (1在线
																					// 0不在线)
						} else {
							res = rt;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	/**
	 * 2014-10-17 获取演示点设备
	 * 
	 * @param
	 * @return ArrayList<Device> 设备列表
	 */
	public static ArrayList<Device> getDemoDeviceList(String softName) {

		ArrayList<Device> demoList = new ArrayList<Device>();
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_DEMO_POINT);// 2057
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// 1.0
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// 1
			jObj.put(JVDeviceConst.JK_CUSTOM_TYPE, softName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getDemoDeviceList---request", jObj.toString());

		// 接收返回数据
		byte[] resultStr = new byte[1024 * 3];
		int error = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
				jObj.toString(), resultStr);

		if (0 == error) {
			String result = new String(resultStr);
			MyLog.v("getDemoDeviceList---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);// 2058
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);

						if (0 != rt) {// 获取失败
							demoList = null;
						} else {// 获取成功
							demoList = new ArrayList<Device>();
							JSONArray dlist = new JSONArray(
									temObj.optString(JVDeviceConst.JK_DEVICE_LIST));
							if (null != dlist && 0 != dlist.length()) {
								for (int i = 0; i < dlist.length(); i++) {
									JSONObject obj = dlist.getJSONObject(i);
									if (null != obj) {
										String fullNum = obj
												.optString(JVDeviceConst.JK_DEVICE_GUID);
										String gid = ConfigUtil
												.getGroup(fullNum);
										int no = ConfigUtil.getYST(fullNum);
										String user = obj
												.optString(JVDeviceConst.JK_DEVICE_VIDEO_USERNAME);
										String pwd = obj
												.optString(JVDeviceConst.JK_DEVICE_VIDEO_PASSWORD);
										int counts = obj
												.optInt(JVDeviceConst.JK_DEVICE_CHANNEL_SUM);

										Device dev = new Device("", 9101, gid,
												no, user, pwd, true, counts, 1);
										demoList.add(dev);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return demoList;
	}
}
