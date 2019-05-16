package fengyun.android.com.factory.presenter.contact;

import android.support.v7.util.DiffUtil;


import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;
import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.presenter.BaseRecyclerPresenter;

import java.util.List;

import fengyun.android.com.factory.data.helper.UserHelper;
import fengyun.android.com.factory.data.user.ContactDataSource;
import fengyun.android.com.factory.data.user.ContactRepository;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.utils.DiffUtilDataCallback;


/**
 * 联系人的逻辑
 *
 * @author fengyun
 */

public class ContactPresenter extends BaseSourcePresenter<User,User,ContactDataSource,ContactContract.View>
        implements ContactContract.Presenter ,DataSource.SuccessedCallback<List<User>>{

    public ContactPresenter(ContactContract.View view) {
        //初始化数据仓库
        super(new ContactRepository(),view);
    }

    @Override
    public void start() {
        super.start();
        //加载网络数据
        UserHelper.refreshContacts();
    }

    //运行到这里的时候是子线程
    @Override
    public void onDataLoaded(List<User> users) {
        //无论怎么操作，数据变更，最终都会通知到这里来
        final ContactContract.View view =getView();
        if (view==null)
            return;
        RecyclerAdapter<User> adapter =view.getRecyclerAdapter();
        List<User> old = adapter.getItems();
        DiffUtil.Callback callback = new DiffUtilDataCallback<User>(old,users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //调用基类方法进行界面刷新
        refreshData(result,users);
    }
}
