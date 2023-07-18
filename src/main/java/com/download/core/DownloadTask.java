package com.download.core;

import com.download.constant.Constant;
import com.download.util.HttpUtils;
import com.download.util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;


/**
 * @author 憨憨
 */
public class DownloadTask implements Callable<Boolean> {

    public long startPoint;

    public long endPoint;

    public String url;

    public String part;

    public CountDownLatch countDownLatch;

    public DownloadTask(long startPoint, long endPoint, String url, String part, CountDownLatch countDownLatch) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.url = url;
        this.part = part;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws Exception {

        String fileName = HttpUtils.getFileName(url) + ".temp" + part;
        String DownloadPath = Constant.PATH + fileName;

        HttpURLConnection httpConnection = null;

        try {
            httpConnection = HttpUtils.getHttpConnection(url,startPoint,endPoint);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                InputStream input = httpConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(input);
                RandomAccessFile file = new RandomAccessFile(DownloadPath,"rw")

        ){
            int len = -1;
            byte[] buffer = new byte[Constant.BYTE_SIZE];
            while ((len = bis.read(buffer)) != -1){
                DownloadInfoThread.downSize.add(len);
                file.write(buffer,0,len);
            }
            LogUtils.info("分区" + part + "下载完成");
            countDownLatch.countDown();
        }catch (FileNotFoundException e){
            LogUtils.error("文件不存在{}",url);
            return false;
        } catch (Exception e) {
            LogUtils.error("下载失败");
            return false;
        } finally {
            if(httpConnection != null){
                httpConnection.disconnect();
            }
        }
        return true;
    }
}
