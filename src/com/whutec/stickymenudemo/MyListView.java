package com.whutec.stickymenudemo;

import com.whutec.stickymenudemo.MyListFragment.OnHeadViewScrollerListener;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * @author Jimmy
 * @mail keshuangjie@gmail.com
 * @date 2014-5-1 下午10:02:59
 * @description 重载ListView，支持下拉刷新，上滑加载更多（参考Maxwin）
 */
public class MyListView extends ListView {
	private static final String TAG = MyListView.class.getName();

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back

	private IPullRefresh mIPullRefresh;
	private OnHeadViewScrollerListener mOnHeadViewScrollerListener;

	private MyHeaderView mHeaderView;
	private RelativeLayout mHeaderViewContent;

	private int mHeaderViewReallyHeight; // headerView实际高度
	private int mHeaderViewDefaultHeight; // headerView默认显示高度
	private int mHeaderViewMarginTop; // headerView marginTop

	private boolean mEnablePullRefresh = true;//是否支持下拉刷新
	private volatile boolean mPullRefreshing = false; // is refreashing.

	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;

	private final static int SCROLL_DURATION = 0; // scroll back duration
													// at bottom, trigger
													// load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.
	public MyListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mHeaderViewMarginTop = -context.getResources().getDimensionPixelSize(
				R.dimen.margintop_my_header);

		mScroller = new Scroller(context, new DecelerateInterpolator());
	}
	
	public void setHeaderView(MyHeaderView headerView){
		// init header view
		mHeaderView = headerView;
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.header_layout);
		addHeaderView(mHeaderView);

		// init header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewDefaultHeight = mHeaderView.getHeight();
						mHeaderViewReallyHeight = mHeaderView.getHeight() + mHeaderViewMarginTop;
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}

				});
	}

	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		Log.i(TAG, "stopRefresh() -> mPullRefreshing: "
				+ mPullRefreshing);
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
			if(mOnHeadViewScrollerListener != null){
				mOnHeadViewScrollerListener.setState(MyHeaderView.STATE_NORMAL);
			}
		}
	}

	private void updateHeaderHeight(float delta) {
		int scrollY = getTop();
		Log.i(TAG, "updateHeaderHeight()-> scrollY: " + scrollY);
		if(scrollY > 0){
			return;
		}
		int headerTop = mHeaderView.getTop();
		Log.i(TAG, "updateHeaderHeight()-> headerTop: " + headerTop);
		
		if(headerTop < 0){
			return;
		}
		
		if(delta <= 0){
			return;
		}
		
		int endHeight = (int) delta + mHeaderView.getVisiableHeight();
		
		mHeaderView.setVisiableHeight(endHeight);
		
		if(mOnHeadViewScrollerListener != null){
			mOnHeadViewScrollerListener.onHeaderPull(endHeight);
		}

		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewReallyHeight) {
				if(mOnHeadViewScrollerListener != null){
					mOnHeadViewScrollerListener.setState(MyHeaderView.STATE_READY);
				}
			} else {
				if(mOnHeadViewScrollerListener != null){
					mOnHeadViewScrollerListener.setState(MyHeaderView.STATE_NORMAL);
				}
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (height <= mHeaderViewDefaultHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (height > mHeaderViewDefaultHeight) {
			finalHeight = mHeaderViewDefaultHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
				if(mOnHeadViewScrollerListener != null){
					mOnHeadViewScrollerListener.onHeaderPull(mScroller.getCurrY());
				}
			}
			postInvalidate();
		}
		super.computeScroll();
	}
	
	private float mDownY;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownY = ev.getY();
			mLastY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getY() - mLastY;
			mLastY = ev.getY();
			if (getFirstVisiblePosition() == 0) {
				if (mHeaderView.getVisiableHeight() >= mHeaderViewDefaultHeight || (deltaY > 0 && (mLastY-mDownY)>0 )){
					updateHeaderHeight(deltaY / OFFSET_RADIO);
				}
			}
			break;
		default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() >= mHeaderViewReallyHeight) {
					mPullRefreshing = true;
					if(mOnHeadViewScrollerListener != null){
						mOnHeadViewScrollerListener.setState(MyHeaderView.STATE_REFRESHING);
					}
					if (mIPullRefresh != null) {
						mIPullRefresh.onRefresh();
					}
				}
				resetHeaderHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 滑到ListView top, 显示headerViews刷新状态，并执行刷新动作
	 */
//	public void showHeaderAndRefresh() {
//		if (getFirstVisiblePosition() == 0) {
//			// invoke refresh
//			if (mEnablePullRefresh && !mPullRefreshing) {
//
//				int height = mHeaderView.getVisiableHeight();
//				if (height != 0) // not visible.
//					return;
//
//				mPullRefreshing = true;
//				mHeaderView.setState(MyStoryHeader.STATE_REFRESHING);
//
//				mScrollBack = SCROLLBACK_HEADER;
//				mScroller.startScroll(0, 0, 0, mHeaderViewReallyHeight,
//						SCROLL_DURATION);
//				// trigger computeScroll
//				invalidate();
//
//				if (mIPullRefresh != null) {
//					mIPullRefresh.onRefresh();
//				}
//			}
//
//		}
//	}

	public void setPullRefreshListener(IPullRefresh listener) {
		mIPullRefresh = listener;
	}
	
	public void setOnHeadViewScrollerListener(OnHeadViewScrollerListener listener){
		mOnHeadViewScrollerListener = listener;
	}

}
