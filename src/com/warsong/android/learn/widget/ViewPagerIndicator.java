package com.warsong.android.learn.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * 与viewpager绑定的指示器基类
 * 
 * @author zhanqu
 * @date 2013-11-20 下午6:25:02
 */
public abstract class ViewPagerIndicator {

	protected Context context;

	protected ViewPager viewPager;

	protected List<View> indicatorViews;

	protected ViewGroup container;

	public ViewPagerIndicator(Context context, ViewGroup container) {
		this.context = context;
		this.container = container;
		indicatorViews = new ArrayList<View>();
	}

	public List<View> getIndicatorViews() {
		return indicatorViews;
	}

	public ViewGroup getContainer() {
		return container;
	}

	public void setViewPager(ViewPager vp) {
		this.viewPager = vp;
		if (viewPager == null) {
			return;
		}

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				ViewPagerIndicator.this.selectIndicator(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		// 动态生成item view
		indicatorViews.clear();
		PagerAdapter adapter = viewPager.getAdapter();
		if (adapter != null) {
			for (int i = 0; i < adapter.getCount(); i++) {
				View v = getItemView(null, i);
				if (v != null) {
					indicatorViews.add(v);
					if (container != null) {
						container.addView(v);
					}
				}
			}
		}
		//触发选择第一个indicator
		selectIndicator(0);
	}

	public View getItemView(View convertView, int index) {
		return null;
	}

	/**
	 * indicator切换的回调
	 * @param v
	 * @param index
	 * @param active
	 */
	protected void onIndicatorChanged(View v, int index, boolean active) {

	}
	
	/**
	 * indicator切换的回调
	 * @param v
	 * @param index
	 * @param active
	 */
	protected void onIndicatorSelected(int index) {

	}

	/**
	 * 指定选择indicator(需和view pager同步)
	 * 
	 * @param index
	 */
	protected void selectIndicator(int index) {
		int size = indicatorViews.size();
		if (index < size) {
			for (int i = 0; i < size; i++) {
				View v = (View) indicatorViews.get(i);
				if (v != null) {
					if (i == index) {
						onIndicatorChanged(v, i, true);
					} else {
						onIndicatorChanged(v, i, false);
					}
				}
			}
		}
		onIndicatorSelected(index);
	}

}
