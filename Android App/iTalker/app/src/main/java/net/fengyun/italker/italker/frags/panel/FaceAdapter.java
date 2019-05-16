package net.fengyun.italker.italker.frags.panel;

import android.view.View;

import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;
import net.fengyun.italker.face.Face;
import net.fengyun.italker.italker.R;

import java.util.List;

/**
 * 表情的Adapter
 * @author fengyun
 */
public class FaceAdapter extends RecyclerAdapter<Face.Bean>{


    public FaceAdapter(List<Face.Bean> been, AdapterListener listener) {
        super(been, listener);
    }

    @Override
    protected ViewHolder<Face.Bean> onCreateViewHolder(View root, int viewType) {
        return new FaceHolder(root);
    }

    @Override
    protected int getItemViewType(int position, Face.Bean bean) {

        return R.layout.cell_face;
    }
}
