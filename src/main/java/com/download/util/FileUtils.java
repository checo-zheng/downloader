package com.download.util;

import java.io.File;

/**
 * @author 憨憨
 */
public class FileUtils {
    public static long getFileSize(String path){
        File file = new File(path);

        return file.exists() && file.isFile()? file.length() : 0;

    }
}
