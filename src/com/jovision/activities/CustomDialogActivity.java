package com.jovision.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.ActionReceiver;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVConst;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.CacheUtil;
public class CustomDialogActivity extends BaseActivity implements
		android.view.View.OnClickListener, ActionReceiver.EventHandler {
	/** 查看按钮 **/
	private Button lookVideoBtn;
	/** 报警图片 **/
	private ImageView alarmImage;
	/** 报警时间 **/
	private TextView alarmTime;

	private String vod_uri_ = "";
	private String strImgUrl = "";

	private int msg_tag;
	
//	private String imgLoaderFilePath = "";
	private String localImgName="", localImgPath = "";
	private String localVodName="", localVodPath = "";
	private boolean bLocalFile = false;
	private boolean bConnectFlag = false;
	private int bDownLoadFileType = 0; //0图片 1视频
	private String strYstNum = "";
	private ProgressDialog progressdialog;
	private PlayWindowManager manager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_alarm_dialog);
		InitViews();
		Bundle extras = getIntent().getExtras();
		bDownLoadFileType = 0;//默认先下载图片
		msg_tag = extras.getInt("MSG_TAG");
		if(msg_tag == 0 || msg_tag == JVAccountConst.MESSAGE_PUSH_TAG){//旧的报警
			String strTime = extras.getString("ALR_TIME");
			alarmTime.setText(strTime);
			strImgUrl = extras.getString("IMG_URL");
			if (strImgUrl.equals("")) {
				strImgUrl = "http://no picture";// 为空的话，会显示空白
			}
			vod_uri_ = extras.getString("VOD_URL");
			if (vod_uri_.equals("") || vod_uri_ == null) {
				lookVideoBtn.setEnabled(false);
				lookVideoBtn.setText(getResources().getString(
						R.string.str_alarm_no_video));
			}
		} else if (msg_tag == JVAccountConst.MESSAGE_NEW_PUSH_TAG) {// 新报警
			int index = extras.getInt("POS");
			PushInfo pushInfo = BaseApp.pushList.get(index);
			strYstNum = pushInfo.ystNum;
			String strAlarmTime = new String(pushInfo.alarmTime);
			strImgUrl = new String(pushInfo.pic);
			vod_uri_ =new String(pushInfo.video);
			alarmTime.setText(strAlarmTime);
			if (vod_uri_.equals("") || vod_uri_ == null) {
				lookVideoBtn.setEnabled(false);
				lookVideoBtn.setText(getResources().getString(
						R.string.str_alarm_no_video));
			}			
//			strImgUrl = "./rec/00/20141017/A01185730.jpg";
//			vod_uri_ = "./rec/00/20141017/A01183434.mp4";
			
			if(!vod_uri_.equals("")){
				String temp[] = vod_uri_.split("/");
				localVodName = temp[temp.length-1];
				localVodPath = Consts.SD_CARD_PATH + "CSAlarmIMG/"+localVodName;

			}
			
			if(!strImgUrl.equals("")){
				String temp[] = strImgUrl.split("/");
				localImgName = temp[temp.length-1];
				localImgPath = Consts.SD_CARD_PATH + "CSAlarmVOD/"+localImgName;
//				imgLoaderFilePath = "file://"+localImgPath;				
			}	
				
			MyLog.i("New Alarm", "img_url:"+strImgUrl+", vod_url:"+vod_uri_);	
			MyLog.i("New Alarm", "localVodPath:"+localVodPath);	
			MyLog.e("New Alarm", "localImgPath:"+localImgPath);
			
			if(!fileIsExists(localImgPath)){
				bLocalFile = false;
				if(!strImgUrl.equals("")){
					JVSUDT.JVC_SetDownLoadFileUrl(localImgPath);
					AlarmConnect(strYstNum);					
				}
				if(!vod_uri_.equals("")){
					lookVideoBtn.setEnabled(true);
				}				
			}
			else{
				if(!vod_uri_.equals("")){
					lookVideoBtn.setEnabled(true);
				}
				
				Bitmap bmp= getLoacalBitmap(localImgPath);
				if(null != bmp){
					alarmImage.setImageBitmap(bmp);
				}
				bLocalFile = true;
				showToast("文件已存在", Toast.LENGTH_SHORT);
			}
		}

	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ActionReceiver.ehList.add(this);
		MyLog.e("CloudSEE[alarm]", "CustomDialogActivity onResume() Invoked");
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		ActionReceiver.ehList.remove(this);
		super.onPause();
		MyLog.e("CloudSEE[alarm]", "CustomDialogActivity onPause() Invoked");
	}	
	@Override
	public void onDestroy() {
		if(!bLocalFile && bConnectFlag){
			JVSUDT.JVC_DisConnect(JVConst.ONLY_CONNECT);//断开连接
		}

		super.onDestroy();
	}
	private void InitViews(){

		lookVideoBtn = (Button) findViewById(R.id.alarm_lookup_video_btn);
		lookVideoBtn.setOnClickListener(this);
		lookVideoBtn.setText(getResources().getString(
				R.string.str_alarm_check_video));
		lookVideoBtn.setEnabled(false);
		alarmImage = (ImageView) findViewById(R.id.alarm_img);
		alarmTime = (TextView) findViewById(R.id.alarm_datetime_tv);
		progressdialog = new ProgressDialog(CustomDialogActivity.this);
		progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressdialog.setMessage(getResources().getString(R.string.str_downloading_vod));
		progressdialog.setIndeterminate(false);
		progressdialog.setCancelable(false);
	}
	/**
	* 加载本地图片
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url) {
	     try {
	          FileInputStream fis = new FileInputStream(url);
	          return BitmapFactory.decodeStream(fis);
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}	
	public boolean fileIsExists(String strFilePath){
        try{
                File f=new File(strFilePath);
                if(!f.exists()){
                    return false;
                }
                
        }catch (Exception e) {
                // TODO: handle exception
            return false;
        }
        return true;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.alarm_lookup_video_btn:// 查看录像
			if (null != vod_uri_ && !"".equalsIgnoreCase(vod_uri_)) {
				//先判断本地是否存在
				if(fileIsExists(localVodPath)){
					//启动播放界面
					Intent intent = new Intent();
					intent.setClass(CustomDialogActivity.this, JVVideoActivity.class);
					intent.putExtra("URL", localVodPath);
					intent.putExtra("IS_LOCAL", true);		
					startActivity(intent);
				}
				else
				{
					bDownLoadFileType = 1;//下载录像
					JVSUDT.JVC_SetDownLoadFileUrl(localVodPath);
					progressdialog.show();
					if(!bConnectFlag)//如果没连接，先连接
					{
						AlarmConnect(strYstNum);
						//在连接成功后发送下载命令
					}
					else{
						//如果已经连接上，发送下载命令
						
						Jni.sendBytes(0,
								 (byte) JVNetConst.JVN_CMD_DOWNLOADSTOP, new byte[0], 0);
						byte[]dataByte = vod_uri_.getBytes();
						MyLog.e("new alarm", "vedio_url: "+vod_uri_+"nsize: "+dataByte.length);
						Jni.sendBytes(0,
								 (byte) JVNetConst.JVN_REQ_DOWNLOAD, dataByte, dataByte.length);	
					}					
				}

			}			
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void AlarmConnect(String strYstNum) {
//		Device devs = null;
//		boolean bfind = false;
//		for (int j = 0; j < CacheUtil.getDevList().size(); j++) {
//			devs = CacheUtil.getDevList().get(j);
//			MyLog.e("AlarmConnect", "dst:" + strYstNum + "---yst-num = "
//					+ devs.getFullNo());
//			if (strYstNum.equalsIgnoreCase(devs.devs.getFullNo())) {
//				bfind = true;
//				break;
//			}
//		}
//		if (bfind) {
//			JVConnectInfo info = new JVConnectInfo();
//			info.setAction(false);
//			// info.setBackLight(false);
//			info.setByUDP(true);
//			info.setCsNumber(devs.yst);
//			// info.setDeleNum("-1,");
//			info.setChannel(1);
//			// info.setHaveSearchChannel(1);
//			info.setRemoteIp(devs.deviceLocalIp);
//			info.setPort(devs.deviceLocalPort);
//			// info.setSavePasswd(true);
//			// info.setChannelCount(-1);
//			if(devs.deviceLocalIp.equals("") && !JVConst.DEFAULT_IP.equals(devs.deviceLocalIp)){
//				info.setConnType(JVConst.JV_CONNECT_CS);
//			}
//			else{
//				info.setConnType(JVConst.JV_CONNECT_IP);
//				info.setRemoteIp(devs.deviceLocalIp);
//				info.setPort(devs.deviceLocalPort);
//			}
//			// info.setChannelRealCount(-1);
//			info.setGroup(devs.group);
//			info.setLocalTry(true);
//			info.setSrcName("1");
//			info.setUserName(devs.deviceLoginUser);
//			info.setPasswd(devs.deviceLoginPwd);
//			info.isRequestVedio = false;
//			
//			
//			if (info.getConnType() == JVConst.JV_CONNECT_CS) {// 通过号码连接
//				Log.v("tags............云视通号连接", "csNumber=" + info.getCsNumber()
//						+ ",channel=" + info.getChannel());
//				synchronized (JVSUDT.disconnect) {
//					JVSUDT.JVC_Connect(JVConst.ONLY_CONNECT, info.getChannel(), "", info.getPort(),
//							info.getUserName(), info.getPasswd(),
//							info.getCsNumber(), info.getGroup().toUpperCase(),
//							info.isLocalTry(), JVConst.JVN_TRYTURN, true,
//							JVNetConst.TYPE_3GMOHOME_UDP, null, info.isRequestVedio);
//					Log.v("clientSize", "connect----------------");
//				}
//			}
//			else{
//				Log.v("tags ......IP", ",windowIndex:" + BaseApp.windowIndex
//						+ ",info.getChannel():" + info.getChannel()
//						+ ",info.getRemoteIp():" + info.getRemoteIp()
//						+ ",info.getPort():" + info.getPort()
//						+ ",info.getUserName():" + info.getUserName()
//						+ ",info.getPasswd():" + info.getPasswd()
//						+ ",info.getGroup():" + info.getGroup()
//						+ ",info.isLocalTry():" + info.isLocalTry()
//						+ ",JVConst.JVN_TRYTURN:" + JVConst.JVN_TRYTURN);				
//				synchronized (JVSUDT.disconnect) {
//					JVSUDT.JVC_Connect(JVConst.ONLY_CONNECT, info.getChannel(), info
//							.getRemoteIp(), info.getPort(), info
//							.getUserName(), info.getPasswd(), -1, info
//							.getGroup().toUpperCase(), info.isLocalTry(),
//							JVConst.JVN_TRYTURN, true,
//							JVNetConst.TYPE_3GMOHOME_UDP, null, info.isRequestVedio);
//					}			
//			}
//			
//		}
//		else{
//			Log.e("AlarmConnect", "not find dst:"+strYstNum);
//		}
	}

	@Override
	public void onHandlerNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
//		switch (what) {
//		case 9001: //一级：远程下载
//		{
//			switch (arg1) { //二级: 结果代码
//			case JVNetConst.JVN_RSP_DOWNLOADOVER://文件下载完毕
//				showToast("文件下载完毕", Toast.LENGTH_SHORT);
//				//JVSUDT.JVC_DisConnect(JVConst.ONLY_CONNECT);//断开连接,如果视频走远程回放，这个连接需要现在断开，因为通道不同
//	
//				if(bDownLoadFileType == 0){
//					//下载图片
//					if (!vod_uri_.equals("")){
//						lookVideoBtn.setEnabled(true);	
//					}
//					
//					Bitmap bmp= getLoacalBitmap(localImgPath);
//					if(null != bmp){
//						alarmImage.setImageBitmap(bmp);
//					}
//				}
//				else if(bDownLoadFileType == 1){
//					//下载录像完毕
//					if(progressdialog.isShowing()){
//						progressdialog.dismiss();
//					}
//					//启动播放界面
//					Intent intent = new Intent();
//					intent.setClass(CustomDialogActivity.this, JVVideoActivity.class);
//					intent.putExtra("URL", localVodPath);
//					intent.putExtra("IS_LOCAL", true);		
//					startActivity(intent);
//				}
//				else{
//					
//				}
//				if(progressdialog.isShowing()){
//					progressdialog.dismiss();
//				}
//				break;
//			case JVNetConst.JVN_CMD_DOWNLOADSTOP://停止文件下载
//				showToast("停止文件下载", Toast.LENGTH_SHORT);
//				break;
//			case JVNetConst.JVN_RSP_DOWNLOADE://文件下载失败
//				showToast("文件下载失败", Toast.LENGTH_SHORT);
//				break;
//			case JVNetConst.JVN_RSP_DLTIMEOUT://文件下载超时
//				showToast("文件下载超时", Toast.LENGTH_SHORT);
//				break;	
//			default:
//				break;
//			}
//		}	
//		break;
//
//		default:
//			break;
//		}
	}

	@Override
	public void onHandleConnectRes(int ret, Object obj) {
		// TODO Auto-generated method stub
//		switch (ret) {
//		case 1://连接成功
//		case 3://已经连上，如正在播放视频界面
//			Log.e("New alarm", "连接成功");
//			bConnectFlag = true;
//			showToast("连接成功", Toast.LENGTH_SHORT);
//			String strFilePath = ""; 
//			if(bDownLoadFileType == 0){
//				strFilePath = strImgUrl;
//			}else if(bDownLoadFileType == 1){
//				strFilePath = vod_uri_;
//			}
//			else{
//				
//			}
//			Log.e("New Alarm", "DownFile Path:"+strFilePath);
//			 JVSUDT.JVC_SendData(5007,
//			 (byte) JVNetConst.JVN_CMD_DOWNLOADSTOP, new byte[0], 0);
//			 byte[]dataByte = strFilePath.getBytes();
//			 JVSUDT.JVC_SendDataStr(5007,
//			 (byte) JVNetConst.JVN_REQ_DOWNLOAD, strFilePath);			
//			break;
//		case 2://断开连接成功
//			bConnectFlag = false;
//			break;
//		case 4://连接失败
//			Log.e("New alarm", "连接失败");
//			bConnectFlag = false;
//			showToast("连接失败", Toast.LENGTH_SHORT);
//			break;
//		default:
//			showToast("连接:"+ret, Toast.LENGTH_SHORT);
//			break;
//		}
	}
	
	private void showToast(String text, int duration){
		Toast.makeText(this, "[DEBUG] "+text, duration).show();
	}
	
	/** 
     * 复制单个文件 
     * @param srcPath String 原文件路径 如：c:/fqf.txt 
     * @param dstPath String 复制后路径 如：f:/fqf.txt 
     * @return void 
     */ 
   public void copyFile(String srcPath, String dstPath) { 
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           File srcfile = new File(srcPath); 
           if (!srcfile.exists()) {
        	   MyLog.e("New Alarm", "文件不存在");
        	   return;
           }
           if (!srcfile.isFile()) {
        	   MyLog.e("New Alarm", "不是一个文件");
        	   return;
           }
           if (!srcfile.canRead()) {
        	   MyLog.e("New Alarm", "文件不能读");
        	   return;
           }
  
           InputStream inStream = new FileInputStream(srcPath); //读入原文件 
           FileOutputStream fs = new FileOutputStream(dstPath); 
           int file_size = inStream.available();
           MyLog.e("New Alarm", "file size:"+file_size);
           byte[] buffer = new byte[file_size]; 

           while ( (byteread = inStream.read(buffer)) != -1) { 
               bytesum += byteread; //字节数 文件大小 
               System.out.println(bytesum); 
               fs.write(buffer, 0, byteread); 
           } 
           inStream.close(); 
       } 
       catch (Exception e) { 
           System.out.println("复制单个文件操作出错"); 
           e.printStackTrace(); 

       } 

   }
	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		MyLog.v(TAG, "onHandler--what=" + what + ";arg1=" + arg1 + ";arg2="
				+ arg2);		
		switch (what) {
		// 连接结果
		case Consts.CALL_CONNECT_CHANGE:
			Channel channel = manager.getChannel(arg2);
			if (null == channel) {
				MyLog.e("CustomDialogActivity onHandler", "the channel "+arg2+" is null");
				return;
			}
			switch (arg1) {
				
				case JVNetConst.NO_RECONNECT:// 1 -- 连接成功//3 不必重新连接
				case JVNetConst.CONNECT_OK: {// 1 -- 连接成功
					channel.setConnecting(false);
					channel.setConnected(true);
					
					MyLog.e("New alarm", "连接成功");
					bConnectFlag = true;
					showToast("连接成功", Toast.LENGTH_SHORT);
					String strFilePath = ""; 
					if(bDownLoadFileType == 0){
						strFilePath = strImgUrl;
					}else if(bDownLoadFileType == 1){
						strFilePath = vod_uri_;
					}
					else{
						
					}
					MyLog.e("New Alarm", "DownFile Path:"+strFilePath);
					Jni.sendBytes(0,
					(byte) JVNetConst.JVN_CMD_DOWNLOADSTOP, new byte[0], 0);
					byte[]dataByte = strFilePath.getBytes();
					Jni.sendBytes(0,
					(byte) JVNetConst.JVN_REQ_DOWNLOAD, dataByte, dataByte.length);							
				}
				break;
				// 2 -- 断开连接成功
				case JVNetConst.DISCONNECT_OK: {
					channel.setConnecting(false);
					channel.setConnected(false);
					bConnectFlag = false;
				}
				break;
				// 4 -- 连接失败
				case JVNetConst.CONNECT_FAILED: {
					bConnectFlag = false;
					try {
						JSONObject connectObj = new JSONObject(obj.toString());
						String errorMsg = connectObj.getString("msg");
						if ("password is wrong!".equalsIgnoreCase(errorMsg)
								|| "pass word is wrong!".equalsIgnoreCase(errorMsg)) {// 密码错误时提示身份验证失败
							showTextToast(R.string.connfailed_auth);
						} else if ("channel is not open!"
								.equalsIgnoreCase(errorMsg)) {// 无该通道服务
							showTextToast(R.string.connfailed_channel_notopen);
						} else if ("connect type invalid!"
								.equalsIgnoreCase(errorMsg)) {// 连接类型无效
							showTextToast(R.string.connfailed_type_invalid);
						} else if ("client count limit!".equalsIgnoreCase(errorMsg)) {// 超过主控最大连接限制
							showTextToast(R.string.connfailed_maxcount);
						} else if ("connect timeout!".equalsIgnoreCase(errorMsg)) {//
							showTextToast(R.string.connfailed_timeout);
						} else {// "Connect failed!"
							showTextToast(R.string.connect_failed);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					channel.setConnecting(false);
					channel.setConnected(false);
				}	
				break;
			};
			
			case JVNetConst.JVN_REQ_DOWNLOAD:
			{
				switch (arg1) { //二级: 结果代码
				case JVNetConst.JVN_RSP_DOWNLOADOVER://文件下载完毕
					showToast("文件下载完毕", Toast.LENGTH_SHORT);
					//JVSUDT.JVC_DisConnect(JVConst.ONLY_CONNECT);//断开连接,如果视频走远程回放，这个连接需要现在断开，因为通道不同
		
					if(bDownLoadFileType == 0){
						//下载图片
						if (!vod_uri_.equals("")){
							lookVideoBtn.setEnabled(true);	
						}
						
						Bitmap bmp= getLoacalBitmap(localImgPath);
						if(null != bmp){
							alarmImage.setImageBitmap(bmp);
						}
					}
					else if(bDownLoadFileType == 1){
						//下载录像完毕
						if(progressdialog.isShowing()){
							progressdialog.dismiss();
						}
						//启动播放界面
						Intent intent = new Intent();
						intent.setClass(CustomDialogActivity.this, JVVideoActivity.class);
						intent.putExtra("URL", localVodPath);
						intent.putExtra("IS_LOCAL", true);		
						startActivity(intent);
					}
					else{
						
					}
					if(progressdialog.isShowing()){
						progressdialog.dismiss();
					}
					break;
				case JVNetConst.JVN_CMD_DOWNLOADSTOP://停止文件下载
					showTextToast("停止文件下载");
					break;
				case JVNetConst.JVN_RSP_DOWNLOADE://文件下载失败
					showTextToast("文件下载失败");
					break;
				case JVNetConst.JVN_RSP_DLTIMEOUT://文件下载超时
					showTextToast("文件下载超时");
					break;	
				default:
					break;
				}
			}
			break;
		}
	}
	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}
	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void initUi() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub
		
	} 

}
