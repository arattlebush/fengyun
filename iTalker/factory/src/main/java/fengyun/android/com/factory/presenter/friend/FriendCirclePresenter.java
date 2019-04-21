package fengyun.android.com.factory.presenter.friend;

import android.support.annotation.StringRes;

import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.presenter.BasePresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import fengyun.android.com.factory.data.helper.FriendCircleHelper;
import fengyun.android.com.factory.model.card.FriendCircleCard;
import retrofit2.Call;

/**
 * 朋友圈的逻辑处理
 */

public class FriendCirclePresenter extends BasePresenter<FriendCircleContract.View>
        implements FriendCircleContract.Presenter
        ,DataSource.Callback<List<FriendCircleCard>> {
    private Call mCall;

    public FriendCirclePresenter(FriendCircleContract.View view) {
        super(view);
    }

    @Override
    public void friendCircle() {
        start();
        Call call = mCall;
        if (call != null && !call.isCanceled()) {
            //如果有上一次的请求，并且没有取消，
            //则调用取消请求操作
            call.cancel();
        }
        mCall = FriendCircleHelper.friendCircle(this);
    }

    @Override
    public void onDataLoaded(final List<FriendCircleCard> friendCircleCards) {
        final FriendCircleContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onFriendCircleDone(friendCircleCards);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final FriendCircleContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(strRes);
                }
            });
        }
    }
}
