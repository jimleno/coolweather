package com.zyb.coolweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
/**
 * ��дViewPager  ��Ҫ��һ���л������Ĺ��� 
 * @author Administrator
 *
 */
public class GuideViewPager extends ViewPager {

	private Bitmap bg;
	private Paint b = new Paint(1);
	
	public GuideViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GuideViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(this.bg != null){
			int width = this.bg.getWidth();
			int height = this.bg.getHeight();
			int count = getAdapter().getCount();
			int x =getScrollX();
			// ��View�б���ͼƬ��Ҫ��ʾ�Ŀ�ȣ��Ŵ󱳾�ͼ����С����ͼ��
			int n = height *getWidth()/getHeight();
			 /** 
             * (width - n) / (count - 1)��ʾ��ȥ��ʾ��һ��ViewPagerҳ����ȥ�ı�����ȣ�ʣ���ViewPager��Ҫ��ʾ�ı���ͼƬ�Ŀ�ȡ� 
             * getWidth()����ViewPagerһ��ҳ��Ŀ�ȣ����ֻ���Ļ��ȡ��ڸü����п������Ϊ����һ��ViewPagerҳ����Ҫ����������ֵ�� 
             * ((width - n) / (count - 1)) /getWidth()Ҳ�ͱ�ʾViewPager����һ������ʱ������ͼƬ�����Ŀ�ȡ� 
             * x * ((width - n) / (count - 1)) /  getWidth()Ҳ�ͱ�ʾViewPager����x������ʱ������ͼƬ�����Ŀ�ȡ� 
             * ����ͼƬ�����Ŀ�ȵĿ�ȿ������Ϊ����ͼƬ���������λ�á� 
             */  
			int w = x*((width -n)/(count -1))/getWidth();
			canvas.drawBitmap(bg, new Rect(w,0,n+w,height),new Rect(x,0,x+getWidth(),getHeight()),this.b);
		}
		
		super.dispatchDraw(canvas);
	}

	public void setBackGroud(Bitmap paramBitmap){
		this.bg = paramBitmap;
		this.b.setFilterBitmap(true);
	}
}
