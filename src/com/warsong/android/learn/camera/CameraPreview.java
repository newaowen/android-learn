package com.warsong.android.learn.camera;

import java.io.IOException;
import java.util.List;

import com.warsong.android.learn.util.GeneralUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 相机预览组件
 * 
 * @author zhanqu
 * 
 */
public class CameraPreview extends FrameLayout implements SurfaceHolder.Callback {

    private final String TAG = "CameraPreview";

    protected Size previewSize;
    protected Size pictureSize;

    //是否正在预览的标记
    protected boolean isPreviewRunning = false;

    protected SurfaceView surfaceView;
    protected SurfaceHolder surfaceHolder;
    protected List<Size> supportedPictureSizes;
    protected List<Size> supportedPreviewSizes;
    protected CameraPreviewController previewController;

    //表示当前绑定的相机
    protected Camera camera;
    //表示选择的相机id, -1表示无效相机id, >=0才是有效的相机id
    protected int cameraId = -1;
    //相机显示的旋转角度(注意，不是相机传感器的本身旋转角度)
    protected int cameraDisplayRotation;
    //前后相机
    protected int cameraFacing = -1;
    //表面是否初始化的标记(在表面changed后执行)
    private boolean isSurfaceValid = false;
    private boolean needRelayout = false;

    public CameraPreview(Context context) {
        super(context);
        initSurface();
    }

    public CameraPreview(Context context, AttributeSet as) {
        super(context, as);
        initSurface();
    }

