package com.whutec.stickymenudemo;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author Jimmy
 * @mail keshuangjie@gmail.com
 * @date 2014-5-1 下午10:32:56
 * @description 获取屏幕宽高、密度信息工具类
 */
public class DPIUtil {

	private static float mDensity = DisplayMetrics.DENSITY_DEFAULT;
	private static Display defaultDisplay;

	public static void setDensity(float density) {
		mDensity = density;
	}
	public static float getDensity() {
		return mDensity;
	}
	public static Display getDefaultDisplay(Context context) {
		if (null == defaultDisplay) {
			WindowManager systemService = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			defaultDisplay = systemService.getDefaultDisplay();
		}
		return defaultDisplay;
	}

	public static int percentWidth(float percent, Context context) {
		return (int) (getWidth(context) * percent);
	}

	public static int percentHeight(float percent, Context context) {
		return (int) (getHeight(context) * percent);
	}

	public static int dip2px(float dipValue) {
		return (int) (dipValue * mDensity + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		return (int) (pxValue / mDensity + 0.5f);
	}

	public static int getWidth(Context context) {
		return getDefaultDisplay(context).getWidth();
	}

	public static int getHeight(Context context) {
		return getDefaultDisplay(context).getHeight();
	}
	
	public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  

}
