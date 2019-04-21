package fengyun.android.com.factory.presenter.friend;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;

import net.fengyun.italker.common.app.Application;
import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.presenter.BasePresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import fengyun.android.com.factory.R;
import fengyun.android.com.factory.data.helper.FriendCircleHelper;
import fengyun.android.com.factory.model.api.friend.CommentModel;

/**
 * @author fengyun
 *         评论的逻辑
 */

public class CommentPresenter extends BasePresenter<CommentContract.CommentView>
        implements CommentContract.Presenter, DataSource.Callback {
    public CommentPresenter(CommentContract.CommentView view) {
        super(view);
    }

    @Override
   public void comment(String friendCircleId,String content) {
        if(TextUtils.isEmpty(friendCircleId)){
        } else if(TextUtils.isEmpty(friendCircleId)){
            Application.showToast(R.string.hint_comment_info);
        }else{
            //评论
            CommentModel commentModel = new CommentModel(friendCircleId,content);
            Log.e("commentModel", commentModel.toString());
            FriendCircleHelper.comment(commentModel,this);
        }
    }

    @Override
    public void onDataLoaded(Object user) {
        final CommentContract.CommentView view = getView();
        if (view == null)
            return;
        //强制执行在主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showLoading();
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final CommentContract.CommentView view = getView();
        if (view == null)
            return;
        //强制执行在主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
    }
}
