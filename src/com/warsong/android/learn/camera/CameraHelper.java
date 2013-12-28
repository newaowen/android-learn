package com.warsong.android.learn.camera;
 
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

public class CameraHelper {

    private static final String TAG = "CameraHelper";

    public static final int CAMERA_FACING_BACK = 0;
    public static final int CAMERA_FACING_FRONT = 1;
    
    public static final int NO_CAMERA = -1;

    /**
     * 打开指定相机，如果系统版本低于9,则打开后置相机(9以先系统不支持前置)
     * TODO 抛出打开相机失败的异常
     * @param id
     * @return
     */
    @SuppressLint("NewApi")
    public static Camera open(int id) {
        Camera result = null;
        if (android.os.Build.VERSION.SDK_INT >= 9) {
            try {
                result = Camera.open(id);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            try {
                result = Camera.open();
                //反射打开指定相机
//                try {
//                  Method cameraOpenMethod = cameraClass.getMethod("open", Integer.TYPE);
//                  if (cameraOpenMethod != null) {
//                      //camera = (Camera) cameraOpenMethod.invoke(null, camIdx);
//                  }
//              } catch (RuntimeException e) {
//                  Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
//              }
            } catch (Exception e) {
                Log.d(TAG, "Error when trying Camera.open() on 9 lower system ", e);
            }
        }

        return result;
    }

    /**
     * 设备是否有摄像头
     * @return
     */
    public static boolean isSupportCamera(Context context) {
        return isSupportBackCamera(context) || isSupportFrontCamera(context);
    }

    /**
     * 设备是否支持前置摄像头
     * api 9前虽然sdk不支持摄像头，但设备可能提供了前置摄像头
     * @param context
     * @return
     */
    @SuppressLint("InlinedApi")
    public static boolean isSupportFrontCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    /**
     * 设备是否支持后置摄像头 (某些pad设备只有前置摄像头)
     * @return
     */
    public static boolean isSupportBackCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 获取前置摄像头id，如果设备无前置摄像头则返回空
     * (如2.2设备以下或2.3以上但没有前置摄像头)
     * @return
     */
    @SuppressLint("NewApi")
    public static int findFrontCameraId(Context context) {
        int result = NO_CAMERA;
        //先判断设备是否支持后置相机
        if (!isSupportFrontCamera(context)) {
            return result;
        }
        
        int level = android.os.Build.VERSION.SDK_INT;
        if (level >= 9) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = i;
                    break;
                }
            }
        } else {
            //TODO 2.3设备以下如何获得前置摄像头?
        }

        return result;
    }

    /**
     * 获取后置摄像头id
     */
    @SuppressLint("NewApi")
    public static int findBackCameraId(Context context) {
        int result = NO_CAMERA;
        //先判断设备是否支持后置相机
        if (!isSupportBackCamera(context)) {
            return result;
        }
        
        int level = android.os.Build.VERSION.SDK_INT;
        if (level >= 9) { //2.3设备上
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    result = i;
                    break;
                }
            }
        } else { //2.3设备以下 
            result = 0;
        }

        return result;
    }

    protected static Object getCameraInfoField(Object info, String fieldKey) {
        Object result = null; 
        try {
            Field field = info.getClass().getField("orientation");
            result = field.getInt(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 获取相机属性信息
     * @param cameraId
     * @return
     */
    @SuppressLint("NewApi")
    public static Object getCameraInfo(int cameraId) {
        Object result = null;
        if (Build.VERSION.SDK_INT >= 9) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            result = info;
        } else {
            //反射CameraInfo有没有意义？
            Class<?> cameraClass;
            try {
                cameraClass = Class.forName("android.hardware.Camera");
                Object cameraInfo = null;
                Class<?> cameraInfoClass = Class.forName("android.hardware.Camera$CameraInfo");
                if (cameraInfoClass != null) {
                    cameraInfo = cameraInfoClass.newInstance();
                }
                //Field field = null;
//                if (cameraInfo != null) {
//                    //field = cameraInfo.getClass().getField("facing");
//                }
                Method getCameraInfoMethod = cameraClass.getMethod("getCameraInfo", Integer.TYPE,
                    cameraInfoClass);
                if (getCameraInfoMethod != null && cameraInfoClass != null) {
                    getCameraInfoMethod.invoke(null, cameraId, cameraInfo);
                }
//                    for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
//                        getCameraInfoMethod.invoke(null, camIdx, cameraInfo);
//                        int facing = field.getInt(cameraInfo);
//                        if (facing == 1) { //Camera.CameraInfo.CAMERA_FACING_FRONT 
//                        }
//                    }
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 获取相机传感器的朝向
     * @param context
     * @param cameraId
     * @return
     */
    @SuppressLint("NewApi")
    public static int getCameraOrientation(int cameraId) {
        //默认角度为90(没有取到相机信息或2.2以下不支持orientation反射)
        int result = 90;
        Object info = getCameraInfo(cameraId);
        if (info != null) {
            if (Build.VERSION.SDK_INT >= 9) {
                result = ((Camera.CameraInfo)info).orientation;
            } else {
                Object r = getCameraInfoField(info, "orientation");
                if (r != null) {
                    result = (Integer)r;
                }  
            }
        }  
        return result;
    }

    /**
     * 获取相机facing,即是前置还是后置
     * @param cameraId
     * @return
     */
    @SuppressLint("NewApi")
    public static int getCameraFacing(int cameraId) {
        int result = -1;
        Object info = getCameraInfo(cameraId);
        if (info != null) {
            if (Build.VERSION.SDK_INT >= 9) {
                result = ((Camera.CameraInfo)info).facing;
                if (result == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = CAMERA_FACING_FRONT;
                } else { //back-facing
                    result = CAMERA_FACING_BACK;
                }
            } else {
                Object r = getCameraInfoField(info, "facing");
                if (r != null) {
                    result = (Integer)r;
                }
            }
        }
        
        return result;
    }

    /**
     * 根据当前设备的朝向设置相机旋转
     * (注意在api 14前不支持preview时调整转向)
     * @param context
     * @param cameraId
     * @param camera
     * @return 返回相机旋转角度
     */
    public static int setCameraDisplayOrientation(Context context, int cameraId, Camera camera) {
        //屏幕当前旋转角度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //屏幕朝向
        int rotation = wm.getDefaultDisplay().getRotation();
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

        int facing = getCameraFacing(cameraId);
        int orientation = getCameraOrientation(cameraId);
        int result = 0;
        if (facing == CAMERA_FACING_FRONT) {
            result = (orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { //非前置相机即为后置
            result = (orientation - degrees + 360) % 360;
        }
        //设置相机display的旋转角度(影响preview输出，不影响最终生成照片角度)
        camera.setDisplayOrientation(result);
        return result;
    }

//    /**
//     * @deprecated
//     * 从系统中获取一个相机id
//     * @param context
//     * @param isFrontPrior 是否优先查找前置相机
//     * @return
//     */
//    public static int findCameraId(Context context, boolean isFrontPrior) {
//        int result = -1;
//
//        int frontId = -1;
//        int backId = -1;
//        if (CameraHelper.isSupportFrontCamera(context)) {
//            frontId = CameraHelper.findFrontCameraId();
//        }
//        if (CameraHelper.isSupportBackCamera(context)) {
//            backId = CameraHelper.findBackCameraId();
//        }
//
//        if (frontId >= 0 && backId >= 0) {
//            if (isFrontPrior) {
//                result = frontId;
//            } else {
//                result = backId;
//            }
//        } else {
//            result = (frontId >= 0) ? frontId : backId;
//        }
//
//        return result;
//    }

    /**
     * 获取相机拍出的照片的旋转角度
     * @param context
     * @param imageUri
     * @param imagePath
     * @return
     */
    public static int getCameraPhotoOrientation(Context context, String imagePath) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
    
    /**
     * 判断是否魅族手机mx2
     * 该手机前置相机比较奇琶,不会翻转
     * @return
     */
    public static boolean isMZPhone() {
        String model = android.os.Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;
        Log.i(TAG, "model: " + model);
        Log.i(TAG, "manufacturer: " + manufacturer);
        if ("Meizu".equalsIgnoreCase(manufacturer)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 判断是否小米3手机
     * 该手机后置相机设置开启闪光灯时拍的图片会变黑
     * @return
     */
    public static boolean isXiaoMi3() {
        String model = android.os.Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;
        Log.i(TAG, "model: " + model);
        Log.i(TAG, "manufacturer: " + manufacturer);
        if ("Xiaomi".equalsIgnoreCase(manufacturer) && "MI 3".equalsIgnoreCase(model)) {
            return true;
        } else {
            return false;
        }
    }

}
