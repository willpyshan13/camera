package com.meitu.meiyancamera.Utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.meitu.core.processor.FilterProcessor;
import com.meitu.core.types.NativeBitmap;
import com.meitu.meiyancamera.Constant.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by meitu on 2017/3/20.
 */

public class BitmapUtil {
    private final static String TAG ="BitmapUtil";

    public static NativeBitmap loadBitmap(String path,int maxSize){

        NativeBitmap bitmap = NativeBitmap.createBitmap(path, maxSize);
        return bitmap;
    }



    public static void saveOriginalBitmap(Bitmap bitmap,String bitmapPath){
        try {
            File file = new File(FileUtil.makeFileAndGetPath(Constant.BITMAP_CACHE_PATHNAME)+FileUtil.getFileName(bitmapPath)+"_meiyan" + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.e(TAG,"saveBitmap:");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

//    public static String saveEffectBitmapCache(Bitmap bitmap,String bitmapPath){
//
//        try {
//            File file = new File(FileUtil.getInnerSDCardCachePath()+FileUtil.getFileName(bitmapPath) +".jpg");
//            FileOutputStream out = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//            Log.e(TAG,"saveBitmap:"+file.getName());
//            return file.getAbsolutePath();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }

        public static void saveEffectBitmapCache(String bitmapPath,int type,float alpha) {
            NativeBitmap bitmap = NativeBitmap.createBitmap(bitmapPath, 0);
            FilterProcessor.renderProc(bitmap, type, alpha);
            try {
                File file = new File(FileUtil.getInnerSDCardCachePath() + FileUtil.getFileName(bitmapPath) + "_save" + ".jpg");
                FileOutputStream out = new FileOutputStream(file);
                bitmap.getImage().compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                Log.e(TAG, "saveBitmap:" + file.getName());

            } catch (IOException e) {
                e.printStackTrace();

            }


        }
}

