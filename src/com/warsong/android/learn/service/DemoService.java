package com.warsong.android.learn.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * binder service的使用aidl实现形式 实际上就是对binder ipc的简单封装!!
 * 
 * 在client binderService时如果发现服务未启动，将new service, 然后执行onBind
 * 
 * @author newaowen@gmail.com
 * @date 2013-11-17 下午4:18:54
 */
public class DemoService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		int pid = android.os.Process.myPid();
		Log.i("DemoService", "[" + pid + "]onCreated");
	}

	private IDemoService.Stub mBinder = new IDemoService.Stub() {

		private static final String TAG = "IDemoService.Stub";
		private int x = 0;

		@Override
		public int get() throws RemoteException {
			int ppid = android.os.Process.myPid();
			int pid = getCallingPid();
			Log.i(TAG, "[calling pid " + pid + ", " + ppid + "]get() called");
			return x;
		}

		@Override
		public void set(int i) throws RemoteException {
			int pid = getCallingPid();
			Log.i(TAG, "[calling pid " + pid + "]set() called");
			x = i;
		}
	};

	/**
	 * 在server进程?
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		int pid = android.os.Process.myPid();
		Log.i("DemoService", "[" + pid + "]onBind");
		return mBinder;
	}

}
