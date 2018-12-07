package com.lai.geolocation.baidu;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.lai.geolocation.w3.PositionOptions;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class BDGeolocation {

	private String TAG = "BDGeolocation";
	private LocationClient client;

	public static final String COORD_BD09LL = "bd09ll";
	public static final String COORD_BD09 = "bd09";
	public static final String COORD_GCJ02 = "gcj02";

	private BDLocationListener listener;

	BDGeolocation(Context context) {
		client = new LocationClient(context);
	}

	private void setOptions(PositionOptions options) {
		// set default coorType
		String coorType = options.getCoorType();
		if (coorType == null || coorType.trim().isEmpty()) {
			coorType = COORD_GCJ02;
		}

		// set default locationMode
		LocationMode locationMode = LocationMode.Battery_Saving;
		if (options.isEnableHighAccuracy()) {
			locationMode = LocationMode.Hight_Accuracy;
		}

		LocationClientOption bdoptions = new LocationClientOption();
		bdoptions.setCoorType(coorType);
		bdoptions.setLocationMode(locationMode);
		bdoptions.setOpenAutoNotifyMode();
		// bdoptions.setOpenAutoNotifyMode(5000, 2,
		// LocationClientOption.LOC_SENSITIVITY_MIDDLE);
		bdoptions.setIsNeedAddress(true);
		client.setLocOption(bdoptions);
	}

	public boolean getCurrentPosition(PositionOptions options,
			final BDLocationListener callback) {
		listener = new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				callback.onReceiveLocation(location);
				clearWatch();
			}
		};
		setOptions(options);
		client.registerLocationListener(listener);
		client.start();
		return true;
	}

	public boolean watchPosition(PositionOptions options,
			BDLocationListener callback) {
		listener = callback;
		setOptions(options);
		client.registerLocationListener(listener);
		client.start();
		return true;
	}

	public boolean clearWatch() {
		client.stop();
		client.unRegisterLocationListener(listener);
		listener = null;
		return true;
	}
	/**
	 * 开启前台定位服务
	 * @return
	 */
	public boolean openFrontLocationService() {

		Activity mainActivity = new Activity();

		Notification.Builder builder = new Notification.Builder(mainActivity.getApplicationContext());
		// 获取一个Notification构造器
		Intent nfIntent = new Intent(mainActivity.getApplicationContext(),
				Activity.class);
		builder.setContentIntent(
				PendingIntent.getActivity(mainActivity, 0, nfIntent, 0)) // 设置PendingIntent
				.setContentTitle("正在进行后台定位") // 设置下拉列表里的标题
//				.setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
				.setContentText("后台定位通知") // 设置上下文内容
				.setAutoCancel(true)
				.setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
		Notification notification = null;
		notification = builder.build();
		notification.defaults = Notification.DEFAULT_SOUND; // 设置为默认的声音
		client.enableLocInForeground(1001, notification);// 调起前台定位
		return true;
	}
	/**
	 * 关闭前台定位服务，同时移除通知栏
	 * @return
	 */
	public boolean closeFrontLocationService() {
		client.disableLocInForeground(true);
		return true;
	}

}
