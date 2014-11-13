package com.jovision.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.MediaFolderAdapter;
import com.jovision.utils.BitmapCache;

public class JVMediaListActivity extends BaseActivity {

	private static final int LOAD_IMAGE_SUCCESS = 0x01;
	private static final int LOAD_IMAGE_FINISHED = 0x02;
	private static final int FILE_LOAD_SUCCESS = 0x03;

	private String media = "";
	private String mediaPath = "";
	private boolean noFile = true;
	private ArrayList<File> fileList = new ArrayList<File>();
	private MediaFolderAdapter mfAdapter;

	/** topBar **/
	private Button back;// 左侧返回按钮
	private TextView currentMenu;// 当前页面名称
	private Button rigButton;

	private LinearLayout fileLayout;
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
			if (noFile) {
				noFileLayout.setVisibility(View.VISIBLE);
				fileLayout.setVisibility(View.GONE);
			} else {
				noFileLayout.setVisibility(View.GONE);
				fileLayout.setVisibility(View.VISIBLE);
				mfAdapter.setLoadImage(false);
				mfAdapter.setData(media, fileList);
				fileListView.setAdapter(mfAdapter);
			}
			break;
		}
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

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
		currentMenu = (TextView) findViewById(R.id.currentmenu);

		if ("image".equalsIgnoreCase(media)) {
			currentMenu.setText(R.string.media_image);
		} else if ("video".equalsIgnoreCase(media)) {
			currentMenu.setText(R.string.media_video);
		}

		back.setOnClickListener(myOnClickListener);
		rigButton = (Button) findViewById(R.id.btn_right);
		rigButton.setVisibility(View.GONE);

		fileLayout = (LinearLayout) findViewById(R.id.filelayout);
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
			case R.id.btn_left: {
				JVMediaListActivity.this.finish();
				break;
			}
			}

		}

	};

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
						fileList.add(fileArray[i]);
					}
					noFile = false;
				}
			}

			Collections.sort(fileList, comparator);

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
				handler.sendMessage(handler.obtainMessage(LOAD_IMAGE_FINISHED));
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
