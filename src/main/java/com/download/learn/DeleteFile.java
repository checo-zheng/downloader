package com.download.learn;

import java.io.File;

/**
 * @author 憨憨
 */
public class DeleteFile {
    public static void main(String[] args) {
        File file = new File("D:\\downloadTEst\\QQ9.7.12.29094.exe.temp0");
        boolean delete = file.delete();
        System.out.println(delete);
    }
}
