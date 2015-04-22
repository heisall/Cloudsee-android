
package com.jovision.bean;

import com.jovision.Consts;
import com.jovision.commons.MyList;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 简单的设备集合类
 * 
 * @author neo
 */
public class Device {

    private final static String TAG = "Device";
    private long primaryID = 0;

    private MyList<Channel> channelList;

    /** 设备IP */
    private String ip;

    /** 设备域名 */
    private String doMain = "";

    /** 设备端口 */
    private int port;
    /** 分组 A */
    private String gid;
    /** 云视通号 361 */
    private int no;
    /** 云视通号 A361 */
    private String fullNo;
    /** 设备用户名 */
    private String user;
    /** 设备密码 */
    private String pwd;
    // /** 是否是家用产品 */
    // private boolean isHomeProduct;// true IPC ，false 非IPC

    private boolean isHelperEnabled;
    /** 设备类型 */
    private int type;// 云视通端定义 如下常量及含义
    // public static final int DEVICE_TYPE_UNKOWN = -1;
    // public static final int DEVICE_TYPE_DVR = 0x01;
    // public static final int DEVICE_TYPE_950 = 0x02;
    // public static final int DEVICE_TYPE_951 = 0x03;
    // public static final int DEVICE_TYPE_IPC = 0x04;
    // public static final int DEVICE_TYPE_NVR = 0x05;

    /** 设备类型 */
    private int deviceType;// 服务端定义 2：家用设备 其他值 非家用
    /** 0几版解码器 */
    private boolean is05;

    /** 是否带帧头 */
    private boolean isJFH;

    /** 设备昵称 */
    private String nickName;

    /** 连接方式标志位 0:云视通号连接 1:手动ip直连 2:域名设备 */
    private int isDevice = 0;
    /** 互联网 是否在线 0.不在线 1.在线 */
    private int onlineStateNet = 0;
    /** 局域网 是否在线 0.不在线 1.在线 */
    private int onlineStateLan = 0;

    /**** 2015-03-02 ***/
    private int enableTcpConnect; // 是否为TCP连接 0. 不开启TCP连接 1.开启TCP连接

    /*** 2014-12-25 ***/
    private boolean admin;// 是否管理员用户
    private int power;// 权限值
    private String descript;// 描述
    /**
     * 设备在线服务器（报警服务器）上线状态标识 dimols为0表示此设备在设备在线服务器离线，为1表示在线
     * 在设备管理界面（报警防护开关界面）需判断此标识，如果此标识为离线，则不允许用户操作报警防护开关。
     * 需要通过设备服务器操作设备的业务（报警防护，报警时段，baby模式等）需要此标识在线。
     */
    /** 　设备服务器是否上线　 */
    private int serverState = 0;// 1,在线 0,离线

    /** 设备是否带Wi-Fi */
    private int hasWifi = 0;
    /** 局域网是否添加设备 */
    private boolean islanselect = true;
    private int alarmSwitch = 0;// 告警开关，0-关闭，1-打开
    private ArrayList<ThirdAlarmDev> thirdDevList = null; // 第三方报警设备

    /** 一键升级用的字段 */
    private String deviceModel = ""; // 设备型号
    private String deviceVerName = ""; // 设备软件版本
    private int deviceVerNum = 0; // 设备软件版本号

    private boolean isCard = false;// 是否是板卡

    /** 设备是否已在设备列表里 ，声波配置使用 */
    private boolean hasAdded = false;// 声波配置使用

    /** 设备是否已在设备列表里 ，声波配置使用 */
    private boolean oldDevice = false;// 连接视频判断新老设备连接的码流不一样，MobileQuality存在就是新设备，不存在就是老设备

    private int shortConnRes = -1;// 设备短连接返回值

    private int ytSpeed = 100;// 2015.4.3设备云台转速

    private int cloudEnabled = -1; // 云存储服务开关

    private int channelBindFlag = 0; // 2015.4.18 设备通道数量正确与否 0：通道数正确 1.通道数不正确

    private boolean homeIPCMergeCode = true;// 2015.4.17是否融合后的家用 true ：融合 false
                                            // ：非融合

