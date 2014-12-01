package com.jovision.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.WaveDevlListAdapter;
import com.jovision.bean.Device;
import com.jovision.bean.WifiAdmin;
import com.jovision.commons.JVConst;
import com.jovision.commons.MyAudio;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.PlayUtil;

public class JVWaveSetActivity extends BaseActivity {

	private static final String TAG = "JVWaveSetActivity";

	protected static final int PLAY_AUDIO_WHAT = 0xA1;
	protected static final int SEND_WAVE_FINISHED = 0xA2;// 声波发送完毕
	protected static final int BROAD_DEVICE = 0xA3;// 广播到一个设备
	protected static final int BROAD_FINISHED = 0xA4;// 广播回调完毕

	String[] stepSoundCH = { "voi_info.mp3", "voi_next.mp3", "voi_send.mp3" };
	String[] stepSoundEN = { "voi_info_en.mp3", "voi_next_en.mp3",
			"voi_send_en.mp3" };

	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private ArrayList<Device> broadList = new ArrayList<Device>();

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
	protected RelativeLayout stepLayout5;

	protected ImageView stepImage1;
	protected ImageView waveImage;// 声波动画按钮
	protected ImageView pressToSendWave;// 点击发送声波按钮
	protected EditText desWifiName;
	protected EditText desWifiPwd;
	protected ToggleButton desPwdEye;
	protected ListView devListView;// 广播到的设备列表
	protected WaveDevlListAdapter wdListAdapter;// 设备列表adaper

	ArrayList<RelativeLayout> layoutList = new ArrayList<RelativeLayout>();

	protected Button nextBtn1;
	protected Button nextBtn2;
	protected Button showDemoBtn;// 观看操作演示
	protected Button nextBtn3;// 只有发送完声波此按钮才管用

	protected int currentStep = 0;

	// 播放操作步骤音频
	protected AssetManager assetMgr = null;
	protected MediaPlayer mediaPlayer = new MediaPlayer();

	// 声波
	protected MyAudio playAudio;
	protected static int audioSampleRate = 48000;
	protected static int playBytes = 16;

	ScaleAnimation waveScaleAnim = null;// 发送声波动画
	AlphaAnimation waveAlphaAnim = null;// 发送声波动画

