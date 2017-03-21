package com.meitu.meiyancamera.Adapter;

import android.content.ClipData;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meitu.core.types.NativeBitmap;
import com.meitu.meiyancamera.Activity.EffectActivity;
import com.meitu.meiyancamera.Constant.Constant;
import com.meitu.meiyancamera.Interface.OnItemClickListener;
import com.meitu.meiyancamera.R;
import com.meitu.meiyancamera.Utils.AsysntaskUtil;
import com.meitu.meiyancamera.Utils.DensityUtil;
import com.meitu.meiyancamera.Utils.ImageCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zero on 2017/3/17.
 */

public class EffectRvAdapter extends RecyclerView.Adapter<EffectRvAdapter.MyViewHolder> {

    private static final String TAG = "EffectRvAdapter";
    private Context mContext;
    private ArrayList<String> mEffectList = new ArrayList<>();
    private HashMap<String, Integer> mEffectMap = new HashMap<>();

    private Handler mHandler;
    private NativeBitmap mNativeBitmap;
    private SeekBar mSeekBar;
    private String path;
    int mBitmapSize;

    private int currentPosition = -1;

    public EffectRvAdapter(Context context, NativeBitmap nativeBitmap, Handler mHandler, SeekBar mSeekBar, String path) {
        this.mHandler = mHandler;
        this.mContext = context;
        this.mNativeBitmap = nativeBitmap;
        this.mSeekBar = mSeekBar;
        this.path = path;
        init();
    }

    void init() {

        mEffectMap.put("甜美可人", 118);
        mEffectMap.put("自然", 116);
        mEffectMap.put("花颜", 480);
        mEffectMap.put("粉黛", 553);
        mEffectMap.put("初夏", 477);
        mEffectMap.put("蓝调", 161);
        mEffectMap.put("唯美", 124);
        mEffectMap.put("萤彩", 357);
        mEffectMap.put("新雪", 501);
        mEffectMap.put("洛可可", 283);
        mEffectMap.put("霏颜", 389);
        mEffectMap.put("蜜柚", 505);
        mEffectMap.put("恬淡", 358);
        mEffectMap.put("白露", 175);
        mEffectMap.put("月光", 284);
        mEffectMap.put("粉嫩系", 120);
        mEffectMap.put("柔光", 122);
        mEffectMap.put("清凉", 130);
        mEffectMap.put("日系", 126);
        mEffectMap.put("阿宝色", 132);
        mEffectMap.put("典雅", 160);
        mEffectMap.put("魅惑", 360);
        mEffectMap.put("柳丁", 359);
        mEffectMap.put("迷幻", 361);
        mEffectMap.put("黑白", 113);
        mEffectMap.put("原图", 0);

//       mEffectList.add(0,"原图");
        loopMap(mEffectMap, mEffectList);

        mBitmapSize = DensityUtil.dip2px(mContext, 80f) * 3 / 4;

    }

    /**
     * 循环存特效数据的map并用键值关联list，同是map的特效数值放入到图片缓存map中
     *
     * @param map
     * @param list
     */
    private void loopMap(Map<String, Integer> map, List<String> list) {
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
//            ImageCache.getInstance().putEffectBitmap((int)entry.getValue(),null);
            if (((String) entry.getKey()).equals("原图")) {
                list.add(0, (String) entry.getKey());
            } else
                list.add((String) entry.getKey());
        }

    }





    @Override
    public EffectRvAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.effect_rv_item, viewGroup,
                false));
        return holder;
    }


    @Override
    public void onBindViewHolder(EffectRvAdapter.MyViewHolder holder, int i, List<Object> list) {
        Log.d(TAG, "onBindViewHolder " + i+" currentPosition "+currentPosition);
        if (list.isEmpty()) {
            if (currentPosition == i) {

                holder.effectactivity_rv_item_selected_iv.setVisibility(View.VISIBLE);
            } else{
                onBindViewHolder(holder, i);
            }
        } else {
            currentPosition = (int) list.get(0);


            holder.effectactivity_rv_item_selected_iv.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(final EffectRvAdapter.MyViewHolder viewHolder, final int i) {

        viewHolder.effectactivity_rv_item_selected_iv.setVisibility(View.GONE);
        viewHolder.effectactivity_rv_item_tv.setText(mEffectList.get(i));
        if (ImageCache.getInstance().geteffectBitmap(mEffectMap.get(mEffectList.get(0))) != null)
            viewHolder.effectactivity_rv_item_iv.setImageBitmap(ImageCache.getInstance().geteffectBitmap(mEffectMap.get(mEffectList.get(0))));
        if (ImageCache.getInstance().geteffectBitmap(mEffectMap.get(mEffectList.get(i))) != null) {
            viewHolder.effectactivity_rv_item_iv.setImageBitmap(ImageCache.getInstance().geteffectBitmap(mEffectMap.get(mEffectList.get(i))));
        } else {
            AsysntaskUtil asysnc = new AsysntaskUtil(viewHolder.effectactivity_rv_item_iv, mEffectMap.get(mEffectList.get(i)), mBitmapSize, path);
            viewHolder.effectactivity_rv_item_iv.setTag(R.id.tag, asysnc);
            asysnc.execute();
        }


        if (viewHolder.mOnItemClickLitener == null) {

            viewHolder.effectactivity_rv_item_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage2Activity(i);

                }
            });
        }
    }


    void sendMessage2Activity(int index) {
        Message msg = new Message();
        msg.what = Constant.ON_CLICK_EVENT;
        msg.arg1 = index;
        if (index == 0) {
            msg.arg2 = 0;
        } else {
            msg.arg2 = mEffectMap.get(mEffectList.get(index));
        }

        mHandler.sendMessage(msg);
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);

        AsysntaskUtil asysnc = (AsysntaskUtil) holder.effectactivity_rv_item_iv.getTag(R.id.tag);
        if (asysnc != null)
            asysnc.cancel(true);
    }

    @Override
    public int getItemCount() {
        return mEffectList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView effectactivity_rv_item_tv;
        ImageView effectactivity_rv_item_iv,effectactivity_rv_item_selected_iv;
        OnItemClickListener mOnItemClickLitener;

        public MyViewHolder(View view) {
            super(view);
            effectactivity_rv_item_tv = (TextView) view.findViewById(R.id.effectactivity_rv_item_tv);
            effectactivity_rv_item_iv = (ImageView) view.findViewById(R.id.effectactivity_rv_item_iv);
            effectactivity_rv_item_selected_iv = (ImageView) view.findViewById(R.id.effectactivity_rv_item_selected_iv);
        }

        public void setOnItemClickLitener(OnItemClickListener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }
    }


}
