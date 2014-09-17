package com.jovision.old;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jovision.commons.JVConfigManager;
import com.jovision.commons.MyLog;
import com.jovision.newbean.UserBean;

public class OldDataTool {

	public static JVConfigManager dbManager;
	private static String tableName = "JVConfigTemp";
	private static String userTableName = "LoginUser";

	public OldDataTool(JVConfigManager manager) {
		dbManager = manager;
	}

	/**
	 * 查询所有用户信息
	 * 
	 * @return
	 */
	public static ArrayList<UserBean> queryAllUserList() {
		ArrayList<UserBean> list = new ArrayList<UserBean>();

		SQLiteDatabase readDB = null;
		Cursor c = null;
		try {
			readDB = dbManager.getReadableDatabase();
			if (readDB.isOpen()) {
				c = readDB.rawQuery("select * from '" + userTableName
						+ "' order by userId desc",// 从数据库里查询按降序查询，最新添加的在最上面显示
						null);
			} else {
				return null;
			}

			if (c == null)
				return null;
			boolean t = c.moveToFirst();
			while (t) {
				UserBean user = new UserBean();
				user.setPrimaryID(c.getLong(0));
				user.setUserName(c.getString(1));
				user.setUserPwd(c.getString(2));
				user.setLastLogin(c.getInt(3));
				list.add(user);
				t = c.moveToNext();
			}
			c.close();

		} catch (Exception e) {
			list = null;
		} finally {
			try {
				if (readDB != null && readDB.isOpen()) {
					readDB.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 查询通道或设备数据,flag = true 查询设备；flag = false 查询通道
	 * 
	 * @param flag
	 * @return
	 */
	public static List<JVConnectInfo> queryAllDataList(boolean flag) {
		List<JVConnectInfo> list = new ArrayList<JVConnectInfo>();
		JVConnectInfo info = null;
		SQLiteDatabase readDB = null;
		Cursor c = null;
		try {
			readDB = dbManager.getReadableDatabase();
			MyLog.v("String.valueOf(flag==true?1:0)-----",
					String.valueOf(flag == true ? 1 : 0));
			if (readDB.isOpen()) {
				// c = readDB.query(tableName, clm, null, null, null, null,
				// null);
				c = readDB.rawQuery("select * from '" + tableName
						+ "' where isParent=?" + " order by ID desc",// 从数据库里查询按降序查询，最新添加的在最上面显示
						new String[] { String.valueOf(flag == true ? 1 : 0)
								+ "" });
			} else {
				return null;
			}

			if (c == null)
				return null;
			boolean t = c.moveToFirst();
			while (t) {
				info = new JVConnectInfo();
				info.setPrimaryID(Long.parseLong(c.getString(0)));
				info.setSrcName(c.getString(1));
				info.setConnType(c.getInt(2));
				info.setCsNumber(c.getInt(3));
				info.setRemoteIp(c.getString(4));
				info.setPort(c.getInt(5));
				info.setChannel(c.getInt(6));
				info.setUserName(c.getString(7));
				info.setPasswd(c.getString(8));
				info.setByUDP(c.getInt(9) == 1 ? true : false);
				info.setLocalTry(c.getInt(10) == 1 ? true : false);
				info.setOnlineState(1);
				info.setParent(c.getInt(11) == 1 ? true : false);
				info.setNickName(c.getString(12));
				info.setGroup(c.getString(13));
				if ("null".equalsIgnoreCase(info.getRemoteIp())) {
					info.setRemoteIp("");
				}
				if (info.isParent()) {
					if (null == info.getRemoteIp()
							|| "".equals(info.getRemoteIp())
							|| "null".equalsIgnoreCase(info.getRemoteIp())) {
						// info.setDevice(0);
						MyLog.v("sss", "falsedevice");
						MyLog.v("sss", info.getRemoteIp());
					} else {
						// info.setDevice(1);
						MyLog.v("sss", "truedevice");
						MyLog.v("sss", info.getRemoteIp());
					}
				}
				if (info.isParent() == flag) {
					list.add(info);
				}
				MyLog.v("tags	",
						"yst: " + c.getInt(3)
								+ ", group: "
								+ c.getString(13)
								+ ", isParent: "
								+ c.getInt(11)
								+ ", channel: "
								+
								// c.getInt(6)+"");
								// i++;
								// MyLog.v("tags",
								//
								"name=" + c.getString(1) + ",ystNumber="
								+ c.getInt(3) + ",channel=" + c.getInt(6));
				t = c.moveToNext();
			}
			// myList.get(0).setAction(true);
			c.close();

		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			try {
				if (readDB != null && readDB.isOpen()) {
					readDB.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}

}
