package net.fengyun.italker.italker.activities;



import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.activities.weather.db.City;
import net.fengyun.italker.italker.activities.weather.db.County;
import net.fengyun.italker.italker.activities.weather.db.Province;
import net.fengyun.italker.italker.activities.weather.util.HttpUtil;
import net.fengyun.italker.italker.activities.weather.util.Utility;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BaiduMapActivity extends AppCompatActivity {

    public LocationClient mLocationClient;

    private TextView positionText;

    private MapView mapView;

    private BaiduMap baiduMap;

    private boolean isFirstLocate = true;

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    private String Province;
    private String City;
    private String County;
    int Proid=0;
    int Cityid=0;

    private double Latitude;
    private double Longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidu_map);
        mapView = (MapView) findViewById(R.id.bmapView);
        Button button=(Button) findViewById(R.id.bu);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        positionText = (TextView) findViewById(R.id.position_text_view);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(BaiduMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(BaiduMapActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(BaiduMapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(BaiduMapActivity.this, permissions, 1);
        } else {
            requestLocation();
        }




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Proid=queryProvinces(Province);
                Log.d("TestActivity", String.valueOf(Proid));
                if(0 != Proid){
                    Cityid=queryCities(City,Proid);
                    Log.d("TestActivity", String.valueOf(Cityid));
                    if(Cityid!=0){
                        String weatherId = queryCounties(County, Cityid, Proid);
                        Log.d("TestActivity", String.valueOf(weatherId));
                        if(weatherId!=null){
                            //Toast.makeText(BaiduMapActivity.this, weatherId, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BaiduMapActivity.this, WeatherActivity.class);
                            intent.putExtra("weather_id",weatherId);
                            startActivity(intent);
                        }else{
                            finish();
                        }
                    }else{

                    }
                }else{

                }
            }
        });


    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            Toast.makeText(this, "nav to " + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.
                Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        mLocationClient.setLocOption(option);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
            currentPosition.append("经线：").append(location.getLongitude()).append("\n");
            currentPosition.append("国家：").append(location.getCountry()).append("\n");
            currentPosition.append("省：").append(location.getProvince()).append("\n");
            currentPosition.append("市：").append(location.getCity()).append("\n");
            currentPosition.append("区：").append(location.getDistrict()).append("\n");
            currentPosition.append("街道：").append(location.getStreet()).append("\n");
            currentPosition.append("定位方式：");
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            }
            Province=location.getProvince();
            City=location.getCity();
            County=location.getDistrict();
            Province=Sub(Province);
            City=Sub(City);
            Latitude=location.getLatitude();
            Longitude=location.getLongitude();
            positionText.setText(currentPosition);
            //Toast.makeText(BaiduMapActivity.this, currentPosition, Toast.LENGTH_LONG).show();
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                //navigateTo(location);
            }
        }

    }

    private String Sub(String string){
        string=string.substring(0,string.length()-1);
        return string;
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private int queryProvinces(String Province) {

        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {

            for (Province province : provinceList) {
                if(Province.equals(province.getProvinceName())){
                    return province.getId();
                }
            }

            //currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }

        return 0;
    }


    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private int queryCities(String City, int Proid) {

        cityList = DataSupport.where("provinceid = ?", String.valueOf(Proid)).find(City.class);
        if (cityList.size() > 0) {
            for (City city : cityList) {
                if(City.equals( city.getCityName())){
                    return city.getId();
                }
            }
        } else {
            List<Province> pro=DataSupport.where("id = ?",String.valueOf(Proid)).find(Province.class);
            int provinceCode =pro.get(0).getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
        return 0;
    }


    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private String queryCounties(String County, int cityid, int Proid) {

        countyList = DataSupport.where("cityid = ?", String.valueOf(cityid)).find(County.class);
        if (countyList.size() > 0) {

            for (County county : countyList) {
                if(County.equals(county.getCountyName())){
                    return county.getWeatherId();
                }
            }

        } else {
            List<Province> pro=DataSupport.where("id = ?",String.valueOf(Proid)).find(Province.class);
            int provinceCode =pro.get(0).getProvinceCode();

            List<City> city =DataSupport.where("id = ?",String.valueOf(cityid)).find(City.class);
            int cityCode =city.get(0).getCityCode();

            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
        return null;
    }





    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     */
    private void queryFromServer(String address, final String type) {
        //showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, Proid);
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, Cityid);
                }


                if (result) {
                    if ("province".equals(type)) {
                        queryProvinces(Province);
                    } else if ("city".equals(type)) {
                        queryCities(City,Proid);
                    } else if ("county".equals(type)) {
                        queryCounties(County,Cityid,Proid);
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                BaiduMapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //closeProgressDialog();
                        Toast.makeText(BaiduMapActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
