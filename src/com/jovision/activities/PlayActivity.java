
package com.jovision.activities;

import android.app.ActionBar.LayoutParams;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.FuntionAdapter;
import com.jovision.adapters.MyPagerAdp;
import com.jovision.adapters.ScreenAdapter;
import com.jovision.adapters.StreamAdapter;
import com.jovision.bean.Channel;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyAudio;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.PlayUtil;
import com.jovision.views.MyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class PlayActivity extends BaseActivity implements
        OnPageChangeListener {
    private static final String TAG = "PlayActivity";
    public static MyAudio playAudio;

    public MediaPlayer mediaPlayer = new MediaPlayer();

    protected boolean bigScreen = false;// 大小屏标识

    protected boolean isOmx = true;
    protected Boolean lowerSystem = false;// 低于4.1的系统
    protected boolean realStop = false;

    /** 播放相关 */
    protected RelativeLayout.LayoutParams reParamsV;
    protected RelativeLayout.LayoutParams reParamsH;
    // protected RelativeLayout.LayoutParams reParamsyt;
    protected int surfaceWidth = -1;
    protected int surfaceHeight = -1;
    protected int playFlag = -1;
    protected long startRecordTime = 0;// 开始录像时间
    protected String recordingPath = "";// 正在录像文件路径

    /** layout 上 */
    protected LinearLayout topBar;// 顶部标题栏
    protected RelativeLayout ishitvis;// 惠通闪光灯
    protected ImageView ht_motion;// 移动侦测
    protected ImageView ht_fight;// 闪光灯
    protected ImageView selectScreenNum;// 下拉选择当前分屏数按钮
    protected PopupWindow screenPopWindow;// 选择框
    protected ScreenAdapter screenAdapter;
    protected ListView screenListView;
    protected ArrayList<Integer> screenList = new ArrayList<Integer>();// 分屏下拉选择列表

    /** layout 中 */
    protected MyViewPager playViewPager;
    protected SurfaceView playSurface;
    protected TextView linkMode;// 测试显示连接方式

    protected RelativeLayout varvoice_bg;
    protected ImageView varvoice;
    protected RelativeLayout playBackBar;// 远程回放工具条
    protected SeekBar progressBar;// 远程回放进度
    protected Button playBackPause;// 远程回放暂停继续播
    protected Button voiceListener;// 音频监听
    protected ImageView playBackFullScreen;// 远程回放全屏按钮
    protected ImageView fullScreen;// 视频播放全屏按钮
    protected RelativeLayout.LayoutParams reParamstop2;

    /** 　竖屏播放工具bar　 */
    protected RelativeLayout verPlayBarLayout;

    /** 　横屏播放工具bar　 */
    protected RelativeLayout horPlayBarLayout;

    /** 远程回放连接状态 */
    protected TextView linkState;// 连接文字
    protected ProgressBar loading;// 加载进度

    // protected Button audioMonitor;// 音频监听
    // protected Button ytOperate;// 云台
    // protected Button remotePlayback;// 远程回放
    protected LinearLayout ytLayout;// 云台布局
    // protected LinearLayout playFuctionLayout;// 小分辨率时功能界面

    protected LinearLayout function; // 显示远程回放等功能
    protected RelativeLayout talk_eachother; // 显示对讲页面
    protected ImageView talk_img;// 对讲图片
    protected ImageView talk_cancel;// 取消对讲
    protected ImageView talk_img_down;// 语音外圈

    /**
     * 横屏对讲
     */
    protected RelativeLayout horfunc_talk;
    protected ImageView horfunc_talk_normal;
    protected ImageView horfunc_talk_down;

    protected ListView playFunctionList;// 大分辨率时功能列表
    protected FuntionAdapter functionListAdapter;
    protected ArrayList<String> functionList = new ArrayList<String>();

    /** 云台操作 */
    protected ImageView autoimage, zoomIn, zoomout, scaleSmallImage,
            scaleAddImage, upArrow, downArrow, leftArrow, rightArrow,
            yt_cancle;

    /***** 2015.4.3 云台速度调整 *******/
    // protected EditText ytSpeed;// 云台速度
    // protected Button ysSpeedSet;// 云台速度调整按钮
    protected RelativeLayout ytSeekLayout;// 云台速度布局
    protected SeekBar ytSeekBar;// 云台速度seekbar
    protected TextView ytSpeed;// 当前云台速度

    /** layout 下 */
    protected Button capture;// 抓拍
    protected Button voiceCall;// 喊话
    protected Button videoTape;// 录像
    protected Button moreFeature;// 更多
    protected LinearLayout footerBar;// 底部工具栏
    protected LinearLayout apFuncLayout;// 底部Ap下一步按钮
    protected Button nextStep;// 下一步

    protected Boolean recoding = false;

    /** 录像按钮 */
    protected Drawable videoTapeTop1 = null;
    protected Drawable videoTapeTop2 = null;
    /** 对讲按钮 */
    protected Drawable voiceCallTop1 = null;
    protected Drawable voiceCallTop2 = null;

    // protected PlayAudio playAudio;// 音频监听
    // protected LinkedBlockingQueue<byte[]> audioQueue;

    // protected MICRecorder recorder;// 音频采集

    protected Button left_btn_h;// 横屏返回键
    protected TextView currentMenu_h;//
    protected Button right_btn_h;// 横屏手动录像，报警录像键
    protected RelativeLayout topBarH;// 横屏topbar

    protected Button bottombut1;// 视频播放和暂停
    protected Button bottombut2;// 软硬解
    protected Button bottombut3;// 抓拍
    protected Button bottombut4;// 远程回放
    protected Button bottombut5;// 对讲
    protected Button bottombut6;// 视频翻转
    protected Button bottombut7;// 录像
    protected Button bottombut8;// 音频监听
    protected ImageView notFullScreen;// 非全屏按钮

    protected TextView bottomStream;
    protected boolean bottomboolean1;
    private LinearLayout linear;
    private RelativeLayout relative1;
    private RelativeLayout relative2;
    private RelativeLayout relative3;
    private RelativeLayout relative4;
    private RelativeLayout relative5;
    private RelativeLayout relative6;
    private RelativeLayout relative7;
    private RelativeLayout relative8;

    /** IPC独有特性 */
    protected Button decodeBtn;// 软硬解
    protected Button videTurnBtn;// 视频翻转
    protected Button currentKbps;// 当前统计
    protected TextView playStatistics;// 播放统计

    /**
     * 帮助界面
     */
    protected RelativeLayout playHelp;
    private ViewPager helpViewPager;

    // 当前页面索引
    private int currentImage = 0;
    // 前一个页面索引
    private int oldImage = 0;
    // 点集合
    private List<ImageView> dots;
    private LinearLayout ll_dot;
    private MyPagerAdp adp;
    private List<View> pics;
    int flag = 0;
    private int currentIndex = 0;// 当前页卡index

    private LinearLayout autoRelative;
    protected LinearLayout.LayoutParams Params;

    /**
     * 横屏帮助图
     */
    protected RelativeLayout horPlayHelp;
    private ViewPager horViewPager;
    private MyPagerAdp horadp;
    private List<View> horpics;
    /**  */
    protected TextView currentMenu_v;

    // 录像模式----rightFuncButton
    // 码流切换----moreFeature
    protected String[] streamArray;
    protected ListView streamListView;// 码流listview
    protected StreamAdapter streamAdapter;// 码流adapter
    protected RelativeLayout voiceTip;// 单向对讲提示

    // protected static boolean CAPTURING = false;// 是否正在抓拍
    public static boolean AUDIO_SINGLE = false;// 单向对讲标志
    public static boolean VOICECALL_LONG_CLICK = false;// 语音喊话flag长按状态,长按发送数据
    public static boolean VOICECALLING = false;// 对讲功能已经开启
    public static boolean GATHER_AUDIO_DATA = true;// 是否采集音频数据

    // 按钮图
    protected Drawable alarmRecordDrawableTop = null;
    protected Drawable normalRecordDrawableTop = null;

    // protected RelativeLayout mainfunctionLayout;

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {

    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

    }

    @Override
    protected void initSettings() {
    }

    @Override
    protected void initUi() {
        PlayUtil.setContext(PlayActivity.this);
        // [Neo] TODO
        playAudio = MyAudio.getIntance(Consts.PLAY_AUDIO_WHAT,
                PlayActivity.this, 8000);

        setContentView(R.layout.play_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 屏幕常亮

        /** 帮助图 */
        playHelp = (RelativeLayout) findViewById(R.id.playhelp);
        if (MySharedPreference.getBoolean("playhelp1")) {
            playHelp.setVisibility(View.GONE);
        } else {
            playHelp.setVisibility(View.VISIBLE);
        }
        helpViewPager = (ViewPager) findViewById(R.id.playhelp_viewpager);
        ll_dot = (LinearLayout) findViewById(R.id.play_ll_dot);
        helpViewPager.setOnPageChangeListener(PlayActivity.this);
        ll_dot.setVisibility(View.VISIBLE);
        helpViewPager.setCurrentItem(0);
        helpViewPager.setVisibility(View.VISIBLE);
        getPic();
        adp = new MyPagerAdp(pics);
        helpViewPager.setAdapter(adp);
        /** 上 */
        topBar = (LinearLayout) findViewById(R.id.top_bar);// 顶部标题栏
        leftBtn = (Button) findViewById(R.id.btn_left);
        alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
        accountError = (TextView) findViewById(R.id.accounterror);
        rightBtn = (Button) findViewById(R.id.btn_right);
        ishitvis = (RelativeLayout) findViewById(R.id.ishitvis);
        ht_motion = (ImageView) findViewById(R.id.ht_motion);
        ht_fight = (ImageView) findViewById(R.id.ht_flight);
        rightBtn.setVisibility(View.GONE);
        if (Consts.ISHITVIS == 1) {
            ishitvis.setVisibility(View.VISIBLE);
        }
        reParamstop2 = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        reParamstop2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        reParamstop2.addRule(RelativeLayout.CENTER_VERTICAL);
        reParamstop2.setMargins(0, 0, 30, 0);
        rightBtn.setLayoutParams(reParamstop2);
        currentMenu = (TextView) findViewById(R.id.currentmenu);

        selectScreenNum = (ImageView) findViewById(R.id.selectscreen);
        alarmRecordDrawableTop = getResources().getDrawable(
                R.drawable.record_alarm);
        normalRecordDrawableTop = getResources().getDrawable(
                R.drawable.record_normal);

        int[] screenArray = getResources().getIntArray(R.array.array_screen);
        if (null != screenArray) {
            int length = screenArray.length;
            screenList.clear();
            for (int i = 0; i < length; i++) {
                screenList.add(screenArray[i]);
            }
        }

        /** 中 */
        // mainfunctionLayout = (RelativeLayout)findViewById(R.id.mainfunction);
        // reParamsyt = new RelativeLayout.LayoutParams(
        // ViewGroup.LayoutParams.MATCH_PARENT,
        // ViewGroup.LayoutParams.WRAP_CONTENT);
        playViewPager = new MyViewPager(PlayActivity.this);
        playViewPager.setContext(this);
        playViewPager = (MyViewPager) findViewById(R.id.play_viewpager);
        playSurface = (SurfaceView) findViewById(R.id.remotesurfaceview);
        linkMode = (TextView) findViewById(R.id.linkstate);
        playBackBar = (RelativeLayout) findViewById(R.id.playbackbar);
        progressBar = (SeekBar) findViewById(R.id.playback_seekback);
        playBackPause = (Button) findViewById(R.id.playbackpause);
        voiceListener = (Button) findViewById(R.id.voice);
        varvoice_bg = (RelativeLayout) findViewById(R.id.varvoice_bg);
        varvoice = (ImageView) findViewById(R.id.varvoice);
        fullScreen = (ImageView) findViewById(R.id.fullscreen);
        playBackFullScreen = (ImageView) findViewById(R.id.playbackfullscreen);
        linkMode.setVisibility(View.VISIBLE);

        linkState = (TextView) findViewById(R.id.playstate);// 连接文字
        loading = (ProgressBar) findViewById(R.id.videoloading);// 加载进度

        /** 竖直播放function bar */
        verPlayBarLayout = (RelativeLayout) findViewById(R.id.play_ver_func);
        decodeBtn = (Button) findViewById(R.id.decodeway);
        videTurnBtn = (Button) findViewById(R.id.overturn);
        currentKbps = (Button) findViewById(R.id.kbps);
        playStatistics = (TextView) findViewById(R.id.play_statistics);// 播放统计
        currentMenu_v = (TextView) findViewById(R.id.play_nickname);

        decodeBtn.setVisibility(View.GONE);
        videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.turnleft_noturn));
        currentKbps.setVisibility(View.GONE);
        currentKbps.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (MySharedPreference.getBoolean(Consts.MORE_LITTLE)) {
                    closePopWindow();
                    if (View.VISIBLE == playStatistics.getVisibility()) {
                        playStatistics.setVisibility(View.GONE);
                    } else {
                        playStatistics.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        playStatistics.setVisibility(View.GONE);
        voiceTip = (RelativeLayout) findViewById(R.id.voicetip);

        /** 水平播放function bar */
        horPlayBarLayout = (RelativeLayout) findViewById(R.id.play_hor_func);

        horPlayHelp = (RelativeLayout) findViewById(R.id.horplayhelp);
        horViewPager = (ViewPager) findViewById(R.id.horplayhelp_viewpager);
        horViewPager.setOnPageChangeListener(PlayActivity.this);

        topBarH = (RelativeLayout) horPlayBarLayout.findViewById(R.id.topbarh);
        left_btn_h = (Button) horPlayBarLayout.findViewById(R.id.btn_left);// 横屏返回键
        currentMenu_h = (TextView) horPlayBarLayout
                .findViewById(R.id.currentmenu);
        right_btn_h = (Button) horPlayBarLayout.findViewById(R.id.btn_right);// 横屏手动录像，报警录像键
        right_btn_h.setVisibility(View.GONE);
        topBarH.setBackgroundColor(getResources().getColor(
                R.color.halftransparent));

        bottombut1 = (Button) findViewById(R.id.bottom_but1);
        bottombut2 = (Button) findViewById(R.id.bottom_but2);
        bottombut3 = (Button) findViewById(R.id.bottom_but3);
        bottombut4 = (Button) findViewById(R.id.bottom_but4);
        bottombut5 = (Button) findViewById(R.id.bottom_but5);
        bottombut6 = (Button) findViewById(R.id.bottom_but6);
        bottombut7 = (Button) findViewById(R.id.bottom_but7);
        bottombut8 = (Button) findViewById(R.id.bottom_but8);
        notFullScreen = (ImageView) findViewById(R.id.notfullscreen);

        bottomStream = (TextView) findViewById(R.id.video_bq);
        relative1 = (RelativeLayout) findViewById(R.id.relative1);
        relative2 = (RelativeLayout) findViewById(R.id.relative2);
        relative3 = (RelativeLayout) findViewById(R.id.relative3);
        relative4 = (RelativeLayout) findViewById(R.id.relative4);
        relative5 = (RelativeLayout) findViewById(R.id.relative5);
        relative6 = (RelativeLayout) findViewById(R.id.relative6);
        relative7 = (RelativeLayout) findViewById(R.id.relative7);
        relative8 = (RelativeLayout) findViewById(R.id.relative8);
        linear = (LinearLayout) findViewById(R.id.linear);
        if ((disMetrics.heightPixels > 800 && disMetrics.widthPixels > 480)
                || (disMetrics.heightPixels > 480 && disMetrics.widthPixels > 800)) {// 大屏
            bigScreen = true;
        }

        bigScreen = true;

        /** 小分辨率功能 */
        // playFuctionLayout = (LinearLayout)
        // findViewById(R.id.play_function_layout);
        // audioMonitor = (Button) findViewById(R.id.audio_monitor);// 音频监听
        // ytOperate = (Button) findViewById(R.id.yt_operate);// 云台
        // remotePlayback = (RelativeLayout)
        // findViewById(R.id.remote_playback);// 远程回放

        /** 大分辨率功能 */
        function = (LinearLayout) findViewById(R.id.function);
        talk_eachother = (RelativeLayout) findViewById(R.id.talk_eachother);
        talk_img_down = (ImageView) findViewById(R.id.talk_img_down);
        talk_img = (ImageView) findViewById(R.id.talk_img);
        talk_cancel = (ImageView) findViewById(R.id.talk_cancel);

        horfunc_talk = (RelativeLayout) findViewById(R.id.horfunc_talk);
        horfunc_talk_normal = (ImageView) findViewById(R.id.horfunc_talk_normal);
        horfunc_talk_down = (ImageView) findViewById(R.id.horfunc_talk_down);

        playFunctionList = (ListView) findViewById(R.id.play_function_list_layout);
        functionList.add(getResources().getString(R.string.str_yt_operate));
        functionList
                .add(getResources().getString(R.string.str_remote_playback));
        functionList.add(getResources().getString(R.string.str_audio_monitor));
        functionListAdapter = new FuntionAdapter(PlayActivity.this, bigScreen,
                playFlag);
        functionListAdapter.setData(functionList);
        playFunctionList.setAdapter(functionListAdapter);

        if (bigScreen) {
            playFunctionList.setVisibility(View.VISIBLE);
            // playFuctionLayout.setVisibility(View.GONE);
        } else {
            playFunctionList.setVisibility(View.GONE);
            // playFuctionLayout.setVisibility(View.VISIBLE);
        }

        /** 云台 布局 */
        ytLayout = (LinearLayout) findViewById(R.id.yt_layout);
        // yt_cancle = (ImageView)ytLayout.findViewById(R.id.yt_cancled);
        autoimage = (ImageView) ytLayout.findViewById(R.id.autoimage);
        zoomIn = (ImageView) ytLayout.findViewById(R.id.zoomin);
        zoomout = (ImageView) ytLayout.findViewById(R.id.zoomout);
        scaleSmallImage = (ImageView) ytLayout
                .findViewById(R.id.scaleSmallImage);
        scaleAddImage = (ImageView) ytLayout.findViewById(R.id.scaleAddImage);
        upArrow = (ImageView) ytLayout.findViewById(R.id.upArrow);
        downArrow = (ImageView) ytLayout.findViewById(R.id.downArrow);
        leftArrow = (ImageView) ytLayout.findViewById(R.id.leftArrow);
        rightArrow = (ImageView) ytLayout.findViewById(R.id.rightArrow);
        // ytSpeed = (EditText) ytLayout.findViewById(R.id.ytspeed);// 云台速度
        // ysSpeedSet = (Button) ytLayout.findViewById(R.id.setspeed);//
        // 云台速度调整按钮
        ytSeekLayout = (RelativeLayout) ytLayout.findViewById(R.id.ytspeedseekbarlayout);// 云台速度调整布局
        ytSeekBar = (SeekBar) ytLayout.findViewById(R.id.ytspeedseekbar);// 云台速度调整seekbar
        ytSpeed = (TextView) ytLayout.findViewById(R.id.yt_speed);// 云台速度值
        autoimage.setOnClickListener(imageOnClickListener);
        zoomIn.setOnClickListener(imageOnClickListener);
        zoomout.setOnClickListener(imageOnClickListener);
        scaleSmallImage.setOnClickListener(imageOnClickListener);
        scaleAddImage.setOnClickListener(imageOnClickListener);
        upArrow.setOnClickListener(imageOnClickListener);
        downArrow.setOnClickListener(imageOnClickListener);
        leftArrow.setOnClickListener(imageOnClickListener);
        rightArrow.setOnClickListener(imageOnClickListener);

        /** 下 */
        capture = (Button) findViewById(R.id.capture);// 抓拍
        voiceCall = (Button) findViewById(R.id.voicecall);// 喊话
        videoTape = (Button) findViewById(R.id.videotape);// 录像
        moreFeature = (Button) findViewById(R.id.more_features);// 更多

        if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(PlayActivity.this)) {
            capture.setTextSize(12);// 抓拍
            voiceCall.setTextSize(12);// 喊话
            videoTape.setTextSize(12);// 录像
            moreFeature.setTextSize(12);
        } else {
            capture.setTextSize(10);// 抓拍
            voiceCall.setTextSize(10);// 喊话
            videoTape.setTextSize(10);// 录像
            moreFeature.setTextSize(10);
        }

        footerBar = (LinearLayout) findViewById(R.id.footbar);// 底部工具栏
        apFuncLayout = (LinearLayout) findViewById(R.id.apfunclayout);// 底部AP下一步
        nextStep = (Button) findViewById(R.id.nextstep);// 下一步

        videoTapeTop1 = getResources().getDrawable(R.drawable.video_record_1);
        videoTapeTop2 = getResources().getDrawable(R.drawable.video_record_2);

        voiceCallTop1 = getResources().getDrawable(R.drawable.voice_call_1);
        voiceCallTop2 = getResources().getDrawable(R.drawable.voice_call_2);
        setPlayViewSize();

        playBackFullScreen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (Configuration.ORIENTATION_PORTRAIT == configuration.orientation) {// 竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
                    playBackFullScreen.setImageDrawable(getResources()
                            .getDrawable(R.drawable.notfull_screen_icon));
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
                    playBackFullScreen.setImageDrawable(getResources()
                            .getDrawable(R.drawable.full_screen_icon));
                }

            }
        });

        fullScreen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
            }
        });

        notFullScreen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
            }
        });
    }

    // 云台按钮事件
    ImageView.OnClickListener imageOnClickListener = new ImageView.OnClickListener() {
        @Override
        public void onClick(View arg0) {
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setPlayViewSize();
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        super.onConfigurationChanged(newConfig);

    }

    private void getPic() {
        flag = 0;
        pics = new ArrayList<View>();
        View view1 = LayoutInflater.from(PlayActivity.this).inflate(
                R.layout.help_item7, null);
        View view2 = LayoutInflater.from(PlayActivity.this).inflate(
                R.layout.help_item8, null);
        int height = disMetrics.heightPixels;
        int width = disMetrics.widthPixels;
        int useWidth = 0;
        if (height < width) {
            useWidth = height;
        } else {
            useWidth = width;
        }
        autoRelative = (LinearLayout) view2.findViewById(R.id.autoRelative);
        Params = new LinearLayout.LayoutParams(useWidth,
                (int) (0.7 * useWidth) - 80);
        autoRelative.setLayoutParams(Params);
        View view3 = LayoutInflater.from(PlayActivity.this).inflate(
                R.layout.help_item6, null);
        pics.add(view1);
        pics.add(view2);
        pics.add(view3);
        initDot(2);
    }

    private void getHorpic() {
        flag = 1;
        horpics = new ArrayList<View>();
        View view1 = LayoutInflater.from(PlayActivity.this).inflate(
                R.layout.help_item9, null);
        View view2 = LayoutInflater.from(PlayActivity.this).inflate(
                R.layout.help_item6, null);
        horpics.add(view1);
        horpics.add(view2);
    }

    private void initDot(int dotnum) {
        dots = new ArrayList<ImageView>();
        // 得到点的父布局
        for (int i = 0; i < dotnum; i++) {
            ll_dot.getChildAt(i);// 得到点
            dots.add((ImageView) ll_dot.getChildAt(i));
            dots.get(i).setEnabled(false);// 将点设置为白色
        }
        dots.get(currentImage).setEnabled(true); // 因为默认显示第一张图片，将第一个点设置为黑色
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        currentImage = arg0; // 获取当前页面索引
        if (flag == 0) {
            if (arg0 != 2) {
                dots.get(oldImage).setEnabled(false); // 前一个点设置为白色
                dots.get(currentImage).setEnabled(true); // 当前点设置为黑色
                oldImage = currentImage; // 改变前一个索引
                currentImage = (currentImage) % 2; // 有几张就对几求余
            }
            if (arg0 == 2) {
                MySharedPreference.putBoolean("playhelp1", true);
                helpViewPager.setVisibility(View.GONE);
                ll_dot.setVisibility(View.GONE);
            }
        } else if (flag == 1) {
            if (arg0 == 1) {
                MySharedPreference.putBoolean("playhelp2", true);
                horPlayHelp.setVisibility(View.GONE);
                helpViewPager.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 横竖屏布局隐藏显示
     */
    protected void setPlayViewSize() {
        if (Configuration.ORIENTATION_PORTRAIT == configuration.orientation) {// 竖屏
            if (!MySharedPreference.getBoolean("playhelp1")) {
                flag = 0;
                playHelp.setVisibility(View.VISIBLE);
                helpViewPager.setVisibility(View.VISIBLE);
            }
            horPlayHelp.setVisibility(View.GONE);
            playViewPager.setDisableSliding(false);
            getWindow()
                    .setFlags(
                            disMetrics.widthPixels
                                    - getStatusHeight(PlayActivity.this),
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
            topBar.setVisibility(View.VISIBLE);// 顶部标题栏

            if (Consts.PLAY_AP == playFlag) {
                playHelp.setVisibility(View.GONE);
                footerBar.setVisibility(View.GONE);// 底部工具栏
                apFuncLayout.setVisibility(View.VISIBLE);
            } else {
                footerBar.setVisibility(View.VISIBLE);// 底部工具栏
                apFuncLayout.setVisibility(View.GONE);
            }

            verPlayBarLayout.setVisibility(View.GONE);
            horPlayBarLayout.setVisibility(View.GONE);
            int height = disMetrics.heightPixels;
            int width = disMetrics.widthPixels;
            int useWidth = 0;
            if (height < width) {
                useWidth = height;
            } else {
                useWidth = width;
            }

            reParamsV = new RelativeLayout.LayoutParams(useWidth,
                    (int) (0.75 * useWidth));

            MyLog.v(Consts.TAG_XXX, "useWidth=" + useWidth
                    + "(int) (0.75 * useWidth)=" + (int) (0.75 * useWidth));

            playViewPager.setLayoutParams(reParamsV);
            // MyLog.v(Consts.TAG_XXX,
            // "viewpager:width="+playViewPager.getWidth()
            // + "viewpager:height="+playViewPager.getHeight());

            playSurface.setLayoutParams(reParamsV);
            decodeBtn.setVisibility(View.VISIBLE);
            currentKbps.setVisibility(View.VISIBLE);
            // [Neo] surface.step 0
            if (surfaceWidth < 0 || surfaceHeight < 0) {
                surfaceWidth = disMetrics.widthPixels;
                surfaceHeight = (int) (0.75 * disMetrics.widthPixels);
            }
        } else {// 横
            closePopWindow();
            if (!MySharedPreference.getBoolean("playhelp2")
                    && Consts.PLAY_NORMAL == playFlag) {
                horPlayHelp.setVisibility(View.VISIBLE);
                horViewPager.setCurrentItem(0);
                getHorpic();
                horadp = new MyPagerAdp(horpics);
                horViewPager.setAdapter(horadp);
            }
            playHelp.setVisibility(View.GONE);
            playViewPager.setDisableSliding(true);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            topBar.setVisibility(View.GONE);// 顶部标题栏
            footerBar.setVisibility(View.GONE);// 底部工具栏
            apFuncLayout.setVisibility(View.GONE);
            verPlayBarLayout.setVisibility(View.GONE);

            horPlayBarLayout.setVisibility(View.GONE);
            // init();
            if (Consts.PLAY_AP == playFlag) {
                playHelp.setVisibility(View.GONE);
                bottombut6.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.turn_no));
                videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.turnleft_noturn));
                videTurnBtn.setClickable(false);
                bottomStream.setVisibility(View.GONE);
            }
            decodeBtn.setVisibility(View.GONE);
            videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.turnleft_noturn));
            currentKbps.setVisibility(View.GONE);

            reParamsH = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            playViewPager.setLayoutParams(reParamsH);
            // MyLog.v(Consts.TAG_XXX,
            // "viewpager:width="+playViewPager.getWidth()
            // + "viewpager:height="+playViewPager.getHeight());
            playSurface.setLayoutParams(reParamsH);
            // [Neo] surface.step 0
            if (surfaceWidth < 0 || surfaceHeight < 0) {
                surfaceWidth = disMetrics.widthPixels;
                surfaceHeight = disMetrics.heightPixels;
            }

        }
    }

    /**
     * 显示横屏功能
     * 
     * @param channnel
     */
    @SuppressWarnings("deprecation")
    public void showVerFuc(Channel channel) {
        if (MySharedPreference.getBoolean("playhelp1")) {
            verPlayBarLayout.setVisibility(View.GONE);
        } else {
            verPlayBarLayout.setVisibility(View.VISIBLE);
        }
        horPlayBarLayout.setVisibility(View.GONE);

        // if (Consts.PLAY_AP == playFlag) {
        // currentKbps.setVisibility(View.GONE);
        // } else {
        currentKbps.setVisibility(View.VISIBLE);
        // }
        // 获取软硬解状态
        if (channel.isOMX()) {
            decodeBtn.setText(R.string.is_omx);
        } else {
            decodeBtn.setText(R.string.not_omx);
        }

        if (lowerSystem) {
            decodeBtn.setVisibility(View.GONE);
        } else {
            decodeBtn.setVisibility(View.VISIBLE);
        }

        if (Consts.PLAY_AP == playFlag) {
            playHelp.setVisibility(View.GONE);
            rightBtn.setVisibility(View.GONE);
        } else {
            // 录像模式
            rightBtn.setTextSize(8);
            rightBtn.setTextColor(getResources().getColor(R.color.white));
            rightBtn.setBackgroundDrawable(null);
            if (Consts.STORAGEMODE_NORMAL == channel.getStorageMode()) {
                rightBtn.setText(R.string.video_normal);
                rightBtn.setCompoundDrawablesWithIntrinsicBounds(null,
                        normalRecordDrawableTop, null, null);

                rightBtn.setVisibility(View.VISIBLE);

            } else if (Consts.STORAGEMODE_ALARM == channel.getStorageMode()) {
                rightBtn.setText(R.string.video_alarm);
                rightBtn.setCompoundDrawablesWithIntrinsicBounds(null,
                        alarmRecordDrawableTop, null, null);
                rightBtn.setVisibility(View.VISIBLE);
            } else {
                rightBtn.setVisibility(View.GONE);
            }
        }

        if (Consts.PLAY_AP == playFlag) {
            playHelp.setVisibility(View.GONE);
            videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.turnleft_noturn));
            videTurnBtn.setClickable(false);
        } else {
            // 屏幕方向
            if (Consts.SCREEN_NORMAL == channel.getScreenTag()) {
                videTurnBtn.setVisibility(View.VISIBLE);
                videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.ver_left_selector));
            } else if (Consts.SCREEN_OVERTURN == channel.getScreenTag()) {
                videTurnBtn.setVisibility(View.VISIBLE);
                videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.ver_right_selector));
            } else {
                videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.turnleft_noturn));
            }
        }

        int streamIndex = channel.getStreamTag() - 1;
        // 码流设置
        if (streamIndex >= 0 && streamIndex <= 2) {
            streamAdapter.selectStream = streamIndex;
            if (streamAdapter.selectStream < streamArray.length) {
                streamAdapter.notifyDataSetChanged();
                moreFeature.setText(streamArray[streamIndex]);
                bottomStream.setText(streamArray[streamIndex]);
            }
        }
    }

    public void showFunc(Channel channel, int screen, int index) {

        if (index != channel.getIndex()) {
            return;
        }

        if (screen > 1 || !channel.isConnected()) {
            rightBtn.setVisibility(View.GONE);
            right_btn_h.setVisibility(View.GONE);
            if (MySharedPreference.getBoolean("playhelp1")) {
                verPlayBarLayout.setVisibility(View.GONE);
            } else {
                verPlayBarLayout.setVisibility(View.VISIBLE);
            }
            horPlayBarLayout.setVisibility(View.GONE);
        } else {
            if (Configuration.ORIENTATION_PORTRAIT == configuration.orientation) {// 竖屏
                showVerFuc(channel);
            } else {
                showHorFuc(channel);
            }
        }
    }

    /**
     * 显示横屏功能
     * 
     * @param channnel
     */
    @SuppressWarnings("deprecation")
    public void showHorFuc(Channel channel) {
        verPlayBarLayout.setVisibility(View.GONE);
        horPlayBarLayout.setVisibility(View.GONE);

        // 获取软硬解状态
        if (channel.isOMX()) {
            bottombut2.setText(R.string.is_omx);
        } else {
            bottombut2.setText(R.string.not_omx);
        }

        if (lowerSystem) {
            bottombut2.setVisibility(View.GONE);
        } else {
            bottombut2.setVisibility(View.VISIBLE);
        }

        if (Consts.PLAY_AP == playFlag) {
            playHelp.setVisibility(View.GONE);
            right_btn_h.setVisibility(View.GONE);
        } else {
            // 录像模式
            right_btn_h.setTextSize(8);
            right_btn_h.setTextColor(getResources().getColor(R.color.white));
            right_btn_h.setBackgroundDrawable(null);
            if (Consts.STORAGEMODE_NORMAL == channel.getStorageMode()) {
                right_btn_h.setText(R.string.video_normal);
                right_btn_h.setCompoundDrawablesWithIntrinsicBounds(null,
                        normalRecordDrawableTop, null, null);
                right_btn_h.setVisibility(View.VISIBLE);
            } else if (Consts.STORAGEMODE_ALARM == channel.getStorageMode()) {
                right_btn_h.setText(R.string.video_alarm);
                right_btn_h.setCompoundDrawablesWithIntrinsicBounds(null,
                        alarmRecordDrawableTop, null, null);
                right_btn_h.setVisibility(View.VISIBLE);
            } else {
                right_btn_h.setVisibility(View.GONE);
            }

        }

        if (Consts.PLAY_AP == playFlag) {
            playHelp.setVisibility(View.GONE);
            bottombut6.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.turn_no));
            videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.turnleft_noturn));
            videTurnBtn.setClickable(false);
        } else {
            // 屏幕方向
            if (Consts.SCREEN_NORMAL == channel.getScreenTag()) {
                relative6.setVisibility(View.VISIBLE);
                bottombut6.setVisibility(View.VISIBLE);
                bottombut6.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.turn_lan_left));
            } else if (Consts.SCREEN_OVERTURN == channel.getScreenTag()) {
                relative6.setVisibility(View.VISIBLE);
                bottombut6.setVisibility(View.VISIBLE);
                bottombut6.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.turn_lan_right));
            } else {
                bottombut6.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.turn_no));
                videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.turnleft_noturn));
            }
        }

        int streamIndex = channel.getStreamTag() - 1;
        // 码流设置
        if (streamIndex >= 0 && streamIndex <= 2) {
            streamAdapter.selectStream = streamIndex;
            if (streamAdapter.selectStream < streamArray.length) {
                streamAdapter.notifyDataSetChanged();
                moreFeature.setText(streamArray[streamIndex]);
                bottomStream.setText(streamArray[streamIndex]);
            }
        }
    }

    /**
     * 清空所有状态
     */
    @SuppressWarnings("deprecation")
    public void resetFunc(Channel channel) {
        MyLog.v("resetFunc", channel.toString());
        try {
            verPlayBarLayout.setVisibility(View.GONE);
            bottombut6.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.turn_no));
            videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.turnleft_noturn));
            right_btn_h.setVisibility(View.GONE);// 录像模式
            moreFeature.setText(R.string.default_stream);// 码流
            bottomStream.setText(R.string.default_stream);
            // 停止音频监听
            if (PlayUtil.isPlayAudio(channel.getIndex())) {
                MyLog.v("resetFunc", channel.getIndex() + "正在监听");
                stopAudio(channel.getIndex());
                functionListAdapter.selectIndex = -1;
                bottombut8.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.video_monitor_ico));
                varvoice.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.video_monitor_ico));
                functionListAdapter.notifyDataSetChanged();
            }

            // 正在录像停止录像
            if (PlayUtil.checkRecord(channel.getIndex())) {
                MyLog.v("resetFunc", channel.getIndex() + "正在录像");
                long recordTime = System.currentTimeMillis() - startRecordTime;
                MyLog.e(TAG, "recordTime=" + recordTime);
                PlayUtil.stopVideoTape();
                if (recordTime <= 2000) {
                    File recordFile = new File(recordingPath);
                    recordFile.delete();
                    showTextToast(R.string.record_failed);
                } else {
                    showTextToast(Consts.VIDEO_PATH);
                }
                tapeSelected(false);
            }

            // 停止对讲
            if (channel.isVoiceCall()) {
                MyLog.v("resetFunc", channel.getIndex() + "正在对讲");
                channel.setVoiceCall(false);
                MyLog.e("MyOnGestureListener--resetFunc", "false");
                realStop = true;
                voiceCallSelected(false);
                stopVoiceCall(channel.getIndex());
            }
            // 云台功能如果显示，则先关闭云台
            if (View.VISIBLE == ytLayout.getVisibility()) {
                ytLayout.setVisibility(View.GONE);
                if (bigScreen) {
                    playFunctionList.setVisibility(View.VISIBLE);
                } else {
                    playFunctionList.setVisibility(View.GONE);
                }
            }

            // dddd
            AUDIO_SINGLE = false;// 单向对讲标志
            VOICECALL_LONG_CLICK = false;// 语音喊话flag长按状态,长按发送数据
            VOICECALLING = false;// 对讲功能已经开启
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // /**
    // * 刷新IPC状态显示
    // *
    // * @param channel
    // */
    // @SuppressWarnings("deprecation")
    // protected void refreshIPCFun(Channel channel) {
    // currentKbps.setVisibility(View.VISIBLE);
    // // 获取软硬解状态
    // if (channel.isOMX()) {
    // decodeBtn.setText(R.string.is_omx);
    // bottombut2.setText(R.string.is_omx);
    // } else {
    // decodeBtn.setText(R.string.not_omx);
    // bottombut2.setText(R.string.not_omx);
    // }
    //
    // if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
    // 横屏
    // decodeBtn.setVisibility(View.GONE);
    // } else {
    // decodeBtn.setVisibility(View.VISIBLE);
    // }
    //
    // if (lowerSystem) {
    // bottombut2.setVisibility(View.GONE);
    // decodeBtn.setVisibility(View.GONE);
    // }
    //
    // // 录像模式
    // if (Consts.STORAGEMODE_NORMAL == channel.getStorageMode()) {
    // rightFuncButton.setText(R.string.video_normal);
    // rightFuncButton.setCompoundDrawablesWithIntrinsicBounds(null,
    // normalRecordDrawableTop, null, null);
    // rightFuncButton.setTextSize(8);
    // rightFuncButton
    // .setTextColor(getResources().getColor(R.color.white));
    // rightFuncButton.setBackgroundDrawable(null);
    //
    // right_btn_h.setText(R.string.video_normal);
    // right_btn_h.setCompoundDrawablesWithIntrinsicBounds(null,
    // normalRecordDrawableTop, null, null);
    // right_btn_h.setTextSize(8);
    // right_btn_h.setTextColor(getResources().getColor(R.color.white));
    // right_btn_h.setBackgroundDrawable(null);
    //
    // if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
    // 横屏
    // right_btn_h.setVisibility(View.VISIBLE);
    // rightFuncButton.setVisibility(View.GONE);
    // } else {
    // right_btn_h.setVisibility(View.GONE);
    // rightFuncButton.setVisibility(View.VISIBLE);
    // }
    //
    // } else if (Consts.STORAGEMODE_ALARM == channel.getStorageMode()) {
    // rightFuncButton.setText(R.string.video_alarm);
    // rightFuncButton.setCompoundDrawablesWithIntrinsicBounds(null,
    // alarmRecordDrawableTop, null, null);
    // rightFuncButton.setTextSize(8);
    // rightFuncButton
    // .setTextColor(getResources().getColor(R.color.white));
    // rightFuncButton.setBackgroundDrawable(null);
    //
    // right_btn_h.setText(R.string.video_alarm);
    // right_btn_h.setCompoundDrawablesWithIntrinsicBounds(null,
    // alarmRecordDrawableTop, null, null);
    // right_btn_h.setTextSize(8);
    // right_btn_h.setTextColor(getResources().getColor(R.color.white));
    // right_btn_h.setBackgroundDrawable(null);
    //
    // if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
    // 横屏
    // right_btn_h.setVisibility(View.VISIBLE);
    // rightFuncButton.setVisibility(View.GONE);
    // } else {
    // right_btn_h.setVisibility(View.GONE);
    // rightFuncButton.setVisibility(View.VISIBLE);
    // }
    // } else {
    // rightFuncButton.setVisibility(View.GONE);
    // right_btn_h.setVisibility(View.GONE);
    // }
    //
    // // 屏幕方向
    // if (Consts.SCREEN_NORMAL == channel.getScreenTag()) {
    // videTurnBtn.setVisibility(View.VISIBLE);
    // videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
    // R.drawable.turn_left_selector));
    // relative6.setVisibility(View.VISIBLE);
    // bottombut6.setVisibility(View.VISIBLE);
    // bottombut6.setBackgroundDrawable(getResources().getDrawable(
    // R.drawable.turn_left_selector));
    // if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
    // 横屏
    // relative6.setVisibility(View.VISIBLE);
    // videTurnBtn.setVisibility(View.GONE);
    // } else {
    // videTurnBtn.setVisibility(View.VISIBLE);
    // }
    // } else if (Consts.SCREEN_OVERTURN == channel.getScreenTag()) {
    // videTurnBtn.setVisibility(View.VISIBLE);
    // videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
    // R.drawable.turn_right_selector));
    // relative6.setVisibility(View.VISIBLE);
    // bottombut6.setVisibility(View.VISIBLE);
    // bottombut6.setBackgroundDrawable(getResources().getDrawable(
    // R.drawable.turn_right_selector));
    // if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
    // 横屏
    // relative6.setVisibility(View.VISIBLE);
    // videTurnBtn.setVisibility(View.GONE);
    // } else {
    // videTurnBtn.setVisibility(View.VISIBLE);
    // }
    // } else {
    // if (relative6.getVisibility() == View.VISIBLE) {
    // linear.removeView(relative6);
    // linear.addView(relative6, linear.getChildCount());
    // bottombut6.setVisibility(View.GONE);
    // videTurnBtn.setVisibility(View.GONE);
    // }
    // }
    // // 码流设置
    // if (-1 != channel.getStreamTag()) {
    // streamAdapter.selectStream = channel.getStreamTag() - 1;
    // streamAdapter.notifyDataSetChanged();
    // moreFeature.setText(streamArray[channel.getStreamTag() - 1]);
    // bottomStream.setText(streamArray[channel.getStreamTag() - 1]);
    // }
    // }

    /**
     * 录像
     * 
     * @param selected
     */
    @SuppressWarnings("deprecation")
    protected void tapeSelected(Boolean selected) {
        if (selected) {// 选中
            // videoTape.setTextColor(getResources().getColor(
            // R.color.functionbtncolor2));

            videoTape.setCompoundDrawablesWithIntrinsicBounds(null,
                    videoTapeTop2, null, null);
            bottombut7.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.video_record1));
        } else {
            // videoTape.setTextColor(getResources().getColor(
            // R.color.functionbtncolor1));

            videoTape.setCompoundDrawablesWithIntrinsicBounds(null,
                    videoTapeTop1, null, null);
            bottombut7.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.video_record));
        }

        recoding = selected;
    }

    /**
     * 对讲
     * 
     * @param selected
     */
    @SuppressWarnings("deprecation")
    protected void voiceCallSelected(Boolean selected) {
        if (selected) {// 选中
            // voiceCall.setTextColor(getResources().getColor(
            // R.color.functionbtncolor2));

            voiceCall.setCompoundDrawablesWithIntrinsicBounds(null,
                    voiceCallTop2, null, null);
            bottombut5.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.video_talkselect_icon));
            // if (this.getResources().getConfiguration().orientation ==
            // Configuration.ORIENTATION_LANDSCAPE){
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            // }
            // else if(this.getResources().getConfiguration().orientation
            // ==Configuration.ORIENTATION_PORTRAIT) {
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // }

        } else {
            // voiceCall.setTextColor(getResources().getColor(
            // R.color.functionbtncolor1));
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            voiceCall.setCompoundDrawablesWithIntrinsicBounds(null,
                    voiceCallTop1, null, null);
            bottombut5.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.video_talkback_icon));

        }
    }

    /**
     * 显示云台功能
     */
    protected void showPTZ() {
        if (View.GONE == ytLayout.getVisibility()) {
            // reParamsyt.setMargins(0, 0, 0, 80);
            // reParamsyt.addRule(RelativeLayout.BELOW,R.id.videolayout);
            // mainfunctionLayout.setLayoutParams(reParamsyt);
            ytLayout.setVisibility(View.VISIBLE);
            playFunctionList.setVisibility(View.GONE);
            // playFuctionLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 停止对讲，音频监听和录像功能
     * 
     * @param index
     */
    protected void stopAll(int index, Channel channel) {
        if (PlayUtil.checkRecord(index)) {// 正在录像，停止录像
            PlayUtil.stopVideoTape();
            showTextToast(getResources().getString(R.string.str_stop_record)
                    + Consts.VIDEO_PATH);
            tapeSelected(false);
        }

        if (PlayUtil.isPlayAudio(index)) {// 正在音频监听，停止监听
            PlayUtil.stopAudioMonitor(index);
            functionListAdapter.selectIndex = -1;
            functionListAdapter.notifyDataSetChanged();
            bottombut8.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.video_monitor_ico));
            varvoice.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.video_monitor_ico));
        }

        if (null != channel && channel.isVoiceCall()) {
            stopVoiceCall(index);
            channel.setVoiceCall(false);
            MyLog.e("MyOnGestureListener--stopAll", "false");
            realStop = true;
            voiceCallSelected(false);
        }
    }

    @Override
    protected void saveSettings() {

    }

    @Override
    protected void onPause() {
        dismissDialog();
        super.onPause();
    }

    @Override
    protected void freeMe() {
        try {
            if (null != mediaPlayer) {
                mediaPlayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onFlip(View view) {

    }

    /**
     * 应用层开启音频监听功能
     * 
     * @param index
     * @return
     */
    public static boolean startAudio(int index, int audioByte) {
        boolean open = false;
        if (PlayUtil.isPlayAudio(index)) {// 正在监听,确保不会重复开启
            open = true;
        } else {
            PlayUtil.startAudioMonitor(index);// enable audio
            playAudio.startPlay(audioByte, true);
            open = true;
        }
        return open;
    }

    /**
     * 应用层关闭音频监听功能
     * 
     * @param index
     * @return
     */
    public static boolean stopAudio(int index) {
        boolean close = false;
        if (PlayUtil.isPlayAudio(index)) {// 正在监听，停止监听
            PlayUtil.stopAudioMonitor(index);// stop audio
            playAudio.stopPlay();
            close = true;
        } else {// 确保不会重复关闭
            close = true;
        }
        return close;
    }

    /**
     * 开始语音对讲
     */
    public static void startVoiceCall(int index, Channel channel) {
        Jni.sendBytes(index, JVNetConst.JVN_REQ_CHAT, new byte[0], 8);
    }

    /**
     * 停止语音对讲
     */
    public static void stopVoiceCall(int index) {
        Jni.sendBytes(index, JVNetConst.JVN_CMD_CHATSTOP, new byte[0], 8);
        // 关闭语音对讲
        playAudio.stopPlay();
        playAudio.stopRec();

    }

    // if(PlayUtil.checkRecord(lastClickIndex)){
    // long recordTime = System.currentTimeMillis()
    // - startRecordTime;
    // MyLog.e(TAG, "recordTime=" + recordTime);
    // PlayUtil.stopVideoTape();
    // if (recordTime <= 2000) {
    // File recordFile = new File(recordingPath);
    // recordFile.delete();
    // showTextToast(R.string.record_failed);
    // } else {
    // showTextToast(Consts.VIDEO_PATH);
    // }
    // tapeSelected(false);
    // }else{
    //
    // }

    /**
     * 开始录像
     * 
     * @param lastClickIndex
     * @param path
     * @return
     */
    public boolean startRecord(int lastClickIndex, String path) {
        boolean res = PlayUtil.startVideoTape(lastClickIndex, path);

        MyLog.v("resetFunc--startRecord", "lastClickIndex=" + lastClickIndex);
        recordingPath = path;
        startRecordTime = System.currentTimeMillis();
        tapeSelected(true);
        showTextToast(R.string.str_start_record);
        return res;
    }

    /**
     * 停止录像
     * 
     * @return
     */
    public boolean stopRecord(boolean autoStop) {// 是否自动断开
        long recordTime = System.currentTimeMillis() - startRecordTime;
        MyLog.e(TAG, "recordTime=" + recordTime);

        boolean res = false;
        if (!autoStop) {// 自动断开
            res = PlayUtil.stopVideoTape();
        }

        if (recordTime <= 3000) {
            File recordFile = new File(recordingPath);
            recordFile.delete();
            showTextToast(R.string.record_failed);
        } else {
            showTextToast(Consts.VIDEO_PATH);
        }
        if (!autoStop) {// 自动断开
            tapeSelected(false);
        }
        return res;
    }

    /**
     * 关掉提示框选择列表框
     */
    protected void closePopWindow() {
        if (null != streamListView
                && View.VISIBLE == streamListView.getVisibility()) {
            streamListView.setVisibility(View.GONE);
        }
        if (null != screenPopWindow && screenPopWindow.isShowing()) {
            screenPopWindow.dismiss();
        }
    }

    // protected void init() {
    // new CountDownTimer(6 * 1000, 2 * 1000) {
    //
    // @Override
    // public void onTick(long millisUntilFinished) {
    //
    // }
    //
    // @Override
    // public void onFinish() {
    // horPlayBarLayout.setVisibility(View.GONE);
    // }
    // }.start();
    // }
}
