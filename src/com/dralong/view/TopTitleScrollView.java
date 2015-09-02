package com.dralong.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

/**
 * Created by Jerry on 2015/8/27.
 */
public class TopTitleScrollView extends HorizontalScrollView {

    //  整体布局
    private View content;
    // 更多栏目选择布局
    private View more;
    // 拖动栏布局
    private View column;
    // 左阴影图片
    private ImageView leftImage;
    //  右阴影图片
    private ImageView rightImage;
    //  屏幕宽度
    private int mScreenWitdh = 0;
    //   父类的activity
    private Activity activity;

    public TopTitleScrollView(Context context) {
        super(context);
    }

    public TopTitleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopTitleScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置首页标题滚动
     *
     * @param param1
     * @param param2
     * @param param3
     * @param param4
     */
    @Override
    protected void onScrollChanged(int param1, int param2, int param3, int param4) {
        super.onScrollChanged(param1, param2, param3, param4);
        showOrHide();
        if (!activity.isFinishing() && content != null && leftImage != null && rightImage != null && more != null && column != null) {
            if (content.getWidth() <= mScreenWitdh) {
                leftImage.setVisibility(View.GONE);
                rightImage.setVisibility(View.GONE);
            }
        } else {
            return;
        }
        if (param1 == 0) {
            leftImage.setVisibility(View.GONE);
            rightImage.setVisibility(View.VISIBLE);
            return;
        }
        if (content.getWidth() - param1 + more.getWidth() + column.getLeft() == mScreenWitdh) {
            leftImage.setVisibility(View.VISIBLE);
            rightImage.setVisibility(View.GONE);
            return;
        }
        leftImage.setVisibility(View.VISIBLE);
        rightImage.setVisibility(View.VISIBLE);
    }

    public void setParam(Activity activity, int mScreenWitdh, View paramView1, ImageView paramView2, ImageView paramView3, View paramView4, View paramView5) {
        this.activity = activity;
        this.mScreenWitdh = mScreenWitdh;
        content = paramView1;
        leftImage = paramView2;
        rightImage = paramView3;
        more = paramView4;
        column = paramView5;
    }

    /**
     *
     */
    public void showOrHide() {
        if (!activity.isFinishing() && content != null) {
            measure(0, 0);
            if (mScreenWitdh >= getMeasuredWidth()) {
                leftImage.setVisibility(View.GONE);
                rightImage.setVisibility(View.GONE);
            }
        } else {
            return;
        }
        if (getLeft() == 0) {
            leftImage.setVisibility(View.GONE);
            rightImage.setVisibility(View.VISIBLE);
            return;
        }
        if (getRight() == getMeasuredWidth() - mScreenWitdh) {
            leftImage.setVisibility(View.VISIBLE);
            rightImage.setVisibility(View.GONE);
            return;
        }
        leftImage.setVisibility(View.VISIBLE);
        rightImage.setVisibility(View.VISIBLE);
    }
}
