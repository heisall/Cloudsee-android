
package com.jovision.utils;

import com.jovision.utils.FileUtils.onDownloadListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Project: Android_Download
 * @Desciption:
 * @Author: LinYiSong
 * @Date: 2011-3-25~2011-3-25
 */
public class HttpDownloader {

    private URL url = null;

    public String download(String urlStr) {
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader buffer = null;
        try {
            url = new URL(urlStr);

            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection();

            buffer = new BufferedReader(new InputStreamReader(
                    urlConn.getInputStream()));

            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public int httpJudge(String urlStr) {
        int responseCode = -1;
        // try {
        // url = new URL(urlStr);
        // Log.e("Http", "urlStr:"+urlStr);
        // HttpURLConnection urlConn = (HttpURLConnection) url
        // .openConnection();
        // responseCode = urlConn.getResponseCode();
        // } catch (Exception e) {
        // e.printStackTrace();
        // responseCode = -1;
        // }
        // httpGet连接对象
        HttpGet httpRequest = new HttpGet(urlStr);
        // 取得HttpClient 对象
        HttpClient httpclient = new DefaultHttpClient();
        try {
            // 请求httpClient ，取得HttpRestponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            // if(httpResponse.getStatusLine().getStatusCode() ==
            // HttpStatus.SC_OK){
            // 取得相关信息 取得HttpEntiy
            // HttpEntity httpEntity = httpResponse.getEntity();
            // //获得一个输入流
            // InputStream is = httpEntity.getContent();
            // System.out.println(is.available());
            // System.out.println("Get, Yes!");
            // Bitmap bitmap = BitmapFactory.decodeStream(is);
            // is.close();
            // iv.setImageBitmap(bitmap);
            // }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseCode;
    }

    public int downFile(String urlStr, String path, String fileName, onDownloadListener listener) {

        InputStream inputStream = null;
        int responseCode = -1;
        try {
            FileUtils fileUtils = new FileUtils();

            // if (fileUtils.isFileExist(path + fileName)) {
            // System.out.println("exits");
            // return 1;
            // } else {
            // inputStream = getInputStreamFromURL(urlStr);
            // HttpURLConnection urlConn = null;
            //
            // try {
            // url = new URL(urlStr);
            // urlConn = (HttpURLConnection) url.openConnection();
            // responseCode = urlConn.getResponseCode();
            // if (responseCode == 200) {
            // int length = urlConn.getContentLength();
            // inputStream = urlConn.getInputStream();
            // } else {
            // return responseCode;
            // }
            // } catch (MalformedURLException e) {
            // e.printStackTrace();
            // return -1;
            // } catch (IOException e) {
            // e.printStackTrace();
            // return -1;
            // }
            // httpGet连接对象
            HttpGet httpRequest = new HttpGet(urlStr);
            // 取得HttpClient 对象
            HttpClient httpclient = new DefaultHttpClient();
            try {
                // 请求httpClient ，取得HttpRestponse
                HttpResponse httpResponse = httpclient.execute(httpRequest);
                responseCode = httpResponse.getStatusLine().getStatusCode();
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    // 取得相关信息 取得HttpEntiy
                    HttpEntity httpEntity = httpResponse.getEntity();
                    // 获得一个输入流
                    inputStream = httpEntity.getContent();
                    // System.out.println(is.available());
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // System.out.println("inpustStream:"+inputStream);
            fileUtils.setOnDownloadListener(listener);
            File resultFile = fileUtils.write2SDFromInput(path, fileName,
                    inputStream);
            if (resultFile == null) {
                return -1;
            }
            // }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        responseCode = 0;
        return 0;
    }

    // public InputStream getInputStreamFromURL(String urlStr) {
    // HttpURLConnection urlConn = null;
    // InputStream inputStream = null;
    // try {
    // url = new URL(urlStr);
    // urlConn = (HttpURLConnection) url.openConnection();
    // int length = urlConn.getContentLength();
    // Log.e("download", "length:----------" + length);
    // inputStream = urlConn.getInputStream();
    //
    // } catch (MalformedURLException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // return inputStream;
    // }
}
