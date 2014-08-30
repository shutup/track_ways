package com.shutup.track_ways;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	 int screenWidth = 0;
	 int screenHeight = 0;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener ;
	private BitmapDescriptor mCurrentMarker;
	private BitmapDescriptor startPosMarker;
	private OverlayOptions start_mark=null;
	private OverlayOptions stop_mark=null;
	//*********************************************
	LatLng pt1=null;
	LatLng pt2=null;
	LatLng pt3=null;
	LatLng pt4=null;
	
	List<LatLng> pts = null;  
	OverlayOptions polygonOption=null;
	//********************************************
	LocationClientOption option=null;
	BDLocation location=null;
	MapView mapView=null;
	BaiduMap mBaiduMap=null;
	MyLocationData locData=null;
	MyHandler mHandler=null;
	TextView location_info=null;
	TextView current_info=null;
	TextView pos_info=null;
	TextView total_info=null;
	Button btn_start=null;
	Button btn_stop=null;
	private boolean isFirst=true;
	private ArrayList<LatLng> points=null;
	private LatLng start_point=null;
	private LatLng current_point=null;
	private LatLng before_current_point=null;
	private double current_distance=0;
	private double total_distance=0;
	private SharedPreferences data=null;
	private String DATA="data";
	private String SETTING="set";
	
	private float ZOOM_LEVEL= 19.0f;
	private float FUZZY=10; 
	private int filter_num =0;
	private ArrayList<LatLng> filter_array=null;
	private OverlayOptions line=null;;
	private boolean filtered=false;
	private int FILTER_FUZZY=5;
	
	private float pos_count=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//baidu said it is necessary to initial here
		SDKInitializer.initialize(getApplicationContext());
		//set the no title theme
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//get the screen info
		 
			 DisplayMetrics dm  = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(dm);
	        screenWidth = dm.widthPixels;
	        screenHeight = dm.heightPixels;
	        Log.d("Screen", "("+screenWidth+","+screenHeight+")");
		 
		data =getSharedPreferences(DATA,Context.MODE_PRIVATE );
		
		setContentView(R.layout.activity_main);
		//when you touch outside the activity ,system will not hide the app
		setFinishOnTouchOutside(false);
		
		mHandler =new MyHandler(Looper.myLooper());
		myListener = new MyLocationListener(mHandler);
		
		points=new ArrayList<LatLng>();
		filter_array=new ArrayList<LatLng>();
	    mLocationClient = new LocationClient(getApplicationContext()); 
	    mLocationClient.registerLocationListener( myListener );  
	    option = new LocationClientOption();
	    option.setLocationMode(LocationMode.Hight_Accuracy);
	    option.setCoorType("bd09ll");
	    option.setScanSpan(3000);
	    option.setIsNeedAddress(true);
	    option.setNeedDeviceDirect(true);
	    mLocationClient.setLocOption(option);
	 
	    mapView=(MapView) findViewById(R.id.bmapView);
		mBaiduMap = mapView.getMap();
		
		//覆盖了大部分世界
			 pt1 = new LatLng(83.946149,-182.981267);  
			 pt2 = new LatLng(84.130077,146.698293);  
			 pt3 = new LatLng(-78.479528,140.811158);  
			 pt4 = new LatLng(-78.826638,-186.513548);  
			 pts= new ArrayList<LatLng>();
			pts.add(pt1);  
			pts.add(pt2);  
			pts.add(pt3);  
			pts.add(pt4);  
			polygonOption = new PolygonOptions()  
		    .points(pts)  
		    .stroke(new Stroke(100, 0x77FFFF00))  
		    .fillColor(0x77FFFF00);  
			mBaiduMap.clear();
			mBaiduMap.addOverlay(polygonOption);
		
//		ViewTreeObserver	 vto =mapView.getViewTreeObserver(); 
//		OnPreDrawListener listener=new OnPreDrawListener() {
//			@Override
//			public boolean onPreDraw() {
//				// TODO Auto-generated method stub
//				screenHeight =mapView.getMeasuredHeight(); 
//				screenWidth =mapView.getMeasuredWidth();   
//				Log.d("Screen", "("+screenWidth+","+screenHeight+")");
//				return true;
//			}
//		};
		//vto.addOnPreDrawListener(listener);
		//vto.removeOnPreDrawListener(listener);
