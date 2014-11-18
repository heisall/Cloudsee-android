package com.jovision.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;

import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.MainApplication;
import com.jovision.activities.BaseActivity;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.bean.RemoteVideo;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;

/**
 * 播放相关功能
 * 
 * @author Administrator
 * 
 */

public class PlayUtil {

	private static final String TAG = "PlayUtil";
	private static Context mContext;

	public static Bitmap[] h264Bitmap = { null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null };

	public static void setContext(Context con) {
		mContext = con;
	}

	/**
	 * 广播
	 * 
	 * @param con
	 * @return
	 */
	public static boolean broadCast(Context con) {
		boolean canBroad = false;
		if (!((BaseActivity) con).is3G(false)) {// 非3G加广播设备
			canBroad = true;
			int res = Jni.searchLanDevice("", 0, 0, 0, "", 2000, 1);
		}
		return canBroad;
	}

	/**
	 * 抓拍
	 */
	public static boolean capture(int index) {
		String capturePath = Consts.CAPTURE_PATH + ConfigUtil.getCurrentTime()
				+ File.separator;
		String fileName = String.valueOf(System.currentTimeMillis()) + ".png";
		MobileUtil.createDirectory(new File(capturePath));
		MyLog.v(TAG, "capture=" + capturePath + fileName);
		return Jni.screenshot(index, capturePath + fileName, 100);
	}

	// /**
	// * 抓拍回调
	// *
	// * @param obj
	// */
	// public static boolean captureCallBack(Object obj) {
	// boolean captueRes = false;
	// if (null != obj) {
	// Object[] array = (Object[]) obj;
	// if (2 == array.length) {
	// try {
	// JSONObject object = new JSONObject(array[0].toString());
	// if (object.getInt("width") <= 0
	// || object.getInt("height") <= 0) {
	// return captueRes;
	// }
	// MyLog.v(TAG, "width = " + object.getInt("width")
	// + "--height = " + object.getInt("height"));
	// Bitmap bitmap = PlayUtil.createBitmap((byte[]) array[1],
	// object.getInt("width"), object.getInt("height"));
	// captueRes = saveCapture(bitmap);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// return captueRes;
	// }
	//
	// /**
	// * 老库生成bitmap
	// *
	// * @param index
	// * @param buffer
	// * @param w
	// * @param h
	// * @return
	// */
	// public static Bitmap parseBitmap(int index, ByteBuffer buffer, int w, int
	// h) {
	//
	// if (h264Bitmap[index] == null || h264Bitmap[index].isRecycled()
	// || w != h264Bitmap[index].getWidth()
	// || h != h264Bitmap[index].getHeight()) {
	//
	// h264Bitmap[index] = null;
	// Bitmap bit = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
	// h264Bitmap[index] = bit;
	// }
	//
	// buffer.position(0);
	// h264Bitmap[index].copyPixelsFromBuffer(buffer);
	// buffer.clear();
	//
	// return h264Bitmap[index];
	// }
	//
	// /**
	// * 新库生成bitmap
	// *
	// * @param data
	// * @param width
	// * @param height
	// * @return
	// */
	// public static Bitmap createBitmap(byte[] data, int width, int height) {
	// ByteBuffer buffer = ByteBuffer.wrap(data);
	// Bitmap bitmap = Bitmap.createBitmap(width, height,
	// Bitmap.Config.RGB_565);
	// buffer.position(0);
	// bitmap.copyPixelsFromBuffer(buffer);
	// buffer.clear();
	// data = null;
	// return bitmap;
	// }

