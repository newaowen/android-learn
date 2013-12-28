package com.warsong.android.learn.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.warsong.android.learn.DemoItem;
import com.warsong.android.learn.DemoListAdapter;
import com.warsong.android.learn.R;
import com.warsong.android.learn.helper.DemoHelper;

public class MainActivity extends Activity {

    private List<DemoItem> demos;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initDemos();
        initView();
    }

    public void initDemos() {
    	demos = DemoHelper.getDemoList();
    }

    public void initView() {
        ListAdapter la = new DemoListAdapter(this, demos);
        ListView lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(la);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String className = (String) view.getTag();
                    Class<?> cls = Class.forName(className);
                    Intent i = new Intent();
                    i.setClass(MainActivity.this, cls);
                    MainActivity.this.startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
