package net.fengyun.italker.factory.data;

import android.support.annotation.StringRes;

/**
 * 数据源接口的定义
 * @author fengyun
 */
public interface DataSource {
    /**
     * 同时包括了成功和失败的回调接口
     * @param <T> 泛型 任意类型
     *  双继承
     */
    interface Callback<T> extends SuccessedCallback<T>,Failedback{

    }
    /**
     * 只关注成功的接口
     */
    interface SuccessedCallback<T>{
        //数据加载失败,网络请求成功
        void onDataLoaded(T user);
    }

    /**
     * 只关注失败的接口
     */
    interface Failedback{
        //数据加载失败,网络请求失败
        void onDataNotAvailable(@StringRes int strRes);
    }


    /**
     *销毁操作
     */
    void dispose();


}
