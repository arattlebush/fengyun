package fengyun.android.com.factory.presenter.contact;

import net.fengyun.italker.factory.presenter.BaseContract;

import fengyun.android.com.factory.model.card.UserCard;

/**
 * 关注的接口定义
 * @author fengyun
 * 联系人契约
 */

public interface FollowContract {

    interface Presenter extends BaseContract.Presenter {
        //关注一个人
       void follow(String id);
    }

    interface View extends BaseContract.View<Presenter>{
        //关注成功 返回一个用户信息
        void onFollowSucceed(UserCard userCard);
    }

}
