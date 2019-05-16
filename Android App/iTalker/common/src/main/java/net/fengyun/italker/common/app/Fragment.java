package net.fengyun.italker.common.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.fengyun.italker.common.widget.convention.PlaceHolderView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author fengyun
 * fragment的基类
 */
public abstract class Fragment extends android.support.v4.app.Fragment {

    protected PlaceHolderView mPlaceHolderView;
    protected View mRoot;
    protected Unbinder mRootUnbinder;
    //标注是否第一次初始化数据
    protected boolean mIsFirstInitData = true;

    //当activity添加fragment的时候
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //初始化参数
        initArgs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            int layId = getContentLayoutId();
            View root = inflater.inflate(layId, container, false);
            initWeight(root);
            mRoot = root;
        } else {
            if (mRoot.getParent() != null) {
                //把当前root从其父控件移除
                ((ViewGroup) mRoot.getParent()).removeView(mRoot);
            }
        }

        return mRoot;
    }

    /**
     * 初始化相关参数
     */
    protected void initArgs(Bundle bundle){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //当view创建完成之后，初始化数据
        if(mIsFirstInitData){
            //触发一次后就不会触发
            mIsFirstInitData = false;
            onFirstInit();
        }
        initData();
    }

    /**
     * 得到当前界面的资源文件id
     *
     * @return 资源文件id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWeight(View root) {
        mRootUnbinder =   ButterKnife.bind(this,root);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 当首次初始化数据
     */
    protected void onFirstInit() {

    }
    /**
     * 返回按键触发时调用
     * @return 返回 True 是我处理返回逻辑 Activity 不用自己 Finish
     * 返回 False 代表我没有处理逻辑 Activity 自己走自己的逻辑
     */
    public boolean onBackPressed(){
        return false;
    }

    /**
     * 设置占位布局
     * @param mPlaceHolderView PlaceHolderView
     */
    public void setPlaceHolderView(PlaceHolderView mPlaceHolderView) {

        this.mPlaceHolderView = mPlaceHolderView;
    }
}
