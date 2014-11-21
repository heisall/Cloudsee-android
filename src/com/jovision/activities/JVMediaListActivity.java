package com.jovision.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.MediaFolderAdapter;
import com.jovision.utils.BitmapCache;

public class JVMediaListActivity extends BaseActivity {

	private static final int LOAD_IMAGE_SUCCESS = 0x01;
	private static final int LOAD_IMAGE_FINISHED = 0x02;
	private static final int FILE_LOAD_SUCCESS = 0x03;
	private static final int FILE_NUM = 0x04;

	private String media = "";
	private String mediaPath = "";
	private boolean noFile = true;
	private ArrayList<File> fileList = new ArrayList<File>();
	private MediaFolderAdapter mfAdapter;

	/** topBar **/
	private Button back;// 左侧返回按钮
	private TextView currentMenu;// 当前页面名称
	private Button rigButton;// 选择删除按钮
	private boolean isdelect = true;
	private boolean isselectall;
	private RelativeLayout fileBottom;
	private TextView fileCompleted;
	private TextView fileCancel;
	private TextView fileSelectNum;
	private TextView fileNumber;
	private ImageView fileSlectAll;
	public static int fileSum = 0;
	public static int fileSelectSum = 0;
	private String selectNum;
	private String totalNum;
	public static ArrayList<String> delectlist = new ArrayList<String>();

