package com.warsong.android.learn.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.warsong.android.learn.R;
import com.warsong.android.learn.service.IDemoService;

/**
 * binder 测试
 * 
 * @author newaowen@gmail.com
 * @date 2013-11-17 下午2:27:46
 */
public class BinderTestActivity extends Activity {
	
	private static final String TAG = "BinderTestActivity";
	
	public static final String REMOTE_SERVICE_ACTION = "com.warsong.android.learn.service.DemoService";
	
	IDemoService demoService;
	
	private Button btn;
	
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			int pid = android.os.Process.myPid();
			Log.i(TAG, "[" + pid + "]onServiceDisconnected");
		}

		@Override
		public void onServiceConnected(ComponentName cpn, IBinder service) {
			//service类型为android.os.BinderProxy!!!
			String binderClassName = service.getClass().getCanonicalName();
			Log.i(TAG, "onServiceConnected: IBinder is " + binderClassName);
			//由于BinderProxy的queryLocalInterface默认返回null
			//将触发Stub.asInterface中的新建Stub.Proxy分支
			//new com.warsong.android.learn.service.IDemoService.Stub.Proxy(obj)
			demoService = IDemoService.Stub.asInterface(service);
			try {
				demoService.set(10);
				int x = demoService.get();
				
				int pid = android.os.Process.myPid();
				//service.get
				Log.i(TAG,   "[" + pid + "]demoService get after set: " + x);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.binder_test);
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent service = new Intent(REMOTE_SERVICE_ACTION);
				boolean r = bindService(service, conn, Context.BIND_AUTO_CREATE);
				Log.i(TAG, "bindService: " + r);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
	}

}