    private boolean homeIPCFlag = false;// 2015.4.17是否家用ipc true ：家用 false ：非家用

    public Device() {
        channelList = new MyList<Channel>(1);
        thirdDevList = new ArrayList<ThirdAlarmDev>();
    }

    /**
     * 创建已有一个通道的配置设备
     * 
     * @param gid
     * @param no
     */
    public Device(String gid, int no) {
        ip = Consts.IPC_DEFAULT_IP;
        port = Consts.IPC_DEFAULT_PORT;
        this.gid = gid;
        this.no = no;
        this.fullNo = gid + no;
        user = Consts.IPC_DEFAULT_USER;
        pwd = Consts.IPC_DEFAULT_PWD;

        // isHomeProduct = true;
        isHelperEnabled = false;

        channelList = new MyList<Channel>(1);
        channelList.add(new Channel(this, -1, 1, false, false, ""));

        thirdDevList = new ArrayList<ThirdAlarmDev>();
    }

    /**
     * 创建指定通道个数的起始索引的设备
     * 
     * @param ip
     * @param port
     * @param gid
     * @param no
     * @param user
     * @param pwd
     * @param isHomeProduct
     * @param channelCount
     * @param startWindowIndex
     */
    public Device(String ip, int port, String gid, int no, String user,
            String pwd, boolean isHomeProduct, int channelCount,
            int startWindowIndex, String devName) {
        this.ip = ip;
        this.port = port;
        this.gid = gid;

        if (-1 == no) {
            this.no = -1;
            this.fullNo = gid;
        } else {
            this.no = no;
            this.fullNo = gid + no;
        }

        if (null == devName || "".equalsIgnoreCase(devName)) {
            this.nickName = fullNo;
        } else {
            this.nickName = devName;
        }

        this.user = user;
        this.pwd = pwd;
        // this.isHomeProduct = isHomeProduct;

        isHelperEnabled = false;

        channelList = new MyList<Channel>(1);
        Channel channel = null;

        for (int i = 0; i < channelCount; i++) {
            channel = new Channel(this, startWindowIndex + i + 1, i + 1, false,
                    false, fullNo + "_" + (i + 1));
            channelList.add(channel);
        }

        thirdDevList = new ArrayList<ThirdAlarmDev>();
    }

    public MyList<Channel> getChannelList() {
        return channelList;
    }

