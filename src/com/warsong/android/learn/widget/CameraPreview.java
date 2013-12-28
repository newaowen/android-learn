package com.warsong.android.learn.widget;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the Camera
 * to the surface. We need to center the SurfaceView because not all devices have cameras that
 * support preview sizes at the same aspect ratio as the device's display.
 */
public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";


    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;


    public CameraPreview(Context context) {
        super(context);
        initSurface();
    }
    
    public CameraPreview(Context context, AttributeSet as) {
        super(context, as);
        initSurface();
//        mSurfaceView = new SurfaceView(context);
//        addView(mSurfaceView);
//
//        // Install a SurfaceHolder.Callback so we get notified when the
//        // underlying surface is created and destroyed.
//        mHolder = mSurfaceView.getHolder();
//        mHolder.addCallback(this);
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    private void initSurface() {
    	 mSurfaceView = new SurfaceView(getContext());
         addView(mSurfaceView);

         // Install a SurfaceHolder.Callback so we get notified when the
         // underlying surface is created and destroyed.
         mHolder = mSurfaceView.getHolder();
         mHolder.addCallback(this);
         mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mCamera.setDisplayOrientation(90);
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    public void switchCamera(Camera camera) {
       setCamera(camera);
       try {
           camera.setPreviewDisplay(mHolder);
       } catch (IOException exception) {
           Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
       }
       Camera.Parameters parameters = camera.getParameters();
       parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
       requestLayout();

       camera.setParameters(parameters);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        Log.i("CameraPreview", "setMeasuredDimension(w,h) " + width + ", " + height);
        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
        
        Log.i("CameraPreview", "preview size(w,h) " + mPreviewSize.width + ", " + mPreviewSize.height);
        //打印supported preview size
        for (int i = 0; i < mSupportedPreviewSizes.size(); i++) {
        	Size s = mSupportedPreviewSizes.get(i);
        	Log.i("CameraPreview", "mSupportedPreviewSizes[" + i + "](w,h) " + s.width + ", " + s.height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

           // child.layout(0, 0, width, height);
//                  width, (height + scaledChildHeight) / 2);
            //居中计算
            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }


    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;


        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;


        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }


        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        //Camera.Parameters parameters = mCamera.getParameters();
        //parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        //requestLayout();
        
        Parameters parameters = mCamera.getParameters();
        Display display = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0)
        {
            parameters.setPreviewSize(height, width);                           
            mCamera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            parameters.setPreviewSize(width, height);                           
        }

        if(display.getRotation() == Surface.ROTATION_180)
        {
            parameters.setPreviewSize(height, width);               
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
            parameters.setPreviewSize(width, height);
            mCamera.setDisplayOrientation(180);
        }

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }
    
//    public Bitmap snapShot() {
//    	 Canvas canvas = holder.lockCanvas();
//    	 camera.capture(canvas);
//    	 holder.unlockCanvasAndPost(canvas);
//    	 
//    	 Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//         Canvas canvas = new Canvas(bitmap);
//         mSurfaceView.draw(canvas);
//         
//         return bitmap;
//    }
    
    public void takePicture(final TakePictureCallback callback) {
//    	mCamera.setca
//	    //CameraDevice.CaptureParams param = new CameraDevice.CaptureParams();
//	    //mCamera.getParameters();
//	    param.type = 1; // preview
//	    param.srcWidth = 1280;
//	    param.srcHeight = 960;
//	    param.leftPixel = 0;
//	    param.topPixel = 0;
//	    param.outputWidth = 320;
//	    param.outputHeight = 240;
//	    param.dataFormat = 2; // RGB_565
//	    mCamera.setCaptureParams(param);

	    Bitmap myPic = Bitmap.createBitmap(320, 240, Config.ARGB_8888);
	
	    Canvas canvas = new Canvas(myPic);
	
	    try {
	    	//sets what code should be executed after the picture is taken
	         Camera.PictureCallback mCall = new Camera.PictureCallback() {
	             @Override
	             public void onPictureTaken(byte[] data, Camera camera)
	             {
	                 //decode the data obtained by the camera into a Bitmap
	                 Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
	                 //set the iv_image
	                 callback.run(bmp);
	                // iv_image.setImageBitmap(bmp);
	             }
	         };

	         mCamera.takePicture(null, null, mCall);
	            //FileOutputStream stream = super.openFileOutput("picture" + i++ + ".png", MODE_PRIVATE);
	          //  mCamera.capture(canvas);
	            //myPic.compress(CompressFormat.PNG, 100, stream);
	
	            //stream.flush();
	
	            // stream.close();
	
	    } catch(Exception e) { Log.e("MyLog", e.toString()); }
	}
    
    public interface TakePictureCallback {
    	 public void run(Bitmap bmp);
    }
}

