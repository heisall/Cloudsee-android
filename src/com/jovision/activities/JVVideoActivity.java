package com.jovision.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.jovetech.CloudSee.temp.R;

public class JVVideoActivity extends BaseActivity implements OnErrorListener,
		OnCompletionListener, OnPreparedListener {
	private VideoView mVideoView;
	private String url;
	private boolean isLocal;

	@Override
	protected void initSettings() {
		Intent intent = getIntent();
		if (null != intent) {
			url = intent.getStringExtra("URL");
			isLocal = intent.getBooleanExtra("IS_LOCAL", true);
		}

		if (null == url) {
			this.finish();
			return;
		}
	}

	@Override
	protected void initUi() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video_layout);
		mVideoView = (VideoView) this.findViewById(R.id.videoView);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(this);
		MediaController mc = new MediaController(JVVideoActivity.this);
		mc.setAnchorView(mVideoView);
		mVideoView.setMediaController(mc);
		if (isLocal) {
			mVideoView.setVideoPath(url);
		} else {
			createDialog(R.string.connecting_buffer2);
			mVideoView.setVideoURI(Uri.parse(url));
		}

		mVideoView.requestFocus();
		mVideoView.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getWindow().getDecorView().setKeepScreenOn(true);
		if (null != mVideoView) {
			mVideoView.resume();
		}
	}

	@Override
	protected void onPause() {

		getWindow().getDecorView().setKeepScreenOn(false);
		if (null != mVideoView) {
			mVideoView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (null != mVideoView) {
			mVideoView.stopPlayback();
			mVideoView = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {

		} else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {

		}
		showTextToast(R.string.str_play_failed);

		if (null != mVideoView) {
			mVideoView.seekTo(0);
			mVideoView.stopPlayback();
		}
		this.finish();
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (null != mVideoView) {
			mVideoView.seekTo(0);
			mVideoView.stopPlayback();
			this.finish();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		dismissDialog();
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

}
