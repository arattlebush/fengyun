package net.fengyun.italker.common.app;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;

import net.fengyun.italker.common.R;
import net.fengyun.italker.factory.presenter.BaseContract;

/**
 * @author fengyun
 *         专门显示toolbar的activity
 */

public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter>
        extends ToolbarActivity implements BaseContract.View<Presenter>{

    protected Presenter mPresenter;
    protected ProgressDialog mLoadingDialog;

    @Override
    protected void initBefore() {
        super.initBefore();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //进行销毁
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    //初始化Presenter
    protected abstract Presenter initPresenter();

    @Override
    public void showError(@StringRes int str) {
        //不管你怎么样，我先隐藏自己
        hideDialogLoading();
        //显示错误如果有占位布局用占位布局
        if(mPlaceHolderView!=null){
            mPlaceHolderView.triggerError(str);
        }else{
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        }else{
            ProgressDialog dialog = mLoadingDialog;
            if (dialog == null) {
                dialog = new ProgressDialog(this,R.style.AppTheme_Dialog_Alert_Light);
                //不可触摸取消
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(true);
                //强制取消关闭界面
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                mLoadingDialog = dialog;
            }
            dialog.setMessage(getText(R.string.prompt_loading));
            dialog.show();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        //View中赋值
        mPresenter = presenter;
    }

    protected void hideDialogLoading(){
        ProgressDialog dialog = mLoadingDialog;
        if (dialog != null) {
            mLoadingDialog=null;
            dialog.dismiss();
        }
    }

    protected void hideLoading(){
        //不管你怎么样，我先隐藏自己
        hideDialogLoading();
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

}
