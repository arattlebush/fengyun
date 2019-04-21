package net.fengyun.italker.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author fengyun
 * 正方形布局
 */
public class SquareLayout extends FrameLayout{
    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //因为这个控件宽高是一样的 所以返回给基类的是宽高都市宽 是基于宽度的正方形
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
