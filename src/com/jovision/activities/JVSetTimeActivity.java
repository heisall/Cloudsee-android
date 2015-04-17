
package com.jovision.activities;

import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.views.TimePopView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JVSetTimeActivity extends BaseActivity {

    private RelativeLayout dateType;
    private RelativeLayout timeType;
    private RelativeLayout dateTypeGone;
    private RelativeLayout timeTypeGone;
    private RelativeLayout intenttime;
    private ImageView intent_img;
    private TextView dateText;
    private TextView timeText;
    private LinearLayout linear;
    private TimePopView popupWindow; // 声明PopupWindow对象；
    private int isdatetype;
    public int showtime;
    public int window;
    public boolean isopen;
    public int opennum;
    public int nTimeFormat;
    private int START_YEAR = 1990;
    protected RelativeLayout.LayoutParams reParamstop2;

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {

    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {

    }

    @Override
    protected void initSettings() {
        setContentView(R.layout.set_time_layout);
        Bundle extras = getIntent().getExtras();
        showtime = Integer.valueOf(extras.getString("timetype"));
        window = extras.getInt("window");
        opennum = extras.getInt("opennum");
        nTimeFormat = extras.getInt("nTimeFormat");
    }

    @Override
    protected void initUi() {

        leftBtn = (Button) findViewById(R.id.btn_left);
        rightBtn = (Button) findViewById(R.id.btn_right);
        currentMenu = (TextView) findViewById(R.id.currentmenu);
        currentMenu.setText(getResources().getString(R.string.str_setting_time));
        dateType = (RelativeLayout) findViewById(R.id.date_type);
        timeType = (RelativeLayout) findViewById(R.id.time_type);
        dateTypeGone = (RelativeLayout) findViewById(R.id.date_typegone);
        timeTypeGone = (RelativeLayout) findViewById(R.id.time_typegone);
        dateText = (TextView) findViewById(R.id.date_text);
        intenttime = (RelativeLayout) findViewById(R.id.intenttime);
        intent_img = (ImageView) findViewById(R.id.intenttime_img);
        timeText = (TextView) findViewById(R.id.time_text);
        linear = (LinearLayout) findViewById(R.id.linear);

        // rightBtn.setOnClickListener(myOnClickListener);
        rightBtn.setVisibility(View.GONE);
        dateTypeGone.setOnClickListener(myOnClickListener);
        timeTypeGone.setOnClickListener(myOnClickListener);
        intenttime.setOnClickListener(myOnClickListener);
        dateType.setOnClickListener(myOnClickListener);
        timeType.setOnClickListener(myOnClickListener);
        leftBtn.setOnClickListener(myOnClickListener);

        if (0 == opennum) {
            intent_img.setImageResource(R.drawable.morefragment_normal_icon);
            dateTypeGone.setVisibility(View.GONE);
            timeTypeGone.setVisibility(View.GONE);
            isopen = true;
        } else if (1 == opennum) {
            intent_img.setImageResource(R.drawable.morefragment_selector_icon);
            dateTypeGone.setVisibility(View.VISIBLE);
            timeTypeGone.setVisibility(View.VISIBLE);
            isopen = false;
        }

        int width = disMetrics.widthPixels;
        rightBtn.setTextColor(Color.WHITE);
        rightBtn.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.setting_save));
        rightBtn.setText(getResources().getString(
                R.string.str_finish));
        reParamstop2 = new RelativeLayout.LayoutParams(
                width / 5, LayoutParams.WRAP_CONTENT);
        reParamstop2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        reParamstop2.addRule(RelativeLayout.CENTER_VERTICAL);
        reParamstop2.setMargins(0, 0, 30, 0);
        rightBtn.setLayoutParams(reParamstop2);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);

        MySharedPreference.putInt("timetype", nTimeFormat);
        if (1 == MySharedPreference.getInt("timetype")) {
            dateText.setText("YYYY-MM-DD");
        } else if (2 == MySharedPreference.getInt("timetype")) {
            dateText.setText("DD/MM/YYYY");
        } else if (0 == MySharedPreference.getInt("timetype")) {
            dateText.setText("MM/DD/YYYY");
        }
        timeText.setText(str);

        // 初始化话时间表 设置值为 -1;
        MySharedPreference.putInt("year", -1);
        MySharedPreference.putInt("month", -1);
        MySharedPreference.putInt("day", -1);
        MySharedPreference.putInt("hour", -1);
        MySharedPreference.putInt("mins", -1);
    }

    /**
	 * 
	 */
    OnClickListener myOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.date_typegone:
                    break;
                case R.id.time_typegone:
                    break;
                case R.id.intenttime:
                    String params = "";// 0关，1开
                    if (!isopen) {
                        params = "bSntp=" + 0 + ";";
                        intent_img.setImageResource(R.drawable.morefragment_normal_icon);
                        isopen = true;
                        dateTypeGone.setVisibility(View.GONE);
                        timeTypeGone.setVisibility(View.GONE);
                        Jni.sendString(window,
                                JVNetConst.JVN_RSP_TEXTDATA,
                                false, 0, Consts.TYPE_SET_PARAM, params);
                    } else {
                        intent_img.setImageResource(R.drawable.morefragment_selector_icon);
                        isopen = false;
                        params = "bSntp=" + 1 + ";";
                        Jni.sendString(window,
                                JVNetConst.JVN_RSP_TEXTDATA,
                                false, 0, Consts.TYPE_SET_PARAM, params);
                        dateTypeGone.setVisibility(View.VISIBLE);
                        timeTypeGone.setVisibility(View.VISIBLE);

                        MySharedPreference.putInt("year", -1);
                        MySharedPreference.putInt("month", -1);
                        MySharedPreference.putInt("day", -1);
                        MySharedPreference.putInt("hour", -1);
                        MySharedPreference.putInt("mins", -1);

                        switch (MySharedPreference.getInt("timetype")) {
                            case 2:
                                SimpleDateFormat formatter = new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss");
                                Date curDate = new Date(System.currentTimeMillis());
                                String str = formatter.format(curDate);
                                timeText.setText(str);
                                break;
                            case 1:
                                SimpleDateFormat formatter1 = new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss");
                                Date curDate1 = new Date(System.currentTimeMillis());
                                String str1 = formatter1.format(curDate1);
                                timeText.setText(str1);
                                break;
                            case 0:
                                SimpleDateFormat formatter2 = new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss");
                                Date curDate2 = new Date(System.currentTimeMillis());
                                String str2 = formatter2.format(curDate2);
                                timeText.setText(str2);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case R.id.btn_left:
                    finish();
                    break;
                case R.id.pop_outside:
                    popupWindow.dismiss();
                    break;
                case R.id.date_type:
                    isdatetype = 1;
                    popupWindow = new TimePopView(JVSetTimeActivity.this, myOnClickListener,
                            isdatetype);
                    popupWindow.setBackgroundDrawable(null);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.showAtLocation(linear, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
                    if (1 == showtime) {
                        popupWindow.relativeThree.setVisibility(View.GONE);
                    }
                    SettingTime();
                    break;
                case R.id.time_type:
                    isdatetype = 0;
                    popupWindow = new TimePopView(JVSetTimeActivity.this, myOnClickListener,
                            isdatetype);
                    popupWindow.setBackgroundDrawable(null);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.showAtLocation(linear, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
                    break;
                case R.id.save:
                    if (0 == isdatetype) {
                        timeText.setText(popupWindow.wheelMain.getTime());
                    } else if (1 == isdatetype) {
                        if (-1 != MySharedPreference.getInt("year")) {
                            StringBuffer sb = new StringBuffer();
                            sb.append(MySharedPreference.getInt("year") + START_YEAR)
                                    .append("-")
                                    .append(MySharedPreference.getInt("month") + 1).append("-")
                                    .append(MySharedPreference.getInt("day") + 1).append(" ")
                                    .append(MySharedPreference.getInt("hour")).append(":")
                                    .append(MySharedPreference.getInt("mins")).append(":")
                                    .append("00");
                            // if (1 == MySharedPreference.getInt("timetype")) {
                            //
                            // } else if (2 ==
                            // MySharedPreference.getInt("timetype")) {
                            // sb.append(MySharedPreference.getInt("day") +
                            // 1).append("/")
                            // .append(MySharedPreference.getInt("month") +
                            // 1).append("/")
                            // .append(MySharedPreference.getInt("year") +
                            // START_YEAR)
                            // .append(" ")
                            // .append(MySharedPreference.getInt("hour")).append(":")
                            // .append(MySharedPreference.getInt("mins")).append(":")
                            // .append("00");
                            // } else if (0 ==
                            // MySharedPreference.getInt("timetype")) {
                            // sb.append(MySharedPreference.getInt("month") +
                            // 1).append("/")
                            // .append(MySharedPreference.getInt("day") +
                            // 1).append("/")
                            // .append(MySharedPreference.getInt("year") +
                            // START_YEAR)
                            // .append(" ")
                            // .append(MySharedPreference.getInt("hour")).append(":")
                            // .append(MySharedPreference.getInt("mins")).append(":")
                            // .append("00");
                            // }
                            timeText.setText(sb.toString());
                        }
                        // else {
                        // switch (MySharedPreference.getInt("timetype")) {
                        // case 2:
                        // SimpleDateFormat formatter = new SimpleDateFormat(
                        // "dd/MM/yyyy HH:mm:ss");
                        // Date curDate = new Date(System.currentTimeMillis());
                        // String str = formatter.format(curDate);
                        // timeText.setText(str);
                        // break;
                        // case 1:
                        // SimpleDateFormat formatter1 = new SimpleDateFormat(
                        // "yyyy-MM-dd HH:mm:ss");
                        // Date curDate1 = new Date(System.currentTimeMillis());
                        // String str1 = formatter1.format(curDate1);
                        // timeText.setText(str1);
                        // break;
                        // case 0:
                        // SimpleDateFormat formatter2 = new SimpleDateFormat(
                        // "MM/dd/yyyy HH:mm:ss");
                        // Date curDate2 = new Date(System.currentTimeMillis());
                        // String str2 = formatter2.format(curDate2);
                        // timeText.setText(str2);
                        // break;
                        // default:
                        // break;
                        // }
                        // }
                    }
                    Savetime();
                    popupWindow.dismiss();
                    break;
                case R.id.quit:
                    popupWindow.dismiss();
                    break;

                case R.id.relativeone:
                    popupWindow.relativeOne_img.setVisibility(View.VISIBLE);
                    popupWindow.relativeTwo_img.setVisibility(View.GONE);
                    popupWindow.relativeThree_img.setVisibility(View.GONE);
                    popupWindow.Currenttype.setText("(MM/DD/YYYY)");
                    dateText.setText("MM/DD/YYYY");
                    MySharedPreference.putInt("timetype", 0);
                    break;
                case R.id.relativetwo:
                    popupWindow.relativeOne_img.setVisibility(View.GONE);
                    popupWindow.relativeTwo_img.setVisibility(View.VISIBLE);
                    popupWindow.relativeThree_img.setVisibility(View.GONE);
                    popupWindow.Currenttype.setText("(YYYY-MM-DD)");
                    dateText.setText("YYYY-MM-DD");
                    MySharedPreference.putInt("timetype", 1);
                    break;
                case R.id.relativethree:
                    popupWindow.relativeOne_img.setVisibility(View.GONE);
                    popupWindow.relativeTwo_img.setVisibility(View.GONE);
                    popupWindow.relativeThree_img.setVisibility(View.VISIBLE);
                    popupWindow.Currenttype.setText("(DD/MM/YYYY)");
                    dateText.setText("DD/MM/YYYY");
                    MySharedPreference.putInt("timetype", 2);
                    break;
                default:
                    break;
            }
        }
    };

    private void Savetime() {
        String time = MySharedPreference.getInt("timetype") + ":"
                + timeText.getText().toString();
        MyLog.i("TAG", time + "时间格式");
        Jni.sendSuperBytes(window,
                JVNetConst.JVN_RSP_TEXTDATA,
                false,
                time.getBytes().length,
                JVNetConst.RC_SETSYSTEMTIME, 0, 0, 0,
                time.getBytes(), time.getBytes().length);
        showTextToast(getResources().getString(R.string.str_setting_time_success));
    }

    private void SettingTime() {
        if (1 == MySharedPreference.getInt("timetype")) {
            popupWindow.relativeTwo_img.setVisibility(View.VISIBLE);
            popupWindow.relativeOne_img.setVisibility(View.GONE);
            popupWindow.relativeThree_img.setVisibility(View.GONE);
            popupWindow.Currenttype.setText("(YYYY-MM-DD)");
            dateText.setText("YYYY-MM-DD");
        } else if (2 == MySharedPreference.getInt("timetype")) {
            popupWindow.relativeOne_img.setVisibility(View.GONE);
            popupWindow.relativeThree_img.setVisibility(View.VISIBLE);
            popupWindow.relativeTwo_img.setVisibility(View.GONE);
            popupWindow.Currenttype.setText("(DD/MM/YYYY)");
            dateText.setText("DD/MM/YYYY");
        } else if (0 == MySharedPreference.getInt("timetype")) {
            popupWindow.relativeThree_img.setVisibility(View.GONE);
            popupWindow.relativeTwo_img.setVisibility(View.GONE);
            popupWindow.relativeOne_img.setVisibility(View.VISIBLE);
            popupWindow.Currenttype.setText("(MM/DD/YYYY)");
            dateText.setText("MM/DD/YYYY");
        }
    }

    @Override
    protected void saveSettings() {

    }

    @Override
    protected void freeMe() {
        MySharedPreference.putInt("timetype", -1);
    }

}
