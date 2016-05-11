package com.zyb.coolweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zyb.coolweather.utils.MyBaseFragment;

public class FirstFragment extends MyBaseFragment {

	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootview = inflater.inflate(R.layout.fragment_first, null);
		return rootview;
	}

	@Override
	public void startAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopAnimation() {
		// TODO Auto-generated method stub

	}

}
