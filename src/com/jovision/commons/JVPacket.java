package com.jovision.commons;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//数据包类型

//基本包类型
public class JVPacket {

	protected ByteBuffer myPacket = null;
	public byte[] data = null; // 数据
	protected int nType = 0;
	protected int nPacketLen = 1024;

	public JVPacket() {
		this.data = new byte[1024];
		this.myPacket = ByteBuffer.wrap(data);
	}

	public JVPacket(int nLen) {
		this.data = new byte[nLen];
		this.myPacket = ByteBuffer.wrap(data);
		this.myPacket.order(ByteOrder.LITTLE_ENDIAN);
		nPacketLen = nLen;
	}

	public JVPacket pack() {
		this.myPacket.putInt(this.nType);
		return this;
	}

	public JVPacket unpack() {
		this.nType = this.myPacket.getInt(0);
		return this;
	}

	public byte[] getPacket() {
		return this.data;
	}

	public ByteBuffer getBuffer() {
		this.myPacket.position(0);
		return this.myPacket;
	}

	public int getType() {
		return nType;
	}

	public void setType(int nType) {
		this.nType = nType;
	}

	public int getLen() {
		return this.nPacketLen;
	}

	public void setLen(int nLen) {
		this.nPacketLen = nLen;
	}

}

// 请求主控IP包
class JVReqSPacket extends JVPacket {
	/*
	 * Pakcet Struct int nType int nCsNumber
	 */
	private int nCSNumber = 0;

	public JVReqSPacket() {
		super(8);
		this.setType(JVNetConst.JVN_REQ_CONNA);
		this.setCSNumber(nCSNumber);
	}

	public JVReqSPacket(int nCsNumber) {
		super(8);
		this.setType(JVNetConst.JVN_REQ_CONNA);
		this.setCSNumber(nCsNumber);
	}

	public long getCSNumber() {
		return nCSNumber;
	}

	public void setCSNumber(int nCSNumber) {
		this.nCSNumber = nCSNumber;
	}

	public JVPacket pack() {
		this.myPacket.position(0);
		this.myPacket.putInt(this.nType);
		this.myPacket.putInt(this.nCSNumber);
		return this;
	}
}

// 服务器返回主控IP包
class JVRspSPacket extends JVPacket {
	/*
	 * Pakcet Struct int nType int nPort byte[20] addr; byte[20] laddr; byte[4]
	 * reserve
	 */
	private int nPort = 0;
	private String sAddr;
	private String lAddr;

	public JVRspSPacket() {
		super(52);
		this.setType(JVNetConst.JVN_RSP_CONNA);
	}

	public void setsAddr(String sAddr) {
		this.sAddr = sAddr;
	}

	public String getsAddr() {
		return sAddr;
	}

	public void setlAddr(String lAddr) {
		this.lAddr = lAddr;
	}

	public String getlAddr() {
		return lAddr;
	}

	public void setPort(int nPort) {
		this.nPort = nPort;
	}

	public int getPort() {
		return nPort;
	}

	public JVPacket unpack() {
		this.myPacket.position(0);
		this.nType = this.myPacket.getInt();
		this.nPort = this.myPacket.getInt();
		byte[] dst = new byte[20];
		for (int i = 0; i < 20; i++) {
			dst[i] = 0;
		}
		this.myPacket.get(dst, 0, 20);
		this.sAddr = new String(dst).trim();
		for (int i = 0; i < 20; i++) {
			dst[i] = 0;
		}
		this.myPacket.get(dst, 0, 20);
		this.lAddr = new String(dst).trim();
		System.out.println(this.lAddr);
		return this;
	}
}