    public ArrayList<ThirdAlarmDev> getThirdDevList() {
        return thirdDevList;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getGid() {
        return gid;
    }

    public int getNo() {
        return no;
    }

    public String getFullNo() {
        return fullNo;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    // public boolean isHomeProduct() {
    // return isHomeProduct;
    // }

    public void setHelperEnabled(boolean isHelperEnabled) {
        this.isHelperEnabled = isHelperEnabled;
    }

    // public void setHomeProduct(boolean isHomeProduct) {
    // this.isHomeProduct = isHomeProduct;
    // }

    public boolean isHelperEnabled() {
        return isHelperEnabled;
    }

    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();

        try {
            object.put("ip", ip);
            object.put("port", port);
            object.put("doMain", doMain);
            object.put("gid", gid);
            object.put("no", no);
            object.put("fullNo", fullNo);
            object.put("user", user);
            object.put("pwd", pwd);
            // object.put("isHomeProduct", isHomeProduct);
            object.put("deviceType", deviceType);
            object.put("is05", is05);
            object.put("enableTcpConnect", enableTcpConnect);
            object.put("nickName", nickName);
            object.put("deviceModel", deviceModel);// 设备型号
            object.put("deviceVerName", deviceVerName);// 设备软件版本
            object.put("deviceVerNum", deviceVerNum);// 设备软件版本号
            object.put("isDevice", isDevice);
            object.put("onlineStateNet", onlineStateNet);
            object.put("onlineStateLan", onlineStateLan);
            object.put("hasWifi", hasWifi);
            object.put("ytSpeed", ytSpeed);
            object.put("serverState", serverState);
            object.put("alarmSwitch", alarmSwitch);
            object.put("oldDevice", oldDevice);
            object.put("cloudEnabled", cloudEnabled);// 云存储
            object.put("channelBindFlag", channelBindFlag);// 获取通道数量是否正确
            try {
                ArrayList<Channel> list = channelList.toList();
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    array.put(i, list.get(i).toJson());
                }
                object.put("channelList", array);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public static JSONArray toJsonArray(ArrayList<Device> devList) {
        JSONArray devArray = new JSONArray();

        try {
            if (null != devList && 0 != devList.size()) {
                int size = devList.size();
                for (int i = 0; i < size; i++) {
                    devArray.put(i, devList.get(i).toJson());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return devArray;
    }

    public static String listToString(ArrayList<Device> devList) {
        return toJsonArray(devList).toString();
    }

    /**
     * JSON转成Device
     * 
     * @param string
     * @return
     */
    public static Device fromJson(String string) {
        Device dev = new Device();
        try {
            JSONObject object = new JSONObject(string);

            dev.setIp(ConfigUtil.getString(object, "ip"));
            dev.setPort(ConfigUtil.getInt(object, "port"));
            dev.setDoMain(ConfigUtil.getString(object, "doMain"));
            dev.setGid(ConfigUtil.getString(object, "gid"));
            dev.setNo(ConfigUtil.getInt(object, "no"));
            dev.setFullNo(ConfigUtil.getString(object, "fullNo"));
            dev.setEnableTcpConnect(ConfigUtil.getInt(object,
                    "enableTcpConnect"));
            dev.setUser(ConfigUtil.getString(object, "user"));
            dev.setPwd(ConfigUtil.getString(object, "pwd"));
            // dev.setHomeProduct(ConfigUtil.getBoolean(object,
            // "isHomeProduct"));
            // dev.setHelperEnabled(ConfigUtil.getBoolean(object,"isHelperEnabled"));
            dev.setDeviceType(ConfigUtil.getInt(object, "deviceType"));
            dev.setO5(ConfigUtil.getBoolean(object, "is05"));
            try {
                // [Neo] may not contains this value
                dev.setNickName(ConfigUtil.getString(object, "nickName"));
            } catch (Exception e) {
                dev.setNickName("");
                e.printStackTrace();
            }
            dev.setDeviceModel(ConfigUtil.getString(object, "deviceModel"));
            dev.setDeviceVerName(ConfigUtil.getString(object, "deviceVerName"));
            dev.setDeviceVerNum(ConfigUtil.getInt(object, "deviceVerNum"));
            dev.setIsDevice(ConfigUtil.getInt(object, "isDevice"));
            dev.setOnlineStateNet(ConfigUtil.getInt(object, "onlineStateNet"));
            dev.setOnlineStateLan(ConfigUtil.getInt(object, "onlineStateLan"));
            dev.setHasWifi(ConfigUtil.getInt(object, "hasWifi"));
            dev.setServerState(ConfigUtil.getInt(object, "serverState"));
            dev.setAlarmSwitch(ConfigUtil.getInt(object, "alarmSwitch"));
            dev.setYtSpeed(ConfigUtil.getInt(object, "ytSpeed"));
            dev.setOldDevice(ConfigUtil.getBoolean(object, "oldDevice"));
            dev.setChannelList(Channel.fromJsonArray(
                    ConfigUtil.getString(object, "channelList"), dev));

            dev.setChannelBindFlag(ConfigUtil.getInt(object, "channelBindFlag"));// 获取通道数是否正确
            dev.setCloudEnabled(ConfigUtil.getInt(object, "cloudEnabled"));// 云存储
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dev;
    }

    public static ArrayList<Device> fromJsonArray(String string) {
        ArrayList<Device> devList = new ArrayList<Device>();
        if (null == string || "".equalsIgnoreCase(string)) {
            return devList;
        }
        JSONArray devArray;
        try {
            devArray = new JSONArray(string);
            if (null != devArray && 0 != devArray.length()) {
                int length = devArray.length();
                for (int i = 0; i < length; i++) {
                    Device dev = fromJson(devArray.get(i).toString());
                    if (null != dev) {
                        devList.add(dev);
                        CacheUtil.setNickNameWithYstfn(dev.getFullNo(),
                                dev.getNickName());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return devList;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public boolean is05() {
        return is05;
    }

    public void setChannelList(MyList<Channel> channelList) {
        this.channelList = channelList;
    }

    public void setThirdDevList(ArrayList<ThirdAlarmDev> thirdDevList) {
        this.thirdDevList = thirdDevList;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public void setFullNo(String fullNo) {
        this.fullNo = fullNo;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setO5(boolean isO5) {
        this.is05 = isO5;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getIsDevice() {
        return isDevice;
    }

    public void setIsDevice(int isDevice) {
        this.isDevice = isDevice;
    }

    public int getHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(int hasWifi) {
        this.hasWifi = hasWifi;
    }

    public int getAlarmSwitch() {
        return alarmSwitch;
    }

    public void setAlarmSwitch(int alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }

    public long getPrimaryID() {
        return primaryID;
    }

    public void setPrimaryID(long primaryID) {
        this.primaryID = primaryID;
    }

    public int getServerState() {
        return serverState;
    }

    public void setServerState(int serverState) {
        this.serverState = serverState;
    }

    public String getDoMain() {
        return doMain;
    }

    public void setDoMain(String doMain) {
        this.doMain = doMain;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isCard() {
        return isCard;
    }

    public void setCard(boolean isCard) {
        this.isCard = isCard;
    }

    public boolean isJFH() {
        return isJFH;
    }

    public void setJFH(boolean isJFH) {
        this.isJFH = isJFH;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceVerName() {
        return deviceVerName;
    }

    public void setDeviceVerName(String deviceVerName) {
        this.deviceVerName = deviceVerName;
    }

    public int getDeviceVerNum() {
        return deviceVerNum;
    }

    public void setDeviceVerNum(int deviceVerNum) {
        this.deviceVerNum = deviceVerNum;
    }

    public boolean isIslanselect() {
        return islanselect;
    }

    public void setIslanselect(boolean islanselect) {
        this.islanselect = islanselect;
    }

    public boolean isHasAdded() {
        return hasAdded;
    }

    public void setHasAdded(boolean hasAdded) {
        this.hasAdded = hasAdded;
    }

    public boolean isOldDevice() {
        return oldDevice;
    }

    public void setOldDevice(boolean oldDevice) {
        this.oldDevice = oldDevice;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getOnlineStateNet() {
        return onlineStateNet;
    }

    public void setOnlineStateNet(int onlineStateNet) {
        this.onlineStateNet = onlineStateNet;
    }

    public int getOnlineStateLan() {
        return onlineStateLan;
    }

    public void setOnlineStateLan(int onlineStateLan) {
        this.onlineStateLan = onlineStateLan;
    }

    public int getShortConnRes() {
        return shortConnRes;
    }

    public void setShortConnRes(int shortConnRes) {
        this.shortConnRes = shortConnRes;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public int getEnableTcpConnect() {
        return enableTcpConnect;
    }

    public void setEnableTcpConnect(int enableTcpConnect) {
        this.enableTcpConnect = enableTcpConnect;
    }

    public int getYtSpeed() {
        return ytSpeed;
    }

    public void setYtSpeed(int ytSpeed) {
        this.ytSpeed = ytSpeed;
    }

    public int getCloudEnabled() {
        return this.cloudEnabled;
    }

    public void setCloudEnabled(int enabled) {
        this.cloudEnabled = enabled;
    }

    public boolean isHomeIPCMergeCode() {
        return homeIPCMergeCode;
    }

    public void setHomeIPCMergeCode(boolean homeIPCMergeCode) {
        this.homeIPCMergeCode = homeIPCMergeCode;
    }

    public boolean isHomeIPCFlag() {
        return homeIPCFlag;
    }

    public void setHomeIPCFlag(boolean homeIPCFlag) {
        this.homeIPCFlag = homeIPCFlag;
    }

    public int getChannelBindFlag() {
        return channelBindFlag;
    }

    public void setChannelBindFlag(int channelBindFlag) {
        this.channelBindFlag = channelBindFlag;
    }

}
