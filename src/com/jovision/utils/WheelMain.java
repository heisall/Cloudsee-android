
package com.jovision.utils;

import android.view.View;

import com.jovetech.CloudSee.temp.R;
import com.jovision.adapters.NumericWheelAdapter;
import com.jovision.commons.MySharedPreference;
import com.jovision.views.WheelView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class WheelMain {

    private View view;
    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;
    private WheelView wv_hours;
    private WheelView wv_mins;
    private static int START_YEAR = 1990, END_YEAR = 2100;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public static int getSTART_YEAR() {
        return START_YEAR;
    }

    public static void setSTART_YEAR(int sTART_YEAR) {
        START_YEAR = sTART_YEAR;
    }

    public static int getEND_YEAR() {
        return END_YEAR;
    }

    public static void setEND_YEAR(int eND_YEAR) {
        END_YEAR = eND_YEAR;
    }

    public WheelMain(View view) {
        super();

        this.view = view;
        setView(view);
    }

    /**
     * @Description: TODO 弹出日期时间选择器
     */
    public void initDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);

        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {
                "1", "3", "5", "7", "8", "10", "12"
        };
        String[] months_little = {
                "4", "6", "9", "11"
        };

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        // 年
        wv_year = (WheelView) view.findViewById(R.id.year);
        wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
        wv_year.setCyclic(true);// 可循环滚动
        if (-1 != MySharedPreference.getInt("year")) {
            wv_year.setCurrentItem(MySharedPreference.getInt("year"));// 初始化时显示的数据
        } else {
            wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
        }

        // 月
        wv_month = (WheelView) view.findViewById(R.id.month);
        wv_month.setAdapter(new NumericWheelAdapter(1, 12));
        wv_month.setCyclic(true);
        if (-1 != MySharedPreference.getInt("month")) {
            wv_month.setCurrentItem(MySharedPreference.getInt("month"));
        } else {
            wv_month.setCurrentItem(month);
        }

        // 日
        wv_day = (WheelView) view.findViewById(R.id.day);
        wv_day.setCyclic(true);

        // 时
        wv_hours = (WheelView) view.findViewById(R.id.hour);
        wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        wv_hours.setCyclic(true);
        if (-1 != MySharedPreference.getInt("hour")) {
            wv_hours.setCurrentItem(MySharedPreference.getInt("hour"));
        } else {
            wv_hours.setCurrentItem(hour);
        }

        // 分
        wv_mins = (WheelView) view.findViewById(R.id.minute);
        wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
        wv_mins.setCyclic(true);
        if (-1 != MySharedPreference.getInt("mins")) {
            wv_mins.setCurrentItem(MySharedPreference.getInt("mins"));
        } else {
            wv_mins.setCurrentItem(mins);
        }
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 31));
        } else if (list_little.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 30));
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wv_day.setAdapter(new NumericWheelAdapter(1, 29));
            else
                wv_day.setAdapter(new NumericWheelAdapter(1, 28));
        }
        if (-1 != MySharedPreference.getInt("day")) {
            wv_day.setCurrentItem(MySharedPreference.getInt("day"));
        } else {
            wv_day.setCurrentItem(day - 1);
        }

        // 添加"年"监听
        OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int year_num = newValue + START_YEAR;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big
                        .contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(wv_month
                        .getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0)
                            || year_num % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                }
            }
        };
        // 添加"月"监听
        OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int month_num = newValue + 1;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
                            .getCurrentItem() + START_YEAR) % 100 != 0)
                            || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                }
            }
        };
        wv_year.addChangingListener(wheelListener_year);
        wv_month.addChangingListener(wheelListener_month);

        // 根据屏幕密度来指定选择器字体的大小
        int textSize = 0;

        textSize = 15;

        wv_day.TEXT_SIZE = textSize;
        wv_month.TEXT_SIZE = textSize;
        wv_year.TEXT_SIZE = textSize;
        wv_hours.TEXT_SIZE = textSize;
        wv_mins.TEXT_SIZE = textSize;

    }

    public String getTime(int Count) {
        StringBuffer sb = new StringBuffer();
        MySharedPreference.putInt("year", wv_year.getCurrentItem());
        MySharedPreference.putInt("month", wv_month.getCurrentItem());
        MySharedPreference.putInt("day", wv_day.getCurrentItem());
        MySharedPreference.putInt("hour", wv_hours.getCurrentItem());
        MySharedPreference.putInt("mins", wv_mins.getCurrentItem());
        if (2 == Count) {
            sb.append((wv_day.getCurrentItem() + 1)).append("/")
                    .append((wv_month.getCurrentItem() + 1)).append("/")
                    .append((wv_year.getCurrentItem() + START_YEAR)).append(" ")
                    .append((wv_hours.getCurrentItem())).append(":")
                    .append((wv_mins.getCurrentItem())).append(":")
                    .append("00");
        } else if (1 == Count) {
            sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
                    .append((wv_month.getCurrentItem() + 1)).append("-")
                    .append((wv_day.getCurrentItem() + 1)).append(" ")
                    .append((wv_hours.getCurrentItem())).append(":")
                    .append((wv_mins.getCurrentItem())).append(":")
                    .append("00");
        } else if (0 == Count) {
            sb.append((wv_month.getCurrentItem() + 1)).append("/")
                    .append((wv_day.getCurrentItem() + 1)).append("/")
                    .append((wv_year.getCurrentItem() + START_YEAR)).append(" ")
                    .append((wv_hours.getCurrentItem())).append(":")
                    .append((wv_mins.getCurrentItem())).append(":")
                    .append("00");
        }
        return sb.toString();
    }
}
