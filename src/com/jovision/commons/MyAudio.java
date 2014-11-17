package com.jovision.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.jovision.Consts;
import com.jovision.Jni;

public class MyAudio {

	private static final String TAG = "Audio";

	private static final int SAMPLERATE = 8000;
	private static final int SOURCE = MediaRecorder.AudioSource.MIC;
	private static final int CHANNEL = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
	private static final int TRACK_MODE = AudioTrack.MODE_STREAM;
	private static final int TRACK_STEP = 320;

	private static final String TARGET_FILE = Consts.LOG_PATH + File.separator
			+ "audio.out";

	private Play play;
	private Record record;

	private LinkedBlockingQueue<byte[]> queue;

	private int minSize;
	private boolean isRec;

	private MyAudio() {
		isRec = false;
		queue = new LinkedBlockingQueue<byte[]>();
	}

	private static class Container {
		private static MyAudio HOLDER = new MyAudio();
	}

	public static MyAudio getIntance() {
		return Container.HOLDER;
	}

	public void put(byte[] data) {
		if (null != data) {
			queue.offer(data);
		}
	}

	public void startPlay(int bit, boolean isFromQueue) {
		stopPlay();
		queue.clear();
		play = new Play(bit, isFromQueue);
		play.start();
	}

	public void stopPlay() {
		if (null != play) {
			play.interrupt();
			queue.offer(new byte[0]);
			play = null;
		}
	}

	public void startRec(int type, int bit, int block, boolean isSend) {
		stopRec();
		isRec = true;
		record = new Record(type, bit, block, isSend);
		record.start();
	}

	public void stopRec() {
		if (null != record) {
			isRec = false;
			record.interrupt();
			record = null;
		}
	}

	private class Play extends Thread {

		private int bit;
		private boolean isFromQueue;

		public Play(int bit, boolean isFromQueue) {
			this.bit = bit;
			this.isFromQueue = isFromQueue;
		}

		@Override
		public void run() {
			MyLog.w(TAG, "Play E: bit = " + bit + ", fromQueue = "
					+ isFromQueue);

			int offset = 0;
			int length = 0;
			int left = 0;

			byte[] data = null;

			FileInputStream inputStream = null;

			if (false == isFromQueue) {
				try {
					inputStream = new FileInputStream(new File(TARGET_FILE));
					data = new byte[TRACK_STEP];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			AudioTrack track = new AudioTrack(STREAM_TYPE, SAMPLERATE, CHANNEL,
					((16 == bit) ? AudioFormat.ENCODING_PCM_16BIT
							: AudioFormat.ENCODING_PCM_8BIT), minSize,
					TRACK_MODE);

			if (null != track
					&& AudioTrack.STATE_INITIALIZED == track.getState()) {
				track.play();

				while (false == isInterrupted()) {
					try {
						if (false == isFromQueue) {
							if (-1 != (length = inputStream.read(data, offset,
									left))) {
								if (offset + length < TRACK_STEP) {
									offset += length;
									left = TRACK_STEP - offset;
									continue;
								}
							} else {
								break;
							}
						} else {
							data = queue.take();
						}
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}

					if (null != data) {
						offset = 0;
						left = data.length;

						while (left > 0) {
							length = (left < TRACK_STEP) ? left : TRACK_STEP;
							track.write(data, offset, length);

							left -= TRACK_STEP;
							offset += TRACK_STEP;
						}
					}

					length = 0;
					offset = 0;
					left = 0;
				}

				track.stop();
				track.release();

			}

			track = null;

			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			MyLog.w(TAG, "Play X");
		}
	}

	private class Record extends Thread {

		private int type;
		private int bit;
		private int block;
		private boolean isSend;

		public Record(int type, int bit, int block, boolean isSend) {
			this.type = type;
			this.bit = bit;
			this.block = block;
			this.isSend = isSend;
		}

		@Override
		public void run() {
			minSize = AudioRecord.getMinBufferSize(SAMPLERATE, CHANNEL,
					AudioFormat.ENCODING_PCM_16BIT);

			MyLog.w(TAG, "Record E: type = " + type + ", bit = " + bit
					+ ", block = " + block + ", send = " + isSend
					+ ", minSize = " + minSize);

			if (minSize > 128) {
				AudioRecord rec = new AudioRecord(SOURCE, SAMPLERATE, CHANNEL,
						AudioFormat.ENCODING_PCM_16BIT, minSize);

				if (null != rec
						&& AudioRecord.STATE_INITIALIZED == rec.getState()) {

					int count = 0;
					int result = 0;
					byte[] data = new byte[block];
					byte[] out = new byte[block / 2];

					ByteBuffer buffer = ByteBuffer.wrap(out).order(
							ByteOrder.LITTLE_ENDIAN);

					if (isSend && Consts.JAE_ENCODER_G729 == type) {
						for (int i = 0; i < 16; i++) {
							out[i] = 0;
						}
					}

					if (type >= 0) {
						Jni.initAudioEncoder(type, SAMPLERATE, 1, 16, block);
					}

					FileOutputStream outputStream = null;
					if (false == isSend) {
						try {
							File file = new File(TARGET_FILE);
							boolean ready = true;
							if (file.exists()) {
								ready = file.delete();
								MyLog.d(TAG, "override: " + ready);
							}

							outputStream = new FileOutputStream(file);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					rec.startRecording();

					while (isRec) {
						result = rec.read(data, 0, block);
						if (result == block) {

							byte[] encoded = null;

							if (type >= 0) {
								encoded = Jni.encodeAudio(data);
							} else {
								encoded = data;
							}

							if (isSend) {
								if (Consts.JAE_ENCODER_G729 == type) {
									buffer.position(0);
									buffer.putInt(count++);
									buffer.position(16);
									buffer.put(encoded, 0, 60);

									Jni.sendBytes(0,
											JVNetConst.JVN_RSP_CHATDATA, out,
											76);
								} else {
									Jni.sendBytes(0,
											JVNetConst.JVN_RSP_CHATDATA,
											encoded, encoded.length);
								}
							} else {
								if (null != outputStream) {
									try {
										outputStream.write(encoded, 0,
												encoded.length);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

						} else {
							MyLog.e(TAG, "record: bad read size = " + result);
						}
					}

					rec.stop();
					rec.release();

					if (null != outputStream) {
						try {
							outputStream.flush();
							outputStream.close();
						} catch (Exception e) {
						}
					}

					if (type >= 0) {
						Jni.deinitAudioEncoder();
					}

				}

				rec = null;

			}

			MyLog.w(TAG, "Record X");
		}
	}

}
