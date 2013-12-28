package com.warsong.android.learn.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.warsong.android.learn.R;
import com.warsong.android.learn.widget.CameraPreview;

// ----------------------------------------------------------------------

/**
 * 带浮层的照相
 * 
 * @author newaowen@gmail.com
 * @date 2013-11-6 下午3:14:32
 */

public class CameraPreviewActivity extends Activity {
	private CameraPreview mPreview;
	Camera mCamera;
	int numberOfCameras;
	int cameraCurrentlyLocked;

	// The first rear facing camera
	int defaultCameraId;
	
	private ImageView snapImage;
	private Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Create a RelativeLayout container that will hold a SurfaceView,
		// and set it as the content of our activity.
		// mPreview = new Preview(this);
		setContentView(R.layout.camera);
		mPreview = (CameraPreview) findViewById(R.id.camera_preview);

		// Find the total number of cameras available
		numberOfCameras = Camera.getNumberOfCameras();

		// Find the ID of the default camera
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				defaultCameraId = i;
			}
		}
		
		snapImage = (ImageView)findViewById(R.id.snapImg);
		btn = (Button)findViewById(R.id.snap);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPreview.setDrawingCacheEnabled(true);
				mPreview.buildDrawingCache(false);
				//Bitmap bmp = mPreview.snapShot();
				//v1.setDrawingCacheEnabled(false);
				
				mPreview.takePicture(new CameraPreview.TakePictureCallback() {
					
					@Override
					public void run(Bitmap bmp) {
						if (bmp != null) {
							snapImage.setImageBitmap(bmp);
						}
					}
				});
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Open the default i.e. the first rear facing camera.
		mCamera = Camera.open();
		cameraCurrentlyLocked = defaultCameraId;
		mPreview.setCamera(mCamera);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Because the Camera object is a shared resource, it's very
		// important to release it when the activity is paused.
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	//
	//
	// // Inflate our menu which can gather user input for switching camera
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.camera_menu, menu);
	// return true;
	// }

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle item selection
	// switch (item.getItemId()) {
	// case R.id.switch_cam:
	// // check for availability of multiple cameras
	// if (numberOfCameras == 1) {
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setMessage(this.getString(R.string.camera_alert))
	// .setNeutralButton("Close", null);
	// AlertDialog alert = builder.create();
	// alert.show();
	// return true;
	// }
	//
	//
	// // OK, we have multiple cameras.
	// // Release this camera -> cameraCurrentlyLocked
	// if (mCamera != null) {
	// mCamera.stopPreview();
	// mPreview.setCamera(null);
	// mCamera.release();
	// mCamera = null;
	// }
	//
	//
	// // Acquire the next camera and request Preview to reconfigure
	// // parameters.
	// mCamera = Camera
	// .open((cameraCurrentlyLocked + 1) % numberOfCameras);
	// cameraCurrentlyLocked = (cameraCurrentlyLocked + 1)
	// % numberOfCameras;
	// mPreview.switchCamera(mCamera);
	//
	//
	// // Start the preview
	// mCamera.startPreview();
	// return true;
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// }

	public static void setCameraDisplayOrientation(Activity activity, int cameraId,
			android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}
}
