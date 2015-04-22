
package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;

import java.util.ArrayList;

public class FuntionAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<String> functionList = new ArrayList<String>();
    public int selectIndex = -1;
    private boolean bigScreen = false;
    private int playFlag;
    private boolean bFromAlerm;

    public FuntionAdapter(Context con, boolean flag, int playFlag) {
        mContext = con;
        bigScreen = flag;
        this.playFlag = playFlag;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<String> list) {
        functionList = list;
    }

    public void setFromAlerm(boolean bFromAlerm) {
        this.bFromAlerm = bFromAlerm;
    }

    @Override
    public int getCount() {
        return functionList.size();
    }

    @Override
    public Object getItem(int position) {
        return functionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.function_item, null);
            viewHolder = new ViewHolder();
            viewHolder.funcLayout = (RelativeLayout) convertView
                    .findViewById(R.id.funclayout);
            viewHolder.funtionImageView = (ImageView) convertView
                    .findViewById(R.id.funtion_image);
            viewHolder.funtionTitle1 = (TextView) convertView
                    .findViewById(R.id.funtion_titile1);
            viewHolder.funtionTitle2 = (TextView) convertView
                    .findViewById(R.id.funtion_titile2);
            viewHolder.funtionArrow = (ImageView) convertView
                    .findViewById(R.id.function_arrow);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // -----------------customize start--------------------
        // if (null != functionList && 0 != functionList.size()
        // && position < functionList.size()) {
        // viewHolder.funtionTitle1.setText(functionList.get(position));
        // }
        // -----------------customize end--------------------

        if (bigScreen) {// 大屏小dpi
            // -----------------customize start--------------------
            // 当前不再区别大小屏
            // 设置布局
            deployListLayoutByTag(viewHolder, functionList.get(position));
            // -----------------customize end--------------------

            if (selectIndex == position && selectIndex == 2) {
                // viewHolder.funtionImageView
                // .setImageResource(R.drawable.voice_monitor_small_2);
                // viewHolder.funcLayout
                // .setBackgroundResource(R.drawable.voice_hover_bg);
            }
            if (selectIndex == position && selectIndex == 1) {
                if (Consts.PLAY_AP == playFlag) {
                    viewHolder.funtionImageView
                            .setImageResource(R.drawable.apv_call_2);
                    viewHolder.funcLayout
                            .setBackgroundResource(R.drawable.talk_hover_bg);
                }
            }
            if (1 == position && selectIndex != position) {
                if (Consts.PLAY_AP == playFlag) {
                    viewHolder.funtionImageView
                            .setImageResource(R.drawable.apv_call_1);
                    viewHolder.funcLayout
                            .setBackgroundResource(R.drawable.talk_normal_bg);
                }
            }

        } else {
            if (2 == position) {
                if (Consts.PLAY_AP == playFlag) {
                    viewHolder.funcLayout.setVisibility(View.GONE);
                }
                viewHolder.funtionImageView
                        .setImageResource(R.drawable.voice_monitor_small_1);
                viewHolder.funcLayout
                        .setBackgroundResource(R.drawable.voice_normal_bg);
                viewHolder.funtionTitle2
                        .setText(R.string.str_audio_monitor_tips);
            } else if (0 == position) {
                viewHolder.funcLayout.setVisibility(View.VISIBLE);
                viewHolder.funtionImageView
                        .setImageResource(R.drawable.yt_controller);
                viewHolder.funtionTitle2.setText(R.string.str_yt_operate_tips);
            } else if (1 == position) {
                viewHolder.funcLayout.setVisibility(View.VISIBLE);
                viewHolder.funtionImageView
                        .setImageResource(R.drawable.remote_playback);
                viewHolder.funtionTitle2
                        .setText(R.string.str_remote_playback_tips);
            }
            if (selectIndex == position && selectIndex == 2) {
                // viewHolder.funtionImageView
                // .setImageResource(R.drawable.voice_monitor_small_2);
                // viewHolder.funcLayout
                // .setBackgroundResource(R.drawable.voice_hover_bg);
            }
        }
        if (bFromAlerm) {
            viewHolder.funcLayout
                    .setBackgroundResource(R.drawable.voice_normal_alermbg);
            viewHolder.funtionTitle1.setTextColor(mContext.getResources()
                    .getColor(R.color.more_fragment_color7));
        }
        return convertView;
    }

    class ViewHolder {
        RelativeLayout funcLayout;

        ImageView funtionImageView;
        TextView funtionTitle1;
        TextView funtionTitle2;
        ImageView funtionArrow;
    }

    // -------------------------------------------------
    // ## 根据不同的情况创建不同的布局
    // -------------------------------------------------
    private void deployListLayoutByTag(ViewHolder viewHolder, String pTag) {
        char tag = pTag.charAt(0);
        switch (tag) {
            case 'a':// 云台控制
                viewHolder.funtionTitle1.setText(R.string.str_yt_operate);
                viewHolder.funtionTitle2.setText(R.string.str_yt_operate_tips);
                viewHolder.funtionImageView.
                        setImageResource(R.drawable.yt_controller);
                viewHolder.funcLayout
                        .setBackgroundResource(R.drawable.yt_normal_bg);
                break;
            case 'b':// 远程回放
                viewHolder.funtionTitle1.setText(R.string.str_remote_playback);
                viewHolder.funtionTitle2.setText(R.string.str_remote_playback_tips);
                viewHolder.funcLayout
                        .setBackgroundResource(R.drawable.remote_normal_bg);
                viewHolder.funtionImageView
                        .setImageResource(R.drawable.remote_playback);
                break;
            case 'c':// 设备设置
                viewHolder.funtionTitle1.setText(R.string.str_audio_monitor);
                viewHolder.funtionTitle2.setText(R.string.str_audio_monitor_tips);
                viewHolder.funcLayout
                        .setBackgroundResource(R.drawable.voice_normal_bg);
                viewHolder.funtionImageView
                        .setImageResource(R.drawable.voice_monitor_small_1);
                break;
            case 'd':// 语音对讲
                viewHolder.funtionArrow.setVisibility(View.GONE);

                viewHolder.funtionTitle1.setText(R.string.voice_call_ap);
                viewHolder.funtionTitle2.setText(R.string.voice_call_ap_tips);
                viewHolder.funtionImageView
                        .setImageResource(R.drawable.apv_call);
                viewHolder.funcLayout
                        .setBackgroundResource(R.drawable.talk_normal_bg);
                break;
            case 'e':// 分享链接//TODO
                viewHolder.funtionTitle1.setText(R.string.str_share_link);
                viewHolder.funtionTitle2.setText(R.string.str_share_link_tips);
                viewHolder.funtionImageView
                        .setImageResource(R.drawable.function_share_link);
                viewHolder.funcLayout
                        .setBackgroundResource(R.drawable.share_link_normal_bg);
                break;
            case 'f':
                break;
            default:

        }
    }

}
