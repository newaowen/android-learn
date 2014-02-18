package com.warsong.android.learn.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.warsong.android.learn.R;

/**
 * window测试
 * @author newaowen@gmail.com
 */
public class WindowTestActivity extends Activity {

    private WindowManager mWM;
    
    private View mView;
    
    private WindowManager.LayoutParams mParams;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mParams = new WindowManager.LayoutParams();
        
        mView = LayoutInflater.from(this).inflate(R.layout.window_test, null);
        //Window w = getWindow();
        //w.addContentView(v, null);
        
        windowAddView();
    }
    
    private void windowAddView() {
        mWM = (WindowManager)mView.getContext().getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        // We can resolve the Gravity here by using the Locale for getting
        // the layout direction
        final Configuration config = mView.getContext().getResources().getConfiguration();
        
        int mGravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        final int gravity = Gravity.getAbsoluteGravity(mGravity, View.LAYOUT_DIRECTION_LTR);
        mParams.gravity = gravity;
        if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
            mParams.horizontalWeight = 1.0f;
        }
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
            mParams.verticalWeight = 1.0f;
        }
        mParams.x = 20;
        mParams.y = 20;
        mParams.verticalMargin = 20;
        mParams.horizontalMargin = 20;
        if (mView.getParent() != null) {
            mWM.removeView(mView);
        }
        mWM.addView(mView, mParams);
    }

}
