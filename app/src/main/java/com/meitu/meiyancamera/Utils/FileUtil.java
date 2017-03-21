package com.meitu.meiyancamera.Utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by ZerO on 2017/3/17.
 */

public class FileUtil {


    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/meiyan/";
    }

    public static String getInnerSDCardCachePath() {
//        Log.e()
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/meiyan/meiyancache/";
    }

    public static String makeFileAndGetPath(String fileName){
        File file=new File(getInnerSDCardPath()+fileName);
        if(!file.exists()){
            file.mkdir();
        }
        Log.e("filepath",file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static String getFileName(String path){
        int start=path.lastIndexOf("/");
        int end=path.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return path.substring(start+1,end);
        }else{
            return null;
        }

    }

    public static boolean fileIsExists(String filePath){
        File f=new File(filePath);
        Log.e("FileUtil",f.getAbsolutePath());
        if(!f.exists()){
            return false;
        }
        return true;
    }
}



