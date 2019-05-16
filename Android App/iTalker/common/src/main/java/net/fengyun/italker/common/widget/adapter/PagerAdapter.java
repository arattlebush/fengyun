package net.fengyun.italker.common.widget.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengyun
 *         ViewPager的适配器
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;

    private List<String> mTitles;


    public PagerAdapter(FragmentManager fm) {
        super(fm);
        this.mFragments = new ArrayList<>();
        this.mTitles = new ArrayList<>();
    }


    public PagerAdapter add(Fragment fragment, String title) {
        if (fragment != null && !TextUtils.isEmpty(title)) {
            mFragments.add(fragment);
            mTitles.add(title);
        }
        return this;
    }

    public PagerAdapter addAll(List<Fragment> fragments, List<String> titles) {
        if (fragments != null && fragments.size() > 0 && titles != null && titles.size() > 0) {
            mFragments.addAll(fragments);
            mTitles.addAll(titles);
        }
        return this;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
