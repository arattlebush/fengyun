package fengyun.android.com.factory.presenter.search;

import android.support.annotation.StringRes;

import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.presenter.BasePresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import fengyun.android.com.factory.data.helper.UserHelper;
import fengyun.android.com.factory.model.card.UserCard;
import retrofit2.Call;

/**
 * @author fengyun
 * * 搜索人的Presenter
 */

public class SearchUserPresenter extends BasePresenter<SearchContract.UserView> implements SearchContract.Presenter,DataSource.Callback<List<UserCard>>{
    private Call searchCall;
    public SearchUserPresenter(SearchContract.UserView view) {
        super(view);
    }


    @Override
    public void search(String content) {
        start();
        Call call = searchCall;
        if (call != null && !call.isCanceled()) {
            //如果有上一次的请求，并且没有取消，
            //则调用取消请求操作
            call.cancel();
        }
        searchCall = UserHelper.search(content,this);
    }

    @Override
    public void onDataLoaded(final List<UserCard> userCards) {
       final SearchContract.UserView  view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onSearchDone(userCards);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final SearchContract.UserView  view = getView();
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
