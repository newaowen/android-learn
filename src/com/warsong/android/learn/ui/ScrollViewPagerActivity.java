package com.warsong.android.learn.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.warsong.android.learn.R;

/**
 * scroll与viewpager冲突的解决
 * 
 * @author newaowen@gmail.com
 */
public class ScrollViewPagerActivity extends Activity {

    private ViewPager viewPager;

    private CustomPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_viewpager);

        init();
    }

    private void init() {
        int[] colors = {0xff00ff00, 0xffff0000, 0xff0000ff };
        pagerAdapter = new CustomPagerAdapter(this, colors);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
    }

    private class CustomPagerAdapter extends PagerAdapter {
        private Context context;
        private int[] data;

        public CustomPagerAdapter(Context context, int[] data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            if (data == null) {
                return 0;
            } else {
                return data.length;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        /**
         * 初始化时就默认执行多次 
         */
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.scroll_view_pager_item, null);
            int c = data[position];
            view.setBackgroundColor(c);

            container.addView(view);
            view.invalidate();

            return view;
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

}
