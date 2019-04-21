package fengyun.android.com.factory.presenter.user;

import net.fengyun.italker.factory.presenter.BaseContract;

/**
 * @author fengyun
 * 修改用户信息契约
 */

public interface UpdateInfoContract {

    interface Presenter extends BaseContract.Presenter{
        void update(String phoneFilePath,String desc,boolean isMan);
    }

    interface View extends BaseContract.View<Presenter>{
        //回调成功
        void updateSucceed();
    }
}
