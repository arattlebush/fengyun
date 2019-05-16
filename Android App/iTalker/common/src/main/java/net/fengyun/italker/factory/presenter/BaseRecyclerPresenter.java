package net.fengyun.italker.factory.presenter;

import android.support.v7.util.DiffUtil;

import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

/**
 * 基础的recycler的p层
 *
 * @author fengyun
 */

public class BaseRecyclerPresenter<ViewModel, View extends BaseContract.RecyclerView> extends BasePresenter<View> {
    public BaseRecyclerPresenter(View view) {
        super(view);
    }

    /**
     * 刷新一堆新数据到界面中
     * @param dataList 新数据
     */
    protected void refreshData(final List<ViewModel> dataList) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                View view = getView();
                if (view == null) {
                    return;
                }
                //更新数据并刷新界面
                RecyclerAdapter<ViewModel> adapter = view.getRecyclerAdapter();
                adapter.replace(dataList);
                view.onAdapterDataChanged();
            }
        });
    }

    protected void refreshData(final DiffUtil.DiffResult diffResult, final List<ViewModel> dataList) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //这里是主线程执行
                refreshDataOnUiThread(diffResult,dataList);
            }
        });
    }

    /**
     * 刷新界面操作，该操作可以保证执行方法在主线程执行
     * @param diffResult
     * @param dataList
     */
    private void refreshDataOnUiThread(final DiffUtil.DiffResult diffResult, final List<ViewModel> dataList) {
        View view = getView();
        if (view == null) {
            return;
        }
        //改变数据集合并不通知界面刷新
        RecyclerAdapter<ViewModel> adapter = view.getRecyclerAdapter();
        adapter.getItems().clear();
        adapter.getItems().addAll(dataList);
        view.onAdapterDataChanged();
        //进行增量更新
        diffResult.dispatchUpdatesTo(adapter);
    }
}
