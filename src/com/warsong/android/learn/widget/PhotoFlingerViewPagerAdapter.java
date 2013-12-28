package com.warsong.android.learn.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warsong.android.learn.R;

/**
 * 合影模板适配器
 * (视觉同学变态需求，有些模板要等比拉伸，有些模板要从底部横向铺满容器，有些还需要顶部水平铺满容器，
 * 文字还得准确定位到图片内指定位置
 * 相当蛋疼，在这营销活动上真他妈浪费时间)
 * @author zhanqu
 * @date 2013-11-21 下午1:52:59
 */
public class PhotoFlingerViewPagerAdapter extends PagerAdapter {
    private Context context;
    private int viewCount;

    private String expend;
    private String fundIncome;

    private int containerWidth;
    private int containerHeight;

    //制造模板图片时采用的参考宽度
    private int refImgWidth = 640;
    private int refImgWidth2 = 720;
    
    //屏幕密度
    private double relativeDencity;
    private double dencity;
    //长度伸缩比(容器宽度与参考图片宽度比)
    private double refImgScale;
    private double refImgScale2;

    public PhotoFlingerViewPagerAdapter(Context context, String expend, String fundIncome,
                                        int viewCount) {
        this.context = context;
        this.viewCount = viewCount;
        this.expend = expend;
        this.fundIncome = fundIncome;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        dencity = metrics.densityDpi * 1.0 / 160;
        relativeDencity = dencity / 2;

        getContainerSize(null);
    }

