package fengyun.android.com.factory.presenter.friend;

import net.fengyun.italker.factory.presenter.BaseContract;

/**
 * @author fengyun
 * 点赞评论契约
 */

public interface FabulousContract {

    interface Presenter extends BaseContract.Presenter{
        //点赞请求
        void fabulous(String friendCircleId);
    }

    interface FabulousView extends BaseContract.View<Presenter>{
        //点赞成功
        void onFabulousDone();
    }
}
