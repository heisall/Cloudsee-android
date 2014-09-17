package com.jovision.commons;

public class JVLittleTipsPacket extends JVPacket {

	public static final int H264PACKET_SIZE = 8;
	private String chGroup;// 占用四个字节
	private int nYSTNO = 0;// 占用4个字节位
	private int nChannel = 0;// 占用4个字节位
	private String chPName;// 占用一个字节中的8个位
	private String chPWord;
	private int nConnectStatus;

	// int x;
	public JVLittleTipsPacket() {
		super(H264PACKET_SIZE);
	}

	public JVLittleTipsPacket(int packetLen) {
		super(packetLen);
	}

	public JVLittleTipsPacket pack() {
		this.myPacket.position(0);
		byte group[] = new byte[4];
		System.arraycopy(chGroup.getBytes(), 0, group, 0,
				chGroup.getBytes().length);
		this.myPacket.put(group);

		this.myPacket.putInt(this.nYSTNO);
		this.myPacket.putInt(this.nChannel);
		byte[] buff = new byte[256];
		System.arraycopy(this.chPName.getBytes(), 0, buff, 0,
				this.chPName.getBytes().length);
		this.myPacket.put(buff);
		byte[] pwdBuff = new byte[256];
		System.arraycopy(this.chPWord.getBytes(), 0, pwdBuff, 0,
				this.chPWord.getBytes().length);
		this.myPacket.put(pwdBuff);
		this.myPacket.putInt(nConnectStatus);
		return this;
	}

	public String getChGroup() {
		return chGroup;
	}

	public void setChGroup(String chGroup) {
		this.chGroup = chGroup;
	}

	public int getnYSTNO() {
		return nYSTNO;
	}

	public void setnYSTNO(int nYSTNO) {
		this.nYSTNO = nYSTNO;
	}

	public int getnChannel() {
		return nChannel;
	}

	public void setnChannel(int nChannel) {
		this.nChannel = nChannel;
	}

	public String getChPName() {
		return chPName;
	}

	public void setChPName(String chPName) {
		this.chPName = chPName;
	}

	public String getChPWord() {
		return chPWord;
	}

	public void setChPWord(String chPWord) {
		this.chPWord = chPWord;
	}

	public int getnConnectStatus() {
		return nConnectStatus;
	}

	public void setnConnectStatus(int nConnectStatus) {
		this.nConnectStatus = nConnectStatus;
	}

}

class JVAudioPacket extends JVPacket {

	public static final int H264PACKET_SIZE = 76;
	private int index = 0;// 占用4个字节位
	private byte[] audioData;// 占用60个字节
	byte[] buff = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	// int x;
	public JVAudioPacket() {
		super(H264PACKET_SIZE);
	}

	public JVAudioPacket(int packetLen) {
		super(packetLen);
	}

	public JVAudioPacket pack() {
		this.myPacket.position(0);

		this.myPacket.putInt(this.index);

		this.myPacket.put(buff);
		this.myPacket.put(audioData, 0, 60);
		return this;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public byte[] getAudioData() {
		return audioData;
	}

	public void setAudioData(byte[] audioData) {
		this.audioData = audioData;
	}
}