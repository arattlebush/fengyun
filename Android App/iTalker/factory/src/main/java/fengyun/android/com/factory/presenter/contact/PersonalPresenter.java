package fengyun.android.com.factory.presenter.contact;

import net.fengyun.italker.factory.presenter.BasePresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.data.helper.UserHelper;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.persistence.Account;

/**
 * @author fengyun
 * 个人信息的逻辑
 */

public class PersonalPresenter extends BasePresenter<PersonalContract.View> implements PersonalContract.Presenter{

    private String id;
    private User user;


    public PersonalPresenter(PersonalContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        //个人界面用户数据优先从网络拉取
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                PersonalContract.View view = getView();
                if (view != null) {
                    String id = view.getUserId();
                    User user = UserHelper.searchFirstOfNet(id);
                    onLoaded(user);
                }

            }
        });

    }

    /**
     * 进行界面更新
     * @param user
     */
    private void onLoaded(final User user){
        this.user = user;
        //是否就是我自己
        final boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId());
        //是否关注
        final boolean isFollow = isSelf || user.isFollow();
        //已经关注，并且不是自己才能聊天
        final boolean allowSayHello = isFollow && !isSelf;
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                final PersonalContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.onLoadDone(user);
                view.setFollowStatus(isFollow);
                view.allowSayHello(allowSayHello);
            }
        });
    }

    @Override
    public User getUserPersonal() {
        return user;
    }
}
