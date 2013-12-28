package com.warsong.android.learn.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.warsong.android.learn.R;
import com.warsong.android.learn.camera.CameraHelper;
import com.warsong.android.learn.camera.CameraPreview;
import com.warsong.android.learn.camera.CameraPreview.CameraPreviewController;
import com.warsong.android.learn.helper.SaveImageHelper;
import com.warsong.android.learn.util.GeneralUtil;
import com.warsong.android.learn.widget.PhotoFlingerViewPagerAdapter;
import com.warsong.android.learn.widget.PhotoViewPagerIndicator;

/**
 * 账单合影界面 (flinger有组合的含义,见SurfaceFlinger)
 * (当前只有年账单中使用)
 * @author zhanqu
 * @date 2013-11-19 上午10:38:52
 */
public class CameraTestActivity extends Activity {

    private static final String TAG = "CameraTestActivity";

    private View contentView;
    private CameraPreview preview;
    private CameraPreviewController previewController;

    private StateHelper stateHelper;
    //模板
    private ViewPager viewPager;
    private PhotoViewPagerIndicator vpIndicator;
    private PhotoFlingerViewPagerAdapter viewPagerAdapter;
    private ViewGroup indicatorBox;

    //生成的照片显示view
    private ImageView photoImageView;
    //相机控制按钮容器
    private View cameraControlBox;
    private ImageView flashBtn;
    private View switchBtn;
    private View snapBtn;
    private View shareBtn;
    private ImageView albumImage;
    private Button cancelBtn;

    private ViewGroup photoBox;
    //待分享的图片(用于有效回收图片)
    private Bitmap toShareBitmap = null;

    //闪光灯设置：不支持，禁止，自动，打开
    private String flashMode;

    private String expend = "";
    private String fundIncome = "";

    private GestureDetector gestureDetector;
    //TODO 分享标题等

    //private BillShareHelper shareHelper;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        contentView = LayoutInflater.from(this).inflate(R.layout.camera_test, null);
        setContentView(contentView);

        processIntent();
        initView();

