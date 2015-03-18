package com.jovision.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.adapters.MediaSelectorAdapter;
import com.jovision.utils.BitmapCache;

public class JVMediaActivity extends BaseActivity {

	/** topBar **/
	private ListView mediaListView;
	private MediaSelectorAdapter msAdapter;
	private ArrayList<String> mediaList = new ArrayList<String>();

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {
		if (null == mediaList) {
			mediaList = new ArrayList<String>();
		}
		mediaList.clear();
		mediaList.add(getResources().getString(R.string.media_image));
		mediaList.add(getResources().getString(R.string.media_video));
		mediaList.add(getResources().getString(R.string.media_downvideo));
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.mediaselector_layout);

		/** topBar **/
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.media);
		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);

		mediaListView = (ListView) findViewById(R.id.medialistview);
		msAdapter = new MediaSelectorAdapter(JVMediaActivity.this);
		msAdapter.setData(mediaList);
		mediaListView.setAdapter(msAdapter);
		mediaListView.setOnItemClickListener(mOnItemClickListener);
		JVMediaListActivity.fileMap.clear();
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.btn_left: {
				JVMediaActivity.this.finish();
				break;
			}
			}
		}

	};

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// createDialog("", false);
			Intent mediaListIntent = new Intent();
			mediaListIntent.setClass(JVMediaActivity.this,
					JVMediaListActivity.class);
			switch (arg2) {
			case 0: {
				mediaListIntent.putExtra("Media", "image");
				break;
			}
			case 1: {
				mediaListIntent.putExtra("Media", "video");
				break;
			}
			case 2: {
				mediaListIntent.putExtra("Media", "downVideo");
				break;
			}
			}
			JVMediaActivity.this.startActivity(mediaListIntent);
		}

	};

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {
		// dismissDialog();
		BitmapCache.getInstance().clearCache();
	}

}
