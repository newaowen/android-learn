package com.warsong.android.learn.helper;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

/**
 * 图片保存辅助类
 *
 * @author zhanqu
 * @date 2013-12-10 下午10:07:04
 */
public class SaveImageHelper {

    public final static String TAG = "BillSaveImageHelper";

    private Context context;

    public SaveImageHelper(Context context) {
        this.context = context;
    }

    public void save(String prefix, byte[] bytes, SaveImageResultCallback callback) {
        new SavePhotoTask(context, bytes, callback).execute();
    }

    /**
     * 照片保存后台线程
     * @author zhanqu
     * @date 2013-11-21 下午4:00:49
     */
    private class SavePhotoTask extends AsyncTask<Void, Object, String> {
        private final Context context;
        private byte[] bytes;
        private SaveImageResultCallback callback;

        public SavePhotoTask(Context context, byte[] bytes, SaveImageResultCallback callback) {
            this.context = context;
            this.bytes = bytes;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (bytes != null) {
                return doSavePhoto(bytes);
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !"".equals(result)) {
                callback.run(true, result);
            } else {
                callback.run(false, result);
            }
            super.onPostExecute(result);
        }

        @SuppressLint("SimpleDateFormat")
        protected String doSavePhoto(byte[] data) {
            File pictureFileDir = getDir();
            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                Log.d(TAG, "Can't create directory to save image.");
                return null;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            String date = dateFormat.format(new Date());
            String name = "alipay_annual_bill_" + date;
            String fileName = pictureFileDir.getPath() + File.separator + name + ".jpg";
            File pictureFile = new File(fileName);
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                //图片插入media store
                insertAndBroadCastFileScan(name, fileName);
                return fileName;
            } catch (IOException e) {
                //TODO 空间不够的异常
                Log.d(TAG, "File" + fileName + "not saved: " + e.getMessage());
            } catch (Exception e) {
                Log.d(TAG, "File" + fileName + "not saved: " + e.getMessage());
            } 
            return null;
        }

        /**
         * 广播媒体文件扫描事件
         */
        private void insertAndBroadCastFileScan(String name, String fileName) {
            //图片保持进数据库
            final ContentValues values = new ContentValues(2);
            values.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Video.Media.DATA, fileName);
            try {
                //将图片（及缩略图）插入media数据库
                MediaStore.Images.Media.insertImage(context.getContentResolver(), fileName, name,
                    "alipay annual bill photos");
                //(插入后不需要scan file可立即看到)
                //发送scan广播
                Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
                    Uri.parse("file:///" + fileName));
                context.sendBroadcast(i);
                //String scanFile = "file://" + fileName;
                //                MediaScannerConnection.scanFile(context, new String[] { fileName }, null,
                //                    new MediaScannerConnection.OnScanCompletedListener() {
                //                        public void onScanCompleted(String path, Uri uri) {
                //                            callback.run(true, path);
                //                            Log.i("ExternalStorage", "Scanned path: " + path + ":");
                //                            Log.i("ExternalStorage", "Scanned uri: " + uri);
                //                        }
                //                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private File getDir() {
            File rootsd = Environment.getExternalStorageDirectory();
            File dcim = new File(rootsd.getAbsolutePath() + "/" + Environment.DIRECTORY_DCIM);
            return dcim;
        }
    }

    public interface SaveImageResultCallback {
        public void run(boolean success, String path);
    }

//    public interface ScanImageCompleteCallback {
//        public void run(String path, Uri uri);
//    }

}

