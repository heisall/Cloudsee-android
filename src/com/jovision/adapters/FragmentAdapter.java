
package com.jovision.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.BaseFragment;
import com.jovision.bean.MoreFragmentBean;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends BaseAdapter {
    private BaseFragment mfragment;
    private List<MoreFragmentBean> dataList;
    private boolean localFlag;
    private ImageView item_img;
    private TextView name;
    private ImageView item_next;
    private TextView item_version;
    private RelativeLayout more_relative;
    private FrameLayout more_item;
    private ImageView divider_img;
    private RelativeLayout item_new;
    private TextView tv_new_nums;
    private LinearLayout kongbai;
    private ImageView divider_img_up;

    private int new_nums_;
    private int new_bbsnums_;

    // private boolean showGCS = false;// 是否显示工程

    public FragmentAdapter(BaseFragment mfragment,
            ArrayList<MoreFragmentBean> dataList) {
        this.mfragment = mfragment;
        this.dataList = dataList;
    }

    // public void setShowGCS(boolean show) {
    // this.showGCS = show;
    // }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setNewNums(int nums) {
        this.new_nums_ = nums;
    }

    public void setBBSNums(int nums) {
        this.new_bbsnums_ = nums;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mfragment.getActivity()).inflate(
                R.layout.fragment_more_item, null);
        more_relative = (RelativeLayout) convertView
                .findViewById(R.id.more_relative);
        item_new = (RelativeLayout) convertView
                .findViewById(R.id.item_new_layout);
        divider_img_up = (ImageView) convertView
                .findViewById(R.id.divider_img_up);
        kongbai = (LinearLayout) convertView.findViewById(R.id.kongbai);
        more_item = (FrameLayout) convertView.findViewById(R.id.item);
        divider_img = (ImageView) convertView.findViewById(R.id.divider_img);
        item_img = (ImageView) convertView.findViewById(R.id.item_img);
        item_next = (ImageView) convertView.findViewById(R.id.item_next);
        name = (TextView) convertView.findViewById(R.id.item_name);
        item_version = (TextView) convertView.findViewById(R.id.item_version);
        localFlag = Boolean
                .valueOf(((BaseActivity) mfragment.getActivity()).statusHashMap
                        .get(Consts.LOCAL_LOGIN));
        item_img.setBackgroundResource(dataList.get(position).getItem_img());
        name.setText(dataList.get(position).getName());
        tv_new_nums = (TextView) convertView
                .findViewById(R.id.tv_item_new_nums);

        /** 设置隐藏 **/
        if (dataList.get(position).isDismiss()) {
            more_item.setVisibility(View.GONE);
            divider_img.setVisibility(View.GONE);
        } else {
            more_item.setVisibility(View.VISIBLE);
            divider_img.setVisibility(View.VISIBLE);
        }

        /** 显示白框 **/
        if (dataList.get(position).isShowWhiteBlock()) {
            kongbai.setVisibility(View.VISIBLE);
            divider_img_up.setVisibility(View.VISIBLE);
        }

        /** 显示右侧圆形按钮 **/
        if (dataList.get(position).isShowRightCircleBtn()) {
            if (MySharedPreference.getBoolean(dataList.get(position)
                    .getItemFlag(), false)) {
                item_next
                        .setBackgroundResource(R.drawable.morefragment_selector_icon);
            } else {
                item_next
                        .setBackgroundResource(R.drawable.morefragment_normal_icon);
            }
        }

        /** 是否显示”新“ **/
        if (dataList.get(position).isIsnew()) {
            if (!MySharedPreference.getBoolean(dataList.get(position)
                    .getItemFlag())) {
                tv_new_nums.setText(R.string.new_tag);
                item_new.setVisibility(View.VISIBLE);
            }
        }
        /** 显示白框 **/
        if (dataList.get(position).isShowWhiteBlock()) {
            kongbai.setVisibility(View.VISIBLE);
            divider_img_up.setVisibility(View.VISIBLE);
        }
        /** 显示信息数目 **/
        if (dataList.get(position).isShowTVNews()) {
            if (!localFlag) {
                if (new_nums_ > 0) {
                    tv_new_nums.setText(String.valueOf(new_nums_));
                    item_new.setVisibility(View.VISIBLE);
                } else {
                    tv_new_nums.setText("0");
                    item_new.setVisibility(View.INVISIBLE);
                }
            } else {
                item_new.setVisibility(View.INVISIBLE);
            }
        }
        /** 显示论坛信息数目 **/
        if (dataList.get(position).isShowBBSNews()) {
            if (new_bbsnums_ > 0) {
                tv_new_nums.setText(String.valueOf(new_bbsnums_));
                item_new.setVisibility(View.VISIBLE);
            } else {
                tv_new_nums.setText("0");
                item_new.setVisibility(View.INVISIBLE);
            }
        }
        /** 显示版本信息 **/
        if (dataList.get(position).isShowVersion()) {
            item_next.setVisibility(View.GONE);
            item_version.setVisibility(View.VISIBLE);
            item_version
                    .setText(ConfigUtil.getVersion(mfragment.getActivity()));
        }

        return convertView;
    }
}
