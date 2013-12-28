package com.warsong.android.learn.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.warsong.android.learn.R;

/**
 * 合影界面中使用的indicator
 *
 * @author zhanqu
 * @date 2013-11-21 上午11:18:40
 */
public class PhotoViewPagerIndicator extends ViewPagerIndicator {

    private SelectedListener listener;
    
    public PhotoViewPagerIndicator(Context context, ViewGroup container) {
        super(context, container);
    }
    
    public void setSelectedListener(SelectedListener l) {
        this.listener = l;
    }

    @Override
    public View getItemView(View convertView, int index) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.bill_photo_flinger_indicator_item,
                    container, false);
        }
        return convertView;
    }

    @Override
    protected void onIndicatorChanged(View v, int index, boolean active) {
        super.onIndicatorChanged(v, index, active);
        int resId = 0;
        if (active) {
            resId = R.drawable.bill_vp_indicator_active;
        } else {
            resId = R.drawable.bill_vp_indicator_normal;
        }
        v.setBackgroundResource(resId);
    }
    
    @Override
    protected void onIndicatorSelected(int index) {
        super.onIndicatorSelected(index);
        if (listener != null) {
            listener.run(index);
        }
    }
    
    public interface SelectedListener {
        public void run(int index);
    }
}
