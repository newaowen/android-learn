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
import com.warsong.android.learn.widget.PrintTextView;

/**
 * 内存占用检验
 * 
 * @author zhanqu
 * @date 2014-1-3 下午4:01:14
 */
public class MemoryUseActivity extends Activity {

	private PrintTextView tv;
	
	private int[] states = {1, 2, 3, 4};
	
	private MState s = MState.NEW;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_use);
        
        init();
    }

    public void init() {
    	tv = (PrintTextView)findViewById(R.id.tv);
    }
    
    public static enum MState {
        NEW, DOWNLOADING, DOWNLOADED, COMPLETE
    }
    
    private int caclObjectSize() {
    	MState s = MState.COMPLETE;
    	return 0;
    }
    
}
