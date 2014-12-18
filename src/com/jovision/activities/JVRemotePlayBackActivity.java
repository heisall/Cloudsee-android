package com.jovision.activities;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.PlayUtil;

public class JVRemotePlayBackActivity extends PlayActivity {

	private int indexOfChannel;
	private String acBuffStr;
	private int currentProgress;// 当前进度
	private int seekProgress;// 手动进度
	private int totalProgress;// 总进度

	private Timer playTimer;
	private PlayTimerTask playTask;
	private int seconds;
	private int audioByte;// 音频监听比特率
	private boolean is05 = true;
	private boolean isRemotePause = false;
	public boolean bFromAlarm = false;// 是否报警视频
	private boolean isAudio = false;// 是否正在监听

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {

		case Consts.CALL_CONNECT_CHANGE: {
			MyLog.i(TAG, "CALL_CONNECT_CHANGE:what=" + what + ",arg1=" + arg1
					+ ",arg2=" + arg2 + ",obj=" + obj);
			switch (arg2) {
			// 1 -- 连接成功
			case JVNetConst.CONNECT_OK: {
				break;
			}
			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK: {
				showTextToast(R.string.closed);
				this.finish();
				break;
			}
			// 4 -- 连接失败
			case JVNetConst.CONNECT_FAILED: {
				showTextToast(R.string.closed);
				this.finish();
				break;
			}
			// 6 -- 连接异常断开
			case JVNetConst.ABNORMAL_DISCONNECT: {
				showTextToast(R.string.closed);
				this.finish();
				break;
			}
			// 7 -- 服务停止连接，连接断开
			case JVNetConst.SERVICE_STOP: {
				showTextToast(R.string.closed);
				this.finish();
				break;
			}
			case Consts.BAD_NOT_CONNECT: {
				showTextToast(R.string.closed);
				this.finish();
				break;
			}
			// 3 -- 不必要重复连接
			case JVNetConst.NO_RECONNECT: {
				break;
			}
			// 5 -- 没有连接
			case JVNetConst.NO_CONNECT: {
				break;
			}
			// 8 -- 断开连接失败
			case JVNetConst.DISCONNECT_FAILED: {
				break;
			}

			// 9 -- 其他错误
			case JVNetConst.OHTER_ERROR: {
				break;
			}
			}
		}

		case Consts.CALL_STAT_REPORT: {// 每秒状态回调
			int left = 0;
			try {
				if (null != obj) {
					JSONArray array = new JSONArray(obj.toString());
					JSONObject object = null;

					int size = array.length();
					if (size > 0) {
						object = array.getJSONObject(0);
						left = object.getInt("left");
						MyLog.v(TAG,
								"stat-" + ": fps: "
										+ object.getDouble("decoder_fps") + "+"
										+ object.getDouble("jump_fps") + "/"
										+ object.getDouble("network_fps")
										+ ", left = " + left);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// progressBar.setProgress(currentProgress - left);

			break;
		}

		case Consts.CALL_GOT_SCREENSHOT: {// 抓拍回调
			MyLog.i(TAG, "capture--what=" + what + ";arg1=" + arg1 + ";arg2="
					+ arg2);
			switch (arg2) {
			case Consts.BAD_SCREENSHOT_NOOP:
				PlayUtil.prepareAndPlay(CAPTURING);
				if (CAPTURING) {
					CAPTURING = false;
					showTextToast(Consts.CAPTURE_PATH);
				}
				break;
			case Consts.BAD_SCREENSHOT_INIT:
				if (CAPTURING) {
					showTextToast(R.string.str_capture_error);
				}
				break;
			case Consts.BAD_SCREENSHOT_CONV:
				if (CAPTURING) {
					showTextToast(R.string.str_capture_error);
				}
				break;
			case Consts.BAD_SCREENSHOT_OPEN:
				if (CAPTURING) {
					showTextToast(R.string.str_capture_error);
				}
				break;
			default:
				break;
			}
			break;
		}

		case Consts.CALL_PLAY_AUDIO: {// 音频数据回调
			if (null != obj && null != playAudio) {
				byte[] data = (byte[]) obj;
				// audioQueue.offer(data);
				playAudio.put(data);
			}
			break;
		}
		case Consts.CALL_NEW_PICTURE: {// I帧回调
			linkState.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);// 加载进度
			if (null == playTimer) {
				startTimer();
			}
			break;
		}
		case Consts.CALL_PLAY_DATA: {// 远程回放数据
			switch (arg2) {
			case JVNetConst.JVN_DATA_O: {
				linkState.setVisibility(View.VISIBLE);
				linkState.setText(R.string.connecting_buffer2);
				loading.setVisibility(View.VISIBLE);// 加载进度

				if (0 == totalProgress) {
					try {
						JSONObject jobj;
						jobj = new JSONObject(obj.toString());
						MyLog.i(TAG, "OFrame = " + obj.toString());
						if (null != jobj) {
							totalProgress = jobj.optInt("total");
							progressBar.setMax(totalProgress);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			case JVNetConst.JVN_DATA_I:
			case JVNetConst.JVN_DATA_B:
			case JVNetConst.JVN_DATA_P: {
				currentProgress++;
				progressBar.setProgress(currentProgress);
				break;
			}
			case Consts.ARG2_REMOTE_PLAY_ERROR: {
				showTextToast(R.string.recoding);
				break;
			}
			case Consts.ARG2_REMOTE_PLAY_OVER: {
				break;
			}
			case Consts.ARG2_REMOTE_PLAY_TIMEOUT: {
				showTextToast(R.string.connect_failed);
				this.finish();
				break;
			}
			}

			break;
		}
		case Consts.CALL_PLAY_DOOMED: {// 远程回放结束
			if (Consts.PLAYBACK_DONE == arg2) {
				backMethod();
			}
		}
		case Consts.WHAT_REMOTE_PLAY_DISMISS_PROGRESS: {// 5秒时间到关闭进度条
			// stopTimer();
			seconds = 0;
			playBackBar.setVisibility(View.GONE);
			break;
		}
		// [Neo] surface.step 3
		case Consts.WHAT_DUMMY: {
			playSurface.getHolder().setFixedSize(surfaceWidth, surfaceHeight);
			break;
		}
		default:
			break;
		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		TAG = "RemotePlayA";
		currentProgress = 0;
	}

	@Override
	protected void initUi() {
		super.initUi();

		selectScreenNum.setVisibility(View.GONE);
		rightFuncButton.setVisibility(View.GONE);

		/** 中 */
		viewPager.setVisibility(View.GONE);
		playSurface.setVisibility(View.VISIBLE);
		playSurface.setOnClickListener(myOnClickListener);
		SurfaceHolder holder = playSurface.getHolder();
		progressBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		progressBar.setProgress(0);
		decodeBtn.setVisibility(View.GONE);
		videTurnBtn.setVisibility(View.GONE);

		linkState.setText(R.string.connecting);// 连接文字
		linkState.setVisibility(View.GONE);
		linkState.setTextColor(Color.GREEN);
		linkState.setTextSize(18);
		linkState.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
		loading.setVisibility(View.VISIBLE);// 加载进度

		back.setOnClickListener(myOnClickListener);
		playBackPause.setOnClickListener(myOnClickListener);
		voiceListener.setVisibility(View.VISIBLE);
		voiceListener.setOnClickListener(myOnClickListener);
		playFunctionList.setOnItemClickListener(onItemClickListener);
		/** 下 */
		capture.setOnClickListener(myOnClickListener);
		voiceCall.setOnClickListener(myOnClickListener);
		videoTape.setOnClickListener(myOnClickListener);
		moreFeature.setOnClickListener(myOnClickListener);

		verPlayBarLayout.setVisibility(View.GONE);
		horPlayBarLayout.setVisibility(View.GONE);

		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Jni.pause(indexOfChannel);// boolean result =
				// MyLog.i(Consts.TAG_PLAY, "playback-" + currentIndex
				// + " destroy: " + result);
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Jni.resume(indexOfChannel, holder.getSurface());// boolean
				// result
				// =
				// MyLog.v(Consts.TAG_PLAY, "playback-" + currentIndex
				// + " created: " + result);
				boolean enable = Jni.enablePlayback(indexOfChannel, true);
				if (enable) {
					linkState.setVisibility(View.VISIBLE);
					linkState.setText(R.string.connecting);
					Jni.sendBytes(indexOfChannel,
							(byte) JVNetConst.JVN_REQ_PLAY,
							(byte[]) acBuffStr.getBytes(),
							acBuffStr.getBytes().length);
				}

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// MyLog.i(Consts.TAG_PLAY, "playback-" + currentIndex
				// + " changed: " + width + "x" + height + ", " + format
				// + ", " + holder.hashCode());
			}
		});

		Intent intent = getIntent();
		if (null != intent) {
			indexOfChannel = intent.getIntExtra("IndexOfChannel", 0);
			acBuffStr = intent.getStringExtra("acBuffStr");
			audioByte = intent.getIntExtra("AudioBit", 16);
			MyLog.v(TAG, "audioBit=" + audioByte);
			is05 = intent.getBooleanExtra("is05", true);
			bFromAlarm = intent.getBooleanExtra("bFromAlarm", false);
		}
		functionListAdapter.setFromAlerm(bFromAlarm);
		if (bFromAlarm) {
			currentMenu.setText(R.string.str_remote_alarm);
			// capture.setCompoundDrawablesWithIntrinsicBounds(null,
			// getResources().getDrawable(R.drawable.capture_alerm
			// ) , null, null);
			voiceCall.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.voice_call_alerm),
					null, null);
			// videoTape.setCompoundDrawablesWithIntrinsicBounds(null,
			// getResources().getDrawable(R.drawable.video_record_alerm
			// ) , null, null);
			moreFeature.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.more_feature_alerm),
					null, null);
			// capture.setTextColor(getResources().getColor(R.color.more_fragment_color7));
			// videoTape.setTextColor(getResources().getColor(R.color.more_fragment_color7));
			voiceCall.setTextColor(getResources().getColor(
					R.color.more_fragment_color7));
			moreFeature.setTextColor(getResources().getColor(
					R.color.more_fragment_color7));
		} else {
			currentMenu.setText(R.string.str_remote_playback);
		}
		if (!is05) {
			progressBar.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 开始计时
	 */
	private void startTimer() {
		seconds = 0;
		playTimer = new Timer();
		playTask = new PlayTimerTask();
		playTimer.schedule(playTask, 0, 1000);
		playBackBar.setVisibility(View.VISIBLE);
	}

	/**
	 * 停止计时
	 */
	private void stopTimer() {
		if (null != playTimer) {
			playTimer.cancel();
			playTimer = null;
		}
		if (null != playTask) {
			playTask.cancel();
			playTask = null;
		}

		playBackBar.setVisibility(View.GONE);
	}

	private class PlayTimerTask extends TimerTask {

		@Override
		public void run() {
			seconds++;
			if (6 == seconds) {
				handler.sendMessage(handler
						.obtainMessage(Consts.WHAT_REMOTE_PLAY_DISMISS_PROGRESS));
				return;
			}
		}

	}

	/**
	 * 远程回放进度条拖动事件
	 */
	OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			seekProgress = arg1;
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			currentProgress = seekProgress;
			Jni.sendInteger(indexOfChannel, JVNetConst.JVN_CMD_PLAYSEEK,
					seekProgress);
		}
	};
	/**
	 * 大分辨率功能列表点击事件
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (!bFromAlarm) {
				if (2 == arg2) {// 音频监听
					showTextToast(R.string.str_forbidden_operation);
				} else if (0 == arg2) {// 云台
					showTextToast(R.string.str_forbidden_operation);
				} else if (1 == arg2) {// 远程回放
					backMethod();
				}
			} else {
				showTextToast(R.string.str_forbidden_alarm);
			}
			functionListAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * 所有按钮事件
	 */
	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_left:// 返回
				backMethod();
				break;
			case R.id.playbackpause: {
				if (isRemotePause) {
					playBackPause
							.setBackgroundResource(R.drawable.video_stop_icon);
					// 继续播放视频
					Jni.sendBytes(indexOfChannel, JVNetConst.JVN_CMD_PLAYGOON,
							new byte[0], 0);
					Jni.enablePlayAudio(indexOfChannel, isAudio);
					isRemotePause = false;
				} else {
					// 暂停视频
					Jni.sendBytes(indexOfChannel, JVNetConst.JVN_CMD_PLAYPAUSE,
							new byte[0], 0);
					Jni.enablePlayAudio(indexOfChannel, false);
					playBackPause
							.setBackgroundResource(R.drawable.video_play_icon);
					isRemotePause = true;
				}
				break;
			}
			case R.id.voice: {// 音频监听
				// 停止音频监听
				if (PlayUtil.isPlayAudio(indexOfChannel)) {
					isAudio = false;
					stopAudio(indexOfChannel);
					voiceListener.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.video_monitor_ico));
				} else {
					isAudio = true;
					startAudio(indexOfChannel, audioByte);
					voiceListener.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.video_monitorselect_icon));
				}
				break;
			}
			case R.id.remotesurfaceview:// 单击视频打开隐藏进度条
				seconds = 0;
				if (playBackBar.getVisibility() == View.GONE) {
					playBackBar.setVisibility(View.VISIBLE);
				} else {
					playBackBar.setVisibility(View.GONE);
				}
				break;
			case R.id.audio_monitor:// 音频监听
				// // 停止音频监听
				// if (PlayUtil.isPlayAudio(indexOfChannel)) {
				// stopAudio(indexOfChannel);
				// functionListAdapter.selectIndex = -1;
				// }else{
				// startAudio(indexOfChannel,audioByte);
				// functionListAdapter.selectIndex = arg2;
				// }
				break;
			case R.id.yt_operate:// 云台
				showTextToast(R.string.str_forbidden_operation);
				break;
			case R.id.remote_playback:// 远程回放
				backMethod();
				break;
			case R.id.capture:// 抓拍
				if (isRemotePause) {
					showTextToast(R.string.forbidden_operation_when_paused);
				} else {
					if (hasSDCard()) {
						CAPTURING = PlayUtil.capture(indexOfChannel);
						MyLog.v(TAG, "CAPTURING=" + CAPTURING);
					}
				}
				break;
			case R.id.voicecall:// 语音对讲
				if (bFromAlarm) {
					showTextToast(R.string.str_forbidden_alarm);
				} else {
					showTextToast(R.string.str_forbidden_operation);
				}
				break;
			case R.id.videotape:// 录像
				if (isRemotePause) {
					showTextToast(R.string.forbidden_operation_when_paused);
				} else {
					if (hasSDCard()) {
						String path = PlayUtil.createRecordFile();
						if (PlayUtil.videoRecord(indexOfChannel, path)) {// 打开
							recordingPath = path;
							startRecordTime = System.currentTimeMillis();
							tapeSelected(true);
							showTextToast(R.string.str_start_record);
						} else {// 关闭
							long recordTime = System.currentTimeMillis()
									- startRecordTime;
							MyLog.e(TAG, "recordTime=" + recordTime);

							if (recordTime <= 2000) {
								File recordFile = new File(recordingPath);
								recordFile.delete();
								showTextToast(R.string.record_failed);
							} else {
								showTextToast(Consts.VIDEO_PATH);
							}
							tapeSelected(false);
						}
					}
				}
				break;
			case R.id.more_features:// 更多
				if (bFromAlarm) {
					showTextToast(R.string.str_forbidden_alarm);
				} else {
					showTextToast(R.string.str_forbidden_operation);
				}
				// Intent moreIntent = new Intent();
				// moreIntent.setClass(JVRemotePlayBackActivity.this,
				// JVMoreFeatureActivity.class);
				// JVRemotePlayBackActivity.this.startActivity(moreIntent);
				break;
			}

		}

	};

	/**
	 * 返回事件
	 */
	private void backMethod() {
		stopAllFunc();
		Jni.sendBytes(indexOfChannel, JVNetConst.JVN_CMD_PLAYSTOP, new byte[0],
				0);
		JVRemotePlayBackActivity.this.finish();
	}

	@Override
	public void onBackPressed() {
		backMethod();
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 继续播放视频
		Jni.sendBytes(indexOfChannel, JVNetConst.JVN_CMD_PLAYGOON, new byte[0],
				0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopAll(indexOfChannel, null);
		// 暂停视频
		Jni.sendBytes(indexOfChannel, JVNetConst.JVN_CMD_PLAYPAUSE,
				new byte[0], 0);
	}

	@Override
	protected void saveSettings() {

	}

	private void stopAllFunc() {
		// 停止音频监听
		if (PlayUtil.isPlayAudio(indexOfChannel)) {
			stopAudio(indexOfChannel);
		}

		// 正在录像停止录像
		if (PlayUtil.checkRecord(indexOfChannel)) {
			if (PlayUtil.videoRecord(indexOfChannel, "")) {// 打开
				showTextToast(Consts.VIDEO_PATH);
				tapeSelected(false);
			}
		}
	}

	@Override
	protected void freeMe() {
		stopAllFunc();
		Jni.enablePlayback(indexOfChannel, false);
		super.freeMe();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		verPlayBarLayout.setVisibility(View.GONE);
		horPlayBarLayout.setVisibility(View.GONE);

		// [Neo] surface.step 1
		playSurface.getHolder().setFixedSize(1, 1);

		// [Neo] surface.step 2
		handler.sendEmptyMessageDelayed(Consts.WHAT_DUMMY, 100);
		horPlayBarLayout.setVisibility(View.GONE);
	}
}
