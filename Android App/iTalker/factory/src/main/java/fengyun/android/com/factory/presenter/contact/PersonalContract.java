package fengyun.android.com.factory.presenter.contact;

import net.fengyun.italker.factory.presenter.BaseContract;

import fengyun.android.com.factory.model.db.User;

/**
 * @author fengyun
 */

public interface PersonalContract {

    interface Presenter extends BaseContract.Presenter {
        //获取用户个人信息
        User getUserPersonal();
    }

    interface View extends BaseContract.View<Presenter> {
        String getUserId();

        //加载完成
        void onLoadDone(User user);

        //是否发起聊天
        void allowSayHello(boolean isAllow);

        //设置关注状态
        void setFollowStatus(boolean isFollow);
    }
}