/*
 * char acSendBuf[60]; //存放接受数据的字符数组 int nType; nType = YST_BS_INFO; //通知 分控
 * memcpy(&acSendBuf,&nType,4); memcpy(acSendBuf + 4,&datat.nTCPSerPort,4);
 * memcpy(acSendBuf + 4 + 4,inet_ntoa(((SOCKADDR_IN
 * *)&datat.sockAddUdp2)->sin_addr),20); memcpy(acSendBuf + 4 + 4 +
 * 20,inet_ntoa(((SOCKADDR_IN *)&datat.sockLanUdp2)->sin_addr),20);
 * tcpsendpack(pconninfo->sock,acSendBuf,52,1000);
 */

// 请求转发地址包
class JVReqTSPacket extends JVPacket {
	/*
	 * Pakcet Struct int nType int nCsNumber int nChannel int nCType int nSerial
	 */
	private int nCSNumber = 0x0;
	private int nChannel = 1;
	private int nCType = 3; // client type
	private int nSerial = 0;// random number

	public JVReqTSPacket() {
		super();
	}

	public JVReqTSPacket(int nLen) {
		super(nLen);
	}

	public JVReqTSPacket(int nCsNumber, int nChannel, int nCType, int nSerial,
			int nLen) {
		super(nLen);
		this.setType(JVNetConst.JVN_REQ_S2);
		this.setChannel(nChannel);
		this.setCSNumber(nCsNumber);
		this.setCType(nCType);
		this.setSerial(nSerial);
	}

	public long getCSNumber() {
		return nCSNumber;
	}

	public void setCSNumber(int nCSNumber) {
		this.nCSNumber = nCSNumber;
	}

	public int getChannel() {
		return nChannel;
	}

	public void setChannel(int nChannel) {
		this.nChannel = nChannel;
	}

	public int getCType() {
		return nCType;
	}

	public void setCType(int nCType) {
		this.nCType = nCType;
	}

	public int getSerial() {
		return nSerial;
	}

	public void setSerial(int nSerial) {
		this.nSerial = nSerial;
	}

	public JVPacket pack() {
		this.myPacket.position(0);
		this.myPacket.putInt(this.nType);
		this.myPacket.putInt(this.nCSNumber);
		this.myPacket.putInt(this.nChannel);
		this.myPacket.putInt(this.nCType);
		this.myPacket.putInt(this.nSerial);
		return this;
	}

	public int test() {
		this.myPacket.position(4);
		return this.myPacket.getInt();
	}
}

class JVConnRetPacket extends JVPacket {
	/*
	 * 连接状态数据包 int nType=0x8D or 0x88 int nPort char[20] ip
	 */
	private int nPort = 0;
	private String ip;

	public JVConnRetPacket() {
		super();
	}

	public JVConnRetPacket(int nLen) {
		super(nLen);
	}

	public JVConnRetPacket(int nPort, String ip, int nLen) {
		super(nLen);
		this.setIp(ip);
		this.setPort(nPort);
	}

	public int getPort() {
		return nPort;
	}

	public void setPort(int nPort) {
		this.nPort = nPort;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public JVPacket unpack() {
		this.myPacket.position(0);
		this.nType = this.myPacket.getInt();
		this.nPort = this.myPacket.getInt();
		int i = 8;
		while ((this.data[i] <= '9' && this.data[i] >= '0')
				|| this.data[i] == '.') {
			i++;
		}
		this.myPacket.position(8);
		byte[] buff = new byte[i - 8];
		this.myPacket.get(buff);
		// System.arraycopy(this.myPacket.array(),8, buff, 0, 20-i);
		this.ip = new String(buff).trim();
		// Log.e("ttt", this.ip);
		return this;
	}
}

// 身份验证包
class JVCheckPacket extends JVPacket {
	int type; // ip=1 or cs=0

	private byte cmd = 0;
	private int nCSNumber = 0xA0;
	private int nCType = 3;
	private int nSerial = 0;
	private int nUserLen = 0;
	private int nPassLen = 0;
	private String userName = null;
	private String passwd = null;

	public JVCheckPacket(int type, int nLen) {
		super(nLen);
		this.type = type;
		this.cmd = (byte) (type == 0 ? JVNetConst.JVN_TDATA_CONN
				: JVNetConst.JVN_REQ_CHECKPASS);
		this.nType = (type == 0 ? JVNetConst.JVN_TDATA_CONN
				: JVNetConst.JVN_REQ_CHECKPASS);
	}

