package com.warsong.android.learn;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DemoListAdapter extends BaseAdapter {

	private Context			context;
	private List<DemoItem>	listData;

	public DemoListAdapter(Context context, List<DemoItem> listData) {
		this.listData = listData;
		this.context = context;
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public Object getItem(int pos) {
		return listData.get(pos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.demo_list_item, null);
			convertView = view;
		} else {
			view = convertView;
		}

		DemoItem item = (DemoItem) getItem(position);
		((TextView) view.findViewById(R.id.demo_name)).setText(item.getName());
		((ImageView) view.findViewById(R.id.right_image)).setImageResource(R.drawable.right_arrow);
		view.setTag(item.getFullPackageName());

		return convertView;
	}

}