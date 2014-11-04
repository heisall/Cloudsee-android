package com.jovision.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.activities.JVPlayActivity;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;

public class MICRecorder {

	private static final String TAG = "MICR";

	private static final int SOURCE = MediaRecorder.AudioSource.MIC;
	private static final int SAMPLERATE = 8000;
	private static final int CHANNEL = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	// TODO
	private int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	// TODO
	private int ENCODE_SIZE = 640;// -1 320

	private int bufferSize;
	private boolean isWorking;

	private AudioRecord record;

	private int indexOfChannel;
	private static MICRecorder RECORDER;

	public void setIndex(int index) {
		indexOfChannel = index;
	}

	private MICRecorder() {
		isWorking = false;
		bufferSize = AudioRecord
				.getMinBufferSize(SAMPLERATE, CHANNEL, ENCODING);
		MyLog.i(TAG, "construction, size = " + bufferSize);
	}

	public static MICRecorder getInstance(int audioByte) {
		if (null == RECORDER) {
			RECORDER = new MICRecorder();
		}
		if (8 == audioByte) {
			RECORDER.ENCODING = AudioFormat.ENCODING_PCM_8BIT;
		} else {
			RECORDER.ENCODING = AudioFormat.ENCODING_PCM_16BIT;
		}
		return RECORDER;
	}

	public boolean start(final int type, final int audioByte) {
		boolean result = false;
		ENCODE_SIZE = 320;
		if (Consts.JAE_ENCODER_SAMR == type || Consts.JAE_ENCODER_ALAW == type
				|| Consts.JAE_ENCODER_ULAW == type) {
			ENCODE_SIZE = 640;
		}

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
						MyLog.v(TAG, "recording E");

						try {
							int ret = 0;
							byte[] buffer = new byte[ENCODE_SIZE];

							if (Consts.JAE_ENCODER_SAMR == type
									|| Consts.JAE_ENCODER_ALAW == type
									|| Consts.JAE_ENCODER_ULAW == type) {
								Jni.initAudioEncoder(type, SAMPLERATE, 1,
										audioByte, ENCODE_SIZE);
							}

							while (isWorking) {
								ret = record.read(buffer, 0, ENCODE_SIZE);

								if (ret == ENCODE_SIZE) {
									byte[] enc = buffer;
									if (Consts.JAE_ENCODER_SAMR == type
											|| Consts.JAE_ENCODER_ALAW == type
											|| Consts.JAE_ENCODER_ULAW == type) {
										enc = Jni.encodeAudio(buffer);
									}

									if (null != enc) {
										if (JVPlayActivity.AUDIO_SINGLE) {// 单向对讲长按才发送语音数据
											if (JVPlayActivity.VOICECALL_LONG_CLICK) {
												Jni.sendBytes(
														indexOfChannel,
														JVNetConst.JVN_RSP_CHATDATA,
														enc, enc.length);
											}
										} else {// 双向对讲长按才发送语音数据
											Jni.sendBytes(
													indexOfChannel,
													JVNetConst.JVN_RSP_CHATDATA,
													enc, enc.length);
										}
									} else {
										MyLog.e(Consts.TAG_APP,
												"record decode null");
									}
								} else {
									MyLog.e(Consts.TAG_APP, "record bad size: "
											+ ret);
								}
							}

							if (Consts.JAE_ENCODER_SAMR == type
									|| Consts.JAE_ENCODER_ALAW == type
									|| Consts.JAE_ENCODER_ULAW == type) {
								Jni.deinitAudioEncoder();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

						MyLog.i(TAG, "recording X");
					}

				}.start();
			} else {
				MyLog.e(TAG, "record init not ok");
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

}