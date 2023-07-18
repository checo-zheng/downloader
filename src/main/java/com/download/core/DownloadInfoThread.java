package com.download.core;

import com.download.constant.Constant;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author 憨憨
 */
public class DownloadInfoThread implements Runnable {

    //文件总大小
    private long httpFileConLength;

    //本地已下载文件大小
    private double finishedSize;

    //本次下载大小
    public static volatile LongAdder downSize = new LongAdder();

    //前一次下载大小
    private double preSize;

    public DownloadInfoThread(long httpFileConLength){
        this.httpFileConLength = httpFileConLength;
    }


    @Override
    public void run() {
        String fileSize = String.format("%.2f",httpFileConLength / Constant.MB);

        String currentSize = String.format("%.2f",downSize.doubleValue() / Constant.MB);

        int speed = (int) ((downSize.doubleValue() - preSize)/1024d);
        preSize = downSize.doubleValue();

        double remainSize = httpFileConLength - finishedSize - downSize.doubleValue();

        String remainTime = String.format("%.1f", (remainSize / 1024d / speed));

        if ("Infinity".equalsIgnoreCase(remainTime)){
            remainTime = "-";
        }

        String downloadInfo = String.format("%s:已下载：%sMB/ %sMB, 下载速度：%skb/s, 剩余时间：%ss",
                Thread.currentThread().getName(),currentSize,fileSize,speed,remainTime);

        System.out.print("\r");
        System.out.print(downloadInfo);
    }
}
