package cn.xiayiye5.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MyLocationData;

public class MyLocationListener extends MainActivity implements
		BDLocationListener {

	@Override
	public void onReceiveLocation(BDLocation location_result) {
		// 拿到定位的经纬度
		double latitude = location_result.getLatitude();
		double longitude = location_result.getLongitude();
		MyLocationData location_data = new MyLocationData.Builder()
				.latitude(latitude).longitude(longitude).build();
		// 在定位生效之前调用下面一句。地图层生效
		map.setMyLocationEnabled(true);// 打开图层
		map.setMyLocationData(location_data);
	}

	@Override
	protected void onStart() {
		// 开启定位
		mLocationClient.start();
		super.onStart();
	}

	@Override
	protected void onPause() {
		// 失去焦点的时候，停止定位，省电，省流量
		mLocationClient.stop();
		super.onPause();
	}
}
