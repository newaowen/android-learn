package com.warsong.android.learn.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.warsong.android.learn.R;

/**
 * 界面生命周期
 * 打开界面：oncreate -> onstart -> onresume
 * 跳转出去: onpause -> onSaveInstance -> onStop
 * 按home出去: onpause -> onSaveInstance -> onStop
 * 从跳出去的界面返回到：onRestart -> onstart -> onresume
 * 长按home切换到：onRestart -> onstart -> onresume
 * 
 * 界面中的view本身内容如果没重新调onCreate,则不会丢失
 * 如果重新create了，且没有
 * 
 * configchange时，将onDestory -> onCreate -> onstart -> onRestoreInstanceState
 * 界面内的子view如果带id,则数据将自动恢复 (如不覆盖onSaveInstance)
 * 　
 * 
 * @author newaowen@gmail.com
 * @date 2013-11-24 上午11:40:39
 */
public class ActLifeCycleActivity extends Activity {

	private static final String TAG = "ActViewLifeCycleActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_life_cycle);
		Log.i(TAG, "oncreated");
		println("oncreated");
		initView();
	}
	
	private void initView() {
		Button btn = (Button)findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
				startActivity(i);
			}
		});
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
		println("onSaveInstanceState");
	}
	
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.i(TAG, "onRestoreInstanceState");
		println("onRestoreInstanceState");
	}
	
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart");
		println("onStart");
	}
	
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG, "onRestart");
		println("onRestart");
	}

	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		println("onResume");
	}
	
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
		println("onPause");
	}
	
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "onStop");
		println("onStop");
	}
	
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		println("onDestroy");
	}
	
	private void println(String str) {
		TextView tv = (TextView) findViewById(R.id.text);
		if(tv == null) {
			return;
		}
		String source = (String) tv.getText();
		if (source == null) {
			source = "";
		}
		tv.setText(source + "\n" + str);
	}
	
}
