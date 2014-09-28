package com.jovision.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.SurfaceView;

import com.jovision.Consts;
import com.jovision.commons.MyList;

/**
 * 简单的通道集合类
 * 
 * @author neo
 * 
 */
public class Channel {

	private final static String TAG = "Channel";

	/** 窗口索引 */
	private int index;
	/** 设备通道，从 0 开始 */
	private int channel;

	/** 通道昵称 */
	public String channelName = "";
	private boolean isConnecting;
	private boolean isConnected;
	private boolean isRemotePlay;
	private boolean isConfigChannel;
	private SurfaceView surfaceView;
	private Device parent;
	private boolean isAuto = false;// 是否开启自动巡航
	private boolean isPause = false;// 是否暂停视频
	private boolean isVoiceCall = false;// 是否正在对讲
	private boolean surfaceCreated = false;// surface是否已经创建
	private boolean isSendCMD = false;// 是否只发关键帧

	public Channel(Device device, int index, int channel, boolean isConnected,
			boolean isRemotePlay, String nick) {
		this.parent = device;
		this.index = index;
		this.channel = channel;
		this.isConnected = isConnected;
		this.isRemotePlay = isRemotePlay;
		this.channelName = nick;
		if (Consts.CHANNEL_JY == index) {
			isConfigChannel = true;
		} else {
			isConfigChannel = false;
		}
	}

	public Channel() {
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getChannel() {
		return channel;
	}

	public SurfaceView getSurfaceView() {
		return surfaceView;
	}

	public void setSurfaceView(SurfaceView surfaceView) {
		this.surfaceView = surfaceView;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isRemotePlay() {
		return isRemotePlay;
	}

	public void setRemotePlay(boolean isRemotePlay) {
		this.isRemotePlay = isRemotePlay;
	}

	public boolean isConfigChannel() {
		return isConfigChannel;
	}

	public JSONObject toJson() {
		JSONObject object = new JSONObject();

		try {
			object.put("index", index);
			object.put("channel", channel);
			object.put("channelName", channelName);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return object;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public JSONArray toJsonArray(ArrayList<Channel> channelList) {
		JSONArray channelArray = new JSONArray();

		try {
			if (null != channelList && 0 != channelList.size()) {
				int size = channelList.size();
				for (int i = 0; i < size; i++) {
					channelArray.put(i, channelList.get(i).toJson());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return channelArray;
	}

	public String listToString(ArrayList<Channel> devList) {
		return toJsonArray(devList).toString();
	}

	public static Channel fromJson(String string) {
		Channel channel = new Channel();
		try {
			JSONObject object = new JSONObject(string);
			channel.setIndex(object.getInt("index"));
			channel.setChannel(object.getInt("channel"));
			channel.setChannelName(object.getString("channelName"));
			// channel.setConnecting(object.getBoolean("isConnecting"));
			// channel.setConnecting(object.getBoolean("isConnected"));
			// channel.setRemotePlay(object.getBoolean("isRemotePlay"));
			// channel.setConfigChannel(object.getBoolean("isConfigChannel"));
			// channel.setAuto(object.getBoolean("isAuto"));
			// channel.setVoiceCall(object.getBoolean("isVoiceCall"));
			// channel.setSurfaceCreated(object.getBoolean("surfaceCreated"));
			// channel.setSendCMD(object.getBoolean("isSendCMD"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return channel;
	}

	public static MyList<Channel> fromJsonArray(String string, Device device) {
		MyList<Channel> channelList = new MyList<Channel>();
		if (null == string || "".equalsIgnoreCase(string)) {
			return channelList;
		}
		JSONArray channelArray;
		try {
			channelArray = new JSONArray(string);
			if (null != channelArray && 0 != channelArray.length()) {
				int length = channelArray.length();
				for (int i = 0; i < length; i++) {
					Channel channel = fromJson(channelArray.get(i).toString());
					channel.setParent(device);
					if (null != channel) {
						channelList.add(channel.getChannel(), channel);
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return channelList;
	}

	public Device getParent() {
		return parent;
	}

	public boolean isAuto() {
		return isAuto;
	}

	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}

	public boolean isConnecting() {
		return isConnecting;
	}

	public void setConnecting(boolean isConnecting) {
		this.isConnecting = isConnecting;
	}

	public boolean isVoiceCall() {
		return isVoiceCall;
	}

	public void setVoiceCall(boolean isVoiceCall) {
		this.isVoiceCall = isVoiceCall;
	}

	public boolean isSurfaceCreated() {
		return surfaceCreated;
	}

	public void setSurfaceCreated(boolean surfaceCreated) {
		this.surfaceCreated = surfaceCreated;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public void setConfigChannel(boolean isConfigChannel) {
		this.isConfigChannel = isConfigChannel;
	}

	public void setParent(Device parent) {
		this.parent = parent;
	}

	public boolean isSendCMD() {
		return isSendCMD;
	}

	public void setSendCMD(boolean isSendCMD) {
		this.isSendCMD = isSendCMD;
	}

}
