package net.fengyun.italker.italker.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.List;

import fengyun.android.com.factory.model.db.Product;

/**
 * 自创的蜡烛图
 */

public class CandleChart extends CandleStickChart{

    private CombinedMarkerView mView;
    private List<Product> mProducts;

    public CandleChart(Context context) {
        super(context);
    }

    public CandleChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CandleChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setMarker(CombinedMarkerView view, List<Product> entities){
        this.mView = view;
        this.mProducts = entities;
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        if (!mDrawMarkers || !valuesToHighlight())
            return;
        for (int i = 0; i < mIndicesToHighlight.length; i++) {
            Highlight highlight = mIndicesToHighlight[i];
            int xIndex =  mIndicesToHighlight[i].getDataSetIndex();
            float deltaX = mXAxis != null
                    ? mXAxis.mAxisRange
                    : ((mData == null ? 0.f : mData.getDataSets().size()) - 1.f);
            if (xIndex <= deltaX && xIndex <= deltaX * mAnimator.getPhaseX()) {
                Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);
                // make sure entry not null
                if (e == null || e.getX() != mIndicesToHighlight[i].getX())
                    continue;

                float[] pos = getMarkerPosition(highlight);
                // check bounds
                if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                    continue;

                mView.setData(mProducts.get(i));
                mView.refreshContent(e, mIndicesToHighlight[i]);
               /*重新计算大小*/
                mView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mView.layout(0, 0, mView.getMeasuredWidth(),
                        mView.getMeasuredHeight());
                mView.draw(canvas, mViewPortHandler.contentLeft()
                        - mView.getWidth(), 0);//pos[1] - combinedMarkerView.getHeight() / 2
            }
        }
    }


}
