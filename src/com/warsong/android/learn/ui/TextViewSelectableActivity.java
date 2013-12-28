package com.warsong.android.learn.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warsong.android.learn.R;

/**
 * textview selecatble测试
 * @author newaowen@gmail.com
 */
public class TextViewSelectableActivity extends Activity {

	private View contentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        
        contentView = LayoutInflater.from(this).inflate(R.layout.textview_selectable, null);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            	ListView l = (ListView)contentView.findViewById(R.id.list);
            	RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams)l.getLayoutParams();
            	rl.height = 500;
            }
        });
        
        setContentView(contentView);
        
        init();
    }
    
    private void init() {
    	String[] s = {"xxxxxxxxxx", "aaaaaaaaaaaaa", "BBBBBBBBBBBB",
    			"xxxxxxxxxx", "aaaaaaaaaaaaa", "BBBBBBBBBBBB",
    			"xxxxxxxxxx", "aaaaaaaaaaaaa", "BBBBBBBBBBBB",
    			"xxxxxxxxxx", "aaaaaaaaaaaaa", "BBBBBBBBBBBB",
    			"xxxxxxxxxx", "aaaaaaaaaaaaa", "BBBBBBBBBBBB"};
    	CustomListAdapter adapter =  new CustomListAdapter(this, s);
    	ListView l = (ListView)contentView.findViewById(R.id.list);
    	l.setAdapter(adapter);
    	
//    	LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//    	ViewGroup box = (ViewGroup)findViewById(R.id.box);
//    	for(int i = 0; i < s.length; i++) {
//			View view = inflater.inflate(R.layout.tvs_list_item, null);
//    		((TextView) view.findViewById(R.id.tv)).setText(s[i]);
//			box.addView(view);
//    	}
    }
    
    private class CustomListAdapter extends BaseAdapter {

    	private Context			context;
    	private String[]	listData;

    	public CustomListAdapter(Context context, String[] s) {
    		this.listData = s;
    		this.context = context;
    	}

    	@Override
    	public int getCount() {
    		return listData.length;
    	}

    	@Override
    	public long getItemId(int pos) {
    		return pos;
    	}

    	@Override
    	public Object getItem(int pos) {
    		return listData[pos];
    	}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		View view = null;

    		if (convertView == null) {
    			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    			view = inflater.inflate(R.layout.tvs_list_item, null);
    			convertView = view;
    		} else {
    			view = convertView;
    		}

    		String item = (String) getItem(position);
    		((TextView) view.findViewById(R.id.tv)).setText(item);
    		//view.setTag(item.getFullPackageName());

    		return convertView;
    	}
    	
    }

}