	public JVCheckPacket(int t, String user, String pass) {
		super(9 + user.length() + pass.length());
		this.nUserLen = user.length();
		this.nPassLen = pass.length();
		this.type = t;
		this.cmd = (byte) (type == 0 ? JVNetConst.JVN_TDATA_CONN
				: JVNetConst.JVN_REQ_CHECKPASS);
		this.userName = user;
		this.passwd = pass;
		this.nType = (type == 0 ? JVNetConst.JVN_TDATA_CONN
				: JVNetConst.JVN_REQ_CHECKPASS);
	}

	public JVCheckPacket(int csNumber, int nSerial, String user, String pass) {
		super(24 + user.length() + pass.length());
		this.type = 0;
		this.nUserLen = user.length();
		this.nPassLen = pass.length();
		this.userName = user;
		this.passwd = pass;
		this.nType = JVNetConst.JVN_TDATA_CONN;
		this.cmd = (byte) JVNetConst.JVN_TDATA_CONN;
		this.nSerial = nSerial;
		this.nCSNumber = csNumber;
	}

	public int getCSNumber() {
		return nCSNumber;
	}

	public void setCSNumber(int nCSNumber) {
		this.nCSNumber = nCSNumber;
	}

	public int getCType() {
		return nCType;
	}

	public void setCType(int nCType) {
		this.nCType = nCType;
	}

	public int getSerial() {
		return nSerial;
	}

	public void setSerial(int nSerial) {
		this.nSerial = nSerial;
	}

	public int getUserLen() {
		return nUserLen;
	}

	public void setUserLen(int nUserLen) {
		this.nUserLen = nUserLen;
	}

	public int getPassLen() {
		return nPassLen;
	}

	public void setPassLen(int nPassLen) {
		this.nPassLen = nPassLen;
	}

	public void setUserName(String name) {
		this.userName = new String(name);
	}

	public String getUserName() {
		return this.userName;
	}

	public void setPasswd(String pass) {
		this.userName = new String(pass);
	}

	public String getPasswd() {
		return this.passwd;
	}

	public JVPacket pack() {
		this.myPacket.position(0);
		if (this.type == 0) {
			// this.myPacket.put(cmd);
			this.myPacket.putInt(this.nType);
			this.myPacket.putInt(this.nCSNumber);
			this.myPacket.putInt(this.nCType);
			this.myPacket.putInt(this.nSerial);
		} else
			this.myPacket.put(cmd);
		this.myPacket.putInt(this.nUserLen);
		this.myPacket.putInt(this.nPassLen);
		byte[] buff = new byte[JVConst.MAX_PATH];
		System.arraycopy(this.userName.getBytes(), 0, buff, 0, this.nUserLen);
		this.myPacket.put(buff, 0, this.nUserLen);
		System.arraycopy(this.passwd.getBytes(), 0, buff, 0, this.nPassLen);
		this.myPacket.put(buff, 0, this.nPassLen);
		return this;
	}
}

// IP直连发送的 第一个包
class JVIpFirstPacket extends JVPacket {
	private int nChannel = 1;
	private int nCType = 3;

	public JVIpFirstPacket() {
		super();
	}

	public JVIpFirstPacket(int nLen) {
		super(nLen);
	}

	public JVIpFirstPacket(int nChannel, int nLen) {
		super(nLen);
		this.nChannel = nChannel;
	}

	public int getChannel() {
		return nChannel;
	}

	public void setChannel(int nChannel) {
		this.nChannel = nChannel;
	}

	public int getCType() {
		return nCType;
	}

	public void setCType(int nCType) {
		this.nCType = nCType;
	}

