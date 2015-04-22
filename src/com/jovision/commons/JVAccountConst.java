
package com.jovision.commons;

public class JVAccountConst {
    /** 校验通过 */
    public static final int VALIDATIONUSERNAMETYPE_S = 0;
    /** 用户名长度只能在4-20位字符之间 */
    public static final int VALIDATIONUSERNAMETYPE_LENGTH_E = -1;
    /** 用户名不能全为数字 */
    public static final int VALIDATIONUSERNAMETYPE_NUMBER_E = -2;
    /** 用户名只能由英文、数字及“_”、“-”组成 */
    public static final int VALIDATIONUSERNAMETYPE_OTHER_E = -3;

    /** 注册/登录成功 */
    public static final int SUCCESS = 0;
    /** 注册/登录失败 */
    public static final int FAILED = -1000;
    /** 用户已经存在 */
    public static final int USER_HAS_EXIST = 2;
    /** 用户不存在 */
    public static final int USER_NOT_EXIST = 3;
    /** 手机号格式错误 */
    public static final int PHONE_NOT_TRUE = -15;
    /** 密码错误 */
    public static final int PASSWORD_ERROR = 4;
    /** 登录session不存在（登录已过期） */
    public static final int SESSION_NOT_EXSIT = 5;
    public static final int SQL_NOT_FIND = 6;
    public static final int PTCP_HAS_CLOSED = 7;
    /** 重置用户名密码 */
    public static final int RESET_NAME_AND_PASS = -16;
    /** 重置密码 */
    public static final int RESET_PASSWORD = -17;

    /** 设备平台类型：0:ios; 1:android */
    public static final String DEVICE_TYPE = "1";
    public static final int LOGIN_SUCCESS = 8;// --
    public static final int LOGIN_FAILED_1 = 9;// 用户名密码错
    public static final int LOGIN_FAILED_2 = 10;//

    /** 重置密码成功、失败 */
    public static final int RESET_PASSWORD_SUCCESS = 42;
    public static final int RESET_PASSWORD_FAILED = 43;

    /** 注册用户名检测成功、失败 */
    public static final int USERNAME_DETECTION_SUCCESS = 44;// --
    public static final int USERNAME_DETECTION_FAILED = 45;
    /** 邮箱不符合规则 */
    public static final int MAIL_DETECTION_FAILED = 46;

    /** 注册邮箱检测成功、失败 */
    public static final int EMAIL_DETECTION_SUCCESS = 47;
    public static final int EMAIL_DETECTION_FAILED = 48;

    /** 邮箱已注册过 */
    public static final int HAVE_REGISTED2 = 206;// --
    /** 注册成功 */
    public static final int REGIST_SUCCESS = 207;// --
    /** 注册失败 */
    public static final int REGIST_FAILED = 208;// --
    /** 账号已注册过 */
    public static final int HAVE_REGISTED = 209;// --
    /** 注册成功登陆成功 */
    public static final int REGIST_SUCCESS_LOGIN_SUCCESS = 210;
    /** 注册成功登陆失败 */
    public static final int REGIST_SUCCESS_LOGIN_FAILED = 211;
    /** 注册成功网络错误 */
    // public static final int REGIST_SUCCESS_LOGIN_NET_ERROR = 212;
    /** 更新数据库中的用户名 */
    public static final int LOGIN_USER_REFRESH = 213;
    /** （回调）提退 */
    public static final int OFFLINE_CALL_BACK = 214;
    /** 连接掉线 */
    public static final int TCP_ERROR_OFFLINE = 215;
    /** 绑定邮箱失败 */
    public static final int BOUND_EMAIL_FAILED = 216;
    /** 邮箱已被绑定，绑定失败 */
    public static final int BOUND_EMAIL_EXIST = 217;
    /** 只获取或刷新设备列表 */
    public static final int REFERSH_DEVICE_LIST_ONLY = 218;
    /** 只获取或刷新设备列表 */
    public static final int INITSDK_FAILED = 219;
    /** （回调）掉线 */
    public static final int OFFLINE_CALL_BACK2 = 220;
    /** 推送消息 标识 */
    public static final int MESSAGE_PUSH_TAG = 4602;
    /** 推送新协议 标示 */
    public static final int MESSAGE_NEW_PUSH_TAG = 4604;
    /** 异地登陆 */
    public static final int MESSAGE_OFFLINE = 4301;
    /** TCP错误 */
    public static final int PTCP_ERROR = 3103;
    /** TCP关闭 */
    public static final int PTCP_CLOSED = 3104;
    /** 保持在线登陆 */
    public static final int REKEEP_ONLINE_SUCCESS = 340;
    /** 保持在线登陆失败 */
    public static final int REKEEP_ONLINE_FAILED = 341;
    /** 默认宏 */
    public static final int DEFAULT = -255;
    /** 默认宏 */
    public static final int NOTEMAIL = 342;

}
