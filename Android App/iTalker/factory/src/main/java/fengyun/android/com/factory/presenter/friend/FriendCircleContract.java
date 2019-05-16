package fengyun.android.com.factory.presenter.friend;

import net.fengyun.italker.factory.presenter.BaseContract;

import java.util.List;

import fengyun.android.com.factory.model.card.FriendCircleCard;

/**
 * @author fengyun
 * 获取朋友圈信息
 * 朋友圈契约
 */

public interface FriendCircleContract {
    interface Presenter extends BaseContract.Presenter{
        void friendCircle();
    }

    //用户界面
    interface View extends BaseContract.View<Presenter>{
        void onFriendCircleDone(List<FriendCircleCard> friendCircles);
    }
}
