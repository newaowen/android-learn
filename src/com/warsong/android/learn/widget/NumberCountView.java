package com.warsong.android.learn.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.warsong.android.learn.R;

/**
 * 数量加减(计数)设置控件
 *
 * @author zhanqu
 * @date 2014-1-11 下午12:05:38
 */
public class NumberCountView extends LinearLayout implements OnClickListener {

    public int step = 1;

    public int max = -1;

    public int min = -1;

    public int start = 0;

    private int value;

    private TextView textView;

    private Button substractBtn;

    private Button addBtn;
    
    private float valueTextSize;

    public NumberCountView(Context context) {
        super(context);
        init(context, null);
    }

    public NumberCountView(Context context, AttributeSet as) {
        super(context, as);
        init(context, as);
    }

    public NumberCountView(Context context, AttributeSet as, int defStyle) {
        super(context, as, defStyle);
        init(context, as);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.number_count_view, this, true);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.number_count_view);
            step = a.getInt(R.styleable.number_count_view_step, step);
            start = a.getInt(R.styleable.number_count_view_start, start);
            max = a.getInt(R.styleable.number_count_view_max, max);
            min = a.getInt(R.styleable.number_count_view_min, min);
            
            valueTextSize = a.getDimension(R.styleable.number_count_view_valueTextSize, 22);

            //设置组件背景?
            //setBackgroundResource(R.drawable.border);
            //设置按钮自身?
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    private void initViews() {
        textView = (TextView) findViewById(R.id.text);
        addBtn = (Button) findViewById(R.id.add);
        substractBtn = (Button) findViewById(R.id.substract);

        addBtn.setOnClickListener(this);
        substractBtn.setOnClickListener(this);
        
        if (valueTextSize > 0) {
            textView.setTextSize(valueTextSize);
        }
        
        resetValue();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int newV;
        switch (id) {
            case R.id.add:
                newV = value + step;
                setValue(newV);
                break;
            case R.id.substract:
                newV = value - step;
                setValue(newV);
                break;
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value >= max) {
            value = max;
            addBtn.setEnabled(false);
        } else if (value <= min) {
            value = min;
            substractBtn.setEnabled(false);
        } else {
            if (!addBtn.isEnabled()) {
                addBtn.setEnabled(true);
            }
            if (!substractBtn.isEnabled()) {
                substractBtn.setEnabled(true);
            }
        }
        this.value = value;
        textView.setText(String.valueOf(value));
    }
    
    public void resetValue() {
        setValue(start);
    }

}
