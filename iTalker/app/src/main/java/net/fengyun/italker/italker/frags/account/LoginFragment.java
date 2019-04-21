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
import fengyun.android.com.factory.presenter.account.LoginContract;
import fengyun.android.com.factory.presenter.account.LoginPresenter;

/**
 * @author fengyun
 * 登录的fragment
 */
public class LoginFragment extends PresenterFragment<LoginContract.Presenter>
 implements LoginContract.View{


    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_password)
    EditText mPassword;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    private AccountTrigger mAccountTrigger;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_login;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //拿到我们的activity的引用
        mAccountTrigger = (AccountTrigger) context;

    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter(this);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();
        mPresenter.login(phone,password);
    }

    @OnClick(R.id.txt_go_register)
    void onShowRegisterClick(){
        //让AccountActivity进行界面切换
        mAccountTrigger.triggerView();
    }

    @Override
    public void showError(@StringRes int str) {
        super.showError(str);
        mLoading.stop();
        mPhone.setEnabled(true);
        mPassword.setEnabled(true);
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mLoading.start();
        mPhone.setEnabled(false);
        mPassword.setEnabled(false);
        mSubmit.setEnabled(false);
    }

    @Override
    public void loginSuccess() {
        MainActivity.show(getContext());
        getActivity().finish();
    }
}