	public JVPacket pack() {
		this.myPacket.position(0);
		this.myPacket.putInt(this.nChannel);
		this.myPacket.putInt(this.nCType);
		return this;
	}
}

// 云台控制包
class JVYTCtrPacket extends JVPacket {
	/*
	 * byte cType int len int type
	 */
	byte cType = JVNetConst.JVN_CMD_YTCTRL;
	int nLenData = 4;

	public JVYTCtrPacket() {
		super();
	}

	public JVYTCtrPacket(int nLen) {
		super(nLen);
	}

	public JVYTCtrPacket(int nType, int len) {
		super(len);
		this.nType = nType;
	}

	public byte getcType() {
		return cType;
	}

	public void setcType(byte cType) {
		this.cType = cType;
	}

	public int getnLenData() {
		return nLenData;
	}

	public void setnLenData(int nLenData) {
		this.nLenData = nLenData;
	}

	public JVPacket pack() {
		this.myPacket.position(0);
		// this.myPacket.put(this.cType);
		// this.myPacket.putInt(this.nLenData);
		this.myPacket.putInt(this.nType);
		return this;
	}
}

// 返回状态包
class JVStatusPacket extends JVPacket {
	/*
	 * char nStatus int nLen
	 */
	// nType=len
	private byte nStatus = 0;

	public JVStatusPacket() {
		super(5);
	}

	public JVStatusPacket(int nLen) {
		super(nLen);
	}

	public JVStatusPacket(int nStatus, int len, int nLen) {
		super(nLen);
		this.nStatus = (byte) nStatus;
		this.nType = len;
	}

	public byte getStatus() {
		return nStatus;
	}

	public void setStatus(int nStatus) {
		this.nStatus = (byte) nStatus;
	}

	public int getLen() {
		return this.nType;
	}

	public void setLen(int len) {
		this.nType = len;
	}

	public JVPacket unpack() {
		this.myPacket.position(0);
		this.nStatus = this.myPacket.get();
		this.nType = this.myPacket.getInt();
		return this;
	}

	public JVPacket pack() {
		this.myPacket.position(0);
		this.myPacket.put(this.nStatus);
		this.myPacket.putInt(this.nType);
		return this;
	}

}

// 帧确认包
class JVACKPacket extends JVPacket {
	// nType=len
	/*
	 * byte type=JVNetConst.JVN_DATA_OK; int nLen 长度=4 int nIndex 帧序号
	 */
	private byte type = JVNetConst.JVN_DATA_OK;
	private int nIndex = 0;
	private int nLen = 4;

	public JVACKPacket() {
		super(9);
	}

	public JVACKPacket(int index) {
		super(9);
		this.nIndex = index;
	}

	public JVPacket unpack() {
		this.myPacket.position(0);
		this.type = this.myPacket.get();
		this.nLen = this.myPacket.getInt();
		this.nIndex = this.myPacket.getInt();
		return this;
	}

	public JVPacket pack() {
		this.myPacket.position(0);
		this.myPacket.put(this.type);
		this.myPacket.putInt(this.nLen);
		this.myPacket.putInt(this.nIndex);
		return this;
	}

}

// TCP方式下 H264包结构
class JVH264Packet extends JVPacket {
	/*
	 * byte h264=0x01 标志1 byte reserve =0x01 标志2 int16 width 宽（高位） int16 height
	 * 高（低位） int seq 序号
	 */
	public static final int H264PACKET_SIZE = 10;
	private byte h264 = 0;
	private byte reserve = 0;
	private int width = 0;
	private int height = 0;
	private int seq = 0;

	// int x;
	public JVH264Packet() {
		super(H264PACKET_SIZE);
	}

	public byte getH264() {
		return h264;
	}

	public void setH264(byte h264) {
		this.h264 = h264;
	}

	public byte getReserve() {
		return reserve;
	}

