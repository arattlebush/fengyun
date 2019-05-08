package net.fengyun.italker.italker.frags.main;


import android.widget.ImageView;
import android.widget.TextView;

import net.fengyun.italker.common.app.Fragment;
import net.fengyun.italker.common.widget.PortraitView;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.activities.FriendCircleActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 动态的fragment
 * @author fengyun
 */
public class DynamicFragment extends Fragment {




    public DynamicFragment() {
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_dynamic;
    }

    @OnClick(R.id.layout_dynamic)
    void onDynamicClick(){
        FriendCircleActivity.show(getContext());
    }

}
