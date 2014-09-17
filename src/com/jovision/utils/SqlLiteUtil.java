package com.jovision.utils;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jovision.commons.JVConfigManager;
import com.jovision.commons.JVConst;
import com.jovision.newbean.UserBean;

public class SqlLiteUtil {

	/** 上下文 */
	private Context context;
	private static JVConfigManager dbManager;

	public SqlLiteUtil(Context con) {
		context = con;
		dbManager = new JVConfigManager(context, JVConst.JVCONFIG_DATABASE,
				null, JVConst.JVCONFIG_DB_VER);
	}

	private static String userTableName = "LoginUser";

	// private static String[] clm = new String[] { "ID", "srcName", "connType",
	// "csNumber", "ipAddr", "port", "channel", "user", "pass", "byUDP",
	// "localTry", "isParent", "nickName", "groupName" };

	private static String[] userclm = new String[] { "userId", "userName",
			"userPwd", "lastLogin" };

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
				// c = readDB.query(tableName, clm, null, null, null, null,
				// null);
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
	 * 查询指定用户是否存在
	 * 
	 * @param user
	 * @return
	 */
	public static UserBean queryUser(UserBean user) {
		UserBean userRes = null;
		SQLiteDatabase readDB = null;
		Cursor c = null;
		try {
			readDB = dbManager.getReadableDatabase();
			if (readDB.isOpen()) {
				c = readDB.rawQuery("select * from '" + userTableName
						+ "' where userName = '" + user.getUserName()
						+ "' COLLATE NOCASE", null);

			} else {
				return userRes;
			}
			if (c == null)
				return userRes;
			if (c != null) {
				boolean t = c.moveToFirst();
				while (t) {
					userRes = new UserBean();
					userRes.setPrimaryID(c.getLong(0));
					userRes.setUserName(c.getString(1));
					userRes.setUserPwd(c.getString(2));
					userRes.setLastLogin(c.getInt(3));
					t = c.moveToNext();
				}
				c.close();
			}
		} catch (Exception e) {
			userRes = null;
		} finally {
			try {
				if (readDB != null && readDB.isOpen()) {
					readDB.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return userRes;
	}

	/**
	 * 查询上次登录用户
	 * 
	 * @return
	 */
	public static UserBean queryLoginUser() {
		ArrayList<UserBean> list = new ArrayList<UserBean>();
		SQLiteDatabase readDB = null;
		Cursor c = null;
		UserBean user = null;
		try {
			readDB = dbManager.getReadableDatabase();
			if (readDB.isOpen()) {
				c = readDB.rawQuery("select * from " + userTableName
						+ " where lastLogin=1",// 从数据库里查询lastLogin
												// =
												// 1的user
						null);

			} else {
				return user;
			}

			if (c != null) {
				int count = c.getCount();
				c.moveToFirst();
				for (int i = 0; i < count; i++) {
					user = new UserBean();
					user.setPrimaryID(c.getLong(0));
					user.setUserName(c.getString(1));
					user.setUserPwd(c.getString(2));
					user.setLastLogin(c.getInt(3));
					list.add(user);
					c.moveToNext();
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return user;
		} finally {
			try {
				if (readDB != null && readDB.isOpen()) {
					readDB.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return user;
	}

	/**
	 * 注销时将最后登录标志位都置为0
	 * 
	 * @return
	 */
	public static int resetUser() {

		SQLiteDatabase writableDatabase = null;
		int ret = 0;
		try {
			writableDatabase = dbManager.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("lastLogin", 0);

			if (writableDatabase.isOpen()) {
				ret = writableDatabase.update(userTableName, cv, "", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writableDatabase != null && writableDatabase.isOpen()) {
					writableDatabase.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return ret;
	}

	// 添加用户,
	public static int addUser(UserBean user) {
		resetUser();// 将用户最后登录标志位都置为0
		int i = 0;
		// 如果数据库里已经有了，将该用户更新数据库
		UserBean oldUser = queryUser(user);
		if (null != oldUser) {
			oldUser.setLastLogin(1);// 将登录用户登录标志位置为1
			oldUser.setPrimaryID(user.getPrimaryID());// 最后登录时间更新
			i = modifyUserLoginInfo(oldUser);
			return i;
		}

		SQLiteDatabase database = dbManager.getWritableDatabase();
		try {
			if (database.isOpen()) {
				ContentValues cv = new ContentValues();
				cv.put(userclm[0], Long.toString(user.getPrimaryID()));
				cv.put(userclm[1], user.getUserName());
				cv.put(userclm[2], user.getUserPwd());
				cv.put(userclm[3], 1);
				i = (int) database.insert(userTableName, "userId", cv);

			} else {
				i = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			i = 0;
		} finally {
			try {
				if (database != null && database.isOpen()) {
					database.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return i;
	}

	// 修改登录用户名密码
	public static int modifyUserInfo(UserBean user) {
		SQLiteDatabase writableDatabase = null;
		int ret = 0;
		try {
			writableDatabase = dbManager.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("userPwd", user.getUserPwd());

			if (writableDatabase.isOpen()) {
				ret = writableDatabase.update(userTableName, cv, "userName=? ",
						new String[] { user.getUserName() });
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writableDatabase != null && writableDatabase.isOpen()) {
					writableDatabase.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return ret;
	}

	// 修改登录状态
	public static int modifyUserLoginInfo(UserBean user) {
		SQLiteDatabase writableDatabase = null;
		int ret = 0;
		try {
			writableDatabase = dbManager.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("lastLogin", user.getLastLogin());
			cv.put("userId", user.getPrimaryID());

			if (writableDatabase.isOpen()) {
				ret = writableDatabase.update(userTableName, cv, "userName=? ",
						new String[] { user.getUserName() });
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writableDatabase != null && writableDatabase.isOpen()) {
					writableDatabase.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return ret;
	}

	// 删除登录用户信息
	public static int deleteUser(UserBean user) {
		SQLiteDatabase writableDatabase = null;
		int ret = 0;
		try {
			writableDatabase = dbManager.getWritableDatabase();
			if (writableDatabase.isOpen()) {
				ret = writableDatabase.delete(userTableName, "userName=?",
						new String[] { user.getUserName() });
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != writableDatabase && writableDatabase.isOpen()) {
					writableDatabase.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return ret;
	}

}
