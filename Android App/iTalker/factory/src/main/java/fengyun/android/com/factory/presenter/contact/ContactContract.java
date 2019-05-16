package fengyun.android.com.factory.presenter.contact;

import net.fengyun.italker.factory.presenter.BaseContract;

import fengyun.android.com.factory.model.db.User;

/**
 * @author fengyun
 * 联系人
 */

public interface ContactContract {

    //什么读不需要额外定义，开始就是调用start即可
    interface Presenter extends BaseContract.Presenter{

    }

    //都在基类完成了
    interface View extends BaseContract.RecyclerView<Presenter,User> {

    }

}
