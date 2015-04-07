
package com.jovision.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class JVConfigManager extends SQLiteOpenHelper {

    // private String tableName_O="JVConfig";
    private String tableName = "JVConfigTemp";
    private String ID = "ID"; // long
    private String srcName = "srcName"; // varchar
    private String connType = "connType"; // int
    private String csNumber = "csNumber"; // int
    private String ipAddr = "ipAddr"; // varchar
    private String port = "port"; // int
    private String channel = "channel"; // int
    private String user = "user"; // varchar
    private String pass = "pass"; // varchar
    private String byUDP = "byudp"; // int 0 false 1 true
    private String localTry = "localtry"; // int 0 false 1 true
    private String group = "groupName";
    private String isParent = "isParent";
    private String nickName = "nickName";
    private int itemCount = 0;

    private String captureTable = "JVCaptureTable";
    private String imageName = "imageName";
    private String iamgeId = "imageId";

    private String userTableName = "LoginUser";
    private String userId = "userId";
    private String userName = "userName";
    private String userPwd = "userPwd";
    private String lastLogin = "lastLogin";

    private SQLiteDatabase myWriteDb = null, myReadDb = null;
    public boolean isCreate = false;

    public JVConfigManager(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (" + this.ID
                + " VARCHAR PRIMARY KEY," + this.srcName + " VARCHAR,"
                + this.connType + " INTEGER," + this.csNumber + " INTEGER ,"
                + this.ipAddr + " VARCHAR," + this.port + " INTEGER,"
                + this.channel + " INTEGER," + this.user + " VARCHAR,"
                + this.pass + " VARCHAR," + this.byUDP + " INTEGER,"
                + this.localTry + " INTEGER," + this.isParent + " INTEGER,"
                + this.nickName + " VARCHAR," + this.group + " VARCHAR" + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + captureTable + " ("
                + this.iamgeId + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + this.imageName + " VARCHAR" + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + userTableName + " ("
                + this.userId + " VARCHAR PRIMARY KEY," + this.userName
                + " VARCHAR," + this.userPwd + " VARCHAR," + this.lastLogin
                + " INTEGER" + ")");

        this.isCreate = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        db.execSQL("DROP TABLE IF EXISTS " + captureTable);
        db.execSQL("DROP TABLE IF EXISTS " + userTableName);
        onCreate(db);
    }

    // public Cursor getAllData() {
    // this.initRead();
    // Cursor c = null;
    // String[] clm = new String[] { this.ID, this.srcName, this.connType,
    // this.csNumber, this.ipAddr, this.port, this.channel, this.user,
    // this.pass, this.byUDP,
    // this.localTry,this.isParent,this.nickName,
    // this.group };
    // c = this.myReadDb.query(tableName, clm, null, null, null, null, null);
    // // if(c!=null&&c.getCount()==0)
    // // {
    // // this.addItem(new JVConnectInfo(new Date().getTime()));
    // // }
    // this.finishRead();
    // return c;
    // }
    //
    // public int getNum() {
    // Cursor c = this.getAllData();
    // if (c == null) {
    // return -1;
    // }
    // this.itemCount = c.getCount();
    // return this.itemCount;
    // }

    public void initRead() {
        if (this.myReadDb == null)
            this.myReadDb = this.getReadableDatabase();
    }

    public void finishRead() {
        if (this.myReadDb != null)
            this.myReadDb.close();
        this.myReadDb = null;
    }

    public void initWrite() {
        if (this.myWriteDb == null)
            this.myWriteDb = this.getWritableDatabase();
    }

    public void finishWrite() {
        if (this.myWriteDb != null)
            this.myWriteDb.close();
        this.myWriteDb = null;
    }

    public void close() {
        this.close();
    }

    public void commit() {
    }

    public int addSrcRelative(String imageName) {
        this.initWrite();
        ContentValues cv = new ContentValues();
        // cv.put(this.iamgeId, Integer.toString(gindex));
        cv.put(this.imageName, imageName);
        int i = (int) this.myWriteDb.insert(captureTable, null, cv);
        this.finishWrite();
        return i;
    }

    // public void clearRelative()
    // {
    // this.initWrite();
    // this.myWriteDb.execSQL("delete from JVSource");
    // this.finishWrite();
    // }
}
