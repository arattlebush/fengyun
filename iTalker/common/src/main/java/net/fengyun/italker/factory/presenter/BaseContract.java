package net.fengyun.italker.factory.presenter;

import android.support.annotation.StringRes;

import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;

import java.util.List;

/**
 * MVP模式中的公共契约
 * @author fengyun
 */
public interface BaseContract {
    //基本的界面职责
    interface View<T extends Presenter> {
        //公共的 返回一个错误信息
        void showError(@StringRes int str);

        //公共的 显示进度条
        void showLoading();

        //支持设置一个presenter
        void setPresenter(T presenter);
    }

    interface Presenter {
        //公用的开始出发
        void start();

        //公用的销毁方法
        void destroy();
    }
    //基本的一个列表的View的职责 双泛型 一个数据一个Presenter
    interface RecyclerView<T extends Presenter,ViewMode> extends View<T>{
        //界面端只能刷新整个数据集合，不能精确到每一条数据刷新
//        void onDone(List<User> userLists);
        //拿到一个适配器，然后自己自主的进行更新
        RecyclerAdapter<ViewMode> getRecyclerAdapter();

        //当适配器数据更改的时候触发
        void onAdapterDataChanged();
    }

    interface ChartView<T extends Presenter ,ViewMode> extends View<T>{

        List<ViewMode> getList();

        //当适配器数据更改的时候触发
        void onAdapterDataChanged();
    }

}
