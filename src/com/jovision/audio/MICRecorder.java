package com.jovision.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;

public class MICRecorder {

	private static final String TAG = "MICR";

	private static final int SOURCE = MediaRecorder.AudioSource.MIC;
	private static final int SAMPLERATE = 8000;
	private static final int CHANNEL = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final int ENCODE_SIZE = 640;
	private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
	private static final int TRACK_MODE = AudioTrack.MODE_STREAM;
	private static final int DECODE_SIZE = 21;

	private static final String TARGET_FILE = Consts.LOG_PATH + File.separator
			+ "o.pcm";

	private static int encodeSize;
	private static int bufferSize;
	private static boolean isWorking;

	private AudioTrack track;
	private AudioRecord record;

	private int indexOfChannel;
	private static MICRecorder RECORDER;

	public void setIndex(int index) {
		indexOfChannel = index;
	}

	// private MICRecorder() {
	// isWorking = false;
	// bufferSize = AudioRecord
	// .getMinBufferSize(SAMPLERATE, CHANNEL, ENCODING);
	// encodeSize = ENCODE_SIZE;
	// MyLog.i(TAG, "construction, size = " + bufferSize);
	// }

	// private static class MICRecorderContainer {
	// private static MICRecorder RECORDER = new MICRecorder();
	// }

	public static MICRecorder getInstance() {
		if (null == RECORDER) {
			RECORDER = new MICRecorder();
			isWorking = false;
			bufferSize = AudioRecord.getMinBufferSize(SAMPLERATE, CHANNEL,
					ENCODING);
			encodeSize = ENCODE_SIZE;
			MyLog.i(TAG, "construction, size = " + bufferSize);
		}

		return RECORDER;
	}

	public int getEncodeSize() {
		return encodeSize;
	}

	public void setEncodeSize(int encodeSize) {
		this.encodeSize = encodeSize;
	}

	public boolean start() {
		boolean result = false;

		if (false == isWorking && bufferSize > 128) {
			record = new AudioRecord(SOURCE, SAMPLERATE, CHANNEL, ENCODING,
					bufferSize);

			if (null != record
					&& AudioRecord.STATE_INITIALIZED == record.getState()) {

				record.startRecording();

				result = true;
				isWorking = true;

				new Thread() {

					@Override
					public void run() {
						MyLog.w(TAG, "recording E");
						try {
							File file = new File(TARGET_FILE);
							boolean ready = true;
							if (file.exists()) {
								ready = file.delete();
								MyLog.w(TAG, "overide: " + ready);
							}

							if (ready) {
								FileOutputStream outputStream = new FileOutputStream(
										file);
								int ret = 0;
								byte[] buffer = new byte[encodeSize];
								// byte[] out = null;
								while (isWorking) {
									ret = record.read(buffer, 0, encodeSize);
									if (ret > 0) {
										if (ret == ENCODE_SIZE) {
											// out = Audio.enc(buffer);
											byte[] enc = Jni
													.encodeAudio(buffer);
											if (null != enc) {
												// outputStream.write(out);
												Jni.sendBytes(
														indexOfChannel,
														JVNetConst.JVN_RSP_CHATDATA,
														enc, enc.length);
											} else {
												MyLog.e(Consts.TAG_APP,
														"record decode null");
											}
										} else {
											MyLog.e(Consts.TAG_APP,
													"record bad size: " + ret);
										}
									}
								}
								outputStream.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						MyLog.w(TAG, "recording X");
					}

				}.start();
			} else {
				MyLog.w(TAG, "record init not ok");
			}
		}

		MyLog.i(TAG, "start with: " + result);
		return result;
	}

	public boolean stop() {
		boolean result = false;

		if (isWorking) {
			isWorking = false;
			if (null != record) {
				if (AudioRecord.STATE_INITIALIZED == record.getState()
						&& AudioRecord.RECORDSTATE_RECORDING == record
								.getRecordingState()) {
					record.stop();
				}
				record.release();
				record = null;
				result = true;
			}
		}

		MyLog.i(TAG, "stop with: " + result);
		return result;
	}

	public boolean play() {
		boolean result = false;
		final File file = new File(TARGET_FILE);

		if (null != file && file.exists() && null == track) {

			new Thread() {

				@Override
				public void run() {
					try {
						byte[] buffer = new byte[DECODE_SIZE];
						FileInputStream inputStream = new FileInputStream(file);
						track = new AudioTrack(STREAM_TYPE, SAMPLERATE,
								CHANNEL, ENCODING, bufferSize, TRACK_MODE);

						if (null != track) {
							track.play();

							int offset = 0;
							int length = DECODE_SIZE;
							int count = 0;

							byte[] out = null;

							while (-1 != (count = inputStream.read(buffer,
									offset, length))) {
								if (offset + count < DECODE_SIZE) {
									offset += count;
									length = DECODE_SIZE - offset;
									continue;
								}

								out = Audio.dec(buffer);
								if (null != out) {
									track.write(out, 0, out.length);
									// Thread.sleep(20);
								} else {
									MyLog.e(Consts.TAG_APP, "play with null");
								}
							}

							inputStream.close();
							track.stop();
							track.release();
							track = null;
						}
					} catch (Exception e) {
					}
				}

			}.start();

			result = true;
		}

		return result;
	}

	public boolean foo1() {
		boolean result = false;

		if (false == isWorking && bufferSize > 128) {
			record = new AudioRecord(SOURCE, SAMPLERATE, CHANNEL, ENCODING,
					bufferSize);

			if (null != record
					&& AudioRecord.STATE_INITIALIZED == record.getState()) {

				result = true;
				isWorking = true;

				new Thread() {

					@Override
					public void run() {
						MyLog.w(TAG, "foo E");

						record.startRecording();

						try {
							File file = new File(TARGET_FILE);
							boolean ready = true;
							if (file.exists()) {
								ready = file.delete();
								MyLog.w(TAG, "overide: " + ready);
							}

							if (ready) {
								FileOutputStream outputStream = new FileOutputStream(
										file);
								byte[] buffer = new byte[encodeSize];
								int ret = 0;

								FileInputStream inputStream = new FileInputStream(
										file);
								track = new AudioTrack(STREAM_TYPE, SAMPLERATE,
										CHANNEL, ENCODING, bufferSize,
										TRACK_MODE);

								track.play();

								while (isWorking) {
									ret = record.read(buffer, 0, encodeSize);
									if (AudioRecord.ERROR_INVALID_OPERATION != ret) {
										outputStream.write(buffer);
										track.write(buffer, 0, encodeSize);
									}
								}
								outputStream.close();

								inputStream.close();
								track.stop();
								track.release();
								track = null;
							}
						} catch (Exception e) {
						}

						MyLog.w(TAG, "foo X");
					}

				}.start();
			} else {
				MyLog.w(TAG, "record init not ok");
			}
		}

		MyLog.i(TAG, "foo with: " + result);
		return result;
	}
}
