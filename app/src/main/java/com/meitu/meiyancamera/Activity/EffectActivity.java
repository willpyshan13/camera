package com.meitu.meiyancamera.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.meitu.core.processor.FilterProcessor;
import com.meitu.core.types.NativeBitmap;
import com.meitu.meiyancamera.Adapter.EffectRvAdapter;
import com.meitu.meiyancamera.Constant.Constant;
import com.meitu.meiyancamera.Listener.OnEffectRvScrollListener;
import com.meitu.meiyancamera.R;
import com.meitu.meiyancamera.Utils.AsysntaskUtil;
import com.meitu.meiyancamera.Utils.BitmapUtil;
import com.meitu.meiyancamera.Utils.DensityUtil;
import com.meitu.meiyancamera.Utils.ImageCache;
import com.meitu.meiyancamera.Utils.ScrollSpeedLinearLayoutManger;
import com.meitu.meiyancamera.Utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import static android.support.v7.widget.RecyclerView.OnClickListener;


/**
 * Created by will on 2017/3/16.
 */

public class EffectActivity extends Activity implements OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "EffectActivity";
    private ImageView effectactivity_showoriginalpic_iv,effectactivity_showeffectpic_iv;
    private RecyclerView effectactivity_effect_rv;
    ImageButton effectactivity_sure_ibtn, effectactivity_cancle_ibtn;
    NativeBitmap mNativeBitmap;
    SeekBar effectactivity_sb;
    //原地址缓存
    String path;
    RecyclerView effectactivity_rv;
    //缩放的具体尺寸
    int mBitmapSize;
    Handler mEffectHandler;

    //特效缓存
    int mEffectTypeCache = 0;

    //点击的item角标缓存
    int mIndexCache ;

    //显示的第一个item角标
    int firstIndex;

    //显示的最后一个item角标
    int lastIndex;

    private static EffectRvAdapter adapter;

    private static ScrollSpeedLinearLayoutManger linearLayoutManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.effects_layout);
        init();



    }

    //初始化
    private void init() {
        mEffectHandler = new EffectHandler(EffectActivity.this);
        mBitmapSize = DensityUtil.getScreenWidth(getApplicationContext());
        effectactivity_showoriginalpic_iv = (ImageView) findViewById(R.id.effectactivity_showoriginalpic_iv);
        effectactivity_showeffectpic_iv = (ImageView) findViewById(R.id.effectactivity_showeffectpic_iv);
        effectactivity_effect_rv = (RecyclerView) findViewById(R.id.effectactivity_rv);
        effectactivity_sure_ibtn = (ImageButton) findViewById(R.id.effectactivity_sure_ibtn);
        effectactivity_cancle_ibtn = (ImageButton) findViewById(R.id.effectactivity_cancle_ibtn);
        effectactivity_sb = (SeekBar) findViewById(R.id.effectactivity_sb);
        effectactivity_rv = (RecyclerView) findViewById(R.id.effectactivity_rv);
        effectactivity_sure_ibtn.setOnClickListener(this);
        effectactivity_cancle_ibtn.setOnClickListener(this);
        getImagePathAndShow();

        adapter = new EffectRvAdapter(this, mNativeBitmap, mEffectHandler, effectactivity_sb, getIntent().getExtras().getString("imagePath"));
        linearLayoutManager = new ScrollSpeedLinearLayoutManger(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        effectactivity_effect_rv.setLayoutManager(linearLayoutManager);
//        effectactivity_effect_rv.setAdapter(new EffectRvAdapter(this, mNativeBitmap, mEffectHandler, effectactivity_sb, getIntent().getExtras().getString("imagePath")));
        effectactivity_rv.setAdapter(adapter);
        effectactivity_sb.setOnSeekBarChangeListener(this);
        effectactivity_rv.setHasFixedSize(true);

    }









    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 清楚缓存释放资源
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEffectHandler.removeCallbacksAndMessages(0);
//        EffectActivity.mBitmapCache.clear();
        effectactivity_showeffectpic_iv.setImageBitmap(null);
        effectactivity_showoriginalpic_iv.setImageBitmap(null);
        mEffectHandler = null;
        System.gc();
    }


    /**
     * 获取原地址路径并显示图片
     *
     */
    void getImagePathAndShow() {

        if (getIntent().getExtras()!=null&&!getIntent().getExtras().getString("imagePath").equals(null)) {
            path = getIntent().getExtras().getString("imagePath");
////            mNativeBitmap = CacheUtil.cache2image(path);
//            mNativeBitmap = NativeBitmap.createBitmap(path, mBitmapSize);

//            BitmapUtil.saveOriginalBitmap(BitmapUtil.loadBitmap(path,mBitmapSize),path);
            effectactivity_showoriginalpic_iv.setImageBitmap(BitmapUtil.loadBitmap(path,mBitmapSize).getImage());

        }
    }




    /**
     * 判断点击为当前显示item第一个则往前自动移动一位，如果为最后一个则相反
     *
     * @param index
     */
    void onScrollNextOrPre(int index){

        lastIndex = ((LinearLayoutManager)effectactivity_rv.getLayoutManager()).findLastVisibleItemPosition();
        firstIndex = ((LinearLayoutManager)effectactivity_rv.getLayoutManager()).findFirstVisibleItemPosition();
        if(index == lastIndex){

            effectactivity_rv.smoothScrollToPosition(lastIndex+1);
        }
        if(index ==firstIndex&&index!=0){

            effectactivity_rv.smoothScrollToPosition(firstIndex-1);
        }
    }

    /**
     * 检查是否点击同个item，是则取消seekbar显示，否则相反。并显示点击item所代表的特效
     *
     * @param index
     * @param effectType
     */
    void checkSeekBarVisibleAndShowEffect(int index,int effectType){
        if(index!=0) {
            if (index != mIndexCache) {

                effectactivity_sb.setVisibility(View.VISIBLE);
                effectactivity_sb.setProgress(100);
                Log.e(TAG,""+effectactivity_rv.getChildAt(index));
            }else {
                if(effectactivity_sb.isShown()) {

                    effectactivity_sb.setVisibility(View.GONE);
                }else {

                    effectactivity_sb.setVisibility(View.VISIBLE);
                }
                Log.e(TAG,""+effectactivity_rv.getChildAt(index));
//                effectactivity_rv.getChildAt(index).setBackgroundColor(Color.parseColor("#F5F5DC"));
            }
        }else {
            effectactivity_sb.setVisibility(View.GONE);
            getImagePathAndShow();
        }



        if(effectType!=0&&index != mIndexCache) {
            if(!effectactivity_showeffectpic_iv.isShown()){
                effectactivity_showeffectpic_iv.setVisibility(View.VISIBLE);
            }



            new AsysntaskUtil(effectactivity_showeffectpic_iv,effectType,mBitmapSize,path).execute();
        }else if(effectType ==0){
            effectactivity_showeffectpic_iv.setVisibility(View.GONE);
        }
        mEffectTypeCache = effectType;
        mIndexCache =index;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.effectactivity_sure_ibtn:
//                effectactivity_sure_ibtn.setBackgroundResource(R.drawable.selfie_beauty_ok_btn_ic_pressed);
                saveEffecBtimapCache();
                break;
            case R.id.effectactivity_cancle_ibtn:
                startMainActivity("");
                break;
        }
    }

    /**
     * 打开主页面并携带特效缓存图片路径
     *
     * @param cachePath
     */
    void startMainActivity(String cachePath){
        Intent it = new Intent(EffectActivity.this,MainActivity.class);
        Bundle bundle = new Bundle();
        if(!cachePath.equals("")&&cachePath!=null) {
            bundle.putString("effectCachePath", cachePath);
        }else{
            bundle.putString("effectCachePath", path);
        }
        it.putExtras(bundle);
        startActivity(it);
    }

    /**
     * 保存特效缓存图片并传给路径打开主页面
     *
     */
    void saveEffecBtimapCache(){
        if(mEffectTypeCache!=0) {
            NativeBitmap nativeBitmap = NativeBitmap.createBitmap(getIntent().getExtras().getString("imagePath"), mBitmapSize);
            FilterProcessor.renderProc(nativeBitmap, mEffectTypeCache, effectactivity_showeffectpic_iv.getAlpha() * 100);
            ImageCache.getInstance().putHistoryBitmap(nativeBitmap,effectactivity_showeffectpic_iv.getAlpha() * 100,mEffectTypeCache);
            finish();
        }else{
//            ToastUtil.showShort(EffectActivity.this,getString(R.string.unselected_effect));
        }
 }

    /**
     * 检查所选定的item是否在显示范围，否则滑动过去
     *
     */
    void checkShowItem(){
        lastIndex = ((LinearLayoutManager)effectactivity_rv.getLayoutManager()).findLastVisibleItemPosition();
        firstIndex = ((LinearLayoutManager)effectactivity_rv.getLayoutManager()).findFirstVisibleItemPosition();
        Log.e(TAG,mIndexCache+"  "+firstIndex+"   "+lastIndex);
        if(mIndexCache<firstIndex||mIndexCache>lastIndex){
            if(mIndexCache<firstIndex){
                effectactivity_rv.smoothScrollToPosition(mIndexCache-2);
            }else if(mIndexCache>lastIndex){
                effectactivity_rv.smoothScrollToPosition(mIndexCache+2);
            }

        }
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        effectactivity_showeffectpic_iv.setAlpha((float) (0.01 * progress));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

//        checkShowItem();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private static int currentid = 0;

    private  static class  EffectHandler extends Handler {
        private WeakReference<Activity> activityWeakReference = null;

        public EffectHandler(Activity activity) {
            activityWeakReference = new WeakReference<Activity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            EffectActivity activity = (EffectActivity) activityWeakReference.get();
            super.handleMessage(msg);
            switch (msg.what) {

                case Constant.ON_CLICK_EVENT:
                    activity.onScrollNextOrPre(msg.arg1);
                    activity.checkSeekBarVisibleAndShowEffect(msg.arg1,msg.arg2);
                    View view = activity.effectactivity_effect_rv.getLayoutManager().findViewByPosition(currentid);
                    if (view!=null)
                        view.findViewById(R.id.effectactivity_rv_item_selected_iv).setVisibility(View.GONE);
                    currentid = msg.arg1;
                    adapter.notifyItemChanged(currentid,currentid);

                    break;

            }
        }
    };
}
