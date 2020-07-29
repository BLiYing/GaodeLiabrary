package com.tepia.bliying.gaodelibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.example.gaodelibrary.GaodeEntity;
import com.example.gaodelibrary.OnGaodeLibraryListen;
import com.example.gaodelibrary.UtilsContextOfGaode;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainDemoActivity extends AppCompatActivity implements OnGaodeLibraryListen.DistanceListen, OnGaodeLibraryListen.LocationListen, OnGaodeLibraryListen.DrawTraceListen {
    /**
     * 巡河高德地图工具类
     */
    private GaodeEntity gaodeEntity;
    private TextView distanceTv;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    private AMap aMap;
    private MapView mMapView;
    private MyLocationStyle myLocationStyle;


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        final Button startbutton = findViewById(R.id.startBtn);
        Button endbutton = findViewById(R.id.endBtn);
        distanceTv = findViewById(R.id.distanceTv);
        UtilsContextOfGaode.init(this);

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            int j = ContextCompat.checkSelfPermission(this, permissions[1]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED || j != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                startRequestPermission();
            } else {
                initGaodeMap();
            }
        } else {
            initGaodeMap();

        }*/
        requestPermission();

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gaodeEntity != null) {
                    if (gaodeEntity.isIs_trace_started()) {
                        startbutton.setText("开始轨迹记录");
                        gaodeEntity.stopTrace();
                        distanceTv.setText("--");

                        if (trackPoints != null) {
                            trackPoints.clear();
                        }

                  /*  if (tracedPolyline != null) {
                        tracedPolyline.remove();
                    }
                    if (aMap != null && marker != null) {
                        marker.remove();
                    }*/
                        aMap.clear();
                    } else {
                        gaodeEntity.startTrace();
                        startbutton.setText("停止轨迹记录");
                    }

                }

            }
        });

    }

    //权限检测
    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(permissions)
                .subscribe(permission -> {
                    if (permission.granted) {
                        initGaodeMap();

                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // Denied permission without ask never again
                        Toast.makeText(MainDemoActivity.this,"请开启定位权限",Toast.LENGTH_LONG).show();
                    } else {
                        // Denied permission with ask never again
                        //Need to go to the setting

                    }

                });
    }

    protected void initGaodeMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            setMyLocationStyle(aMap);
            /**
             * 千万记得在application中初始化UtilsContextOfGaode.init(this);
             */
            gaodeEntity = new GaodeEntity(this, MainDemoActivity.class, R.mipmap.ic_launcher_round);
//            gaodeEntity = new GaodeEntity(this);
            gaodeEntity.setDistanceListen(this);
            gaodeEntity.setLocationListen(this);
            gaodeEntity.setDrawTraceListen(this);
            gaodeEntity.startLocation();
        }


    }

    /**
     * 设置地图参数
     */
    private void setMyLocationStyle(AMap aMap) {

        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、
        /// 且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        /// （1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        //连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);
//        myLocationStyle.showMyLocation(true);//是否显示定位蓝点
//        myLocationStyle.myLocationIcon(com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        // 缩放级别（zoom）：地图缩放级别范围为【4-20级】，值越大地图越详细
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));

        aMap.setMyLocationEnabled(true);



    }

    @Override
    public void getDistance(double distance) {
        distanceTv.setText("行走距离：" + distance + "m");
        GaodeEntity.notification = gaodeEntity.creatNotification("--", distance + "");

    }


    private List<LatLng> trackPoints = new ArrayList<>();
    private LatLng currentLatLng;
    private LatLng lastLatLng;

    @Override
    public void getCurrentGaodeLocation(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == 0) {

            currentLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            //在开发时根据精度（通过AMapLocation类的getAccuracy()方法获取）进行定位点过滤，例如精度大于10米的点不进行业务运算。
            float accuracy = aMapLocation.getAccuracy();
            if (accuracy > 100) {
                return;
            }

            if (gaodeEntity.isIs_trace_started()) {

                trackPoints.add(currentLatLng);
                setUpMap(trackPoints);
                float distance = AMapUtils.calculateLineDistance(lastLatLng, currentLatLng);
                BigDecimal b = new BigDecimal(String.valueOf(distance));
                double d = b.doubleValue();
                gaodeEntity.setSumDistance_m(gaodeEntity.getSumDistance_m() + d);

            }
            lastLatLng = currentLatLng;


        }else{
            Toast.makeText(MainDemoActivity.this, aMapLocation.getErrorCode() + aMapLocation.getErrorInfo()+"",Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void drawTrace() {
        setUpMap(trackPoints);

    }

    private Polyline tracedPolyline = null;
    /**
     * 轨迹颜色
     */
    private int coloroftrace = Color.rgb(255, 0, 0);//默认红色
    private int startMarker = 0;


    /**
     * 绘制两多个坐标点之间的线段,从以前位置到现在位置
     */
    private void setUpMap(List<LatLng> rectifications) {
        if (rectifications.size() < 2) {
            return;
        }
        Log.e(MainDemoActivity.class.getName(), "--------开始画线-------");
        if (tracedPolyline != null) {
            tracedPolyline.remove();
        }

        //高德官方提供的抽稀去燥工具
        PathSmoothTool pathSmoothTool = new PathSmoothTool();
        pathSmoothTool.setIntensity(4);
        rectifications = pathSmoothTool.pathOptimize(rectifications);


       //自定义抽稀工具（没有滤波效果）
       /* Douglas douglas = new Douglas(new ArrayList<>(rectifications), 2);
        rectifications = douglas.compress();*/

        addStartMark(rectifications.get(0));
//        double angle = UtilsOfGaode.getAngle(rectifications.get(0),rectifications.get(rectifications.size() -1));
//        addLocationMark(rectifications.get(rectifications.size() -1),angle);
        // 绘制一个大地曲线
        tracedPolyline = aMap.addPolyline((new PolylineOptions())
                .addAll(rectifications).geodesic(true)
                .width(15).color(coloroftrace));


    }

    /**
     * 添加起始图片
     */
    private MarkerOptions markerOption;
    private Marker marker;

    public void addStartMark(LatLng latLng) {
        if (aMap != null && marker != null) {
            marker.remove();
        }
        if (startMarker == 0) {
            startMarker = com.example.gaodelibrary.R.drawable.ic_me_history_startpoint;
        }
        markerOption = new MarkerOptions().icon(com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(startMarker))
                .position(latLng)
                .draggable(true);
        marker = aMap.addMarker(markerOption);
    }


}
