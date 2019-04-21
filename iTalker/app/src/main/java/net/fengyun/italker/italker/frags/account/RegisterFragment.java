package net.fengyun.italker.italker.frags.account;


import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.EditText;

import net.fengyun.italker.common.app.PresenterFragment;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.activities.MainActivity;
import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;
import fengyun.android.com.factory.presenter.account.RegisterContract;
import fengyun.android.com.factory.presenter.account.RegisterPresenter;

/**
 */
public class RegisterFragment extends PresenterFragment<RegisterContract.Presenter> implements RegisterContract.View {

    private AccountTrigger mAccountTrigger;

    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.edit_password)
    EditText mPassword;

    @BindView(R.id.loading)
    Loading mLoading;
    @BindView(R.id.btn_submit)
    Button mSubmit;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //拿到我们的activity的引用
        mAccountTrigger = (AccountTrigger) context;

    }

    @Override
    protected RegisterContract.Presenter initPresenter() {
        return new RegisterPresenter(this);
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_register;
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String phone = mPhone.getText().toString();
        String name = mName.getText().toString();
        String password = mPassword.getText().toString();
        mPresenter.register(phone,name,password);
    }

    @OnClick(R.id.txt_go_login)
    void onShowLoginClick(){
        //让AccountActivity进行界面切换
        mAccountTrigger.triggerView();
    }

    @Override
    public void showError(@StringRes int str) {
        super.showError(str);
        //当需要显示错误的时候触发，一定是结束
        mLoading.stop();
        //让控件可以输入
        mPhone.setEnabled(true);
        mName.setEnabled(true);
        mPassword.setEnabled(true);
        //提交按钮可以继续点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        //正在进行时 正在注册时 界面不可操作
        mLoading.start();
        //让控件不可以输入
        mPhone.setEnabled(false);
        mName.setEnabled(false);
        mPassword.setEnabled(false);
        //提交按钮不可以继续点击
        mSubmit.setEnabled(false);
    }

    @Override
    public void registerSuccess() {
        //注册成功 这个时候账户已经登录
        //我们需要进行跳转到MainACtivity
        MainActivity.show(getContext());
        //关闭当前界面
        getActivity().finish();
    }

}
