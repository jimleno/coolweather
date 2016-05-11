package com.zyb.coolweather;


import java.util.ArrayList;
import java.util.List;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zyb.coolweather.activity.WeatherActivity;
import com.zyb.coolweather.utils.MyAdapter;
import com.zyb.coolweather.utils.MyBaseFragment;



public class LoginActivity extends FragmentActivity {

	private GuideViewPager vPager;
	private List<MyBaseFragment> list = new ArrayList<MyBaseFragment>();
	private MyAdapter mAdapter;
	
	private ImageView[] tips;
	private int currentSelect;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences pfs = PreferenceManager.getDefaultSharedPreferences(this);
		String weather_desp = pfs.getString("weather_desp","");
		
		if(weather_desp != null && weather_desp.length() > 0 ){
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		     }
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wealcome);
		final ViewGroup group = (ViewGroup) this.findViewById(R.id.viewgroup);
		
		new Thread(){

			@Override
			public void run() {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						tips = new ImageView[2];
						for(int i=0;i<tips.length;i++){
							ImageView imageview = new ImageView(LoginActivity.this);
							imageview.setLayoutParams(new LayoutParams(10,10));
							if(i==0){
								imageview.setBackgroundResource(R.drawable.page_indicator_focused);
								
							}else{
								imageview.setBackgroundResource(R.drawable.page_indicator_unfocused);
							}
							
							tips[i] = imageview;
							LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
									(new ViewGroup.LayoutParams
											(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
							layoutParams.leftMargin = 20;
							layoutParams.rightMargin = 20;
							group.addView(imageview,layoutParams);
							}
						
					}});
				
				super.run();
			}
			
		}.start();
		
		
		vPager = (GuideViewPager) this.findViewById(R.id.view_pager);
		
		
		
		LastFragment lastFragment = new LastFragment();
		FirstFragment firstFragment = new FirstFragment();
		list.add(firstFragment);
		
		list.add(lastFragment);
		
		mAdapter = new MyAdapter(getSupportFragmentManager(),list);
		vPager.setAdapter(mAdapter);
		vPager.setOffscreenPageLimit(2);
		vPager.setCurrentItem(0);
		vPager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int index) {
				// TODO Auto-generated method stub
				setImageBackgroud(index);
				MyBaseFragment fragment = list.get(index);
				list.get(currentSelect).stopAnimation();
				fragment.startAnimation();
				currentSelect = index;
			}
			
		});
	}

	protected void setImageBackgroud(int index) {
		// TODO Auto-generated method stub
		for(int i=0;i<tips.length;i++){
			if(i==index){
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
				
			}else{
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
		}
	}
}
