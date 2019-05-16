package fengyun.android.com.factory.presenter.contact;

import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.factory.data.DbDataSource;
import net.fengyun.italker.factory.presenter.BaseContract;
import net.fengyun.italker.factory.presenter.BaseRecyclerPresenter;

import java.util.List;

/**
 * 基础的仓库源的Presenter的定义
 * @author fengyun
 */

public abstract class BaseSourcePresenter<Data,ViewModel,Source extends DbDataSource<Data>,
        View extends BaseContract.RecyclerView> extends BaseRecyclerPresenter<ViewModel,View>
    implements DataSource.SuccessedCallback<List<Data>>{

    protected Source mSource;

    public BaseSourcePresenter(Source source,View view) {
        super(view);
        this.mSource = source;
    }

    @Override
    public void start() {
        super.start();
        if (mSource != null) {
            mSource.load(this);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mSource.dispose();
        mSource=null;
    }
}
