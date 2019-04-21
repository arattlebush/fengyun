package fengyun.android.com.factory.presenter.contact;

import android.support.annotation.StringRes;

import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.presenter.BasePresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import fengyun.android.com.factory.data.helper.UserHelper;
import fengyun.android.com.factory.model.card.UserCard;

/**
 * 关注的实现逻辑
 * @author fengyun
 */

public class FollowPresenter extends BasePresenter<FollowContract.View> implements FollowContract.Presenter,DataSource.Callback<UserCard>{


    public FollowPresenter(FollowContract.View view) {
        super(view);
    }


    @Override
    public void follow(String id) {
        start();
        UserHelper.follow(id,this);
    }

    @Override
    public void onDataLoaded(final UserCard userCard) {
        final FollowContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onFollowSucceed(userCard);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final FollowContract.View view = getView();
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