	public void setReserve(byte reserve) {
		this.reserve = reserve;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int w) {
		this.width = w;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int h) {
		this.height = h;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public JVPacket unpack() {
		this.myPacket.position(0);
		this.h264 = this.myPacket.get();
		this.reserve = this.myPacket.get();
		int wh = this.myPacket.getInt();
		// x=wh;
		this.width = ((wh >> 16) & 0xffff);
		this.height = (wh & 0xffff);
		this.seq = this.myPacket.getInt();
		return this;
	}

	public JVPacket pack() {
		this.myPacket.position(0);
		this.myPacket.put(this.h264);
		this.myPacket.put(this.reserve);
		int wh = ((this.width << 16) | this.height);
		this.myPacket.putInt(wh);
		// this.myPacket.putInt(this.h);
		this.myPacket.putInt(this.seq);
		return this;
	}
}

/*
 * class JVH264PacketEx { public ByteBuffer myPacket=null; public static final
 * int H264PACKET_SIZE=10; private byte h264=0; private byte reserve=0; private
 * int width=0; private int height=0; private int seq=0; //int x; public
 * JVH264PacketEx() { myPacket=ByteBuffer.allocateDirect(H264PACKET_SIZE*2); }
 * public byte getH264() { return h264; } public void setH264(byte h264) {
 * this.h264 = h264; } public byte getReserve() { return reserve; } public void
 * setReserve(byte reserve) { this.reserve = reserve; } public int getWidth() {
 * return width; } public void setWidth(int w) { this.width = w; } public int
 * getHeight() { return height; } public void setHeight(int h) { this.height =
 * h; } public int getSeq() { return seq; } public void setSeq(int seq) {
 * this.seq = seq; }
 * 
 * public JVH264PacketEx unpack() { this.myPacket.position(0);
 * this.h264=this.myPacket.get(); this.reserve=this.myPacket.get(); int
 * wh=this.myPacket.getInt(); //x=wh; this.width=((wh>>16)&0xffff);
 * this.height=(wh&0xffff); this.seq=this.myPacket.getInt();
 * this.myPacket.position(0); return this; } public JVH264PacketEx pack() {
 * this.myPacket.position(0); this.myPacket.put(this.h264);
 * this.myPacket.put(this.reserve); int wh=((this.width<<16)|this.height);
 * this.myPacket.putInt(wh); //this.myPacket.putInt(this.h);
 * this.myPacket.putInt(this.seq); this.myPacket.position(0); return this; }
 * public ByteBuffer getPacket() { return this.myPacket; } }
 */
// UDP方式下 H264包结构
class JVH264PacketEx extends JVPacket {
	/*
	 * int nIndex 帧序号 byte h264=0x01 标志1 byte reserve =0x01 标志2 int16 width
	 * 宽（高位） int16 height 高（低位） int seq 序号
	 */
	public static final int H264PACKET_SIZE = 10;
	// private int nIndex=0;
	private byte h264 = 0;
	private byte reserve = 0;
	private int width = 0;
	private int height = 0;
	private int seq = 0;

	// int x;
	public JVH264PacketEx() {
		super(H264PACKET_SIZE);
	}

	public byte getH264() {
		return h264;
	}

	public void setH264(byte h264) {
		this.h264 = h264;
	}

	public byte getReserve() {
		return reserve;
	}

	public void setReserve(byte reserve) {
		this.reserve = reserve;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int w) {
		this.width = w;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int h) {
		this.height = h;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public JVPacket unpack() {
		this.myPacket.position(0);
		// this.nIndex=this.myPacket.getInt();
		this.h264 = this.myPacket.get();
		this.reserve = this.myPacket.get();
		int wh = this.myPacket.getInt();
		// x=wh;
		this.width = ((wh >> 16) & 0xffff);
		this.height = (wh & 0xffff);
		this.seq = this.myPacket.getInt();
		return this;
	}

	public JVPacket pack() {
		this.myPacket.position(0);
		// this.myPacket.putInt(nIndex);
		this.myPacket.put(this.h264);
		this.myPacket.put(this.reserve);
		int wh = ((this.width << 16) | this.height);
		this.myPacket.putInt(wh);
		// this.myPacket.putInt(this.h);
		this.myPacket.putInt(this.seq);
		return this;
	}
	// public void setIndex(int nIndex) {
	// this.nIndex = nIndex;
	// }
	// public int getIndex() {
	// return nIndex;
	// }
}

// UDP方式下非标准 H264包结构
class JVH264_USDPc extends JVPacket {

