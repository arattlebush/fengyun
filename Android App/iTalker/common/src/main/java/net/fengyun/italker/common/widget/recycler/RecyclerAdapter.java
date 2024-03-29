package net.fengyun.italker.common.widget.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.fengyun.italker.common.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * recyclervide的封装
 *
 * @author fengyun
 */
public abstract class RecyclerAdapter<Data>
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<Data> {

    private final List<Data> mDataList;

    private AdapterListener mListener;

    /**
     * 构造函数模块
     */
    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener listener) {
        this(new ArrayList<Data>(), listener);
    }

    public RecyclerAdapter(List<Data> dataList, AdapterListener listener) {
        this.mDataList = dataList;
        this.mListener = listener;

    }

    /**
     * 创建一个ViewHolder
     *
     * @param parent   RecyclerView
     * @param viewType 界面的类型 约定为是xml布局的id
     * @return ViewHolder
     */
    @Override
    public ViewHolder<Data> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //当xml id为viewType初始化为一个root view
        View root = inflater.inflate(viewType, parent, false);
        ViewHolder<Data> holder = onCreateViewHolder(root, viewType);
        //设置view的tag为viewHolder，进行双向绑定
        root.setTag(R.id.tag_recycler_holder, holder);
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);
        holder.unbinder = ButterKnife.bind(holder, root);
        holder.callback = this;
        return holder;
    }

    protected abstract ViewHolder<Data> onCreateViewHolder(View root, int viewType);

    /**
     * 复写默认的布局类型返回
     *
     * @param position 坐标
     * @return 类型 其实复写后返回的服饰xml文件的id
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    /**
     * 复写默认的布局类型返回
     *
     * @param position 坐标
     * @param data     当前的
     *                 数据
     * @return 类型 其实复写后返回的服饰xml文件的id
     */
    protected abstract int getItemViewType(int position, Data data);

    //绑定一个数据到holder上
    @Override
    public void onBindViewHolder(ViewHolder<Data> holder, int position) {
        Data data = mDataList.get(position);
        //出发viewholder的绑定方法
        holder.bind(data);
    }

    /**
     * 得到当前的集合的数据量
     */
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<Data> getItems() {
        return mDataList;
    }

    /**
     * 插入一条数据并通知插入
     *
     * @param data
     */
    public void add(Data data) {
        mDataList.add(data);
        notifyItemInserted(mDataList.size() - 1);
    }

    /**
     * 插入一堆数据，并通知这段更新
     *
     * @param dataList
     */
    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            int startPos = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeChanged(startPos, dataList.length);
        }
    }

    /**
     * 插入一堆数据，并通知这段更新
     *
     * @param dataList
     */
    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDataList.size();
            Collections.addAll(dataList);
            notifyItemRangeChanged(startPos, dataList.size());
        }
    }

    /**
     * 删除操作
     */
    public void clean() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 替换为一个新的集合
     *
     * @param dataList 一个新的集合
     */
    public void replace(Collection<Data> dataList) {
        mDataList.clear();
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    //更新数据
    @Override
    public void update(Data data, ViewHolder<Data> holder) {
        //得到当前viewholder的坐标
        int pos = holder.getAdapterPosition();
        if (pos >= 0) {
            //进行数据的移除和更新
            mDataList.remove(pos);
            mDataList.add(pos, data);
            //通知这个坐标下的数据有更新
            notifyItemChanged(pos);
        }
    }

    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (mListener != null) {
            //得到当前的坐标
            int pos = viewHolder.getAdapterPosition();
            //设置点击事件
            mListener.onItemClick(viewHolder, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (mListener != null) {
            int pos = viewHolder.getAdapterPosition();
            mListener.onItemLongClick(viewHolder, mDataList.get(pos));
            return true;
        }
        return false;
    }

    //设置适配器的监听
    public void setListener(AdapterListener<Data> listener) {
        this.mListener = listener;
    }

    /**
     * 自定义监听器
     *
     * @param <Data> 泛型
     */
    public interface AdapterListener<Data> {
        //cell点击的时候出发
        void onItemClick(RecyclerAdapter.ViewHolder viewHolder, Data data);

        //cell长按点击的时候出发
        void onItemLongClick(RecyclerAdapter.ViewHolder viewHolder, Data data);
    }

    /**
     * 自定义的viewHolder
     *
     * @param <Data> 泛型的类型
     */
    public abstract static class ViewHolder<Data> extends RecyclerView.ViewHolder {
        protected AdapterCallback<Data> callback;
        protected Data mData;
        protected Unbinder unbinder;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        void bind(Data data) {
            this.mData = data;
            onBind(data);
        }

        //当触发绑定控件的时候要改变数据
        protected abstract void onBind(Data data);

        /**
         * holder对自己更新
         */
        public void updateData(Data data) {
            if (this.callback != null) {
                callback.update(data, this);
            }
        }
    }

    /**
     * 回调接口做一次实现
     *
     * @param <Data>
     */
    public static class AdapterListenerImpl<Data> implements AdapterListener<Data> {

        @Override
        public void onItemClick(ViewHolder holder, Data data) {

        }

        @Override
        public void onItemLongClick(ViewHolder viewHolder, Data data) {

        }
    }


}
