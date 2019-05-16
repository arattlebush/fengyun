package fengyun.android.com.factory.presenter.group;

import net.fengyun.italker.factory.presenter.BaseRecyclerPresenter;

import java.util.List;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.data.helper.GroupHelper;
import fengyun.android.com.factory.model.db.view.MemberUserModel;

/**
 * 群成员的逻辑
 *
 * @author fengyun
 */

public class GroupMembersPresenter extends BaseRecyclerPresenter<MemberUserModel, GroupMembersContract.View>
        implements GroupMembersContract.Presenter {



    public GroupMembersPresenter(GroupMembersContract.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        //显示loading
        start();
        //加载数据
        Factory.runOnAsync(loader);
    }

    /**
     * 异步获取数据，并刷新数据
     */
    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            GroupMembersContract.View view = getView();
            if(view==null)
                return;
            String groupId = view.getGroupId();
            //传递数量为-1，代表查询所有
            List<MemberUserModel> models = GroupHelper.getMemberUsers(groupId,-1);
            //进行界面刷新，这里会跳转到UI线程刷新
            refreshData(models);
        }
    };
}
