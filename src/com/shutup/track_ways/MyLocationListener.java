package com.shutup.track_ways;

import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.shutup.track_ways.MainActivity.MyHandler;

public class MyLocationListener implements BDLocationListener {

	private MyHandler mHandler;
	public MyLocationListener(MyHandler mHandler) {
		// TODO Auto-generated constructor stub
		this.mHandler=mHandler;
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		// TODO Auto-generated method stub
		if ((location == null))
		{  return ;
		
		}
		int temp=location.getLocType();
		switch(temp)
		{
		case 62:
		case 63:
		case 67:
		case 162:
		case 163:
		case 164:
		case 165:
		case 166:
		case 167:
			return;
		default:
			Message msg=mHandler.obtainMessage();
			msg.obj=location;
			msg.what=0;
			mHandler.sendMessage(msg);
			location=null;
			break;
		}
		
		
	}

		
}
