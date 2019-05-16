package net.fengyun.italker.italker.help;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

import net.fengyun.italker.common.app.Fragment;

/**
 *@author fengyun
 * 解决对fragment的调度和重用问题
 */
public class NavHelper<T> {

    //所有的tab集合
    private final SparseArray<Tab<T>> tabs = new SparseArray();
    //用于初始化的参数
    private final Context context;
    private final int containerId; //容器id
    private final FragmentManager fragmentManager; //fragment管理器
    private final OnTabChangedListener listener;
    //当前的一个选中tab
    private Tab<T> currentTab;

    public NavHelper(Context context,
                     int containerId,
                     FragmentManager fragmentManager,
                     OnTabChangedListener listener) {
        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    /**
     * 执行点击菜单的操作
     * 达到最优的Fragment切换
     * @param menuId
     * @return
     */
    public boolean performClickMenu(int menuId){
        //集合中寻找点击的菜单对应的Tab
        //如果有进行处理
        Tab<T> tab = tabs.get(menuId);
        if (tab != null) {
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 进行真实的Tab选择操作
     */
    private void doSelect(Tab<T> tab){
        Tab<T> oldTab=null;
        if (currentTab != null) {
            oldTab = currentTab;
            if (oldTab == tab) {
                //如果说当前的Tab就是点击的Tab
                //那么我们不做处理
                notifyTabReselect(tab);
                return;
            }
        }
        //赋值并调用切换方法
        currentTab = tab;
        doTabChanged(currentTab,oldTab);
    }

    /**
     * 进行fragment的真实的调度
     * @param newTab 新的
     * @param oldTab 旧的
     */
    private void doTabChanged(Tab<T> newTab,Tab<T> oldTab){
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (oldTab != null) {
            if(oldTab.fragment!=null){
                //从界面移除但是还在fragment的缓存中
                ft.detach(oldTab.fragment);
            }
        }
        if (newTab != null) {
            //如果没有fragment就新建 有的话重新加载
            if(newTab.fragment==null){
                //首次新建
                android.support.v4.app.Fragment fragment = Fragment.instantiate
                        (context,newTab.clx.getName(),null);
                //缓存起来
                newTab.fragment = fragment;
                //提交到fragmentManager
                ft.add(containerId,fragment,newTab.clx.getName());
            }else{
                //从fragmentManager 的缓存控件中重新加载到界面中
                ft.attach(newTab.fragment);
            }
        }
        //提交事务
        ft.commit();
        //通知回调
        notifyTabSelect(newTab,oldTab);
    }

    /**
     * 回调我们的监听器
     * @param newTab 新的
     * @param oldTab 旧的
     */
    private void notifyTabSelect(Tab<T> newTab,Tab<T> oldTab){
        if (listener != null) {
            listener.onTabChanged(newTab,oldTab);
        }
    }

    /**
     * 二次点击 tab
     * @param tab
     */
    private void notifyTabReselect(Tab<T> tab){
        //TODO 二次点击tab所做的操作
    }

    /**
     * 添加tab
     * @param menuId Tab对应的菜单id
     * @param tab Tab
     */
    public NavHelper<T> add(int menuId,Tab<T> tab){
        tabs.put(menuId,tab);
        return this;
    }

    /**
     * 获取当前的tab
     * @return
     */
    public Tab<T> getCurrentTab(){
        return currentTab;
    }

    public static class Tab<T>{
        public Tab(Class<?> clx,T extra) {
            this.extra = extra;
            this.clx = clx;
        }

        //fragment对应的class信息
        public Class<?> clx;
        //额外的字段，用户自己设定需要什么东西
        public T extra;

        //内部缓存的对应fragment
        //package权限 外部无法使用
        android.support.v4.app.Fragment fragment;
    }

    /**
     * 处理事件回调接口
     * @param <T> 泛型的额外参数
     */
    public interface OnTabChangedListener<T>{
        //传递一个新的tab和旧的tab
        void onTabChanged(Tab<T> newTab,Tab<T> oldTab);
    }

}
