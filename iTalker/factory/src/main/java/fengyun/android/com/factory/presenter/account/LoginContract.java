package fengyun.android.com.factory.presenter.account;

import net.fengyun.italker.factory.presenter.BaseContract;

/**
 * @author fengyun
 *         登录
 */
public interface LoginContract {

    interface View extends BaseContract.View<Presenter>{
        //登录成功
        void loginSuccess();
    }

    interface Presenter  extends BaseContract.Presenter{
        //发起一个登录请求
        void login(String phone,  String password);
    }
}
