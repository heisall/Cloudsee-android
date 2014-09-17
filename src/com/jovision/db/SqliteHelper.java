package com.jovision.db;

import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jovision.commons.MyLog;

/**
 * 提供sqlite数据表的创建、更新等操作
 * 
 */
class SqliteHelper extends SQLiteOpenHelper {

	private static final String TAG = "SqliteHelper";
	public static final String TABLE_USER = "USER";// 用户表名
	public static final String TABLE_DEVICE = "DEVICE";// 设备表名
	public static final String TABLE_CHANNEL = "CHANNEL";// 通道表名
	public static final String _ID = "_id";// 表中的列名
	public static final String NAME = "name";// 表中的列名

	public SqliteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	// 创建表
	@Override
	public void onCreate(SQLiteDatabase db) {
		HashMap<String, String> userTableMap = new HashMap<String, String>();
		userTableMap.put("userId", "Integer primary key autoincrement");
		userTableMap.put("userName", "VARCHAR");
		userTableMap.put("userPwd", "VARCHAR");
		userTableMap.put("userEmail", "VARCHAR");
		userTableMap.put("lastLogin", "INTEGER");

		HashMap<String, String> devTableMap = new HashMap<String, String>();
		devTableMap.put("deviceId", "Integer primary key autoincrement");
		devTableMap.put("ip", "VARCHAR");
		devTableMap.put("port", "INTEGER");
		devTableMap.put("gid", "VARCHAR");
		devTableMap.put("no", "INTEGER");
		devTableMap.put("fullNo", "VARCHAR");
		devTableMap.put("user", "VARCHAR");
		devTableMap.put("pwd", "VARCHAR");
		devTableMap.put("isHomeProduct", "INTEGER");
		devTableMap.put("isHelperEnabled", "INTEGER");
		devTableMap.put("deviceType", "INTEGER");
		devTableMap.put("isO5", "INTEGER");
		devTableMap.put("nickName", "VARCHAR");
		devTableMap.put("isDevice", "INTEGER");
		devTableMap.put("onlineState", "INTEGER");
		devTableMap.put("hasWifi", "INTEGER");

		HashMap<String, String> channelTableMap = new HashMap<String, String>();
		channelTableMap.put("channelId", "Integer primary key autoincrement");
		channelTableMap.put("nickName", "VARCHAR");
		channelTableMap.put("channelIndex", "INTEGER");
		channelTableMap.put("deviceId", "INTEGER");

		db.execSQL(createSql(TABLE_USER, userTableMap));
		db.execSQL(createSql(TABLE_DEVICE, devTableMap));
		db.execSQL(createSql(TABLE_CHANNEL, channelTableMap));

	}

	/**
	 * 生成建表sql
	 * 
	 * @param tableName
	 * @param map
	 * @return
	 */
	public String createSql(String tableName, HashMap<String, String> map) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("CREATE TABLE IF NOT EXISTS " + tableName + " (");
		for (Object key : map.keySet()) {
			stringBuffer.append(key);
			stringBuffer.append(" ");
			stringBuffer.append(map.get(key));
			stringBuffer.append(",");
		}
		stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));// 去掉最后一个","
		stringBuffer.append(")");
		MyLog.e(TAG, stringBuffer.toString());
		return stringBuffer.toString();
	}

	// 更新表
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
		onCreate(db);
		Log.e("Database", "onUpgrade");
	}

	// 更新列
	public void updateColumn(SQLiteDatabase db, String oldColumn,
			String newColumn, String typeColumn) {
		try {
			db.execSQL("ALTER TABLE " + TABLE_USER + " CHANGE " + oldColumn
					+ " " + newColumn + " " + typeColumn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}