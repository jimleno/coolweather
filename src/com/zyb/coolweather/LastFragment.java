package com.zyb.coolweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.zyb.coolweather.activity.ChooseAreaActivity;
import com.zyb.coolweather.utils.MyBaseFragment;



public class LastFragment extends MyBaseFragment {

	private static final float ZOOM_MAX = 1.3f;
	private static final float ZOOM_MIN = 1.0f;
	
	private ImageView imgView;

	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stereoscopic_launcher, null);
		imgView = (ImageView) rootView.findViewById(R.id.imgView_immediate_experience);
		imgView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),ChooseAreaActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();
			}
			
		});
		return rootView;
	}

	public void playAnimation(){
		final AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(new ScaleAnimation(ZOOM_MIN,ZOOM_MAX,ZOOM_MIN,ZOOM_MAX,
				Animation.RELATIVE_TO_SELF,0.5f,
				Animation.RELATIVE_TO_SELF,0.5f));
		animationSet.addAnimation(new AlphaAnimation(0.8f,1.0f));
		animationSet.setDuration(1000);
		animationSet.setInterpolator(new DecelerateInterpolator());
		animationSet.setFillAfter(false);
		//animationSet.setRepeatCount(Animation.INFINITE);// does not work
		animationSet.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				imgView.startAnimation(animationSet);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		imgView.startAnimation(animationSet);
	}
	
	@Override
	public void startAnimation() {
		// TODO Auto-generated method stub
		
		playAnimation();
	}

	@Override
	public void stopAnimation() {
		// TODO Auto-generated method stub

	}

}
