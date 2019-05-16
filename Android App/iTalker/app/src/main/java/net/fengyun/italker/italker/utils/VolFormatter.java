package net.fengyun.italker.italker.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * @author fengyun
 * 成交量左边的数量转换
 */
public class VolFormatter implements IAxisValueFormatter {

    private final int unit;
    private DecimalFormat mFormat;
    private String u;
    private float mValue;
    public VolFormatter(int unit,float value) {
        this.mValue= value;
        if (unit == 1) {
            mFormat = new DecimalFormat("#0");
        } else {
            mFormat = new DecimalFormat("#0.00");
        }
        this.unit = unit;
        this.u=StringUtil.getVolUnit(unit);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        mValue = mValue / unit;
        if(value==0){
            return u;
        }
        return mFormat.format(mValue);
    }
}