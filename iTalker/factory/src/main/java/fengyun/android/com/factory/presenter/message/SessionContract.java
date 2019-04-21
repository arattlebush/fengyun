package fengyun.android.com.factory.presenter.message;

import net.fengyun.italker.factory.presenter.BaseContract;

import fengyun.android.com.factory.model.db.Session;

/**
 * 最近会费
 * @author fengyun
 */

public interface SessionContract {


    interface Presenter extends BaseContract.Presenter{

    }
    //度在基类完成
    interface View extends BaseContract.RecyclerView<Presenter,Session>{

    }
}
