
package android.test;

/**
 * 账号系统接口
 * 
 * @author Administrator
 */
public class JVACCOUNT {
    /** ================= 账号业务 ============= **/
    /*
     * static { System.loadLibrary("gnustl_shared");
     * System.loadLibrary("EventSo.2.0.21"); System.loadLibrary("Json7.6.1");
     * System.loadLibrary("stlport_shared"); System.loadLibrary("accountsdk"); }
     */
    public static JVACCOUNT jvAccount = new JVACCOUNT();

    public static JVACCOUNT getInstance() {
        if (jvAccount == null) {
            jvAccount = new JVACCOUNT();
        }
        return jvAccount;
    }

    public static native boolean InitSDK(Object handle, String path);

    // 注册账号服务回调函数
    public static native void JVRegisterCallBack(String classUrl,
            String onlineCallBack, String pushCallBack);

    public static native void UninitSDK();

    // 服务器ip
    public static native boolean ConfigServerAddress(String shortConnectIp,
            String longConnectIp);

    /**
     * 服务器IP写入本地
     * 
     * @param ip new出来的String
     * @return 解析出的ip
     */
    public static native String GetServerIP();

    /**
     * 从本地取ip登录
     * 
     * @param ip 本地存的ip
     * @return
     */
    public static native void SetServerIP(String onlineIp, String channelIp);

    /**
     * 1-判断用户是否存在
     * 
     * @param userName用户名
     * @return
     */
    public static native int IsUserExists(String userName);

    /**
     * 2-用户注册 2014-04-22
     * 
     * @param user 用户基本信息（目前只包括用户名和邮箱），用户密码（由上层直接传入加密的密码）
     * @return
     */
    public static native int UserRegister(String username, String password,
            String customtype);

    /**
     * 3-用户登录 2014-04-22
     * 
     * @param userName用户名
     * @param pwd密码
     * @param deviceType客户端相关信息
     * @return
     */
    public static native int UserLogin(String userName, String pwd);

    /**
     * 2014-04-22 汇报设备类型
     */
    public static native int ReportClientPlatformInfo(Object deviceType);

    /**
     * 2014-04-22 注册绑定邮箱
     */
    public static native int BindMailOrPhone(String mail);

    /**
     * 4-用户上线
     * 
     * @return
     */
    public static native int Online();

    /**
     * 5-修改用户密码
     * 
     * @param oldPwd旧密码
     * @param newPwd新密码
     * @return
     */
    public static native int ModifyUserPassword(String oldPwd, String newPwd);

    /**
     * 6-注销用户 0是成功 -3是缓存失败。 非超时的话就这俩返回值
     * 
     * @return
     */
    public static native int UserLogout();

    /**
     * 7-判断用户密码强度
     * 
     * @param userName 用户名
     * @return int 3:用户不存在 118:用户名不符合规范 119:用户名符合规范
     */
    public static native int JudgeUserPasswordStrength(String userName);

    /**
     * 8-不符合规则用户用老用户登陆方式
     * 
     * @param userName 用户名
     * @param password 密码
     * @return int -16:重置用户名密码 -17:重置密码
     */
    public static native int OldUserLogin(String userName, String password);

    /**
     * 9-重置用户名密码
     * 
     * @param userName 用户名
     * @param password 密码
     * @return int 0：成功
     */
    public static native int ResetUserNameAndPassword(String userName,
            String password);

    /**
     * 10-重置密码
     * 
     * @param userName 用户名
     * @param password 密码
     * @return int 0：成功
     */
    public static native int ResetUserPassword(String password, String userName);

    /**
     * 9-注册推送接口
     * 
     * @return
     */
    public static native int RegisterServerPushFunc();

    /**
     * 告警接口短连接，调用
     * 
     * @param tag 2：告警
     * @param request
     * @param response
     * @return
     */
    public static native int GetResponseByRequestShortConnectionServer(int tag,
            String request, byte[] response);

    /**
     * 告警接口长连接，调用
     * 
     * @param tag 2：告警
     * @param request
     * @param response
     * @return
     */
    public static native int GetResponseByRequestPersistConnectionServer(
            int tag, String request, byte[] response);

    /**
     * 设备接口短连接，调用
     * 
     * @param request
     * @param response
     * @return
     */
    // public static native int GetResponseByRequestDeviceShortConnectionServer(
    // String request, byte[] response);
    /**
     * 设备接口短连接V2，调用
     * 
     * @param request
     * @param response
     * @return
     */
    public static native String GetResponseByRequestDeviceShortConnectionServerV2(
            String request);

    /**
     * 设备接口长连接，调用
     * 
     * @param request
     * @param response
     * @return
     */
    // public static native int
    // GetResponseByRequestDevicePersistConnectionServer(
    // String request, byte[] response);
    /**
     * 设备接口长连接V2，调用
     * 
     * @param request
     * @param response
     * @return
     */
    public static native String GetResponseByRequestDevicePersistConnectionServerV2(
            String request);

    /**
     * ALARM_ON = 0, ALARM_OFF = 1,
     * 
     * @brief 得到当前报警是否开启
     * @param [out] 报警开启标识 (枚举 alarmflag）
     */
    public static native int GetCurrentAlarmFlag(byte[] alarm_flag);//

    /**
     * ALARM_ON = 0, ALARM_OFF = 1,
     * 
     * @brief 设置报警标识（关闭或是开启）
     * @param [in] 报警开启标识 (枚举 alarmflag）
     * @param [in] 设备唯一标识
     */
    public static native int SetCurrentAlarmFlag(int alarm_flag, String mobileId);

    /**
     * 停止心跳--2014-3.21
     */
    public static native void StopHeartBeat();

    /**
     * 获取账号信息
     * 
     * @return 账号信息的json
     */
    public static native String GetAccountInfo();

    public static native String onLoginProcess(int reserve, String reqJson);

    public static native String onLoginProcessV2(int reserve, String reqJson);

    public static native String GetSession();

    public static native int SetUserOnlineStatus(int online_state);

    public static native String GetMailPhoneNoSession(String account_name);

    public static native int ResetUserPasswordNoSession(String password,
            String account_name);

    public static native String GetVersion(int aaa);

    public static native String GetDevicesOnlineStatus();

    public static native int ModifyMailPhone(int type, String MailorPhone);

    public static native int SendResetMail(String Mail);

    public static native int RandCodeCheck(String randCode);

    // reqJson:{"user":"111","phone":"18668923911","mail":"","nick":"nicheng"}
    public static native int SetAccountInfo(String reqJson);
    
    public static native int ReportUserFlow(String account, String guid, int channel, int flow_type, int flow);
}
