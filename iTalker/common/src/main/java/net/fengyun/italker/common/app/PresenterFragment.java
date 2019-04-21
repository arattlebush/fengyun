package net.fengyun.italker.common.app;

import android.content.Context;
import android.support.annotation.StringRes;

import net.fengyun.italker.factory.presenter.BaseContract;

/**
 *@author fengyun
 */
public abstract class PresenterFragment<Presenter extends BaseContract.Presenter>
        extends Fragment implements BaseContract.View<Presenter>{
    protected Presenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //在界面onAttach的时候触发
        initPresenter();
    }
    //初始化Presenter
    protected abstract Presenter initPresenter();

    @Override
    public void showError(@StringRes int str) {
        //显示错误如果有占位布局用占位布局
        if(mPlaceHolderView!=null){
            mPlaceHolderView.triggerError(str);
        }else{
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        //  显示一个Loading
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        //View中赋值
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter!=null){
            mPresenter.destroy();
        }
    }
}
