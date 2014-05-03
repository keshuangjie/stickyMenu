package com.whutec.stickymenudemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * @author Jimmy
 * @mail keshuangjie@gmail.com
 * @date 2014-5-1 下午9:44:45
 * @description listview header
 */
public class MyHeaderView extends LinearLayout {
	private static final String TAG = MyHeaderView.class.getName();

	private LinearLayout mContainer;
	private ProgressBar mProgressBar;
	private int mState = STATE_NORMAL;

	private Context mContext;

	public ImageView indicator;
	public View headerLayout;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	public MyHeaderView(Context context) {
		super(context);
		this.mContext = context;
	}

	public MyHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public void initView() {

		View view = (LinearLayout) LayoutInflater.from(mContext).inflate(
				R.layout.header_view_show, null);
		mContainer = (LinearLayout) view;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(view, lp);
		setGravity(Gravity.BOTTOM);
		mProgressBar = (ProgressBar) findViewById(android.R.id.progress);

		headerLayout = findViewById(R.id.header_layout);

		initImageView();
	}

	int mIndicatorWidth;//滑动条宽度
	int offset; //单位偏移量
	int one; 

	private void initImageView() {
		indicator = (ImageView) findViewById(R.id.im_indicator);
		int screenW = DPIUtil.getWidth(mContext);
		mIndicatorWidth = mContext.getResources().getDimensionPixelSize(
				R.dimen.width_my_indicator);
		offset = (screenW / 2 - mIndicatorWidth) / 2;
		one = 2 * offset + mIndicatorWidth;
		initSlide();
	}

	/**
	 * TODO 滑动条移动
	 * @param fromIndex
	 * @param toIndex
	 */
	public void slideAnimation(int fromIndex, int toIndex) {
		Animation animation = new TranslateAnimation(one * fromIndex + offset,
				one * toIndex + offset, 0, 0);
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(200);
		indicator.startAnimation(animation);
	}

	public void initSlide() {
		Animation animation = new TranslateAnimation(offset, offset, 0, 0);// 显然这个比较简洁，只有一行代码。
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(0);
		indicator.startAnimation(animation);
	}

	public void initDefaultView() {

		View view = (LinearLayout) LayoutInflater.from(mContext).inflate(
				R.layout.header_view_hide, null);
		mContainer = (LinearLayout) view;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(view, lp);
		setGravity(Gravity.BOTTOM);
	}

	public void initData() {
		this.setVisibility(View.VISIBLE);
	}

	public void setState(int state) {
		if (state == mState)
			return;

		switch (state) {
		case STATE_NORMAL:
			mProgressBar.setVisibility(View.GONE);
			break;
		case STATE_READY:
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		case STATE_REFRESHING:
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		default:
		}

		mState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
		postInvalidate();
	}

	public int getVisiableHeight() {
		Log.i(TAG,
				"getVisiableHeight() -> visiableHeight"
						+ mContainer.getHeight());
		return mContainer.getHeight();
	}

}
