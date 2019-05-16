package fengyun.android.com.factory.presenter.friend;

import net.fengyun.italker.factory.presenter.BaseContract;

import java.util.List;

import fengyun.android.com.factory.model.card.FriendCircleCard;

/**
 * 发布朋友圈
 */

public interface ReleaseFriendCircleContract {
    interface Presenter extends BaseContract.Presenter{
        void release(String content,List<String> imgs);
    }

    //用户界面
    interface View extends BaseContract.View<Presenter>{
        void onReleaseDone(FriendCircleCard friendCircleCard);
    }
}
