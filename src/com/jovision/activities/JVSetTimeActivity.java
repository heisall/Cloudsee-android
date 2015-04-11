package com.jovision.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
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
		dateType = (RelativeLayout)findViewById(R.id.date_type);
		timeType = (RelativeLayout)findViewById(R.id.time_type);
		dateText = (TextView)findViewById(R.id.date_text);
		timeText = (TextView)findViewById(R.id.time_text);
		linear = (LinearLayout)findViewById(R.id.linear);

		dateType.setOnClickListener(myOnClickListener);
		timeType.setOnClickListener(myOnClickListener);
		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn.setOnClickListener(myOnClickListener);

		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);

		MySharedPreference.putInt("timetype", 1);
		dateText.setText("YYYY-MM-DD");
		timeText.setText(str);
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
				if (1 == MySharedPreference.getInt("timetype")) {
					popupWindow.relativeOne_img.setVisibility(View.VISIBLE);
					popupWindow.relativeTwo_img.setVisibility(View.GONE);
					popupWindow.relativeThree_img.setVisibility(View.GONE);
					popupWindow.Currenttype.setText("(YYYY-MM-DD)");
					dateText.setText("YYYY-MM-DD");
				}else if (0 == MySharedPreference.getInt("timetype")) {
					popupWindow.relativeOne_img.setVisibility(View.GONE);
					popupWindow.relativeTwo_img.setVisibility(View.VISIBLE);
					popupWindow.relativeThree_img.setVisibility(View.GONE);
					popupWindow.Currenttype.setText("(DD/MM/YYYY)");
					dateText.setText("DD/MM/YYYY");
				}else if (2 == MySharedPreference.getInt("timetype")){
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
				MySharedPreference.putInt("timetype", 0);
				break;
			case R.id.relativethree:
				popupWindow.relativeOne_img.setVisibility(View.GONE);
				popupWindow.relativeTwo_img.setVisibility(View.GONE);
				popupWindow.relativeThree_img.setVisibility(View.VISIBLE);
				popupWindow.Currenttype.setText("(MM/DD/YYYY)");
				dateText.setText("MM/DD/YYYY");
				MySharedPreference.putInt("timetype", 2);
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
