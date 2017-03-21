package com.meitu.meiyancamera.Utils;

import android.graphics.Bitmap;

import com.meitu.core.types.NativeBitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * desc
 *
 * @Author zero
 * Created time 2017/3/20 下午9:15.
 */
public class ImageCache {
    private HashMap<Integer ,NativeBitmap> sEffectBitmapCache = new HashMap<>();
    private List<NativeBitmap> sHistoryBitmapCache = new ArrayList<>();

    private List<Float> sHistoryAlphaCache = new ArrayList<>();

    private List<Integer> sHistoryTypeCache = new ArrayList<>();

    private static ImageCache sImageCache;

    private ImageCache() {
//        sHistoryAlphaCache.add(0f);
    }

    public static ImageCache getInstance() {
        if (sImageCache == null) {
            synchronized (ImageCache.class) {
                if (sImageCache == null) {
                    sImageCache = new ImageCache();
                }
            }
        }
        return sImageCache;
    }

    public void putEffectBitmap(int key, NativeBitmap bmp) {
        sEffectBitmapCache.put(key, bmp);
    }

    public Bitmap geteffectBitmap(int key){
        NativeBitmap bitmap = sEffectBitmapCache.get(key);
        if (bitmap == null)
            return null;
        else
            return bitmap.getImage();
    }

    public void popEffectBitmap(int key) {
        sEffectBitmapCache.remove(sEffectBitmapCache.get(key));
    }

    public void popEffectAllBitmap() {
        sEffectBitmapCache.clear();
    }

    public void putHistoryBitmap(NativeBitmap bmp,float alpha,int effectType) {
        if(sHistoryBitmapCache.size()==6){
            sHistoryBitmapCache.remove(1);//0为原图
            sHistoryAlphaCache.remove(1);
            sHistoryTypeCache.remove(1);
        }
        sHistoryTypeCache.add(effectType);
        sHistoryAlphaCache.add(alpha);
        sHistoryBitmapCache.add( bmp);
    }

    public void popHistoryBitmap(int key) {
        sHistoryBitmapCache.remove(key);
    }

    public void popAllHistoryBitmap() {
        sHistoryBitmapCache.clear();
    }

    public Bitmap getHistoryBitmapByKey(int key) {
        NativeBitmap bmp = sHistoryBitmapCache.get(key);
        if (bmp == null)
            return null;
        else
            return bmp.getImage();
    }

    public Bitmap getHistoryLastBitmap() {
        if(sHistoryBitmapCache.size()!=0) {
            NativeBitmap bmp = sHistoryBitmapCache.get(sHistoryBitmapCache.size() - 1);
            if (bmp == null)
                return null;
            else
                return bmp.getImage();
        }else{
            return null;
        }

    }

    public int getHistoryBitmapSize(){
        return sHistoryBitmapCache.size();
    }

    public void clear(){
        sHistoryAlphaCache.clear();
        sHistoryBitmapCache.clear();
        sHistoryTypeCache.clear();
        sEffectBitmapCache.clear();
    }

    public int getEffectType(int index){
        return sHistoryTypeCache.get(index);
    }

    public float getAlpha(int index){
        return sHistoryAlphaCache.get(index);
    }

}
