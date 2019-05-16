package fengyun.android.com.factory.presenter.search;

import android.support.annotation.StringRes;

import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.presenter.BasePresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import fengyun.android.com.factory.data.helper.GroupHelper;
import fengyun.android.com.factory.model.card.GroupCard;
import retrofit2.Call;

/**
 * @author fengyun
 * 搜索群的Presenter
 */

public class SearchGroupPresenter extends BasePresenter<SearchContract.GroupView>
        implements SearchContract.Presenter
        ,DataSource.Callback<List<GroupCard>>{
    private Call searchCall;

    public SearchGroupPresenter(SearchContract.GroupView view) {
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
        searchCall = GroupHelper.search(content,this);
    }
    @Override
    public void onDataLoaded(final List<GroupCard> userCards) {
        final SearchContract.GroupView view = getView();
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
        final SearchContract.GroupView  view = getView();
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
