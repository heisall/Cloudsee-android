
package com.jovision.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class TimeUtil {
    public static String TIME = "06:00:00--08:00:00";
    public static int SPANTIME = 2;
    public static int SPANDAY = 3;

    /**
     * @param time �趨��ʱ��
     * @return�Ա�ʱ�� ���ѡ���time�ڵ�ǰ����SPANTIME��Сʱ֮�� ������
     */
    public static boolean timeCompare(String time) {

        Date setdate = parseTime(time.trim());
        Date currentdate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentdate);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)
                + SPANTIME);

        currentdate = calendar.getTime();

        if (setdate.after(currentdate)) {
            Log.e("����1���ѡ���time�ڵ�ǰ����SPANTIME��Сʱ֮�� ������", "t");
            return true;
        }
        Log.e("����1", "f");
        return false;

    }

    /**
     * @param time �趨��ʱ��
     * @return�Ա�ʱ�� ���ѡ���timeû�г���ӵ�ǰʱ�������3�� ������
     */
    public static boolean timeCompare3Days(String time) {

        Date setdate = parseTime(time);
        Date currentdate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(setdate);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)
                - SPANDAY);

        setdate = calendar.getTime();
        Log.e("����2", "currentdate" + currentdate.toGMTString() + "      "
                + setdate.toGMTString());

        if (setdate.before(currentdate)) {
            Log.e("����2", " ���ѡ���timeû�г���ӵ�ǰʱ�������3�� ������");
            return true;

        }
        Log.e("����2", "f");
        return false;

    }

    /**
     * @return����ǰʱ��Ϊdate
     */
    public static Date parseTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @return��ʽ����ǰ���ں�ʱ��Ϊ�ַ�
     */
    private static String mCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currenttime = df.format(new Date());
        return currenttime;
    }

    /**
     * @return��ʽ����ǰ����Ϊ�ַ�
     */
    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
        String currenttime = df.format(new Date());
        return currenttime;
    }

}
