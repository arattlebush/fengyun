package net.fengyun.italker.italker.frags.media;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import net.fengyun.italker.common.tools.UiTool;
import net.fengyun.italker.common.widget.GalleryView;
import net.fengyun.italker.italker.R;

/**
 * @author fengyun
 * 图片选择fragment
 * BottomSheetDialogFragment是一个弹出的fragment 类似弹框
 */
public class GalleryFragment extends BottomSheetDialogFragment
implements GalleryView.SelectedChangeListener{

    private GalleryView mGallery;
    private OnSelectedListener mListener;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //返回一个我们复写的
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        //获取我们的GalleryView
        mGallery = (GalleryView) view.findViewById(R.id.galleryView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(),this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
        //如果选中了一张图片
        if(count>0){
            //隐藏自己
            dismiss();
            if(mListener!=null){
                //得到选中的所有图片的路径
                String[] paths = mGallery.getSelectedPath();
                mListener.onSelectedImage(paths[0]);
                //取消和唤起者的引用，加快内存回收
                mListener = null;
            }
        }
    }


    /**
     * 设置事件监听并返回自己
     * @param mListener 监听器
     * @return 自己
     */
    public GalleryFragment setListener(OnSelectedListener mListener) {
        this.mListener = mListener;
        return this;
    }

    /**
     * 选中图片的监听器
     */
    public interface OnSelectedListener{
        void onSelectedImage(String path);
    }

    /**
     * 为了解决顶部状态栏变黑创建的
     */
    public static class TransStatusBottomSheetDialog extends BottomSheetDialog{

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window = getWindow();
            if(window==null){
                return;
            }
            //得到屏幕的高度
            int screenHeight = UiTool.getScreenHeight(getOwnerActivity());
            //得到状态的高度
            int statusHeight = UiTool.getStatusBarHeight(getOwnerActivity());
            //计算得到dialog的高度
            int dialogHeight = screenHeight - statusHeight;
            //设置window的高度
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    dialogHeight <= 0? ViewGroup.LayoutParams.MATCH_PARENT:dialogHeight);
        }
    }

}
