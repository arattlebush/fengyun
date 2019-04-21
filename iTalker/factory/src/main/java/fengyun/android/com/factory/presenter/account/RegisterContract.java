package fengyun.android.com.factory.presenter.account;

import net.fengyun.italker.factory.presenter.BaseContract;

/**
 * @author fengyun
 *         注册
 */
public interface RegisterContract {


    interface View extends BaseContract.View<Presenter>{
        //注册成功
        void registerSuccess();
    }

    interface Presenter extends BaseContract.Presenter{
        //发起一个注册
        void register(String phone, String name, String password);

        //检测手机号是否合法
        boolean checkMobile(String phone);
    }
}
