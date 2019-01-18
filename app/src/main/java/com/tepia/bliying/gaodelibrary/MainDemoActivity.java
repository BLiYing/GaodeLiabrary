package com.tepia.bliying.gaodelibrary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.example.gaodelibrary.GaodeEntity;
import com.example.gaodelibrary.OnGaodeLibraryListen;
import com.example.gaodelibrary.UtilsContextOfGaode;

public class MainDemoActivity extends AppCompatActivity implements OnGaodeLibraryListen.DistanceListen,OnGaodeLibraryListen.LocationListen {
    private GaodeEntity gaodeEntity;
    private AMapLocation aMapLocation;
    private TextView distanceTv;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button startbutton = findViewById(R.id.startBtn);
        Button endbutton = findViewById(R.id.endBtn);
        distanceTv = findViewById(R.id.distanceTv);
        UtilsContextOfGaode.init(this);


        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                startRequestPermission();
            }else{
                gaodeEntity = new GaodeEntity(this, MainDemoActivity.class, R.mipmap.ic_launcher_round);
                gaodeEntity.setLocationListen(this);
            }
        }else{
            gaodeEntity = new GaodeEntity(this, MainDemoActivity.class, R.mipmap.ic_launcher_round);
            gaodeEntity.setLocationListen(this);

        }


        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gaodeEntity != null) {
                    gaodeEntity.startTrace();
                }

            }
        });
        endbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gaodeEntity != null) {
                    gaodeEntity.stopTrace();
                }
                distanceTv.setText("--");


            }
        });
    }

    @Override
    public void getDistance(double distance) {
//        distanceTv.setText("行走距离：" + distance + "");
    }

    private void startRequestPermission(){
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                gaodeEntity = new GaodeEntity(this, MainDemoActivity.class, R.mipmap.ic_launcher_round);
                gaodeEntity.setLocationListen(this);


            }
        }
    }

    @Override
    public void getCurrentGaodeLocation(AMapLocation aMapLocation) {
        distanceTv.setText(getSb(aMapLocation).toString());

    }

    //展示定位信息
    private StringBuffer getSb(AMapLocation location) {
        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {
            sb.append("\t 定位成功" + "\n");
            sb.append("定位类型: " + location.getLocationType() + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\t");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
            sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
          /*  sb.append("提供者    : " + location.getProvider() + "\n");

            sb.append("角    度    : " + location.getBearing() + "\n");
            // 获取当前提供定位服务的卫星个数
            sb.append("星    数    : " + location.getSatellites() + "\n");
            sb.append("国    家    : " + location.getCountry() + "\n");
            sb.append("省            : " + location.getProvince() + "\n");
            sb.append("市            : " + location.getCity() + "\n");
            sb.append("城市编码 : " + location.getCityCode() + "\n");
            sb.append("区            : " + location.getDistrict() + "\n");
            sb.append("区域 码   : " + location.getAdCode() + "\n");
            sb.append("地    址    : " + location.getAddress() + "\n");
            sb.append("地    址    : " + location.getDescription() + "\n");
            sb.append("兴趣点    : " + location.getPoiName() + "\n");*/

        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }
        sb.append("***定位质量报告***").append("\n");
        sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
//        sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
        int numberOfSatellites = location.getLocationQualityReport().getGPSSatellites();
        /*if(numberOfSatellites <= 5){

        }*/
        sb.append("* GPS星数：").append(numberOfSatellites).append("\n");
        sb.append("****************").append("\n");
        return sb;

    }
}
