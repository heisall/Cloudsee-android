//package com.jovision.activities;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.ActionBar.LayoutParams;
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Point;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.Settings;
//import android.util.DisplayMetrics;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.Window;
//import android.view.WindowManager;
//
//import com.jovetech.CloudSee.temp.R;
//import com.jovision.bean.ConnPoint;
//import com.jovision.bean.Device;
//import com.jovision.commons.BaseApp;
//import com.jovision.commons.JVConfigManager;
//import com.jovision.commons.JVConst;
//import com.jovision.commons.JVLittleTipsPacket;
//import com.jovision.commons.MyLog;
//import com.jovision.commons.Url;
//import com.jovision.utils.ConfigUtil;
//import com.jovision.utils.ImageUtil;
//import com.jovision.utils.JSONUtil;
//import com.mapabc.mapapi.core.GeoPoint;
//import com.mapabc.mapapi.core.OverlayItem;
//import com.mapabc.mapapi.map.ItemizedOverlay;
//import com.mapabc.mapapi.map.MapActivity;
//import com.mapabc.mapapi.map.MapController;
//import com.mapabc.mapapi.map.MapView;
//import com.mapabc.mapapi.map.Projection;
//
////import android.test.JVSUDT;
//
//public class JVMarkerActivity extends MapActivity {
//
//	private int NET_CONNECT_FLAG = 10001;
//	private DisplayMetrics dm = null;
//	private DataThread dataThread = null;// 开机获取数据线程
//	private DataThread refreshThread = null;// 设置网络后返回刷新线程
//
//	private JVConfigManager dbManager;
//	private MapView mMapView = null;
//	private MapController mMapController;
//	private ArrayList<OverlayItem> ollist = null;
//	private ArrayList<Bitmap> markerBitmapList = new ArrayList<Bitmap>();// 图片列表
//	private List<JSONObject> list;
//	// public String className = "";
//	private ProgressDialog dialog = null;
//	private JSONArray mJsonArray;
//	private boolean netFlag = false;
//
//	private GeoPoint centerPointer = null;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		// this.setMapMode(MAP_MODE_VECTOR);//设置地图为矢量模式
//		try {
//			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//					WindowManager.LayoutParams.FLAG_FULLSCREEN);
//			super.onCreate(savedInstanceState);
//			// 获取手机分辨率
//			dm = new DisplayMetrics();
//			getWindowManager().getDefaultDisplay().getMetrics(dm);
//			// loading框
//			dialog = new ProgressDialog(this);
//			dialog.setMessage(getResources().getString(
//					R.string.str_loading_showpoint));
//			dialog.show();
//
//			// 获取网络状态，开启读数据线程
//			netFlag = BaseApp.isConnected(JVMarkerActivity.this);
//			dataThread = new DataThread(JVMarkerActivity.this);
//			dataThread.start();
//
//			setContentView(R.layout.marker_layout);
//			getWindow().setLayout(LayoutParams.FILL_PARENT,
//					LayoutParams.FILL_PARENT);
//			addPointHelp();
//			initViews();
//
//			if (dbManager == null) {
//				dbManager = new JVConfigManager(this,
//						JVConst.JVCONFIG_DATABASE, null,
//						JVConst.JVCONFIG_DB_VER);
//				BaseApp.dbManager = dbManager;
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//
//	}
//
//	private void addPointHelp() {
//		Device d1 = new Device();
//		d1.deviceNum = "A361";
//		d1.deviceLoginUser = "abc";
//		d1.deviceLoginPwd = "123";
//		d1.getGroupYST();
//		BaseApp.setHelpToDevice(d1);
//
//		Device d2 = new Device();
//		d2.deviceNum = "A362";
//		d2.deviceLoginUser = "abc";
//		d2.deviceLoginPwd = "123";
//		d2.getGroupYST();
//		BaseApp.setHelpToDevice(d2);
//
//		Device d3 = new Device();
//		d3.deviceNum = "A365";
//		d3.deviceLoginUser = "abc";
//		d3.deviceLoginPwd = "123";
//		d3.getGroupYST();
//		BaseApp.setHelpToDevice(d3);
//	}
//
//	Handler myHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			try {
//				switch (msg.what) {
//				case 0:// 无设备
//					if (null != dialog && dialog.isShowing())
//						dialog.dismiss();
//
//					break;
//				case 1:// 有数据
//
//					if (null != dialog && dialog.isShowing())
//						dialog.dismiss();
//					try {
//						if (null == mMapView) {
//							mMapView = (MapView) findViewById(R.id.main_mapView);
//						}
//						mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
//						mMapController = mMapView.getController();
//						mMapController.setCenter(centerPointer);
//						mMapController.setZoom(7);
//						// Drawable marker = getResources().getDrawable(
//						// R.drawable.da_marker_red); // 得到需要标在地图上的资源
//						ImageThread imageThread = new ImageThread(
//								JVMarkerActivity.this);
//						imageThread.start();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//					break;
//				case 2:// 无数据
//					if (null != dialog && dialog.isShowing())
//						dialog.dismiss();
//
//					break;
//				case 3:// 无网络
//					if (null != dialog && dialog.isShowing())
//						dialog.dismiss();
//					alertDialog();
//					break;
//				case 4:// 演示点截图加载完
//					Drawable marker = getResources().getDrawable(
//							R.drawable.trans_dtz); // 得到需要标在地图上的资源
//					// Drawable marker =
//					// getResources().getDrawable(R.drawable.da_marker_red);
//					marker.setBounds(0, 0, marker.getIntrinsicWidth(),
//							marker.getIntrinsicHeight()); // 为maker定义位置和边界
//					try {
//						if (null == mMapView) {
//							mMapView = (MapView) findViewById(R.id.main_mapView);
//						}
//						mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
//						mMapController = mMapView.getController();
//
//						mMapView.getOverlays().add(
//								new OverItemT(marker, JVMarkerActivity.this,
//										ollist, list, markerBitmapList)); // 添加ItemizedOverlay实例到mMapView
//						if (null != dialog && dialog.isShowing())
//							dialog.dismiss();
//						mMapView.setSelected(true);
//						mMapController.setZoom(7);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//					break;
//				case 5:// 演示点加载失败
//					if (null != dialog && dialog.isShowing())
//						dialog.dismiss();
//
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//
//		}
//
//	};
//
//	// 加载演示点截图线程
//	static class ImageThread extends Thread {
//		private final WeakReference<JVMarkerActivity> mActivity;
//
//		public ImageThread(JVMarkerActivity activity) {
//			mActivity = new WeakReference<JVMarkerActivity>(activity);
//		}
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			super.run();
//			JVMarkerActivity activity = mActivity.get();
//			if (null != activity && !activity.isFinishing()) {
//				try {
//					for (int i = 0; i < activity.ollist.size(); i++) {
//						Bitmap marker = ImageUtil
//								.getShowBitmap(Url.MAP_SHOW_POINT_IMAGE
//										+ activity.ollist.get(i).getSnippet());
//
//						Bitmap marker1 = ImageUtil.addbackground4onlyicon(
//								activity, marker);
//						activity.markerBitmapList.add(marker1);
//					}
//					MyLog.e("tags", "markerBitmapListSize: "
//							+ activity.markerBitmapList.size());
//					Message msg = activity.myHandler.obtainMessage();
//					msg.what = 4;
//					activity.myHandler.sendMessage(msg);
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			}
//
//		}
//
//	}
//
//	// 获取基本信息
//	static class DataThread extends Thread {
//		private final WeakReference<JVMarkerActivity> mActivity;
//
//		public DataThread(JVMarkerActivity activity) {
//			mActivity = new WeakReference<JVMarkerActivity>(activity);
//		}
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			JVMarkerActivity activity = mActivity.get();
//			if (null != activity && !activity.isFinishing()) {
//				Message msg = activity.myHandler.obtainMessage();
//				try {
//					// 判断是否连接网络
//					if (activity.netFlag) {
//						// HashMap<String, String> map = new HashMap<String,
//						// String>();
//						// map.put("RequestType", "1");
//						// map.put("Language", lan);
//
//						activity.mJsonArray = JSONUtil
//								.getJSON(Url.MAP_SHOW_POINT
//										+ "RequestType=1&Language="
//										+ ConfigUtil.getLanguage());
//						activity.ollist = new ArrayList<OverlayItem>();
//						activity.list = new ArrayList<JSONObject>();
//
//						if (null != activity.mJsonArray
//								&& activity.mJsonArray.length() > 0) {
//							// Log.v("mJsonArray", "有数据");
//							for (int i = 0; i < activity.mJsonArray.length(); i++) {
//								JSONObject jsonObj = ((JSONObject) activity.mJsonArray
//										.getJSONObject(i));
//								activity.list.add(jsonObj);
//							}
//
//							/**
//							 * 06-25 13:46:55.110: V/result(20121): [ 06-25
//							 * 13:46:55.110: V/result(20121): { 06-25
//							 * 13:46:55.110: V/result(20121): "GUID":
//							 * "2bd2f41709a940bcb427c44257101823", 06-25
//							 * 13:46:55.110: V/result(20121): "OID": 24, 06-25
//							 * 13:46:55.110: V/result(20121): "Longitude":
//							 * "114", 06-25 13:46:55.110: V/result(20121):
//							 * "Latitude": "35", 06-25 13:46:55.110:
//							 * V/result(20121): "Name": "演示点A", 06-25
//							 * 13:46:55.110: V/result(20121): "Address": "山东济南",
//							 * 06-25 13:46:55.110: V/result(20121):
//							 * "DeviceImage": "DeviceImage/apple.jpg" 06-25
//							 * 13:46:55.110: V/result(20121): }, 06-25
//							 * 13:46:55.110: V/result(20121): { 06-25
//							 * 13:46:55.110: V/result(20121): "GUID":
//							 * "3e97116a31dc4b30babec0c389043c17", 06-25
//							 * 13:46:55.110: V/result(20121): "OID": 23, 06-25
//							 * 13:46:55.110: V/result(20121): "Longitude":
//							 * "117", 06-25 13:46:55.110: V/result(20121):
//							 * "Latitude": "36", 06-25 13:46:55.110:
//							 * V/result(20121): "Name": "演示点B", 06-25
//							 * 13:46:55.110: V/result(20121): "Address": "山东济南",
//							 * 06-25 13:46:55.110: V/result(20121):
//							 * "DeviceImage": "DeviceImage/ottawa.jpg" 06-25
//							 * 13:46:55.110: V/result(20121): } 06-25
//							 * 13:46:55.110: V/result(20121): ]
//							 */
//
//							for (int i = 0; i < activity.list.size(); i++) {
//								JSONObject a = activity.list.get(i);
//								Double lat = Double.parseDouble(a
//										.getString("Latitude"));
//								Double lng = Double.parseDouble(a
//										.getString("Longitude"));
//								String name = a.getString("Name");
//
//								String image = a.getString("DeviceImage");
//								// 用给定的经纬度构造GeoPoint，单位是微度（度 * 1E6）
//
//								GeoPoint gp = new GeoPoint((int) (lat * 1E6),
//										(int) (lng * 1E6));
//								activity.centerPointer = gp;
//								// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
//								activity.ollist.add(new OverlayItem(gp, name,
//										image));// item的位置，标题文本，图片地址
//							}
//							if (null != activity.ollist
//									&& 0 != activity.ollist.size()) {
//
//								msg.what = 1;
//								activity.myHandler.sendMessage(msg);
//							} else {
//								msg.what = 2;
//								activity.myHandler.sendMessage(msg);
//							}
//
//						} else {
//							msg.what = 0;
//							activity.myHandler.sendMessage(msg);
//						}
//					} else {
//						msg.what = 3;
//						activity.myHandler.sendMessage(msg);
//
//					}
//					// 获取后台返回的Json对象
//
//				} catch (JSONException e) {
//					System.out.print("异常原因" + e);
//					msg.what = 5;
//					activity.myHandler.sendMessage(msg);
//					e.printStackTrace();
//
//				} catch (Exception e) {
//					msg.what = 5;
//					activity.myHandler.sendMessage(msg);
//					e.printStackTrace();
//				}
//				super.run();
//			}
//
//		}
//
//	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		// TODO Auto-generated method stub
//		super.onConfigurationChanged(newConfig);
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		// setResult(JVConst.JV_RSTCODE_MENU_SHOWPOINT);//.setResult(JVConst.JV_RSTCODE_MENU_SHOWPOINT);
//		// this.finish();
//
//		BaseApp.openMap = false;
//		super.onDestroy();
//
//	}
//
//	void initViews() {
//		mMapView = (MapView) findViewById(R.id.main_mapView);
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		mMapView.destroyDrawingCache();
//		JVMarkerActivity.this.finish();
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		try {
//			if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
//
//				for (int i = 0; i < BaseApp.deviceList.size(); i++) {
//					if (-1000 == BaseApp.deviceList.get(i).deviceOID) {
//						BaseApp.deviceList.remove(i);
//						// MyLog.e("演示点看完，删除演示点数据", i+"");
//					}
//				}
//
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		super.onResume();
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		try {
//			if (requestCode == NET_CONNECT_FLAG) {
//				if (null == dialog) {
//					dialog = new ProgressDialog(this);
//					dialog.setMessage(getResources().getString(
//							R.string.str_loading_showpoint));
//					dialog.show();
//				} else {
//					dialog.show();
//				}
//
//				netFlag = BaseApp.isConnected(JVMarkerActivity.this);
//				refreshThread = new DataThread(JVMarkerActivity.this);
//				refreshThread.start();
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//	}
//
//	public void alertDialog() {
//
//		AlertDialog.Builder builder = new Builder(JVMarkerActivity.this);
//		builder.setMessage(R.string.str_setting_network);
//		builder.setTitle(R.string.tips);
//		builder.setPositiveButton(R.string.str_setting,
//				new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog, int which) {
//						JVMarkerActivity.this.startActivityForResult(
//								new Intent(Settings.ACTION_WIRELESS_SETTINGS),
//								NET_CONNECT_FLAG);
//					}
//				});
//		builder.setNegativeButton(R.string.str_cancel,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//		builder.create().show();
//	}
//
//}
//
///**
// * http://wmap.jovecloud.com:6161/MobileWeb.aspx?RequestType=2&GUID=
// * http://192.168.32.111:6161/MobileWeb.aspx?RequestType=2&GUID=
// * 分条目覆盖物。当某个类型的覆盖物，包含多个类型相同、显示方式相同、处理方式相同的项时，使用此类。
// */
//class OverItemT extends ItemizedOverlay<OverlayItem> {
//	private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
//	private List<JSONObject> list = null;
//	private Drawable marker;
//	private Context mContext;
//	MapView currMapView = null;
//	// private String activityName = "";
//	private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
//	public int selectIndex = 0;
//	byte allByte[] = new byte[528];
//
//	public OverItemT(Drawable image, Context context,
//			List<OverlayItem> itemlist, List<JSONObject> jsonList,
//			ArrayList<Bitmap> imageList) {
//		super(image);
//		this.marker = image;
//		this.mContext = context;
//		GeoList = itemlist;
//		list = jsonList;
//		// activityName = className;
//		bitmapList = imageList;
//		populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
//
//	}
//
//	@Override
//	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
//		// Projection接口用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换
//		try {
//			Projection projection = mapView.getProjection();
//			// Paint paintText = new Paint();
//			// paintText.setTextSize(25);
//			for (int index = size() - 1; index >= 0; index--) { // 遍历GeoList
//				OverlayItem overLayItem = getItem(index); // 得到给定索引的item
//				// String title = overLayItem.getTitle();
//				// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
//				Point point = projection.toPixels(overLayItem.getPoint(), null);
//				// 可在此处添加您的绘制代码
//
//				// paintText.setTypeface(Typeface.DEFAULT_BOLD);
//				//
//				// paintText.setColor(mContext.getResources().getColor(
//				// R.color.greypoint));
//				if (null != bitmapList.get(index)) {
//					canvas.drawBitmap(bitmapList.get(index), point.x - 57,
//							point.y - 150, null);
//				}
//
//				// canvas.drawText(title, point.x-50, point.y, paintText); //
//				// 绘制文本
//			}
//
//			OverlayItem overLayItem = getItem(selectIndex); // 得到给定索引的item
//			Point point = projection.toPixels(overLayItem.getPoint(), null);
//			// paintText.setColor(mContext.getResources().getColor(
//			// R.color.red));
//
//			if (null != bitmapList.get(selectIndex)) {
//				canvas.drawBitmap(bitmapList.get(selectIndex), point.x - 57,
//						point.y - 150, null);
//			}
//			// paintText.setTextSize(30);
//
//			super.draw(canvas, mapView, true);
//			// 调整一个drawable边界，使得(0,0)是这个drawable底部最后一行中心的一个像素
//
//			boundCenterBottom(marker);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//	}
//
//	@Override
//	protected OverlayItem createItem(int i) {
//		// TODO Auto-generated method stub
//		if (GeoList != null) {
//			return GeoList.get(i);
//		}
//		return null;
//	}
//
//	@Override
//	public int size() {
//		// TODO Auto-generated method stub
//		if (GeoList != null) {
//			return GeoList.size();
//		}
//		return 0;
//	}
//
//	@Override
//	public boolean onLongPress(GeoPoint arg0, MotionEvent arg1, MapView arg2) {
//		// TODO Auto-generated method stub
//		return true;
//	}
//
//	@Override
//	// 处理当点击事件
//	protected boolean onTap(int index) {
//		OverlayItem marker = GeoList.get(index);
//		selectIndex = index;
//		setFocus(marker);
//
//		JSONObject b = null;
//		JSONArray jsonArray;
//		List<JSONObject> listDetail = new ArrayList<JSONObject>();
//		List<String> demo = new ArrayList<String>();
//
//		try {
//			for (int i = 0; i < list.size(); i++) {
//				JSONObject JSONpoint = list.get(i);
//				String guid = JSONpoint.getString("GUID");
//				String GUID_URL = Url.MAP_POINT_DETAIL + guid;
//				MyLog.e("演示点详细url：", GUID_URL);
//				demo.add(GUID_URL);
//				GUID_URL = null;
//			}
//			for (int j = 0; j < demo.size(); j++) {
//				jsonArray = JSONUtil.getJSON(demo.get(j));
//				if (null != jsonArray && jsonArray.length() > 0) {
//
//					for (int i = 0; i < jsonArray.length(); i++) {
//						JSONObject jsonObj = ((JSONObject) jsonArray
//								.getJSONObject(i));
//						if (jsonObj.length() > 1) {
//							listDetail.add(jsonObj);
//							System.out.println(jsonObj);
//							jsonObj = null;
//						}
//					}
//
//				}
//			}
//
//			if (listDetail != null && listDetail.size() > 0
//					&& index < listDetail.size()) {
//				b = listDetail.get(index);
//				// JVConnectInfo connectInfo = new JVConnectInfo();
//				Device device = new Device();
//				/**
//				 * [ { "CloudSeeNumber": "A361", "VideoChannelNum": 4,
//				 * "Account": "abc", "PassWord": "123", "IP": "", "Port": 0,
//				 * "Name": "Demonstration Point B", "Address": "shandongjinan",
//				 * "Characteristic": "N71B Camera", "Surroundings":
//				 * "N71B Camera", "MapCoordinatesGUID":
//				 * "724a9e8159dc40ada23969e8d0734638", "AudioURL":
//				 * "http://en.jovetech.com/Products/ProductView.aspx?id=97",
//				 * "GUID": "29acb6c61260415f9837c38899cb7ebc", "OID": 2 } ]
//				 */
//
//				/**
//				 * {"Characteristic":null, "OID":23,
//				 * "GUID":"3e97116a31dc4b30babec0c389043c17", "IP":"",
//				 * "Surroundings":null, "Account":null, "CloudSeeNumber":null,
//				 * "Name":"演示点B", "Port":-1, "PassWord":null, "Address":"山东济南",
//				 * "AudioURL":null, "MapCoordinatesGUID":null,
//				 * "VideoChannelNum":0}
//				 */
//
//				String ystnum = b.getString("CloudSeeNumber");
//
//				if ("null".equalsIgnoreCase(ystnum)) {
//					return true;
//				}
//				device.deviceNum = ystnum;
//				device.getGroupYST();
//				String ystaccount = b.getString("Account");
//				String ystpass = b.getString("PassWord");
//				int ystvcnum = b.getInt("VideoChannelNum");
//				String moreUrl = b.getString("AudioURL");
//
//				// if(!"".equals(b.getString("IP"))){
//				// device.deviceLocalIp = b.getString("IP");
//				// }else{
//				device.deviceLocalIp = "127.0.0.1";
//				// }
//
//				// if(0 != b.getInt("Port")){
//				// device.deviceLocalPort = b.getInt("Port");
//				// }else{
//				device.deviceLocalPort = 9101;
//				// }
//				device.deviceLoginUser = ystaccount;// temObj.getString("LoginUser");
//				device.deviceLoginPwd = ystpass;// temObj.getString("LoginPwd");
//				device.deviceOwner = -1000;// temObj.getInt("Owner");
//				device.deviceName = ystnum;//
//				device.deviceOID = -1000;// temObj.getInt("OID");
//				device.onlineState = 1;// 设置为在线，否则没法看
//
//				ArrayList<ConnPoint> connPointList = new ArrayList<ConnPoint>();
//				if (ystvcnum <= 0) {
//					return true;
//				}
//
//				for (int i = 0; i < ystvcnum; i++) {
//					ConnPoint connPoint = new ConnPoint();
//					connPoint.deviceID = device.deviceOID;// temObj.getInt("DeviceID");
//					connPoint.pointNum = i + 1;// temObj.getInt("PointNum");
//					connPoint.pointOwner = device.deviceOID;// temObj.getInt("Owner");
//					connPoint.pointName = ystnum + "_" + (i + 1);// temObj.getString("Name");
//					connPoint.pointOID = device.deviceOID;// temObj.getInt("OID");
//					connPoint.isParent = false;
//					connPointList.add(connPoint);
//				}
//				device.pointList = connPointList;
//
//				if (null == BaseApp.deviceList) {
//					BaseApp.deviceList = new ArrayList<Device>();
//				}
//				BaseApp.deviceList.add(device);
//
//				// 设置小助手
//
//				device.getGroupYST();
//				JVLittleTipsPacket packet = new JVLittleTipsPacket(256 * 2 + 16);
//				packet.setChGroup(device.group);
//				packet.setnYSTNO(device.yst);
//				packet.setnChannel(1);
//				packet.setChPName(device.deviceLoginUser);
//				packet.setChPWord(device.deviceLoginPwd);
//				packet.setnConnectStatus(0);
//				System.arraycopy(packet.pack().data, 0, allByte, 0,
//						packet.getLen());
//				// JVSUDT.JVC_SetHelpYSTNO(allByte, allByte.length);
//
//				// Bundle bundle = new Bundle();
//				// bundle.putSerializable(
//				// "connetInfo", device.toJVConnectInfo());
//				Intent intent = new Intent(mContext, JVPlayActivity.class);
//				// intent.putExtra("PlayTag",
//				// JVPlayActivity.SHOWPOINT_PLAY_FLAG);
//
//				intent.setClass(mContext, JVPlayActivity.class);
//				intent.putExtra("PlayTag", JVConst.SHOWPOINT_PLAY_FLAG);
//				intent.putExtra("DeviceIndex", BaseApp.deviceList.size() - 1);
//				intent.putExtra("PointIndex", 0);
//
//				// intent.putExtras(bundle);
//				mContext.startActivity(intent);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return true;
//	}
//
//	@Override
//	public boolean onTap(GeoPoint point, MapView mapView) {
//		// TODO Auto-generated method stub
//
//		currMapView = mapView;
//		return super.onTap(point, mapView);
//	}
//
// }
