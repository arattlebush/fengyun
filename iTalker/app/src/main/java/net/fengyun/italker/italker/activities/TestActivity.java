package net.fengyun.italker.italker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.activities.weather.db.*;
import net.fengyun.italker.italker.activities.weather.util.*;



public class TestActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
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

    private String Province="山西省";
    private String City="晋中市";
    private String County="左权县";
    int Proid=0;
    int Cityid=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button button=(Button) findViewById(R.id.button);
        Province=Sub(Province);
        City=Sub(City);
        County=Sub(County);





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
                            Toast.makeText(TestActivity.this, weatherId, Toast.LENGTH_SHORT).show();
                        }else{

                        }
                    }else{

                    }
                }else{

                }
            }
        });


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
                TestActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //closeProgressDialog();
                        Toast.makeText(TestActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



}

