package net.fengyun.italker.italker.frags.shares;


import android.view.View;

import com.github.mikephil.charting.charts.LineChart;

import net.fengyun.italker.common.app.Fragment;
import net.fengyun.italker.italker.R;

import butterknife.BindView;


/**
 * 分时线
 *
 * @author fengyun
 */
public class TimeFragment extends Fragment {

    @BindView(R.id.lineChart)
    LineChart mLineChart;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_time;
    }

    @Override
    protected void initWeight(View root) {
        super.initWeight(root);
    }

    @Override
    protected void initData() {
        super.initData();
//        ChartUtil.initChart(mLineChart);
//        List<Entry> mData = new ArrayList<>();
//
//        for (int i = 0; i < 20; i++) {
//            mData.add(new Entry(i, new Random().nextInt(50)));
//        }
//        ChartUtil.notifyDataSetChanged(mLineChart, mData, 0);
    }
}
