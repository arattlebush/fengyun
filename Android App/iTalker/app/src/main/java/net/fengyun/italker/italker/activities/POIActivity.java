package net.fengyun.italker.italker.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapFragment;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.activities.POI.*;

import net.fengyun.italker.italker.activities.POI.my.*;




public class POIActivity extends AppCompatActivity {


    MapView mMapView = null;//百度地图显示布局对象
    BaiduMap mBaiduMap = null;//百度地图控制对象
    EditText et_city;
    EditText et_key;
    EditText et_around;
    EditText et_line;
    EditText et_start;
    PoiSearch poiSearch;//Poi搜索对象
    PoiSearch busPoiSearch;//公交地铁的Poi搜索对象
    BusLineSearch busLineSearch;//公交检索对象
    RoutePlanSearch routePlanSearch;//路线规划
    TextView text3;


    private double Latitude;
    private double Longitude;
    private String leix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);
        Latitude=Double.valueOf(getIntent().getStringExtra("Latitude"));
        Longitude=Double.parseDouble(getIntent().getStringExtra("Longitude"));
        leix=getIntent().getStringExtra("leix");
        //Toast.makeText(POIActivity.this,String.valueOf(Latitude), Toast.LENGTH_SHORT).show();
        //Log.d("POIActivity", String.valueOf(Latitude));
        initBaiduMap();

    }




    private void initBaiduMap() {
        //布局对象

        et_city = (EditText) findViewById(R.id.et_city);
        et_key = (EditText) findViewById(R.id.et_key);
        et_around = (EditText) findViewById(R.id.et_around);
        et_line = (EditText) findViewById(R.id.et_line);
        et_start = (EditText) findViewById(R.id.et_start);
        text3 = (TextView) findViewById(R.id.text3);
        et_around.setText(leix);
        //获取地图控件引用
        mMapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment)).getMapView();
        //百度地图控制对象
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        //设置默认显示的位置
        //定义Maker坐标点,深圳大学经度和纬度113.943062,22.54069
        //设置的时候经纬度是反的 纬度在前，经度在后
        LatLng latlng = new LatLng(Latitude, Longitude);
        //1-20级 20级室内地图
        MapStatusUpdate mapStatusUpdate =
                MapStatusUpdateFactory.newLatLngZoom(latlng, 18);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        //搜索对象的创建
        poiSearch = PoiSearch.newInstance();
        busPoiSearch = PoiSearch.newInstance();
        //设置Poi监听对象
        poiSearch.setOnGetPoiSearchResultListener(poiSearchResultListener);
        busPoiSearch.setOnGetPoiSearchResultListener(busPoiSearchResultListener);

        //公交检索对象
        busLineSearch = BusLineSearch.newInstance();
        //设置公交监听对象，公交检索得到结果后，里面的方法得到回调
        busLineSearch.setOnGetBusLineSearchResultListener(new OnGetBusLineSearchResultListener() {
            @Override
            public void onGetBusLineResult(BusLineResult busLineResult) {
                MyBusOverLay overlay = new MyBusOverLay(mBaiduMap, busPoiSearch);
                //设置数据,这里只需要一步，
                overlay.setData(busLineResult);
                //添加到地图
                overlay.addToMap();
                //将显示视图拉倒正好可以看到所有POI兴趣点的缩放等级
                overlay.zoomToSpan();//计算工具
                //设置标记物的点击监听事件
                mBaiduMap.setOnMarkerClickListener(overlay);

            }
        });

        routePlanSearch = RoutePlanSearch.newInstance();//路线规划对象
        //给路线规划添加监听
        routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            //步行路线结果回调
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
                mBaiduMap.clear();
                if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    WalkingRouteOverlay walkingOverlay = new WalkingRouteOverlay(mBaiduMap);
                    walkingOverlay.setData(walkingRouteResult.getRouteLines().get(0));// 设置一条路线方案
                    walkingOverlay.addToMap();
                    walkingOverlay.zoomToSpan();
                    mBaiduMap.setOnMarkerClickListener(walkingOverlay);
                    Log.e("TAG", walkingOverlay.getOverlayOptions() + "");

                } else {
                    Toast.makeText(getBaseContext(), "搜不到！", Toast.LENGTH_SHORT).show();
                }
            }

            //换乘线结果回调
            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            //跨城公共交通路线结果回调
            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            //驾车路线结果回调
            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                mBaiduMap.clear();//清除图标或路线
                if (drivingRouteResult == null
                        || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(getBaseContext(), "抱歉，未找到结果",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            mBaiduMap);
                    drivingRouteOverlay.setData(drivingRouteResult.getRouteLines().get(1));// 设置一条驾车路线方案
                    mBaiduMap.setOnMarkerClickListener(drivingRouteOverlay);
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    int totalLine = drivingRouteResult.getRouteLines().size();
                    Toast.makeText(getBaseContext(),
                            "共查询出" + totalLine + "条符合条件的线路", Toast.LENGTH_LONG).show();

                    // 通过getTaxiInfo()可以得到很多关于打车的信息
//                    Toast.makeText(getBaseContext(),"该路线打车总路程"+ drivingRouteResult.getTaxiInfo()
//                                    .getDistance(), Toast.LENGTH_LONG).show();
                }
            }


            //室内路线规划回调
            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            // 骑行路线结果回调
            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
    }


    //POI检索的监听对象
    OnGetPoiSearchResultListener poiSearchResultListener = new OnGetPoiSearchResultListener() {
        //获得POI的检索结果，一般检索数据都是在这里获取
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            mBaiduMap.clear();
            if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {//如果没有错误
                MyOverLay overlay = new MyOverLay(mBaiduMap, poiSearch);
                //设置数据,这里只需要一步，
                overlay.setData(poiResult);
                //添加到地图
                overlay.addToMap();
                //将显示视图拉倒正好可以看到所有POI兴趣点的缩放等级
                overlay.zoomToSpan();//计算工具
                //设置标记物的点击监听事件
                mBaiduMap.setOnMarkerClickListener(overlay);
                return;
            } else {
                Toast.makeText(getApplication(), "搜索不到你需要的信息！", Toast.LENGTH_SHORT).show();
            }
        }

        //获得POI的详细检索结果，如果发起的是详细检索，这个方法会得到回调(需要uid)
        @SuppressLint("SetTextI18n")
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(getApplication(), "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            } else {// 正常返回结果的时候，此处可以获得很多相关信息
                //Toast.makeText(getApplication(), poiDetailResult.getName() + ": " + poiDetailResult.getAddress(), Toast.LENGTH_LONG).show();
                StartWeb(poiDetailResult.getDetailUrl());
                //text3.setText(poiDetailResult.getName() + ": " + poiDetailResult.getAddress());
                //initBaiduMap();
            }
        }



        //获得POI室内检索结果
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    //公交路线的点的POI检索的监听对象
    OnGetPoiSearchResultListener busPoiSearchResultListener = new OnGetPoiSearchResultListener() {
        //获得POI的检索结果，一般检索数据都是在这里获取
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            mBaiduMap.clear();//清除标志
            if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {//如果没有错误
                //遍历所有数据
                for (int i = 0; i < poiResult.getAllPoi().size(); i++) {
                    //获取里面的数据对象
                    PoiInfo poiInfo = poiResult.getAllPoi().get(i);
                    //判断检索到的点的类型是不是公交路线或地铁路线
                    if (poiInfo.type == PoiInfo.POITYPE.BUS_LINE || poiInfo.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                        //发起公交检索
                        BusLineSearchOption busLineOptions = new BusLineSearchOption();
                        busLineOptions.city("深圳").uid(poiInfo.uid);
                        busLineSearch.searchBusLine(busLineOptions);
                    }
                }

                return;
            } else {
                Toast.makeText(getApplication(), "搜索不到你需要的信息！", Toast.LENGTH_SHORT).show();
            }
        }

        //获得POI的详细检索结果，如果发起的是详细检索，这个方法会得到回调(需要uid)
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(getApplication(), "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            } else {// 正常返回结果的时候，此处可以获得很多相关信息
                Toast.makeText(getApplication(), poiDetailResult.getName() + ": "
                                + poiDetailResult.getAddress(),
                        Toast.LENGTH_LONG).show();
            }
        }

        //获得POI室内检索结果
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    //POI搜索，城市检索
    public void select(View view) {
        //获得Key
        String city = et_city.getText().toString();
        String key = et_key.getText().toString();
        //发起检索
        PoiCitySearchOption poiCity = new PoiCitySearchOption();
        poiCity.keyword(key).city(city);
        poiSearch.searchInCity(poiCity);
    }

    //POI搜索，范围检索,
    public void selectAround(View view) {

        //定义Maker坐标点
        double latitude = Latitude;
        double longitude = Longitude;
        //获得Key
        String key = et_around.getText().toString();
        //发起周边检索
        PoiBoundSearchOption boundSearchOption = new PoiBoundSearchOption();
        LatLng southwest = new LatLng(latitude - 0.01, longitude - 0.012);// 西南
        LatLng northeast = new LatLng(latitude + 0.01, longitude + 0.012);// 东北
        LatLngBounds bounds = new LatLngBounds.Builder().include(southwest)
                .include(northeast).build();// 得到一个地理范围对象
        boundSearchOption.bound(bounds);// 设置poi检索范围
        boundSearchOption.keyword(key);// 检索关键字
        boundSearchOption.pageNum(1);//搜索一页
        poiSearch.searchInBound(boundSearchOption);// 发起poi范围检索请求
    }

    //POI搜索，周边检索,
    public void selectPoint(View view) {
        //定义Maker坐标点,
        //设置的时候经纬度是反的 纬度在前，经度在后
        LatLng point = new LatLng(Latitude, Longitude);
        //获得Key
        String key = et_around.getText().toString();
        //周边检索
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(point);
        nearbySearchOption.keyword(key);
        nearbySearchOption.radius(10000);// 检索半径，单位是米
        nearbySearchOption.pageNum(1);//搜索一页
        poiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }

    //公交路线搜索,步骤：
    //1.先发起一个poi检索，(新建一个Poi检索对象)
    // 2.在poi的检索结果中发起公交搜索
    //3.在公交检索搜索结果中获取数据
    public void selectBusLine(View view) {
        String key = et_line.getText().toString();
        //城市内的公交、地铁路线检索
        PoiCitySearchOption poiCitySearchOptions = new PoiCitySearchOption();
        poiCitySearchOptions.city("深圳").keyword(key);//城市检索的数据设置
        busPoiSearch.searchInCity(poiCitySearchOptions);
    }

    //普通路线搜索,步行路线：
    public void selectWalkingLine(View view) {
        //定义Maker坐标点,深圳大学经度和纬度113.943062,22.54069
        //设置的时候经纬度是反的 纬度在前，经度在后
        LatLng point = new LatLng(22.54069, 113.943062);
        //获得关键字
        String key = et_start.getText().toString();
        //创建步行路线搜索对象
        WalkingRoutePlanOption walkingSearch = new WalkingRoutePlanOption();
        //设置节点对象，可以通过城市+关键字或者使用经纬度对象来设置
        PlanNode fromeNode = PlanNode.withCityNameAndPlaceName("深圳", key);
        PlanNode toNode = PlanNode.withLocation(point);
        walkingSearch.from(fromeNode).to(toNode);
        routePlanSearch.walkingSearch(walkingSearch);
    }


    //普通路线搜索,驾车路线：
    public void selectDrivingLine(View view) {
        //定义Maker坐标点,深圳大学经度和纬度113.943062,22.54069
        //设置的时候经纬度是反的 纬度在前，经度在后
        LatLng point = new LatLng(22.54069, 113.943062);
        //获得关键字
        String key = et_start.getText().toString();
        //创建驾车路线搜索对象
        DrivingRoutePlanOption drivingOptions = new DrivingRoutePlanOption();
        //设置节点对象，可以通过城市+关键字或者使用经纬度对象来设置
        PlanNode fromeNode = PlanNode.withCityNameAndPlaceName("深圳", key);
        PlanNode toNode = PlanNode.withLocation(point);
        drivingOptions.from(fromeNode).to(toNode);
        //drivingOptions.passBy(list);//设置路线经过的点，要放入一个PlanNode的集合对象
        drivingOptions.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_AVOID_JAM);//设置驾车策略，避免拥堵
        routePlanSearch.drivingSearch(drivingOptions);//发起驾车检索
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        poiSearch.destroy();
        busLineSearch.destroy();
        mBaiduMap.setMyLocationEnabled(false);
    }
    private void StartWeb(String data){
        Intent intent=new Intent(POIActivity.this, WebActivity.class);
        //intent.putExtra("title", "666");
        intent.putExtra("url",data);
        startActivity(intent);
    }
}
