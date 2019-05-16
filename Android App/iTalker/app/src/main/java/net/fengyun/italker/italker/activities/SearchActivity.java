package net.fengyun.italker.italker.activities;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.fengyun.italker.common.app.Fragment;
import net.fengyun.italker.common.app.ToolbarActivity;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.frags.search.SearchGroupFragment;
import net.fengyun.italker.italker.frags.search.SearchUserFragment;

public class SearchActivity extends ToolbarActivity {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";//传递参数
    public static final int TYPE_USER = 1;//搜索人
    public static final int TYPE_GROUP = 2;//搜索群
    public int type;
    private SearchFragment mSearchFragment;

    /**
     * 用户activity的显示入口
     *
     * @param context 上下文
     * @param type    类型
     */
    public static void show(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity
                .class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        type = bundle.getInt(EXTRA_TYPE);
        //是搜索人或者搜索群
        return type == TYPE_USER || type == TYPE_GROUP;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initWeight() {
        super.initWeight();
        //显示相应的fragment
        Fragment fragment;
        if (type == TYPE_USER) {
            SearchUserFragment searchUserFragment = new SearchUserFragment();
            fragment = searchUserFragment;
            mSearchFragment = searchUserFragment;
        } else {
            SearchGroupFragment searchGroupFragment = new SearchGroupFragment();
            fragment = searchGroupFragment;
            mSearchFragment = searchGroupFragment;
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container,fragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //初始化菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        //找到搜索菜单
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            //添加搜索监听
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    //当点击了提交按钮的时候
                    search(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (TextUtils.isEmpty(newText)) {
                        //当文字改变的时候，咱们不会及时搜索，只在为NULL的情况下搜索
                        search("");
                        return true;
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * 搜索的方法
     *
     * @param query 搜索的字段
     */
    private void search(String query) {
        if (mSearchFragment == null) {
            return;
        }
        mSearchFragment.search(query);
    }

    /**
     * 搜索的fragment必须继承的fragment
     */
    public interface SearchFragment {
        void search(String content);
    }


}
