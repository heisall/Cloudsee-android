
package com.jovision.commons;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger, can output into files
 * 
 * @author neo
 */
public class MyLog {

    public static final String UB = "UB.log";

    public static final String KEY_NAME = "name";
    public static final String KEY_DESC = "desc";
    public static final String KEY_IMEI = "imei";
    public static final String KEY_VENDOR = "vendor";
    public static final String KEY_MODEL = "model";
    public static final String KEY_FP = "fingerprint";
    public static final String KEY_TOPIC = "topic";
    public static final String KEY_COUNT = "count";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_ENV = "env";
    public static final String KEY_TYPE = "type";

    public static final String TYPE_TOPIC = "topic";
    public static final String TYPE_PHONE = "phone";
    public static final String TYPE_LOG = "log";
    public static final String TYPE_STAT = "stat";

    private static final String V = "V";
    private static final String D = "D";
    private static final String I = "I";
    private static final String W = "W";
    private static final String E = "e";
    private static final String Ex = "E";

    private static final String RW = "rw";
    private static final String TAG = "MyLog";
    private static final String LINE_SEPARATOR = "\n";
    private static final String FORMATTER = "yyyy-MM-dd HH:mm:ss.SSS";

    private static String FOLDER = null;

    private static boolean ENABLE_UB = true;
    private static boolean ENABLE_FILE = true;
    private static boolean ENABLE_LOGCAT = true;

