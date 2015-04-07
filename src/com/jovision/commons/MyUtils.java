
package com.jovision.commons;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * My util functions
 * 
 * @author neo
 */
public class MyUtils {

    public static int ip2int(String ip) {
        int result = 0;

        try {
            byte[] bytes = InetAddress.getByName(ip).getAddress();

            int size = bytes.length;
            result = bytes[0] & 0xFF;
            for (int i = 1; i < size; i++) {
                result <<= 8;
                result += bytes[i] & 0xFF;
            }
        } catch (UnknownHostException e) {
            // [Neo] Empty
        }

        return result;
    }

    public static boolean zip(String target, File[] files) {
        boolean result = false;

        File file = new File(target);

        if (file.exists()) {
            file.delete();
        }

        InputStream inputStream = null;
        ZipOutputStream outputStream = null;

        try {
            int perLength = 0;
            outputStream = new ZipOutputStream(new FileOutputStream(file));
            for (int i = 0; i < files.length; ++i) {
                if (false == files[i].exists()) {
                    continue;
                }
                inputStream = new FileInputStream(files[i]);
                outputStream.putNextEntry(new ZipEntry(files[i].getName()));
                while (-1 != (perLength = inputStream.read())) {
                    outputStream.write(perLength);
                }
                inputStream.close();
            }
            outputStream.close();
            result = true;
        } catch (Exception e) {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (IOException e1) {
            }
        }

        if (false == result && file.exists()) {
            file.delete();
        }

        return result;
    }

    public static boolean unzip(String target, String outFolder) {
        boolean result = false;

        File file = new File(target);

        ZipFile zipFile = null;
        ZipEntry entry = null;
        ZipInputStream zipInputStream = null;

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            zipFile = new ZipFile(file);
            zipInputStream = new ZipInputStream(new FileInputStream(file));

            int perLength = 0;
            File outFile = null;

            while (null != (entry = zipInputStream.getNextEntry())) {
                // [Neo] work with entry.getName()
                outFile = new File(outFolder + File.separator + entry.getName());

                if (false == outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdirs();
                }

                if (outFile.exists()) {
                    outFile.delete();
                }

                inputStream = zipFile.getInputStream(entry);
                outputStream = new FileOutputStream(outFile);

                while (-1 != (perLength = inputStream.read())) {
                    outputStream.write(perLength);
                }

                inputStream.close();
                outputStream.close();
            }

            zipInputStream.close();
            zipFile.close();
            result = true;

        } catch (Exception e) {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
                if (null != zipInputStream) {
                    zipInputStream.close();
                }
                if (null != zipFile) {
                    zipFile.close();
                }
            } catch (Exception e2) {
            }
        }

        return result;
    }

    public static boolean hasConnected(Context context) {
        boolean result = false;

        ConnectivityManager manager = (ConnectivityManager) (context
                .getSystemService(Context.CONNECTIVITY_SERVICE));

        result = (State.CONNECTED == manager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState())
                || (State.CONNECTED == manager.getNetworkInfo(
                        ConnectivityManager.TYPE_MOBILE).getState());

        return result;
    }

    /**
     * depends on baidu open api
     * 
     * @return
     */
    public static Date getChinaTime() {
        Date date = null;
        String target = null;

        try {
            final String url = "http://open.baidu.com/special/time/";
            final String enc = "UTF-8";
            final String ua = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)";

            HttpURLConnection connection = (HttpURLConnection) (new URL(url)
                    .openConnection());
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("User-Agent", ua);
            connection.setRequestProperty("Charset", enc);
            connection.setConnectTimeout(2 * 1000);
            connection.setReadTimeout(2 * 1000);
            InputStream inputStream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, enc), 512);

            target = reader.readLine();
            while (null != target) {
                target = reader.readLine();

                if (target.contains("window.baidu_time(")) {
                    int start = target.lastIndexOf("(");
                    int end = target.lastIndexOf(")");

                    if (start > 0 && end > 0 && end > start) {
                        target = target.substring(start + 1, end);
                        break;
                    }
                }
            }

            reader.close();

        } catch (Exception e) {
            // [Neo] Empty
        }

        try {
            long timestamp = Long.parseLong(target);
            date = new Date(timestamp);
        } catch (Exception e) {
        }

        return date;
    }

    public static String md5(String filePath) {
        MessageDigest digest = null;
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(new File(filePath));
            ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);
            FileChannel channel = inputStream.getChannel();

            digest = MessageDigest.getInstance("MD5");
            digest.reset();

            int perLength = channel.read(buffer);
            while (-1 != perLength) {
                buffer.flip();
                digest.update(buffer);
                if (false == buffer.hasRemaining()) {
                    buffer.clear();
                }

                perLength = channel.read(buffer);
            }

            inputStream.close();
            inputStream = null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }

        if (null != digest) {
            byte[] bytes = digest.digest();
            StringBuilder sBuilder = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sBuilder.append(String.format("%02X", bytes[i]));
            }
            return sBuilder.toString();
        } else {
            return null;
        }
    }

}
