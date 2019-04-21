package net.fengyun.italker.italker.frags.shares;


import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import net.fengyun.italker.common.app.Fragment;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.utils.PriceUtil;
import net.fengyun.italker.italker.utils.StringUtil;
import net.fengyun.italker.italker.utils.VolFormatter;
import net.fengyun.italker.italker.weight.CandleChart;
import net.fengyun.italker.italker.weight.CombinedMarkerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import fengyun.android.com.factory.model.db.Product;

import static net.fengyun.italker.italker.R.id.barChart;


/**
 * @author fengyun
 *         五分钟K线
 */
public class FullMarksFragment extends Fragment {


    private boolean isRefresh = false;

    @BindView(R.id.candlerChart)
    CandleChart mChart;

    @BindView(barChart)
    BarChart mBarChart;

    XAxis mBarXAxis, mXAxisK;
    float sum = 0;

    YAxis mBarYAxisLeft, mBarYAxisRight, mYAxisLeftK, mYAxisRightK;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_full_marks;
    }


    @Override
    protected void initWeight(View root) {
        super.initWeight(root);
        initChart();
    }

    private void initChart() {

        mBarChart.setDrawBorders(true);
        mBarChart.setBorderWidth(1);
        mBarChart.setBorderColor(getResources().getColor(R.color.grey_500));
        Description description = new Description();
        description.setText("福鼎白茶");
        mBarChart.setDescription(description);// TODO 等下纠正
        mBarChart.setDragEnabled(true);
        mBarChart.setScaleYEnabled(false);
        mBarChart.setAutoScaleMinMaxEnabled(true);
        Legend barChartLegend = mBarChart.getLegend();
        barChartLegend.setEnabled(false);

        //BarYAxisFormatter  barYAxisFormatter=new BarYAxisFormatter();
        //bar x y轴
        mBarXAxis = mBarChart.getXAxis();
        mBarXAxis.setDrawLabels(true);
        mBarXAxis.setDrawGridLines(false);
        mBarXAxis.setDrawAxisLine(false);
        mBarXAxis.setTextColor(getResources().getColor(R.color.grey_600));
        mBarXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mBarXAxis.setGridColor(getResources().getColor(R.color.grey_500));

        mBarYAxisLeft = mBarChart.getAxisLeft();
        mBarYAxisLeft.setAxisMinValue(0);
        mBarYAxisLeft.setDrawGridLines(false);
        mBarYAxisLeft.setDrawAxisLine(false);
        mBarYAxisLeft.setTextColor(getResources().getColor(R.color.yellow_300));
        mBarYAxisLeft.setDrawLabels(true);
        mBarYAxisLeft.setSpaceTop(0);
        mBarYAxisLeft.setLabelCount(2, true);
        mBarYAxisRight = mBarChart.getAxisRight();
        mBarYAxisRight.setDrawLabels(false);
        mBarYAxisRight.setDrawGridLines(false);
        mBarYAxisRight.setDrawAxisLine(false);
        /****k线控件设置******/

        mChart.setDrawBorders(true);
        mChart.setBorderWidth(1);
        mChart.setBorderColor(getResources().getColor(R.color.grey_500));
        mChart.setDescription(description);
        mChart.setDragEnabled(false);
        mChart.setScaleYEnabled(false);
        mChart.getLegend().setEnabled(false);

        mXAxisK = mChart.getXAxis();
        mXAxisK.setDrawLabels(true);
        mXAxisK.setDrawGridLines(false);
        mXAxisK.setDrawAxisLine(false);
        mXAxisK.setTextColor(getResources().getColor(R.color.grey_300));
        mXAxisK.setPosition(XAxis.XAxisPosition.BOTTOM);

        mYAxisLeftK = mChart.getAxisLeft();
        mYAxisLeftK.setDrawGridLines(true);
        mYAxisLeftK.setDrawAxisLine(false);
        mYAxisLeftK.setDrawLabels(true);
        mYAxisLeftK.setTextColor(getResources().getColor(R.color.grey_300));
        mYAxisLeftK.setGridColor(getResources().getColor(R.color.grey_300));
        mYAxisLeftK.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        mYAxisRightK = mChart.getAxisRight();
        mYAxisRightK.setDrawLabels(false);
        mYAxisRightK.setDrawGridLines(true);
        mYAxisRightK.setDrawAxisLine(false);
        mYAxisRightK.setGridColor(getResources().getColor(R.color.black_alpha_112));

        mChart.setDragDecelerationEnabled(true);
        mBarChart.setDragDecelerationEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.2f);
        mBarChart.setDragDecelerationFrictionCoef(0.2f);