    private static File checkTag(String tag) {
        File result = null;

        if (null != FOLDER && init(FOLDER)) {
            result = new File(FOLDER + File.separator + tag);
            if (false == result.exists()) {
                try {
                    result.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private static String prepare(String level, String msg) {
        StringBuilder sBuilder = new StringBuilder(msg.length() + 64);
        sBuilder.append(new SimpleDateFormat(FORMATTER).format(new Date()))
                .append(File.pathSeparatorChar);

        if (null != level) {
            sBuilder.append(level).append(File.separatorChar);
        }

        if (null != msg) {
            sBuilder.append(msg);
        }

        sBuilder.append(LINE_SEPARATOR);
        return sBuilder.toString();
    }

    private static synchronized boolean append(File file, String string) {
        boolean result = false;

        if (null != file) {
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(file.getPath(), RW);
                randomAccessFile.seek(randomAccessFile.length());
                randomAccessFile.writeBytes(string);
                randomAccessFile.close();
                randomAccessFile = null;
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                        randomAccessFile = null;
                    } catch (IOException e2) {
                    }
                }

                if (false == result) {
                    Log.e(TAG, "append failed when write");
                    ENABLE_FILE = false;
                }
            }
        } else {
            Log.e(TAG, "append without file");
        }

        return result;
    }

    public static void v(String tag, String msg) {
        if (ENABLE_LOGCAT) {
            Log.v(tag, msg);
        }

        if (ENABLE_FILE) {
            append(checkTag(tag), prepare(V, msg));
        }
    }

    public static void d(String tag, String msg) {
        if (ENABLE_LOGCAT) {
            Log.d(tag, msg);
        }

        if (ENABLE_FILE) {
            append(checkTag(tag), prepare(D, msg));
        }
    }

    public static void i(String tag, String msg) {
        if (ENABLE_LOGCAT) {
            Log.i(tag, msg);
        }

        if (ENABLE_FILE) {
            append(checkTag(tag), prepare(I, msg));
        }
    }

    private static void ub(String msg) {
        if (ENABLE_UB && null != msg) {
            append(checkTag(UB), prepare(null, msg));
            Log.d(UB, msg);
        }
    }

    private static String checkString(String string) {
        String result = "";

        if (false == TextUtils.isEmpty(string)) {
            result = string;
        }

        return result;
    }

    public static void ubPhone(String imei, String vendor, String model,
            String fingerprint) {
        StringBuilder sBuilder = new StringBuilder(256);
        sBuilder.append("{\"").append(KEY_TYPE).append("\":\"")
                .append(TYPE_PHONE).append("\",\"").append(KEY_IMEI)
                .append("\":\"").append(checkString(imei)).append("\",\"")
                .append(KEY_VENDOR).append("\":\"").append(checkString(vendor))
                .append("\",\"").append(KEY_MODEL).append("\":\"")
                .append(checkString(model)).append("\",\"").append(KEY_FP)
                .append("\":\"").append(checkString(fingerprint)).append("\"}");
        ub(sBuilder.toString());
    }

    public static void ubTopic(String name, String desc) {
        StringBuilder sBuilder = new StringBuilder(256);
        sBuilder.append("{\"").append(KEY_TYPE).append("\":\"")
                .append(TYPE_TOPIC).append("\",\"").append(KEY_NAME)
                .append("\":\"").append(checkString(name)).append("\",\"")
                .append(KEY_DESC).append("\":\"").append(checkString(desc))
                .append("\"}");
        ub(sBuilder.toString());
    }

    public static void ubLog(String topic) {
        ubLog(topic, null);
    }

    public static void ubLog(String topic, String env) {
        StringBuilder sBuilder = new StringBuilder(1024);
        sBuilder.append("{\"").append(KEY_TYPE).append("\":\"")
                .append(TYPE_LOG).append("\",\"").append(KEY_TOPIC)
                .append("\":\"").append(checkString(topic)).append("\",\"")
                .append(KEY_ENV).append("\":\"").append(checkString(env))
                .append("\"}");
        ub(sBuilder.toString());
    }

    public static void ubStat(String topic, int duration) {
        StringBuilder sBuilder = new StringBuilder(256);
        sBuilder.append("{\"").append(KEY_TYPE).append("\":\"")
                .append(TYPE_STAT).append("\",\"").append(KEY_TOPIC)
                .append("\":\"").append(checkString(topic)).append("\",\"")
                .append(KEY_DURATION).append("\":").append(duration)
                .append("}");
        ub(sBuilder.toString());
    }

    public static String getPath(String tag) {
        String filePath = null;

        File file = checkTag(tag);
        if (null != file && file.exists()) {
            filePath = file.getPath();
        }

        return filePath;
    }

    public static String getFolder(String tag) {
        String filePath = null;

        File file = checkTag(tag);
        if (null != file && file.exists()) {
            filePath = file.getParent();
        }

        return filePath;
    }

    public static boolean clean(String tag) {
        boolean result = false;

        File file = checkTag(tag);
        if (null != file && file.exists()) {
            result = file.delete();
        }

        return result;
    }

    public static void w(String tag, String msg) {
        // if (ENABLE_LOGCAT) {
        Log.w(tag, msg);
        // }

        if (ENABLE_FILE) {
            append(checkTag(tag), prepare(W, msg));
        }
    }

    public static void e(String tag, String msg) {
        // if (ENABLE_LOGCAT) {
        Log.e(tag, msg);
        // }

        if (ENABLE_FILE) {
            append(checkTag(tag), prepare(E, msg));
        }
    }

    public static void e(String tag, Exception e) {
        StringBuilder sBuilder = new StringBuilder(16 * 1024);
        sBuilder.append(e.getMessage()).append(LINE_SEPARATOR);

        StackTraceElement[] elements = e.getStackTrace();
        int size = elements.length;

        for (int i = 0; i < size; i++) {
            sBuilder.append(elements[i].getClassName()).append(".")
                    .append(elements[i].getMethodName()).append("@")
                    .append(elements[i].getLineNumber()).append(LINE_SEPARATOR);
        }

        String msg = sBuilder.toString();

        if (ENABLE_LOGCAT) {
            Log.e(tag, msg);
        }

        if (ENABLE_FILE) {
            append(checkTag(tag), prepare(Ex, msg));
        }
    }

    /**
     * set log files' folder
     * 
     * @param logPath
     * @return
     */
    public static boolean init(String logPath) {
        boolean result = false;

        File folder = new File(logPath);
        if (false == folder.exists()) {
            result = folder.mkdirs();
        } else {
            result = true;
        }

        if (result) {
            result = false;
            if (folder.canRead() && folder.canWrite() && folder.isDirectory()) {
                FOLDER = logPath;
                result = true;
            }
        }

        return result;
    }

    public static void enableUB(boolean enable) {
        ENABLE_UB = enable;
    }

    public static void enableFile(boolean enable) {
        ENABLE_FILE = enable;
    }

    public static void enableLogcat(boolean enable) {
        ENABLE_LOGCAT = enable;
    }

}
