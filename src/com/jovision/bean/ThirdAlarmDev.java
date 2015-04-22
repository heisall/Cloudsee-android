
package com.jovision.bean;

import java.io.Serializable;

public class ThirdAlarmDev implements Serializable {

    private static final long serialVersionUID = 1L;
    public int dev_uid = 0; // 服务器分配的唯一标识
    public String dev_nick_name = ""; // 昵称 e.g:XX南门
    public int dev_type_mark = 0; // 设备型号代号 e.g: 1 门禁 2手环
    public String dev_type_name = ""; // 设备型号称呼 e.g: 门禁 手环
    public int dev_safeguard_flag = 0; // 设备安全防护开关 0 off 1 on
    public String dev_belong_yst = "";// 所属云视通
}