	public static final int H264PACKET_SIZE = 8;
	private int startCode = 0;
	private int uchType = 0;
	private int nSize = 0;

	// int x;
	public JVH264_USDPc() {
		super(H264PACKET_SIZE);
	}

	public JVH264_USDPc unpack() {
		this.myPacket.position(0);
		this.startCode = this.myPacket.getInt();
		int wh = this.myPacket.getInt();
		this.uchType = (wh & 0xf);
		this.nSize = (wh >> 4 & 0xfffff);
		// Log.e("tags",
		// "FrameType="+uchType+",Size="+nSize+",startCode="+startCode);
		return this;
	}

	int getUchType() {
		return uchType;
	}

	void setUchType(int uchType) {
		this.uchType = uchType;
	}

	int getnSize() {
		return nSize;
	}

	void setnSize(int nSize) {
		this.nSize = nSize;
	}

	int getStartCode() {
		return startCode;
	}

	void setStartCode(int startCode) {
		this.startCode = startCode;
	}

}

// UDP方式下o帧 H264包结构
class JVH264_OPC extends JVPacket {
	/*
	 * decodeStartCode 解码器类型 int16 width 宽（高位） int16 height 高（低位）
	 */
	public static final int H264PACKET_SIZE = 12;
	private int decodeStartCode = 0;
	private int width = 0;
	private int height = 0;

	// int x;
	public JVH264_OPC() {
		super(H264PACKET_SIZE);
	}

	public JVH264_OPC unpack() {
		this.myPacket.position(0);
		this.decodeStartCode = this.myPacket.getInt();
		this.width = this.myPacket.getInt();
		this.height = this.myPacket.getInt();
		// Log.e("tags", "FrameType="+uchType+",trueSize="+nSize);
		return this;
	}

	int getDecodeStartCode() {
		return decodeStartCode;
	}

	void setDecodeStartCode(int decodeStartCode) {
		this.decodeStartCode = decodeStartCode;
	}

	int getWidth() {
		return width;
	}

	void setWidth(int width) {
		this.width = width;
	}

	int getHeight() {
		return height;
	}

	void setHeight(int height) {
		this.height = height;
	}

}

// UDP方式下手机分控 H264包结构
class JVH264_PC extends JVPacket {

	public static final int H264PACKET_SIZE = 8;
	private int decodeStartCode = 0;// 占用四个字节
	private int uchType = 0;// 占用一个字节中的4个位
	private int nSize = 0;// 占用一个字节中的20个位
	private int nTickCount;// 占用一个字节中的8个位

	// int x;
	public JVH264_PC() {
		super(H264PACKET_SIZE);
	}

	public JVH264_PC unpack() {
		this.myPacket.position(0);
		this.decodeStartCode = this.myPacket.getInt();
		// int wh = this.myPacket.getInt();
		// this.uchType=(wh&0xf);
		// this.nSize=(wh>>4&0xfffff);
		// this.nTickCount=(wh>>24&0xff);
		// Log.e("tags",
		// "FrameType="+uchType+",trueSize="+nSize+",count="+nTickCount);
		return this;
	}

	int getDecodeStartCode() {
		return decodeStartCode;
	}

	void setDecodeStartCode(int decodeStartCode) {
		this.decodeStartCode = decodeStartCode;
	}

	int getUchType() {
		return uchType;
	}

	void setUchType(int uchType) {
		this.uchType = uchType;
	}

	int getnSize() {
		return nSize;
	}

	void setnSize(int nSize) {
		this.nSize = nSize;
	}

	int getnTickCount() {
		return nTickCount;
	}

