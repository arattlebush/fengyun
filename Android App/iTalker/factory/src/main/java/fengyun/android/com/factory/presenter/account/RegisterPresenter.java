package fengyun.android.com.factory.presenter.account;

import android.text.TextUtils;

import net.fengyun.italker.common.Common;
import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.presenter.BasePresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

import fengyun.android.com.factory.R;
import fengyun.android.com.factory.data.helper.AccountHelper;
import fengyun.android.com.factory.model.api.account.RegisterModel;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.persistence.Account;

/**
 * @author fengyun
 * 注册的逻辑
 */
public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter, DataSource.Callback<User> {

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String name, String password) {
        //调用开始方法，默认启动了loading
        start();
        //得到view接口
        RegisterContract.View view = getView();

        if (!checkMobile(phone)) {
            //提示手机号不合法
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        } else if (name.length() < 2) {
            //姓名需要大于两位
            view.showError(R.string.data_account_register_invalid_parameter_name);
        } else if (password.length() < 6) {
            //密码需要大于六位
            view.showError(R.string.data_account_register_invalid_parameter_password);
        } else {
            //进行网络请求
            //构造model进行请求调用
            RegisterModel model = new RegisterModel(phone, password, name, Account.getPushId());
            //进行网络请求，并设置回送接口给自己
            AccountHelper.register(model, this);
        }
    }

    /**
     * 检测手机号是否合法
     * @param phone 手机号
     * @return 合法为True
     */
    @Override
    public boolean checkMobile(String phone) {
        return !TextUtils.isEmpty(phone)
                && Pattern.matches(Common.Constance.REGEX_MOBILE, phone);
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final RegisterContract.View view = getView();
        if (view == null)
            return;
        //此时是从网络回送回来的，并不保证处于主线程状态
        //强制执行在主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
        //当网络请求告知注册失败，回送一个失败原因
    }

    @Override
    public void onDataLoaded(User user) {
        //当网络请求成功，注册好了，回送一个用户信息回来
        final RegisterContract.View view = getView();
        if (view == null)
            return;
        //此时是从网络回送回来的，并不保证处于主线程状态
        //强制执行在主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //调用主界面注册失败显示错误
                view.registerSuccess();
            }
        });
    }
}
