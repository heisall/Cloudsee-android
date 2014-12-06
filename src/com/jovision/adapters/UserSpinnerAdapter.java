package com.jovision.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.JVLoginActivity;
import com.jovision.bean.User;

public class UserSpinnerAdapter extends BaseAdapter {
	private BaseActivity activity;
	private List<User> list;
	private LayoutInflater mInflater;
	private String userName;

	/**
	 * 自定义构造方法
	 * 
	 * @param activity
	 * @param handler
	 * @param list
	 */
	public UserSpinnerAdapter(BaseActivity activity) {
		this.activity = activity;
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(List<User> aList) {
		this.list = aList;
	}

	public void setName(String name) {
		userName = name;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			// 下拉项布局
			convertView = mInflater.inflate(R.layout.user_item, null);
			holder.pop_relative = (RelativeLayout) convertView
					.findViewById(R.id.pop_relative);
			holder.userIcon = (ImageView) convertView
					.findViewById(R.id.usericon);
			holder.textView = (TextView) convertView
					.findViewById(R.id.otherusername);
			holder.delImageView = (ImageView) convertView
					.findViewById(R.id.otheruser_del);
			holder.dividerImage = (ImageView) convertView
					.findViewById(R.id.useritemdivider);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final User info = list.get(position);
		holder.textView.setText(info.getUserName());

		if (info.getUserName().equalsIgnoreCase(userName)) {
			holder.userIcon.setVisibility(View.INVISIBLE);
			holder.textView.setTextColor(activity.getResources().getColor(
					R.color.userinfocolor2));
		} else {
			holder.userIcon.setVisibility(View.INVISIBLE);
			holder.textView.setTextColor(activity.getResources().getColor(
					R.color.userinfocolor));
		}
		holder.pop_relative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.onNotify(JVLoginActivity.SELECT_USER, position, 0,
						list.get(position));
				holder.textView.setText(list.get(position).getUserName());
				userName = list.get(position).getUserName();
				notifyDataSetChanged();
			}
		});
		holder.delImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.onNotify(JVLoginActivity.DELECT_USER, position, 0,
						null);
				list.remove(position);
				notifyDataSetChanged();
			}
		});
		//
		// // 为下拉框选项文字部分设置事件，最终效果是点击将其文字填充到文本框
		// holder.textView.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// userNameEditText.setText(info.getUserName());
		// passwordEditText.setText(info.getUserPwd());
		// if (pop.isShowing()) {
		// pop.dismiss();
		// }
		// }
		// });
		//
		// // 为下拉框选项删除图标部分设置事件，最终效果是点击将该选项删除
		// holder.imageView.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // DeleAlert(info,position);
		// int res = BaseApp.deleteUser(info);
		// if (res > 0) {
		// userList.remove(info);
		// }
		// // 删除的用户名在输入框中
		// if (info.getUserName().equalsIgnoreCase(
		// userNameEditText.getText().toString())) {
		// if (userList.size() > 0) {
		// userNameEditText.setText(userList.get(0).getUserName());
		// passwordEditText.setText(userList.get(0).getUserPwd());
		// } else {
		// userNameEditText.setText("");
		// passwordEditText.setText("");
		// }
		//
		// }
		//
		// Message msg = loginHandler.obtainMessage();
		// msg.what = JVAccountConst.LOGIN_USER_REFRESH;
		// loginHandler.sendMessage(msg);
		// }
		// });
		holder.pop_relative.setBackgroundDrawable(activity.getResources()
				.getDrawable(R.drawable.pop_bg));
		if (position==list.size()-1) {
			holder.dividerImage.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView userIcon;
		TextView textView;
		ImageView delImageView;
		ImageView dividerImage;
		RelativeLayout pop_relative;
	}
}
