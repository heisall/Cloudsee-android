package barcode.zxing.activity;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import barcode.zxing.camera.CameraManager;
import barcode.zxing.decoding.CaptureActivityHandler;
import barcode.zxing.decoding.InactivityTimer;
import barcode.zxing.view.ViewfinderView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.jovetech.CloudSee.temp.R;
import com.jovision.commons.JVConst;
import com.jovision.utils.RegularUtil;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	// private Button cancelScanButton;
	private Button back;
	private TextView scaningTxt;
	// 扫描结果
	String resultString = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		// 获取手机分辨率
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;

		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		CameraManager.MIN_FRAME_HEIGHT = screenWidth / 3 * 2;
		CameraManager.MIN_FRAME_WIDTH = screenWidth / 3 * 2;

		CameraManager.MAX_FRAME_HEIGHT = screenWidth / 5 * 4;
		CameraManager.MAX_FRAME_WIDTH = screenWidth / 5 * 4;

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		// cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
		back = (Button) this.findViewById(R.id.back);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MipcaActivityCapture.this.finish();

			}
		});
		scaningTxt = (TextView) findViewById(R.id.scaning);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 处理扫描结果
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();

		resultString = result.getText();

		if (!RegularUtil.checkYSTNum(resultString)) {// 验证云视通号不正确
			errorDeviceDialog();
		} else {
			findDeviceDialog();
			scaningTxt.setText(resultString);
		}
	}

	// 发现设备
	private void findDeviceDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MipcaActivityCapture.this);

		builder.setTitle(R.string.tips);
		builder.setMessage(getResources().getString(R.string.str_qr_device)
				+ "[" + resultString + "]");

		builder.setPositiveButton(R.string.login_str_add,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent resultIntent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("result", resultString);
						resultIntent.putExtras(bundle);
						MipcaActivityCapture.this.setResult(
								JVConst.BARCODE_RESULT, resultIntent);
						MipcaActivityCapture.this.finish();
					}
				});
		builder.setNegativeButton(R.string.str_continue_qr_device,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 先销毁
						if (handler != null) {
							handler.quitSynchronously();
							handler = null;
						}
						CameraManager.get().closeDriver();

						inactivityTimer.shutdown();

						// 从新开启
						hasSurface = true;
						inactivityTimer = new InactivityTimer(
								MipcaActivityCapture.this);

						SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
						SurfaceHolder surfaceHolder = surfaceView.getHolder();
						if (hasSurface) {
							initCamera(surfaceHolder);
						} else {
							surfaceHolder
									.addCallback(MipcaActivityCapture.this);
							surfaceHolder
									.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
						}
						decodeFormats = null;
						characterSet = null;

						playBeep = true;
						AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
						if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
							playBeep = false;
						}
						initBeepSound();
						vibrate = true;

						scaningTxt.setText(R.string.str_scanning_device);
						// quit the scan view

					}
				});
		builder.create().show();
	}

	// 错误设备
	private void errorDeviceDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MipcaActivityCapture.this);

		builder.setTitle(R.string.tips);
		builder.setMessage(R.string.str_not_qr_device);

		builder.setPositiveButton(R.string.str_continue_qr_device,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 先销毁
						if (handler != null) {
							handler.quitSynchronously();
							handler = null;
						}
						CameraManager.get().closeDriver();

						inactivityTimer.shutdown();

						// 从新开启
						hasSurface = true;
						inactivityTimer = new InactivityTimer(
								MipcaActivityCapture.this);

						SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
						SurfaceHolder surfaceHolder = surfaceView.getHolder();
						if (hasSurface) {
							initCamera(surfaceHolder);
						} else {
							surfaceHolder
									.addCallback(MipcaActivityCapture.this);
							surfaceHolder
									.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
						}
						decodeFormats = null;
						characterSet = null;

						playBeep = true;
						AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
						if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
							playBeep = false;
						}
						initBeepSound();
						vibrate = true;

						scaningTxt.setText(R.string.str_scanning_device);
					}
				});

		builder.create().show();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}