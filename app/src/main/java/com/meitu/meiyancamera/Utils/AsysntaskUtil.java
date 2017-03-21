package com.meitu.meiyancamera.Utils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.meitu.core.processor.FilterProcessor;
import com.meitu.core.types.NativeBitmap;
import com.meitu.meiyancamera.Activity.EffectActivity;

/**
 * Created by ZerO on 2017/3/18.
 */

public class AsysntaskUtil extends AsyncTask<String,Void,NativeBitmap> {

    ImageView iv;
    int type;
    int size;
    String path;


    public AsysntaskUtil(ImageView iv, int type, int size, String path){
        this.iv = iv;
        this.type = type;
        this.size = size;
        this.path = path;

    }

    @Override
    protected NativeBitmap doInBackground(String[] params) {

        NativeBitmap mNativeBitmap = NativeBitmap.createBitmap(path, size);
        FilterProcessor.renderProc(mNativeBitmap, type, 1.0f);

        return mNativeBitmap;
    }

    @Override
    protected void onPostExecute(NativeBitmap mNativeBitmap) {
        super.onPostExecute(mNativeBitmap);
        if(ImageCache.getInstance().geteffectBitmap(type)==null){

            ImageCache.getInstance().putEffectBitmap(type,mNativeBitmap);
        }


        iv.setImageBitmap(mNativeBitmap.getImage());
    }
}
