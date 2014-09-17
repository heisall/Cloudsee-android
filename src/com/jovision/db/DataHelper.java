package com.jovision.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jovision.Consts;
import com.jovision.newbean.UserBean;

public class DataHelper {
	/** 数据库版本 */
	private static int DB_VERSION = 1;
	private SQLiteDatabase db;
	private SqliteHelper dbHelper;

	private static final String DB_NAME = Consts.DB_NAME;// 数据库名
	private static final String DB_PATH = Consts.DB_PATH;// 数据路径

	private static final String SELECT_SQL = "select * from ";

	public DataHelper(Context context) {
		dbHelper = new SqliteHelper(context, DB_PATH + DB_NAME, null,
				DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public void Close() {
		db.close();
		dbHelper.close();
	}

	/**
	 * 查询所有用户
	 * 
	 * @return
	 */
	public ArrayList<UserBean> selectUserBeanList() {
		ArrayList<UserBean> userList = new ArrayList<UserBean>();
		Cursor cursor = db.rawQuery(SELECT_SQL + SqliteHelper.TABLE_USER, null);
		if (null == cursor)
			return null;

		int count = cursor.getCount();
		cursor.moveToFirst();
		for (int i = 0; i < count; i++) {
			UserBean user = new UserBean();
			user.setPrimaryID(cursor.getInt(0));
			user.setUserName(cursor.getString(1));
			user.setUserPwd(cursor.getString(2));
			user.setUserEmail(cursor.getString(3));
			user.setLastLogin(cursor.getInt(4));
			userList.add(user);
		}

		return userList;
	}

	// 添加users表的记录

	public int addUserBean(UserBean user) {
		int res = 0;
		String sql = "insert into " + SqliteHelper.TABLE_USER
				+ " (userName, userPwd, userEmail, lastLogin) values (" + "'"
				+ user.getUserName() + "', " + "'" + user.getUserPwd() + "', "
				+ "'" + user.getUserEmail() + "', " + "'" + user.getLastLogin()
				+ ")";

		db.execSQL(sql);

		return res;

	}

	//
	// // 删除users表的记录
	//
	// public int DelUserBeanInfo(String UserBeanId) {
	//
	// int id = db.delete(SqliteHelper.TB_NAME,
	//
	// UserBeanInfo.USERID + "=" + UserBeanId, null);
	//
	// Log.e("DelUserBeanInfo", id + "");
	//
	// return id;
	//
	// }

	// // 判断users表中的是否包含某个UserBeanID的记录
	//
	// public Boolean HaveUserBeanInfo(String UserBeanId) {
	//
	// Boolean b = false;
	//
	// Cursor cursor = db.query(SqliteHelper.TB_NAME, null, UserBeanInfo.USERID
	//
	// + "=" + UserBeanId, null, null, null, null);
	//
	// b = cursor.moveToFirst();
	//
	// Log.e("HaveUserBeanInfo", b.toString());
	//
	// cursor.close();
	//
	// return b;
	//
	// }
	//
	// // 更新users表的记录，根据UserBeanId更新用户昵称和用户图标
	//
	// public int UpdateUserBeanInfo(String userName, Bitmap userIcon, String
	// UserBeanId) {
	//
	// ContentValues values = new ContentValues();
	//
	// values.put(UserBeanInfo.USERNAME, userName);
	//
	// // BLOB类型
	//
	// final ByteArrayOutputStream os = new ByteArrayOutputStream();
	//
	// // 将Bitmap压缩成PNG编码，质量为100%存储
	//
	// userIcon.compress(Bitmap.CompressFormat.PNG, 100, os);
	//
	// // 构造SQLite的Content对象，这里也可以使用raw
	//
	// values.put(UserBeanInfo.USERICON, os.toByteArray());
	//
	// int id = db.update(SqliteHelper.TB_NAME, values, UserBeanInfo.USERID +
	// "="
	//
	// + UserBeanId, null);
	//
	// Log.e("UpdateUserBeanInfo2", id + "");
	//
	// return id;
	//
	// }
	//
	// // 更新users表的记录
	//
	// public int UpdateUserBeanInfo(UserBeanInfo user) {
	//
	// ContentValues values = new ContentValues();
	//
	// values.put(UserBeanInfo.USERID, user.getUSERID());
	//
	// values.put(UserBeanInfo.TOKEN, user.getTOKEN());
	//
	// values.put(UserBeanInfo.TOKENSECRET, user.getTOKENSECRET());
	//
	// int id = db.update(SqliteHelper.TB_NAME, values, UserBeanInfo.USERID +
	// "="
	//
	// + user.getUSERID(), null);
	//
	// Log.e("UpdateUserBeanInfo", id + "");
	//
	// return id;
	//
	// }
	//
	// // 添加users表的记录
	//
	// public Long SaveUserBeanInfo(UserBeanInfo user) {
	//
	// ContentValues values = new ContentValues();
	//
	// values.put(UserBeanInfo.USERID, user.getUSERID());
	//
	// values.put(UserBeanInfo.TOKEN, user.getTOKEN());
	//
	// values.put(UserBeanInfo.TOKENSECRET, user.getTOKENSECRET());
	//
	// Long uid = db.insert(SqliteHelper.TB_NAME, UserBeanInfo.ID, values);
	//
	// Log.e("SaveUserBeanInfo", uid + "");
	//
	// return uid;
	//
	// }
	//
	// // 删除users表的记录
	//
	// public int DelUserBeanInfo(String UserBeanId) {
	//
	// int id = db.delete(SqliteHelper.TB_NAME,
	//
	// UserBeanInfo.USERID + "=" + UserBeanId, null);
	//
	// Log.e("DelUserBeanInfo", id + "");
	//
	// return id;
	//
	// }

	// // 获取users表中的UserBeanID、Access Token、Access Secret的记录
	// public List<UserBeanDBInfo> GetUserBeanList(Boolean isSimple) {
	// List<UserBeanDBInfo> userList = new ArrayList<UserBeanDBInfo>();
	// Cursor cursor = db.query(SqliteHelper.TB_NAME, null, null, null, null,
	// null, UserBeanDBInfo.ID + " DESC");
	// cursor.moveToFirst();
	// while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
	// UserBeanDBInfo user = new UserBeanDBInfo();
	// user.setID(cursor.getString(0));
	// user.setUSERID(cursor.getString(1));
	// user.setTOKEN(cursor.getString(2));
	// user.setTOKENSECRET(cursor.getString(3));
	// if (!isSimple) {
	// user.setUSERNAME(cursor.getString(4));
	// ByteArrayInputStream stream = new ByteArrayInputStream(
	// cursor.getBlob(5));
	// Drawable icon = Drawable.createFromStream(stream, "image");
	// user.setUSERICON(icon.toString());
	// }
	// userList.add(user);
	// cursor.moveToNext();
	// }
	// cursor.close();
	// return userList;
	// }
	//
	// // 判断users表中的是否包含某个UserBeanID的记录
	// public Boolean HaveUserBeanInfo(String UserBeanId) {
	// Boolean b = false;
	// Cursor cursor = db.query(SqliteHelper.TB_NAME, null,
	// UserBeanDBInfo.USERID
	// + "=" + UserBeanId, null, null, null, null);
	// b = cursor.moveToFirst();
	// Log.e("HaveUserBeanInfo", b.toString());
	// cursor.close();
	// return b;
	// }
	//
	// // 更新users表的记录，根据UserBeanId更新用户昵称和用户图标
	// public int UpdateUserBeanInfo(String userName, Bitmap userIcon, String
	// UserBeanId) {
	// ContentValues values = new ContentValues();
	// values.put(UserBeanDBInfo.USERNAME, userName);
	// // BLOB类型
	// final ByteArrayOutputStream os = new ByteArrayOutputStream();
	// // 将Bitmap压缩成PNG编码，质量为100%存储
	// userIcon.compress(Bitmap.CompressFormat.PNG, 100, os);
	// // 构造SQLite的Content对象，这里也可以使用raw
	// values.put(UserBeanDBInfo.USERICON, os.toByteArray());
	// int id = db.update(SqliteHelper.TB_NAME, values, UserBeanDBInfo.USERID +
	// "="
	// + UserBeanId, null);
	// Log.e("UpdateUserBeanInfo2", id + "");
	// return id;
	// }
	//
	// // 更新users表的记录
	// public int UpdateUserBeanInfo(UserBeanDBInfo user) {
	// ContentValues values = new ContentValues();
	// values.put(UserBeanDBInfo.USERID, user.getUSERID());
	// values.put(UserBeanDBInfo.TOKEN, user.getTOKEN());
	// values.put(UserBeanDBInfo.TOKENSECRET, user.getTOKENSECRET());
	// int id = db.update(SqliteHelper.TB_NAME, values, UserBeanDBInfo.USERID +
	// "="
	// + user.getUSERID(), null);
	// Log.e("UpdateUserBeanInfo", id + "");
	// return id;
	// }
	//
	// 添加users表的记录
	public Long SaveUserBeanInfo() {
		ContentValues values = new ContentValues();
		values.put("name", System.currentTimeMillis());
		Long uid = db.insert(SqliteHelper.TABLE_USER, Math.random() + "",
				values);
		Log.e("SaveUserBeanInfo", uid + "");
		return uid;
	}
	//
	// // 删除users表的记录
	// public int DelUserBeanInfo(String UserBeanId) {
	// int id = db.delete(SqliteHelper.TB_NAME,
	// UserBeanDBInfo.USERID + "=" + UserBeanId, null);
	// Log.e("DelUserBeanInfo", id + "");
	// return id;
	// }
}