        //shareHelper = new BillShareHelper(this);
        stateHelper = new StateHelper(this);
        stateHelper.enterState(StateHelper.STATE_CAMERA);
    }

    //protected void onFinishin
    private void processIntent() {
        Intent i = getIntent();
        if (i != null) {
            expend = i.getStringExtra("expend");
            fundIncome = i.getStringExtra("fundIncome");
        }
    }

    private void initView() {
        preview = (CameraPreview) findViewById(R.id.camera_preview);
        previewController = preview.getPreviewController();

        cameraControlBox = findViewById(R.id.camera_control_box);
        photoImageView = (ImageView) findViewById(R.id.photo);
        photoBox = (ViewGroup) findViewById(R.id.photo_box);

        flashBtn = (ImageView) findViewById(R.id.flash_btn);
        flashBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeCameraFlashMode();
            }
        });
        //默认关闭闪光灯(前置相机)
        setCameraFlashMode("off");

        switchBtn = findViewById(R.id.switch_btn);
        switchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    previewController.switchCamera();
                    updateFlashBtn();
                } catch (Exception e) {
                    handleCameraException();
                }
            }
        });

        snapBtn = findViewById(R.id.snap_btn);
        snapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                preview.takePicture(new CameraPreview.TakePictureCallback() {

                    @Override
                    public void run(Bitmap bmp) {
                        if (bmp != null) {
                            try {
                                //设置分享状态使用的图片
                                setShareBitmap(bmp);
                                //进入分享状态
                                stateHelper.enterState(StateHelper.STATE_SHARE);
                            } catch (OutOfMemoryError e) {
                                //OOM异常
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

        shareBtn = findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Timer.startTick();
                Bitmap bmp = GeneralUtil.captureView(photoBox);
                //计时
                //Log.i(TAG, "capture view consume: " + Timer.stopAndRestartTick() + "ms");
                byte[] bytes = GeneralUtil.bitmap2JPEGBytes(bmp, 60);
                //Log.i(TAG, "bitmap2JPEGBytes consume: " + Timer.stopTick() + "ms");
                if (bytes != null) {
                    //分享上传图片
                    //share(bytes);
                    //TODO 待修改截图并保存
                    //保存到本地的图片
                    saveSharePhoto(bytes);
                    GeneralUtil.recycleBitmap(bmp);
                }
            }
        });

        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                handleBack();
            }
        });

        albumImage = (ImageView) findViewById(R.id.album_img);
        albumImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //跳转到相册
                Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE_REQUEST);
            }
        });

        //8套模板
        viewPagerAdapter = new PhotoFlingerViewPagerAdapter(this, expend, fundIncome, 8);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        //view pager离屏缓存
        viewPager.setOffscreenPageLimit(1);

        //动态增加indicator
        indicatorBox = (ViewGroup) findViewById(R.id.indicator_box);
        vpIndicator = new PhotoViewPagerIndicator(this, indicatorBox);
        vpIndicator.setViewPager(viewPager);

        //view pager single tap事件进行auto focus
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onSingleTapUp(MotionEvent e) {
                preview.handleTouchFocus(e);
                return false;
            }
        });
        viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CameraHelper.isSupportBackCamera(this) && CameraHelper.isSupportFrontCamera(this)) {
            setSwitchBtnVisibility(View.VISIBLE);
        } else {
            setSwitchBtnVisibility(View.INVISIBLE);
        }
        handleAttachCamera();
        //更新缩略图图片
        refreshPhotoBtn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handleDetachCamera();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleBack();
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri img = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(img, filePathColumn, null, null,
                        null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                    }

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    try {
                        int w = 0, h = 0;
                        int rotation = CameraHelper.getCameraPhotoOrientation(this, filePath);
                        if (rotation > 0) {
                            w = preview.getHeight();
                            h = preview.getWidth();
                        } else {
                            w = preview.getWidth();
                            h = preview.getHeight();
                        }
                        Bitmap bmp = GeneralUtil.decodeFile(filePath, w, h);
                        if (rotation > 0) {
                            bmp = GeneralUtil.rotateBitmap(bmp, rotation, true);
                        }
                        setShareBitmap(bmp);
                        stateHelper.enterState(StateHelper.STATE_SHARE);
                    } catch (Exception e) { //文件不存在？
                        e.printStackTrace();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //TODO 提示？
                }
                break;
            default:
        }
    }

    private void handleAttachCamera() {
        if (CameraHelper.isSupportCamera(this)) {
            try {
                boolean result = previewController.attachCameraOnResume();
                if (result) {
                    updateFlashBtn();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            handleNoCamera();
        }
    }

    private void handleDetachCamera() {
        previewController.detachCamera();
    }

    /**
     * TODO attach camera时打开相机失败
     */
    private void handleCameraException() {
        //alertConfirmExit("相机打开失败");
    }

    //设备没有相机，提示用户
    private void handleNoCamera() {
        //alertConfirmExit("设备没有相机");
    }

    /**
     * 处理返回事件
     */
    private void handleBack() {
        if (stateHelper.curState == StateHelper.STATE_CAMERA) {
            finish();
        } else if (stateHelper.curState == StateHelper.STATE_SHARE) {
            stateHelper.enterState(StateHelper.STATE_CAMERA);
        }
    }

    /**
     * 提示打开
     * @param str
     */
//    private void alertConfirmExit(String str) {
//        alert(null, str, getString(R.string.bill_ok), new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        }, null, null);
//    }

    
    /**
     * (拍照完成后进入分享时)将待分享照片和模板截图并保存
     */
    private void saveSharePhoto(byte[] bytes) {
        Log.w("CameraTestActivity", "save photo size: " + (bytes.length/1000f) + "Byte");
        new SaveImageHelper(this).save("alipay_annual_bill_", bytes,
            new SaveImageHelper.SaveImageResultCallback() {

                @Override
                public void run(final boolean success, String path) {
                    if (success) {
                        //图片保存成功后更新thumbnail
                        refreshPhotoBtn();
                    } else {
                        //toast("图片保存失败", Toast.LENGTH_SHORT);
                    }
                }
            });
    }

    /**
     * 设置待分享图片
     * @param bmp
     */
    protected void setShareBitmap(Bitmap bmp) {
        //注意回收旧的待分享图片
        if (toShareBitmap != null) {
            toShareBitmap.recycle();
        }
        toShareBitmap = bmp;
    }

    /**
     * 更新相册按钮
     */
    private void refreshPhotoBtn() {
        Bitmap bmp = getLatestPhotoThumbnail();
        if (bmp != null) {
            albumImage.setImageBitmap(bmp);
        }
    }

    /**
     * 获取相册内最新图片缩略图
     * @return
     */
    private Bitmap getLatestPhotoThumbnail() {
        String[] projection = { MediaStore.Images.Thumbnails.IMAGE_ID,
                               MediaStore.Images.Thumbnails.DATA };
        // Create the cursor pointing to the SDCard
        Cursor cursor = getContentResolver().query(
            MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, // Which columns to return
            null, // Return all rows
            null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(Thumbnails.IMAGE_ID);
            int dataIndex = cursor.getColumnIndex(Thumbnails.DATA);
            if (cursor.moveToLast()) {
                long id = cursor.getLong(columnIndex);
                try {
                    Bitmap bmp = Thumbnails.getThumbnail(getContentResolver(), id,
                        Thumbnails.MICRO_KIND, null);
                    //获取照片文件选择图
                    int rotation = CameraHelper.getCameraPhotoOrientation(this,
                        cursor.getString(dataIndex));
                    cursor.close();
                    if (rotation > 0) {
                        bmp = GeneralUtil.rotateBitmap(bmp, rotation, true);
                    }
                    return bmp;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        return null;
    }

    private void updateFlashBtn() {
        if (previewController.getCurrentFacing() == CameraHelper.CAMERA_FACING_FRONT) {
            setFlashBtnVisibility(View.INVISIBLE);
        } else {
            setFlashBtnVisibility(View.VISIBLE);
            //切换到当前闪光灯状态
            setCameraFlashMode(previewController.getDefaultFlashMode());
        }
    }

    /**
     * 相机闪光灯状态变化逻辑
     */
    protected void changeCameraFlashMode() {
        String nextMode = "";
        if (flashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
            nextMode = Camera.Parameters.FLASH_MODE_AUTO;
        } else if (flashMode.equals(Camera.Parameters.FLASH_MODE_AUTO)) {
            nextMode = Camera.Parameters.FLASH_MODE_OFF;
        } else if (flashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
            nextMode = Camera.Parameters.FLASH_MODE_ON;
        }
        if (!"".equals(nextMode)) {
            setCameraFlashMode(nextMode);
        }
    }

    /**
     * 设置相机闪光灯状态，处理相应展示
     * @param mode
     */
    private void setCameraFlashMode(String mode) {
        //默认关闭
        int res = 0;
        if (mode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
            res = R.drawable.bill_photo_flinger_flash_off;
        } else if (mode.equals(Camera.Parameters.FLASH_MODE_AUTO)) {
            res = R.drawable.bill_photo_flinger_flash_auto;
        } else if (mode.equals(Camera.Parameters.FLASH_MODE_ON)) {
            res = R.drawable.bill_photo_flinger_flash_on;
        }
        flashMode = mode;
        flashBtn.setImageResource(res);
        previewController.setCameraFlashMode(flashMode);
    }

    private void setSwitchBtnVisibility(int visibility) {
        switchBtn.setVisibility(visibility);
        autoSetCameraControlVisibility();
    }

    private void setFlashBtnVisibility(int visibility) {
        flashBtn.setVisibility(visibility);
        autoSetCameraControlVisibility();
    }

    private void autoSetCameraControlVisibility() {
        //cameraControlBox.setVisibility(View.GONE);
        if (stateHelper.curState == StateHelper.STATE_SHARE) {
            cameraControlBox.setVisibility(View.GONE);
        } else if (stateHelper.curState == StateHelper.STATE_CAMERA) {
            if ((switchBtn.getVisibility() == View.GONE || switchBtn.getVisibility() == View.INVISIBLE)
                && (flashBtn.getVisibility() == View.GONE || flashBtn.getVisibility() == View.INVISIBLE)) {
                cameraControlBox.setVisibility(View.GONE);
                // 
            } else {
                cameraControlBox.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 状态管理辅助类
     *
     * @author zhanqu
     * @date 2013-11-21 下午3:12:09
     */
    public class StateHelper {
        public static final int STATE_CAMERA = 0;
        public static final int STATE_SHARE = 1;

        public int curState = 0;

        public Activity context;

        public StateHelper(Activity context) {
            this.context = context;
        }

        public void enterState(int state) {
            curState = state;
            if (state == STATE_CAMERA) {
                shareBtn.setVisibility(View.GONE);
                snapBtn.setVisibility(View.VISIBLE);

                photoImageView.setVisibility(View.GONE);
                photoImageView.setImageBitmap(toShareBitmap);
                preview.setVisibility(View.VISIBLE);

                previewController.startPreview();
                //更新相册按钮
                refreshPhotoBtn();
            } else if (state == STATE_SHARE) {
                shareBtn.setVisibility(View.VISIBLE);
                snapBtn.setVisibility(View.GONE);

                photoImageView.setVisibility(View.VISIBLE);
                photoImageView.setImageBitmap(toShareBitmap);
                preview.setVisibility(View.GONE);

                previewController.stopPreview();
            }
            autoSetCameraControlVisibility();
        }
    }

}
