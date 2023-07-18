package com.download.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author 憨憨
 */
public class HttpUtils {

    public static long getHttpFileSize(String url) throws IOException {
        HttpURLConnection httpConnection = null;
        int contentLength;
        try{
            httpConnection = getHttpConnection(url);
            contentLength = httpConnection.getContentLength();
        }finally {
            if(httpConnection != null){
                httpConnection.disconnect();
            }
        }
        return contentLength;
    }


    public static HttpURLConnection getHttpConnection(String url,long startPoint,long endPoint) throws IOException {
        HttpURLConnection httpConnection = getHttpConnection(url);
        LogUtils.info("下载区间是{}-{}",startPoint,endPoint);
        if(endPoint != 0){
            httpConnection.setRequestProperty("RANGE","bytes=" + startPoint + "-" + endPoint);
        }else {
            httpConnection.setRequestProperty("RANGE","bytes=" + startPoint + "-");
        }

        return httpConnection;
    }

    public static HttpURLConnection getHttpConnection(String url) throws IOException {
        if(!url.startsWith("https://")) url =  "https://" + url;
        URL httpUrl =  new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection)httpUrl.openConnection();
//        urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1.0; Win64; x64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
        setHeader(urlConnection);
        return  urlConnection;
    }

    public static String getFileName(String url){
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        return fileName;
    }

    private static void setHeader(URLConnection con) {
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.3) Gecko/2008092510 Ubuntu/8.04 (hardy) Firefox/3.0.3");
        con.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
        con.setRequestProperty("Accept-Encoding", "aa");
        con.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        con.setRequestProperty("Keep-Alive", "300");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("If-Modified-Since", "Fri, 02 Jan 2009 17:00:05 GMT");
        con.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Referer", "http://www.dianping.com");
    }
}
