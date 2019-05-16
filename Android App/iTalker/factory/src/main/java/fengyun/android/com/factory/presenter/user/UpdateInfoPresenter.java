package fengyun.android.com.factory.presenter.user;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.presenter.BasePresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.R;
import fengyun.android.com.factory.data.helper.UserHelper;
import fengyun.android.com.factory.model.api.user.UserUpdateModel;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.net.UploadHelper;
import fengyun.android.com.factory.presenter.account.LoginContract;

/**
 * @author fengyun
 */

public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.View> implements UpdateInfoContract.Presenter,DataSource.Callback{

    public UpdateInfoPresenter(UpdateInfoContract.View view) {
        super(view);
    }

    @Override
    public void update(final String phoneFilePath, final String desc, final boolean isMan) {
        start();
        //判断数据是否合法
        final UpdateInfoContract.View view = getView();
        if (TextUtils.isEmpty(phoneFilePath) || TextUtils.isEmpty(desc)) {
            view.showError(R.string.data_account_update_invalid_parameter);
        } else {
            //上传头像
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    String url = UploadHelper.uploadPortrait(phoneFilePath);
                    if (TextUtils.isEmpty(url)) {
                        view.showError(R.string.data_upload_error);
                    }else{
                        //构建model
                        UserUpdateModel model = new UserUpdateModel("",url,desc,isMan? User.SEX_MAN:User.SEX_WOMAN);
                        UserHelper.update(model,UpdateInfoPresenter.this);
                    }
                }
            });
        }
    }

    @Override
    public void onDataLoaded(Object user) {
        final UpdateInfoContract.View view = getView();
        if (view == null)
            return;
        //强制执行在主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.updateSucceed();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final UpdateInfoContract.View view = getView();
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
