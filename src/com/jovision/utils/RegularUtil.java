
package com.jovision.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtil {
    /**
     * 验证云通号
     * 
     * @param ystEdit
     * @return
     */
    public static boolean checkYSTNum(String ystEdit) {
        boolean flag = true;

        try {
            int kkk;
            for (kkk = 0; kkk < ystEdit.length(); kkk++) {
                char c = ystEdit.charAt(kkk);
                if (c <= '9' && c >= '0') {
                    break;
                }
            }
            String group = ystEdit.substring(0, kkk);
            String yst = ystEdit.substring(kkk);
            for (int mm = 0; mm < group.length(); mm++) {
                char c = ystEdit.charAt(mm);
                if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {

                } else {
                    flag = false;
                }
            }

            for (int i = 0; i < yst.length(); i++) {
                char c = yst.charAt(i);
                if ((c >= '0' && c <= '9')) {

                } else {
                    flag = false;
                }
            }
            int ystValue = "".equals(yst) ? 0 : Integer.parseInt(yst);
            if (kkk >= 4 || kkk <= 0 || ystValue <= 0) {
                flag = false;
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;

    }

    /**
     * 检查ip地址格式是否正确
     * 
     * @author suifupeng
     * @param ipAdress
     * @return
     */
    public static boolean checkIPAdress(String ipAddress) {
        if (ipAddress
                .matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查端口号格式是否正确
     * 
     * @author suifupeng
     * @param portNum
     * @return
     */
    public static boolean checkPortNum(String portNum) {
        if (portNum.length() < 1 || portNum.length() > 5) {
            return false;
        }
        for (int i = 0; i < portNum.length(); i++) {
            char c = portNum.charAt(i);
            if (c > '9' || c < '0') {
                return false;
            }
        }
        if (Integer.valueOf(portNum).intValue() <= 0
                && Integer.valueOf(portNum).intValue() > 65535) {
            return false;
        }
        return true;
    }

    // 验证昵称
    public static boolean checkNickName(String str) {
        boolean flag = false;
        try {
            byte[] b = str.getBytes("UTF-8");
            str = new String(b, "UTF-8");
            Pattern pattern = Pattern
                    .compile("^[A-Za-z0-9_.()\\+\\-\\u4e00-\\u9fa5]{1,24}$");
            Matcher matcher = pattern.matcher(str);
            if (matcher.matches() && 24 >= str.getBytes().length) {// 3*8
                flag = true;
            } else {
                flag = false;
            }
        } catch (UnsupportedEncodingException e) {
            flag = false;
        }
        return flag;
    }

    // 验证设备用户名
    public static boolean checkDeviceUsername(String str) {
        boolean flag = false;
        try {
            byte[] b = str.getBytes("UTF-8");
            str = new String(b, "UTF-8");
            Pattern pattern = Pattern.compile("^[A-Za-z0-9_\\-]{1,16}$");
            Matcher matcher = pattern.matcher(str);
            if (matcher.matches()) {
                flag = true;
            } else {
                flag = false;
            }
        } catch (UnsupportedEncodingException e) {
            flag = false;
        }
        // if(0 < str.length() && 16 > str.length()){
        // flag = true;
        // }

        return flag;

    }

    // 验证设备密码
    public static boolean checkDevicePwd(String str) {
        boolean flag = false;
        // try {
        // byte[] b = str.getBytes("UTF-8");
        // str = new String(b, "UTF-8");
        // Pattern pattern = Pattern.compile("^[A-Za-z0-9_\\-]{0,15}$");
        // Matcher matcher = pattern.matcher(str);
        // if (matcher.matches()) {
        // flag = true;
        // } else {
        // flag = false;
        // }
        // } catch (UnsupportedEncodingException e) {
        // flag = false;
        // }
        if (0 <= str.length() && 16 > str.length()) {
            flag = true;
        }

        return flag;

    }
}