//		vto=null;

		
//		Point p=new Point();
//     不知道为啥，这种方法拿不到view的大小
//		int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//		int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//		mapView.measure(width,height);
//		p.x=mapView.getMeasuredWidth();
//		p.y=mapView.getMeasuredHeight();
//		Log.d("screen", "("+p.x+","+p.y+")");
		{
			// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）  
			mCurrentMarker = BitmapDescriptorFactory  
			    .fromResource(R.drawable.a1);   
			startPosMarker = BitmapDescriptorFactory  
				    .fromResource(R.drawable.icon_st);   
		}
		
		location_info=(TextView) findViewById(R.id.location_info);
		current_info=(TextView) findViewById(R.id.current_info);
		total_info=(TextView) findViewById(R.id.total_info);
		pos_info =(TextView) findViewById(R.id.pos_info);
		btn_start=(Button) findViewById(R.id.btn_start);
		btn_stop=(Button) findViewById(R.id.btn_stop);
		btn_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLocationClient.start();
				if(data.getFloat("total_distance", 0)!=0)
				{
					total_distance=data.getFloat("total_distance", -0.1f);
				}
				else
				{
					total_distance=0;
				}
				current_info.setText(getResources().getText(R.string.current_info)+" "+(int)current_distance +"m");
				total_info.setText(getResources().getText(R.string.total_info)+" "+(int)total_distance +"m");
			}
		});
		btn_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				mLocationClient.stop();
				location_info.setText("");
				SharedPreferences.Editor editor = data.edit();
				editor.putFloat("total_distance", (float) total_distance);
				editor.commit();
				//current_distance=0;
			}
		});
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapView.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mapView.onPause();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mapView.onDestroy();
		SharedPreferences.Editor editor = data.edit();
		editor.putFloat("total_distance", (float) total_distance);
		editor.commit();
	}
	
	/** 
	* 从SD卡导入离线地图安装包 
	*/  
	public void importFromSDCard(View view) {  
	    MKOfflineMap mOffline = null;
		int num = mOffline.importOfflineData();  
	    String msg = "";  
	    if (num == 0) {  
	        msg = "没有导入离线包，这可能是离线包放置位置不正确，或离线包已经导入过";  
	    } else {  
	        msg = String.format("成功导入 %d 个离线包", num);  
	    }  
	    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();  
	}
	//the handler to process the location data returned by the baidu Loc SDK
	public   class MyHandler extends Handler{
		private Overlay garbage=null;
		//the constructor
		public MyHandler(Looper myLooper) {
			// TODO Auto-generated constructor stub
		}
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==0)
			{
				pos_count++;
				pos_info.setText(getResources().getText(R.string.pos_info)+""+pos_count);
				location=(BDLocation)msg.obj;
				location_info.setText("");
				location_info.setText(Utils.parseBDLocation(location));
				//Log.i("test", Utils.parseBDLocation(location));
				//if the app is first run,do this
				if(isFirst)
				{
					isFirst=false;
					start_point = new LatLng(location.getLatitude(),
							location.getLongitude());//(纬度，经度)
					points.add(start_point);
					before_current_point=start_point;
					current_point=start_point;
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(start_point, ZOOM_LEVEL);
					
					mBaiduMap.animateMapStatus(u);
					//*********************************
//					LatLng pt1 = new LatLng(start_point.latitude-15, start_point.longitude-15);  
//					LatLng pt2 = new LatLng(start_point.latitude-15, start_point.longitude+15);  
//					LatLng pt3 = new LatLng(start_point.latitude+15, start_point.longitude+15);  
//					LatLng pt4 = new LatLng(start_point.latitude+15, start_point.longitude-15);  
//					LatLng pt1 = new LatLng(56.347651,68.399397);  
//					LatLng pt2 = new LatLng(54.847817,140.516801);  
					//覆盖了全中国。
//					 pt1 = new LatLng(83.946149,-182.981267);  
//					 pt2 = new LatLng(84.130077,146.698293);  
//					 pt3 = new LatLng(-78.479528,140.811158);  
//					 pt4 = new LatLng(-78.826638,-186.513548);  
//					pts.add(pt1);  
//					pts.add(pt2);  
//					pts.add(pt3);  
//					pts.add(pt4);  
//					polygonOption = new PolygonOptions()  
//				    .points(pts)  
//				    .stroke(new Stroke(50, 0xaaFFFF00))  
//				    .fillColor(0xaaFFFF00);  
					//*********************************
					//构建MarkerOption，用于在地图上添加Marker  
					start_mark = new MarkerOptions()
				    .position(start_point)  
				    .icon(startPosMarker);  
					mBaiduMap.clear();
					
					mBaiduMap.addOverlay(start_mark);
					mBaiduMap.addOverlay(polygonOption);
				}
				//else do this 
				else
				{
					//定义Maker坐标点  
					//if the pos for filter is collected enough, then do this
					if(filtered)
					{
						float lat=0,lng=0;
						Log.i("test", "length:"+filter_array.size());
						for(int i=0;i<filter_array.size();i++)
						{
							lat +=filter_array.get(i).latitude;
							lng +=filter_array.get(i).longitude;
						}
						lat=lat/(filter_array.size());
						lng=lng/(filter_array.size());
						Log.d("filter", "(lat: "+lat+"--"+"lng: "+lng+")");
						//setup the filtered pos data
						LatLng temp=new LatLng(lat, lng);
						filter_array.clear();
						points.add(temp);
						current_point=temp;
						before_current_point=points.get(points.size()-2);
						//get the distance between the two point
						Double dis=DistanceUtil.getDistance(current_point, before_current_point);
						Log.d("dis", "dis: "+dis);
						current_distance+=dis;
						total_distance+=dis;
						current_info.setText(getResources().getText(R.string.current_info)+" "+(int)current_distance +"m");
						total_info.setText(getResources().getText(R.string.total_info)+" "+(int)total_distance +"m");
						//绘制路线轨迹
						line = new PolylineOptions().width(5)
								.color(0xFF00FF00).points(points);
						//构建MarkerOption，用于在地图上添加Marker  
						stop_mark = new MarkerOptions()
					    .position(current_point)  
					    .icon(mCurrentMarker); 
						if(garbage!=null)
						{
							garbage.remove();
						}
						 
						//在地图上添加Marker，并显示
						mBaiduMap.clear();
						
						garbage=mBaiduMap.addOverlay(start_mark);
						mBaiduMap.addOverlay(line);
						mBaiduMap.addOverlay(stop_mark);
						mBaiduMap.addOverlay(polygonOption);
						location=null;
						filtered=false;
					}
					//else collect the pos data
					else
					{
						LatLng point = new LatLng(location.getLatitude(),location.getLongitude());

							mBaiduMap.clear();
							mBaiduMap.addOverlay(start_mark);
							mBaiduMap.addOverlay(polygonOption);
							
							if(line!=null)
							{
							 mBaiduMap.addOverlay(line);
							 mBaiduMap.addOverlay(stop_mark);
							}
							
						
						filter_array.add(point);
						filter_num++;
						if(filter_num==FILTER_FUZZY)
						{
							filter_num=0;
							filtered=true;
						}
					}
					
				}
				
			}
		}
	}
}
