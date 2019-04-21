package net.fengyun.italker.italker.frags.main;

import net.fengyun.italker.italker.activities.FriendCircleActivity;
import net.fengyun.italker.italker.activities.WebActivity;
import net.fengyun.italker.italker.frags.main.Wechat.WeChatAdapter;
import net.fengyun.italker.italker.frags.main.Wechat.WeChatData;
import net.fengyun.italker.italker.frags.main.assist.Class;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import net.fengyun.italker.common.app.Fragment;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.frags.main.assist.ClassAdapter;

import butterknife.BindView;
import butterknife.OnClick;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态的fragment
 * @author fengyun
 */
public class ActiveFragment extends Fragment {


    private ListView mListView;

    private List<WeChatData> mList = new ArrayList<>();
    //标题
    private List<String> mListTitle = new ArrayList<>();
    //地址
    private List<String> mListUrl = new ArrayList<>();
    @BindView(R.id.roll_view_pager)
    RollPagerView mRollViewPager;

    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;


//
//    @BindView(R.id.ads_1)
//    ImageView imageView1;
//
//    @BindView(R.id.ads_2)
//    ImageView imageView2;
//
//    @BindView(R.id.ads_3)
//    ImageView imageView3;
//
//    @BindView(R.id.ads_4)
//    ImageView imageView4;
//
//    @OnClick(R.id.ads_1)
//    void onActiveClick1(){
//        StartWeb("http://you.ctrip.com/sight/xian7/2026356.html");
//    }
//
//    @OnClick(R.id.ads_2)
//    void onActiveClick2(){
//        StartWeb("https://www.meituan.com/meishi/2573120/");
//    }
//
//
//    @OnClick(R.id.ads_3)
//    void onActiveClick3(){
//        StartWeb("https://maoyan.com/films/344929");
//    }
//
//
//    @OnClick(R.id.ads_4)
//    void onActiveClick4(){
//        StartWeb("https://www.meituan.com/meishi/93262676/");
//    }





    private Class[]classes={new Class("天气",R.drawable.homepage_icon_light_movie_b),new Class("美食",R.drawable.homepage_icon_light_food_b),new Class("酒店",R.drawable.homepage_icon_light_hotel_b),new Class("医院",R.drawable.homepage_icon_light_amusement_b)};
    private List<Class> classList=new ArrayList<>();
    private ClassAdapter adapter;




    public ActiveFragment() {
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initWeight(View root) {
        super.initWeight(root);
        mRollViewPager.setAnimationDurtion(500);    //设置切换时间
        mRollViewPager.setAdapter(new TestLoopAdapter(mRollViewPager)); //设置适配器
        mRollViewPager.setHintView(new ColorPointHintView(getContext(),Color.WHITE, Color.GRAY));// 设置圆点指示器颜色


        initClasses();//卡片式
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),4);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new ClassAdapter(classList);
        recyclerView.setAdapter(adapter);

        findView(root);

    }
    //初始化View
    private void findView(View view) {
        mListView = (ListView) view.findViewById(R.id.mListView);

        //解析接口
        String url = "http://v.juhe.cn/weixin/query?key=" + "78f723dccf85aea324a3cf0daac97f35" + "&ps=100";
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                //Toast.makeText(getActivity(), t, Toast.LENGTH_LONG).show();
                //L.i("wechat json:" + t);
                //Log.i("baidumaptest","wechat json:" + t);
                parsingJson(t);
            }
        });
        //点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //L.i("position:" + position);
                Intent intent = new Intent(getActivity(), WebActivity.class);
                //intent.putExtra("title", mListTitle.get(position));
                intent.putExtra("url", mListUrl.get(position));
                startActivity(intent);
            }
        });
    }


    //解析Json
    private void parsingJson(String t) {
        try {
            JSONObject jsonObject = new JSONObject(t);
            JSONObject jsonresult = jsonObject.getJSONObject("result");
            JSONArray jsonList = jsonresult.getJSONArray("list");
            for (int i = 0; i < jsonList.length(); i++) {
                JSONObject json = (JSONObject) jsonList.get(i);
                WeChatData data = new WeChatData();

                String titlr = json.getString("title");
                String url = json.getString("url");

                data.setTitle(titlr);
                data.setSource(json.getString("source"));
                data.setImgUrl(json.getString("firstImg"));

                mList.add(data);

                mListTitle.add(titlr);
                mListUrl.add(url);
            }
            WeChatAdapter adapter = new WeChatAdapter(getActivity(), mList);
            mListView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void StartWeb(String data){
        Intent intent=new Intent(getActivity(), WebActivity.class);
        //intent.putExtra("title", "666");
        intent.putExtra("url",data);
        startActivity(intent);
    }




    private void initClasses() {
        classList.clear();
        for(int i=0;i<4;i++){

            classList.add(classes[i]);
        }
    }



    private class TestLoopAdapter extends LoopPagerAdapter {
        private int[] imgs = {R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4};  // 本地图片
        private int count = imgs.length; // banner上图片的数量

        public TestLoopAdapter(RollPagerView viewPager)
        {
            super(viewPager);
        }

        @Override
        public View getView(ViewGroup container, int position)
        {
            final int picNo = position + 1;
            ImageView view = new ImageView(container.getContext());
            view.setImageResource(imgs[position]);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setOnClickListener(new View.OnClickListener()      // 点击事件
            {
                @Override
                public void onClick(View v)
                {
                    //Toast.makeText(MainActivity.this, "点击了第" + picNo + "张图片", Toast.LENGTH_SHORT).show();
                    switch (picNo){
                        case 1:
                            StartWeb("https://zt.dujia.qunar.com/bp/zt.php?id=4569&tf=xbtj02&in_track=xbtj02");
                            break;
                        case 2:
                            StartWeb("https://zt.dujia.qunar.com/bp/zt.php?id=4634&tf=xbtj03&in_track=xbtj03");
                            break;
                        case 3:
                            StartWeb("https://touch.dujia.qunar.com/hotelpackages?tf=xbtj04&in_track=xbtj04");
                            break;
                        case 4:
                            StartWeb("https://zt.dujia.qunar.com/bp/zt.php?id=4578&tf=xbtj05&in_track=xbtj05");
                            break;
                    }

                }

            });

            return view;
        }

        @Override
        public int getRealCount()
        {
            return count;
        }

    }
}
