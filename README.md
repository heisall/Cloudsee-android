# 2014年 09月 17日

新版 CloudSEE 仓库，含重构后的播放库，新应用层逻辑(未完结)

播放库版本

    #define MY_VERSION    "0.6"
    #define REVISION      "[761a78f]"
    #define RELEASE_DATE  "[2014-09-17]"

内置网络库版本

    v2.0.76.3.5[private:v2.0.75.13 20140917.1]
    
## 播放库架构

播放库主要功能

+ 局域网搜索，仅全部网络旧接口
+ 使用链接小助手链接视频源，播放视频源、远程回放
+ 实时、手动切换 **软硬解码器** 播放
+ 统计每个通道网络传输比特率、帧率，**内置播放速度控制**
+ 视频显示可定制放大、缩小
+ 视频截图功能
+ 录制视频源的 **视频和音频** 到本地
+ 向设备直接发送命令
+ 编码 PCM 至 g711 或 amr

播放库调用流程

1. 初始化
2. 局域网搜索，可选
3. 设置需要连接设备的链接小助手
4. 调用连接，等待指定键值的回调
5. 播放过程中可截图、录像、切换软硬解、发送命令
6. 编码音频可独立使用
7. 断开所有连接并接收指定回调
8. 反初始化

文件树结构

+ `depends`: 解码、显示、网络库等依赖头文件
+ `libs`: 依赖静态库文件
+ `defines.h`: 全局定义头文件
+ `play.*`: 应用层对应接口及实现
+ `utils/commons.*`: 底层通用方法
+ `utils/callbacks.*`: 网络库回调函数
+ `utils/threads.*`: 内部线程函数

回调说明

ConnectChange

    id, type, channel, json
    json: {"msg":"", "data":0}

PlayData

    id, type, channel, json
    json: {"count":0,
        "audio_type":0, "audio_sample_rate":0, "audio_bit":0, "audio_channel":0,
        "width":0, "height":0, "fps":0.0, "total":0}

    caution: type == PLAYOVER/PLAYE/PLTIMEOUT doesn't mean play stopped!

NormalData

    id, type, channel, json
    json: {"is05":true, "fps":0.0, "device_type":0, "start_code":0,
        "reserved":0, "width":0, "height":0, "auto_stop_recorder":false,
        "audio_type":0, "audio_sample_rate":0, "audio_bit":0, "audio_channel":0}

CheckResult

    id, 0, channel, byte[]

ChatData

    id, type, channel, byte[]

TextData

    id, type, channel, jsonArray
    jsonArray: [{"result":0, "msg":"", "flag":0, "type":0},
        {"name":"", "pwd":"", "quality":0, "keystat":0, "auth":0, "env":0}]

SearchLanServer

    id, 0, 0, json
    json: {"ip":"", "gid":"", "no":0, "type":0, "count":0,
        "port":0, "variety":0, "timeout":0, "netmod":0, "curmod":0}

queryDevice

    id, 0, 0, json
    json: {"type":0, "gid":"", "no":0, "ip":"", "port":0}

stat

    id, 0, 0, jsonArray
    jsonArray: [{"index":0, "delay":0.0, "kbps":0.0,
        "audio_type":1, "audio_kbps":0.0,
        "audio_network_fps":0.0, "audio_decoder_fps":0.0,
        "audio_decoder_delay":0.0, "audio_play_delay":0.0, 
        "network_fps":0.0, "decoder_fps":0.0, "jump_fps":0.0,
        "decoder_delay":0.0, "render_delay":0.0,
        "space":0, "width":0, "height":0, "left":0,
        "is_omx":false, "is_turn":false, "is_playback":false}]

play

    // screenshot
    id, index, result, null

    // frame I report
    id, index, queue_left, null

    // play doomed
    id, index, bad_status, null
    id, index, playback_done, null

    // play audio
    id, index, is_play_back, byte[]
    byte: pcm raw data

## 应用层架构

包分布

+ 全局常量、Jni 接口等都定义在 `com.jovision` 包中
+ 活动都在 `com.jovision.activities` 包中
+ 集合类都在 `com.jovision.beans` 包中
+ 通用方法都在 `com.jovision.commons` 包中，可再细分 `net`，`resource` 等子包

消息分发

+ MainApplication 负责消息分发，包括底层所有回调和应用层活动之间的通信
+ BaseActivity 作为所有活动基类，实现消息分发、生命周期规划等
+ 每个活动必须实现 `onHandler` 和 `onNotify` 接口，处理消息
+ 每个活动将对应事务写入 `iniSettings`、`initUi`、`saveSettings` 和 `freeMe`

待完善

