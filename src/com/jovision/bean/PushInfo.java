package com.jovision.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PushInfo {
	public int nMobileSysType;
	public String strMobileID;
	public int nAppType;
	public String strAlert;
	public String strCustomMsg;// "y":"B231723516","c":1,"time":"2013-11-08 21:43:01"
	public String strGUID;
	public String ystNum = "";// 云视通号
	public int coonNum = 0;// 通道
	public long alarmTime = 0;// 报警时间

	public String pic = "";// 推送图片地址
	public String video = "";// 推送视频地址

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void getYSTinfo(String msg) {
		if (!"".equalsIgnoreCase(msg.trim()) && msg.contains(",")) {
			msg = msg.replace("\"", "");

			String[] temArray = msg.split(",");
			if (null != temArray) {
				for (int i = 0; i < temArray.length; i++) {
					String str = temArray[i];
					if (str.contains("y:")) {
						ystNum = str.replace("y:", "");
					} else if (str.contains("c:")) {
						coonNum = Integer.parseInt(str.replace("c:", ""));
					} else if (str.contains("time:")) {
						String alarmTimeStr = str.replace("time:", "");
						try {
							Date dt2 = sdf.parse(alarmTimeStr);
							// 继续转换得到秒数的long型
							alarmTime = dt2.getTime() / 1000;
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
		}
	}

}
