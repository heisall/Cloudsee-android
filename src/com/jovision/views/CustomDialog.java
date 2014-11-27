package com.jovision.views;

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.commons.CommonInterface;
import com.jovision.commons.JVConst;
import com.jovision.utils.ConfigUtil;

public class CustomDialog extends Dialog implements CommonInterface {
	private ImageView img_guide;
	private TextView loadingTv;
	private TextView tipsTv;
	private int img_res_id;
	private int dev_mark_id;
	private String[] third_guide_desc = null;
	private MediaPlayer myPlayer = null;
	private Context context;

	public CustomDialog(Context context) {
		super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
		this.context = context;
		third_guide_desc = context.getResources().getStringArray(
				R.array.array_third_guide);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.guide_custom_dialog_layout);
		setCanceledOnTouchOutside(false);
	}

	@Override
	public void onStart() {
		img_guide = (ImageView) findViewById(R.id.guide_img);
		loadingTv = (TextView) findViewById(R.id.loadingText);
		tipsTv = (TextView)findViewById(R.id.guide_text_tips);
		img_guide.setImageResource(img_res_id);
		loadingTv.setText(third_guide_desc[dev_mark_id]);
		tipsTv.setText(third_guide_desc[dev_mark_id]);
		myPlayer = new MediaPlayer();
	}

	@Override
	public void onStop() {
		if (null != myPlayer) {
			myPlayer.stop();
			myPlayer.release();
		}
	}

	public void Show(int img_id, int dev_mark) {
		img_res_id = img_id;
		dev_mark_id = dev_mark;
		this.show();
		playSound(dev_mark_id);
	}

	@Override
	public void playSound(int soundType) {
		// TODO Auto-generated method stub
		try {
			// 打开指定音乐文件
			String file = "";
			if (ConfigUtil.getLanguage() == JVConst.LANGUAGE_ZH) {
				switch (dev_mark_id) {
				case 1:// 门磁
					file = "menci.mp3";
					break;
				case 2:// 手环
					file = "shouhuan.mp3";
					break;
				case 3:// 遥控器
					file = "telecontrol.mp3";
					break;
				default:
					break;
				}
			} else if (ConfigUtil.getLanguage() == JVConst.LANGUAGE_EN) {
				switch (dev_mark_id) {
				case 1:// 门磁
					file = "menci_en.mp3";
					break;
				case 2:// 手环
					file = "shouhuan_en.mp3";
					break;
				case 3:// 遥控器
					file = "telecontrol_en.mp3";
					break;
				default:
					break;
				}
			}
			int maxVolume = 100; // 最大音量值
			int curVolume = 20; // 当前音量值

			AudioManager audioMgr = null; // Audio管理器，用了控制音量
			audioMgr = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			// 获取最大音乐音量
			maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			// 初始化音量大概为最大音量的1/2
			// curVolume = maxVolume / 2;
			// 每次调整的音量大概为最大音量的1/6
			audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume,
					AudioManager.FLAG_PLAY_SOUND);
			AssetManager assetMgr = context.getAssets();
			// 资源管理器
			AssetFileDescriptor afd = assetMgr.openFd(file);

			myPlayer.reset();

			// 使用searchView.myPlayer加载指定的声音文件。
			myPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			// 准备声音
			myPlayer.prepare();
			// 播放
			myPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
