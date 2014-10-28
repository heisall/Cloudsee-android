package com.jovision.audio;

import java.util.concurrent.LinkedBlockingQueue;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.jovision.commons.MyLog;

public class PlayAudio extends Thread {

	private static final int SAMPLERATE = 8000;
	private static final int CHANNEL = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	// TODO
	private int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
	private static final int TRACK_MODE = AudioTrack.MODE_STREAM;

	private LinkedBlockingQueue<byte[]> audioQueue;

	public MICRecorder recorder;
	private int indexOfChannel;

	public PlayAudio(LinkedBlockingQueue<byte[]> queue, int audioByte) {
		audioQueue = queue;
		recorder = MICRecorder.getInstance();

		if (8 == audioByte) {
			MyLog.v("ENCODING", "8---" + ENCODING);
			ENCODING = AudioFormat.ENCODING_PCM_8BIT;
		} else {
			MyLog.v("ENCODING", "16---" + ENCODING);
			ENCODING = AudioFormat.ENCODING_PCM_16BIT;
		}

	}

	public void setIndex(int index) {
		indexOfChannel = index;
		recorder.setIndex(indexOfChannel);
	}

	@Override
	public void run() {
		// File file = new File(Consts.LOG_PATH + "/x.pcm");
		// if (file.exists()) {
		// file.delete();
		// }
		//
		// FileOutputStream outputStream = null;
		// try {
		// outputStream = new FileOutputStream(file);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }

		AudioTrack track = new AudioTrack(STREAM_TYPE, SAMPLERATE, CHANNEL,
				ENCODING, 1024, TRACK_MODE);
		track.play();

		MyLog.v("ENCODING", ENCODING + "");
		byte[] data = null;
		byte[] enc = null;
		while (false == isInterrupted()) {
			if (null != track && null != audioQueue) {
				try {
					data = audioQueue.take();
					// data = recorder.start();
					if (null != data) {
						track.write(data, 0, data.length);
						// enc = Jni.encodeAudio(data);
						// if (null != enc && enc.length > 0) {
						// MyLog.v("PlayAudio", ""+enc.length);
						// // outputStream.write(enc);
						// // Jni.sendBytes(0,
						// // JVNetConst.JVN_RSP_CHATDATA, enc,
						// // enc.length);
						// }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// if (null != outputStream) {
		// try {
		// outputStream.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }

		track.stop();
		track.release();
		track = null;
	}
}