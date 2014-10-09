package com.jovision.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.JVACCOUNT;

import com.jovision.bean.PushInfo;
import com.jovision.commons.JVAlarmConst;
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
		byte[] resultStr = new byte[1024 * 2];
		JVACCOUNT.GetResponseByRequestShortConnectionServer(ALARMTAG,
				jObj.toString(), resultStr);
		String result = new String(resultStr);

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
		byte[] resultStr = new byte[1024 * 2];
		JVACCOUNT.GetResponseByRequestShortConnectionServer(ALARMTAG,
				jObj.toString(), resultStr);
		String result = new String(resultStr);

		if (null != result && !"".equalsIgnoreCase(result)) {
			try {
				JSONObject temObj = new JSONObject(result);
				if (null != temObj) {
					// int mid = temObj.optInt(JVAlarmConst.JK_ALARM_MID);
					videoUrl = temObj.optString(JVAlarmConst.JK_ALARM_VIDEOURL);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MyLog.v("getAlarmVideo---res", videoUrl);
		return videoUrl;
	}

	/**
	 * 2014-02-27 5.客户端查询报警的历史记录
	 * 
	 * @param userName
	 */
	public static ArrayList<PushInfo> getUserAlarmList(int beginIndex, int count) {
		ArrayList<PushInfo> pushList = new ArrayList<PushInfo>();
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
			jObj.put(JVAlarmConst.JK_ALARM_MID,
					JVAlarmConst.MID_REQUEST_ALARMHISTORY);
			// jObj.put(JVAlarmConst.JK_ALARM_ACCOUNT, userName);
			jObj.put(JVAlarmConst.JK_ALARM_SEARCHINDEX, beginIndex);
			jObj.put(JVAlarmConst.JK_ALARM_SEARCHCOUNT, count);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		MyLog.v("getUserAlarmList", jObj.toString());

		// 接收返回数据
		byte[] resultStr = new byte[1024 * 2];
		JVACCOUNT.GetResponseByRequestShortConnectionServer(ALARMTAG,
				jObj.toString(), resultStr);
		String result = new String(resultStr);

		MyLog.v("getUserAlarmList---result---res", result + "");

		if (null != result && !"".equalsIgnoreCase(result)) {
			try {
				JSONObject temObj = new JSONObject(result);
				if (null != temObj) {
					// int mid = temObj.optInt(JVAlarmConst.JK_ALARM_MID);
					int pushCount = temObj
							.optInt(JVAlarmConst.JK_ALARM_SEARCHCOUNT);
					if (pushCount > 0) {
						JSONArray dlist = new JSONArray(
								temObj.optString(JVAlarmConst.JK_ALARM_LIST));
						if (null != dlist && 0 != dlist.length()) {
							for (int i = 0; i < dlist.length(); i++) {
								JSONObject obj = dlist.getJSONObject(i);
								if (null != obj) {
									PushInfo pi = new PushInfo();
									pi.strGUID = obj
											.optString(JVAlarmConst.JK_ALARM_GUID);
									pi.ystNum = obj
											.optString(JVAlarmConst.JK_ALARM_CLOUDNUM);
									pi.coonNum = obj
											.optInt(JVAlarmConst.JK_ALARM_CLOUDCHN);
									pi.alarmType = obj
											.optInt(JVAlarmConst.JK_ALARM_ALARMTYPE);
									pi.alarmLevel = obj
											.optInt(JVAlarmConst.JK_ALARM_ALARMLEVEL);// 2014.03.08报警级别：1级最高
									pi.alarmTime = obj
											.optString(JVAlarmConst.JK_ALARM_ALARMTIME);
									MyLog.e("报警ID---strGUID---", pi.strGUID
											+ "");
									MyLog.e("报警时间---alarmTime---", pi.alarmTime
											+ "");
									pi.deviceName = obj
											.optString(JVAlarmConst.JK_ALARM_CLOUDNAME);
									pi.pic = obj
											.optString(JVAlarmConst.JK_ALARM_PICURL);
									pushList.add(pi);
								}
							}
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		MyLog.v("getUserAlarmList---res", pushList.size() + "");
		return pushList;
	}

	/**
	 * 6.删除报警信息 0失败，1成功
	 */
	public static int deleteAlarmInfo(String userName, String alarmGuid) {
		int deleteRes = -1;

		JSONObject jObj = new JSONObject();
		try {
			jObj.put(JVAlarmConst.JK_ALARM_MID,
					JVAlarmConst.MID_REQUEST_REMOVEALARM);
			jObj.put(JVAlarmConst.JK_ALARM_ACCOUNT, userName);
			jObj.put(JVAlarmConst.JK_ALARM_GUID, alarmGuid);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		MyLog.v("deleteAlarmInfo", jObj.toString());

		// 接收返回数据
		byte[] resultStr = new byte[1024 * 2];
		JVACCOUNT.GetResponseByRequestShortConnectionServer(ALARMTAG,
				jObj.toString(), resultStr);
		String result = new String(resultStr);

		if (null != result && !"".equalsIgnoreCase(result)) {
			try {
				JSONObject temObj = new JSONObject(result);
				if (null != temObj) {
					// int mid = temObj.optInt(JVAlarmConst.JK_ALARM_MID);
					// String guid =
					// temObj.optString(JVAlarmConst.JK_ALARM_GUID);
					deleteRes = temObj.optInt(JVAlarmConst.JK_ALARM_OPTFLAG);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		MyLog.v("deleteAlarmInfo---res", deleteRes + "");
		return deleteRes;
	}
}