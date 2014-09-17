package com.jovision.activities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.bean.ConnPoint;
import com.jovision.bean.Device;
import com.jovision.bean.NewProduct;
import com.jovision.commons.AsyncImageLoader;
import com.jovision.commons.AsyncImageLoader.ImageCallback;
import com.jovision.commons.BaseApp;
import com.jovision.commons.JVConst;
import com.jovision.commons.JVLittleTipsPacket;
import com.jovision.commons.MyLog;
import com.jovision.commons.Url;
import com.jovision.utils.ImageUtil;
import com.jovision.utils.LoginUtil;
import com.jovision.views.ScaleImageView;

//import android.test.JVSUDT;

@SuppressLint("InlinedApi")
public class JVNPDetailActivity extends BaseActivity {

	private GestureDetector mGestureDetector;

	private float oldDis = 0;

	private int NONE = 0;
	private int DRAG = 1;
	private int ZOOM = 2;
	private int mode = NONE;
	private Bitmap bigBitmap;

	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			newImage.postTranslateCenter(-distanceX, -distanceY);
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {

			return true;
		}
	}

	private Button back;// 左侧返回按钮
	private TextView currentMenu;// 当前页面名称
	private Button startPlay;// 观看效果

	private int p_oid = 0;// id
	private int p_size = 0;// 图片size
	private int p_lan = 0;// 语言

	private ArrayList<NewProduct> npPicList = new ArrayList<NewProduct>();
	private NewProduct npObj = null;
	private ProgressDialog dialog = null;
	private ScaleImageView newImage = null;

	private byte allByte[] = new byte[528];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.newpro_layout);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		Intent intent = getIntent();
		if (null != intent) {
			p_lan = intent.getIntExtra("NEWLAN", 0);
			p_size = intent.getIntExtra("NEWSIZE", 0);
			p_oid = intent.getIntExtra("NEWPOID", 0);

		}
		if (null == dialog) {
			dialog = new ProgressDialog(JVNPDetailActivity.this);
			dialog.setMessage(getResources().getString(R.string.str_deleting));
		}
		dialog.setCancelable(false);
		dialog.show();

		initViews();
		GetDetailThread gdThread = new GetDetailThread(JVNPDetailActivity.this);
		gdThread.start();
	}

	public void initViews() {
		BaseApp.npHandler = new NewProHandler(JVNPDetailActivity.this);
		back = (Button) findViewById(R.id.back);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_newpro_show);
		// npViewFlow = (ViewFlow) findViewById(R.id.npviewflow);
		// newPicLayout = (FrameLayout) findViewById(R.id.newpiclayout);
		// imageAdapter = new NPImageAdapter(JVNPDetailActivity.this);

		// NEW_PIC_HEIGHT = screenWidth/5*2;
		//
		// RelativeLayout.LayoutParams reParams = new
		// RelativeLayout.LayoutParams(screenWidth, NEW_PIC_HEIGHT);
		// newPicLayout.setLayoutParams(reParams);
		// newProText = (TextView) findViewById(R.id.newprotext);
		startPlay = (Button) findViewById(R.id.startplay);// 观看效果
		// imageLayout = (LinearLayout) findViewById(R.id.imagelayout);
		startPlay.setVisibility(View.GONE);

		newImage = (ScaleImageView) findViewById(R.id.newimage);

		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		newImage.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mGestureDetector.onTouchEvent(event);
				float newDis = 0;
				// int mode = NONE;
				int pointCount = event.getPointerCount();
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN: {
					mode = DRAG;
					// mGestureDetector.onTouchEvent(event);
					break;
				}
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN: {
					oldDis = spacing(event);
					if (pointCount == 2)
						mode = ZOOM;
					break;
				}
				case MotionEvent.ACTION_MOVE: {
					if (mode == DRAG) {
						mGestureDetector.onTouchEvent(event);
					} else if (mode == ZOOM) {
						newDis = spacing(event);
						if (oldDis != 0 && newDis != 0 && newDis > 10f
								&& Math.abs(newDis - oldDis) > 5) {
							// float num = newDis / oldDis;
							// oldDis = newDis;
							// rescaleImage(num);
							if (newDis > oldDis)
								rescaleImage(1);
							else if (newDis < oldDis)
								rescaleImage(-1);
						}
					}
					break;
				}
				}
				return true;
			}
		});

		back.setOnClickListener(onClickListener);
		startPlay.setOnClickListener(onClickListener);
	}

	// onclick事件
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				// JVMainActivity.openNewPro = false;
				JVNPDetailActivity.this.finish();
				break;
			case R.id.startplay:
				System.gc();
				Device device = new Device();
				device.deviceLocalIp = "127.0.0.1";
				device.deviceLocalPort = 9101;
				device.deviceLoginUser = npObj.UserName;// temObj.getString("LoginUser");
				device.deviceLoginPwd = npObj.UserPwd;// temObj.getString("LoginPwd");
				device.deviceOwner = -1000;// temObj.getInt("Owner");
				device.deviceName = npObj.CloudSeeNo;//
				device.deviceNum = npObj.CloudSeeNo;
				device.deviceOID = -1000;// temObj.getInt("OID");
				device.onlineState = 1;// 设置为在线，否则没法看

				ArrayList<ConnPoint> connPointList = new ArrayList<ConnPoint>();
				if (npObj.ChannelNum <= 0) {
					break;
				}

				for (int i = 0; i < npObj.ChannelNum; i++) {
					ConnPoint connPoint = new ConnPoint();
					connPoint.deviceID = device.deviceOID;// temObj.getInt("DeviceID");
					connPoint.pointNum = i + 1;// temObj.getInt("PointNum");
					connPoint.pointOwner = device.deviceOID;// temObj.getInt("Owner");
					connPoint.pointName = npObj.CloudSeeNo + "_" + (i + 1);// temObj.getString("Name");
					connPoint.pointOID = device.deviceOID;// temObj.getInt("OID");
					connPoint.isParent = false;
					connPointList.add(connPoint);
				}
				device.pointList = connPointList;

				if (null == BaseApp.deviceList) {
					BaseApp.deviceList = new ArrayList<Device>();
				}
				BaseApp.deviceList.add(device);

				// 设置小助手
				device.getGroupYST();
				JVLittleTipsPacket packet = new JVLittleTipsPacket(256 * 2 + 16);
				packet.setChGroup(device.group);
				packet.setnYSTNO(device.yst);
				packet.setnChannel(1);
				packet.setChPName(device.deviceLoginUser);
				packet.setChPWord(device.deviceLoginPwd);
				packet.setnConnectStatus(0);
				System.arraycopy(packet.pack().data, 0, allByte, 0,
						packet.getLen());
				// JVSUDT.JVC_SetHelpYSTNO(allByte, allByte.length);
				Intent intent = new Intent(JVNPDetailActivity.this,
						JVPlayActivity.class);
				intent.setClass(JVNPDetailActivity.this, JVPlayActivity.class);
				intent.putExtra("PlayTag", JVConst.SHOWPOINT_PLAY_FLAG);
				intent.putExtra("DeviceIndex", BaseApp.deviceList.size() - 1);
				intent.putExtra("PointIndex", 0);
				JVNPDetailActivity.this.startActivity(intent);
				break;
			}
		}
	};

	private static class GetDetailThread extends Thread {
		private final WeakReference<JVNPDetailActivity> mActivity;

		public GetDetailThread(JVNPDetailActivity activity) {
			mActivity = new WeakReference<JVNPDetailActivity>(activity);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			JVNPDetailActivity activity = mActivity.get();
			if (null != activity && !activity.isFinishing()) {
				activity.npObj = LoginUtil.getNewProductDetail(activity.p_oid,
						activity.p_size, activity.p_lan);
				if (null != activity.npObj && null != activity.npObj.imageList
						&& 0 != activity.npObj.imageList.size()) {
					activity.npPicList = activity.npObj.imageList;
					if (null != activity.npPicList
							&& 0 != activity.npPicList.size()) {
						for (int i = 0; i < activity.npPicList.size(); i++) {
							String url = Url.DOWNLOAD_IMAGE_URL
									+ activity.npPicList.get(i).newProImgUrl;
							ImageUtil.getbitmapAndwrite(activity, url);
							MyLog.e("新品url", url + "");

						}
					}

				}

				Message msg = BaseApp.npHandler.obtainMessage();
				msg.what = JVConst.GET_NEWS_SUCCESS;
				BaseApp.npHandler.sendMessage(msg);
			}

		}

	}

	// 检查软件更新handler
	public static class NewProHandler extends Handler {

		private final WeakReference<JVNPDetailActivity> mActivity;

		public NewProHandler(JVNPDetailActivity activity) {
			mActivity = new WeakReference<JVNPDetailActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			JVNPDetailActivity activity = mActivity.get();
			if (null != activity && !activity.isFinishing()) {
				if (null != activity.dialog && activity.dialog.isShowing()) {
					activity.dialog.dismiss();
				}

				switch (msg.what) {
				case JVConst.GET_NEWS_SUCCESS:
					// 加载的图片
					if (null != activity.npPicList
							&& 0 != activity.npPicList.size()) {

						for (int i = 0; i < activity.npPicList.size(); i++) {

							Bitmap bm = ImageUtil
									.getbitmapAndwrite(
											activity,
											Url.DOWNLOAD_IMAGE_URL
													+ activity.npPicList.get(i).newProImgUrl);
							activity.newImage.setImageBitmap(bm);

						}
					}
					// 加载的详细信息
					if (null != activity.npObj) {
						// 确保连接参数都有
						if (!"".equalsIgnoreCase(activity.npObj.CloudSeeNo)
								&& !"".equalsIgnoreCase(activity.npObj.UserName)
								&& !"".equalsIgnoreCase(activity.npObj.UserPwd)
								&& 0 != activity.npObj.ChannelNum) {
							activity.startPlay.setVisibility(View.VISIBLE);
						}
					}

					break;
				}
			}

		}

	};

	@Override
	protected void onResume() {
		super.onResume();
		// TODO Auto-generated method stub
		try {
			if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {

				for (int i = 0; i < BaseApp.deviceList.size(); i++) {
					if (-1000 == BaseApp.deviceList.get(i).deviceOID) {
						BaseApp.deviceList.remove(i);
						// MyLog.e("演示点看完，删除演示点数据", i+"");
					}
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// 图片adapter
	class NPImageAdapter extends BaseAdapter {

		ArrayList<NewProduct> npList = null;
		Context mContext = null;
		public LayoutInflater inflater;

		public NPImageAdapter(Context con) {
			mContext = con;
			inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setData(ArrayList<NewProduct> list) {
			npList = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return npList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return npList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder viewHolder;
			if (null == convertView) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.image_item, null);
				viewHolder.npImageView = (ImageView) convertView
						.findViewById(R.id.img);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String url = Url.DOWNLOAD_IMAGE_URL
					+ npList.get(position).newProImgUrl;
			// Bitmap bitmap1 = ImageUtil.getBitmap(url);

			viewHolder.npImageView.setTag(url);
			MyLog.e("图片地址", "url: " + url + " , channel: " + position);
			bigBitmap = AsyncImageLoader.getInstance().imageCache.get(url);

			if (bigBitmap == null) {
				bigBitmap = AsyncImageLoader.getInstance().loadBitmap(url,
						new ImageCallback() {
							@Override
							public void imageLoaded(Bitmap imageBitmap,
									String imageUrl) {
								ImageView imageViewByTag = (ImageView) viewHolder
										.findViewWithTag(imageUrl);
								if (imageViewByTag != null) {
									imageViewByTag.setImageBitmap(imageBitmap);
								} else {
									// load image failed from Internet
									// imageViewByTag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.point_default_img));
								}
							}
						});
			}

			if (null != bigBitmap) {
				viewHolder.npImageView.setImageBitmap(bigBitmap);
			} else {
				viewHolder.npImageView.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.viewflow_default_img));
			}
			return convertView;
		}

		class ViewHolder {
			ImageView npImageView;

			public ImageView findViewWithTag(String imageUrl) {
				// TODO Auto-generated method stub

				if (imageUrl.equalsIgnoreCase(npImageView.getTag().toString())) {
					return npImageView;
				}
				return null;
			}
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			JVNPDetailActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	float imageScale = 1f;

	void rescaleImage(int scaleType) {
		imageScale = scaleType * 0.15f + imageScale;
		if (imageScale <= 0) {
			imageScale = -scaleType * 0.15f + imageScale;
			return;
		}
		newImage.zoomTo(imageScale);

	}

	void resetImage() {
		imageScale = 1f;
		newImage.zoomTo(imageScale);
	}

	// 计算移动距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		if (null != bigBitmap) {
			bigBitmap.recycle();
			bigBitmap = null;
		}
		super.onDestroy();
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

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
