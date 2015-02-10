package com.jovision.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.JVACCOUNT;

import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.bean.Device;
import com.jovision.bean.PushInfo;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVAlarmConst;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;

public class AlarmUtil {
	final private static int ALARMTAG = 2;

	/**
	 * 3.客户端获取报警关联的图片地址
	 */
	public static String getAlarmPic(String userName, String alarmGuid) {
		String imgUrl = "";
		// {"alarmguid":"5eafa66013e83d6205a9c8553fe4eee2","accountname":"jjjyyy","mid":1001}
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVAlarmConst.JK_ALARM_MID,
					JVAlarmConst.MID_REQUEST_ALARMPICURL);
			jObj.put(JVAlarmConst.JK_ALARM_ACCOUNT, userName);
			jObj.put(JVAlarmConst.JK_ALARM_GUID, alarmGuid);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		MyLog.v("getAlarmPic", jObj.toString());

		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 2];
		// int ret = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
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

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mid = temObj.optInt(JVAlarmConst.JK_ALARM_MID);
						imgUrl = temObj.optString(JVAlarmConst.JK_ALARM_PICURL);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			MyLog.v("getAlarmPic---res", imgUrl);
		}
		return imgUrl;
	}

	/**
	 * 4.客户端获取报警关联的视频地址
	 */
	public static String getAlarmVideo(String userName, String alarmGuid) {
		String videoUrl = "";

		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVAlarmConst.JK_ALARM_MID,
					JVAlarmConst.MID_REQUEST_ALARMVIDEOURL);
			// jObj.put(JVAlarmConst.JK_ALARM_ACCOUNT, userName);
			jObj.put(JVAlarmConst.JK_ALARM_GUID, alarmGuid);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		MyLog.v("getAlarmVideo", jObj.toString());

		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 2];
		// int ret =
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

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mid = temObj.optInt(JVAlarmConst.JK_ALARM_MID);
						videoUrl = temObj
								.optString(JVAlarmConst.JK_ALARM_VIDEOURL);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			MyLog.v("getAlarmVideo---res", videoUrl);
		}

		return videoUrl;
	}

	//
	// /**
	// * 2014-02-27 5.客户端查询报警的历史记录
	// *
	// * @param userName
	// */
	// public static ArrayList<PushInfo> getUserAlarmList(int beginIndex, int
	// count) {
	// ArrayList<PushInfo> pushList = new ArrayList<PushInfo>();
	// // 参数例子：
	// // Request:
	// // {
	// // 　　JK_ALARM_MID : <int> (alarm_client_messageid_t)
	// // 　　JK_ALARM_ACCOUNT:<string>,
	// // 　　JK_ALARM_SEARCHINDEX:<int>,
	// // 　　JK_ALARM_SEARCHCOUNT:<int>
	// // }
	// JSONObject jObj = new JSONObject();
	// try {
	// jObj.put(JVAlarmConst.JK_ALARM_MID,
	// JVAlarmConst.MID_REQUEST_ALARMHISTORY);
	// // jObj.put(JVAlarmConst.JK_ALARM_ACCOUNT, userName);
	// jObj.put(JVAlarmConst.JK_ALARM_SEARCHINDEX, beginIndex);
	// jObj.put(JVAlarmConst.JK_ALARM_SEARCHCOUNT, count);
	// } catch (JSONException e1) {
	// e1.printStackTrace();
	// }
	//
	// MyLog.v("getUserAlarmList", jObj.toString());
	//
	// // 接收返回数据
	// byte[] resultStr = new byte[1024 * 2];
	// JVACCOUNT.GetResponseByRequestShortConnectionServer(ALARMTAG,
	// jObj.toString(), resultStr);
	// String result = new String(resultStr);
	//
	// MyLog.v("getUserAlarmList---result---res", result + "");
	//
	// if (null != result && !"".equalsIgnoreCase(result)) {
	// try {
	// JSONObject temObj = new JSONObject(result);
	// if (null != temObj) {
	// // int mid = temObj.optInt(JVAlarmConst.JK_ALARM_MID);
	// int pushCount = temObj
	// .optInt(JVAlarmConst.JK_ALARM_SEARCHCOUNT);
	// if (pushCount > 0) {
	// JSONArray dlist = new JSONArray(
	// temObj.optString(JVAlarmConst.JK_ALARM_LIST));
	// if (null != dlist && 0 != dlist.length()) {
	// for (int i = 0; i < dlist.length(); i++) {
	// JSONObject obj = dlist.getJSONObject(i);
	// if (null != obj) {
	// PushInfo pi = new PushInfo();
	// pi.strGUID = obj
	// .optString(JVAlarmConst.JK_ALARM_GUID);
	// pi.ystNum = obj
	// .optString(JVAlarmConst.JK_ALARM_CLOUDNUM);
	// pi.coonNum = obj
	// .optInt(JVAlarmConst.JK_ALARM_CLOUDCHN);
	// pi.alarmType = obj
	// .optInt(JVAlarmConst.JK_ALARM_ALARMTYPE);
	// pi.alarmLevel = obj
	// .optInt(JVAlarmConst.JK_ALARM_ALARMLEVEL);// 2014.03.08报警级别：1级最高
	// pi.alarmTime = obj
	// .optString(JVAlarmConst.JK_ALARM_ALARMTIME);
	// MyLog.e("报警ID---strGUID---", pi.strGUID
	// + "");
	// MyLog.e("报警时间---alarmTime---", pi.alarmTime
	// + "");
	// pi.deviceName = obj
	// .optString(JVAlarmConst.JK_ALARM_CLOUDNAME);
	// pi.pic = obj
	// .optString(JVAlarmConst.JK_ALARM_PICURL);
	// pushList.add(pi);
	// }
	// }
	// }
	// }
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// MyLog.v("getUserAlarmList---res", pushList.size() + "");
	// return pushList;
	// }

	/**
	 * 2014-02-27 5.客户端查询报警的历史记录
	 * 
	 * @param userName
	 */
	public static ArrayList<PushInfo> getUserAlarmList(int beginIndex, int count) {
		ArrayList<PushInfo> pushList = null;
		// 参数例子：
		// Request:
		// {
		// 　　JK_ALARM_MID : <int> (alarm_client_messageid_t)
		// 　　JK_ALARM_ACCOUNT:<string>,
		// 　　JK_ALARM_SEARCHINDEX:<int>,
		// 　　JK_ALARM_SEARCHCOUNT:<int>
		// }
		JSONObject jObj = new JSONObject();
		try {
			/**
			 * 老协议查询参数
			 */
			// jObj.put(JVAlarmConst.JK_ALARM_MID,
			// JVAlarmConst.MID_REQUEST_ALARMHISTORY);
			// // jObj.put(JVAlarmConst.JK_ALARM_ACCOUNT, userName);
			// jObj.put(JVAlarmConst.JK_ALARM_SEARCHINDEX, beginIndex);
			// jObj.put(JVAlarmConst.JK_ALARM_SEARCHCOUNT, count);
			/**
			 * 新协议查询参数 查询全部
			 */
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_LPT, 11);
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_MT, 6000);
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_PV, "1.0");
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_AISTART, beginIndex);
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_AISTOP, beginIndex + count
					- 1);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("getUserAlarmList", jObj.toString());

		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 10];
		// int ret = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (ret == 0) {
		//
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

			MyLog.v("getUserAlarmList---result---res", result + "");

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						/**
						 * 老协议报警
						 */
						// int mid = temObj.optInt(JVAlarmConst.JK_ALARM_MID);
						// int pushCount = temObj
						// .optInt(JVAlarmConst.JK_ALARM_SEARCHCOUNT);
						// if (pushCount > 0) {
						// JSONArray dlist = new JSONArray(
						// temObj.optString(JVAlarmConst.JK_ALARM_LIST));
						// if (null != dlist && 0 != dlist.length()) {
						// for (int i = 0; i < dlist.length(); i++) {
						// JSONObject obj = dlist.getJSONObject(i);
						// if (null != obj) {
						// PushInfo pi = new PushInfo();
						// // if(){
						// //
						// // }
						// pi.strGUID = obj
						// .optString(JVAlarmConst.JK_ALARM_GUID);
						// pi.ystNum = obj
						// .optString(JVAlarmConst.JK_ALARM_CLOUDNUM);
						// pi.coonNum = obj
						// .optInt(JVAlarmConst.JK_ALARM_CLOUDCHN);
						// pi.alarmType = obj
						// .optInt(JVAlarmConst.JK_ALARM_ALARMTYPE);
						// pi.alarmLevel = obj
						// .optInt(JVAlarmConst.JK_ALARM_ALARMLEVEL);//
						// 2014.03.08报警级别：1级最高
						// pi.alarmTime = obj
						// .optString(JVAlarmConst.JK_ALARM_ALARMTIME);
						// Log.e("报警ID---strGUID---", pi.strGUID + "");
						// Log.e("报警时间---alarmTime---", pi.alarmTime
						// + "");
						// pi.deviceName = obj
						// .optString(JVAlarmConst.JK_ALARM_CLOUDNAME);
						// pi.pic = obj
						// .optString(JVAlarmConst.JK_ALARM_PICURL);
						// pushList.add(pi);
						// }
						// }
						// }
						// }

						/**
						 * 新协议报警
						 */
						int mid = temObj.optInt(JVAlarmConst.JK_ALARM_MID);
						int rt = temObj.optInt(JVAlarmConst.JK_ALARM_NEW_RT);
						switch (rt) {
						case 0:// 获取数据成功
							pushList = new ArrayList<PushInfo>();
							JSONArray dlist = temObj
									.getJSONArray(JVAlarmConst.JK_ALARM_NEW_AINFO);
							if (null != dlist && 0 != dlist.length()) {
								for (int i = 0; i < dlist.length(); i++) {
									JSONObject obj = dlist.getJSONObject(i);
									if (null != obj) {
										PushInfo pi = new PushInfo();
										// if(){
										//
										// }
										// {"mt":6001,"rt":0,"mid":30,
										// "ainfo":[{"aguid":"bff9ff1eaa772acf47f2d0043990f777",
										// "asln":0,"dguid":"S90252170","dname":"HD IPC","dcn":1,"atype":7,
										// "apic":".\/rec\/00\/20141014\/A01092715.jpg","avd":"1413278835","ats":1413278835},}
										pi.alarmType = obj
												.optInt(JVAlarmConst.JK_ALARM_NEW_ALARMTYPE);
										if (pi.alarmType == 7
												|| pi.alarmType == 4) {
											pi.deviceNickName = obj
													.optString(JVAlarmConst.JK_ALARM_NEW_CLOUDNAME);
										} else if (pi.alarmType == 11)// 第三方
										{
											pi.deviceNickName = obj
													.optString(JVAlarmConst.JK_ALARM_NEW_ALARM_THIRD_NICKNAME);
										} else {

										}

										pi.strGUID = obj
												.optString(JVAlarmConst.JK_ALARM_NEW_GUID);
										pi.ystNum = obj
												.optString(JVAlarmConst.JK_ALARM_NEW_CLOUDNUM);
										pi.coonNum = obj
												.optInt(JVAlarmConst.JK_ALARM_NEW_CLOUDCHN);
										pi.alarmSolution = obj
												.optInt(JVAlarmConst.JK_ALARM_SOLUTION);
										// ArrayList<Device> deviceList =
										// CacheUtil
										// .getDevList();// 再取一次
										// int dev_index = DeviceUtil
										// .getDeivceIndex(pi.ystNum);
										String deviceNickName = CacheUtil
												.getNickNameByYstfn(pi.ystNum);
										if (deviceNickName == null
												|| deviceNickName.equals("")) {
											deviceNickName = pi.deviceNickName;
										} else {
											// deviceNickName = deviceList.get(
											// dev_index).getNickName();
											if (pi.alarmType == 11)// 第三方
											{
												deviceNickName = deviceNickName
														+ "-"
														+ pi.deviceNickName;
											}
										}
										pi.deviceNickName = deviceNickName;
										// pi.alarmLevel = obj
										// .optInt(JVAlarmConst.JK_ALARM_ALARMLEVEL);//
										// 2014.03.08报警级别：1级最高
										// pi.timestamp = obj
										// .optString(JVAlarmConst.JK_ALARM_NEW_ALARMTIME);
										// pi.alarmTime =
										// getStrTime(pi.timestamp);
										// String timeS = obj
										// .optString(JVAlarmConst.JK_ALARM_NEW_ALARM_ATS);
										String strTempTime = "";
										strTempTime = obj
												.optString(JVAlarmConst.JK_ALARM_NEW_ALARMTIME_STR);
										if (!strTempTime.equals("")) {
											// 有限取这个字段的时间，格式：yyyyMMddhhmmss
											pi.timestamp = "";
											pi.alarmTime = AlarmUtil
													.formatStrTime(strTempTime);
										} else {
											pi.timestamp = obj
													.optString(JVAlarmConst.JK_ALARM_NEW_ALARMTIME);
											pi.alarmTime = AlarmUtil
													.getStrTime(pi.timestamp);
										}
										pi.deviceName = obj
												.optString(JVAlarmConst.JK_ALARM_NEW_CLOUDNAME);
										pi.pic = obj
												.optString(JVAlarmConst.JK_ALARM_NEW_PICURL);
										pi.video = obj
												.optString(JVAlarmConst.JK_ALARM_NEW_VIDEOURL);
										pi.messageTag = JVAccountConst.MESSAGE_NEW_PUSH_TAG;
										pushList.add(pi);
									}
								}
							}
							break;
						case -10:// 请求格式错误
							MyLog.e("tags", "请求格式错误");
							break;
						case -3:
							MyLog.e("tags", "缓存操作错误");
							break;
						case -1://
							MyLog.e("tags", "其它错误");
							break;
						default:
							break;
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {

		}
		// Log.v("getUserAlarmList---res", pushList.size() + "");
		return pushList;
	}

	/**
	 * 6.删除报警信息 0成功，1失败
	 */
	public static int deleteAlarmInfo(String userName, String alarmGuid) {
		int deleteRes = -1;

		JSONObject jObj = new JSONObject();
		// try {
		// jObj.put(JVAlarmConst.JK_ALARM_MID,
		// JVAlarmConst.MID_REQUEST_REMOVEALARM);
		// jObj.put(JVAlarmConst.JK_ALARM_ACCOUNT, userName);
		// jObj.put(JVAlarmConst.JK_ALARM_GUID, alarmGuid);
		// } catch (JSONException e1) {
		// e1.printStackTrace();
		// }
		// MyLog.v("deleteAlarmInfo", jObj.toString());
		/**
		 * 新协议传输参数
		 */
		try {
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_LPT, 11);
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_MT, 6002);
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_PV, "1.0");
			jObj.put(JVAlarmConst.JK_ALARM_NEW_GUID, alarmGuid);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("deleteAlarmInfo", jObj.toString());
		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 2];
		//
		// int ret = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (ret == 0) {// 操作成功
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

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						// int mid = temObj.optInt(JVAlarmConst.JK_ALARM_MID);
						// String guid =
						// temObj.optString(JVAlarmConst.JK_ALARM_GUID);
						deleteRes = temObj.optInt(JVAlarmConst.JK_ALARM_NEW_RT);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			deleteRes = -1;
		}

		MyLog.v("deleteAlarmInfo---res", deleteRes + "");
		return deleteRes;
	}

	/**
	 * 7.清空报警信息 0成功，1失败
	 */
	public static int clearAlarmInfo() {
		int clearRes = -1;

		JSONObject jObj = new JSONObject();
		/**
		 * 新协议传输参数
		 */
		try {
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_LPT, 11);
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_MT, 6004);
			jObj.put(JVAlarmConst.JK_ALARM_NEW_ALARM_PV, "1.0");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("Alarm", "clearAlarmInfo:" + jObj.toString());
		// 接收返回数据
		// byte[] resultStr = new byte[1024 * 2];
		//
		// int ret = JVACCOUNT.GetResponseByRequestDeviceShortConnectionServer(
		// jObj.toString(), resultStr);
		// if (ret == 0) {// 操作成功
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

			if (null != result && !"".equalsIgnoreCase(result)) {
				try {
					JSONObject temObj = new JSONObject(result);
					if (null != temObj) {
						clearRes = temObj.optInt(JVAlarmConst.JK_ALARM_NEW_RT);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			clearRes = ret;
		}

		MyLog.v("Alarm", "clearAlarmInfo---res: " + clearRes + ", ret:" + ret);
		return clearRes;
	}

	public static int OnlyConnect(String strYstNum) {
		Device device = null;
		boolean bfind = false;

		ArrayList<Device> devCacheList = CacheUtil.getDevList();
		for (int j = 0; j < devCacheList.size(); j++) {
			// if(CacheUtil.getDevList().size() == 0){
			// return -99;
			// }
			// device = CacheUtil.getDevList().get(j);
			device = devCacheList.get(j);
			MyLog.v("AlarmConnect", "dst:" + strYstNum + "---yst-num = "
					+ device.getFullNo());
			if (strYstNum.equalsIgnoreCase(device.getFullNo())) {
				bfind = true;
				break;
			}
		}
		int con_res = -99;
		if (bfind) {
			if ("".equalsIgnoreCase(device.getIp()) || 0 == device.getPort()) {
				// 云视通连接
				MyLog.v("New Alarm", device.getNo() + "--云视通--连接");
				con_res = Jni.connect(Consts.ONLY_CONNECT_INDEX, 1, device
						.getIp(), device.getPort(), device.getUser(), device
						.getPwd(), device.getNo(), device.getGid(), true, 1,
						true,
						device.isOldDevice() ? JVNetConst.TYPE_3GMOHOME_UDP
								: JVNetConst.TYPE_3GMO_UDP, null, false, false,
						null);
			} else {
				// IP直连
				MyLog.v("New Alarm",
						device.getNo() + "--IP--连接：" + device.getIp());
				con_res = Jni.connect(Consts.ONLY_CONNECT_INDEX, 1, device
						.getIp(), device.getPort(), device.getUser(), device
						.getPwd(), -1, device.getGid(), true, 1, true, device
						.isOldDevice() ? JVNetConst.TYPE_3GMOHOME_UDP
						: JVNetConst.TYPE_3GMO_UDP, null, false, false, null);

			}

			return con_res;
		} else {
			MyLog.e("AlarmConnect", "not find dst:" + strYstNum);
			return con_res;
		}
	}

	public static boolean OnlyConnect2(String strYstNum) {
		Device device = null;
		boolean bfind = false;
		ArrayList<Device> devCacheList = CacheUtil.getDevList();
		for (int j = 0; j < devCacheList.size(); j++) {
			device = devCacheList.get(j);
			MyLog.v("AlarmConnect", "dst:" + strYstNum + "---yst-num = "
					+ device.getFullNo());
			if (strYstNum.equalsIgnoreCase(device.getFullNo())) {
				bfind = true;
				break;
			}
		}
		if (bfind) {
			boolean con_res = false;
			if ("".equalsIgnoreCase(device.getIp()) || 0 == device.getPort()) {
				// 云视通连接
				MyLog.v("New Alarm", device.getNo() + "--云视通--连接");
				con_res = Jni.connect(Consts.ONLY_CONNECT_INDEX, 1, device
						.getIp(), device.getPort(), device.getUser(), device
						.getPwd(), device.getNo(), device.getGid(), true, 1,
						true,
						device.isOldDevice() ? JVNetConst.TYPE_3GMOHOME_UDP
								: JVNetConst.TYPE_3GMO_UDP, null, false, false,
						null) >= 0;
			} else {
				// IP直连
				MyLog.v("New Alarm",
						device.getNo() + "--IP--连接：" + device.getIp());
				con_res = Jni.connect(Consts.ONLY_CONNECT_INDEX, 1, device
						.getIp(), device.getPort(), device.getUser(), device
						.getPwd(), -1, device.getGid(), true, 1, true, device
						.isOldDevice() ? JVNetConst.TYPE_3GMOHOME_UDP
						: JVNetConst.TYPE_3GMO_UDP, null, false, false, null) >= 0;

			}
			return con_res;
		} else {
			MyLog.e("AlarmConnect", "not find dst:" + strYstNum);
			return false;
		}
	}

	// 将时间戳转为字符串
	public static String getStrTime(String cc_time) {
		String re_StrTime = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;

	}

	// 将字符串格式为yyyyMMddhhmmss转换为yyyy-MM-dd HH:mm:ss
	public static String formatStrTime(String dstTimeStr) {
		String re_StrTime = null;
		if (dstTimeStr.length() != 14) {
			return "";
		}
		String strYear = dstTimeStr.substring(0, 3 + 1);
		String strMonth = dstTimeStr.substring(4, 5 + 1);
		String strDay = dstTimeStr.substring(6, 7 + 1);
		String strHour = dstTimeStr.substring(8, 9 + 1);
		String strMin = dstTimeStr.substring(10, 11 + 1);
		String strSecond = dstTimeStr.substring(12, 13 + 1);

		StringBuffer sbResBuffer = new StringBuffer(strYear);
		sbResBuffer.append("-").append(strMonth).append("-").append(strDay)
				.append(" ").append(strHour).append(":").append(strMin)
				.append(":").append(strSecond);

		re_StrTime = sbResBuffer.toString();
		return re_StrTime;

	}

	// 将字符串格式为yyyyMMddhhmmss转换为yyyy-MM-dd HH:mm:ss
	public static String formatStrTime2(String dstTimeStr) {
		String re_StrTime = null;
		if (dstTimeStr.length() != 14) {
			return "";
		}
		String strYear = dstTimeStr.substring(0, 3 + 1);
		String strMonth = dstTimeStr.substring(4, 5 + 1);
		String strDay = dstTimeStr.substring(6, 7 + 1);
		String strHour = dstTimeStr.substring(8, 9 + 1);
		String strMin = dstTimeStr.substring(10, 11 + 1);
		String strSecond = dstTimeStr.substring(12, 13 + 1);

		StringBuffer sbResBuffer = new StringBuffer(strHour);
		sbResBuffer.append(":").append(strMin).append(":").append(strSecond);

		re_StrTime = sbResBuffer.toString();
		return re_StrTime;

	}
}