package com.download;

import com.download.core.Downloader;

import java.util.Scanner;

/**
 * @author 憨憨
 */
public class Main {

    public static void main(String[] args) {
        String url;
        if(args == null || args.length == 0){
            for(;;){
                System.out.println("请输入下载地址：");
                Scanner scanner = new Scanner(System.in);
                url = scanner.next();
                if(url != null) break;
            }
        }else{
            url = args[0];
        }

        Downloader downloader = new Downloader();
        downloader.download(url);
    }
}