    /**
     * 初始化surface
     */
    @SuppressWarnings("deprecation")
    private void initSurface() {
        Log.i(TAG, "initSurface, create surface view");
        previewController = new CameraPreviewController(this);
        surfaceView = new SurfaceView(getContext());
        addView(surfaceView);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        //自动重新计算相机preview大小
        if (width > 0 && height > 0) {
            determineCameraSize(width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if ((changed || needRelayout) && getChildCount() > 0) {
            needRelayout = false;

            View child = getChildAt(0);
            int width = r - l;
            int height = b - t;
            //目标surface尺寸
            int surfaceW = width;
            int surfaceH = height;
            if (previewSize != null) {
                Point p = getPreviewViewSize();
                surfaceW = p.x;
                surfaceH = p.y;
            }
            //使用centerCrop模式(拉伸居中，保证preview可撑满容器)
            RectF rect = GeneralUtil.scaleCenterCropRect(surfaceW, surfaceH, width, height);
            Log.i(TAG, "box: width,height onLayout(l,t,r,b):" + width + "," + height + "   "
                       + rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom);
            child.layout((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        }
    }

    /**
     * 绑定到相机
     * (在容器界面的onResume中调用)
     * @param camera
     * @param cameraId
     * @throws Exception 
     */
    protected boolean attachCamera(int id) throws Exception {
        if (!validateAttachCamera(id)) {
            return false;
        }
        //当前已绑定到某相机，需要绑定到不同相机时，先detach
        if (camera != null && cameraId != id) {
            detachCamera();
        }
        Log.i(TAG, "attachCamera " + id);
        camera = CameraHelper.open(id);
        if (camera != null) {
            cameraId = id;
            //设置相机display图片的orientation
            cameraDisplayRotation = CameraHelper.setCameraDisplayOrientation(getContext(),
                cameraId, camera);
            cameraFacing = CameraHelper.getCameraFacing(cameraId);
            Parameters p = camera.getParameters();
            if (p != null) {
                supportedPreviewSizes = p.getSupportedPreviewSizes();
                supportedPictureSizes = p.getSupportedPictureSizes();
            }
            resumePreview();
        } else {
            throw new Exception("open camera " + cameraId + " failed ");
        }
        return true;
    }

    /**
     * 切换相机（前后），需要重新调整preview大小?
     * @param camera
     */
    protected boolean switchToCamera(int id) throws Exception {
        if (!attachCamera(id)) {
            return false;
        }
        resumePreview();
        return true;
    }
    
    private void resumePreview() {
        //切换到相机后，如果surface已存在（不会重建），重新设置preview
        //如果surface还未准备好，由surface回调处理preview
        if (isSurfaceValid) {
            preparePreview(surfaceHolder);
            //根据容器尺寸重新设置相机preview和图片大小
            determineCameraSize(getWidth(), getHeight());
            startPreview();
        }
    }

    /**
     * 与相机解绑
     * (在容器界面的onPause中调用)
     */
    protected void detachCamera() {
        Log.i(TAG, "detachCamera: " + cameraId);
        stopPreview();
        releaseCamera();
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        camera.release();
        camera = null;
        cameraDisplayRotation = 0;
        cameraFacing = -1;
    }

    /**
     * 检查是否可以绑定相机, 当id无效或当前已经绑定到id指定的相机上时，返回false
     * @param cameraId
     * @return
     */
    protected boolean validateAttachCamera(int id) {
        if (id < 0) {
            return false;
        }
        //忽略绑定到同一个cameraId上的操作(防止重复绑定)
        if (camera != null && cameraId == id) {
            return false;
        }
        return true;
    }

    /**
     * 设置闪光灯模式
     */
    protected void setCameraFlashMode(String mode) {
        Parameters p = camera.getParameters();
        if (p != null) {
            //小米3开启闪光灯时拍照变黑，这里屏蔽掉小米3的on flash mode
            if (CameraHelper.isXiaoMi3() && mode.equals("on")) {
                //ignore
            } else {
                p.setFlashMode(mode);
                camera.setParameters(p);
            }
        }
    }

    /**
     * surface创建回调
     */
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        // The Surface has been created, acquire the camera and tell it where to draw.
        preparePreview(holder);
    }

    /**
     * surface变化回调
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged:" + width + ", " + height);
        isSurfaceValid = true;
        if (camera != null) {
            if (!isPreviewRunning) {
                determineCameraSize(width, height);
                startPreview();
            } else {
                //如果正在preview,需重新preview
                restartPreview();
            }
        }
    }

    /**
     * surface销毁回调
     * (特殊情况，当按power键进入sleep状态时，surfaceCreated很可能不被触发)
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        isSurfaceValid = false;
        if (camera != null) {
            stopPreview();
        }
    }

    protected void preparePreview(SurfaceHolder holder) {
        //surface created且camera被detach时可能为空
        if (camera == null) {
            return;
        }
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    @SuppressLint("NewApi")
    protected void startPreview() {
        if (isPreviewRunning || previewSize == null || pictureSize == null) {
            return;
        }
        needRelayout = true;
        requestLayout();
        isPreviewRunning = true;
        try {
            Parameters p = camera.getParameters();
            if (p != null) {
                //设置自动对焦模式
                if (isSupportAutoFocus(p)) {
                    p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                p.setPreviewSize(previewSize.width, previewSize.height);
                p.setPictureSize(pictureSize.width, pictureSize.height);
                camera.setParameters(p);
                Log.i(TAG, "startPreview previewSize(width,height): " + previewSize.width + ","
                           + previewSize.height);
                Log.i(TAG, "startPreview pictureSize(width,height): " + pictureSize.width + ","
                           + pictureSize.height);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            camera.startPreview();
        }
    }

    protected void stopPreview() {
        if (isPreviewRunning) {
            //注意先取消autofocus
            cancelAutoFocus();
            camera.stopPreview();
            isPreviewRunning = false;
        }
    }

    protected void restartPreview() {
        stopPreview();
        startPreview();
    }

    /**
     * 确定相机的最佳预览及照片大小
     */
    protected void determineCameraSize(int width, int height) {
        int w = width;
        int h = height;
        if (isCameraWHSwitched()) {
            w = height;
            h = width;
        }
        if (supportedPreviewSizes != null) {
            previewSize = getOptimalSize(supportedPreviewSizes, w, h);
            Log.i(TAG, "determineCameraSize previewSize: " + previewSize.width + ", "
                       + previewSize.height);

            //不能保证preview和picture的分辨率完全一致
            if (supportedPictureSizes != null) {
                pictureSize = getOptimalSize(supportedPictureSizes, previewSize.width,
                    previewSize.height);
                Log.i(TAG, "determineCameraSize pictureSize: " + pictureSize.width + ", "
                           + pictureSize.height);
            }
        }
    }

    /**
     * 获取preview view尺寸大小(旋转过的)
     * @return
     */
    protected Point getPreviewViewSize() {
        Point p = new Point();
        if (isCameraWHSwitched()) {
            p.x = previewSize.height;
            p.y = previewSize.width;
        } else {
            p.x = previewSize.width;
            p.y = previewSize.height;
        }
        return p;
    }

    /**
     * 从机器支持的分辨率和屏幕尺寸中，选取最适合屏幕尺寸的预览窗口大小
     * @param sizes
     * @param w
     * @param h
     * @return
     */
    protected Size getOptimalSize(List<Size> sizes, int w, int h) {
        if (sizes == null) {
            return null;
        }

        final double EQUAL_ASPECT_TOLERANCE = 0.001;
        final double ASPECT_TOLERANCE = 0.2;//过小的话可能导致只选择小尺寸
        final double SIZE_TOLERANCE = 300;//绝对值偏差
        double targetRatio = (double) w / h;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        //先选择宽高比差别极小的
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > EQUAL_ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - h) > SIZE_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            //宽高比稍微有差别的
            for (Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                    continue;
                }
                if (Math.abs(size.height - h) > SIZE_TOLERANCE) {
                    continue;
                }
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }

    /**
     * 调用摄像头拍照？
     * @param callback
     */
    public void takePicture(final TakePictureCallback callback) {
        try {
            Camera.PictureCallback mCall = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    //重要，出异常时重新开启preview
                    stopPreview();
                    try {
                        int w = getWidth();
                        int h = getHeight();
                        if (isCameraWHSwitched()) {
                            w = getHeight();
                            h = getWidth();
                        }
                        Bitmap bmp = GeneralUtil.decodeImageBytes(data, w, h);
                        if (isCameraRotated()) {
                            //拍照生成的图片处理
                            int rotataion = CameraHelper.getCameraOrientation(cameraId);
                            if (cameraFacing == CameraHelper.CAMERA_FACING_FRONT) {//如果是前置摄像头，还需要翻转
                                if (CameraHelper.isMZPhone()) { //MZ前置相机特殊
                                    //先翻转再旋转
                                    bmp = GeneralUtil.flipAndRotateBitmap(bmp, rotataion, true);
                                } else {
                                    //先旋转再翻转
                                    bmp = GeneralUtil.rotateAndFlipBitmap(bmp, rotataion, true);
                                }
                            } else {//后置相机
                                bmp = GeneralUtil.rotateBitmap(bmp, rotataion, true);
                            }
                        }
                        //由于图片和preview分辨率可能不一致，需要将图片centerCrop到preview的分辨率上
                        //centerCropCameraPicture(bmp);
                        callback.run(bmp);
                    } catch (Error err) {
                    	err.printStackTrace();
                        //异常则重新打开相机
                        startPreview();
                    } catch (Exception e) {
                        e.printStackTrace();
                        //异常则重新打开相机
                        startPreview();
                    }
                }
            };

            camera.takePicture(new ShutterCallback() {
                public void onShutter() {
                    AudioManager mgr = (AudioManager) getContext().getSystemService(
                        Context.AUDIO_SERVICE);
                    mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
                }
            }, null, mCall);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private Bitmap centerCropCameraPicture(Bitmap bmp) {
        if (previewSize.width != pictureSize.width || previewSize.height != pictureSize.height) {
            if (bmp != null) {
                Point p = getPreviewViewSize();
                bmp = GeneralUtil.scaleCenterCrop(bmp, p.x, p.y);
            }
        }
        return bmp;
    }

    @SuppressLint("NewApi")
    public void handleTouchFocus(MotionEvent e) {
        if (isPreviewRunning && camera != null) {
            Parameters p = camera.getParameters();
            if (p != null && isSupportAutoFocus(p)) {
                startAutoFocus();
            }

            //focus area实现
            //            float x = e.getX();
            //            float y = e.getY();
            //            //api level 14才支持自动对焦
            //            if (android.os.Build.VERSION.SDK_INT >= 14) {
            //                float touchMajor = e.getTouchMajor();
            //                float touchMinor = e.getTouchMinor();
            //                Rect touchRect = new Rect((int) (x - touchMajor / 2), (int) (y - touchMinor / 2),
            //                    (int) (x + touchMajor / 2), (int) (y + touchMinor / 2));
            //                setTouchFocusArea(touchRect);
            //            }
        }
    }

    /**
     * 必须在api level 14上调用
     * @param tfocusRect
     */
    @SuppressLint("NewApi")
    protected void setTouchFocusArea(Rect tfocusRect) {
        Parameters p = camera.getParameters();
        if (p == null) {
            return;
        }
        if (p.getMaxNumFocusAreas() <= 0) {
            return;
        }

        try {
            //Convert from View's width and height to +/- 1000
            //Rect targetFocusRect = getCameraArea(tfocusRect, 1000);
            //            Rect targetFocusRect = getCameraArea(tfocusRect, 100);
            //            List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            //            Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
            //            focusList.add(focusArea);
            //设置focus 
            //            p.setFocusAreas(focusList);
            // check that metering areas are supported
            //            if (p.getMaxNumMeteringAreas() > 0) {
            //                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
            //                //            Rect areaRect1 = new Rect(tfocusRect.left * 400 / previewSize.width - 200,
            //                //                tfocusRect.top * 400 / previewSize.height - 200, tfocusRect.right * 400
            //                //                                                                 / previewSize.width - 200,
            //                //                tfocusRect.bottom * 400 / previewSize.height - 200); // specify an area in center of image
            //                Rect areaRect1 = getCameraArea(tfocusRect, 100);
            //                meteringAreas.add(new Camera.Area(areaRect1, 1000)); // set weight to 60%
            //                //Rect areaRect2 = new Rect(800, -1000, 1000, -800); // specify an area in upper right of image
            //                //meteringAreas.add(new Camera.Area(areaRect2, 400)); // set weight to 40%
            //                p.setMeteringAreas(meteringAreas);
            //            }
            camera.setParameters(p);
            startAutoFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始自动对焦
     * @param p
     */
    private void startAutoFocus() {
        camera.autoFocus(new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                //                if (autoFocusTimer != null) {
                //                    autoFocusTimer.cancel();
                //                }
                if (success) {
                    Log.i(TAG, "autofocus success");
                } else {
                    Log.i(TAG, "autofocus fail");
                    //startAutoFocus();
                }
            }
        });
    }

    /**
     * 取消自动聚焦
     */
    private void cancelAutoFocus() {
        try {
            camera.cancelAutoFocus();
        } catch (Error e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Rect getCamreaArea(int left, int right, int top, int bottom, int scale) {
        int fscale = 2 * scale;
        Rect result = new Rect(left * fscale / getWidth() - scale, top * fscale / getHeight()
                                                                   - scale, right * fscale
                                                                            / getWidth() - scale,
            bottom * fscale / getHeight() - scale);
        return result;
    }

    @SuppressWarnings("unused")
    private Rect getCameraArea(Rect rect, int scale) {
        return getCamreaArea(rect.left, rect.right, rect.top, rect.bottom, scale);
    }

    private boolean isSupportAutoFocus(Parameters p) {
        //设置自动对焦模式
        List<String> focusModes = p.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 相机是否旋转过
     * @return
     */
    protected boolean isCameraRotated() {
        if (cameraDisplayRotation == 90 || cameraDisplayRotation == 270
            || cameraDisplayRotation == 180) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 相机输出的长宽是否需切换(旋转90,270将导致长宽切换)
     * @return
     */
    protected boolean isCameraWHSwitched() {
        if (cameraDisplayRotation == 90 || cameraDisplayRotation == 270) {
            return true;
        } else {
            return false;
        }
    }

    public CameraPreviewController getPreviewController() {
        return previewController;
    }

    public void setPreviewController(CameraPreviewController previewController) {
        this.previewController = previewController;
    }

    public interface TakePictureCallback {
        public void run(Bitmap bmp);
    }

    /**
     * 相机预览控制器
     *
     * @author zhanqu
     * @date 2013-11-21 上午11:06:37
     */
    public class CameraPreviewController {
        private CameraPreview preview;

        //是否默认使用前置摄像头
        private boolean isFrontPrior = true;

        private int frontCameraId;
        private int backCameraId;

        public CameraPreviewController(CameraPreview preview) {
            this.preview = preview;
            frontCameraId = CameraHelper.findFrontCameraId(getContext());
            backCameraId = CameraHelper.findBackCameraId(getContext());
        }

        /**
         * 获取优先相机id
         * 如果优先前置相机，且前置存在，则取前置
         * 否则，先判断后置存在，不存在再取前置
         * @return
         */
        public int getPriorCameraId() {
            if (isFrontPrior) {
                return frontCameraId != CameraHelper.NO_CAMERA ? frontCameraId : backCameraId;
            } else {
                return backCameraId != CameraHelper.NO_CAMERA ? backCameraId : frontCameraId;
            }
        }

        public List<String> getSupportFlashMode() {
        	List<String> result = null;
            if (preview.camera != null) {
                Parameters p = preview.camera.getParameters();
                if (p != null) {
                    result = p.getSupportedFlashModes();
                }
            }
            return result;
        }
        
        /**
         * 默认闪光灯模式
         * @return
         */
        public String getDefaultFlashMode() {
            return Camera.Parameters.FLASH_MODE_AUTO;
        }

        /**
         * 获取当前闪光灯模式
         * @return
         */
        public String getCurrentFlashMode() {
            String result = "";
            if (preview.camera != null) {
                Parameters p = preview.camera.getParameters();
                if (p != null) {
                    result = p.getFlashMode();
                }
            }
            return result;
        }

        public int getCurrentFacing() {
            return preview.cameraFacing;
        }

        public void startPreview() {
            if (preview.camera != null) {
                preview.startPreview();
            }
        }

        public void stopPreview() {
            if (preview.camera != null) {
                preview.stopPreview();
            }
        }

        public boolean attachCameraOnResume() throws Exception {
            boolean result = false;
            int id = preview.cameraId;
            if (id >= 0) { //preview已获取过相机
                result = preview.attachCamera(id);
            } else {
                result = preview.attachCamera(getPriorCameraId());
            }
            return result;
        }

        public void detachCamera() {
            if (preview.camera != null) {
                preview.detachCamera();
            }
        }

        /**
         * 切换相机，并返回切换后的相机朝向(front or back)
         * @return
         * @throws Exception
         */
        public int switchCamera() throws Exception {
            int result = -1;
            if (canSwitchCamera()) {
                if (cameraId == frontCameraId) {
                    preview.switchToCamera(backCameraId);
                    result = CameraHelper.CAMERA_FACING_BACK;
                } else if (cameraId == backCameraId) {
                    preview.switchToCamera(frontCameraId);
                    result = CameraHelper.CAMERA_FACING_FRONT;
                }
            }
            return result;
        }

        public boolean canSwitchCamera() {
            return backCameraId >= 0 && frontCameraId >= 0;
        }

        public void setCameraFlashMode(String mode) {
            if (preview.camera != null) {
                preview.setCameraFlashMode(mode);
            }
        }
    }
}