	private RelativeLayout fileLayout;
	private ListView fileListView;
	private LinearLayout noFileLayout;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case LOAD_IMAGE_SUCCESS: {
			// mfAdapter.setData(media, fileList,true);
			// mfAdapter.notifyDataSetChanged();
			break;
		}
		case LOAD_IMAGE_FINISHED: {
			mfAdapter.setLoadImage(true);
			mfAdapter.notifyDataSetChanged();
			dismissDialog();
			break;
		}
		case FILE_LOAD_SUCCESS: {
			dismissDialog();
			if (noFile) {
				noFileLayout.setVisibility(View.VISIBLE);
				fileLayout.setVisibility(View.GONE);
			} else {
				noFileLayout.setVisibility(View.GONE);
				fileLayout.setVisibility(View.VISIBLE);
				mfAdapter.setLoadImage(false);
				mfAdapter.setData(media, fileList, isdelect, isselectall);
				fileListView.setAdapter(mfAdapter);
			}
			break;
		}
		case FILE_NUM:
			fileSelectNum.setText(selectNum.replace("?", String.valueOf(0)));
			fileNumber.setText(totalNum.replace("?", String.valueOf(arg1)));
			break;
		case 1222:
			fileSelectNum.setText(selectNum.replace("?", String.valueOf(arg1)));
			if (arg2 == 1) {
				fileSlectAll
						.setBackgroundResource(R.drawable.morefragment_selector_icon);
			} else if (arg2 == 0) {
				fileSlectAll
						.setBackgroundResource(R.drawable.morefragment_normal_icon);
			}
			break;
		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		Intent intent = getIntent();
		media = intent.getStringExtra("Media");
		if ("image".equalsIgnoreCase(media)) {
			mediaPath = Consts.CAPTURE_PATH;
		} else if ("video".equalsIgnoreCase(media)) {
			mediaPath = Consts.VIDEO_PATH;
		}
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.medialist_layout);

		/** topBar **/
		back = (Button) findViewById(R.id.btn_left);
		selectNum = JVMediaListActivity.this.getResources().getString(
				R.string.selectnum);
		totalNum = JVMediaListActivity.this.getResources().getString(
				R.string.number);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		fileBottom = (RelativeLayout) findViewById(R.id.file_bottom);
		fileCompleted = (TextView) findViewById(R.id.file_completed);
		fileCancel = (TextView) findViewById(R.id.file_cancel);
		fileSelectNum = (TextView) findViewById(R.id.file_selectnum);
		fileNumber = (TextView) findViewById(R.id.file_number);
		fileSlectAll = (ImageView) findViewById(R.id.file_selectall);

		if ("image".equalsIgnoreCase(media)) {
			currentMenu.setText(R.string.media_image);
		} else if ("video".equalsIgnoreCase(media)) {
			currentMenu.setText(R.string.media_video);
		}
		rigButton = (Button) findViewById(R.id.btn_right);
		rigButton.setBackgroundResource(R.drawable.mydevice_cancleedit_icon);
		back.setOnClickListener(myOnClickListener);
		rigButton.setOnClickListener(myOnClickListener);
		fileCompleted.setOnClickListener(myOnClickListener);
		fileCancel.setOnClickListener(myOnClickListener);
		fileSlectAll.setOnClickListener(myOnClickListener);

		fileLayout = (RelativeLayout) findViewById(R.id.filelayout);
		noFileLayout = (LinearLayout) findViewById(R.id.nofilelayout);
		fileListView = (ListView) findViewById(R.id.filelistview);
		mfAdapter = new MediaFolderAdapter(JVMediaListActivity.this);
		createDialog("");
		LoadImageThread loadThread = new LoadImageThread();
		loadThread.start();
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.btn_right: {
				if (isdelect) {
					fileBottom.setVisibility(View.VISIBLE);
					isdelect = !isdelect;
					mfAdapter.setData(media, fileList, isdelect, isselectall);
					mfAdapter.notifyDataSetChanged();
					rigButton.setBackgroundResource(R.drawable.close_btn);
				} else {
					fileBottom.setVisibility(View.GONE);
					isdelect = !isdelect;
					mfAdapter.setData(media, fileList, isdelect, false);
					mfAdapter.notifyDataSetChanged();
					rigButton
							.setBackgroundResource(R.drawable.mydevice_cancleedit_icon);
				}
			}
				break;
			case R.id.btn_left: {
				JVMediaListActivity.this.finish();
				fileSum = 0;
				break;
			}
			case R.id.file_selectall:
				if (!isselectall) {
					fileSlectAll
							.setBackgroundResource(R.drawable.morefragment_selector_icon);
					isselectall = true;
				} else {
					fileSlectAll
							.setBackgroundResource(R.drawable.morefragment_normal_icon);
					isselectall = false;
				}
				mfAdapter.setData(media, fileList, isdelect, isselectall);
				mfAdapter.notifyDataSetChanged();
				break;
			case R.id.file_cancel:
				fileBottom.setVisibility(View.GONE);
				isdelect = !isdelect;
				rigButton
						.setBackgroundResource(R.drawable.mydevice_cancleedit_icon);
				mfAdapter.setData(media, fileList, isdelect, false);
				mfAdapter.notifyDataSetChanged();
				break;
			case R.id.file_completed:
				if (JVMediaListActivity.delectlist.size() != 0) {
					for (int i = 0; i < JVMediaListActivity.delectlist.size(); i++) {
						File file = new File(
								JVMediaListActivity.delectlist.get(i));
						file.delete();
					}
					File file = new File(mediaPath);
					if (file.exists()) {
						File[] fileArray = file.listFiles();
						for (int j = 0; j < fileArray.length; j++) {
							Log.i("TAG", fileArray[j].getAbsolutePath());
							if (fileArray[j].list().length == 0) {
								delete(fileArray[j]);
							}
						}
					}
					fileBottom.setVisibility(View.GONE);
					fileSlectAll
							.setBackgroundResource(R.drawable.morefragment_normal_icon);
					rigButton
							.setBackgroundResource(R.drawable.mydevice_cancleedit_icon);
					fileList.clear();
					delectlist.clear();
					fileSum = 0;
					LoadImageThread loadThread = new LoadImageThread();
					loadThread.start();
					isdelect = true;
					if (file.list().length == 0) {
						noFile = true;
						noFileLayout.setVisibility(View.VISIBLE);
						fileLayout.setVisibility(View.GONE);
					}
					mfAdapter.setData(media, fileList, true, false);
					mfAdapter.notifyDataSetChanged();
				} else {
					JVMediaListActivity.this.showTextToast("请选择要删除的文件");
				}
				break;
			}
		}
	};

	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	private class LoadImageThread extends Thread {

		@Override
		public void run() {
			File file = new File(mediaPath);
			if (file.exists()) {
				File[] fileArray = file.listFiles();
				if (null != fileArray && 0 != fileArray.length) {
					int length = fileArray.length;
					for (int i = 0; i < length; i++) {
						Log.i("TAG", fileArray[i].getAbsolutePath());
						if (fileArray[i].isDirectory()) {
							fileList.add(fileArray[i]);
							fileSum = fileSum + fileArray[i].list().length;
							Log.i("TAG", fileSum + "文件数量"
									+ fileArray[i].list().length);
						}
					}
					if (0 != fileList.size()) {
						noFile = false;
					}
				}
			}

			if (noFile) {
				handler.sendMessage(handler.obtainMessage(FILE_LOAD_SUCCESS));
			} else {
				Collections.sort(fileList, comparator);
				Message msg = new Message();
				msg.arg1 = fileSum;
				msg.what = FILE_NUM;
				handler.sendMessage(msg);
				handler.sendMessage(handler.obtainMessage(FILE_LOAD_SUCCESS));
				if (null != fileList && 0 != fileList.size()) {
					int size = fileList.size();
					for (int i = 0; i < size; i++) {
						handler.sendMessage(handler
								.obtainMessage(LOAD_IMAGE_SUCCESS));
						File[] fileArray = fileList.get(i).listFiles();
						if (null != fileArray && 0 != fileArray.length) {
							int length = fileArray.length;
							for (int j = 0; j < length; j++) {
								BitmapCache.getInstance().getBitmap(
										fileArray[j].getAbsolutePath(), media);
							}
						}
					}
					handler.sendMessage(handler
							.obtainMessage(LOAD_IMAGE_FINISHED));
				}
			}

			super.run();
		}

	}

	// 文件按时间倒序排序
	static Comparator<File> comparator = new Comparator<File>() {
		public int compare(File f1, File f2) {
			if (f1 == null || f2 == null) {// 先比较null
				if (f1 == null) {
					{
						return -1;
					}
				} else {
					return 1;
				}
			} else {
				if (f1.isDirectory() == true && f2.isDirectory() == true) { // 再比较文件夹
					return -f1.getName().compareToIgnoreCase(f2.getName());
				} else {
					if ((f1.isDirectory() && !f2.isDirectory()) == true) {
						return -1;
					} else if ((f2.isDirectory() && !f1.isDirectory()) == true) {
						return 1;
					} else {
						return -f1.getName().compareToIgnoreCase(f2.getName());// 最后比较文件
					}
				}
			}
		}
	};

}
