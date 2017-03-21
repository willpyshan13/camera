package com.meitu.meiyancamera.Listener;

import android.os.Handler;

import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.meitu.meiyancamera.Constant.Constant;

/**
 * Created by ZerO on 2017/3/19.
 */

public class OnEffectRvScrollListener extends RecyclerView.OnScrollListener {

    Handler mHandler;


    public OnEffectRvScrollListener(Handler mHandler) {
        super();
        this.mHandler = mHandler;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
//        Message msg = new Message();
//        msg.what = Constant.SCROLL_STATE;
//        msg.arg1 = newState;
//        mHandler.sendMessage(msg);


    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }
}
