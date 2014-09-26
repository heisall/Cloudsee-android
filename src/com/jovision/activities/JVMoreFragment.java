package com.jovision.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.adapters.FragmentAdapter;
import com.jovision.bean.MoreFragmentBean;
import com.jovision.commons.CheckUpdateTask;
import com.jovision.commons.MyActivityManager;
import com.jovision.utils.ListViewUtil;

/**
 * 更多
 */
public class JVMoreFragment extends BaseFragment {
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
	// 最后一次登录时间
	private TextView more_lasttime;
	// 图片数组
	private int[] Image = { R.drawable.more_help_img,
			R.drawable.more_usermessage_img, R.drawable.more_feature_img,
			R.drawable.more_vibrator_img, R.drawable.more_about_img,
			R.drawable.more_suggesr_img, R.drawable.more_remark_img };
	// 功能名称数组
	private String[] name;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_more, container, false);
		intiUi(view);
		listViewClick();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mActivity = (BaseActivity) getActivity();
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	private void intiUi(View view) {
		activity = getActivity();
		name = activity.getResources().getStringArray(R.array.name);
		initDatalist();
		more_cancle = (RelativeLayout) view.findViewById(R.id.more_cancle);
		more_modify = (TextView) view.findViewById(R.id.more_modify);
		more_findpassword = (TextView) view
				.findViewById(R.id.more_findpassword);
		more_username = (TextView) view.findViewById(R.id.more_uesrname);
		more_lasttime = (TextView) view.findViewById(R.id.more_lasttime);
		more_head = (ImageView) view.findViewById(R.id.more_head_img);

		more_listView = (ListView) view.findViewById(R.id.more_listView);
		adapter = new FragmentAdapter(activity, dataList);
		more_listView.setAdapter(adapter);
		ListViewUtil.setListViewHeightBasedOnChildren(more_listView);

		more_cancle.setOnClickListener(myOnClickListener);
		more_modify.setOnClickListener(myOnClickListener);
		more_findpassword.setOnClickListener(myOnClickListener);

	}

	private void initDatalist() {
		dataList = new ArrayList<MoreFragmentBean>();
		for (int i = 0; i < Image.length; i++) {
			MoreFragmentBean bean = new MoreFragmentBean();
			bean.setItem_img(Image[i]);
			bean.setName(name[i]);
			if (i == 0 || i == 2 || i == 6) {
				bean.setIsnew(true);
			} else {
				bean.setIsnew(false);
			}
			dataList.add(bean);
		}
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.more_cancle:// 注销

				MyActivityManager.getActivityManager().popAllActivityExceptOne(
						JVLoginActivity.class);
				Intent intent = new Intent();
				intent.setClass(mActivity, JVLoginActivity.class);
				mActivity.startActivity(intent);
				mActivity.finish();
				break;
			case R.id.more_modify:

				break;
			case R.id.more_findpassword:

				break;
			default:
				break;

			}
		}
	};

	private void listViewClick() {
		more_listView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						switch (position) {
						case 0:
							Toast.makeText(activity, "点击成功0",
									Toast.LENGTH_SHORT).show();
							break;
						case 1:
							Toast.makeText(activity, "点击成功1",
									Toast.LENGTH_SHORT).show();
							break;
						case 2:
							Toast.makeText(activity, "点击成功2",
									Toast.LENGTH_SHORT).show();
							break;
						case 3:
							Toast.makeText(activity, "点击成功3",
									Toast.LENGTH_SHORT).show();
							break;
						case 4:
							Toast.makeText(activity, "点击成功4",
									Toast.LENGTH_SHORT).show();
							break;
						case 5:
							activity.startActivity(new Intent(activity,
									JVFeedbackActivity.class));
							break;
						case 6:
							Toast.makeText(activity, "点击成功6",
									Toast.LENGTH_SHORT).show();
							CheckUpdateTask task = new CheckUpdateTask(
									mActivity);
							String[] strParams = new String[3];
							strParams[0] = "1";// 0,手动检查更新
							task.execute(strParams);
							break;
						default:
							break;
						}
					}
				});
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
	}
}
