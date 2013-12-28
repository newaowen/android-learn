package com.warsong.android.learn.service;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * local service不需要使用aidl
 * 
 * @author newaowen@gmail.com
 * @date 2013-11-17 下午6:12:09
 */
public class LocalService extends Service {
	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();
	// Random number generator
	private final Random mGenerator = new Random();

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 * 返回的是本进程内的binder对象
	 */
	public class LocalBinder extends Binder {
		public LocalService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return LocalService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/** method for clients */
	public int getRandomNumber() {
		return mGenerator.nextInt(100);
	}
}