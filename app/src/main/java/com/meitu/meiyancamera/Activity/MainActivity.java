package com.meitu.meiyancamera.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meitu.core.types.NativeBitmap;
import com.meitu.meiyancamera.R;
import com.meitu.meiyancamera.Utils.BitmapUtil;
import com.meitu.meiyancamera.Utils.DensityUtil;
import com.meitu.meiyancamera.Utils.FileUtil;
import com.meitu.meiyancamera.Utils.ImageCache;
import com.meitu.meiyancamera.Utils.ToastUtil;

import java.io.File;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "main_activity";

    private ImageButton mainactivity_revoke_ibtn, mainactivity_reform_ibtn,mainactivity_choicepic_btn;
    TextView mainactivity_back_tv, mainactivity_save_tv,mainactivity_effect_tv;
    private ImageView mainactivity_showpic_iv;
    private RelativeLayout mainactivity_btnll_rl;
    private LinearLayout mainactivity_effect_ll;
    //缩放图片具体尺寸
    private int mBitmapSize;

    private int indexsize = 0;

    private String imagePathCache = "";
    private static final int IMAGE = 1;


    private int mCurrentPositon = 0;

    //原图片地址路径缓存；
    private String originalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mBitmapSize = DensityUtil.getScreenWidth(getApplicationContext());
        requesePremission();
        init();
    }

    /*初始化控件*/
    private void init() {


        mainactivity_revoke_ibtn = (ImageButton) findViewById(R.id.mainactivity_revoke_ibtn);
        mainactivity_reform_ibtn = (ImageButton) findViewById(R.id.mainactivity_reform_ibtn);
        mainactivity_back_tv = (TextView) findViewById(R.id.mainactivity_back_tv);
        mainactivity_save_tv = (TextView) findViewById(R.id.mainactivity_save_tv);
        mainactivity_effect_tv = (TextView) findViewById(R.id.mainactivity_effect_tv);
        mainactivity_btnll_rl = (RelativeLayout) findViewById(R.id.mainactivity_btnll_rl);
        mainactivity_effect_ll = (LinearLayout) findViewById(R.id.mainactivity_effect_ll);

//        mainactivity_effect_btn = (Button) findViewById(R.id.mainactivity_effect_btn);
        mainactivity_choicepic_btn = (ImageButton) findViewById(R.id.mainactivity_choisepic_ibtn);

        mainactivity_showpic_iv = (ImageView) findViewById(R.id.mainactivity_showpic_iv);


        mainactivity_choicepic_btn.setOnClickListener(this);

//        mainactivity_effect_btn.setOnClickListener(this);
        mainactivity_back_tv.setOnClickListener(this);
        mainactivity_save_tv.setOnClickListener(this);
        mainactivity_revoke_ibtn.setOnClickListener(this);
        mainactivity_reform_ibtn.setOnClickListener(this);
        mainactivity_effect_tv.setOnClickListener(this);
        mainactivity_effect_ll.setOnClickListener(this);
        mainactivity_revoke_ibtn.setEnabled(false);
        mainactivity_reform_ibtn.setEnabled(false);
    }



    @Override
    protected void onResume() {
        super.onResume();
        checkBtnOnResume();
        mainactivity_btnll_rl.bringToFront();
        checkEffectTv();
    }

    void checkEffectTv(){
        if(originalPath==null) mainactivity_effect_tv.setEnabled(false);
        else mainactivity_effect_tv.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainactivity_showpic_iv.setImageBitmap(null);
        ImageCache.getInstance().clear();
    }

    /*点击事件处理*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.mainactivity_choisepic_ibtn:
                openSystemPhoto();
                break;
            case R.id.mainactivity_effect_tv:
                startEffectActivity();
                break;

            case R.id.mainactivity_save_tv:

                break;
            case R.id.mainactivity_back_tv:
                finish();
                break;
            case R.id.mainactivity_reform_ibtn:
                onUndoClick();
                break;
            case R.id.mainactivity_revoke_ibtn:
                onRedoClick();
                break;

        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            c.close();
            showImage(imagePath);
            originalPath = imagePath;
            Log.e("imagePath", imagePath);
        }

    }


    private void saveEffectBitmap(){
        if(originalPath!=null) {
            BitmapUtil.saveEffectBitmapCache(originalPath, ImageCache.getInstance().getEffectType(mCurrentPositon), ImageCache.getInstance().getAlpha(mCurrentPositon));
        }else{
            ToastUtil.show(getApplicationContext(),R.string.nopic_saved, Toast.LENGTH_SHORT);
        }
    }
    private void checkBtnOnResume(){
        if (indexsize != ImageCache.getInstance().getHistoryBitmapSize() || indexsize == 0) {
            indexsize = ImageCache.getInstance().getHistoryBitmapSize();
            if (ImageCache.getInstance().getHistoryLastBitmap() != null) {
                mainactivity_showpic_iv.setImageBitmap(ImageCache.getInstance().getHistoryLastBitmap());
                indexsize = ImageCache.getInstance().getHistoryBitmapSize();
                mCurrentPositon = ImageCache.getInstance().getHistoryBitmapSize() - 1;
                if (mCurrentPositon >= 1)
                    mainactivity_reform_ibtn.setEnabled(true);
                Log.e(TAG, "size:" + ImageCache.getInstance().getHistoryBitmapSize() + "  " + mCurrentPositon);
            }
        }
    }


    /*打开系统相册*/
    private void openSystemPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE);
    }

    /*显示图片*/
    private void showImage(String imagePath) {
        Log.e("mainactivity", "showImage:" + imagePath);
//        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        NativeBitmap bitmap = BitmapUtil.loadBitmap(imagePath, mBitmapSize);
        if (bitmap != null) {
            mainactivity_showpic_iv.setImageBitmap(bitmap.getImage());
            ImageCache.getInstance().putHistoryBitmap(bitmap, 0f, 0);
            imagePathCache = imagePath;
            mainactivity_choicepic_btn.setVisibility(View.GONE);
        }

    }


    /*打开特效界面*/
    private void startEffectActivity() {
        Intent it = new Intent(MainActivity.this, EffectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", imagePathCache);
        Log.e("bundle imagepath", imagePathCache + "");
        it.putExtras(bundle);
        startActivity(it);
    }

    private void onUndoClick() {
        Log.e(TAG, "size:" + ImageCache.getInstance().getHistoryBitmapSize() + " onUndoClick " + mCurrentPositon);
        if (mCurrentPositon > 0) {
            mCurrentPositon--;
            Log.e(TAG, "size:" + ImageCache.getInstance().getHistoryBitmapSize() + " onUndoClick after  " + mCurrentPositon);
            mainactivity_showpic_iv.setImageBitmap(ImageCache.getInstance().getHistoryBitmapByKey(mCurrentPositon));
            checkBtn();
        } else {

            mainactivity_reform_ibtn.setEnabled(false);
        }

    }

    public void checkBtn() {
        if (indexsize > 2) {
            if (mCurrentPositon == ImageCache.getInstance().getHistoryBitmapSize() - 1) {
                changeNextAndPreBtn(true, false);
            } else if (mCurrentPositon == 0) {
                changeNextAndPreBtn(false, true);
            } else {
                changeNextAndPreBtn(true, true);
            }
        } else if (mCurrentPositon == 0) {
            changeNextAndPreBtn(false, true);
        } else if (mCurrentPositon == 1) {
            changeNextAndPreBtn(true, false);
        }
    }

    public void changeNextAndPreBtn(boolean dobol, boolean undobol) {
        mainactivity_reform_ibtn.setEnabled(dobol);
        mainactivity_revoke_ibtn.setEnabled(undobol);
    }

    private void onRedoClick() {
        Log.e(TAG, "size:" + ImageCache.getInstance().getHistoryBitmapSize() + " onRedoClick " + mCurrentPositon);
//        if(ImageCache.getInstance().getHistoryBitmapSize()>1){
        if (mCurrentPositon < ImageCache.getInstance().getHistoryBitmapSize() - 1) {
            mCurrentPositon++;
            Log.e(TAG, "size:" + ImageCache.getInstance().getHistoryBitmapSize() + " onRedoClick after  " + mCurrentPositon);
            mainactivity_showpic_iv.setImageBitmap(ImageCache.getInstance().getHistoryBitmapByKey(mCurrentPositon));
            checkBtn();
        } else {

            changeNextAndPreBtn(true,false);

        }
    }

    public void requesePremission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
                    1);
        }

    }


}