//        // 将K线控的滑动事件传递给交易量控件
//        mChart.setOnChartGestureListener(new CoupleChartGestureListener(combinedchart, new Chart[]{barChart}));
//        // 将交易量控件的滑动事件传递给K线控件
//        mBarChart.setOnChartGestureListener(new CoupleChartGestureListener(barChart, new Chart[]{combinedchart}));
        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (mChart != null)
                    mChart.highlightValues(new Highlight[]{h});
            }

            @Override
            public void onNothingSelected() {
                if (mChart != null)
                    mChart.highlightValue(null);
            }
        });
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (mBarChart != null)
                    mBarChart.highlightValues(new Highlight[]{h});
            }

            @Override
            public void onNothingSelected() {
                if (mBarChart != null)
                    mBarChart.highlightValue(null);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            if(i==0){
                products.add(new Product(5.06f, 10.8f, 4.86f, 8.42f, 56, "06"));
            }else{
                products.add(new Product(products.get(i-1).getClose(),products.get(i-1).getClose()+3.6f, products.get(i-1).getClose()-1.25f,
                        products.get(i-1).getClose()+1.5f, new Random().nextInt(5000), "07"));
            }
        }

        setData(products);
    }

    private void setData(List<Product> products) {

        //运算
        String unit = StringUtil.getVolUnit(1000);
        int intUnit = 1;
        if ("x1".equals(unit)) {
            intUnit = 0;
        } else if ("x10".equals(unit)) {
            intUnit = 1;
        } else if ("x100".equals(unit)) {
            intUnit = 2;
        } else if ("x千".equals(unit)) {
            intUnit = 3;
        } else if ("x万".equals(unit)) {
            intUnit = 4;
        }
        mBarYAxisLeft.setValueFormatter(new VolFormatter((int) Math.pow(10, intUnit), 1000));
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<CandleEntry> candleEntries = new ArrayList<>();
        ArrayList<Entry> line5Entries = new ArrayList<>();
        ArrayList<Entry> line10Entries = new ArrayList<>();
        ArrayList<Entry> line30Entries = new ArrayList<>();
        for (int i = 0, j = 0; i < products.size(); i++, j++) {
            xVals.add(products.get(i).getTime());
            barEntries.add(new BarEntry(products.get(i).getOpen(), products.get(i).getClose()));
            candleEntries.add(new CandleEntry(i, products.get(i).getHigh(),
                    products.get(i).getLow(),
                    products.get(i).getClose(),
                    products.get(i).getOpen()));
            if (i >= 4) {
                sum = 0;
                line5Entries.add(new Entry(getSum(products, i - 4, i) / 5, i));
            }
            if (i >= 9) {
                sum = 0;
                line10Entries.add(new Entry(getSum(products, i - 9, i) / 10, i));
            }
            if (i >= 29) {
                sum = 0;
                line30Entries.add(new Entry(getSum(products, i - 29, i) / 30, i));
            }
        }
        //设置弹出框
        mChart.setMarker( new CombinedMarkerView(getContext(), R.layout.combined_marker, 1),products);

        BarDataSet barDataSet = new BarDataSet(barEntries, "成交量");
//        barDataSet.setBarSpacePercent(20); //bar空隙
        barDataSet.setHighlightEnabled(true);
        barDataSet.setHighLightAlpha(255);
        barDataSet.setHighLightColor(Color.WHITE);
        barDataSet.setDrawValues(false);
        List<Integer> list = new ArrayList<>();
        list.add(getResources().getColor(R.color.red_800));
        list.add(getResources().getColor(R.color.green_a400));
        barDataSet.setColors(list);
        /**设置数据**/
        BarData barData = new BarData(barDataSet);
        mBarChart.setData(barData);

        ViewPortHandler viewPortHandlerBar = mBarChart.getViewPortHandler();
        viewPortHandlerBar.setMaximumScaleX(culcMaxscale(xVals.size()));
        if (isRefresh) {
            Matrix touchmatrix = viewPortHandlerBar.getMatrixTouch();
            touchmatrix.postScale(3f, 1f);
        }

        float[] prices = PriceUtil.getDifferencePrice(products);

        mYAxisLeftK.setAxisMinValue(prices[2]);
        mYAxisLeftK.setAxisMaxValue(prices[1]);

        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "福鼎白茶");
        candleDataSet.setDrawHorizontalHighlightIndicator(false);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(Color.WHITE);
        candleDataSet.setValueTextSize(10f);
        candleDataSet.setDrawValues(false);
        candleDataSet.setColor(Color.RED);
        candleDataSet.setShadowWidth(1f);
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setShadowColorSameAsCandle(true);//影线颜色与实体一致
        candleDataSet.setShadowWidth(0.7f);//影线
        candleDataSet.setDecreasingColor(Color.RED);
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);//红涨，实体
        candleDataSet.setIncreasingColor(Color.GREEN);
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);//绿跌，空心
        candleDataSet.setNeutralColor(Color.RED);//当天价格不涨不跌（一字线）颜色
        candleDataSet.setHighlightLineWidth(1f);//选中蜡烛时的线宽
        CandleData candleData = new CandleData(candleDataSet);
        ArrayList<ILineDataSet> sets = new ArrayList<>();

        /******此处修复如果显示的点的个数达不到MA均线的位置所有的点都从0开始计算最小值的问题******************************/
        if (products.size() >= 30) {
            sets.add(setMaLine(5, xVals, line5Entries));
            sets.add(setMaLine(10, xVals, line10Entries));
            sets.add(setMaLine(30, xVals, line30Entries));
        } else if (products.size() >= 10 && products.size() < 30) {
            sets.add(setMaLine(5, xVals, line5Entries));
            sets.add(setMaLine(10, xVals, line10Entries));
        } else if (products.size() >= 5 && products.size() < 10) {
            sets.add(setMaLine(5, xVals, line5Entries));
        }
