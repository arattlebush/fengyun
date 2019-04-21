package fengyun.android.com.factory.presenter.group;

import net.fengyun.italker.factory.presenter.BaseContract;

import fengyun.android.com.factory.model.db.Group;

/**
 * @author fengyun
 * 我的群列表契约
 */

public interface GroupsContract {

    //什么读不需要额外定义，开始就是调用start即可
    interface Presenter extends BaseContract.Presenter{

    }

    //都在基类完成了
    interface View extends BaseContract.RecyclerView<Presenter,Group> {

    }

}
