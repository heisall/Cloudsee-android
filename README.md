# 规范

## java 源文件

+ 接口命名 IAdjNoun
+ 类名 NounPhrase，即 UpperCamelCase
+ 派生类命名 XXXParentClzName
+ 常量全部大写，下划线分割，使用 static final 修饰
+ 变量、参数都采用 lowerCamelCase 命名
+ 尽量不使用枚举类型
+ 修饰符的顺序依次为 private static final native int
+ 工具类可存放在 xx.commons，xx.commons.net，xx.commons.views 包里面
+ 界面类可存放在 xx.activites，xx.views，xx.examples
+ 列宽 80 个字节
+ 缩进使用 4 字节宽
+ 重载方法在源码物理行上连续实现，永不分离
+ 不要用类对象调用类静态方法
+ 只要有异常就应该捕获
+ switch 中必须有 default
+ 代码块即使只有一行，也必须有大括号包裹
+ 注释统一写在待注释语句的上面，而不是后面
+ Eclipse 统一使用默认格式化 Ctrl + Shift + F
+ Eclipse 通过 Ctrl + Shift + O 导入包，手写 import 不要带通配符

## c/c++ 源文件

+ 文件名统一小写，下划线分割
+ 后缀名统一用 cc 和 h
+ 变量名需要有描述性，统一小写，下划线分割，成员变量结尾添加下划线
+ 只要可以不用全局变量，就不使用他，必要情况下，使用 `g_` 做前缀
+ 常亮使用 `c_` 做前缀
+ 类、结构体、类型定义、方法名称大写字母开头，不使用下划线
+ 短小的内联函数也可以用小写字母命名
+ 枚举值采用常量的命名方式
+ 尽量避免使用宏，必要情况下，统一大写，下划线分割

## Javadoc

+ 每个 public 以及 protected 的方法和成员都需要 javadoc
+ 不含 @XXX 的注释可以单行标注
+ 分段需要添加 `<p>`
+ 必要的位置添加 @param， @return, @throws, @deprecated
+ 对于明显的 getter 和 setter 方法，完全可以不去写

# 应用层

## 包分布

+ 全局常量、Jni 接口等都定义在 `com.jovision` 包中
+ 活动都在 `com.jovision.activities` 包中
+ 集合类都在 `com.jovision.beans` 包中
+ 通用方法都在 `com.jovision.commons` 包中，可再细分 `net`，`resource` 等子包

## 消息分发

+ MainApplication 负责消息分发，包括底层所有回调和应用层活动之间的通信
+ BaseActivity 作为所有活动基类，实现消息分发、生命周期规划等
+ BaseFragment 作为所有碎片基类，实现子消息分发、子生命周期规划等
+ 每个活动、碎片必须实现 `onHandler` 和 `onNotify` 接口，处理消息
+ 每个活动将对应事务写入对应方法体中

    + initSettings，从配置文件中获取配置，在父类的 onCreate 已调用
    + initUi， 界面相关初始化，在父类中的 onCreate 已调用
    + saveSettings，将变更的设置或状态存储起来，在父类 onPause 已调用
    + freeMe，解锁、删除不用的对象，释放资源，在父类 onDestroy 中调用
    + onNotify，作为外部回调，接口回调的入口，重写具体的实现

## 设备与通道索引

+ 设备包含通道，一对多
+ 通道包含一个通道索引和一个窗口索引
+ 通道索引从固定数值开始，递增，最多 64 个，用户可定制个数、顺序
+ 窗口索引是进入播放界面后，可播放的通道序列，从 0 开始，最多同时维持 36 个连接

# 接口层

## 版本

播放库版本

    #define MY_VERSION    "0.8"
    #define REVISION      "[88635b0]"
    #define RELEASE_DATE  "[2014-11-07]"

网络库版本

    v2.0.76.3.8[private:v2.0.75.13 201401104]

## 播放库主要功能

+ 局域网搜索，仅全部网络旧接口
+ 使用链接小助手链接视频源，播放视频源、远程回放
+ 实时、手动切换 **软硬解码器** 播放
+ 统计每个通道网络传输比特率、帧率，**内置播放速度控制**
+ 视频显示可定制放大、缩小
+ 视频截图功能
+ 录制视频源的 **视频和音频** 到本地
+ 向设备直接发送命令
+ 编码 PCM 至 g711 或 amr

