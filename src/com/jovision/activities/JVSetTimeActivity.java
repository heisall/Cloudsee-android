package com.jovision.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Jni;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MySharedPreference;
import com.jovision.views.TimePopView;

public class JVSetTimeActivity extends BaseActivity{

	private RelativeLayout dateType;
	private RelativeLayout timeType;
	private TextView dateText;
	private TextView timeText;
	private LinearLayout linear;
	private TimePopView popupWindow; // 声明PopupWindow对象；
	private int isdatetype;
	public int showtime;
	public int window;
	private  int START_YEAR = 1990;
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
	}

	@Override
	protected void initUi() {

		leftBtn = (Button)findViewById(R.id.btn_left);
		rightBtn = (Button)findViewById(R.id.btn_right);
		currentMenu = (TextView)findViewById(R.id.currentmenu);
		currentMenu.setText(getResources().getString(R.string.str_setting_time));
		dateType = (RelativeLayout)findViewById(R.id.date_type);
		timeType = (RelativeLayout)findViewById(R.id.time_type);
		dateText = (TextView)findViewById(R.id.date_text);
		timeText = (TextView)findViewById(R.id.time_text);
		linear = (LinearLayout)findViewById(R.id.linear);

		dateType.setOnClickListener(myOnClickListener);
		timeType.setOnClickListener(myOnClickListener);
		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn.setOnClickListener(myOnClickListener);

		 int width = disMetrics.widthPixels;
		rightBtn.setText(getResources().getString(R.string.str_setting_submittime));
		rightBtn.setTextSize(16.0f);
		rightBtn.setTextColor(getResources().getColor(R.color.more_fragment_color3));
		rightBtn.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.setting_save));
		reParamstop2 = new RelativeLayout.LayoutParams(
				width/5, LayoutParams.WRAP_CONTENT);
		reParamstop2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		reParamstop2.addRule(RelativeLayout.CENTER_VERTICAL);
		reParamstop2.setMargins(0, 0, 20, 0);
		rightBtn.setLayoutParams(reParamstop2);


		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);

		MySharedPreference.putInt("timetype", 1);
		dateText.setText("YYYY-MM-DD");
		timeText.setText(str);

		//初始化话时间表 设置值为 -1;
		MySharedPreference.putInt("year",  -1);
		MySharedPreference.putInt("month",  -1);
		MySharedPreference.putInt("day",  -1);
		MySharedPreference.putInt("hour",  -1);
		MySharedPreference.putInt("mins",  -1);
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_right:
				String time = MySharedPreference.getInt("timetype")+":"+timeText.getText().toString();
				Jni.sendSuperBytes(window,
						JVNetConst.JVN_RSP_TEXTDATA, 
						false,
						time.getBytes().length,   								
						JVNetConst.RC_SETSYSTEMTIME, 0, 0, 0, 		
						time.getBytes(), time.getBytes().length);
				showTextToast(getResources().getString(R.string.str_setting_time_success));
				finish();
				break;
			case R.id.pop_outside:
				popupWindow.dismiss();
				break;
			case R.id.date_type:
				isdatetype = 1;
				popupWindow = new TimePopView(JVSetTimeActivity.this,myOnClickListener,isdatetype);
				popupWindow.setBackgroundDrawable(null);
				popupWindow.setOutsideTouchable(true);
				popupWindow.showAtLocation(linear, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				if (1 == showtime) {
					popupWindow.relativeTwo.setVisibility(View.GONE);
				}
				if (1 == MySharedPreference.getInt("timetype")) {
					popupWindow.relativeOne_img.setVisibility(View.VISIBLE);
					popupWindow.relativeTwo_img.setVisibility(View.GONE);
					popupWindow.relativeThree_img.setVisibility(View.GONE);
					popupWindow.Currenttype.setText("(YYYY-MM-DD)");
					dateText.setText("YYYY-MM-DD");
				}else if (2 == MySharedPreference.getInt("timetype")) {
					popupWindow.relativeOne_img.setVisibility(View.GONE);
					popupWindow.relativeTwo_img.setVisibility(View.VISIBLE);
					popupWindow.relativeThree_img.setVisibility(View.GONE);
					popupWindow.Currenttype.setText("(DD/MM/YYYY)");
					dateText.setText("DD/MM/YYYY");
				}else if (0 == MySharedPreference.getInt("timetype")){
					popupWindow.relativeOne_img.setVisibility(View.GONE);
					popupWindow.relativeTwo_img.setVisibility(View.GONE);
					popupWindow.relativeThree_img.setVisibility(View.VISIBLE);
					popupWindow.Currenttype.setText("(MM/DD/YYYY)");
					dateText.setText("MM/DD/YYYY");
				}
				break;
			case R.id.time_type:
				isdatetype = 0;
				popupWindow = new TimePopView(JVSetTimeActivity.this,myOnClickListener,isdatetype);
				popupWindow.setBackgroundDrawable(null);
				popupWindow.setOutsideTouchable(true);
				popupWindow.showAtLocation(linear, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				break;
			case R.id.save:
				if (0 ==  isdatetype) {
					timeText.setText(popupWindow.wheelMain.getTime(MySharedPreference.getInt("timetype")));
				}else if (1 == isdatetype) {
					if (-1 != MySharedPreference.getInt("year")) {
						StringBuffer sb = new StringBuffer();
						if (1 == MySharedPreference.getInt("timetype")) {
							sb.append(MySharedPreference.getInt("year")+START_YEAR).append("-")
							.append(MySharedPreference.getInt("month")).append("-")
							.append(MySharedPreference.getInt("day")).append(" ")
							.append(MySharedPreference.getInt("hour")).append(":")
							.append(MySharedPreference.getInt("mins")).append(":")
							.append("00");
						}else if (2 == MySharedPreference.getInt("timetype")) {
							sb.append(MySharedPreference.getInt("day")).append("/")
							.append(MySharedPreference.getInt("month")).append("/")
							.append(MySharedPreference.getInt("year")+START_YEAR).append(" ")
							.append(MySharedPreference.getInt("hour")).append(":")
							.append(MySharedPreference.getInt("mins")).append(":")
							.append("00");
						}else if (0 == MySharedPreference.getInt("timetype")){
							sb.append(MySharedPreference.getInt("month")).append("/")
							.append(MySharedPreference.getInt("day")).append("/")
							.append(MySharedPreference.getInt("year")+START_YEAR).append(" ")
							.append(MySharedPreference.getInt("hour")).append(":")
							.append(MySharedPreference.getInt("mins")).append(":")
							.append("00");
						}
						timeText.setText(sb.toString());
					}else {
						switch (MySharedPreference.getInt("timetype")) {
						case 2:
							SimpleDateFormat formatter = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss");
							Date curDate = new Date(System.currentTimeMillis());
							String str = formatter.format(curDate);
							timeText.setText(str);
							break;
						case 1:
							SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
							Date curDate1 = new Date(System.currentTimeMillis());
							String str1 = formatter1.format(curDate1);
							timeText.setText(str1);
							break;
						case 0:
							SimpleDateFormat formatter2 = new SimpleDateFormat ("MM/dd/yyyy HH:mm:ss");
							Date curDate2 = new Date(System.currentTimeMillis());
							String str2 = formatter2.format(curDate2);
							timeText.setText(str2);
							break;
						default:
							break;
						}
					}
				}
				popupWindow.dismiss();
				break;
			case R.id.quit:
				popupWindow.dismiss();
				break;

			case R.id.relativeone:
				popupWindow.relativeOne_img.setVisibility(View.VISIBLE);
				popupWindow.relativeTwo_img.setVisibility(View.GONE);
				popupWindow.relativeThree_img.setVisibility(View.GONE);
				popupWindow.Currenttype.setText("(YYYY-MM-DD)");
				dateText.setText("YYYY-MM-DD");
				MySharedPreference.putInt("timetype", 1);
				break;
			case R.id.relativetwo:
				popupWindow.relativeOne_img.setVisibility(View.GONE);
				popupWindow.relativeTwo_img.setVisibility(View.VISIBLE);
				popupWindow.relativeThree_img.setVisibility(View.GONE);
				popupWindow.Currenttype.setText("(DD/MM/YYYY)");
				dateText.setText("DD/MM/YYYY");
				MySharedPreference.putInt("timetype", 2);
				break;
			case R.id.relativethree:
				popupWindow.relativeOne_img.setVisibility(View.GONE);
				popupWindow.relativeTwo_img.setVisibility(View.GONE);
				popupWindow.relativeThree_img.setVisibility(View.VISIBLE);
				popupWindow.Currenttype.setText("(MM/DD/YYYY)");
				dateText.setText("MM/DD/YYYY");
				MySharedPreference.putInt("timetype", 0);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {
		MySharedPreference.putInt("timetype",-1);
	}

}