	/**
	 * 抓拍声音
	 */
	public static void prepareAndPlay() {
		try {
			AssetManager assetMgr = mContext.getAssets();
			// 打开指定音乐文件
			AssetFileDescriptor afd = assetMgr.openFd("aaa.wav");
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.reset();

			// 使用MediaPlayer加载指定的声音文件。
			mediaPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			// 准备声音
			mediaPlayer.prepare();
			// 播放
			mediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// /**
	// * 抓拍保存图片
	// *
	// * @param bitmap
	// */
	// public static boolean saveCapture(Bitmap bitmap) {
	// boolean captureRes = false;
	//
	// if (null == bitmap) {
	// return captureRes;
	// }
	// prepareAndPlay();
	// String fileName = String.valueOf(System.currentTimeMillis()) + ".png";
	// BufferedOutputStream os = null;
	// try {
	// // JVPlayActivity.getInstance().makeSing();
	// long time = System.currentTimeMillis();
	// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	// String savePath = Consts.SD_CARD_PATH
	// + mContext.getResources().getString(
	// R.string.str_capture_path)
	// + dateFormat.format(time) + File.separator;
	// createDirectory(new File(savePath));
	// os = new BufferedOutputStream(new FileOutputStream(new File(
	// savePath + fileName)));
	// bitmap.compress(CompressFormat.PNG, 100, os);
	// if (null != bitmap) {
	// captureRes = true;
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// bitmap.recycle();
	// bitmap = null;
	// if (os != null) {
	// os.flush();
	// os.close();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// return captureRes;
	// }

	public static void destryBitmap(Bitmap bm) {
		if (bm != null && !bm.isRecycled()) {
			bm.recycle();
			bm = null;
			System.gc();
		}
	}

//	/**
//	 * 应用层调用音频监听功能
//	 * 
//	 * @param index
//	 * @return
//	 */
//	public static boolean audioPlay(int index) {
//		boolean open = false;
//		if (isPlayAudio(index)) {
//			boolean stopRes = stopAudioMonitor(index);
//			open = !stopRes;
//		} else {
//			boolean startRes = startAudioMonitor(index);
//			// // [Neo] 开启音频监听
//			// Jni.enablePlayAudio(index, true);
//			// audio.startPlay(channelList.get(index).getAudioBitCount(),true);
//			open = startRes;
//		}
//		return open;
//	}

	// // [Neo] 开启音频监听
	// Jni.enablePlayAudio(index, true);
	// audio.startPlay(channelList.get(index).getAudioBitCount(),
	// true);
	//
	// // [Neo] 关闭音频监听
	// Jni.enablePlayAudio(index, false);

	/**
	 * 查询音频监听状态
	 */
	public static boolean isPlayAudio(int index) {
		return Jni.isPlayAudio(index);
	}

	/**
	 * 开始音频监听
	 */
	public static boolean startAudioMonitor(int index) {
		return Jni.enablePlayAudio(index, true);
	}

	/**
	 * 停止音频监听
	 */
	public static boolean stopAudioMonitor(int index) {
		return Jni.enablePlayAudio(index, false);
	}

	/**
	 * 开始语音对讲
	 */
	public static void startVoiceCall(int index) {
		Jni.sendBytes(index, JVNetConst.JVN_REQ_CHAT, new byte[0], 8);
	}

	/**
	 * 停止语音对讲
	 */
	public static void stopVoiceCall(int index) {
		Jni.sendBytes(index, JVNetConst.JVN_CMD_CHATSTOP, new byte[0], 8);
	}

	/**
	 * 应用层调用录像功能
	 * 
	 * @param index
	 * @return
	 */
	public static boolean videoRecord(int index) {
		boolean open = false;
		if (checkRecord(index)) {
			boolean stopRes = stopVideoTape();
			open = !stopRes;
		} else {
			boolean startRes = startVideoTape(index);
			open = startRes;
		}
		return open;
	}

	/**
	 * 查询录像状态
	 * 
	 * @param index
	 * @return
	 */
	public static boolean checkRecord(int index) {
		return Jni.checkRecord(index);
	}

	/**
	 * 开始录像
	 * 
	 * @param context
	 * @param index
	 * @return
	 */
	public static boolean startVideoTape(int index) {
		String videoPath = Consts.VIDEO_PATH + ConfigUtil.getCurrentTime()
				+ File.separator;
		String fileName = String.valueOf(System.currentTimeMillis()) + ".mp4";
		MobileUtil.createDirectory(new File(videoPath));

		boolean startSuccess = Jni.startRecord(index, videoPath + fileName,
				true, true);
		return startSuccess;
	}

	/**
	 * 停止录像
	 */
	public static boolean stopVideoTape() {
		return Jni.stopRecord();
	}

	/**
	 * 给云台发短命令
	 * 
	 * @param index
	 * @param cmd
	 */
	public static void sendCtrlCMD(int index, int cmd) {

		byte[] data = new byte[4];
		data[0] = (byte) cmd;
		data[1] = (byte) 0;
		data[2] = (byte) 0;
		data[3] = (byte) 0;

		// 云台命令
		Jni.sendBytes(index, (byte) JVNetConst.JVN_CMD_YTCTRL, data, 4);
		if (cmd == JVNetConst.JVN_YTCTRL_A || cmd == JVNetConst.JVN_YTCTRL_AT)
			return;
		// 如果不是自动命令 发完云台命令接着发一条停止
		byte[] data1 = new byte[4];
		data1[0] = (byte) (cmd + 20);
		data1[1] = (byte) 0;
		data1[2] = (byte) 0;
		data1[3] = (byte) 0;
		Jni.sendBytes(index, (byte) JVNetConst.JVN_CMD_YTCTRL, data1, 4);
	}

	/**
	 * 长按给云台发命令
	 * 
	 * @param index
	 * @param cmd
	 * @param stop
	 */
	public static void sendCtrlCMDLongPush(final int index, final int cmd,
			final boolean stop) {
		new Thread() {
			@Override
			public void run() {
				byte[] data = new byte[4];
				data[0] = (byte) cmd;
				data[1] = (byte) 0;
				data[2] = (byte) 0;
				data[3] = (byte) 0;
				// 云台命令
				Jni.sendBytes(index, (byte) JVNetConst.JVN_CMD_YTCTRL, data, 4);
				if (stop)
					return;
				// 如果不是自动命令 发完云台命令接着发一条停止
				byte[] data1 = new byte[4];
				data1[0] = (byte) (cmd + 20);
				data1[1] = (byte) 0;
				data1[2] = (byte) 0;
				data1[3] = (byte) 0;
				Jni.sendBytes(index, (byte) JVNetConst.JVN_CMD_YTCTRL, data1, 4);
			}
		}.start();

	}

	/**
	 * 云台自动巡航命令
	 * 
	 * @param index
	 * @param cmd
	 * @param stop
	 */
	public static void sendCtrlCMDAuto(final int index, final int cmd,
			final boolean stop) {

		new Thread() {
			@Override
			public void run() {
				byte[] data = new byte[4];
				data[0] = (byte) cmd;
				data[1] = (byte) 0;
				data[2] = (byte) 0;
				data[3] = (byte) 0;
				Jni.sendBytes(index, (byte) JVNetConst.JVN_CMD_YTCTRL, data, 4);
				if (cmd == JVNetConst.JVN_YTCTRL_A)
					return;

				byte[] data1 = new byte[4];
				data1[0] = (byte) (cmd + 20);
				data1[1] = (byte) 0;
				data1[2] = (byte) 0;
				data1[3] = (byte) 0;
				Jni.sendBytes(index, (byte) JVNetConst.JVN_CMD_YTCTRL, data1, 4);
			}
		}.start();

	}

	/**
	 * 给设备列表添加小助手
	 * 
	 * @param deviceList
	 */
	public static void setHelperToList(ArrayList<Device> deviceList) {
		HashMap<String, Boolean> helperMap = getEnableHelperArray();
		JSONArray array = new JSONArray();
		JSONObject object = null;

		if (null == helperMap || 0 == helperMap.size()) {
			for (Device device : deviceList) {
				try {
					object = new JSONObject();
					object.put("gid", device.getGid());
					object.put("no", device.getNo());
					object.put("channel", 1);
					object.put("name", device.getUser());
					object.put("pwd", device.getPwd());
					array.put(object);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			for (Device device : deviceList) {
				if (helperMap.containsKey(device.getFullNo())
						&& helperMap.get(device.getFullNo())) {
					// 已设置小助手
					MyLog.v("已设置小助手", device.getFullNo());
				} else {
					MyLog.e("还没设置小助手", device.getFullNo());
					try {
						object = new JSONObject();
						object.put("gid", device.getGid());
						object.put("no", device.getNo());
						object.put("channel", 1);
						object.put("name", device.getUser());
						object.put("pwd", device.getPwd());
						array.put(object);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}

		if (!"".equalsIgnoreCase(array.toString())) {
			MyLog.e("需要设置小助手", array.toString());
			Jni.setLinkHelper(array.toString());
		}

	}

	/**
	 * 获取小助手是否已设置状态
	 * 
	 * @param deviceNum
	 * @return
	 */
	public static boolean hasEnableHelper(String deviceNum) {
		boolean isEnable = false;

		String allStr = Jni.getAllDeviceStatus();
		if (null != allStr && !"".equalsIgnoreCase(allStr)) {
			try {
				JSONArray devArray = new JSONArray(allStr);
				if (null != devArray) {
					int length = devArray.length();
					for (int i = 0; i < length; i++) {
						JSONObject obj = devArray.getJSONObject(i);
						if (deviceNum.equalsIgnoreCase(obj.getString("cno"))) {
							isEnable = obj.getBoolean("enable");
							break;
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		MyLog.v(TAG, deviceNum + "--小助手状态--" + isEnable);

		return isEnable;
	}

	/**
	 * 获取小助手是否已设置状态
	 * 
	 * @param deviceNum
	 * @return
	 */
	public static HashMap<String, Boolean> getEnableHelperArray() {
		HashMap<String, Boolean> helperMap = new HashMap<String, Boolean>();

		String allStr = Jni.getAllDeviceStatus();
		MyLog.v("设置的小助手总数=", allStr);
		if (null != allStr && !"".equalsIgnoreCase(allStr)) {
			try {
				JSONArray devArray = new JSONArray(allStr);
				if (null != devArray) {
					int length = devArray.length();
					for (int i = 0; i < length; i++) {
						JSONObject obj = devArray.getJSONObject(i);
						if (obj.getBoolean("enable")) {
							MyLog.e("小助手生效=", obj.toString() + "");
							helperMap.put(obj.getString("cno"),
									obj.getBoolean("enable"));
						}

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return helperMap;
	}

	// /**
	// * 暂停继续播对应视频
	// *
	// * @param currentScreen
	// * 当前分屏
	// * @param currentPage
	// * 当前页
	// * @param channelMap
	// * 存放channel的集合
	// */
	// public static void disAndPause(int currentScreen, int currentPage,
	// HashMap<Integer, Channel> channelMap) {
	//
	// MyLog.v(TAG, "disAndPause--E--currentScreen=" + currentScreen
	// + "currentPage=" + currentPage);
	// int start = 0;
	// int end = 4;
	// if (currentScreen == 1) {// 单屏
	// start = 0;
	// end = 4;
	// } else {
	// start = currentPage * currentScreen;
	// end = (currentPage + 1) * currentScreen;
	// }
	//
	// if (null == channelMap) {
	// return;
	// }
	// try {
	// // 单屏
	// if (currentScreen == 1) {
	// for (int key : channelMap.keySet()) {
	// Channel channel = channelMap.get(key);
	// if (null == channel) {
	// continue;
	// }
	//
	// if (key >= 4) {// 多屏切到单屏需断开多余的
	// // 断开连接
	// MyLog.v(TAG, "--" + channel.getIndex() + "不在范围内--"
	// + "断开视频");
	// channel.setPause(false);
	// Jni.disconnect(channel.getIndex());
	//
	// } else {
	// if (channel.isConnecting()) {// 正在连接调断开
	// MyLog.v(TAG, "--" + channel.getIndex() + "在范围内--"
	// + "正在连接调断开");
	// channel.setPause(false);
	// Jni.disconnect(channel.getIndex());
	// } else if (channel.isConnected() && !channel.isPause()) {//
	// 已连接的,非暂停的，需要发暂停
	// MyLog.v(TAG, "--" + channel.getIndex() + "在范围内--"
	// + "暂停视频");
	// channel.setPause(true);
	// // 暂停视频
	// Jni.sendBytes(channel.getIndex(),
	// (byte) JVNetConst.JVN_CMD_VIDEOPAUSE,
	// new byte[0], 8);
	//
	// }
	// }
	//
	// }
	// } else {
	// for (int key : channelMap.keySet()) {
	// // int flag = 0;
	// Channel channel = channelMap.get(key);
	// if (null == channel) {
	// continue;
	// }
	// if (channel.getIndex() >= start && channel.getIndex() < end) {
	// if (channel.isConnecting()) {// 正在连接调断开
	// MyLog.v(TAG, "--" + channel.getIndex() + "在范围内--"
	// + "正在连接调断开");
	// channel.setPause(false);
	// Jni.disconnect(channel.getIndex());
	// } else if (channel.isConnected() && !channel.isPause()) {//
	// 已连接的,非暂停的，需要发暂停
	// MyLog.v(TAG, "--" + channel.getIndex() + "在范围内--"
	// + "暂停视频");
	// channel.setPause(true);
	// // 暂停视频
	// Jni.sendBytes(channel.getIndex(),
	// (byte) JVNetConst.JVN_CMD_VIDEOPAUSE,
	// new byte[0], 8);
	// }
	// } else {
	// if (channel.isConnected() || channel.isConnecting()) {// 已连接的，需要断开
	// MyLog.v(TAG, "--" + channel.getIndex() + "不在范围内--"
	// + "断开视频");
	// channel.setPause(false);
	// // 断开连接
	// Jni.disconnect(channel.getIndex());
	// }
	// }
	//
	// // // 在map中找到在这个要连接的范围内的
	// // if (channel.isConnected()) {
	// // for (int i = start; i < end; i++) {
	// // if (channel.getIndex() == i) {
	// // // if (channel.isConnecting()) {// 正在连接调断开
	// // // MyLog.v(TAG, "--" + channel.getIndex()
	// // // + "在范围内--" + "正在连接调断开");
	// // // channel.setPause(false);
	// // // Jni.disconnect(channel.getIndex());
	// // // } else if (channel.isConnected()
	// // // && !channel.isPause()) {// 已连接的,非暂停的，需要发暂停
	// // // MyLog.v(TAG, "--" + channel.getIndex()
	// // // + "在范围内--" + "暂停视频");
	// // // channel.setPause(true);
	// // // // 暂停视频
	// // // Jni.sendBytes(
	// // // channel.getIndex(),
	// // // (byte) JVNetConst.JVN_CMD_VIDEOPAUSE,
	// // // new byte[0], 8);
	// // // }
	// // flag = 1;
	// // break;
	// // }
	// // }
	// // }
	// // // 找不到在这个范围内的
	// // if (0 == flag) {
	// // if (channel.isConnected() || channel.isConnecting()) {//
	// // 已连接的，需要断开
	// // MyLog.v(TAG, "--" + channel.getIndex() + "不在范围内--"
	// // + "断开视频");
	// // channel.setPause(false);
	// // // 断开连接
	// // Jni.disconnect(channel.getIndex());
	// // }
	// // }
	//
	// }
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// MyLog.v(TAG, "disAndPause--X");
	// }

	/**
	 * 断开所有视频
	 * 
	 * @param channleList
	 */
	public static void disConnectAll(ArrayList<Channel> channleList) {
		if (null != channleList && 0 != channleList.size()) {
			try {
				int size = channleList.size();
				for (int i = 0; i < size; i++) {
					if (channleList.get(i).isConnected()
							|| channleList.get(i).isConnecting()) {

						// ((View) mLastPlayView.getParent())
						// .setBackgroundColor(Color.BLACK);
						MyLog.v("disconnect", channleList.get(i).toString()
								+ "");
						Jni.disconnect(channleList.get(i).getIndex());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新所有已连接视频状态
	 * 
	 * @param channleList
	 */
	public static void updateState(ArrayList<Channel> channleList) {
		try {
			int size = channleList.size();
			for (int i = 0; i < size; i++) {
				if (channleList.get(i).isConnected()) {
					// || channleList.get(i).isConnecting()
					// || channleList.get(i).isPause()) {
					((MainApplication) mContext.getApplicationContext())
							.onNotify(Consts.CALL_NEW_PICTURE,
									channleList.get(i).getIndex(), 0, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /**
	// * 暂停所有
	// *
	// * @param channelList
	// */
	// public static void pauseAll(ArrayList<Channel> channelList) {
	// try {
	// // ArrayList<Channel> channelList = manager
	// // .getValidChannelList(currentPage);
	// int size = channelList.size();
	// for (int i = 0; i < size; i++) {
	// Channel channel = channelList.get(i);
	// if (channel.isConnected()) {// 已连接上的发暂停
	// channel.setPause(true);
	// // 暂停视频
	// Jni.sendBytes(channelList.get(i).getIndex(),
	// (byte) JVNetConst.JVN_CMD_VIDEOPAUSE, new byte[0],
	// 8);
	// // loadingState(channelList.get(i).getIndex(),
	// // R.string.paused, JVConst.PLAY_DIS_CONNECTTED);
	// } else if (channel.isConnecting()) {// 正在连接调断开
	// channel.setPause(false);
	// Jni.disconnect(channel.getIndex());
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * resume所有
	// *
	// * @param channelList
	// */
	// public static void resumeAll(final ArrayList<Channel> channelList,
	// final boolean isOmx, final String ssid) {
	// try {
	// Thread connectThread = new Thread() {
	// @Override
	// public void run() {
	// if (null != channelList && 0 != channelList.size()) {
	// int size = channelList.size();
	// for (int i = 0; i < size; i++) {
	// Channel channel = channelList.get(i);
	// if (channel.isPause()) {
	// channel.setPause(false);
	// // 继续播放视频
	// Jni.sendBytes(channelList.get(i).getIndex(),
	// (byte) JVNetConst.JVN_CMD_VIDEO,
	// new byte[0], 8);
	// } else if (!channel.isConnected()) {
	// // // 调用视频连接
	// // try {
	// // Thread.sleep(500);
	// // } catch (InterruptedException e) {
	// // e.printStackTrace();
	// // }
	// // channel.setPause(false);
	// // connect(channel, isOmx, ssid);
	// }
	// }
	// }
	// super.run();
	// }
	//
	// };
	// connectThread.start();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// // try {
	// // int size = channelList.size();
	// // for (int i = 0; i < size; i++) {
	// // Channel channel = channelList.get(i);
	// // if (channel.isPause()) {
	// // channel.setPause(false);
	// // // 继续播放视频
	// // Jni.sendBytes(channelList.get(i).getIndex(),
	// // (byte) JVNetConst.JVN_CMD_VIDEO, new byte[0], 8);
	// // // loadingState(channelList.get(i).getIndex(),
	// // // R.string.connecting_buffer,
	// // // JVConst.PLAY_CONNECTING_BUFFER);
	// // } else if (!channel.isConnected()) {
	// // // 调用视频连接
	// // channel.setPause(false);
	// // connect(channel.getParent(), channel.getChannel());
	// // }
	// // }
	// // } catch (Exception e) {
	// // e.printStackTrace();
	// // }
	// }

	// 根据在线状态排序
	public static ArrayList<Device> getOnLineList(ArrayList<Device> devList,
			boolean local) {
		ArrayList<Device> onlineDevice = new ArrayList<Device>();
		if (!local) {
			for (Device dev : devList) {
				if (1 == dev.getOnlineState()) {
					onlineDevice.add(dev);
				}
			}
		} else {
			return devList;
		}

		return onlineDevice;
	}

	// public static ArrayList<Device> prepareConnect(
	// ArrayList<Device> deviceList, int deviceIndex) {
	// // 获取真正播放的列表
	// // ArrayList<Device> playList = getOnLineList(deviceList, local);
	//
	// ArrayList<Channel> clist = new ArrayList<Channel>();
	//
	// if (MySharedPreference.getBoolean("PlayDeviceMode")) {
	// for (Device device : deviceList) {
	// clist.addAll(device.getChannelList().toList());
	// }
	// } else {
	// try {
	// if (null == deviceList.get(deviceIndex).getChannelList()
	// || 0 == deviceList.get(deviceIndex).getChannelList()
	// .size()) {
	//
	// } else {
	// clist.addAll(deviceList.get(deviceIndex).getChannelList()
	// .toList());
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	//
	// }
	//
	// int size = clist.size();
	// for (int i = 0; i < size; i++) {
	// // [Neo] 循环利用播放数组，我 tm 就是个天才
	// clist.get(i).setIndex(i);// % Consts.MAX_CHANNEL_CONNECTION);
	// }
	// // if (save) {
	// // CacheUtil.saveDevList(deviceList);
	// // }
	//
	// return deviceList;
	// }

	public static ArrayList<Device> prepareConnect(
			ArrayList<Device> deviceList, int deviceIndex) {
		ArrayList<Channel> clist = new ArrayList<Channel>();

		if (MySharedPreference.getBoolean("PlayDeviceMode")) {
			for (Device device : deviceList) {
				clist.addAll(device.getChannelList().toList());
			}
		} else {
			clist.addAll(deviceList.get(deviceIndex).getChannelList().toList());
		}

		int size = clist.size();
		for (int i = 0; i < size; i++) {
			// [Neo] 循环利用播放数组，我 tm 就是个天才
			clist.get(i).setIndex(i);// % Consts.MAX_CHANNEL_CONNECTION);
		}
		return deviceList;
	}

	// 获取播放列表中的index
	public static int getPlayIndex(ArrayList<Device> playList, String devNum) {
		int index = 0;
		int size = playList.size();
		for (int i = 0; i < size; i++) {
			Device dev = playList.get(i);
			if (devNum.equalsIgnoreCase(dev.getFullNo())) {
				index = i;
				break;
			}
		}
		return index;
	}

	// /**
	// * 视频连接
	// *
	// * @param device
	// * @param index
	// * ,设备的通道索引
	// */
	// public static void connect(Channel channel, boolean isOmx, String ssid) {
	// try {
	// if (null != channel) {
	// Device device = channel.getParent();
	//
	// // 如果是域名添加的设备需要先去解析IP
	// if (2 == device.getIsDevice()) {
	// device.setIp(ConfigUtil.getInetAddress(device.getDoMain()));
	// }
	//
	// // MyLog.e(TAG, "device=" + device.hashCode() + "--index=" +
	// // index);
	// // Channel channel = device.getChannelList().get(index);
	// if (null != channel) {
	// MyLog.v(TAG, channel.getIndex() + "");
	// ((MainApplication) mContext.getApplicationContext())
	// .onNotify(JVConst.WHAT_STARTING_CONNECT,
	// channel.getIndex(), 0, null);
	// if (channel.isConnecting()) {
	// MyLog.v(TAG, channel.getIndex() + "--正在连接，不需要重复连接");
	// return;
	// }
	// if (channel.isPause()) {
	// Jni.resume(channel.getIndex(), channel.getSurfaceView()
	// .getHolder().getSurface());
	// channel.setPause(false);
	// channel.setConnecting(false);
	// channel.setConnected(true);
	// // 继续播放视频
	// Jni.sendBytes(channel.getIndex(),
	// (byte) JVNetConst.JVN_CMD_VIDEO, new byte[0], 8);
	// MyLog.v(TAG, channel.getIndex() + "--发继续播");
	//
	// ((MainApplication) mContext.getApplicationContext())
	// .onNotify(Consts.CALL_FRAME_I_REPORT,
	// channel.getIndex(), 0, null);
	//
	// } else {
	// channel.setConnecting(true);
	// channel.setConnected(false);
	// MyLog.v(TAG, channel.getIndex() + "--调用连接");
	//
	// // if (Consts.CHANNEL_JY == channel.getIndex()) {
	// // Jni.connect(Consts.CHANNEL_JY, 0,
	// // Consts.IPC_DEFAULT_IP,
	// // Consts.IPC_DEFAULT_PORT,
	// // Consts.IPC_DEFAULT_USER,
	// // Consts.IPC_DEFAULT_PWD, device.getNo(),
	// // device.getGid(), true, 1, true, 6, null,
	// // false);
	// // } else {
	//
	// if (null != ssid
	// && channel.getParent().getFullNo()
	// .equalsIgnoreCase(ssid)) {
	// // IP直连
	// MyLog.v(TAG,
	// device.getNo() + "--AP--直连接："
	// + device.getIp());
	// Jni.connect(channel.getIndex(),
	// channel.getChannel(), device.getIp(),
	// device.getPort(), device.getUser(),
	// device.getPwd(), -1, device.getGid(), true,
	// 1, true, (device.isHomeProduct() ? 6 : 6),
	// channel.getSurfaceView().getHolder()
	// .getSurface(), isOmx);
	// } else {
	// if ("".equalsIgnoreCase(device.getIp())
	// || 0 == device.getPort()) {
	// // 云视通连接
	// MyLog.v(TAG, device.getNo() + "--云视通--连接");
	// Jni.connect(channel.getIndex(),
	// channel.getChannel(), device.getIp(),
	// device.getPort(), device.getUser(),
	// device.getPwd(), device.getNo(),
	// device.getGid(), true, 1, true,
	// (device.isHomeProduct() ? 6 : 6),
	// channel.getSurfaceView().getHolder()
	// .getSurface(), isOmx);
	// } else {
	// // IP直连
	// MyLog.v(TAG, device.getNo() + "--IP--连接："
	// + device.getIp());
	// Jni.connect(channel.getIndex(), channel
	// .getChannel(), device.getIp(), device
	// .getPort(), device.getUser(), device
	// .getPwd(), -1, device.getGid(), true,
	// 1, true, (device.isHomeProduct() ? 6
	// : 6), channel.getSurfaceView()
	// .getHolder().getSurface(),
	// isOmx);
	// }
	// }
	//
	// // }
	// }
	// }
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }

	/**
	 * 连接设备
	 * 
	 * @param ipcWifi
	 */
	public static void connectDevice(Device dev) {
		Jni.enablePlayAudio(1, false);
		if (!"".equalsIgnoreCase(dev.getIp())) {// IP直连云视通号置为-1
			Jni.connect(1, 1, dev.getIp(), dev.getPort(), dev.getUser(),
					dev.getPwd(), -1, ConfigUtil.getGroup(dev.getFullNo()),
					true, 1, true, 6, null, false);
		} else {
			Jni.connect(1, 1, dev.getIp(), dev.getPort(), dev.getUser(),
					dev.getPwd(), ConfigUtil.getYST(dev.getFullNo()),
					ConfigUtil.getGroup(dev.getFullNo()), true, 1, true, 6,
					null, false);
		}

	}

	/**
	 * 断开视频
	 */
	public static boolean disconnectDevice() {
		return Jni.disconnect(1);
	}

	/***************** 以下为远程回放所有功能 ***************************/

	private static byte[] acFLBuffer = new byte[2048];

	public static void checkRemoteData(int index, String date) {
		Jni.sendBytes(index, (byte) JVNetConst.JVN_REQ_CHECK, date.getBytes(),
				28);
	}

	// JVSUDT.JVC_SendData(windowIndex + 1,
	// (byte) JVNetConst.JVN_REQ_CHECK, date.getBytes(),
	// 28);

	/**
	 * 远程检索回调获取到码流数据list
	 * 
	 * @param pBuffer
	 * @param deviceType
	 * @param channelIndex
	 * @return
	 */
	public static ArrayList<RemoteVideo> getRemoteList(byte[] pBuffer,
			int deviceType, int channelOfChannel) {

		ArrayList<RemoteVideo> datalist = new ArrayList<RemoteVideo>();

		try {
			String textString1 = new String(pBuffer);
			MyLog.v("远程回放pBuffer", "deviceType=" + deviceType + ";pBuffer="
					+ textString1);

			int nSize = pBuffer.length;
			// 无数据
			if (nSize == 0) {
				return datalist;
				// if (null != videoList) {
				// videoList.clear();
				// }
				// handler.sendMessage(handler
				// .obtainMessage(JVConst.REMOTE_NO_DATA_FAILED));
				// return;
			}

			if (deviceType == 0) {
				for (int i = 0; i <= nSize - 7; i += 7) {
					RemoteVideo rv = new RemoteVideo();
					rv.remoteChannel = String.format("%02d", channelOfChannel);
					rv.remoteDate = String.format("%c%c:%c%c:%c%c",
							pBuffer[i + 1], pBuffer[i + 2], pBuffer[i + 3],
							pBuffer[i + 4], pBuffer[i + 5], pBuffer[i + 6]);
					rv.remoteDisk = String.format("%c", pBuffer[i]);
					datalist.add(rv);
				}
			} else if (deviceType == 1 || deviceType == 4 || deviceType == 5) {
				int nIndex = 0;
				for (int i = 0; i <= nSize - 10; i += 10) {
					acFLBuffer[nIndex++] = pBuffer[i];// 录像所在盘
					acFLBuffer[nIndex++] = pBuffer[i + 7];// 录像类型
					RemoteVideo rv = new RemoteVideo();
					rv.remoteChannel = String.format("%c%c", pBuffer[i + 8],
							pBuffer[i + 9]);
					rv.remoteDate = String.format("%c%c:%c%c:%c%c",
							pBuffer[i + 1], pBuffer[i + 2], pBuffer[i + 3],
							pBuffer[i + 4], pBuffer[i + 5], pBuffer[i + 6]);
					rv.remoteKind = String.format("%c", pBuffer[i + 7]);
					rv.remoteDisk = String.format("%s%d", "",
							(pBuffer[i] - 'C') / 10 + 1);
					datalist.add(rv);
				}
				// int nIndex = 0;
				// for (int i = 0; i <= nSize - 10; i += 10) {
				// acFLBuffer[nIndex++] = pBuffer[i];// 录像所在盘
				// acFLBuffer[nIndex++] = pBuffer[i + 7];// 录像类型
				// RemoteVideo rv = new RemoteVideo();
				// rv.remoteChannel = String.format("%c%c", pBuffer[i + 8],
				// pBuffer[i + 9]);
				// rv.remoteDate = String.format("%c%c:%c%c:%c%c",
				// pBuffer[i + 1], pBuffer[i + 2], pBuffer[i + 3],
				// pBuffer[i + 4], pBuffer[i + 5], pBuffer[i + 6]);
				// rv.remoteDisk = String.format("%s%d", "",
				// (pBuffer[i] - 'C') / 10 + 1);
				// datalist.add(rv);
				// }
			} else if (deviceType == 2 || deviceType == 3) {
				for (int i = 0; i <= nSize - 7; i += 7) {
					RemoteVideo rv = new RemoteVideo();
					rv.remoteChannel = String.format("%02d", channelOfChannel);
					rv.remoteDate = String.format("%c%c:%c%c:%c%c",
							pBuffer[i + 1], pBuffer[i + 2], pBuffer[i + 3],
							pBuffer[i + 4], pBuffer[i + 5], pBuffer[i + 6]);
					rv.remoteDisk = String.format("%c", pBuffer[i]);
					datalist.add(rv);
				}
			}

			// if (null != datalist && 0 != datalist.size()) {
			// videoList = datalist;
			// handler.sendMessage(handler.obtainMessage(JVConst.REMOTE_DATA_SUCCESS));
			//
			// } else {
			// handler.sendMessage(handler.obtainMessage(JVConst.REMOTE_NO_DATA_FAILED));
			//
			// }
			//
			// datalist = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// for(int i = 0 ; i < datalist.size();i++){
		// RemoteVideo rv = datalist.get(i);
		// MyLog.v("远程回放--"+i, "channel="+rv.remoteChannel
		// +"Date="+rv.remoteDate
		// +"disk="+rv.remoteDisk
		// +"kind="+rv.remoteKind);
		// }

		return datalist;
	}

	/**
	 * 拼接远程回放视频参数
	 * 
	 * @param videoBean
	 * @param is05
	 * @param deviceType
	 * @param year
	 * @param month
	 * @param day
	 * @param listIndex
	 * @return
	 */
	public static String getPlayFileString(RemoteVideo videoBean,
			boolean isJFH, int deviceType, int year, int month, int day,
			int listIndex) {
		byte acChn[] = new byte[3];
		byte acTime[] = new byte[10];
		byte acDisk[] = new byte[2];
		String acBuffStr = "";
		if (null == videoBean) {
			return acBuffStr;
		}

		MyLog.v("远程回放单个文件", "deviceType=" + deviceType + ";isJFH=" + isJFH);
		if (isJFH) {
			if (deviceType == 0) {

				// sprintf(acChn, "%s",videoBean.remoteChannel);
				String channelStr = String
						.format("%s", videoBean.remoteChannel);
				MyLog.e("channelStr", channelStr);
				System.arraycopy(channelStr.getBytes(), 0, acChn, 0,
						channelStr.length());

				// sprintf(acTime, "%s",videoBean.remoteDate);
				String acTimeStr = String.format("%s", videoBean.remoteDate);
				System.arraycopy(acTimeStr.getBytes(), 0, acTime, 0,
						acTimeStr.length());

				// sprintf(acDisk, "%s",videoBean.remoteDisk);
				String acDiskStr = String.format("%s", videoBean.remoteDisk);
				System.arraycopy(acDiskStr.getBytes(), 0, acDisk, 0,
						acDiskStr.length());
				acBuffStr = String.format(
						"%c:\\JdvrFile\\%04d%02d%02d\\%c%c%c%c%c%c%c%c.mp4",
						acDisk[0], year, month, day, acChn[0], acChn[1],
						acTime[0], acTime[1], acTime[3], acTime[4], acTime[6],
						acTime[7]);

			} else if (deviceType == 1 || deviceType == 4 || deviceType == 5) {
				String channelStr = String
						.format("%s", videoBean.remoteChannel);
				System.arraycopy(channelStr.getBytes(), 0, acChn, 0,
						channelStr.length());

				// sprintf(acTime, "%s",videoBean.remoteDate);
				String acTimeStr = String.format("%s", videoBean.remoteDate);
				System.arraycopy(acTimeStr.getBytes(), 0, acTime, 0,
						acTimeStr.length());

				acBuffStr = String.format(
						"./rec/%02d/%04d%02d%02d/%c%c%c%c%c%c%c%c%c.mp4",
						acFLBuffer[listIndex * 2] - 'C', year, month, day,
						acFLBuffer[listIndex * 2 + 1], acChn[0], acChn[1],
						acTime[0], acTime[1], acTime[3], acTime[4], acTime[6],
						acTime[7]);

			}

			MyLog.v("url: ", acBuffStr);
		} else if (deviceType == 0) {
			String channelStr = String.format("%s", videoBean.remoteChannel);
			System.arraycopy(channelStr.getBytes(), 0, acChn, 0,
					channelStr.length());

			// sprintf(acTime, "%s",videoBean.remoteDate);
			String acTimeStr = String.format("%s", videoBean.remoteDate);
			System.arraycopy(acTimeStr.getBytes(), 0, acTime, 0,
					acTimeStr.length());

			// sprintf(acDisk, "%s",videoBean.remoteDisk);
			String acDiskStr = String.format("%s", videoBean.remoteDisk);
			System.arraycopy(acDiskStr.getBytes(), 0, acDisk, 0,
					acDiskStr.length());

			acBuffStr = String.format(
					"%c:\\JdvrFile\\%04d%02d%02d\\%c%c%c%c%c%c%c%c.sv4",
					acDisk[0], year, month, day, acChn[0], acChn[1], acTime[0],
					acTime[1], acTime[3], acTime[4], acTime[6], acTime[7]);

		} else if (deviceType == 1 || deviceType == 4 || deviceType == 5) {
			String channelStr = String.format("%s", videoBean.remoteChannel);
			System.arraycopy(channelStr.getBytes(), 0, acChn, 0,
					channelStr.length());
			MyLog.v("channelStr:", channelStr);
			// sprintf(acTime, "%s",videoBean.remoteDate);
			String acTimeStr = String.format("%s", videoBean.remoteDate);
			MyLog.v("acTimeStr:", acTimeStr);
			System.arraycopy(acTimeStr.getBytes(), 0, acTime, 0,
					acTimeStr.length());
			acBuffStr = String.format(
					"./rec/%02d/%04d%02d%02d/%c%c%c%c%c%c%c%c%c.sv5",
					acFLBuffer[listIndex * 2] - 'C', year, month, day,
					acFLBuffer[listIndex * 2 + 1], acChn[0], acChn[1],
					acTime[0], acTime[1], acTime[3], acTime[4], acTime[6],
					acTime[7]);
			MyLog.v("acBuffStr:", acBuffStr);
		} else if (deviceType == 2 || deviceType == 3) {
			String channelStr = String.format("%s", videoBean.remoteChannel);
			System.arraycopy(channelStr.getBytes(), 0, acChn, 0,
					channelStr.length());

			// sprintf(acTime, "%s",videoBean.remoteDate);
			String acTimeStr = String.format("%s", videoBean.remoteDate);
			System.arraycopy(acTimeStr.getBytes(), 0, acTime, 0,
					acTimeStr.length());

			// sprintf(acDisk, "%s",videoBean.remoteDisk);
			String acDiskStr = String.format("%s", videoBean.remoteDisk);
			System.arraycopy(acDiskStr.getBytes(), 0, acDisk, 0,
					acDiskStr.length());
			acBuffStr = String.format(
					"%c:\\JdvrFile\\%04d%02d%02d\\%c%c%c%c%c%c%c%c.sv6",
					acDisk[0], year, month, day, acChn[0], acChn[1], acTime[0],
					acTime[1], acTime[3], acTime[4], acTime[6], acTime[7]);
			MyLog.v("url: ", acBuffStr);

		}
		MyLog.v("tags", "bytesize: " + acBuffStr.getBytes().length + ", url:"
				+ acBuffStr);
		acChn = null;
		acTime = null;
		acDisk = null;

		return acBuffStr;
	}
}