## 播放库调用流程

1. 初始化
2. 局域网搜索，可选
3. 设置需要连接设备的链接小助手
4. 调用连接，等待指定键值的回调
5. 播放过程中可截图、录像、切换软硬解、发送命令
6. 编码音频可独立使用
7. 断开所有连接并接收指定回调
8. 反初始化

## 延迟统计

+ searchLanDevice -> `JVC_MOLANSerchDevice`: ms
+ queryDevice -> `JVC_QueryDevice`: ms
+ getChannelCount -> `JVC_WANGetChannelCount`: s
+ isDeviceOnlin -> `JVC_GetYSTStatus`: s

## 回调说明

ConnectChange

    id, window, type, json
    json: {"msg":""}

ConnectChangeRTMP

    id, window, type, json
    json: {"msg":""}

PlayData

    id, window, type, json
    json: {"count":0,
        "audio_type":0, "audio_sample_rate":0, "audio_bit":0, "audio_channel":0,
        "width":0, "height":0, "fps":0.0, "total":0}

    caution: type == PLAYOVER/PLAYE/PLTIMEOUT doesn't mean play stopped!

NormalData

    id, window, type, json
    json: {"is05":true, "fps":0.0, "device_type":0, "start_code":0,
        "reserved":0, "width":0, "height":0, "auto_stop_recorder":false,
        "audio_type":0, "audio_sample_rate":0, "audio_bit":0, "audio_channel":0}

CheckResult

    id, window, 0, byte[]

ChatData

    id, window, type, byte[]

TextData

    id, window, type, json
    json: {"result":0, "msg":"", "flag":0, "type":0,
        "packet_type":0, "packet_count":0, "packet_id":0, "packet_length":0,
        "extend_type":0, "extend_arg1":0, "extend_arg2":0, "extend_arg3":0,
        "wifi":[{"name":"", "pwd":"", "quality":0, "keystat":0, "auth":0, "env":0},{}]
        }

Download

    id, window, type, NULL

SearchLanServer

    id, 0, 0, json
    json: {"ip":"", "gid":"", "no":0, "type":0, "count":0,
        "port":0, "variety":0, "timeout":0, "netmod":0, "curmod":0}

queryDevice

    id, 0, 0, json
    json: {"ip":"", "gid":"", "no":0, "type":0, "count":0,
        "port":0, "variety":0, "timeout":0, "netmod":0, "curmod":0}

stat

    id, 0, 0, jsonArray
    jsonArray: [{"window":0, "delay":0.0, "kbps":0.0,
        "audio_type":1, "audio_kbps":0.0,
        "audio_network_fps":0.0, "audio_decoder_fps":0.0,
        "audio_decoder_delay":0.0, "audio_play_delay":0.0, 
        "network_fps":0.0, "decoder_fps":0.0, "jump_fps":0.0,
        "decoder_delay":0.0, "render_delay":0.0,
        "space":0, "width":0, "height":0, "left":0,
        "is_omx":false, "is_turn":false, "is_playback":false}]

play

    // screenshot
    id, window, result, null

    // frame I report
    id, window, try_omx, null

    // play doomed
    id, window, bad_status, null
    id, window, playback_done, null
    id, window, hdec_buffering, null

    // play audio
    id, window, is_play_back, byte[]
    byte: pcm raw data

# 播放流程

## 工作流

1. 创建可显示的平面
2. 调用连接接口，绑定平面
3. 暂停和恢复
4. 调用断开接口

## 应用层

1. `manager.genPageList(count)`
2. `adapter.update()` & `pager.setAdapter()`
3. `onLifecycle`: created | destroyed
4. handler: `WHAT_CHECK_SURFACE`
5. check: currentPage without null surface
6. `new Connecter().start()`

## 底层

1. connect: `pthread_create(onPlayVideo`
2. changed: `is_connected = true`
3. normal O: `pthread_create(onPlayAudio`
4. disconn: nothing but log
5. changed: `is_connected = false`
6. video wait audio, max 10 * 500ms
7. callback quit

