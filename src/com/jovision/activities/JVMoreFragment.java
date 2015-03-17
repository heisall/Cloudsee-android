package com.jovision.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.test.JVACCOUNT;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.activities.JVTabActivity.OnMainListener;
import com.jovision.adapters.FragmentAdapter;
import com.jovision.bean.MoreFragmentBean;
import com.jovision.bean.WebUrl;
import com.jovision.commons.CheckUpdateTask;
import com.jovision.commons.GetDemoTask;
import com.jovision.commons.JVAlarmConst;
import com.jovision.commons.MyActivityManager;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.BitmapCache;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.GetPhoneNumber;
import com.jovision.utils.ListViewUtil;
import com.jovision.utils.MobileUtil;
import com.jovision.utils.UserUtil;
import com.jovision.views.AlarmDialog;
import com.jovision.views.popw;
import com.tencent.stat.StatService;

/**
 * 更多
 */
public class JVMoreFragment extends BaseFragment implements OnMainListener {
	// Adapter 存储模块文字和图标
	private ArrayList<MoreFragmentBean> dataList;
	// 模块listView
	private ListView more_listView;
	// listView 适配器
	private FragmentAdapter adapter;
	// Fragment依附的activity
	private Activity activity;
	// 修改资料
	private TextView more_modify;
	// 找回密码
	private TextView more_findpassword;
	// 头像
	private ImageView more_head;
	// 注销按钮
	private RelativeLayout more_cancle;
	// 用户名称
	private TextView more_username;
	// 用户名
	private String more_name;

	// 最后一次登录时间
	private TextView more_lasttime;
	// 修改密码
	private TextView more_modifypwd;
	// 绑定邮箱
	private TextView more_bindmail;
	// 获取url
	private WebUrl url;

	private boolean isgetemail;

	private GetPhoneNumber phoneNumber;

	private popw popupWindow; // 声明PopupWindow对象；
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	// 存放头像的文件夹
	File file;
	// 旧头像文件
	File tempFile;
	// 新头像文件
	File newFile;

	private String hasbandEmail = "";
	private String hasbandPhone = "";
	private String hasnicknameString = "";
	private String usernameInfo = "";

	private final String TAG = "JVMoreFragment";
	// 图片数组
	private int[] Image = { R.drawable.morefragment_help_icon,
			R.drawable.morefragment_autologin_icon,
			R.drawable.morefragment_warmmessage_icon,
			R.drawable.alarm_info_icon, R.drawable.morefragment_setting_icon,
			R.drawable.develop_warning, R.drawable.develop_warning,
			R.drawable.develop_warning, R.drawable.develop_warning,
			R.drawable.morefragment_install_icon,
			R.drawable.morefragment_sharedevice_icon,
			R.drawable.morefragment_data_icon, R.drawable.more_bbs,
			R.drawable.more_message, R.drawable.media_image,
			R.drawable.morefragment_feedback_icon,
			R.drawable.morefragment_update_icon,
			R.drawable.morefragment_aboutus_icon };
	// 功能名称数组
	private String[] fragment_name;

	public static boolean localFlag = false;// 本地登陆标志位

	private LinearLayout linear;

	private int littlenum = 0;

	private MainApplication mApp;

	private ImageView more_camera;

	private boolean showGCS = false;// 是否显示工程商

	public interface OnFuncActionListener {
		public void OnFuncEnabled(int func_index, int enabled);

		public void OnFuncSelected(int func_index, String params);
	}

