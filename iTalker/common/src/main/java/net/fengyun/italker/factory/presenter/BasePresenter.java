package net.fengyun.italker.factory.presenter;

/**
 * @author fengyun
 * 底层的presenter封装
 */
public class BasePresenter<T extends BaseContract.View>
        implements BaseContract.Presenter {

    private T mView;

    public BasePresenter(T view) {
        setView(view);
    }

    /**
     * 设置一个view 可以复写
     * @param view
     */
    @SuppressWarnings("unchecked")
    protected void setView(T view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }

    /**
     * 不可以复写
     * 给子类使用的获取view的操作
     * @return View
     */
    protected final T getView() {
        return mView;
    }

    @Override
    public void start() {
        //开始的时候进行showLoading
        T view = mView;
        if (view != null) {
            view.showLoading();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void destroy() {
        T view = mView;
        mView = null;
        if (view != null) {
            //把Presenter设置为NULL
            view.setPresenter(null);
        }
    }
}
