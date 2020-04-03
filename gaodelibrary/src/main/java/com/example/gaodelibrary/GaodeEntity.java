package com.example.gaodelibrary;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Keep;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;

/**
 * 高德地图实时轨迹记录类
 * Created by BLiYing on 2018/5/28.
 */
public class GaodeEntity implements AMapLocationListener {
    private final static String TAG = GaodeEntity.class.getName();
    private Context mContext;

    /**
     * 地图client
     */
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    /**
     * 是否开始巡河的标志
     */
    private boolean is_trace_started;
    /**
     * 首次开始巡河
     */
    private boolean first_start = true;

    /**
     * 返回巡河的距离
     */
    private double sumDistance_m = 0;//单位米
    /**
     * 设置起点图片
     */
    private int startMarker = 0;
    private int locationIcon = 0;
    /**
     * 轨迹颜色
     */
    private  int coloroftrace = Color.rgb(255, 0, 0);//默认红色

    /**
     * 通知栏
     */
    public static Notification notification;
    /**
     * 回调监听监听
     */
    private OnGaodeLibraryListen.DistanceListen distanceListen;
    private OnGaodeLibraryListen.LocationListen locationListen;
    private OnGaodeLibraryListen.NotificationListen notificationListen;
    private OnGaodeLibraryListen.DrawTraceListen drawTraceListen;

    /**
     * 锁屏相关
     */
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;
    private WakeLockScreenReceiver WakeLockScreenReceiver = null;

    private boolean isRegisterReceiver = false;
    private Intent serviceIntent = null;
    @Keep
    public void setDistanceListen(OnGaodeLibraryListen.DistanceListen distanceListen) {
        this.distanceListen = distanceListen;
    }

    @Keep
    public void setLocationListen(OnGaodeLibraryListen.LocationListen locationListen) {
        this.locationListen = locationListen;
    }
    @Keep
    public void setNotificationListen(OnGaodeLibraryListen.NotificationListen notificationListen) {
        this.notificationListen = notificationListen;
    }
    @Keep
    public void setDrawTraceListen(OnGaodeLibraryListen.DrawTraceListen drawTraceListen) {
        this.drawTraceListen = drawTraceListen;
    }

    /**
     * 点击通知栏进入的页面
     */
    private Class<?> startClass;
    private int resIdIcon;
    @Keep
    public GaodeEntity(Context context){
        this.mContext = context;
        initLocation();
    }

    /**
     * 初始化相关参数
     *
     * @param context
     */
    @Keep
    public GaodeEntity(Context context, Class<?> startClass,int resIdIcon) {
        this.mContext = context;
        this.startClass = startClass;
        this.resIdIcon = resIdIcon;

        init();

    }

    private void init(){

        powerManager = (PowerManager) UtilsContextOfGaode.getContext().getSystemService(Context.POWER_SERVICE);
        initLocation();
//        setMyLocationStyle();


    }

    public int getStartMarker() {
        return startMarker;
    }

    public void setStartMarker(int startMarker) {
        this.startMarker = startMarker;
    }

    public int getColoroftrace() {
        return coloroftrace;
    }

    public void setColoroftrace(int coloroftrace) {
        this.coloroftrace = coloroftrace;
    }
    @Keep
    public double getSumDistance_m() {
        return sumDistance_m;
    }
    @Keep
    public void setSumDistance_m(double sumDistance_m) {
        this.sumDistance_m = sumDistance_m;
    }


    public boolean isIs_trace_started() {
        return is_trace_started;
    }

    public void setIs_trace_started(boolean is_trace_started) {
        this.is_trace_started = is_trace_started;
    }
    @Keep
    public AMapLocationClient getLocationClient() {
        return locationClient;
    }
    @Keep
    public void setLocationClient(AMapLocationClient locationClient) {
        this.locationClient = locationClient;
    }
    @Keep
    public AMapLocationClientOption getLocationOption() {
        return locationOption;
    }
    @Keep
    public void setLocationOption(AMapLocationClientOption locationOption) {
        this.locationOption = locationOption;
    }

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    public void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(mContext.getApplicationContext());
        locationOption = getDefaultOption(5000);
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(this);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    public AMapLocationClientOption getDefaultOption(int interval) {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        //高德地图说明，来自高德android开发常见问题：
        //GPS模块无法计算出定位结果的情况多发生在卫星信号被阻隔时，在室内环境卫星信号会被墙体、玻璃窗阻隔反射，在“城市峡谷”（高楼林立的狭长街道）地段卫星信号也会损失比较多。
        ////强依赖GPS模块的定位模式——如定位SDK的仅设备定位模式，需要在室外环境进行，多用于行车、骑行、室外运动等场景。
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        /*mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setInterval(interval);//可选，设置定位间隔。默认为2秒
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差*/
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(interval);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 设置参数
     *
     * @param interval 定位频率
     */
    @Keep
    public void setTransportMode(int interval) {
        locationOption = getDefaultOption(interval);
        //设置定位参数
        locationClient.setLocationOption(locationOption);
    }



    /**
     * 开始定位
     */
    @Keep
    public void startLocation() {
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 关闭定位
     */
    @Keep
    public void closeLocation(){
        if(locationClient != null){
            locationClient.stopLocation();
        }
    }

    @Keep
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null) {
            if(locationListen != null){
                locationListen.getCurrentGaodeLocation(aMapLocation);

            }
            show(aMapLocation,"");
            /**
             * 距离回调
             */
            if(is_trace_started) {

                if(distanceListen != null){
                    distanceListen.getDistance(sumDistance_m);

                }
                if(drawTraceListen != null) {
                    drawTraceListen.drawTrace();
                }

            }

        }

    }

