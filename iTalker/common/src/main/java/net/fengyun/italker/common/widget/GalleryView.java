package net.fengyun.italker.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.fengyun.italker.common.R;
import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author fengyun
 *         画廊
 */
public class GalleryView extends RecyclerView {

    private Adapter mAdapter = new Adapter();
    private static final int LOADER_ID = 0x0100;
    private static final int MAX_IMAGE_COUNT = 3;//最大的图片选中数量
    private static final int MIN_IMAGE_FILE_SIZE = 10 * 1024;//最小的图片长度
    private LoaderCallback loaderCallback = new LoaderCallback();
    private SelectedChangeListener mListener;
    //为什么用LinkedList Arraylist添加删除 耗性能 遍历快
    //LinkedList 遍历慢 不耗性能 这里不怎么遍历所以用LinkedList
    private List<Image> mSelectedImages = new LinkedList<>();

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                //cell点击操作，如果说我们点击是允许的那么更新对应的cell的状态
                //然后更新界面：同理 如果说不能允许点击（已经达到了最大的选中数量）那么久不刷新界面
                if (onItemSelectClick(image)) {
                    holder.updateData(image);
                }
            }
        });
    }

    /**
     * 初始化方法
     *
     * @param loaderManager Loader管理器
     * @return 返回一个loaderID 可用于销毁
     */
    public int setup(LoaderManager loaderManager, SelectedChangeListener listener) {
        this.mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, loaderCallback);
        return LOADER_ID;
    }

    /**
     * cell 点击的具体逻辑
     *
     * @param image image
     * @return True 代表我进行了数据更改 你需要刷新 反之 不刷新
     */
    private boolean onItemSelectClick(Image image) {
        //是否需要进行刷新
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            //如果之前在那么现在就移除
            mSelectedImages.remove(image);
            image.isSelect = false;
            //状态已经改变则需要更新
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= MAX_IMAGE_COUNT) {
                //进行一个Toast提示
                //得到提示文字
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                //格式化填充
                str = String.format(str, MAX_IMAGE_COUNT);
                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            } else {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }
        //如果数据有改变，那么我们需要通知外面的监听者我们的数据选中改变了
        if (notifyRefresh)
            notifySelectChanged();
        return true;
    }

    /**
     * 得到选中的图片的全部地址
     *
     * @return 返回一个数组
     */
    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image image : mSelectedImages) {
            paths[index++] = image.path;
        }
        return paths;
    }

    /**
     * 可以进行清空选中的图片
     */
    public void clear() {
        for (Image image : mSelectedImages) {
            //一定要重置状态再刷新 不然界面不会改变
            image.isSelect = false;
        }
        mSelectedImages.clear();
        //刷新数据
        mAdapter.notifyDataSetChanged();
        //通知选中数量改变
        notifySelectChanged();
    }

    /**
     * 通知选中状态改变
     */
    private void notifySelectChanged() {
        SelectedChangeListener listener = mListener;
        if (listener != null) {
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    /**
     * 通知Adapter数据更改的方法
     *
     * @param images 新的数据
     */
    private void updateSource(List<Image> images) {
        mAdapter.replace(images);
    }

    /**
     * 用于实际的数据加载的loader callback
     */
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,//id
                MediaStore.Images.Media.DATA,//图片路径
                MediaStore.Images.Media.DATE_ADDED//图片创建时间
        };

        @Override  //当Loader创建
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //如果是我们的id就进行初始化
            if (id == LOADER_ID) {
                return new CursorLoader(getContext()
                        , MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        , IMAGE_PROJECTION
                        , null
                        , null
                        , IMAGE_PROJECTION[2] + " DESC");//时间倒序查询
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //当Loader加载完成时
            List<net.fengyun.italker.common.widget.GalleryView.Image> images = new ArrayList<>();
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    //移动游标到开始
                    data.moveToFirst();
                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        //循环读取直到没有下一条数据
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        long date = data.getLong(indexDate);
                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            //如果没有图片，或者图片太小太小，则跳过
                            continue;
                        }
                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.date = date;
                        images.add(image);
                    } while (data.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            //当Loader销毁或者重置 进行界面清空
            updateSource(null);
        }
    }

    /**
     * 内部的数据结构
     */
    private static class Image {

        int id;//数据ID
        String path;//图片路径
        long date;//图片的创建时间
        boolean isSelect;//是否选中

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return path != null ? path.equals(image.path) : image.path == null;

        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleryView.ViewHolder(root);
        }

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.celll_galley;
        }

    }

    /**
     * cell对应的holder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {

        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            mPic = (ImageView) itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = (CheckBox) itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path)//加载路径
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存直接用原图加载
                    .centerCrop()//居中剪切
                    .placeholder(R.color.grey_200)
                    .into(mPic);
            mShade.setVisibility(image.isSelect ? VISIBLE : INVISIBLE);
            mSelected.setChecked(image.isSelect);
            mSelected.setVisibility(VISIBLE);
        }
    }

    /**
     * 对外的监听器
     */
    public interface SelectedChangeListener {
        void onSelectedCountChanged(int count);
    }

}
