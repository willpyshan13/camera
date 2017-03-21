# camera

1、recyclerview局部刷新

        需要使用这个依赖库compile 'com.android.support:recyclerview-v7:25.3.0'
        复写adapter里面的这个方法。
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
        
    再调用：adapter.notifyItemChanged(currentid,currentid);
    就可以实现局部刷新

