package net.fengyun.italker.italker.weight;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.utils.DateUtil;

import fengyun.android.com.factory.model.db.Product;

/**
 * 浮动的popupwindow
 */

public class CombinedMarkerView extends MarkerView{

    private TextView mOpen;
    private TextView mHigh;
    private TextView mLow;
    private TextView mClose;
    private TextView mTime;
    private TextView mVolume;
    private int status;
    private Product entry;


    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CombinedMarkerView(Context context, int layoutResource, int status) {
        super(context, layoutResource);
        init(status);

    }
    /**
     * 添加指定的数据进来，可以做成多态形式
     */
    public void setData(Product entry) {
        this.entry = entry;
    }


    private void init(int status) {
        this.status = status;
        mTime = (TextView) findViewById(R.id.cm_time);
        mOpen = (TextView) findViewById(R.id.cm_open);
        mHigh = (TextView) findViewById(R.id.cm_high);
        mLow = (TextView) findViewById(R.id.cm_low);
        mClose = (TextView) findViewById(R.id.cm_closed);
        mVolume = (TextView) findViewById(R.id.cm_volume);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (status == 1) {
            mTime.setText(DateUtil.time(entry.getTime()));
        } else if (status == 4) {
            mTime.setText(entry.getTime());
        } else {
            mTime.setText(entry.getTime());
        }

        mOpen.setText(String.valueOf(entry.getOpen()));
        mHigh.setText(String.valueOf(entry.getHigh()));
        mLow.setText(String.valueOf(entry.getLow()));
        mClose.setText(String.valueOf(entry.getClose()));
        if (entry.getVolume() / 10000 > 1) {
            mVolume.setText(String.valueOf(entry.getVolume() / 10000) + "万");
        } else {
            mVolume.setText(String.valueOf(((int) entry.getVolume())));
        }
        if (entry.getClose() < entry.getOpen()) {
            mClose.setTextColor(Color.parseColor("#dd08ed08"));
        } else {
            mClose.setTextColor(Color.parseColor("#ff0000"));
        }
    }
}
