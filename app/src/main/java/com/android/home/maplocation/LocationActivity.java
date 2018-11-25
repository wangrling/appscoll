package com.android.home.maplocation;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.home.R;
import com.baidu.location.*;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends Activity {

    public static final String TAG = "Location";

    private final int SDK_PERMISSION_REQUEST = 127;

    LocationClient locationClient;
    LocationClientOption locationOption;

    private String permissionInfo;

    private MapView mMapView;

    BaiduMap mBaiduMap;
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
    private BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
    private int accuracyCircleFillColor = 0xAAFFFF88;//自定义精度圈填充颜色
    private int accuracyCircleStrokeColor = 0xAA00FF00;//自定义精度圈边框颜色

    public BDNotifyListener notifyListener = new MyNotifyListener();

    LatLng llText = new LatLng(39.86923, 116.397428);
    OverlayOptions textOption = new TextOptions()
            .bgColor(0xAAFFFF00)
            .fontSize(24)
            .fontColor(0xFFFF00FF)
            .text("百度地图SDK")
            .rotate(-30)
            .position(llText);

    // 构建折线点
    LatLng p1 = new LatLng(39.97923, 116.357428);
    LatLng p2 = new LatLng(39.94923, 116.397428);
    LatLng p3 = new LatLng(39.97923, 116.437428);

    List<LatLng> points = new ArrayList<>();

    Polyline mPolyline;


    private void initLocationOption() {
        locationClient = new LocationClient(getApplicationContext());
        locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);

        setLocationOptions();

    }

    private void setLocationOptions() {
        // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备。
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd0911.
        // locationOption.setCoorType("gcj02");
        // 可选，默认0，即仅定位一次，设置发起连续定位的请求间隔需要>=1000ms才有效。
        locationOption.setScanSpan(1000);
        // 可选，设置是否需要地址信息，默认不需要。
        locationOption.setIsNeedAddress(true);
        // 可选，设置是否需要地址描述。
        locationOption.setIsNeedLocationDescribe(true);
        // 可选，设置是否需要设备方向结果。
        locationOption.setNeedDeviceDirect(false);

        locationOption.setLocationNotify(true);
        locationOption.setIgnoreKillProcess(true);
        locationOption.setIsNeedLocationDescribe(true);
        locationOption.setIsNeedLocationPoiList(true);
        locationOption.setOpenGps(true);

        locationOption.setIsNeedAltitude(false);
        locationOption.setOpenAutoNotifyMode();
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果。
            Log.d(TAG, "latitude = " + bdLocation.getLatitude());
            Log.d(TAG, "longitude = " + bdLocation.getLongitude());
            Log.d(TAG, "country code = " + bdLocation.getCountryCode());
            Log.d(TAG, "country = " + bdLocation.getCountry());
            Log.d(TAG, "city = " + bdLocation.getCity());
            Log.d(TAG, "street = " + bdLocation.getStreet());
            Log.d(TAG, "addr = " + bdLocation.getAddrStr());
            Log.d(TAG, "description = " + bdLocation.getLocationDescribe());

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);
        }
    }

    public class MyNotifyListener extends BDNotifyListener {
        @Override
        public void onNotify(BDLocation bdLocation, float v) {
            Log.d(TAG, "onNotify");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);

        // After android m, must request permission on runtime.
        getPermissions();

        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        initLocationOption();

        locationClient.registerNotify(notifyListener);

        notifyListener.SetNotifyLocation(40.013f, 116.362f, 3000,
                locationClient.getLocOption().getCoorType());

        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker, accuracyCircleFillColor, accuracyCircleStrokeColor
        ));

        mBaiduMap.setMyLocationEnabled(true);

        points.add(p1);
        points.add(p2);
        points.add(p3);

    }

    @Override
    protected void onStart() {
        super.onStart();


        locationClient.start();

        // 绘制折线
        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(0xAAFF0000).points(points);

        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);

        mBaiduMap.addOverlay(textOption);

    }

    private void getPermissions() {

        List<String> permissions = new ArrayList<>();

        // 定位权限为必须权限，如果用户禁止，则每次进入都会申请。
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        /*
         * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
         */
        if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
        }
        // 读取电话状态权限
        if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
            permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
        }

        if (permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
        }

    }

    private boolean addPermission(List<String> permissionList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
