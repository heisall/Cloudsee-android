
package com.jovision.utils;

import com.jovision.commons.MyLog;
import com.sun.mail.iap.ConnectionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONUtil {

    private static final String TAG = "JSONUtil";

    public void stringToMap(String jsonStr) {

    }

    // /**
    // * 向api发送get请求，返回从后台取得的信息。
    // *
    // * @param url
    // * @return String
    // */
    // //get方法获取数据，超时时间为15秒
    // public static String getRequest(String url) {
    // // MyLog.e("tags 5555555555555555555555555", "getRequest(String url) ");
    // //String httpUrl = "http://www.baidu.com";
    // // HttpGet request = new HttpGet(url);
    //
    // HttpPost request = new HttpPost(url);
    // request.setHeader(HTTP.CONN_DIRECTIVE,HTTP.CONN_CLOSE);
    //
    // HttpClient httpClient = new DefaultHttpClient();
    // HttpParams params=httpClient.getParams(); //计算网络超时用.
    // params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
    //
    // String str = "";
    // try {
    // HttpConnectionParams.setConnectionTimeout(params, 15000);
    // HttpConnectionParams.setSoTimeout(params, 15000);
    // HttpResponse response = httpClient.execute(request);
    // if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    // str = EntityUtils.toString(response.getEntity());
    // // tv_rp.setText(str);
    // // Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    // return str;
    // } else {
    // // tv_rp.setText("请求错误");
    // return "";
    // }
    // }catch (ClientProtocolException e) {
    // e.printStackTrace();
    // } catch (IOException e) { // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return str;
    // }

    public static String getRequest(String url) {
        // MyLog.e("请求地址：", url);
        StringBuffer sBuffer = new StringBuffer();
        URL u = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        try {
            u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(45000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type",
                    "application/json; charset=UTF-8");
            if (conn.getResponseCode() == 400) {
                MyLog.e("服务器响应：", "400");
            } else if (conn.getResponseCode() == 200) {
                byte[] buf = new byte[1024];
                in = conn.getInputStream();
                for (int n; (n = in.read(buf)) != -1;) {
                    sBuffer.append(new String(buf, 0, n, "UTF-8"));
                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != conn) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // MyLog.e("返回结果：", sBuffer.toString());
        return sBuffer.toString();
    }

    public static String getRequest3(String url) {
        // MyLog.e("请求地址：", url);
        StringBuffer sBuffer = new StringBuffer();
        URL u = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        try {
            u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type",
                    "application/json; charset=UTF-8");
            if (conn.getResponseCode() == 400) {
                MyLog.e("服务器响应：", "400");
            } else if (conn.getResponseCode() == 200) {
                byte[] buf = new byte[1024];
                in = conn.getInputStream();
                for (int n; (n = in.read(buf)) != -1;) {
                    sBuffer.append(new String(buf, 0, n, "UTF-8"));
                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != conn) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // MyLog.e("返回结果：", sBuffer.toString());
        return sBuffer.toString();
    }

    // 保持在线
    public static String getRequest1(String url) {
        // MyLog.e("保持在线请求地址：", url);
        StringBuffer sBuffer = new StringBuffer();
        URL u = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        try {
            u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(45000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type",
                    "application/json; charset=utf-8");
            if (conn.getResponseCode() == 400) {
                MyLog.e("服务器响应：", "400");
            } else if (conn.getResponseCode() == 200) {
                byte[] buf = new byte[1024];
                in = conn.getInputStream();
                for (int n; (n = in.read(buf)) != -1;) {
                    sBuffer.append(new String(buf, 0, n, "UTF-8"));
                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.e("tags", "url: " + url + ", exception :" + e.getMessage());
        } finally {
            try {
                // MyLog.e("tags",
                // "-----------------------------------------------------");
                if (null != in) {
                    in.close();
                }
                if (null != conn) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // MyLog.e("保持在线返回结果：", sBuffer.toString());
        return sBuffer.toString();
    }

    /**
     * 处理httpResponse信息,返回String
     * 
     * @param httpEntity
     * @return String
     */
    protected static String retrieveInputStream(HttpEntity httpEntity) {

        int length = (int) httpEntity.getContentLength();
        // the number of bytes of the content, or a negative number if unknown.
        // If the content length is known but exceeds Long.MAX_VALUE, a negative
        // number is returned.
        // length==-1，下面这句报错，println needs a message
        if (length < 0)
            length = 10000;
        StringBuffer stringBuffer = new StringBuffer(length);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    httpEntity.getContent(), HTTP.UTF_8);
            char buffer[] = new char[length];
            int count;
            while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {

                stringBuffer.append(buffer, 0, count);
            }
        } catch (UnsupportedEncodingException e) {
            MyLog.e(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            MyLog.e(TAG, e.getMessage());
        } catch (IOException e) {
            MyLog.e(TAG, e.getMessage());
        }
        System.out.print(stringBuffer.toString());
        return stringBuffer.toString();
    }

    /**
     * 获取json内容
     * 
     * @param url
     * @return JSONArray
     * @throws JSONException
     * @throws ConnectionException
     */
    public static JSONArray getJSON(String url) {
        MyLog.e("url", url);
        String result = getRequest(url);
        MyLog.e("result", result);

        JSONArray array = null;
        try {
            array = new JSONArray(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    // http Post请求获取数据，超时时间是10秒
    public static String httpPost(String url, Map<String, String> map) {

        String result = "";
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        String key, value;
        if (null != map && 0 != map.size()) {
            for (int i = 0; i < map.size(); i++) {
                key = (String) map.keySet().toArray()[i];
                value = map.get(key);
                nameValuePairs.add(new BasicNameValuePair(key, value));
            }
        }

        HttpPost httpRequest = new HttpPost(url);
        try {
            HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs,
                    HTTP.UTF_8);
            httpRequest.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            // 请求超时
            client.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
            // 读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                    10000);
            HttpResponse response = client.execute(httpRequest);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
            } else {
                result = String.valueOf(response.getStatusLine()
                        .getStatusCode());
            }
        } catch (Exception e) {
            result = "";
            e.printStackTrace();
        }
        MyLog.e("feedback-Post请求-result", result);
        return result;
    }

    public static String httpGet(String url) {

        /* 建立HTTPGet对象 */

        String paramStr = "";

        if (!paramStr.equals("")) {
            paramStr = paramStr.replaceFirst("&", "?");
            url += paramStr;
        }
        HttpGet httpRequest = new HttpGet(url);

        MyLog.e("Get请求-requeset", url);

        String strResult = "doGetError";

        try {
            HttpClient httpClient = new DefaultHttpClient();
            /* 发送请求并等待响应 */
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            /* 若状态码为200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                /* 读返回数据 */
                strResult = EntityUtils.toString(httpResponse.getEntity());

            } else {
                strResult = "Error Response: "
                        + httpResponse.getStatusLine().toString();
            }
        } catch (ClientProtocolException e) {
            strResult = e.getMessage().toString();
            e.printStackTrace();
        } catch (IOException e) {
            strResult = e.getMessage().toString();
            e.printStackTrace();
        } catch (Exception e) {
            strResult = e.getMessage().toString();
            e.printStackTrace();
        }

        MyLog.e("Get请求-result", strResult);
        return strResult;
    }

}