    /**
     * 定时接收广播定位
     * 用于锁屏后或者退出到后台时使用
     * 这样可以越过android本身对后台应用和定位频率的限制
     * 小米手机神隐模式智能限制下无效
     */
    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LOCATION")) {
                if (null != locationClient) {
                    startLocation();
                    Log.e(TAG, "开启广播定位--------");
                }
            }

        }
    };


    //展示定位信息
    private void show(AMapLocation location,String tag) {
        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {
            sb.append(tag + "\t 定位成功" + "\n");
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
        sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
        int numberOfSatellites = location.getLocationQualityReport().getGPSSatellites();
        /*if(numberOfSatellites <= 5){

        }*/
        sb.append("* GPS星数：").append(numberOfSatellites).append("\n");
        sb.append("****************").append("\n");
        Log.e(TAG, sb.toString());
    }

    /**
     * 获取GPS状态的字符串
     *
     * @param statusCode GPS状态码
     * @return
     */
    private String getGPSStatusString(int statusCode) {
        String str = "";
        switch (statusCode) {
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    /**
     * 开启
     */
    @Keep
    public void startTrace() {
        if (!is_trace_started) {
            setSumDistance_m(0);
            /**
             * 传入相应的启动界面
             */
            notification = creatNotification("00:00:00","0.000KM");
            startLocation();
            registerReceiverAndPower();

            sumDistance_m = 0;
            is_trace_started = true;

        }
    }

    /**
     * 创建通知
     * @param timeStr
     * @param disStr
     * @return
     */
    public Notification creatNotification(String timeStr,String disStr){
        Notification notification = NotificationBuildUtil.showNotification(UtilsContextOfGaode.getContext(),
                timeStr,disStr,startClass,resIdIcon);
        return notification;

    }

    /**
     * 关闭
     */
    @Keep
    public void stopTrace() {
        //清空之前的轨迹线
        NotificationBuildUtil.clearNotification();
        closeServiceAndReceiver();
        // 停止成功后，直接移除is_trace_started记录（便于区分用户没有停止服务，直接杀死进程的情况）
        is_trace_started = false;
        //是否显示定位蓝点
        first_start = true;
       /* myLocationStyle.showMyLocation(true);//是否显示定位蓝点
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style*/

    }

    /**
     * 开启高德地图记录轨迹
     */
    private void startGaodeService() {
        // 开启监听service(前台服务)
        GaodeLibraryService.isCheck = true;
        GaodeLibraryService.isRunning = true;
        serviceIntent = new Intent(UtilsContextOfGaode.getContext(), GaodeLibraryService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            // Pre-O behavior.
            UtilsContextOfGaode.getContext().startForegroundService(serviceIntent);
        } else {
            UtilsContextOfGaode.getContext().startService(serviceIntent);
        }

    }

    private void closeGaodeService(){
        // 停止监听service
        GaodeLibraryService.isCheck = false;
        GaodeLibraryService.isRunning = false;
        if (null != serviceIntent) {
            UtilsContextOfGaode.getContext().stopService(serviceIntent);
        }
    }


    /**
     * 注册广播（电源锁、服务）
     */
    private void registerReceiverAndPower() {

        if (isRegisterReceiver) {
            return;
        }
        registerLocationReceiver();
        if (!GaodeLibraryService.isRunning) {
            startGaodeService();
        }
        registerPowerReceiver();

        isRegisterReceiver = true;

    }

    /**
     * 解除广播（电源锁、服务）
     */
    private void closeServiceAndReceiver() {
        if (hasRegAlarm) {
            unregisterLocationReceiver();
        }
        closeGaodeService();
        unregisterPowerReceiver();
    }

    private boolean hasRegAlarm;

    //注册定位广播
    private void registerLocationReceiver() {
        if (hasRegAlarm) {
            return;
        }
        hasRegAlarm = true;
        //动态注册一个广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOCATION");
        mContext.registerReceiver(locationReceiver, filter);
    }

    //解除定位广播
    private void unregisterLocationReceiver() {
        //动态注册一个广播
        hasRegAlarm = false;
        mContext.unregisterReceiver(locationReceiver);
    }

    //注册锁屏监听广播
    private void registerPowerReceiver(){
        if (null == wakeLock) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track:upload");
        }
        if (null == WakeLockScreenReceiver) {
            WakeLockScreenReceiver = new WakeLockScreenReceiver(wakeLock);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        UtilsContextOfGaode.getContext().registerReceiver(WakeLockScreenReceiver, filter);
    }

    //解除锁屏监听广播
    private void unregisterPowerReceiver() {

        if (!isRegisterReceiver) {
            return;
        }
        if (null != WakeLockScreenReceiver) {
            UtilsContextOfGaode.getContext().unregisterReceiver(WakeLockScreenReceiver);
        }
        isRegisterReceiver = false;
    }



}
