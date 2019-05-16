package fengyun.android.com.factory.presenter.group;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.presenter.BaseRecyclerPresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.data.helper.GroupHelper;
import fengyun.android.com.factory.data.helper.UserHelper;
import fengyun.android.com.factory.model.api.group.GroupCreateModel;
import fengyun.android.com.factory.model.card.GroupCard;
import fengyun.android.com.factory.model.db.view.UserSampleModel;
import fengyun.android.com.factory.net.UploadHelper;

/**
 * 群创建的逻辑和改变群加入的状态
 *
 * @author fengyun
 */

public class GroupCreatePresenter extends BaseRecyclerPresenter<GroupCreateContract.ViewModel, GroupCreateContract.View>
        implements GroupCreateContract.Presenter, DataSource.Callback<GroupCard>{

    private Set<String> users = new HashSet<>();

    public GroupCreatePresenter(GroupCreateContract.View view) {
        super(view);
    }


    @Override
    public void start() {
        super.start();
        //加载数据
        Factory.runOnAsync(loader);
    }

    @Override
    public void create(final String name, final String desc, final String picture) {

        GroupCreateContract.View view = getView();
        view.showLoading();

        //判断参数
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc)
                || TextUtils.isEmpty(picture) || users.size() == 0) {
            view.showError(net.fengyun.italker.common.R.string.label_group_create_invalid);
            return;
        }
        //上传图片
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                String url = uploadPicture(picture);
                if(TextUtils.isEmpty(url))
                    return;
                GroupCreateModel model = new GroupCreateModel(name,desc,url,users);
                GroupHelper.create(model,GroupCreatePresenter.this);
            }
        });

    }

    @Override
    public void changeSelect(GroupCreateContract.ViewModel model, boolean isSelected) {
        if (isSelected)
            users.add(model.author.getId());
        else
            users.remove(model.author.getId());
    }

    //同步上传
    private String uploadPicture(String path) {
        String url = UploadHelper.uploadPortrait(path);
        if (TextUtils.isEmpty(url)) {
            //切换到UI 线程，提示信息
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    GroupCreateContract.View view = getView();
                    if (view != null) {
                        view.showError(net.fengyun.italker.common.R.string.data_upload_error);
                    }
                }
            });
        }
        return url;
    }

    /**
     * 异步获取数据，并刷新数据
     */
    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            List<UserSampleModel> sampleModels = UserHelper.getSampleContact();
            List<GroupCreateContract.ViewModel> models = new ArrayList<>();
            for (UserSampleModel sampleModel : sampleModels) {
                GroupCreateContract.ViewModel viewModel = new GroupCreateContract.ViewModel();
                viewModel.author = sampleModel;
                models.add(viewModel);
            }
            //界面刷新
            refreshData(models);
        }
    };

    @Override
    public void onDataLoaded(GroupCard user) {
        //成功了
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = getView();
                if (view != null) {
                    view.onCreateSucceed();
                }
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = getView();
                if (view != null) {
                    view.showError(strRes);
                }
            }
        });
    }
}
