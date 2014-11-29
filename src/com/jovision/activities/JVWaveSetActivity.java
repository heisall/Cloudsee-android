package com.jovision.activities;

import java.io.IOException;
import java.util.ArrayList;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.bean.WifiAdmin;
import com.jovision.commons.JVConst;
import com.jovision.utils.ConfigUtil;

public class JVWaveSetActivity extends BaseActivity {

	String[] stepSoundCH = { "voi_info.mp3", "voi_next.mp3", "voi_send.mp3" };
	String[] stepSoundEN = { "voi_info_en.mp3", "voi_next_en.mp3",
			"voi_send_en.mp3" };

	protected WifiAdmin wifiAdmin;
	protected String oldWifiSSID = "";

	/** topBar */
	protected LinearLayout topBar;
	protected Button leftBtn;
	protected TextView currentMenu;
	protected Button rightBtn;

	protected RelativeLayout stepLayout1;
	protected RelativeLayout stepLayout2;
	protected RelativeLayout stepLayout3;
	protected RelativeLayout stepLayout4;

	protected ImageView stepImage1;
	protected EditText desWifiName;
	protected EditText desWifiPwd;

	ArrayList<RelativeLayout> layoutList = new ArrayList<RelativeLayout>();

	protected Button nextBtn1;
	protected Button nextBtn2;
	protected Button showDemoBtn;// 观看操作演示
	protected Button nextBtn3;

	protected int currentStep = 0;

	// 播放操作步骤音频
	protected AssetManager assetMgr = null;
	protected MediaPlayer mediaPlayer = new MediaPlayer();

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {
		wifiAdmin = new WifiAdmin(JVWaveSetActivity.this);
		// wifi打开的前提下,获取oldwifiSSID
		if (wifiAdmin.getWifiState()) {
			if (null != wifiAdmin.getSSID()) {
				oldWifiSSID = wifiAdmin.getSSID().replace("\"", "");
			}
		}
		assetMgr = this.getAssets();
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.soundwave_layout);
		topBar = (LinearLayout) findViewById(R.id.top_bar);
		leftBtn = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		rightBtn = (Button) findViewById(R.id.btn_right);
		currentMenu.setText(R.string.prepare_step);
		rightBtn.setVisibility(View.GONE);

		stepLayout1 = (RelativeLayout) findViewById(R.id.step_layout1);
		stepLayout2 = (RelativeLayout) findViewById(R.id.step_layout2);
		stepLayout3 = (RelativeLayout) findViewById(R.id.step_layout3);
		stepLayout4 = (RelativeLayout) findViewById(R.id.step_layout4);
		stepImage1 = (ImageView) findViewById(R.id.step_img1);

		if (JVConst.LANGUAGE_ZH == ConfigUtil.getLanguage()) {
			stepImage1.setImageResource(R.drawable.reset_bg_zh);
		} else {
			stepImage1.setImageResource(R.drawable.reset_bg_en);
		}
		layoutList.add(0, stepLayout1);
		layoutList.add(1, stepLayout2);
		layoutList.add(2, stepLayout3);
		layoutList.add(3, stepLayout4);

		desWifiName = (EditText) findViewById(R.id.deswifiname);
		desWifiPwd = (EditText) findViewById(R.id.deswifipwd);

		desWifiName.setText(oldWifiSSID);

		nextBtn1 = (Button) findViewById(R.id.step_btn1);
		nextBtn2 = (Button) findViewById(R.id.step_btn2);
		showDemoBtn = (Button) findViewById(R.id.showdemo);
		nextBtn3 = (Button) findViewById(R.id.step_btn3);

		rightBtn.setOnClickListener(myOnClickListener);
		leftBtn.setOnClickListener(myOnClickListener);
		nextBtn1.setOnClickListener(myOnClickListener);
		nextBtn2.setOnClickListener(myOnClickListener);
		showDemoBtn.setOnClickListener(myOnClickListener);
		nextBtn3.setOnClickListener(myOnClickListener);

		showLayoutAtIndex(currentStep);

	}

	private void showLayoutAtIndex(int showIndex) {
		if (showIndex < 0) {
			JVWaveSetActivity.this.finish();
		} else {
			int length = layoutList.size();

			for (int i = 0; i < length; i++) {
				RelativeLayout layout = layoutList.get(i);
				if (null != layout) {
					if (i == showIndex) {
						layout.setVisibility(View.VISIBLE);
					} else {
						layout.setVisibility(View.GONE);
					}
				}

			}
			if (showIndex >= 0 && showIndex < stepSoundEN.length) {
				playSoundStep(showIndex);
			}
		}
		int length = layoutList.size();

		for (int i = 0; i < length; i++) {
			RelativeLayout layout = layoutList.get(i);
			if (null != layout) {
				if (i == showIndex) {
					layout.setVisibility(View.VISIBLE);
				} else {
					layout.setVisibility(View.GONE);
				}
			}

		}
		if (showIndex >= 0 && showIndex < stepSoundEN.length) {
			playSoundStep(showIndex);
		}
	}

	private void backMethod() {
		showLayoutAtIndex(--currentStep);
	}

	@Override
	public void onBackPressed() {
		backMethod();
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				backMethod();
				break;
			case R.id.btn_right:
				break;
			case R.id.step_btn1:
				currentStep = 1;
				showLayoutAtIndex(currentStep);
				break;
			case R.id.step_btn2:
				currentStep = 2;
				showLayoutAtIndex(currentStep);
				break;
			case R.id.step_btn3:
				currentStep = 0;
				showLayoutAtIndex(currentStep);
				break;
			case R.id.showdemo:
				currentStep = 3;
				showLayoutAtIndex(currentStep);
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
		mediaPlayer.release();
	}

	private void playSoundStep(int index) {
		try {
			String file = "";
			if (JVConst.LANGUAGE_ZH == ConfigUtil.getLanguage()) {
				file = stepSoundCH[index];
			} else {
				file = stepSoundEN[index];
			}

			AssetFileDescriptor afd = assetMgr.openFd(file);
			mediaPlayer.reset();

			// 使用MediaPlayer加载指定的声音文件。
			mediaPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			// 准备声音
			mediaPlayer.prepare();
			// 播放
			mediaPlayer.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