	@SuppressWarnings("deprecation")
	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case SEND_WAVE_FINISHED: {// 声波发送完毕
			waveScaleAnim.cancel();
			nextBtn3.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.blue_bg));
			nextBtn3.setClickable(true);
			break;
		}
		case BROAD_FINISHED: {// 广播超时
			dismissDialog();
			if (null == broadList || 0 == broadList.size()) {

			} else {
				wdListAdapter = new WaveDevlListAdapter(JVWaveSetActivity.this);
				wdListAdapter.setData(broadList);
				devListView.setAdapter(wdListAdapter);
			}

			break;
		}
		case BROAD_DEVICE: {
			if (null == broadList || 0 == broadList.size()) {

			} else {
				wdListAdapter = new WaveDevlListAdapter(JVWaveSetActivity.this);
				wdListAdapter.setData(broadList);
				devListView.setAdapter(wdListAdapter);
			}
		}

		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case PLAY_AUDIO_WHAT: {
			MyLog.v(TAG, "PLAY_AUDIO_WHAT:what=" + what + ",arg1=" + arg1
					+ ",arg2=" + arg2 + ",obj=" + obj);
			break;
		}
		// 获取到声波音频
		case Consts.CALL_GEN_VOICE: {
			MyLog.i(TAG, "CALL_GEN_VOICE:what=" + what + ";arg1=" + arg1
					+ ";arg2=" + arg2 + ";obj=" + obj);
			if (null != obj && null != playAudio) {
				byte[] data = (byte[]) obj;
				playAudio.put(data);
			}

			break;
		}
		// 广播回调
		case Consts.CALL_LAN_SEARCH: {// nNetMod 设备是否带wifi nCurMod 设备是否正在使用wifi
			MyLog.v(TAG, "CALL_LAN_SEARCH = what=" + what + ";arg1=" + arg1
					+ ";arg2=" + arg1 + ";obj=" + obj.toString());

			// * @param nNetMod
			// * 设备是否带wifi 有无wifi
			// * @param nCurMod
			// * 设备是否正在使用wifi
			// CALL_LAN_SEARCH =
			// what=168;arg1=0;arg2=0;obj={"count":1,"curmod":0,"gid":"S","ip":"192.168.7.139","netmod":1,"no":224356522,"port":9101,"timeout":0,"type":59162,"variety":3}

			JSONObject broadObj;
			try {
				broadObj = new JSONObject(obj.toString());
				if (0 == broadObj.optInt("timeout")) {
					String gid = broadObj.optString("gid");
					int no = broadObj.optInt("no");
					String ip = broadObj.optString("ip");
					int port = broadObj.optInt("port");
					int channelCount = broadObj.optInt("port");
					int count = channelCount > 0 ? channelCount : 1;
					String broadDevNum = gid + no;
					if (1 == broadObj.optInt("netmod")) {// 带wifi设备
						Device addDev = new Device(ip, port, gid, no,
								getResources().getString(
										R.string.str_default_user),
								getResources().getString(
										R.string.str_default_pass), false,
								count, 0);
						if (!PlayUtil.addDev(broadList, addDev)) {
							broadList.add(addDev);
						}
					}
					PlayUtil.hasDev(deviceList, broadDevNum, ip, port);
					handler.sendMessage(handler.obtainMessage(BROAD_DEVICE));
				} else if (1 == broadObj.optInt("timeout")) {
					CacheUtil.saveDevList(deviceList);
					handler.sendMessage(handler.obtainMessage(BROAD_FINISHED));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		default: {
			handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
			break;
		}
		}

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
		deviceList = CacheUtil.getDevList();
		assetMgr = this.getAssets();
		playAudio = MyAudio.getIntance(PLAY_AUDIO_WHAT, JVWaveSetActivity.this,
				audioSampleRate);
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
		stepLayout5 = (RelativeLayout) findViewById(R.id.step_layout5);

		stepImage1 = (ImageView) findViewById(R.id.step_img1);
		waveImage = (ImageView) findViewById(R.id.wavebg);
		pressToSendWave = (ImageView) findViewById(R.id.press_sendwave);
		if (JVConst.LANGUAGE_ZH == ConfigUtil.getLanguage()) {
			stepImage1.setImageResource(R.drawable.reset_bg_zh);
		} else {
			stepImage1.setImageResource(R.drawable.reset_bg_en);
		}
		layoutList.add(0, stepLayout1);
		layoutList.add(1, stepLayout2);
		layoutList.add(2, stepLayout3);
		layoutList.add(3, stepLayout4);
		layoutList.add(4, stepLayout5);

		desWifiName = (EditText) findViewById(R.id.deswifiname);
		desWifiPwd = (EditText) findViewById(R.id.deswifipwd);
		desWifiName.setText(oldWifiSSID);
		desPwdEye = (ToggleButton) findViewById(R.id.despwdeye);
		devListView = (ListView) findViewById(R.id.devlistview);

		desPwdEye.setChecked(true);
		desPwdEye.setOnCheckedChangeListener(myOnCheckedChangeListener);

		nextBtn1 = (Button) findViewById(R.id.step_btn1);
		nextBtn2 = (Button) findViewById(R.id.step_btn2);
		nextBtn3 = (Button) findViewById(R.id.step_btn3);
		showDemoBtn = (Button) findViewById(R.id.showdemo);

		rightBtn.setOnClickListener(myOnClickListener);
		leftBtn.setOnClickListener(myOnClickListener);
		nextBtn1.setOnClickListener(myOnClickListener);
		nextBtn2.setOnClickListener(myOnClickListener);
		showDemoBtn.setOnClickListener(myOnClickListener);
		nextBtn3.setOnClickListener(myOnClickListener);
		pressToSendWave.setOnClickListener(myOnClickListener);
		waveImage.setOnClickListener(myOnClickListener);

		/** 设置缩放动画 */
		waveScaleAnim = new ScaleAnimation(0.0f, 5.0f, 0.0f, 5.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		waveScaleAnim.setDuration(800);// 设置动画持续时间
		waveScaleAnim.setRepeatCount(99999);// 设置重复次数
		// animation.setFillAfter(boolean);//动画执行完后是否停留在执行完的状态
		// animation.setStartOffset(long startOffset);//执行前的等待时间
		waveAlphaAnim = new AlphaAnimation(0.1f, 1.0f);
		waveAlphaAnim.setDuration(1000);// 设置动画持续时间
		waveAlphaAnim.setRepeatCount(99999);// 设置重复次数
		waveAlphaAnim.setStartOffset(200);// 执行前的等待时间
		waveImage.setAnimation(waveScaleAnim);
		showLayoutAtIndex(currentStep);

	}

	/**
	 * 密码显示隐藏
	 */
	OnCheckedChangeListener myOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if (arg1) {
				desWifiPwd
						.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);// 显示密码
			} else {
				desWifiPwd.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);// 隐藏密码
			}
			desWifiPwd.setSelection(desWifiPwd.getText().toString().length());
			desPwdEye.setChecked(arg1);
		}

	};

	/**
	 * 显示哪个布局
	 * 
	 * @param showIndex
	 */
	@SuppressWarnings("deprecation")
	private void showLayoutAtIndex(int showIndex) {
		nextBtn3.setClickable(false);
		nextBtn3.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.login_blue_bg));
		waveScaleAnim.cancel();
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
		if (currentStep == (layoutList.size() - 1)) {
			JVWaveSetActivity.this.finish();
		} else {
			showLayoutAtIndex(--currentStep);
		}

	}

	@Override
	public void onBackPressed() {
		backMethod();
	}

	/**
	 * 点击事件
	 */
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
			case R.id.step_btn3:// 发局域网广播搜索局域网设备
				createDialog("");
				broadList.clear();
				Jni.queryDevice("A", 361, 20 * 1000);
				currentStep = 4;
				showLayoutAtIndex(currentStep);

				break;
			case R.id.press_sendwave:
				playAudio.startPlay(playBytes, true);
				if (null != mediaPlayer) {
					mediaPlayer.stop();
				}
				/** 开始动画 */
				waveScaleAnim.startNow();
				final String params = desWifiName.getText() + ";"
						+ desWifiPwd.getText();
				MyLog.v(TAG, "params:" + params);

				new SendWaveThread(params).start();
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
	protected void onPause() {
		super.onPause();
		if (null != mediaPlayer) {
			mediaPlayer.stop();
		}
	}

	// 发送声波线程
	private class SendWaveThread extends Thread {
		String params = "";

		SendWaveThread(String param) {
			params = param;
		}

		@Override
		public void run() {
			super.run();
			try {
				Jni.genVoice(params);
				Thread.sleep(1000);
				Jni.genVoice(params);
				Thread.sleep(1000);
				Jni.genVoice(params);
				handler.sendMessage(handler.obtainMessage(SEND_WAVE_FINISHED));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	};

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