    private void getContainerSize(View view) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        containerWidth = display.getWidth();
        //        containerWidth = view.getMeasuredWidth();
        //        containerHeight = view.getMeasuredHeight();
        //图片相对于imgRefWidth在容器内需要拉伸的比例
        refImgScale = containerWidth * 1.0 / refImgWidth;
        refImgScale2 = containerWidth * 1.0 / refImgWidth2;
    }

    @Override
    public int getCount() {
        return viewCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    /**
     * 初始化时就默认执行多次 
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        int id = R.layout.bill_photo_flinger_vp_item_0;
        switch (position) {
            case 0:
                break;
            case 1:
                id = R.layout.bill_photo_flinger_vp_item_1;
                break;
            case 2:
                id = R.layout.bill_photo_flinger_vp_item_2;
                break;
            case 3:
                id = R.layout.bill_photo_flinger_vp_item_3;
                break;
            case 4:
                id = R.layout.bill_photo_flinger_vp_item_4;
                break;
            case 5:
                id = R.layout.bill_photo_flinger_vp_item_5;
                break;
            case 6:
                id = R.layout.bill_photo_flinger_vp_item_6;
                break;
            case 7:
                id = R.layout.bill_photo_flinger_vp_item_7;
                break;
            default:
                break;
        }

        //这里可能有问题，不能保证获取准确的宽带和高度
        //getContainerSize(container);
        //图片相对于imgRefWidth在容器内需要拉伸的比例

        View view = LayoutInflater.from(context).inflate(id, null);
        TextView expendTV = (TextView) view.findViewById(R.id.expend);
        if (expendTV != null) {
            expendTV.setText(expend);
        }
        TextView fundIncomeTV = (TextView) view.findViewById(R.id.fund_income);
        if (fundIncomeTV != null) {
            fundIncomeTV.setText(fundIncome);
        }

        //第５个模板特殊处理,文字内偏移按图片缩放程度自动调整
        if (position == 0) {
            setItemView0(view);
        } else if (position == 1) {
            setItemView1(view);
        } else if (position == 2) {
            setItemView2(view);
        } else if (position == 3) {
            setItemView3(view);
        } else if (position == 4) {
            setItemView4(view);
        } else if (position == 5) {
            setItemView5(view);
        } else if (position == 6) {
            setItemView6(view);
        } else if (position == 7) {
            setItemView7(view);
        }

        ((ViewPager) container).addView(view);
        view.invalidate();

        return view;
    }

    /**
     * 上下图片等比拉伸，右下角文字
     * @param parent
     */
    private void setItemView0(View parent) {
        //图片拉伸
        ImageView top = (ImageView) parent.findViewById(R.id.top_img);
        ImageView bottom = (ImageView) parent.findViewById(R.id.bottom_img);
        strechImageView(top);
        strechImageView(bottom);
        setViewMargin(bottom, 0, 0, 0, (int) (20 * dencity));

        View expend = parent.findViewById(R.id.expend);
        if (expend != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) expend.getLayoutParams();
            //固定底部＋图片内偏移
            lp.rightMargin = (int) (20 * refImgScale);
            lp.bottomMargin = (int) (20 * dencity + 1 * refImgScale);
        }
    }

    /**
     * 背景图片铺满容器，文字水平居中，垂直居图片底部一定偏移
     * @param parent
     */
    private void setItemView1(View parent) {
        //ImageView img = (ImageView) parent.findViewById(R.id.img);
        //等比拉伸可能留白　or 拉伸变形 or 点9拉伸,
        //暂时选择点9拉伸
        //double wScale = getImageWidthFitScale(img);
        //double hScale = getImageHeightFitScale(img);
        //strechImageView(img, wScale, hScale);
        
        //底部图片基于720宽度，采用refImgScale2拉伸
        ImageView bottom = (ImageView) parent.findViewById(R.id.bottom_img);
        strechImageView(bottom, refImgScale2);
        
        View textBox = parent.findViewById(R.id.text_box);
        if (textBox != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textBox
                .getLayoutParams();
            //图片内偏移
            lp.bottomMargin = (int) (28 * refImgScale2);
        }
    }

    /**
     * 上下图片等比拉伸，右下角文字
     * @param parent
     */
    private void setItemView2(View parent) {
        //图片拉伸
        ImageView top = (ImageView) parent.findViewById(R.id.top_img);
        ImageView bottom = (ImageView) parent.findViewById(R.id.bottom_img);
        strechImageView(top);
        strechImageView(bottom);
        //右下角图片右边距，下边距
        setViewMargin(bottom, 0, 0, (int) (10 * dencity), (int) (50 * dencity));

        View income = parent.findViewById(R.id.fund_income);
        if (income != null) {
            //固定右,下边距离
            strechViewMargin(income, 0, 0, 10, 10, dencity);
        }
    }

    /**
     * 上水平填充，下图片等比拉伸，右下角文字
     * @param parent
     */
    private void setItemView3(View parent) {
        //上图水平填充
        ImageView top = (ImageView) parent.findViewById(R.id.top_img);
        double scale = getImageWidthFitScale(top);
        //右下图等比
        ImageView bottom = (ImageView) parent.findViewById(R.id.bottom_img);
        strechImageView(bottom);
        //右下角图片右边距，下边距
        setViewMargin(bottom, 0, 0, (int) (7 * dencity), (int) (15 * dencity));

        View expend = parent.findViewById(R.id.expend);
        if (expend != null) {
            strechViewMargin(expend, 46, 60, 0, 0, scale);
        }
        View fundIncomeTV = parent.findViewById(R.id.fund_income);
        if (fundIncomeTV != null) {
            strechViewMargin(fundIncomeTV, 46, 174, 0, 0, scale);
        }
    }

    /**
     * 居底水平充满容器, 文字特定偏移
     * @param parent
     */
    private void setItemView4(View parent) {
        View expendBox = parent.findViewById(R.id.expend_box);
        if (expendBox != null) {
            strechViewMargin(expendBox, 0, 0, 75, 24);
        }
    }

    /**
     * 居底水平充满容器，且带左右边距(够蛋疼)
     * @param parent
     */
    private void setItemView5(View parent) {
        ImageView img = (ImageView) parent.findViewById(R.id.img);
        //计算拉伸系数
        double scale = (containerWidth - dencity * 30) / getImageWidth(img);

        View expend = parent.findViewById(R.id.expend);
        if (expend != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) expend.getLayoutParams();
            //距底dp + 图片拉伸
            lp.bottomMargin = (int) (40 * dencity + (40 * scale));
        }
        View fundIncomeTV = parent.findViewById(R.id.fund_income);
        if (fundIncomeTV != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fundIncomeTV
                .getLayoutParams();
            //距底dp
            lp.bottomMargin = (int) (10 * dencity);
        }
    }

    /**
     * 图片铺满容器
     * @param parent
     */
    private void setItemView6(View parent) {
        //图片等比拉伸
        ImageView top = (ImageView) parent.findViewById(R.id.top_img);
        ImageView bottom = (ImageView) parent.findViewById(R.id.bottom_img);
        strechImageView(top, refImgScale2);
        strechImageView(bottom, refImgScale2);
        setViewMargin(top, (int) (15 * dencity), (int) (15 * dencity), 0, 0);
        setViewMargin(bottom, 0, 0, 0, (int) (20 * dencity));
        
        //ImageView img = (ImageView) parent.findViewById(R.id.img);
        //double wScale = getImageWidthFitScale(img);
        //double hScale = getImageHeightFitScale(img);
        //strechImageView(img, wScale);//, hScale);
        View textBox = parent.findViewById(R.id.text_box);
        if (textBox != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textBox.getLayoutParams();
            //固定底部＋图片内偏移
            lp.leftMargin = (int) (40 * refImgScale2);
            lp.bottomMargin = (int) (20 * dencity + 1 * refImgScale2);
        }
    }

    /**
     * 上下图片等比拉伸(带边距)
     * @param parent
     */
    private void setItemView7(View parent) {
        //图片拉伸
        ImageView top = (ImageView) parent.findViewById(R.id.top_img);
        ImageView bottom = (ImageView) parent.findViewById(R.id.bottom_img);
        strechImageView(top);
        //左上固定15dp
        setViewMargin(top, (int) (15 * dencity), (int) (15 * dencity), 0, 0);
        strechImageView(bottom);
        //右下固定15dp
        setViewMargin(bottom, 0, 0, (int) (15 * dencity), (int) (15 * dencity));

        View fundIncomeTV = parent.findViewById(R.id.fund_income);
        if (fundIncomeTV != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fundIncomeTV
                .getLayoutParams();
            //固定底部＋图片内偏移
            lp.topMargin = (int) (15 * dencity + 24 * refImgScale);
            lp.leftMargin = (int) (15 * dencity + 20 * refImgScale);
        }
    }

    private void strechViewMargin(View v, int left, int top, int right, int bottom) {
        strechViewMargin(v, left, top, right, bottom, refImgScale);
    }

    private void strechViewMargin(View v, int left, int top, int right, int bottom, double scale) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        lp.leftMargin = (int) (left * scale);
        lp.rightMargin = (int) (right * scale);
        lp.topMargin = (int) (top * scale);
        lp.bottomMargin = (int) (bottom * scale);
    }

    private void setViewMargin(View v, int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        lp.leftMargin = left;
        lp.rightMargin = right;
        lp.topMargin = top;
        lp.bottomMargin = bottom;
    }

    /**
     * 等比拉伸
     * 根据容器宽带与参考宽带之比进行拉伸
     * @param img
     */
    private void strechImageView(ImageView img) {
        strechImageView(img, refImgScale);
    }

    /**
     * 按指定伸缩比列拉伸
     * 根据容器宽带与参考宽带之比进行拉伸
     * @param img
     */
    private void strechImageView(ImageView img, double wScale, double hScale) {
        if (img == null) {
            return;
        }
        Drawable d = img.getDrawable();
        if (d == null) {
            return;
        }

        double w = d.getIntrinsicWidth() / relativeDencity;
        double h = d.getIntrinsicHeight() / relativeDencity;
        if (w == 0 || h == 0) {
            return;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) img.getLayoutParams();
        lp.leftMargin = 0;
        lp.rightMargin = 0;
        lp.width = (int) (w * wScale);
        lp.height = (int) (h * hScale);
    }

    /**
     * 等比拉伸
     * 根据容器宽带与参考宽带之比进行拉伸
     * @param img
     */
    private void strechImageView(ImageView img, double scale) {
        strechImageView(img, scale, scale);
    }

    /**
     * 计算水平填充比例
     */
    private double getImageWidthFitScale(ImageView img) {
        double result = 1;
        if (img == null) {
            return result;
        }

        double w = getImageWidth(img);
        if (w == 0) {
            return result;
        }

        return containerWidth / w;
    }

    /**
     * 计算垂直填充比例
     */
    @SuppressWarnings("unused")
    private double getImageHeightFitScale(ImageView img) {
        double result = 1;
        if (img == null) {
            return result;
        }

        double h = getImageHeight(img);
        if (h == 0) {
            return result;
        }

        return containerHeight / h;
    }

    private double getImageWidth(ImageView img) {
        Drawable d = img.getDrawable();
        if (d == null) {
            return 0;
        }
        //getIntrinsicWidth返回的是图片在当前设备下应该有的像素长度（会自动拉伸)，所以这里要除设备相对于2的密度
        double r = d.getIntrinsicWidth() / relativeDencity;
        return r;
    }

    private double getImageHeight(ImageView img) {
        Drawable d = img.getDrawable();
        if (d == null) {
            return 0;
        }
        double r = d.getIntrinsicHeight() / relativeDencity;
        return r;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

}
