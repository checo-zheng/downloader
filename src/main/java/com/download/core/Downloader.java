package com.download.core;

import com.download.constant.Constant;
import com.download.util.FileUtils;
import com.download.util.HttpUtils;
import com.download.util.LogUtils;
import org.omg.PortableServer.THREAD_POLICY_ID;

import java.beans.FeatureDescriptor;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 憨憨
 */
public class Downloader {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Constant.THREAD_NUM,Constant.THREAD_NUM,5,TimeUnit.SECONDS,new ArrayBlockingQueue<>(Constant.THREAD_NUM),new DownloadThreadFactory(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            return thread;
        }
    }, "download-pool"));

    private CountDownLatch countDownLatch = new CountDownLatch(Constant.THREAD_NUM);

    public void download(String url){
        String fileName = HttpUtils.getFileName(url);
        String DownloadPath = Constant.PATH + fileName;

        long fileSize = FileUtils.getFileSize(DownloadPath);

        HttpURLConnection httpConnection = null;
        DownloadInfoThread downloadInfoThread = null;

        try {
            httpConnection = HttpUtils.getHttpConnection(url);

            int contentLength = httpConnection.getContentLength();
            if(fileSize >= contentLength){
                LogUtils.info("{}文件已下载",fileName);
                return;
            }
            downloadInfoThread = new DownloadInfoThread(contentLength);
            scheduledExecutorService.scheduleAtFixedRate(downloadInfoThread,1,1, TimeUnit.SECONDS);
            List<Future> futures = new ArrayList<>(Constant.THREAD_NUM);

            splitDownload(url,futures);

            countDownLatch.await();

            mergeFile(DownloadPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(httpConnection != null){
                httpConnection.disconnect();
            }

            scheduledExecutorService.shutdown();
            threadPool.shutdown();
            System.out.print("\r");
            System.out.print(Thread.currentThread().getName() + "下载完成");
        }
    }

    private void splitDownload(String url,List<Future> booleans){
        try {
            long httpFileSize = HttpUtils.getHttpFileSize(url);
            long size = httpFileSize / Constant.THREAD_NUM;
            for(int i = 0; i < Constant.THREAD_NUM; i++){
                long startPoint = i * size;
                long endPoint = startPoint + size - 1;
                DownloadTask downloadTask = null;
                if(i == Constant.THREAD_NUM - 1){
                    downloadTask = new DownloadTask(startPoint,0,url,String.valueOf(i),countDownLatch);
                }else {
                    downloadTask = new DownloadTask(startPoint, endPoint, url, String.valueOf(i),countDownLatch);
                }
                Future<Boolean> result = threadPool.submit(downloadTask);
                booleans.add(result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mergeFile(String fileName){
        try {
            RandomAccessFile file = new RandomAccessFile(fileName, "rw");
            for(int i = 0; i < Constant.THREAD_NUM; i++){
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName + ".temp" + i));
                int len = -1;
                byte[] buffer = new byte[Constant.BYTE_SIZE];
                while ((len = bis.read(buffer)) != -1){
                    file.write(buffer,0,len);
                }
                bis.close();
                File file1 = new File(fileName + ".temp" + i);
                boolean delete = file1.delete();
                if (!delete){
                    LogUtils.error("删除文件出错--{}",fileName + ".temp" + i);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
