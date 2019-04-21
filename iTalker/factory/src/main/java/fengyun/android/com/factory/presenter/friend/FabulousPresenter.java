package fengyun.android.com.factory.presenter.friend;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.presenter.BasePresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import fengyun.android.com.factory.data.helper.FriendCircleHelper;

/**
 * @author fengyun
 *         评论的逻辑
 */

public class FabulousPresenter extends BasePresenter<FabulousContract.FabulousView>
        implements FabulousContract.Presenter, DataSource.Callback {
    public FabulousPresenter(FabulousContract.FabulousView view) {
        super(view);
    }


    @Override
    public void fabulous(String friendCircleId) {
        if(TextUtils.isEmpty(friendCircleId)){
            return;
        }else{
            //点赞
            FriendCircleHelper.fabulous(friendCircleId,this);
        }
    }


    @Override
    public void onDataLoaded(Object user) {
        final FabulousContract.FabulousView view = getView();
        if (view == null)
            return;
        //强制执行在主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.onFabulousDone();
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final FabulousContract.FabulousView view = getView();
        if (view == null)
            return;
        //强制执行在主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
    }
}
