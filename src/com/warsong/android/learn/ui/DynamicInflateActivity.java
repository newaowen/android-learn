package com.warsong.android.learn.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.warsong.android.learn.R;

public class DynamicInflateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dynamic_inflate);

		ViewGroup box = (ViewGroup) findViewById(R.id.box);
		View v = LayoutInflater.from(this).inflate(R.layout.inflate_item, box, false);
		box.addView(v);
	}

}
