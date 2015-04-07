
package com.jovision.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MobileUtil {

    /**
     * 收集手机信息
     * 
     * @param context
     * @return
     */
    public static String mobileSysVersion(Context context) {
        String mobileVersion = "";
        try {
            // String model = android.os.Build.MODEL;
            mobileVersion = android.os.Build.VERSION.RELEASE;
            // String fingerprint = android.os.Build.FINGERPRINT;
            // String ip = getIP();
            // String cpu = Build.CPU_ABI;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mobileVersion;

    }

    /**
     * 获取手机IP
     * 
     * @return
     */
    public static String getIP() {
        String ip = "";
        BufferedReader in = null;
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            ip = in.readLine();

        } catch (Exception e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ip;
    }

    /**
     * 获取剩余sdk卡空间
     * 
     * @return
     */
    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 递归创建文件目录
     * 
     * @author
     * @param path 要创建的目录路径
     */
    public static void createDirectory(File file) {

        if (file.exists()) {
            return;
        }
        File parentFile = file.getParentFile();
        if (null != file && parentFile.exists()) {
            if (parentFile.isDirectory()) {
            } else {
                parentFile.delete();
                boolean res = parentFile.mkdir();
                if (!res) {
                    parentFile.delete();
                }
            }

            boolean res = file.mkdir();
            if (!res) {
                file.delete();
            }

        } else {
            createDirectory(file.getParentFile());
            boolean res = file.mkdir();
            if (!res) {
                file.delete();
            }
        }
    }

    /**
     * 递归删除文件和文件夹,清空文件夹
     * 
     * @param file 要删除的根目录
     */
    public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

    /**
     * 根据文件存放url获取文件的绝对路径
     * 
     * @param fileUrl
     * @return
     */
    public static String getRealPath(Context mContext, Uri fileUrl) {
        String fileName = null;
        Uri filePathUri = fileUrl;
        if (fileUrl != null) {
            if (fileUrl.getScheme().toString().compareTo("content") == 0) // content://开头的uri
            {
                Cursor cursor = mContext.getContentResolver().query(fileUrl,
                        null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    fileName = cursor.getString(column_index); // 取出文件路径
                    // if (!fileName.startsWith("/mnt")) {
                    // // 检查是否有”/mnt“前缀
                    //
                    // fileName = "/mnt" + fileName;
                    // }
                    cursor.close();
                }
            } else if (fileUrl.getScheme().compareTo("file") == 0) { // file:///开头的uri

                fileName = filePathUri.toString();
                // fileName = filePathUri.toString().replace("file://", "");
                // // 替换file://
                // if (!fileName.startsWith("/mnt")) {
                // // 加上"/mnt"头
                // fileName += "/mnt";
                // }
            }
        }
        return fileName;
    }

    /**
     * [获取cpu类型和架构]
     * 
     * @return 三个参数类型的数组，第一个参数标识是不是ARM架构，第二个参数标识是V6还是V7架构，第三个参数标识是不是neon指令集
     */
    public static Object[] getCpuArchitecture() {
        Object[] mArmArchitecture = new Object[32];
        for (int i = 0; i < mArmArchitecture.length; i++) {
            mArmArchitecture[i] = "0";
        }
        if (Integer.parseInt(mArmArchitecture[1].toString()) != -1) {
            return mArmArchitecture;
        }
        try {
            InputStream is = new FileInputStream("/proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            try {
                String nameProcessor = "Processor";
                String nameFeatures = "Features";
                String nameModel = "model name";
                String nameCpuFamily = "cpu family";
                while (true) {
                    String line = br.readLine();
                    String[] pair = null;
                    if (line == null) {
                        break;
                    }
                    pair = line.split(":");
                    if (pair.length != 2)
                        continue;
                    String key = pair[0].trim();
                    String val = pair[1].trim();
                    if (key.compareTo(nameProcessor) == 0) {
                        String n = "";
                        for (int i = val.indexOf("ARMv") + 4; i < val.length(); i++) {
                            String temp = val.charAt(i) + "";
                            if (temp.matches("\\d")) {
                                n += temp;
                            } else {
                                break;
                            }
                        }
                        mArmArchitecture[0] = "ARM";
                        mArmArchitecture[1] = Integer.parseInt(n);
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameFeatures) == 0) {
                        if (val.contains("neon")) {
                            mArmArchitecture[2] = "neon";
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameModel) == 0) {
                        if (val.contains("Intel")) {
                            mArmArchitecture[0] = "INTEL";
                            mArmArchitecture[2] = "atom";
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameCpuFamily) == 0) {
                        mArmArchitecture[1] = Integer.parseInt(val);
                        continue;
                    }
                }
            } finally {
                br.close();
                ir.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mArmArchitecture;
    }
}
