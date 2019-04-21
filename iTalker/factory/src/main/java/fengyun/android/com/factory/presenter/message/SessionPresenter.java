package fengyun.android.com.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import java.util.List;

import fengyun.android.com.factory.model.db.Session;
import fengyun.android.com.factory.presenter.contact.BaseSourcePresenter;
import fengyun.android.com.factory.utils.DiffUtilDataCallback;

/**
 * 会话的逻辑
 *
 * @author fengyun
 */

public class SessionPresenter extends BaseSourcePresenter<Session, Session,
        SessionDataSource, SessionContract.View> implements SessionContract.Presenter {


    public SessionPresenter(SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view = getView();
        if (view == null) {
            return;
        }
        List<Session> old = view.getRecyclerAdapter().getItems();
        //
        DiffUtilDataCallback<Session> callback = new DiffUtilDataCallback<>(old, sessions);
        //计算差异
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //刷新数据
        refreshData(result, sessions);
    }
}
