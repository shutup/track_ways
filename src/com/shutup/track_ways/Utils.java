package com.shutup.track_ways;

import com.baidu.location.BDLocation;

public class Utils {
	public static String parseBDLocation(BDLocation location)
	{
		StringBuffer sb = new StringBuffer(256);
		sb.append("\ntime : ");
		sb.append(location.getTime());
		sb.append("\ncode : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		sb.append("\ncity : ");
		sb.append(location.getCity());
		sb.append("\ncity code: ");
		sb.append(location.getCityCode());
		
		sb.append("\nspeed : ");
		sb.append(location.getSpeed());
		sb.append("\nsatellite : ");
		sb.append(location.getSatelliteNumber());
		sb.append("\naddr : ");
		sb.append(location.getAddrStr());
		sb.append("\ndir : ");
		sb.append(location.getDirection());
		return sb.toString();
	}
}