	private OnFuncActionListener mListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_more, container, false);
		mApp = (MainApplication) getActivity().getApplication();
		intiUi(view);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFuncActionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnFuncEnabledListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mActivity = (BaseActivity) getActivity();

		// 判断是否显示工程商
		String showGcsStr = mActivity.statusHashMap.get(Consts.MORE_GCS_SWITCH);
		if (null != showGcsStr && !"".equalsIgnoreCase(showGcsStr)) {
			if (1 == Integer.parseInt(showGcsStr)) {
				showGCS = true;
			} else {
				showGCS = false;
			}
		}
		localFlag = Boolean.valueOf(mActivity.statusHashMap
				.get(Consts.LOCAL_LOGIN));
		currentMenu.setText(R.string.more_featrue);
		rightBtn.setVisibility(View.GONE);
		leftBtn.setVisibility(View.GONE);
		if (null != mActivity.statusHashMap.get(Consts.KEY_LAST_LOGIN_TIME)) {
			more_lasttime.setText(mActivity.statusHashMap
					.get(Consts.KEY_LAST_LOGIN_TIME));
		} else {
			more_lasttime.setText("");
		}
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_PUSH_MESSAGE:
			// 弹出对话框
			if (null != mActivity) {
				mActivity.onNotify(Consts.NEW_PUSH_MSG_TAG_PRIVATE, 0, 0, null);//
				// 通知显示报警信息条数
				int new_alarm_nums = mApp.getNewPushCnt();
				adapter.setNewNums(new_alarm_nums);
				adapter.notifyDataSetChanged();
				new AlarmDialog(mActivity).Show(obj);
			} else {
				MyLog.e("Alarm",
						"onHandler mActivity is null ,so dont show the alarm dialog");
			}
			break;
		case Consts.WHAT_BIND:
			// more_bindmail.setVisibility(View.VISIBLE);
			break;
		case Consts.NEW_BBS:
			if (null != adapter) {
				adapter.setBBSNums(arg1);
				adapter.notifyDataSetChanged();
			}
			// mActivity.showTextToast("获得结果");
			break;
		}

	}

	private void intiUi(View view) {
		activity = getActivity();
		if (MySharedPreference.getBoolean(Consts.MORE_PLAYMODE)) {
			fragment_name = activity.getResources().getStringArray(
					R.array.array_moreduo);
		} else {
			fragment_name = activity.getResources().getStringArray(
					R.array.array_more);
		}
		MySharedPreference.putBoolean("ISPHONE", false);
		MySharedPreference.putBoolean("ISEMAIL", false);
		if (Boolean.valueOf(((BaseActivity) activity).statusHashMap
				.get(Consts.LOCAL_LOGIN))) {
			more_name = activity.getResources().getString(
					R.string.location_login);
		} else {
			more_name = ((BaseActivity) activity).statusHashMap
					.get(Consts.KEY_USERNAME);
			MySharedPreference.putString("ACCOUNT", more_name);
		}
		if (AccountUtil.verifyEmail(more_name)) {
			MySharedPreference.putBoolean("ISEMAIL", true);
		} else {
			MySharedPreference.putBoolean("ISEMAIL", false);
		}
		phoneNumber = new GetPhoneNumber(more_name);
		if (5 != phoneNumber.matchNum() && 4 != phoneNumber.matchNum()) {
			MySharedPreference.putBoolean("ISPHONE", true);
		} else {
			MySharedPreference.putBoolean("ISPHONE", false);
		}
		initDatalist();

		more_camera = (ImageView) view.findViewById(R.id.more_camera);
		more_modifypwd = (TextView) view.findViewById(R.id.more_modifypwd);
		more_bindmail = (TextView) view.findViewById(R.id.more_bindmail);
		more_cancle = (RelativeLayout) view.findViewById(R.id.more_cancle);
		more_modify = (TextView) view.findViewById(R.id.more_modify);
		more_findpassword = (TextView) view
				.findViewById(R.id.more_findpassword);
		more_username = (TextView) view.findViewById(R.id.more_uesrname);
		more_lasttime = (TextView) view.findViewById(R.id.more_lasttime);
		linear = (LinearLayout) view.findViewById(R.id.lin);
		more_head = (ImageView) view.findViewById(R.id.more_head_img);

		more_listView = (ListView) view.findViewById(R.id.more_listView);
		adapter = new FragmentAdapter(JVMoreFragment.this, dataList);
		more_listView.setAdapter(adapter);
		ListViewUtil.setListViewHeightBasedOnChildren(more_listView);
		listViewClick();

		more_username.setOnClickListener(myOnClickListener);
		more_bindmail.setOnClickListener(myOnClickListener);
		more_modifypwd.setOnClickListener(myOnClickListener);
		more_cancle.setBackgroundResource(R.drawable.blue_bg);
		more_head.setOnClickListener(myOnClickListener);
		more_cancle.setOnClickListener(myOnClickListener);
		more_modify.setOnClickListener(myOnClickListener);
		more_findpassword.setOnClickListener(myOnClickListener);

		MySharedPreference.putString("REBINDPHONE", "");
		MySharedPreference.putString("REBINDEMAIL", "");

		if (!Boolean.valueOf(((BaseActivity) activity).statusHashMap
				.get(Consts.LOCAL_LOGIN))) {
			more_modifypwd.setVisibility(View.VISIBLE);
		} else {
			more_modifypwd.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		MyLog.e("TMAC", "the JVMoreFragment onResume invoke~~~");
		super.onResume();
		more_bindmail.setVisibility(View.GONE);
		if (!Boolean.valueOf(((BaseActivity) activity).statusHashMap
				.get(Consts.LOCAL_LOGIN))
				&& "".equals(MySharedPreference.getString("USERINFO"))) {
			isgetemail = false;
			CheckUserInfoTask task = new CheckUserInfoTask();
			task.execute(more_name);
		} else if (!"".equals(MySharedPreference.getString("USERINFO"))) {
			File file = new File(Consts.HEAD_PATH
					+ MySharedPreference.getString("USERINFO") + ".jpg");
			if (file.exists()) {
				more_camera.setVisibility(View.GONE);
			}
			Bitmap bitmap = BitmapFactory.decodeFile(Consts.HEAD_PATH
					+ MySharedPreference.getString("USERINFO")
					+ Consts.IMAGE_JPG_KIND);
			more_head.setImageBitmap(bitmap);
		} else if (Boolean.valueOf(((BaseActivity) activity).statusHashMap
				.get(Consts.LOCAL_LOGIN))) {
			file = new File(Consts.HEAD_PATH);
			MobileUtil.createDirectory(file);
			tempFile = new File(Consts.HEAD_PATH + more_name + ".jpg");
			newFile = new File(Consts.HEAD_PATH + more_name + "1.jpg");

			if (null != tempFile && tempFile.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(Consts.HEAD_PATH
						+ more_name + Consts.IMAGE_JPG_KIND);
				more_head.setImageBitmap(bitmap);
				more_camera.setVisibility(View.GONE);
			}
		}
		// if (MySharedPreference.getBoolean("ISSHOW", false)) {
		// more_bindmail.setVisibility(View.VISIBLE);
		// }
		if (!"".equals(MySharedPreference.getString("ACCOUNT"))
				&& null != MySharedPreference.getString("ACCOUNT")) {
			more_name = MySharedPreference.getString("ACCOUNT");
		}
		more_username.setText(more_name);
		int alarm_new_nums = mApp.getNewPushCnt();
		adapter.setNewNums(alarm_new_nums);
		adapter.setShowGCS(showGCS);
		adapter.notifyDataSetChanged();
		ListViewUtil.setListViewHeightBasedOnChildren(more_listView);
	}

	private void initDatalist() {
		try {
			dataList = new ArrayList<MoreFragmentBean>();
			for (int i = 0; i < Image.length; i++) {
				MoreFragmentBean bean = new MoreFragmentBean();
				bean.setItem_img(Image[i]);
				bean.setName(fragment_name[i]);
				dataList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pop_outside:
				popupWindow.dismiss();
				break;
			case R.id.btn_pick_photo: {
				popupWindow.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
				break;
			}
			case R.id.btn_take_photo:
				// 调用系统的拍照功能
				popupWindow.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 指定调用相机拍照后照片的储存路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
				startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
				break;
			case R.id.btn_cancel:
				popupWindow.dismiss();
				break;
			case R.id.more_uesrname:
			case R.id.more_head_img:
				// TODO
				isgetemail = true;
				if (!Boolean.valueOf(((BaseActivity) activity).statusHashMap
						.get(Consts.LOCAL_LOGIN))) {
					mActivity.createDialog("", true);
					CheckUserInfoTask task = new CheckUserInfoTask();
					task.execute(more_name);
				}
				if (Boolean.valueOf(((BaseActivity) activity).statusHashMap
						.get(Consts.LOCAL_LOGIN))) {
					StatService.trackCustomEvent(mActivity,
							"census_moreheadimg", mActivity.getResources()
									.getString(R.string.census_moreheadimg));
					popupWindow = new popw(mActivity, myOnClickListener);
					popupWindow.setBackgroundDrawable(null);
					popupWindow.setOutsideTouchable(true);
					popupWindow.showAtLocation(linear, Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				}
				break;
			case R.id.more_cancle:// 注销
				LogOutTask task = new LogOutTask();
				String[] strParams = new String[3];
				task.execute(strParams);
				break;
			case R.id.more_modify:

				break;
			case R.id.more_bindmail:
				StatService.trackCustomEvent(
						mActivity,
						"MoreBindmail",
						mActivity.getResources().getString(
								R.string.census_morebindmail));
				startActivity(new Intent(mActivity,
						JVReBoundEmailActivity.class));
				break;
			case R.id.more_modifypwd:
				if (!localFlag) {
					Intent editpassintent = new Intent(mActivity,
							JVEditPassActivity.class);
					startActivity(editpassintent);
				} else {
					mActivity.showTextToast(R.string.more_nologin);
				}
				break;
			case R.id.more_findpassword:

				break;
			default:
				break;

			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			if (resultCode == -1) {
				startPhotoZoom(Uri.fromFile(newFile), 300);
			}
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData(), 300);
			break;

		case PHOTO_REQUEST_CUT:
			if (data != null)
				setPicToView(data);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	// 将进行剪裁后的图片显示到UI界面上
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			saveBitmap(photo);
			Drawable drawable = new BitmapDrawable(photo);
			more_camera.setVisibility(View.GONE);
			more_head.setBackgroundDrawable(drawable);
		}
	}

	public void saveBitmap(Bitmap bm) {
		if (null == bm) {
			return;
		}
		File f;
		if (localFlag) {
			f = new File(Consts.HEAD_PATH + more_name + ".jpg");
		} else {
			f = new File(Consts.HEAD_PATH + usernameInfo + ".jpg");
		}
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void listViewClick() {
		more_listView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (position) {
						case 0: // 帮助图片是否显示
							if (MySharedPreference.getBoolean(Consts.MORE_HELP)) {
								MySharedPreference.putBoolean(Consts.MORE_HELP,
										false);
								MySharedPreference.putBoolean(
										Consts.MORE_PAGEONE, true);
								MySharedPreference.putBoolean(
										Consts.MORE_PAGETWO, true);
							} else {
								MySharedPreference.putBoolean(Consts.MORE_HELP,
										true);
								MySharedPreference.putBoolean(
										Consts.MORE_PAGEONE, false);
								MySharedPreference.putBoolean(
										Consts.MORE_PAGETWO, false);
							}
							break;
						case 1: // 自动登录功能
							// TODO
							if (MySharedPreference
									.getBoolean(Consts.MORE_REMEMBER)) {
								MySharedPreference.putBoolean(
										Consts.MORE_REMEMBER, false);
							} else {
								MySharedPreference.putBoolean(
										Consts.MORE_REMEMBER, true);
							}
							break;
						case 2: // 报警通知开关
							AlarmTask task = new AlarmTask();
							Integer[] params = new Integer[3];
							if (!MySharedPreference.getBoolean(
									Consts.MORE_ALARMSWITCH, true)) {// 1是关
								// 0是开
								params[0] = JVAlarmConst.ALARM_ON;// 关闭状态，去打开报警
							} else {
								params[0] = JVAlarmConst.ALARM_OFF;// 已经打开了，要去关闭
							}
							task.execute(params);

							break;
						case 3:// 换成报警信息
							if (localFlag)// 本地登录
							{
								mActivity.showTextToast(R.string.more_nologin);
							} else {
								if (!ConfigUtil.isConnected(mActivity)) {
									mActivity.alertNetDialog();
								} else {
									mApp.setNewPushCnt(0);
									Intent intent2 = new Intent(mActivity,
											AlarmInfoActivity.class);
									startActivity(intent2);
								}
							}

							// if (!MySharedPreference.getBoolean("VideoSquer"))
							// {
							// MySharedPreference.putBoolean("VideoSquer",
							// true);
							// }
							//
							// if (!ConfigUtil.isConnected(mActivity)) {
							// mActivity.alertNetDialog();
							// } else {
							// StatService.trackCustomEvent(
							// mActivity,
							// "Demo",
							// mActivity.getResources().getString(
							// R.string.census_demo));
							//
							// GetDemoTask demoTask = new GetDemoTask(
							// mActivity);
							// String[] demoParams = new String[3];
							// if (!Boolean
							// .valueOf(((BaseActivity) activity).statusHashMap
							// .get(Consts.LOCAL_LOGIN))) {
							// String sessionResult = ConfigUtil
							// .getSession();
							//
							// MyLog.v("session", sessionResult);
							// demoParams[0] = sessionResult;
							// } else {
							// demoParams[0] = "";
							// }
							// demoTask.execute(demoParams);
							// }
							// TODO
							break;
						case 4: // 观看模式（单设备，多设备）
							if (MySharedPreference
									.getBoolean(Consts.MORE_PLAYMODE)) {
								MySharedPreference.putBoolean(
										Consts.MORE_PLAYMODE, false);
								dataList.get(4).setName(
										mActivity.getResources().getString(
												R.string.str_video_modetwo));
							} else {
								MySharedPreference.putBoolean(
										Consts.MORE_PLAYMODE, true);
								dataList.get(4)
										.setName(
												mActivity
														.getResources()
														.getString(
																R.string.str_video_more_modetwo));
							}
							break;
						case 5:// 小助手
							if (MySharedPreference
									.getBoolean(Consts.MORE_LITTLEHELP)) {
								MySharedPreference.putBoolean(
										Consts.MORE_LITTLEHELP, false);
							} else {
								MySharedPreference.putBoolean(
										Consts.MORE_LITTLEHELP, true);
							}
							break;
						case 6:// 广播
							if (MySharedPreference
									.getBoolean(Consts.MORE_BROADCAST)) {
								MySharedPreference.putBoolean(
										Consts.MORE_BROADCAST, false);
							} else {
								MySharedPreference.putBoolean(
										Consts.MORE_BROADCAST, true);
							}
							break;
						case 7:// 测试服务器开关
							MySharedPreference.putString("ChannelIP", "");
							MySharedPreference.putString("OnlineIP", "");
							MySharedPreference.putString("ChannelIP_en", "");
							MySharedPreference.putString("OnlineIP_en", "");

							if (MySharedPreference
									.getBoolean(Consts.MORE_TESTSWITCH)) {
								MySharedPreference.putBoolean(
										Consts.MORE_TESTSWITCH, false);
							} else {
								// MySharedPreference.clearAll();
								// 打开测试开关要关闭记住密码功能
								MySharedPreference.putBoolean(
										Consts.MORE_TESTSWITCH, true);
								MySharedPreference.putBoolean(
										Consts.MORE_REMEMBER, false);
							}
							break;
						case 8:// 版本号
								// 获取用户未读消息
								// v.php?mod=api&act=user_pm&sid=<>
								// sid 用户标识
								// return:
								// {"success":true,"msg":null,"errCode":null,"data":[{"url":"","count":""}]}
								// count:消息数量
								// url:消息页面
								// 现在success一直返回false

							Intent intentVersion = new Intent(mActivity,
									JVVersionActivity.class);
							mActivity.startActivity(intentVersion);

							break;

						case 9: // 2015.3.16 我要装监控改为工程商入驻
							if (!showGCS) {
								break;
							}
							if (!MySharedPreference
									.getBoolean(Consts.MORE_GCSURL)) {
								MySharedPreference.putBoolean(
										Consts.MORE_GCSURL, true);
								mListener.OnFuncEnabled(0, 1);
							}
							if (!ConfigUtil.isConnected(mActivity)) {
								mActivity.alertNetDialog();
							} else {
								if (null != ((BaseActivity) mActivity).statusHashMap
										.get(Consts.MORE_GCSURL)) {
									Intent intentAD0 = new Intent(mActivity,
											JVWebViewActivity.class);
									intentAD0
											.putExtra(
													"URL",
													((BaseActivity) mActivity).statusHashMap
															.get(Consts.MORE_GCSURL));
									intentAD0.putExtra("title", -2);
									mActivity.startActivity(intentAD0);
								} else {
									if ("false".equals(mActivity.statusHashMap
											.get(Consts.KEY_INIT_ACCOUNT_SDK))) {
										MyLog.e("Login", "初始化账号SDK失败");
										ConfigUtil
												.initAccountSDK(((MainApplication) mActivity
														.getApplication()));// 初始化账号SDK
									}
									GetDemoTask UrlTask = new GetDemoTask(
											mActivity);
									String[] demoParams = new String[3];
									demoParams[1] = "0";
									UrlTask.execute(demoParams);
								}
							}
							break;
						case 10: // 设备分享
							// GetDemoTask UrlTask1 = new
							// GetDemoTask(mActivity);
							// String[] demoParams1 = new String[3];
							// demoParams1[0] = "1";
							// UrlTask1.execute(demoParams1);
							break;
						case 11: // 云视通指数
							if (!MySharedPreference
									.getBoolean(Consts.MORE_STATURL)) {
								MySharedPreference.putBoolean(
										Consts.MORE_STATURL, true);
								mListener.OnFuncEnabled(0, 1);
							}
							if (!ConfigUtil.isConnected(mActivity)) {
								mActivity.alertNetDialog();
							} else {
								if (null != ((BaseActivity) mActivity).statusHashMap
										.get(Consts.MORE_STATURL)) {
									Intent intentAD0 = new Intent(mActivity,
											JVWebViewActivity.class);
									intentAD0
											.putExtra(
													"URL",
													((BaseActivity) mActivity).statusHashMap
															.get(Consts.MORE_STATURL));
									intentAD0.putExtra("title", -2);
									mActivity.startActivity(intentAD0);
								} else {
									if ("false".equals(mActivity.statusHashMap
											.get(Consts.KEY_INIT_ACCOUNT_SDK))) {
										MyLog.e("Login", "初始化账号SDK失败");
										ConfigUtil
												.initAccountSDK(((MainApplication) mActivity
														.getApplication()));// 初始化账号SDK
									}
									GetDemoTask UrlTask2 = new GetDemoTask(
											mActivity);
									String[] demoParams2 = new String[3];
									demoParams2[1] = "2";
									UrlTask2.execute(demoParams2);
								}
							}
							break;
						case 12:
							// if
							// (!MySharedPreference.getBoolean(Consts.MORE_BBS))
							// {
							// MySharedPreference.putBoolean(Consts.MORE_BBS,
							// true);
							// mListener.OnFuncEnabled(0, 1);
							// }
							if (!ConfigUtil.isConnected(mActivity)) {
								mActivity.alertNetDialog();
							} else {
								if (null != ((BaseActivity) mActivity).statusHashMap
										.get(Consts.MORE_BBS)) {
									Intent intentAD0 = new Intent(mActivity,
											JVWebViewActivity.class);
									intentAD0
											.putExtra(
													"URL",
													((BaseActivity) mActivity).statusHashMap
															.get(Consts.MORE_BBS));
									intentAD0.putExtra("title", -2);
									mActivity.startActivity(intentAD0);
								} else {
									String sid = "";
									if (!Boolean
											.valueOf(mActivity.statusHashMap
													.get(Consts.LOCAL_LOGIN))) {
										String sessionResult = ConfigUtil
												.getSession();
										sid = sessionResult;
									} else {
										sid = "";
									}

									if ("false".equals(mActivity.statusHashMap
											.get(Consts.KEY_INIT_ACCOUNT_SDK))) {
										MyLog.e("Login", "初始化账号SDK失败");
										ConfigUtil
												.initAccountSDK(((MainApplication) mActivity
														.getApplication()));// 初始化账号SDK
									}
									adapter.setBBSNums(0);
									adapter.notifyDataSetChanged();
									GetDemoTask UrlTask2 = new GetDemoTask(
											mActivity);
									String[] demoParams2 = new String[3];
									demoParams2[0] = sid;
									demoParams2[1] = "3";
									UrlTask2.execute(demoParams2);
								}
							}
							break;
						case 13: // 系统消息
							if (!MySharedPreference
									.getBoolean(Consts.MORE_SYSTEMMESSAGE)) {
								MySharedPreference.putBoolean(
										Consts.MORE_SYSTEMMESSAGE, true);
								mListener.OnFuncEnabled(0, 1);
							}
							if (!ConfigUtil.isConnected(mActivity)) {
								mActivity.alertNetDialog();
							} else {
								StatService.trackCustomEvent(
										mActivity,
										"MoreMessage",
										mActivity.getResources().getString(
												R.string.census_moremessage));
								Intent infoIntent = new Intent();
								infoIntent.setClass(mActivity,
										JVSystemInfoActivity.class);
								mActivity.startActivity(infoIntent);
							}
							break;
						case 14: // 图像查看
							StatService.trackCustomEvent(
									mActivity,
									"Media",
									mActivity.getResources().getString(
											R.string.census_media));
							Intent intentMedia = new Intent(mActivity,
									JVMediaActivity.class);
							mActivity.startActivity(intentMedia);
							break;
						case 15: // 意见反馈
							// Intent intent = new Intent(mActivity,
							// JVFeedbackActivity.class);
							// startActivity(intent);
							break;
						case 16: // 检查更新
							if (!ConfigUtil.isConnected(mActivity)) {
								mActivity.alertNetDialog();
							} else {
								mActivity.createDialog("", false);
								CheckUpdateTask taskf = new CheckUpdateTask(
										mActivity);
								String[] strParams = new String[3];
								strParams[0] = "1";// 1,手动检查更新
								taskf.execute(strParams);
							}

							break;
						case 17: // 关于
							if (!MySharedPreference
									.getBoolean(Consts.MORE_LITTLE)) {
								littlenum++;
								if (littlenum < 20) {
									if (littlenum >= 17) {
										mActivity
												.showTextToast((20 - littlenum)
														+ " ");
									}
								} else if (littlenum == 20) {
									MySharedPreference.putBoolean(
											Consts.MORE_LITTLEHELP, true);
									MySharedPreference.putBoolean(
											Consts.MORE_BROADCAST, true);
									MySharedPreference.putBoolean(
											Consts.MORE_LITTLE, true);
									ListViewUtil
											.setListViewHeightBasedOnChildren(more_listView);
								}
							} else {
								littlenum = 0;
								MySharedPreference.putBoolean(
										Consts.MORE_LITTLEHELP, false);
								MySharedPreference.putBoolean(
										Consts.MORE_BROADCAST, false);
								MySharedPreference.putBoolean(
										Consts.MORE_LITTLE, false);
								ListViewUtil
										.setListViewHeightBasedOnChildren(more_listView);
							}
							break;
						default:
							break;
						}
						adapter.notifyDataSetChanged();
					}
				});
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}

	class CheckUserInfoTask extends AsyncTask<String, Integer, Integer> {
		String account = "";
		String strResonse = "";
		String strPhone = "";
		String strMail = "";

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			account = params[0];
			int ret = -1;
			strResonse = JVACCOUNT.GetAccountInfo();
			JSONObject resObject = null;
			Log.i("TAG", strResonse);
			try {
				resObject = new JSONObject(strResonse);
				ret = resObject.optInt("result", -2);
				if (ret == 0) {
					strPhone = resObject.optString("phone");
					strMail = resObject.optString("mail");
					hasnicknameString = resObject.optString("nickname");
					usernameInfo = resObject.optString("username");
					Log.i("TAG", usernameInfo);
					MySharedPreference.putString("USERINFO", usernameInfo);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return ret;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			mActivity.dismissDialog();
			if (result == 0)// ok
			{
				file = new File(Consts.HEAD_PATH);
				MobileUtil.createDirectory(file);
				tempFile = new File(Consts.HEAD_PATH + usernameInfo + ".jpg");
				newFile = new File(Consts.HEAD_PATH + usernameInfo + "1.jpg");

				if (null != tempFile && tempFile.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(Consts.HEAD_PATH
							+ usernameInfo + Consts.IMAGE_JPG_KIND);
					Log.i("TAG", Consts.HEAD_PATH + usernameInfo
							+ Consts.IMAGE_JPG_KIND);
					more_head.setImageBitmap(bitmap);
					more_camera.setVisibility(View.GONE);
				}

				if ((strMail.equals("") || null == strMail)
						&& (strPhone.equals("") || null == strPhone)) {
					MySharedPreference.putBoolean("ISSHOW", true);
					onNotify(Consts.WHAT_BIND, 0, 0, null);
				}
				if (!strMail.equals("") && null != strMail) {
					hasbandEmail = strMail;
					MySharedPreference.putString("EMAIL", strMail);
				} else {
					hasbandEmail = "noemail";
				}
				if (!strPhone.equals("") && null != strPhone) {
					hasbandPhone = strPhone;
				} else {
					hasbandPhone = "nophone";
				}
				if (isgetemail) {
					Intent intentmore = new Intent(mActivity,
							JVRebandContactActivity.class);
					intentmore.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intentmore.putExtra("phone", hasbandPhone);
					intentmore.putExtra("email", hasbandEmail);
					intentmore.putExtra("nickname", hasnicknameString);
					intentmore.putExtra("username", usernameInfo);
					startActivity(intentmore);
				}
			} else {
				mActivity.showTextToast(R.string.str_video_load_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
		}
	}

	// 设置三种类型参数分别为String,Integer,String
	private class AlarmTask extends AsyncTask<Integer, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(Integer... params) {
			int switchRes = -1;
			if (JVAlarmConst.ALARM_ON == params[0]) {// 开报警
				switchRes = JVACCOUNT.SetCurrentAlarmFlag(
						JVAlarmConst.ALARM_ON, ConfigUtil.getIMEI(mActivity));
				if (0 == switchRes) {
					MyLog.e("JVAlarmConst.ALARM--ON-", switchRes + "");
					MySharedPreference
							.putBoolean(Consts.MORE_ALARMSWITCH, true);
				}
			} else {// 关报警
				switchRes = JVACCOUNT.SetCurrentAlarmFlag(
						JVAlarmConst.ALARM_OFF, ConfigUtil.getIMEI(mActivity));
				if (0 == switchRes) {
					MyLog.e("JVAlarmConst.ALARM--CLOSE-", switchRes + "");
					MySharedPreference.putBoolean(Consts.MORE_ALARMSWITCH,
							false);
				}
			}

			return switchRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			mActivity.dismissDialog();
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			mActivity.createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	// 注销线程
	class LogOutTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int logRes = -1;
			try {
				if (!localFlag) {
					MyLog.v(TAG, "start-logout");
					// if (0 != AccountUtil.userLogout()) {
					AccountUtil.userLogout();
					// }
					MyLog.v(TAG, "end-logout");
					MySharedPreference.putString(Consts.KEY_LAST_LOGIN_USER,
							more_name);
					MySharedPreference.putString(Consts.DEVICE_LIST, "");
					// 添加手动注销标志，离线报警使用，如果为手动注销账号，不接收离线报警
					MySharedPreference.putBoolean(Consts.MANUAL_LOGOUT_TAG,
							true);

				}

				UserUtil.resetAllUser();
				BitmapCache.getInstance().clearAllCache();
				mActivity.statusHashMap.put(Consts.HAS_LOAD_DEMO, "false");
				mActivity.statusHashMap.put(Consts.HAG_GOT_DEVICE, "false");
				mActivity.statusHashMap.put(Consts.ACCOUNT_ERROR, null);
				MySharedPreference.putString("USERINFO", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return logRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			((BaseActivity) mActivity).dismissDialog();
			((BaseActivity) mActivity).statusHashMap.put(Consts.MORE_BBS, null);
			((BaseActivity) mActivity).statusHashMap.put(Consts.MORE_STATURL,
					null);
			((BaseActivity) mActivity).statusHashMap.put(Consts.MORE_GCSURL,
					null);
			((BaseActivity) mActivity).statusHashMap.put(Consts.MORE_DEMOURL,
					null);
			MySharedPreference.putBoolean("ISSHOW", false);
			MySharedPreference.putString("ACCOUNT", "");
			MyActivityManager.getActivityManager().popAllActivityExceptOne(
					JVLoginActivity.class);
			Intent intent = new Intent();
			String userName = mActivity.statusHashMap.get(Consts.KEY_USERNAME);
			mActivity.statusHashMap.put(Consts.HAS_LOAD_DEMO, "false");

			clearCacheFolder(mActivity.getCacheDir(),
					System.currentTimeMillis());

			mActivity.deleteDatabase("webview.db");
			mActivity.deleteDatabase("webviewCache.db");

			intent.putExtra("UserName", userName);
			// MySharedPreference.putBoolean(Consts.MORE_REMEMBER, false);
			intent.setClass(mActivity, JVLoginActivity.class);
			mActivity.startActivity(intent);
			mActivity.finish();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			((BaseActivity) mActivity).createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if (littlenum < 20) {
			littlenum = 0;
		}
		super.onPause();
	}

	private int clearCacheFolder(File dir, long numDays) {

		int deletedFiles = 0;

		if (dir != null && dir.isDirectory()) {

			try {

				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {

						deletedFiles += clearCacheFolder(child, numDays);

					}

					if (child.lastModified() < numDays) {

						if (child.delete()) {

							deletedFiles++;

						}

					}

				}

			} catch (Exception e) {

				e.printStackTrace();

			}

		}

		return deletedFiles;

	}

	@Override
	public void onMainAction(int packet_type) {
		// TODO Auto-generated method stub

	}
}
