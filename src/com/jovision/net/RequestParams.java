/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.jovision.net;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求参数封装类
 * 
 * @author wby
 */
public class RequestParams {
    private static String ENCODING = "UTF-8";

    protected ConcurrentHashMap<String, String> urlParams;
    protected ConcurrentHashMap<String, FileWrapper> fileParams;

    /**
     * Constructs a new empty <code>RequestParams</code> instance.
     */
    public RequestParams() {
        init();
    }

    /**
     * Constructs a new RequestParams instance containing the key/value string
     * params from the specified map.
     * 
     * @param source the source key/value string map to add.
     */
    public RequestParams(Map<String, String> source) {
        init();

        for (Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Constructs a new RequestParams instance and populate it with a single
     * initial key/value string param.
     * 
     * @param key the key name for the intial param.
     * @param value the value string for the initial param.
     */
    public RequestParams(String key, String value) {
        init();

        put(key, value);
    }

    /**
     * Adds a key/value string pair to the request.
     * 
     * @param key the key name for the new param.
     * @param value the value string for the new param.
     */
    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    /**
     * Adds a file to the request.
     * 
     * @param key the key name for the new param.
     * @param file the file to add.
     */
    public void put(String key, File file) throws FileNotFoundException {
        put(key, new FileInputStream(file), file.getName());
    }

    /**
     * Adds an input stream to the request.
     * 
     * @param key the key name for the new param.
     * @param stream the input stream to add.
     */
    public void put(String key, InputStream stream) {
        put(key, stream, null);
    }

    /**
     * Adds an input stream to the request.
     * 
     * @param key the key name for the new param.
     * @param stream the input stream to add.
     * @param fileName the name of the file.
     */
    public void put(String key, InputStream stream, String fileName) {
        put(key, stream, fileName, null);
    }

    /**
     * Adds an input stream to the request.
     * 
     * @param key the key name for the new param.
     * @param stream the input stream to add.
     * @param fileName the name of the file.
     * @param contentType the content type of the file, eg. application/json
     */
    public void put(String key, InputStream stream, String fileName,
            String contentType) {
        if (key != null && stream != null) {
            fileParams.put(key, new FileWrapper(stream, fileName, contentType));
        }
    }

    /**
     * Removes a parameter from the request.
     * 
     * @param key the key name for the parameter to remove.
     */
    public void remove(String key) {
        urlParams.remove(key);
        fileParams.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams
                .entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams
                .entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        return result.toString();
    }

    /**
     * get a HttpEntity for the request
     * 
     * @return HttpEntity
     */
    HttpEntity getEntity() {
        HttpEntity entity = null;

        if (!fileParams.isEmpty()) {
            SimpleMultipartEntity multipartEntity = new SimpleMultipartEntity();

            // Add string params
            for (ConcurrentHashMap.Entry<String, String> entry : urlParams
                    .entrySet()) {
                multipartEntity.addPart(entry.getKey(), entry.getValue());
            }

            // Add file params
            int currentIndex = 0;
            int lastIndex = fileParams.entrySet().size() - 1;
            for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams
                    .entrySet()) {
                FileWrapper file = entry.getValue();
                if (file.inputStream != null) {
                    boolean isLast = currentIndex == lastIndex; // 是否添加到最后一个
                    if (file.contentType != null) {
                        multipartEntity.addPart(entry.getKey(),
                                file.getFileName(), file.inputStream,
                                file.contentType, isLast);
                    } else {
                        multipartEntity.addPart(entry.getKey(),
                                file.getFileName(), file.inputStream, isLast);
                    }
                }
                currentIndex++;
            }

            entity = multipartEntity;
        } else {
            try {
                entity = new UrlEncodedFormEntity(getParamsList(), ENCODING);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return entity;
    }

    /**
     * 初始化参数列表集合对象
     */
    private void init() {
        urlParams = new ConcurrentHashMap<String, String>();
        fileParams = new ConcurrentHashMap<String, FileWrapper>();
    }

    /**
     * 添加字符请求参数
     * 
     * @return 参数集合
     */
    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for (ConcurrentHashMap.Entry<String, String> entry : urlParams
                .entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey().toString(), entry
                    .getValue().toString()));
        }

        return lparams;
    }

    /**
     * 获得指定编码格式的参数字符串
     * 
     * @return 参数字符串
     */
    protected String getParamString() {
        return URLEncodedUtils.format(getParamsList(), ENCODING);
    }

    /**
     * 文件包装类
     */
    private static class FileWrapper {
        public InputStream inputStream; // 文件流
        public String fileName; // 文件名称
        public String contentType; // 请求时文件的类型

        public FileWrapper(InputStream inputStream, String fileName,
                String contentType) {
            this.inputStream = inputStream;
            this.fileName = fileName;
            this.contentType = contentType;
        }

        public String getFileName() {
            if (fileName != null) {
                return fileName;
            } else {
                return "nofilename";
            }
        }
    }

    public static String getENCODING() {
        return ENCODING;
    }

    public static void setENCODING(String eNCODING) {
        ENCODING = eNCODING;
    }

    public ConcurrentHashMap<String, String> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(ConcurrentHashMap<String, String> urlParams) {
        this.urlParams = urlParams;
    }

    public ConcurrentHashMap<String, FileWrapper> getFileParams() {
        return fileParams;
    }

    public void setFileParams(ConcurrentHashMap<String, FileWrapper> fileParams) {
        this.fileParams = fileParams;
    }

}
