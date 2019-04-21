package net.fengyun.italker.common.widget.recycler;

/**
 * Created by Administrator on 2017/5/23 0023.
 */
public interface AdapterCallback<Data> {

    void update(Data data,RecyclerAdapter.ViewHolder<Data> holder);

}