//
//        CombinedData combinedData = new CombinedData();
//        LineData lineData = new LineData(sets);
//        combinedData.setData(candleData);
//        combinedData.setData(lineData);
//
//        mChart.setData(lineData);

        mChart.setData(candleData);

        mChart.moveViewToX(products.size() - 1);
        ViewPortHandler viewPortHandlerCombin = mChart.getViewPortHandler();
        viewPortHandlerCombin.setMaximumScaleX(culcMaxscale(xVals.size()));
        if (isRefresh) {
            Matrix matrixCombin = viewPortHandlerCombin.getMatrixTouch();
            matrixCombin.postScale(3f, 1f);
            isRefresh = false;
            setOffset();
            mChart.animateY(1500);
            mChart.moveViewToX(products.size() - 1);
            mBarChart.moveViewToX(products.size() - 1);
        }
        mBarChart.setAutoScaleMinMaxEnabled(true);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.notifyDataSetChanged();
        mBarChart.notifyDataSetChanged();
        mChart.invalidate();
        mBarChart.invalidate();
    }

    private float culcMaxscale(float count) {
        return count / 127 * 5;
    }

    @NonNull
    private LineDataSet setMaLine(int ma, ArrayList<String> xVals, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + ma);
        if (ma == 5) {
            lineDataSetMa.setHighlightEnabled(true);
            lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
            lineDataSetMa.setHighLightColor(Color.WHITE);
        } else {/*此处必须得写*/
            lineDataSetMa.setHighlightEnabled(false);
        }
        lineDataSetMa.setDrawValues(false);
        if (ma == 5) {
            lineDataSetMa.setColor(Color.WHITE);
        } else if (ma == 10) {
            lineDataSetMa.setColor(Color.YELLOW);
        } else {
            lineDataSetMa.setColor(Color.parseColor("#ad00fc"));
        }
        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSetMa;
    }

    /*设置量表对齐*/
    private void setOffset() {
        float lineLeft = mChart.getViewPortHandler().offsetLeft();
        float barLeft = mBarChart.getViewPortHandler().offsetLeft();
        float lineRight = mChart.getViewPortHandler().offsetRight();
        float barRight = mBarChart.getViewPortHandler().offsetRight();
        float barBottom = mBarChart.getViewPortHandler().offsetBottom();
        float offsetLeft, offsetRight;
        float transLeft = 0, transRight = 0;
 /*注：setExtraLeft...函数是针对图表相对位置计算，比如A表offLeftA=20dp,B表offLeftB=30dp,则A.setExtraLeftOffset(10),并不是30，还有注意单位转换*/
        if (barLeft < lineLeft) {
           /* offsetLeft = Utils.convertPixelsToDp(lineLeft - barLeft);
            barChart.setExtraLeftOffset(offsetLeft);*/
            transLeft = lineLeft;
        } else {
            offsetLeft = Utils.convertPixelsToDp(barLeft - lineLeft);
            mChart.setExtraLeftOffset(offsetLeft);
            transLeft = barLeft;
        }
  /*注：setExtraRight...函数是针对图表绝对位置计算，比如A表offRightA=20dp,B表offRightB=30dp,则A.setExtraLeftOffset(30),并不是10，还有注意单位转换*/
        if (barRight < lineRight) {
          /*  offsetRight = Utils.convertPixelsToDp(lineRight);
            barChart.setExtraRightOffset(offsetRight);*/
            transRight = lineRight;
        } else {
            offsetRight = Utils.convertPixelsToDp(barRight);
            mChart.setExtraRightOffset(offsetRight);
            transRight = barRight;
        }
        mBarChart.setViewPortOffsets(transLeft, 15, transRight, barBottom);
    }

    /**
     * 算出平均价
     *
     * @param products 数据
     * @param a
     * @param b
     * @return 平均价
     */
    private float getSum(List<Product> products, Integer a, Integer b) {
        for (int i = a; i <= b; i++) {
            sum += products.get(i).getClose();
        }
        return sum;
    }


}
