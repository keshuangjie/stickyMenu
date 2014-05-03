package com.whutec.stickymenudemo;

import com.nineoldandroids.animation.ObjectAnimator;
import com.whutec.stickymenudemo.MyListFragment.OnHeadViewScrollerListener;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * 用于滑动时固定HeaderView的某部分容器
 * 
 * @author keshuangjie
 */
public class StickyScrollManager implements OnHeadViewScrollerListener {
	private static final String TAG = StickyScrollManager.class.getName();

	private Context mContext;

	// 当前的页面索引
	private int mIndex;
	// 滑动时固定在上面的容器
	private View mTabView;
	// 相当于HeaderView
	private MyHeaderView mHeaderView;
	
	private int mMaxScrollHeight;
	
	private int mScrollValue;
	
	private int mScrollY[] = {1, 1}; 
	
	public StickyScrollManager(Context context, MyHeaderView headerView, View tabView) {
		this.mContext = context;
		this.mHeaderView = headerView;
		this.mTabView = tabView;
		
		mMaxScrollHeight = context.getResources().getDimensionPixelSize(R.dimen.my_header_height_really);
	}

	@Override
	public void onHeaderScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount, int index) {
		
		Log.i(TAG, "onHeaderScroll -> firstVisibleItem: " + firstVisibleItem);
		
		if (mTabView == null || mIndex != index || mHeaderView == null) {
			return;
		}
		
		if(restoreListView((ListView) view)){
			return ;
		}
		
		if (firstVisibleItem != 0) {
			startAnimation(-mMaxScrollHeight);
			return;
		}

		final View topView = view.getChildAt(firstVisibleItem);
		if (topView == null) {
			startAnimation(0);
			return;
		}

		int y = topView.getTop() > 0 ? 0 : topView.getTop();
		
		Log.i(TAG, "onHeaderScroll -> topView y: " + y);
		
		Log.i(TAG, "onHeaderScroll -> topView height: " + topView.getHeight());
		Log.i(TAG, "onHeaderScroll -> mHeaderView height: " + mHeaderView.getHeight());
		
		int moveToY = Math.max(y, -mMaxScrollHeight);
		
		startAnimation(moveToY);
		
		if(mHeaderView != null){
			Log.i(TAG, "onHeaderScroll -> header height: " + mHeaderView.getVisiableHeight());
		}
	}
	
	@Override
	public void onHeaderPull(int height) {
		if(mHeaderView != null){
			mHeaderView.setVisiableHeight(height);
		}
	}

	public void setTabIndex(int index) {
		this.mIndex = index;
	}
	
	public int getScrollY(){
		return mScrollY[mIndex];
	}

	/**
	 * 切换时保持可见headerView的位置不动
	 * @param listView
	 * @return
	 */
	public boolean restoreListView(final ListView listView) {
		final int lastIndex = 1 - mIndex;
		mScrollValue = -mScrollY[lastIndex];
		if(mScrollValue >= 0){
			 int firstVisiablePosition = listView.getFirstVisiblePosition();
			 if(firstVisiablePosition > 0 && mScrollValue >= mMaxScrollHeight){
				 mScrollY[lastIndex] = 1;
				 return true;
			 }
			 
			 listView.post(new Runnable() {
				
				@Override
				public void run() {
					listView.setSelectionFromTop(0, -mScrollValue);
				}
			});
			 mScrollY[lastIndex] = 1;
			 return true;
		}
		return false;
	}

	/**
	 * headerView滑动动画，支持1.6+版本
	 * HONEYCOMB以上版本使用动画滑动，HONEYCOMB一下版本更改padding实现
	 * @param value
	 */
	public void startAnimation(int value) {
		if (mHeaderView == null) {
			return;
		}
		
		mScrollY[mIndex] = value;
		
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Interpolator interpolator = AnimationUtils
					.loadInterpolator(mContext,
							android.R.anim.accelerate_decelerate_interpolator);
			ObjectAnimator animator = ObjectAnimator.ofFloat(mHeaderView, "y",
					value);
			animator.setInterpolator(interpolator);
			animator.setDuration(0);
			animator.start();
		} else {
			mHeaderView.setPadding(0, value, 0, 0);
		}
	}

	@Override
	public void setState(int state) {
		if(mHeaderView != null){
			mHeaderView.setState(state);
		}
	}
}
