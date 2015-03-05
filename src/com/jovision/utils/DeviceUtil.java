package com.jovision.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.JVACCOUNT;

import com.jovision.Consts;
import com.jovision.bean.AD;
import com.jovision.bean.APPImage;
import com.jovision.bean.AppVersion;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.bean.OneKeyUpdate;
import com.jovision.bean.SystemInfo;
import com.jovision.bean.WebUrl;
import com.jovision.commons.JVDeviceConst;
import com.jovision.commons.MyList;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;

//JK_MESSAGE_ID和JK_SESSION_ID  库自己添加，应用层不用传。

public class DeviceUtil {
	// 新获取在线状态接口调用流程：
	// 获取用户设备列表（2003）-> 调用新接口获取在线状态 -> 定时调用刷新用户列表(2025)同时调用新接口。
	//
	// 和以前不同，也就是第一次调用完2003后已经获取了全部信息，不必调用用2025。等自动刷新时间到后再调用2025和新接口。

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
					JVDeviceConst.PROTO_VERSION_2);
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
		// byte[] resultStr = new byte[1024 * 30];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getUserDeviceList---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);//

						// if (0 != rt) {// 获取失败
						// deviceList = null;
						// } else

						if (0 == rt || JVDeviceConst.YST_INDEX_SEND_ERROR == rt) {// 获取成功0，-29
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
										dev.setShortConnRes(rt);
										dev.setFullNo(obj
												.optString(JVDeviceConst.JK_DEVICE_GUID));
										dev.setGid(ConfigUtil.getGroup(obj
												.optString(JVDeviceConst.JK_DEVICE_GUID)));
										dev.setNo(ConfigUtil.getYST(obj
												.optString(JVDeviceConst.JK_DEVICE_GUID)));
										// dev.setDeviceType(obj
										// .optInt(JVDeviceConst.JK_DEVICE_TYPE));
										dev.setNickName(obj
												.optString(JVDeviceConst.JK_DEVICE_NAME));
										dev.setUser(obj
												.optString(JVDeviceConst.JK_DEVICE_VIDEO_USERNAME));
										dev.setPwd(obj
												.optString(JVDeviceConst.JK_DEVICE_VIDEO_PASSWORD));
										dev.setIp(obj
												.optString(
														JVDeviceConst.JK_DEVICE_VIDEO_IP)
												.trim());
										dev.setPort(obj
												.optInt(JVDeviceConst.JK_DEVICE_VIDEO_PORT));
										/** 一键升级使用 */
										dev.setDeviceModel(obj
												.optString(JVDeviceConst.JK_DEVICE_SUB_TYPE));// dstype
										dev.setDeviceVerName(obj
												.optString(JVDeviceConst.JK_DEVICE_SOFT_VERSION));// dsv
										dev.setDeviceVerNum(obj
												.optInt(JVDeviceConst.JK_DEVICE_SUB_TYPE_INT));// dstypeint
										if (dev.getIp() == null
												|| "".equals(dev.getIp())
												|| "null".equalsIgnoreCase(dev
														.getIp())) {
											dev.setIsDevice(0);
										} else {
											dev.setIsDevice(1);
										}

										dev.setEnableTcpConnect(Integer.parseInt(obj
												.optString(JVDeviceConst.JK_DEVICE_VIDEO_TCP)));
										dev.setDeviceType(obj
												.optInt(JVDeviceConst.JK_DEVICE_TYPE));
										dev.setServerState(obj
												.optInt(JVDeviceConst.JK_DEVICE_IM_ONLINE_STATUS));
										dev.setOnlineStateNet(obj
												.optInt(JVDeviceConst.JK_DEVICES_ONLINE_STATUS));
										deviceList.add(dev);

										// 同步map,也算是初始化
										CacheUtil.setNickNameWithYstfn(
												dev.getFullNo(),
												dev.getNickName());
									}
								}
							}
						} else {// 获取失败
							deviceList = null;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			return null;
		}

		if (null != deviceList && 0 != deviceList.size()) {
			refreshOnlineState(deviceList);
		}

		return deviceList;
	}

	/**
	 * 2015-1-27 刷新设备在线状态
	 * 
	 * @param deviceList
	 */
	public static void refreshOnlineState(ArrayList<Device> deviceList) {

		// {"dev_array":[{"dguid":"A361","dstat":1},{"dguid":"S23045624","dstat":0}],"ret":0}
		// 优先判断"ret"的值，0成功，其他值失败，为0时再去取"dev_array"的值
		// "dev_array"：是个json array
		// "dguid"：设备云视通号
		// "dstat":在线状态 1：在线 0：离线

		String onLineString = JVACCOUNT.GetDevicesOnlineStatus();
		MyLog.v("refreshOnlineState---result", onLineString);// {"dev_array":null,"ret":-6}
		if (null != onLineString && !"".equalsIgnoreCase(onLineString)) {
			try {
				JSONObject resObject = new JSONObject(onLineString);
				if (null != resObject) {
					int getRes = resObject.getInt("ret");
					if (0 == getRes) {
						String onLineRes = resObject.getString("dev_array");
						if (null != onLineRes
								&& !"".equalsIgnoreCase(onLineRes)) {
							JSONArray resArray = new JSONArray(onLineRes);
							if (null != resArray && 0 != resArray.length()) {
								for (int i = 0; i < resArray.length(); i++) {
									JSONObject devObj = new JSONObject(resArray
											.get(i).toString());
									if (null != devObj) {
										String dGuid = devObj
												.optString("dguid");
										int onlineState = devObj
												.optInt("dstat");
										PlayUtil.refreshDevOnlineState(
												deviceList, dGuid, onlineState);
									}
								}

							}

						}
					} else {
						MyLog.e("refreshOnlineState---result", "error");// {"dev_array":null,"ret":-6}
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		MyLog.v("refreshOnlineState---devList", deviceList.toString());
	}

	/**
	 * 2015-2-4 获取单个设备是否在线
	 * 
	 * @param deviceList
	 */
	public static int getOnlineState(String devFullNo) {
		int devOnline = 1;
		// {"dev_array":[{"dguid":"A361","dstat":1},{"dguid":"S23045624","dstat":0}],"ret":0}
		// 优先判断"ret"的值，0成功，其他值失败，为0时再去取"dev_array"的值
		// "dev_array"：是个json array
		// "dguid"：设备云视通号
		// "dstat":在线状态 1：在线 0：离线

		String onLineString = JVACCOUNT.GetDevicesOnlineStatus();
		MyLog.v("getDevOnlineState---result", onLineString);// {"dev_array":null,"ret":-6}
		if (null != onLineString && !"".equalsIgnoreCase(onLineString)) {
			try {
				JSONObject resObject = new JSONObject(onLineString);
				if (null != resObject) {
					int getRes = resObject.getInt("ret");
					if (0 == getRes) {
						String onLineRes = resObject.getString("dev_array");
						if (null != onLineRes
								&& !"".equalsIgnoreCase(onLineRes)) {
							JSONArray resArray = new JSONArray(onLineRes);
							if (null != resArray && 0 != resArray.length()) {
								for (int i = 0; i < resArray.length(); i++) {
									JSONObject devObj = new JSONObject(resArray
											.get(i).toString());
									if (null != devObj) {
										String dGuid = devObj
												.optString("dguid");
										if (devFullNo.equalsIgnoreCase(dGuid)) {
											devOnline = devObj.optInt("dstat");
											break;
										}

									}
								}

							}

						}
					} else {
						MyLog.e("getDevOnlineState---result", "error");// {"dev_array":null,"ret":-6}
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return devOnline;
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
		// byte[] resultStr = new byte[1024 * 30];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		MyLog.v("modifyUserPassword--", "-----||||||" + res + "");
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
	public static Device getUserDeviceDetail(Device dev, String userName) {
		// 参数例子：{"mt":2005,"pv":"1.0","lpt":1,"dguid":"ABCD0002","mid":1}
		// {"mid":3,"mt":2019,"pv":"1.0","lpt":1,"username":"zhangs","dguid":"ABCD0008"}
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_USER_DEVICE_INFO);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);
			jObj.put(JVDeviceConst.JK_USERNAME, userName);
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dev.getFullNo());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		MyLog.v("getDeviceDetail---before", dev.toString());

		MyLog.v("getDeviceDetail---request", jObj.toString());

		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 2];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getDeviceDetail---result", result);
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						int mt = temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);// 0正确,其他为错误码
						if (0 == rt) {
							int mid = temObj
									.optInt(JVDeviceConst.JK_MESSAGE_ID);
							String temStr = temObj
									.optString(JVDeviceConst.JK_DEVICE_INFO);
							if (null != temStr && !"".equalsIgnoreCase(temStr)) {
								JSONObject obj = new JSONObject(temStr);
								if (null != obj) {
									dev.setNickName(obj
											.optString(JVDeviceConst.JK_DEVICE_NAME));
									dev.setDeviceType(obj
											.optInt(JVDeviceConst.JK_DEVICE_TYPE));
									dev.setUser(obj
											.optString(JVDeviceConst.JK_DEVICE_VIDEO_USERNAME));
									dev.setPwd(obj
											.optString(JVDeviceConst.JK_DEVICE_VIDEO_PASSWORD));
									dev.setIp(obj.optString(
											JVDeviceConst.JK_DEVICE_VIDEO_IP)
											.trim());
									dev.setPort(obj
											.optInt(JVDeviceConst.JK_DEVICE_VIDEO_PORT));
									/** 一键升级使用 */
									dev.setDeviceModel(obj
											.optString(JVDeviceConst.JK_DEVICE_SUB_TYPE));// dstype
									dev.setDeviceVerName(obj
											.optString(JVDeviceConst.JK_DEVICE_SOFT_VERSION));// dsv
									dev.setDeviceVerNum(obj
											.optInt(JVDeviceConst.JK_DEVICE_SUB_TYPE_INT));// dstypeint
									if (dev.getIp() == null
											|| "".equals(dev.getIp())
											|| "null".equalsIgnoreCase(dev
													.getIp())) {
										dev.setIsDevice(0);
									} else {
										dev.setIsDevice(1);
									}

									if (2 == dev.getDeviceType()) {// 家用设备
										dev.setAlarmSwitch(obj
												.optInt(JVDeviceConst.JK_ALARM_SWITCH));
									}
								}
							}
						} else {
							dev = null;
						}

					}
				} catch (Exception e) {
					dev = null;
					e.printStackTrace();
				}

			}
		} else {
			dev = null;
		}
		if (null != dev) {
			MyList<Channel> channelList = getDevicePointList(dev, dev.getGid());
			if (null != channelList && 0 != channelList.size()) {
				dev.setChannelList(channelList);
			}
		}

		return dev;
	}

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

			if (1 == device.getIsDevice()) {// IP
				jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_IP, device.getIp());// dvip
				jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_PORT, device.getPort());// dvport
				jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_TCP,
						device.getEnableTcpConnect());// dvport
			} else if (0 == device.getIsDevice()) {// 云视通
				jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_IP, "");// dvip
				jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_PORT, 0);// dvport
			}
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
	public static Device addDevice1(String loginUserName, Device device) {
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		Device dev = null;
		if (0 == res) {
			dev = getUserDeviceDetail(device, loginUserName);
			// tong bu map by lkp
			CacheUtil.setNickNameWithYstfn(device.getFullNo(),
					dev.getNickName());
		}
		return dev;
	}

	/**
	 * 用户绑定设备业务
	 */
	public static Device addDevice2(Device device, String userName,
			String nickName) {

		int res = -1;
		MyLog.v("addDevice2---before", device.toString());
		// 请求参数示例
		/**
		 * {"dvpassword":"","dvusername":"admin","lpt":1,"pv":"2.0",
		 * "mt":2015,"dguid":"S230829711","mid":12,"sid":"sidtest","dcs":2}
		 */
		// JK_MESSAGE_TYPE : <int> , (USER_BIND_DEVICE 2015)
		// JK_PROTO_VERSION : <string> , (2.0)
		// JK_LOGIC_PROCESS_TYPE : <int> , (DEV_INFO_PRO 1)
		// JK_DEVICE_GUID : <string> ,
		// JK_DEVICE_VIDEO_USERNAME: <string> ,
		// JK_DEVICE_VIDEO_PASSWORD: <string> , (base64加密)
		// JK_DEVICE_CHANNEL_SUM : <int> , (通道数)

		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.USER_BIND_DEVICE);// mt 2015
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION_2);// pv 2.0
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt 1
			if (null != nickName && !"".equals(nickName)) {
				jObj.put(JVDeviceConst.JK_DEVICE_NAME, nickName);
			}
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, device.getFullNo());
			jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_USERNAME, device.getUser());
			jObj.put(JVDeviceConst.JK_DEVICE_VIDEO_PASSWORD, device.getPwd());// (服务端base64加密)
			jObj.put(JVDeviceConst.JK_DEVICE_CHANNEL_SUM, device
					.getChannelList().size());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("addDevice2---request", jObj.toString());

		// 返回参数示例
		// JK_MESSAGE_ID : <int> ,
		// JK_MESSAGE_TYPE : <int> , (USER_BIND_DEVICE_RESPONSE 2016)
		// JK_RESULT : <int> , (0-正确,其他为错误码
		// JSON_PARSE_ERROR(-10)：请求协议格式不正确；SESSION_NOT_EXSIT(5)：session
		// id不存在；MY_SQL_ERROR(-4)：MySQL操作失败；
		// DEVICE_CHANNEL_LIMIT(18)：超过最大允许通道数量；-1:其他错误)
		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 3];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("addDevice3---result", result);
			// String result = "{\"mt\":2016,\"rt\":0,\"mid\":1}";
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// (0-正确,其他为错误码 JSON_PARSE_ERROR(-10)：请求协议格式不正确；
						// SESSION_NOT_EXSIT(5)：session id不存在；
						// MY_SQL_ERROR(-4)：MySQL操作失败；
						// DEVICE_CHANNEL_LIMIT(18)：超过最大允许通道数量；-1:其他错误)
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);
						if (0 == rt) {
							String devStr = temObj
									.optString(JVDeviceConst.JK_DEVICE_INFO);
							JSONObject devObj = new JSONObject(devStr);
							device.setNickName(devObj
									.optString(JVDeviceConst.JK_DEVICE_NAME));
							device.setDeviceType(devObj
									.optInt(JVDeviceConst.JK_DEVICE_TYPE));
							device.setIsDevice(devObj
									.optInt(JVDeviceConst.JK_VIDEO_LINK_TYPE));
							device.setDeviceModel(devObj
									.optString(JVDeviceConst.JK_DEVICE_SUB_TYPE));
							device.setDeviceVerNum(devObj
									.optInt(JVDeviceConst.JK_DEVICE_SUB_TYPE_INT));
							device.setDeviceVerName(devObj
									.optString(JVDeviceConst.JK_DEVICE_SOFT_VERSION));
							device.setUser(devObj
									.optString(JVDeviceConst.JK_DEVICE_VIDEO_USERNAME));
							device.setPwd(devObj
									.optString(JVDeviceConst.JK_DEVICE_VIDEO_PASSWORD));
							device.setIp(devObj
									.optString(JVDeviceConst.JK_DEVICE_VIDEO_IP));
							device.setPort(devObj
									.optInt(JVDeviceConst.JK_DEVICE_VIDEO_PORT));
							device.setAlarmSwitch(devObj
									.optInt(JVDeviceConst.JK_ALARM_SWITCH));
							device.setOnlineStateNet(devObj
									.optInt(JVDeviceConst.JK_DEVICES_ONLINE_STATUS));
							MyLog.v("online-tag-3", device.getFullNo() + "--"
									+ device.getOnlineStateNet());
							device.setServerState(devObj
									.optInt(JVDeviceConst.JK_DEVICE_IM_ONLINE_STATUS));
							device.setHasWifi(devObj
									.optInt(JVDeviceConst.JK_DEVICE_WIFI_FLAG));// dsls
						} else {
							device = DeviceUtil.getUserDeviceDetail(device,
									userName);

						}
					}
				} catch (Exception e) {
					device = DeviceUtil.getUserDeviceDetail(device, userName);
					e.printStackTrace();
				}
			}
		} else {
			device = DeviceUtil.getUserDeviceDetail(device, userName);
		}
		// tong bu map by lkp
		try {
			if (null != device) {
				// 再取一下设备的在线状态
				int onlineState = getOnlineState(device.getFullNo());
				device.setOnlineStateNet(onlineState);
				CacheUtil.setNickNameWithYstfn(device.getFullNo(),
						device.getNickName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return device;
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		// 参数例子：{"mt":2025,"pv":"1.0","lpt":1,"username":"zhangs"}
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_USER_DEVICES_STATUS_INFO);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION_2);
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
		// byte[] resultStr = new byte[1024 * 30];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
						if (0 == rt || JVDeviceConst.YST_INDEX_SEND_ERROR == rt) {// 获取成功0，-29
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

													dev.setOnlineStateNet(obj
															.optInt(JVDeviceConst.JK_DEVICES_ONLINE_STATUS));// dsls
													MyLog.v("online-tag-2",
															dev.getFullNo()
																	+ "--"
																	+ dev.getOnlineStateNet());
													dev.setHasWifi(obj
															.optInt(JVDeviceConst.JK_DEVICE_WIFI_FLAG));// dsls

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

													// 同步map
													CacheUtil
															.setNickNameWithYstfn(
																	dev.getFullNo(),
																	dev.getNickName());
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

		if (res) {
			if (null != deviceList && 0 != deviceList.size()) {
				refreshOnlineState(deviceList);
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		// byte[] resultStr = new byte[512];
		// int error = JVACCOUNT
		// .GetResponseByRequestDevicePersistConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		// byte[] resultStr = new byte[512];
		// int error = JVACCOUNT
		// .GetResponseByRequestDevicePersistConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		// byte[] resultStr = new byte[512];
		// int error = JVACCOUNT
		// .GetResponseByRequestDevicePersistConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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

	// /**
	// * 添加设备
	// *
	// * @param device1
	// * 要添加的设备
	// * @return 返回null表示添加失败
	// */
	// public static Device addDeviceMethod(boolean local, Device device1,
	// String userName) {
	// // 用户列表中没有，添加到数据库中，或者提交到服务器
	// if (local) {// 本地将数据添加到数据库中
	// if ("".equalsIgnoreCase(device1.getNickName())) {
	// device1.setNickName(device1.getFullNo());
	// }
	// } else {// 发请求添加设备
	// if ("".equalsIgnoreCase(device1.getNickName())) {
	// device1.setNickName(device1.getFullNo());
	// }
	// int res = DeviceUtil.addDevice(userName, device1);
	// if (0 != res) {
	// device1 = null;
	// }
	// }
	// return device1;
	// }

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
		// byte[] resultStr = new byte[512];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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

	private static String urls[] = { "rtmp://119.188.172.3/live/a366_1",
			"rtmp://119.188.172.3/live/a361_1",
			"rtmp://119.188.172.3/live/a362_1",
			"rtmp://119.188.172.3/live/s230348788_1",
			"rtmp://119.188.172.3/live/a367_1",
			"rtmp://119.188.172.3/live/a368_1", };

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
		// byte[] resultStr = new byte[1024 * 3];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
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
												.optString(JVDeviceConst.JK_DEVICE_DEMO_USERNAME);
										String pwd = obj
												.optString(JVDeviceConst.JK_DEVICE_DEMO_PASSWORD);
										int counts = obj
												.optInt(JVDeviceConst.JK_DEVICE_CHANNEL_SUM);
										Device dev = new Device("", 9101, gid,
												no, user, pwd, true, counts, 1);
										dev.setUser(user);
										dev.setPwd(pwd);

										for (int j = 0; j < counts; j++) {
											Channel channel = dev
													.getChannelList().toList()
													.get(j);
											int vipLevel = obj
													.optInt(JVDeviceConst.JK_STREAMING_MEDIA_FLAG);// (是否支持流媒体
											// 0不支持
											// 1支持)
											channel.setVipLevel(vipLevel);
											if (vipLevel > 0) {
												String smsrv = obj
														.optString(JVDeviceConst.JK_STREAMING_MEDIA_SERVER);// (流媒体服务器信息
												// 格式
												// ip|rtmp端口|hls端口)
												String suffixRtmp = dev
														.getFullNo()
														+ "_"
														+ (j + 1);
												String rtmpUrl = "";
												if (!"".equals(smsrv)) {
													String array[] = smsrv
															.split("\\|");
													rtmpUrl = "rtmp://"
															+ array[0] + ":"
															+ array[1]
															+ "/live/"
															+ suffixRtmp;
												} else {
													rtmpUrl = "rtmp://"
															+ "/live/"
															+ suffixRtmp;
												}
												MyLog.i("RTMP", "rtmp_url:"
														+ rtmpUrl);
												channel.setRtmpUrl(rtmpUrl);
											}

										}
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
		MyLog.v("demoList", demoList.toString());
		return demoList;
	}

	/**
	 * 2015-1-4 获取演示点网页地址
	 * 
	 * (2015-01-30 暂停使用此接口)
	 * 
	 * @param
	 * @return ArrayList<Device> 设备列表
	 */
	public static String getDemoDeviceList2(String softName) {
		String demoUrl = "";
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_DEMO_POINT_SERVER);// 2059
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// 1.0
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// 1
			jObj.put(JVDeviceConst.JK_CUSTOM_TYPE, softName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getDemoDeviceList2---request", jObj.toString());

		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 3];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getDemoDeviceList2---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);// 2060
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);

						if (0 != rt) {// 获取失败

						} else {// 获取成功
							demoUrl = temObj
									.optString(JVDeviceConst.JK_DEMO_POINT_SERVER);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		MyLog.v("demoUrl", demoUrl);
		return demoUrl;
	}

	/**
	 * 2015-1-7 获取系统消息接口
	 * 
	 * @param
	 * @return ArrayList<Device> 设备列表
	 */
	public static int getSystemInfoList(int language, int startIndex,
			int count, ArrayList<SystemInfo> infoList) {
		int getRes = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.AD_PUBLISH_PROCESS);// 12
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_PUBLISH_INFO);// 5504
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// 1.0
			jObj.put(JVDeviceConst.JK_PRODUCT_TYPE, Consts.PRODUCT_TYPE);// 0：CloudSEE//
			// 1：NVSIP
			jObj.put(JVDeviceConst.JK_LANGUAGE_TYPE, language);// (语言 0简体中文 1英文
			// 2繁体中文)
			jObj.put(JVDeviceConst.JK_PUB_INDEX_START, startIndex);// (获取信息的起始索引，从0开始)
			jObj.put(JVDeviceConst.JK_PUB_COUNT, count);// (获取信息的个数)

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getSystemInfoList---request", jObj.toString());

		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 5];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());
		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getSystemInfoList---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						getRes = temObj.optInt(JVDeviceConst.JK_RESULT);// (0正确,其他为错误码
						// -10请求格式错误;
						// -4数据库操作错误;
						// 6查询为空)
						if (0 != getRes) {// 获取失败
							infoList = null;
						} else {// 获取成功
							JSONArray infoArray = new JSONArray(
									temObj.optString(JVDeviceConst.JK_PUB_LIST));
							if (null != infoArray && 0 != infoArray.length()) {
								for (int i = 0; i < infoArray.length(); i++) {
									JSONObject obj = infoArray.getJSONObject(i);
									if (null != obj) {
										SystemInfo si = new SystemInfo();
										si.setInfoContent(obj
												.optString(JVDeviceConst.JK_PUB_INFO));
										si.setInfoTime(obj
												.optString(JVDeviceConst.JK_PUB_TIME));
										infoList.add(si);
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
		return getRes;
	}

	/**
	 * 2014-11-20 获取广告信息
	 * 
	 * @param
	 * @return ArrayList<Device> 设备列表
	 */
	public static ArrayList<AD> getADList(int adVersion) {
		ArrayList<AD> adList = new ArrayList<AD>();

		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.AD_PUBLISH_PROCESS);// 12
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE, JVDeviceConst.GET_AD_INFO);//
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// 1.0
			jObj.put(JVDeviceConst.JK_PRODUCT_TYPE, Consts.PRODUCT_TYPE);// 0：CloudSEE
			// 1：NVSIP
			jObj.put(JVDeviceConst.JK_AD_VERSION, adVersion);// (当前广告版本号)
			jObj.put(JVDeviceConst.JK_TERMINAL_TYPE, Consts.TERMINAL_TYPE);// (终端类型
			// 0-未知
			// 1-Android
			// 2-iPhone 3-iPad)
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getAD---request", jObj.toString());

		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 3];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getAD---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);
						int adver = temObj.optInt(JVDeviceConst.JK_AD_VERSION);
						// (0正确,其他为错误码 19没有广告更新; -10请求格式错误; -4数据库操作错误; -1其他错误)
						if (19 == rt) {// 无更新

						} else if (0 == rt) {// 有更新
							JSONArray dlist = new JSONArray(
									temObj.optString(JVDeviceConst.JK_AD_INFO));
							if (null != dlist && 0 != dlist.length()) {
								for (int i = 0; i < dlist.length(); i++) {
									JSONObject obj = dlist.getJSONObject(i);
									if (null != obj) {
										AD ad = new AD();
										ad.setIndex(obj
												.getInt(JVDeviceConst.JK_AD_NO));
										ad.setAdImgUrlCh(obj
												.getString(JVDeviceConst.JK_AD_URL));
										ad.setAdLinkCh(obj
												.getString(JVDeviceConst.JK_AD_LINK));
										ad.setAdImgUrlEn(obj
												.getString(JVDeviceConst.JK_AD_URL_EN));
										ad.setAdLinkEn(obj
												.getString(JVDeviceConst.JK_AD_LINK_EN));
										ad.setAdImgUrlZht(obj
												.getString(JVDeviceConst.JK_AD_URL_ZHT));
										ad.setAdLinkZht(obj
												.getString(JVDeviceConst.JK_AD_LINK_ZHT));
										ad.setAdDesp(obj
												.getString(JVDeviceConst.JK_AD_DESP));
										ad.setVersion(adver);
										adList.add(ad);
									}
								}
							}
						} else {// 错误
							adList = null;
						}
					}
				} catch (Exception e) {
					adList = null;
					e.printStackTrace();
				}
			}
		} else {
			adList = null;
		}
		return adList;
	}

	/**
	 * 2015-1-5 获取欢迎界面图片
	 * 
	 * @param
	 * @return ArrayList<Device> 设备列表
	 */
	public static APPImage getAPPImage(int appVersion) {
		APPImage appImage = new APPImage();
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.AD_PUBLISH_PROCESS);// 12
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE, JVDeviceConst.GET_PORTAL);// 5502
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// 1.0
			jObj.put(JVDeviceConst.JK_PRODUCT_TYPE, Consts.PRODUCT_TYPE);// 0：CloudSEE
			// 1：NVSIP
			jObj.put(JVDeviceConst.JK_PORTAL_VERSION, appVersion);// (当前广告版本号)
			jObj.put(JVDeviceConst.JK_TERMINAL_TYPE, Consts.TERMINAL_TYPE);// (终端类型
			// 0-未知
			// 1-Android
			// 2-iPhone 3-iPad)
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getAPPImage---request", jObj.toString());

		// 接收返回数据
		// byte[] resultStr = new byte[1024];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getAPPImage---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);
						appImage.setResult(rt);

						// (0正确,其他为错误码 19没有更新; -10请求格式错误; -4数据库操作错误; -1其他错误)
						if (19 == rt) {// 无更新

						} else if (0 == rt) {// 有更新
							int appVer = temObj
									.optInt(JVDeviceConst.JK_PORTAL_VERSION);
							String urlZh = temObj
									.optString(JVDeviceConst.JK_PORTAL);
							String urlEn = temObj
									.optString(JVDeviceConst.JK_PORTAL_EN);
							String urlZht = temObj
									.optString(JVDeviceConst.JK_PORTAL_ZHT);
							appImage.setVersion(appVer);
							appImage.setAppImageUrlZh(urlZh);
							appImage.setAppImageUrlEN(urlEn);
							appImage.setAppImageUrlZht(urlZht);
						}
					}
				} catch (Exception e) {
					appImage = null;
					e.printStackTrace();
				}
			}
		} else {
			appImage = null;
		}
		return appImage;
	}

	/**
	 * 获取用户通道列表
	 * 
	 * @param dGuid
	 *            设备云视通号
	 * @return ArrayList<ConnPoint> 设备通道列表
	 */
	public static ArrayList<Channel> getUserPointList() {
		ArrayList<Channel> channelList = new ArrayList<Channel>();
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_USER_CHANNELS);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);
			// jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getUserPointList---request", jObj.toString());
		// byte[] resultStr = new byte[1024 * 30];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getUserPointList---result", result);

			// {"mt":2050,"rt":0,"mid":9,"clist":
			// [{"dguid":"A361","dcn":1,"dcname":"A361_1"},
			// {"dguid":"A361","dcn":2,"dcname":"A361_2"},
			// {"dguid":"A361","dcn":3,"dcname":"A361_3"},
			// {"dguid":"A361","dcn":4,"dcname":"A361_4"},
			// {"dguid":"A362","dcn":1,"dcname":"A362_1"},
			// {"dguid":"A362","dcn":2,"dcname":"A362_2"},
			// {"dguid":"A362","dcn":3,"dcname":"A362_3"},
			// {"dguid":"A362","dcn":4,"dcname":"A362_4"},
			// {"dguid":"S52942216","dcn":1,"dcname":"S52942216_1"},
			// {"dguid":"S53530352","dcn":1,"dcname":"S53530352_1"},
			// {"dguid":"S56658","dcn":1,"dcname":"S56658_1"}]}

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);

						if (0 != rt) {// 获取失败
							channelList = null;
						} else {// 获取成功
							channelList = new ArrayList<Channel>();
						}
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
						JSONArray plist = new JSONArray(
								temObj.optString(JVDeviceConst.JK_CHANNEL_LIST));
						if (null != plist && 0 != plist.length()) {
							for (int i = 0; i < plist.length(); i++) {
								JSONObject obj = plist.getJSONObject(i);
								if (null != obj) {
									Channel cl = new Channel();
									cl.setDguid(obj
											.optString(JVDeviceConst.JK_DEVICE_GUID));
									cl.setChannel(obj
											.optInt(JVDeviceConst.JK_DEVICE_CHANNEL_NO));
									cl.setChannelName(obj
											.optString(JVDeviceConst.JK_DEVICE_CHANNEL_NAME));// BaseApp.UnicodeToString(obj.optString(JVDeviceConst.JK_DEVICE_NAME));

									int vipLevel = obj
											.optInt(JVDeviceConst.JK_STREAMING_MEDIA_FLAG);// (是否支持流媒体
									// 0不支持
									// 1支持)
									cl.setVipLevel(vipLevel);
									if (vipLevel > 0) {
										String smsrv = obj
												.optString(JVDeviceConst.JK_STREAMING_MEDIA_SERVER);// (流媒体服务器信息
										// 格式
										// ip|rtmp端口|hls端口)
										String suffixRtmp = obj
												.optString(JVDeviceConst.JK_DEVICE_GUID)
												+ "_"
												+ obj.optInt(JVDeviceConst.JK_DEVICE_CHANNEL_NO);
										String rtmpUrl = "";
										if (!"".equals(smsrv)) {
											String array[] = smsrv.split("\\|");
											rtmpUrl = "rtmp://" + array[0]
													+ ":" + array[1] + "/live/"
													+ suffixRtmp;
										} else {
											rtmpUrl = "rtmp://" + "/live/"
													+ suffixRtmp;
										}
										MyLog.i("RTMP", "rtmp_url:" + rtmpUrl);
										cl.setRtmpUrl(rtmpUrl);
									}
									channelList.add(cl);
								}
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return channelList;
	}

	/**
	 * 获取用户通道列表V2,使用了V2版设备短连接接口，该函数已测试
	 * 
	 * @param dGuid
	 *            设备云视通号
	 * @return ArrayList<ConnPoint> 设备通道列表
	 */
	public static ArrayList<Channel> getUserPointListV2() {
		ArrayList<Channel> channelList = new ArrayList<Channel>();
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_USER_CHANNELS);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);
			// jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getUserPointList---request", jObj.toString());
		// byte[] resultStr = new byte[1024 * 30];
		String strResp;
		strResp = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());
		MyLog.v("getUserPointList---result json", strResp);
		JSONObject respObject = null;
		try {
			respObject = new JSONObject(strResp);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int error = respObject.optInt("result", -1);
		if (0 == error) {

			// {"mt":2050,"rt":0,"mid":9,"clist":
			// [{"dguid":"A361","dcn":1,"dcname":"A361_1"},
			// {"dguid":"A361","dcn":2,"dcname":"A361_2"},
			// {"dguid":"A361","dcn":3,"dcname":"A361_3"},
			// {"dguid":"A361","dcn":4,"dcname":"A361_4"},
			// {"dguid":"A362","dcn":1,"dcname":"A362_1"},
			// {"dguid":"A362","dcn":2,"dcname":"A362_2"},
			// {"dguid":"A362","dcn":3,"dcname":"A362_3"},
			// {"dguid":"A362","dcn":4,"dcname":"A362_4"},
			// {"dguid":"S52942216","dcn":1,"dcname":"S52942216_1"},
			// {"dguid":"S53530352","dcn":1,"dcname":"S53530352_1"},
			// {"dguid":"S56658","dcn":1,"dcname":"S56658_1"}]}

			String result = respObject.optString("resp", "");
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mt =
						// temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);
						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);

						if (0 != rt) {// 获取失败
							channelList = null;
						} else {// 获取成功
							channelList = new ArrayList<Channel>();
						}
						// int mid = temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);
						JSONArray plist = new JSONArray(
								temObj.optString(JVDeviceConst.JK_CHANNEL_LIST));
						if (null != plist && 0 != plist.length()) {
							for (int i = 0; i < plist.length(); i++) {
								JSONObject obj = plist.getJSONObject(i);
								if (null != obj) {
									Channel cl = new Channel();
									cl.setDguid(obj
											.optString(JVDeviceConst.JK_DEVICE_GUID));
									cl.setChannel(obj
											.optInt(JVDeviceConst.JK_DEVICE_CHANNEL_NO));
									cl.setChannelName(obj
											.optString(JVDeviceConst.JK_DEVICE_CHANNEL_NAME));// BaseApp.UnicodeToString(obj.optString(JVDeviceConst.JK_DEVICE_NAME));

									int vipLevel = obj
											.optInt(JVDeviceConst.JK_STREAMING_MEDIA_FLAG);// (是否支持流媒体
									// 0不支持
									// 1支持)
									cl.setVipLevel(vipLevel);
									if (vipLevel > 0) {
										String smsrv = obj
												.optString(JVDeviceConst.JK_STREAMING_MEDIA_SERVER);// (流媒体服务器信息
										// 格式
										// ip|rtmp端口|hls端口)
										String suffixRtmp = obj
												.optString(JVDeviceConst.JK_DEVICE_GUID)
												+ "_"
												+ obj.optInt(JVDeviceConst.JK_DEVICE_CHANNEL_NO);
										String rtmpUrl = "";
										if (!"".equals(smsrv)) {
											String array[] = smsrv.split("\\|");
											rtmpUrl = "rtmp://" + array[0]
													+ ":" + array[1] + "/live/"
													+ suffixRtmp;
										} else {
											rtmpUrl = "rtmp://" + "/live/"
													+ suffixRtmp;
										}
										MyLog.i("RTMP", "rtmp_url:" + rtmpUrl);
										cl.setRtmpUrl(rtmpUrl);
									}
									channelList.add(cl);
								}
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return channelList;
	}

	/**
	 * 2014-03-17 1.客户端获取是否有更新信息 lpt :1是短连接， 7是长连接 2013-3-18 cv改成dsv
	 */
	public static OneKeyUpdate checkUpdate(String loginUserName, int dType,
			int dsType, String dsv) {
		// 参数例子：
		// 传设备类型(dtype),设备型号(dstype),设备当前版本(dsv)。
		// {"mid":32,"pv":"1.0","lpt":1,"mt":2033,"dtype":2,"dstype":1,"cv":"v10.1.2"}
		// {"dtype":2,"dstype":1,"username":"juyang","lpt":1,"pv":"1.0","cv":"v10.1.2","mt":2033}
		int res = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv 1.0
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.DEV_INFO_PRO);// lpt 1
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_DEVICE_UPDATE_INFO);// mt 2033
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);// username
			jObj.put(JVDeviceConst.JK_DEVICE_TYPE, dType);// dtype
			jObj.put(JVDeviceConst.JK_DEVICE_SUB_TYPE, dsType);// dstype
			jObj.put(JVDeviceConst.JK_DEVICE_SOFT_VERSION, dsv);// dsv

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("checkUpdate---request", jObj.toString());

		// 返回值范例：
		/**
		 * 返回设备升级文件版本(ufver),升级文件URL(ufurl),文件大小(ufsize),文件checksum(ufc),升级描述(
		 * ufdes)。 {"mt":2034, "rt":0, "mid":32, "ufi":{"ufver":"v10.2.1",
		 * "ufurl"
		 * :"http:\/\/60.216.75.26:9999\/jenkins\/job\/IPC\/ws\/H200a_up.bin",
		 * "ufsize":2161088, "ufc":"", "ufdes":"10.2.1"}} 返回值说明：有更新rt=0,无更新rt=14
		 * (DEVICE_HAS_NO_UPDATE). 其他值为其他错误码
		 */
		// {"mt":2034,"rt":0,"mid":8,"ufi":{"ufver":"v10.2.1","ufurl":"http:\/\/60.216.75.26:9999\/jenkins\/job\/IPC\/ws\/H200a_up.bin","ufsize":2161088,"ufc":"","ufdes":"10.2.1"}}

		// 接收返回数据
		// byte[] resultStr = new byte[1024];
		OneKeyUpdate oku = new OneKeyUpdate();
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("checkUpdate---result", result);
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);// mt
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// rt
						// 返回值说明：有更新rt=0,无更新rt=14
						// (DEVICE_HAS_NO_UPDATE).
						// 其他值为其他错误码
						temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);// mid
						// 32
						oku.setResultCode(res);
						if (0 == res) {// 有更新
							if (null != temObj
									.optString(JVDeviceConst.JK_UPDATE_FILE_INFO)) {
								JSONObject updtateFile = new JSONObject(
										temObj.optString(JVDeviceConst.JK_UPDATE_FILE_INFO));
								if (null != updtateFile) {
									oku.setUfver(updtateFile
											.optString(JVDeviceConst.JK_UPGRADE_FILE_VERSION));
									oku.setUfurl(updtateFile
											.optString(JVDeviceConst.JK_UPGRADE_FILE_URL));
									oku.setUfsize(updtateFile
											.optInt(JVDeviceConst.JK_UPGRADE_FILE_SIZE));
									oku.setUfc(updtateFile
											.optString(JVDeviceConst.JK_UPGRADE_FILE_CHECKSUM));
									oku.setUfdes(updtateFile
											.optString(JVDeviceConst.JK_UPGRADE_FILE_DESCRIPTION));
								}
							}

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return oku;
	}

	// 2.客户端向设备推升级命令
	// PUSH_DEVICE_UPDATE_CMD = 3012,
	// PUSH_DEVICE_UPDATE_CMD_RESPONSE = 3013,
	//
	// 传用户名(username),设备云视通号(guid),更新文件URL(ufurl),文件大小(ufsize),升级文件版本(ufver).
	// {"mid":417,"mt":3012,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816","ufurl":"http://60.216.75.26:9999/jenkins/job/IPC/ws/H200a_up.bin","ufsize":2158448,"ufver":"v10.2.1"}
	// 返回rt
	// {"mt":3013,"rt":0,"mid":417}
	// 返回值说明：成功0，其他为错误码

	/**
	 * 2014-03-17 2.客户端向设备推升级命令
	 */
	public static int pushUpdateCommand(String loginUserName, String dGuid,
			OneKeyUpdate oku) {
		int res = -1;
		if (null == oku) {
			return res;
		}
		// 参数例子：
		// 传用户名(username),设备云视通号(guid),更新文件URL(ufurl),文件大小(ufsize),升级文件版本(ufver).
		/**
		 * {"mid":417, "mt":3012, "pv":"1.0", "lpt":7,
		 * "sid":"cfa5cbdd358b8be3d18b429cbff76067", "username":"zhangtest",
		 * "dguid":"A228142816",
		 * "ufurl":"http://60.216.75.26:9999/jenkins/job/IPC/ws/H200a_up.bin",
		 * "ufsize":2158448, "ufver":"v10.2.1"}
		 */

		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.PUSH_DEVICE_UPDATE_CMD);// mt 3102
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv 1.0
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.IM_SERVER_RELAY);// lpt 7
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);// username
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);// dguid
			jObj.put(JVDeviceConst.JK_UPGRADE_FILE_URL, oku.getUfurl());// ufurl
			jObj.put(JVDeviceConst.JK_UPGRADE_FILE_SIZE, oku.getUfsize());// ufsize
			jObj.put(JVDeviceConst.JK_UPGRADE_FILE_VERSION, oku.getUfver());// ufver

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("pushUpdateCommand---request", jObj.toString());

		// 返回值范例：
		/**
		 * 返回rt {"mt":3013,"rt":0,"mid":417} 返回值说明：成功0，其他为错误码
		 */

		// 接收返回数据
		// byte[] resultStr = new byte[1024];
		// int error = JVACCOUNT
		// .GetResponseByRequestDevicePersistConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("pushUpdateCommand---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);// mt
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// rt
						// 返回值说明：成功0，其他为错误码
						temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);// mid
						// 3013

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	//
	// 3.客户端向设备推取消升级命令（下载过程中可推送）
	// PUSH_DEVICE_CANCEL_CMD = 3014,
	// PUSH_DEVICE_CANCEL_CMD_RESPONSE = 3015,
	//
	// 传用户名(username),设备云视通号(guid)。
	// {"mid":421,"mt":3014,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816"}
	// 返回rt
	// {"mt":3015,"rt":0,"mid":421}
	// 返回值说明：成功0，其他为错误码

	/**
	 * 2014-03-17 3.客户端向设备推取消升级命令（下载过程中可推送）
	 */
	public static int cancelUpdate(String loginUserName, String dGuid) {
		// 参数例子：
		// {"mid":421,"mt":3014,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816"}
		/**
		 * {"mid":421, "mt":3014, "pv":"1.0", "lpt":7,
		 * "sid":"cfa5cbdd358b8be3d18b429cbff76067", "username":"zhangtest",
		 * "dguid":"A228142816"}
		 */

		int res = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.PUSH_DEVICE_CANCEL_CMD);// mt 3014
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv 1.0
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.IM_SERVER_RELAY);// lpt 7
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);// username
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);// dguid
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("cancelUpdate---request", jObj.toString());

		// 返回值范例：
		/**
		 * 返回rt {"mt":3015,"rt":0,"mid":421} 返回值说明：成功0，其他为错误码
		 */

		// 接收返回数据
		// byte[] resultStr = new byte[1024];
		// int error = JVACCOUNT
		// .GetResponseByRequestDevicePersistConnectionServer(
		// jObj.toString(), resultStr);
		//
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("cancelUpdate---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);// mt
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// rt
						// 返回值说明：成功0，其他为错误码
						temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);// mid
						// 3013
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	// 4.向设备获取下载进度
	// GET_UPDATE_DOWNLOAD_STEP = 3016,
	// GET_UPDATE_DOWNLOAD_STEP_RESPONSE = 3017,
	//
	// 传用户名(username),设备云视通号(guid)。
	// {"mid":418,"mt":3016,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816"}
	// 返回rt
	// {"mt":3017,"rt":0,"mid":418}
	// 返回值说明：成功0，其他为错误码
	//

	// ---------3.18
	// 获取下载进度和烧写进度协议的响应 我昨天发的没加进度值。
	//
	// 4.向设备获取下载进度
	// GET_UPDATE_DOWNLOAD_STEP = 3016,
	// GET_UPDATE_DOWNLOAD_STEP_RESPONSE = 3017,
	//
	// 传用户名(username),设备云视通号(guid)。
	// @#1$6{"mid":418,"mt":3016,"pv":"1.0","lpt":7,"sid":"c13229eaeb1a6957f2ae6faf9b55568d","username":"zhangshuai","dguid":"A228142816"}
	// 返回rt 下载进度(udstep)
	// {"mt":3017,"rt":0,"mid":418,"udstep":"60"}
	// 返回值说明：成功0，其他为错误码

	/**
	 * 2014-03-18 4.向设备获取下载进度
	 */
	public static int getDownloadProgress(String loginUserName, String dGuid) {
		// 参数例子：
		// {"mid":421,"mt":3014,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816"}
		/**
		 * 传用户名(username),设备云视通号(guid)。
		 * 
		 * @#1$6{"mid":418, "mt":3016, "pv":"1.0", "lpt":7,
		 *                  "sid":"c13229eaeb1a6957f2ae6faf9b55568d",
		 *                  "username":"zhangshuai", "dguid":"A228142816"}
		 */

		int progress = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_UPDATE_DOWNLOAD_STEP);// mt 3016
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv 1.0
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.IM_SERVER_RELAY);// lpt 7
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);// username
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);// dguid
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("getDownloadProgress---request", jObj.toString());

		// 返回值范例：
		/**
		 * 返回rt 下载进度(udstep) {"mt":3017,"rt":0,"mid":418,"udstep":"60"}
		 * 返回值说明：成功0，其他为错误码
		 */

		// 接收返回数据
		// byte[] resultStr = new byte[1024];
		// int error = JVACCOUNT
		// .GetResponseByRequestDevicePersistConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getDownloadProgress---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);// mt
						int res = temObj.optInt(JVDeviceConst.JK_RESULT);// rt
						// 返回值说明：成功0，其他为错误码
						temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);// mid
						// 3013
						if (0 == res) {
							progress = temObj
									.optInt(JVDeviceConst.JK_UPGRADE_DOWNLOAD_STEP);// udstep
							// 下载进度(udstep)
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return progress;
	}

	// 5.向设备获取烧写进度
	// GET_UPDATE_WRITE_STEP = 3018,
	// GET_UPDATE_WRITE_STEP_RESPONSE = 3019,
	//
	// 传用户名(username),设备云视通号(guid)。
	// {"mid":419,"mt":3018,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816"}
	// 返回rt 烧写进度(uwstep)
	// {"mt":3019,"rt":0,"mid":419,"uwstep":"20"}
	// 返回值说明：成功0，其他为错误码
	// 加上这个 我昨天写文档忘了加了

	/**
	 * 2014-03-18 5.向设备获取烧写进度
	 */
	public static int getUpdateProgress(String loginUserName, String dGuid) {
		// 参数例子：
		// 传用户名(username),设备云视通号(guid)。
		// {"mid":419,"mt":3018,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816"}
		/**
		 * 传用户名(username),设备云视通号(guid)。 {"mid":419, "mt":3018, "pv":"1.0",
		 * "lpt":7, "sid":"cfa5cbdd358b8be3d18b429cbff76067",
		 * "username":"zhangtest", "dguid":"A228142816"}
		 */

		int progress = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.GET_UPDATE_WRITE_STEP);// mt 3018
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv 1.0
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.IM_SERVER_RELAY);// lpt 7
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);// username
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);// dguid
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("getUpdateProgress---request", jObj.toString());

		// 返回值范例：
		/**
		 * 返回rt 烧写进度(uwstep) {"mt":3019,"rt":0,"mid":419,"uwstep":"20"}
		 * 返回值说明：成功0，其他为错误码
		 */

		// 接收返回数据
		// byte[] resultStr = new byte[1024];
		// int error = JVACCOUNT
		// .GetResponseByRequestDevicePersistConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getUpdateProgress---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);// mt
						int res = temObj.optInt(JVDeviceConst.JK_RESULT);// rt
						// 返回值说明：成功0，其他为错误码
						temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);// mid
						// 3019
						if (0 == res) {
							progress = temObj
									.optInt(JVDeviceConst.JK_UPGRADE_WRITE_STEP);// 返回rt
							// 烧写进度(uwstep)
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return progress;
	}

	// -----2014.3.17
	// 5.向设备获取烧写进度
	// GET_UPDATE_WRITE_STEP = 3018,
	// GET_UPDATE_WRITE_STEP_RESPONSE = 3019,
	//
	// 传用户名(username),设备云视通号(guid)。
	// {"mid":419,"mt":3018,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816"}
	// 返回rt
	// {"mt":3019,"rt":0,"mid":419}
	// 返回值说明：成功0，其他为错误码
	//
	//
	// 6.烧写完成后向设备推送重启命令(提示客户点击)
	// PUSH_DEVICE_REBOOT_CMD = 3020,
	// PUSH_DEVICE_REBOOT_CMD_RESPONSE = 3021,
	//
	// 传用户名(username),设备云视通号(guid)。
	// {"mid":420,"mt":3020,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816"}
	//
	// 返回rt
	// {"mt":3021,"rt":0,"mid":420}
	// 返回值说明：成功0，其他为错误码

	/**
	 * 2014-03-17 6.烧写完成后向设备推送重启命令(提示客户点击)
	 */
	public static int pushRestart(String loginUserName, String dGuid) {
		// 参数例子：
		// 传用户名(username),设备云视通号(guid)。
		// {"mid":420,"mt":3020,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816"}
		/**
		 * 传用户名(username),设备云视通号(guid)。 {"mid":420, "mt":3020, "pv":"1.0",
		 * "lpt":7, "sid":"cfa5cbdd358b8be3d18b429cbff76067",
		 * "username":"zhangtest", "dguid":"A228142816"}
		 */

		int res = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.PUSH_DEVICE_REBOOT_CMD);// mt 3020
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv 1.0
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.IM_SERVER_RELAY);// lpt 7
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);// username
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);// dguid
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("pushRestart---request", jObj.toString());

		// 返回值范例：
		/**
		 * 返回rt {"mt":3021,"rt":0,"mid":420} 返回值说明：成功0，其他为错误码
		 */

		// 接收返回数据
		// byte[] resultStr = new byte[1024];
		// int error = JVACCOUNT
		// .GetResponseByRequestDevicePersistConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("pushRestart---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);// mt
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// rt
						// 返回值说明：成功0，其他为错误码
						temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);// mid
						// 3013
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	// 7.获取烧写100后发送升级过程终止命令。或在下载过程中发送终止命令进行终止（烧写过程不可发）。发送完终止命令后再发重启。
	// PUSH_DEVICE_CANCEL_CMD = 3014,
	//
	// PUSH_DEVICE_CANCEL_CMD_RESPONSE = 3015,
	//
	// {"mid":420,"mt":3014,"pv":"1.0","lpt":7,"sid":"cfa5cbdd358b8be3d18b429cbff76067","username":"zhangtest","dguid":"A228142816"}
	// {"mt":3015,"rt":0,"mid":420}

	/**
	 * 2014-03-19 7.获取烧写100后发送升级过程终止命令。或在下载过程中发送终止命令进行终止（烧写过程不可发）。发送完终止命令后再发重启。
	 */
	public static int pushStop(String loginUserName, String dGuid) {
		// 参数例子：
		// 传用户名(username),设备云视通号(guid)。
		/**
		 * {"mid":420, "mt":3014, "pv":"1.0", "lpt":7,
		 * "sid":"cfa5cbdd358b8be3d18b429cbff76067", "username":"zhangtest",
		 * "dguid":"A228142816"}
		 */

		int res = -1;
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.PUSH_DEVICE_CANCEL_CMD);// mt 3014
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// pv 1.0
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.IM_SERVER_RELAY);// lpt 7
			jObj.put(JVDeviceConst.JK_USERNAME, loginUserName);// username
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, dGuid);// dguid
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("pushStop---request", jObj.toString());

		// 返回值范例：
		/**
		 * 返回rt {"mt":3015,"rt":0,"mid":420} 返回值说明：成功0，其他为错误码
		 */

		// 接收返回数据
		// byte[] resultStr = new byte[1024];
		// int error = JVACCOUNT
		// .GetResponseByRequestDevicePersistConnectionServer(
		// jObj.toString(), resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDevicePersistConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("pushStop---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						temObj.optInt(JVDeviceConst.JK_MESSAGE_TYPE);// mt
						// 3015
						res = temObj.optInt(JVDeviceConst.JK_RESULT);// rt
						// 返回值说明：成功0，其他为错误码
						temObj.optInt(JVDeviceConst.JK_MESSAGE_ID);// mid
						// 420
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return res;
	}

	// 获取设备通道的云存储信息

	public static String getDevCloudStorageInfo(String devGuid, int channelID) {
		ArrayList<Device> deviceList = null;
		String strSpKey = String.format(Consts.FORMATTER_CLOUD_DEV, devGuid,
				channelID);
		// {"mid":58,"mt":5208,"pv":"1.0","lpt":13,"sid":"sidtest","dguid":"S224350962","dcn":1}
		JSONObject jObj = new JSONObject();
		String resJson = "";
		try {
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.JK_GET_CLOUD_STORAGE_INFO);
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.VAS_PROCESS);
			jObj.put(JVDeviceConst.JK_DEVICE_GUID, devGuid);
			jObj.put(JVDeviceConst.JK_DEVICE_CHANNEL_NO, channelID);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.e("getDevCloudStorageInfo---request", jObj.toString());

		// byte[] resultStr = new byte[1024 * 10];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// // {"mt":5209,"rt":0,"mid":58,"cshost":"oss-cn-qingdao.aliyuncs.com",
		// //
		// "csid":"zQcheMpdeSdLt2CT","cskey":"Mg6vG2oxacE4WBhOkTobJVdZNYWWyg",
		// // "csspace":"missiletcy","cstype":1}
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getDevCloudStorageInfo---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {

						int rt = temObj.optInt(JVDeviceConst.JK_RESULT);

						if (0 != rt) {// 获取失败
							MySharedPreference.putString(strSpKey, "");
							resJson = temObj.toString();
						} else {// 获取成功
							MySharedPreference.putString(strSpKey,
									temObj.toString());
							resJson = temObj.toString();
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return resJson;
	}

	/**
	 * 软件检查更新
	 * 
	 * @param currentVersion
	 * @param product
	 * @param language
	 * @param platform
	 * @return
	 */
	public static int checkSoftWareUpdate(int currentVersion, String product,
			int language, int platform, AppVersion appVersion) {
		int rt = -1;
		// {"mid":58,"mt":5208,"pv":"1.0","lpt":13,"sid":"sidtest","dguid":"S224350962","dcn":1}
		JSONObject reqJson = new JSONObject();
		String resJson = "";
		try {
			reqJson.put(JVDeviceConst.JK_MESSAGE_TYPE,
					JVDeviceConst.SOFT_UPDATE_REQUEST);// 消息类型
			reqJson.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);
			reqJson.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.UPDATE_PROCESS);// 逻辑进程编号，自动更新为4
			reqJson.put(JVDeviceConst.JK_APP_CURRENT_VERSION, currentVersion);// 当前VersionCode
			reqJson.put(JVDeviceConst.JK_PRODUCT_TYPE, product);// 产品类型
			reqJson.put(JVDeviceConst.JK_LANGUAGE_TYPE, language);// 语言类型(语言
			// 0简体中文
			// 1英文//
			// 2繁体中文)
			reqJson.put(JVDeviceConst.JK_APP_CLIENT_TYPE, platform);// 1:android
			// 2:ios
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.e("checkSoftWareUpdate---request", reqJson.toString());

		// byte[] resultStr = new byte[1024 * 10];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// reqJson.toString(), resultStr);
		// //
		// {"mt":5001,"rt":0,"mid":14,"cfd":62,"cfdid":581,"appver":88,"appfullver":"V4.5.9",
		// // "appverurl":"http://www.baidu.com","appverdesc":"升级信息"}
		//
		// // 执行结果说明
		// // rt==0说明需要升级 19不需要升级 其他值是吧
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(reqJson
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("checkSoftWareUpdate---result", result);

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						rt = temObj.optInt(JVDeviceConst.JK_RESULT);
						if (0 == rt) {// // rt==0说明需要升级 18不需要升级 其他值是吧
							appVersion
									.setVersionName(temObj
											.optString(JVDeviceConst.JK_APP_VERSION_FULL));
							appVersion.setVersionCode(temObj
									.optInt(JVDeviceConst.JK_APP_VERSION));
							appVersion
									.setVersionInfo(temObj
											.optString(JVDeviceConst.JK_APP_VERSION_DESC));
							appVersion
									.setDownloadUrl(temObj
											.optString(JVDeviceConst.JK_APP_VERSION_URL));
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return rt;
	}

	public static int getDeivceIndex(String strYstNum) {
		if (null == strYstNum || strYstNum.equals("")) {
			return -1;
		}
		Device devs = null;
		boolean bfind = false;
		for (int j = 0; j < CacheUtil.getDevList().size(); j++) {
			devs = CacheUtil.getDevList().get(j);
			MyLog.v("Device",
					"dst:" + strYstNum + "---yst-num = " + devs.getFullNo());
			if (strYstNum.equalsIgnoreCase(devs.getFullNo())) {
				bfind = true;
				MyLog.v("New Alarm Dialog", "find dev num " + strYstNum
						+ ", index:" + j);
				return j;
			}
		}
		return -1;
	}

	/**
	 * 获取web URL
	 * 
	 * */
	public static WebUrl getWebUrl() {
		int rt = -1;
		WebUrl webUrl = new WebUrl();
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVDeviceConst.JK_LOGIC_PROCESS_TYPE,
					JVDeviceConst.AD_PUBLISH_PROCESS);// 12
			jObj.put(JVDeviceConst.JK_MESSAGE_TYPE, 5506);//
			jObj.put(JVDeviceConst.JK_PROTO_VERSION,
					JVDeviceConst.PROTO_VERSION);// 1.0
			jObj.put(JVDeviceConst.JK_PRODUCT_TYPE, Consts.PRODUCT_TYPE);// 0：CloudSEE
			// 1：NVSIP
			// 2-iPhone 3-iPad)
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MyLog.v("getWebUrl---request", jObj.toString());
		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 3];
		// int error =
		// JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// MyLog.i("TAG", "<===="+error+",res:"+resultStr);
		// if (0 == error) {
		// String result = new String(resultStr);
		String requesRes = JVACCOUNT
				.GetResponseByRequestDeviceShortConnectionServerV2(jObj
						.toString());

		JSONObject respObject = null;
		try {
			respObject = new JSONObject(requesRes);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int ret = respObject.optInt("result", -1);
		if (ret == 0) {
			String result = respObject.optString("resp", "");
			MyLog.v("getWebUrl---result", result);
			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						rt = temObj.optInt(JVDeviceConst.JK_RESULT);
						if (0 == rt) {
							webUrl.setDemoUrl(temObj.optString("demourl"));
							webUrl.setCustUrl(temObj.optString("custurl"));
							webUrl.setStatUrl(temObj.optString("staturl"));
							webUrl.setBbsUrlString(temObj.optString("bbsurl"));
						} else {
							webUrl = null;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return webUrl;
	}
}
