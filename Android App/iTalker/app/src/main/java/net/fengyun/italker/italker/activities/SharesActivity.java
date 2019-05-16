package net.fengyun.italker.italker.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import net.fengyun.italker.common.app.ToolbarActivity;
import net.fengyun.italker.common.widget.adapter.PagerAdapter;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.frags.shares.DayFragment;
import net.fengyun.italker.italker.frags.shares.FifteenPointsFragment;
import net.fengyun.italker.italker.frags.shares.FullMarksFragment;
import net.fengyun.italker.italker.frags.shares.HourFragment;
import net.fengyun.italker.italker.frags.shares.MonthFragment;
import net.fengyun.italker.italker.frags.shares.ThirtyFragment;
import net.fengyun.italker.italker.frags.shares.TimeFragment;
import net.fengyun.italker.italker.frags.shares.WeekFragment;

import butterknife.BindView;

/**
 * @author fengyun
 *         股票详情
 */
public class SharesActivity extends ToolbarActivity {


    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    public static void show(Context context) {
        context.startActivity(new Intent(context, SharesActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_shares;
    }

    @Override
    protected void initWeight() {
        super.initWeight();

    }

    @Override
    protected void initData() {
        super.initData();
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager())
                .add(new TimeFragment(), getString(R.string.label_time))
                .add(new FullMarksFragment(), getString(R.string.label_full_mark))
                .add(new FifteenPointsFragment(), getString(R.string.label_fifteen))
                .add(new ThirtyFragment(), getString(R.string.label_thirty))
                .add(new HourFragment(), getString(R.string.label_hour))
                .add(new DayFragment(), getString(R.string.label_day))
                .add(new WeekFragment(), getString(R.string.label_week))
                .add(new MonthFragment(), getString(R.string.label_month)));
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
