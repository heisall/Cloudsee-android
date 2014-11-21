package com.jovision.activities;

import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Jni;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.commons.JVNetConst;

public class JVRemotePlay extends BaseActivity {
	private SurfaceView remoteSufaceView;
	private Button btnRemotePaly;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		Log.e("tags", arg1 + "," + arg2 + ",");
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub
		setContentView(R.layout.remote_play);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 屏幕常亮
		remoteSufaceView = (SurfaceView) findViewById(R.id.remotesurfaceview);
		btnRemotePaly = (Button) findViewById(R.id.btnRemotePaly);
		btnRemotePaly.setOnClickListener(myOnClickListener);
		handler.postDelayed(new ConnectThread(), 200);

	}

	/**
	 * 连接线程
	 */
	class ConnectThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Channel channel = new Channel();
			Device device = new Device();
			device.setGid("S");
			device.setNo(137722831);
			device.setUser("admin");
			device.setPwd("1");
			device.setFullNo("S137722831");
			channel.setParent(device);
			channel.setChannel(1);
			channel.setIndex(1);
			channel.setSurfaceView(remoteSufaceView);

			Jni.connect(1, 1, device.getIp(), device.getPort(),
					device.getUser(), device.getPwd(), device.getNo(),
					device.getGid(), true, 1, true, JVNetConst.TYPE_3GMO_UDP,
					channel.getSurfaceView().getHolder().getSurface(), true);
		}

	}

	/**
	 * 所有按钮事件
	 */
	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btnRemotePaly:// 返回
				// channel = 5007, uchType = 20, pbuffer =
				// ./rec/00/20141020/A01130921.mp4, nsize: 31
				Jni.enablePlayback(1, true);
				Jni.sendBytes(1, (byte) JVNetConst.JVN_CMD_PLAYSTOP,
						new byte[0], 0);
				String mp4Url = "./rec/00/20141020/A01100427.mp4";
				byte[] data = (byte[]) mp4Url.getBytes();
				Jni.sendBytes(1, (byte) JVNetConst.JVN_REQ_PLAY, data,
						data.length);

				break;

			}

		}

	};

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

}
