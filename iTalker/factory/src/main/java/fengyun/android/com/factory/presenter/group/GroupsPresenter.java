package fengyun.android.com.factory.presenter.group;

import android.support.v7.util.DiffUtil;

import java.util.List;

import fengyun.android.com.factory.data.group.GroupsDataSource;
import fengyun.android.com.factory.data.group.GroupsDispatcher;
import fengyun.android.com.factory.data.helper.GroupHelper;
import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.presenter.contact.BaseSourcePresenter;
import fengyun.android.com.factory.utils.DiffUtilDataCallback;

/**
 * @author fengyun
 *         我的群列表逻辑
 */

public class GroupsPresenter extends BaseSourcePresenter<Group, Group, GroupsDataSource, GroupsContract.View>
        implements GroupsContract.Presenter {

    public GroupsPresenter(GroupsContract.View view) {
        super(new GroupsDispatcher(), view);
    }

    @Override
    public void start() {
        super.start();
        //加载网络数据,以后可以优化到下拉刷新中
        //只有用户下拉进行网络刷新
        GroupHelper.refreshGroups();
    }


    @Override
    public void onDataLoaded(List<Group> groups) {
        final GroupsContract.View view = getView();
        if (view == null) {
            return;
        }
        List<Group> old = view.getRecyclerAdapter().getItems();
        DiffUtilDataCallback<Group> callback = new DiffUtilDataCallback<>(old,groups);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //界面刷新
        refreshData(result,groups);
    }
}