	void setnTickCount(int nTickCount) {
		this.nTickCount = nTickCount;
	}

}

// 小助手包结构
// typedef struct STBASEYSTNO
// {
// char chGroup[4];
// int nYSTNO;
// int nChannel;
// char chPName[MAX_PATH];
// char chPWord[MAX_PATH];
// int nConnectStatus;//连接状态 设置时=0，查询时表示状态 0 未连接 1 内网 2 转发 3外网
// }STBASEYSTNO;//云视通号码基本信息，用于初始化小助手的虚连接
// class JVLittleTipsPacket extends JVPacket {
//
// public static final int H264PACKET_SIZE = 8;
// private String chGroup;// 占用四个字节
// private int nYSTNO = 0;// 占用4个字节位
// private int nChannel = 0;// 占用4个字节位
// private String chPName;// 占用一个字节中的8个位
// private String chPWord;
// private int nConnectStatus;
//
// // int x;
// public JVLittleTipsPacket() {
// super(H264PACKET_SIZE);
// }
//
// public JVLittleTipsPacket(int packetLen) {
// super(packetLen);
// }
//
// public JVLittleTipsPacket pack() {
// this.myPacket.position(0);
// byte group[] = new byte[4];
// System.arraycopy(chGroup.getBytes(), 0, group, 0,
// chGroup.getBytes().length);
// this.myPacket.put(group);
//
// this.myPacket.putInt(this.nYSTNO);
// this.myPacket.putInt(this.nChannel);
// byte[] buff = new byte[256];
// System.arraycopy(this.chPName.getBytes(), 0, buff, 0,
// this.chPName.getBytes().length);
// this.myPacket.put(buff);
// byte[] pwdBuff = new byte[256];
// System.arraycopy(this.chPWord.getBytes(), 0, pwdBuff, 0,
// this.chPWord.getBytes().length);
// this.myPacket.put(pwdBuff);
// this.myPacket.putInt(nConnectStatus);
// return this;
// }
//
// public String getChGroup() {
// return chGroup;
// }
//
// public void setChGroup(String chGroup) {
// this.chGroup = chGroup;
// }
//
// public int getnYSTNO() {
// return nYSTNO;
// }
//
// public void setnYSTNO(int nYSTNO) {
// this.nYSTNO = nYSTNO;
// }
//
// public int getnChannel() {
// return nChannel;
// }
//
// public void setnChannel(int nChannel) {
// this.nChannel = nChannel;
// }
//
// public String getChPName() {
// return chPName;
// }
//
// public void setChPName(String chPName) {
// this.chPName = chPName;
// }
//
// public String getChPWord() {
// return chPWord;
// }
//
// public void setChPWord(String chPWord) {
// this.chPWord = chPWord;
// }
//
// public int getnConnectStatus() {
// return nConnectStatus;
// }
//
// public void setnConnectStatus(int nConnectStatus) {
// this.nConnectStatus = nConnectStatus;
// }
//
// }
//
// class JVAudioPacket extends JVPacket {
//
// public static final int H264PACKET_SIZE = 76;
// private int index = 0;// 占用4个字节位
// private byte[] audioData;//占用60个字节
// byte[] buff = {0,0,0,0,0,0,0,0,0,0,0,0};
// // int x;
// public JVAudioPacket() {
// super(H264PACKET_SIZE);
// }
//
// public JVAudioPacket(int packetLen) {
// super(packetLen);
// }
//
// public JVAudioPacket pack() {
// this.myPacket.position(0);
//
//
// this.myPacket.putInt(this.index);
//
//
//
// this.myPacket.put(buff);
// this.myPacket.put(audioData, 0, 60);
// return this;
// }
//
// public int getIndex() {
// return index;
// }
//
// public void setIndex(int index) {
// this.index = index;
// }
//
//
// public byte[] getAudioData() {
// return audioData;
// }
//
// public void setAudioData(byte[] audioData) {
// this.audioData = audioData;
// }
//
//
//
//
// }
