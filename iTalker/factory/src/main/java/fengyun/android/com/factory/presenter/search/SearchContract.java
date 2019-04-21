package fengyun.android.com.factory.presenter.search;

import net.fengyun.italker.factory.presenter.BaseContract;

import java.util.List;

import fengyun.android.com.factory.model.card.GroupCard;
import fengyun.android.com.factory.model.card.UserCard;

/**
 * @author fengyun
 * 搜索的契约
 */

public interface SearchContract {

    interface Presenter extends BaseContract.Presenter{
        void search(String content);
    }

    //搜索人的界面
    interface UserView extends BaseContract.View<Presenter>{
        void onSearchDone(List<UserCard> userCards);

    }
    //搜索群的界面
    interface GroupView extends BaseContract.View<Presenter>{
        void onSearchDone(List<GroupCard> groupCards);
    }

}
