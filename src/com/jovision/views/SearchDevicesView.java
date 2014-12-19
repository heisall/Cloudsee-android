package com.jovision.views;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;

import com.jovetech.CloudSee.temp.BuildConfig;
import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.CommonInterface;

public class SearchDevicesView extends BaseView implements CommonInterface {

	public static final String TAG = "SearchDevicesView";
	public static final boolean D = BuildConfig.DEBUG;

	@SuppressWarnings("unused")
	// private long TIME_DIFF = 6000;
	int[] lineColor = new int[] { 0x7B, 0x7B, 0x7B };
	int[] innerCircle0 = new int[] { 0xb9, 0xff, 0xFF };
	int[] innerCircle1 = new int[] { 0xdf, 0xff, 0xFF };
	int[] innerCircle2 = new int[] { 0xec, 0xff, 0xFF };

	int[] argColor = new int[] { 0xF3, 0xf3, 0xfa };

	private float offsetArgs = 0;
	public boolean isSearching = true;
	// private Bitmap bitmap;
	// private Bitmap bitmap1;
	private Bitmap bitmap2;
	private Context context;
	public MediaPlayer myPlayer = new MediaPlayer();

	public boolean isSearching() {
		return isSearching;
	}

	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
		offsetArgs = 0;
		if (isSearching == true) {
			playSound(Consts.TAG_SOUNDEIGHT);
		}
		invalidate();
	}

	public SearchDevicesView(Context context) {
		super(context);
		this.context = context;
		initBitmap();
	}

	public SearchDevicesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initBitmap();
	}

	public SearchDevicesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBitmap();
	}

	private void initBitmap() {
		// if(bitmap == null){
		// bitmap =
		// Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.quick_set_searchbg));
		// }
		// if(bitmap1 == null){
		// bitmap1 =
		// Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.quick_set_device));
		// }
		if (bitmap2 == null) {
			bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.gplus_search_args));
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
		// getHeight() / 2 - bitmap.getHeight() / 2, null);
		Rect rMoon = new Rect(-(getHeight() - getWidth()) / 2 - getWidth() / 2,
				getHeight() / 2, getWidth() / 2, getHeight());
		if (isSearching) {
			canvas.rotate(offsetArgs, getWidth() / 2, getHeight() / 2);
			canvas.drawBitmap(bitmap2, null, rMoon, null);
			offsetArgs = offsetArgs + 5;
		} else {

			canvas.drawBitmap(bitmap2, null, rMoon, null);
			if (null != myPlayer) {
				myPlayer.stop();
			}
		}

		// canvas.drawBitmap(bitmap1, getWidth() / 2 - bitmap1.getWidth() / 2,
		// getHeight() / 2 - bitmap1.getHeight() / 2, null);

		if (isSearching)
			invalidate();
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// handleActionDownEvenet(event);
	// return true;
	// case MotionEvent.ACTION_MOVE:
	// return true;
	// case MotionEvent.ACTION_UP:
	// return true;
	// }
	// return super.onTouchEvent(event);
	// }

	// private void handleActionDownEvenet(MotionEvent event) {
	// RectF rectF = new RectF(getWidth() / 2, getHeight() / 2,
	// getWidth() / 2, getHeight() / 2);
	//
	// if (rectF.contains(event.getX(), event.getY())) {
	// if (D)
	// Log.d(TAG, "click search device button");
	// if (!isSearching()) {
	// setSearching(true);
	// } else {
	// setSearching(false);
	// }
	// }
	// }

	/**
	 * 播放声音
	 */
	@Override
	public void playSound(int soundType) {
		// TODO Auto-generated method stub
		try {
			// 打开指定音乐文件
			String file = "";
			switch (soundType) {
			case Consts.TAG_SOUNDEIGHT:// 搜索声音
				file = "quicksetsound.mp3";
				break;
			default:
				break;
			}

			// int maxVolume = 100; // 最大音量值
			// int curVolume = 20; // 当前音量值

			AudioManager audioMgr = null; // Audio管理器，用了控制音量
			audioMgr = (AudioManager) this.context
					.getSystemService(Context.AUDIO_SERVICE);
			// 获取最大音乐音量
			// maxVolume =
			// audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			// 初始化音量大概为最大音量的1/2
			// curVolume = maxVolume / 2;
			// 每次调整的音量大概为最大音量的1/6
			// audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume,
			// AudioManager.FLAG_PLAY_SOUND);
			AssetManager assetMgr = this.context.getAssets();
			// 资源管理器
			AssetFileDescriptor afd = assetMgr.openFd(file);

			myPlayer.reset();

			// 使用MediaPlayer加载指定的声音文件。
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

	public void stopPlayer() {
		if (null != myPlayer) {
			myPlayer.stop();
		}

	}
}