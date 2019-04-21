package net.fengyun.italker.italker.frags.panel;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;

import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;
import net.fengyun.italker.face.Face;
import net.fengyun.italker.italker.R;

import butterknife.BindView;

/**
 * 表情的holder
 * @author fengyun
 */
public class FaceHolder extends RecyclerAdapter.ViewHolder<Face.Bean>{

    @BindView(R.id.im_face)
    ImageView mFace;



    public FaceHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(Face.Bean bean) {
        if(bean!=null && (
                //drawable资源id                      //face zip包资源路径
                (bean.preview instanceof Integer) || bean.preview instanceof String))
        {
            Glide.with(mFace.getContext())
                    .load(bean.preview)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)//设置解码的格式，为了保证清晰度
                    .placeholder(R.drawable.default_face)
                    .into(mFace);
        }
    }
}
