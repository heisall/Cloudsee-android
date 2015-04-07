
package com.jovision.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jovision.Consts;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.bean.User;
import com.jovision.commons.MyLog;

import java.util.ArrayList;

public class ImportOldData {
    private static final String TAG = "OLD";

    public static JVConfigManager dbManager;
    private static String tableName = "JVConfigTemp";
    private static String userTableName = "LoginUser";

    public ImportOldData(Context contex) {
        if (dbManager == null) {
            dbManager = new JVConfigManager(contex, Consts.JVCONFIG_DATABASE,
                    null, Consts.JVCONFIG_DB_VER);
        }
    }

    // 查询所有用户信息
    public ArrayList<User> queryAllUserList() {
        ArrayList<User> list = new ArrayList<User>();

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
                User user = new User();
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
            }
        }
        MyLog.v(TAG, "userList=" + list);
        // [{"userName":"refactor1","lastLogin":0,"userEmail":"","primaryID":1413190799596,"userPwd":"123456"},
        // {"userName":"refactor","lastLogin":0,"userEmail":"","primaryID":1413190781059,"userPwd":"123456"},
        // {"userName":"juyang","lastLogin":0,"userEmail":"","primaryID":1413190738735,"userPwd":"123456"}]
        if (null != list && 0 != list.size()) {
            CacheUtil.saveUserList(list);
        }

        return list;
    }

    // 查询通道或设备数据,flag = true 查询设备；flag = false 查询通道
    public static ArrayList<Device> queryAllDevList() {
        boolean flag = true;
        ArrayList<Device> list = new ArrayList<Device>();

        SQLiteDatabase readDB = null;
        Cursor c = null;
        try {
            readDB = dbManager.getReadableDatabase();
            // Log.e("String.valueOf(flag==true?1:0)-----",
            // String.valueOf(flag == true ? 1 : 0));
            if (readDB.isOpen()) {
                // c = readDB.query(tableName, clm, null, null, null, null,
                // null);
                c = readDB.rawQuery("select * from '" + tableName
                        + "' where isParent=?" + " order by ID desc",// 从数据库里查询按降序查询，最新添加的在最上面显示
                        new String[] {
                            String.valueOf(flag == true ? 1 : 0)
                                    + ""
                        });
            } else {
                return null;
            }

            if (c == null)
                return null;
            boolean t = c.moveToFirst();
            while (t) {
                Device dev = new Device();
                dev.setPrimaryID(Long.parseLong(c.getString(0)));// .setPrimaryID(Long.parseLong(c.getString(0)));
                // dev .setSrcName(c.getString(1));
                // dev .setConnType(c.getInt(2));
                dev.setNo(c.getInt(3));// .setCsNumber(c.getInt(3));
                dev.setIp(c.getString(4));// .setRemoteIp(c.getString(4));
                dev.setPort(c.getInt(5));// .setPort(c.getInt(5));
                // dev .setChannel(c.getInt(6));
                dev.setUser(c.getString(7));// .setUserName(c.getString(7));
                dev.setPwd(c.getString(8));// .setPasswd(c.getString(8));
                // dev .setByUDP(c.getInt(9) == 1 ? true : false);
                // dev .setLocalTry(c.getInt(10) == 1 ? true : false);
                dev.setOnlineStateNet(1);
                // dev .setParent(c.getInt(11) == 1 ? true : false);
                dev.setNickName(c.getString(12));// .setNickName(c.getString(12));
                dev.setGid(c.getString(13));// .setGroup(c.getString(13));
                if ("null".equalsIgnoreCase(dev.getIp())
                        || "".equalsIgnoreCase(dev.getIp())) {
                    dev.setIp("");
                    dev.setIsDevice(0);
                } else {
                    dev.setIsDevice(1);
                }
                dev.setFullNo(dev.getGid() + dev.getNo());
                list.add(dev);
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

        MyLog.v(TAG, "devList=" + list);

        return list;
    }

    // 查询通道或设备数据,flag = true 查询设备；flag = false 查询通道
    public static ArrayList<Channel> queryAllChannelList() {
        boolean flag = false;
        ArrayList<Channel> list = new ArrayList<Channel>();

        SQLiteDatabase readDB = null;
        Cursor c = null;
        try {
            readDB = dbManager.getReadableDatabase();
            // Log.e("String.valueOf(flag==true?1:0)-----",
            // String.valueOf(flag == true ? 1 : 0));
            if (readDB.isOpen()) {
                // c = readDB.query(tableName, clm, null, null, null, null,
                // null);
                c = readDB.rawQuery("select * from '" + tableName
                        + "' where isParent=?" + " order by ID desc",// 从数据库里查询按降序查询，最新添加的在最上面显示
                        new String[] {
                            String.valueOf(flag == true ? 1 : 0)
                                    + ""
                        });
            } else {
                return null;
            }

            if (c == null)
                return null;
            boolean t = c.moveToFirst();
            while (t) {
                Channel channel = new Channel();
                channel.setParent(new Device());
                channel.setPrimaryID(Long.parseLong(c.getString(0)));// .setPrimaryID(Long.parseLong(c.getString(0)));
                // info.setSrcName(c.getString(1));
                // info.setConnType(c.getInt(2));
                channel.getParent().setNo(c.getInt(3));// .setCsNumber(c.getInt(3));
                // info.setRemoteIp(c.getString(4));
                // info.setPort(c.getInt(5));
                channel.setChannel(c.getInt(6));// .setChannel(c.getInt(6));
                // info.setUserName(c.getString(7));
                // info.setPasswd(c.getString(8));
                // info.setByUDP(c.getInt(9) == 1 ? true : false);
                // info.setLocalTry(c.getInt(10) == 1 ? true : false);
                // info.setOnlineState(1);
                // info.setParent(c.getInt(11) == 1 ? true : false);
                channel.setChannelName(c.getString(12));// .setNickName(c.getString(12));
                channel.getParent().setGid(c.getString(13));// .setGroup(c.getString(13));

                channel.getParent().setFullNo(
                        channel.getParent().getGid()
                                + channel.getParent().getNo());
                // if ("null".equalsIgnoreCase(info.getRemoteIp())) {
                // info.setRemoteIp("");
                // }
                // if(c.getString(13).equalsIgnoreCase(BaseApp.IP_ADD_DEVICE)){
                // info.setDeviceNum(c.getString(12));
                // info.ipAddDevice = true;
                // }

                // if (info.isParent()) {
                // if (null == info.getRemoteIp()
                // || "".equals(info.getRemoteIp())
                // || "null".equalsIgnoreCase(info.getRemoteIp())) {
                // info.setDevice(0);
                // Log.v("sss", "falsedevice");
                // Log.v("sss", info.getRemoteIp());
                // } else {
                // info.setDevice(1);
                // Log.v("sss", "truedevice");
                // Log.v("sss", info.getRemoteIp());
                // }
                // }
                // if (info.isParent() == flag) {
                // list.add(info);
                // }

                // Log.e("tags	","yst: "+c.getInt(3)+", group: "+c.getString(13)+", isParent: "+c.getInt(11)+", channel: "+
                // c.getInt(6)+"");
                // i++;
                // Log.e("tags",
                // "name="+c.getString(1)+",ystNumber="+c.getInt(3)+",channel="+c.getInt(6));

                list.add(channel);
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
        MyLog.v(TAG, "channelList=" + list);
        return list;
    }

    public void close() {
        if (dbManager != null) {
            dbManager = null;
        }
    }

    public void getDevList() {
        ArrayList<Device> devList = queryAllDevList();
        ArrayList<Channel> channelList = queryAllChannelList();
        try {
            if (null != channelList && 0 != channelList.size()) {
                for (Channel channel : channelList) {
                    if (null != devList && 0 != devList.size()) {
                        for (Device dev : devList) {
                            if (dev.getFullNo().equalsIgnoreCase(
                                    channel.getParent().getFullNo())) {
                                dev.getChannelList().add(channel);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != devList && 0 != devList.size()) {
            CacheUtil.saveDevList(devList);
        }
        MyLog.v(TAG, "allDevList=" + devList);
    }

}
