package barcode.zxing.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import barcode.zxing.camera.CameraManager;
import barcode.zxing.decoding.CaptureActivityHandler;
import barcode.zxing.decoding.InactivityTimer;
import barcode.zxing.decoding.RGBLuminanceSource;
import barcode.zxing.view.ViewfinderView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.utils.RegularUtil;

public class MipcaActivityCapture extends Activity implements Callback,
		View.OnClickListener {

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

	private TextView scaningTxt;
	// 扫描结果
	String resultString = "";

	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private static final int REQUEST_CODE = 100;
	private static final int PARSE_BARCODE_SUC = 300;
	private static final int PARSE_BARCODE_FAIL = 303;
	private ProgressDialog mProgress;
	private String photo_path;
	private Bitmap scanBitmap;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		Button mButtonBack = (Button) findViewById(R.id.back);
		mButtonBack.setOnClickListener(this);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		scaningTxt = (TextView) findViewById(R.id.scaning);

		Button mImageButton = (Button) findViewById(R.id.zing_chose);
		mImageButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			this.finish();
			break;
		case R.id.zing_chose:
			Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
			innerIntent.setType("image/*");
			Intent wrapperIntent = Intent.createChooser(innerIntent, "");
			this.startActivityForResult(wrapperIntent, REQUEST_CODE);
			break;
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			mProgress.dismiss();
			// switch (msg.what) {
			// case PARSE_BARCODE_SUC:
			// onResultHandler((String)msg.obj, scanBitmap);
			// Toast.makeText(MipcaActivityCapture.this, (String)msg.obj,
			// Toast.LENGTH_LONG).show();
			// break;
			// case PARSE_BARCODE_FAIL:
			// Toast.makeText(MipcaActivityCapture.this, (String)msg.obj,
			// Toast.LENGTH_LONG).show();
			// break;
			inactivityTimer.onActivity();
			playBeepSoundAndVibrate();

			resultString = (String) msg.obj;

			if (!RegularUtil.checkYSTNum(resultString)) {// 验证云视通号不正确
				errorDeviceDialog();
			} else {
				findDeviceDialog();
				scaningTxt.setText(resultString);
			}

			// }
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE:
				if (data != null)
					startPhotoZoom(data.getData(), PARSE_BARCODE_SUC);
				break;
			case PHOTO_REQUEST_CUT:
				if (data != null) {
					// Cursor cursor =
					// getContentResolver().query(data.getData(),
					// null, null, null, null);
					// if (cursor.moveToFirst()) {
					// photo_path = cursor.getString(cursor
					// .getColumnIndex(MediaStore.Images.Media.DATA));
					// }
					// cursor.close();

					setPicToView(data);

					mProgress = new ProgressDialog(MipcaActivityCapture.this);
					mProgress.setMessage("");
					mProgress.setCancelable(false);
					mProgress.show();

					new Thread(new Runnable() {
						@Override
						public void run() {
							Result result = scanningImage(photo_path);
							if (result != null) {
								Message m = mHandler.obtainMessage();
								m.what = PARSE_BARCODE_SUC;
								m.obj = result.getText();
								mHandler.sendMessage(m);
							} else {
								Message m = mHandler.obtainMessage();
								m.what = PARSE_BARCODE_FAIL;
								m.obj = "Scan failed!";
								mHandler.sendMessage(m);
							}
						}
					}).start();
				}
				break;

			}
		}
	}

	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			saveBitmap(photo);
			photo_path = Consts.HEAD_PATH + "erweima.jpg";
		}
	}

	public void saveBitmap(Bitmap bm) {
		if (null == bm) {
			return;
		}
		File f = new File(Consts.HEAD_PATH + "erweima.jpg");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ɨ���ά��ͼƬ�ķ���
	 * 
	 * @param path
	 * @return
	 */
	public Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8");

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);

		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
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
	 * ����ɨ����
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		// inactivityTimer.onActivity();
		// playBeepSoundAndVibrate();
		// String resultString = result.getText();
		// onResultHandler(resultString, barcode);
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
								Consts.WHAT_BARCODE_RESULT, resultIntent);
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
		Dialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
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