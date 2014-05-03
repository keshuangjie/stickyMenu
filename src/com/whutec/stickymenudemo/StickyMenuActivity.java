package com.whutec.stickymenudemo;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

public class StickyMenuActivity extends FragmentActivity implements OnClickListener{
	private static final String TAG = StickyMenuActivity.class.getName();
	
	private static final int STATE_FIRST = 0;
	private static final int STATE_SECOND = 1;
	
	TextView tv_first, tv_second;
	StickyScrollManager mScrollManager;
	
	MyHeaderView mHeaderView;
	
	FrameLayout mRootView;
	
	View mContentLayout;
	
	int mCurrentItem = 0;
	
	ArrayList<MyListFragment<User>> fragments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_main);
		mRootView = (FrameLayout) findViewById(R.id.fl_root);
		
		initView();
	}
	
	private void initView(){
		
		mContentLayout = findViewById(R.id.rl_content);
		
		initHeaderView();
		
		mScrollManager = new StickyScrollManager(this, mHeaderView, mHeaderView.indicator);
		
		fragments = new ArrayList<MyListFragment<User>>();
		FragmentTab2 fragment1 = FragmentTab2.newInstance();
		fragment1.setOnHeadViewScrollerListener(mScrollManager, STATE_FIRST);
		FragmentTab1 fragment2 = new FragmentTab1();
		fragment2.setOnHeadViewScrollerListener(mScrollManager, STATE_SECOND);
		fragments.add(fragment1);
		fragments.add(fragment2);
		
		initFirstFragment(STATE_FIRST);
	}
	
	private void initHeaderView(){
		mHeaderView = new MyHeaderView(this); 
		mHeaderView.initView();
		mHeaderView.setVisibility(View.VISIBLE);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mRootView.addView(mHeaderView, lp);
		
		tv_first = (TextView) mHeaderView.findViewById(R.id.tv_origin);
		tv_second = (TextView) mHeaderView.findViewById(R.id.tv_favorite);
		tv_first.setTag(STATE_FIRST);
		tv_second.setTag(STATE_SECOND);
		tv_first.setOnClickListener(this);
		tv_second.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		int index = (Integer) v.getTag();
		if(index == mCurrentItem){
			return;
		}
		mHeaderView.slideAnimation(mCurrentItem, index);
		setState(index);
		mScrollManager.setTabIndex(index);
		switchFragment(mCurrentItem, index);
		mCurrentItem = index;
	}
	
	private void setState(int index){
		switch (index) {
		case STATE_FIRST:
			tv_first.setSelected(true);
			tv_second.setSelected(false);
			break;
		case STATE_SECOND:
			tv_first.setSelected(false);
			tv_second.setSelected(true);
			break;
		default:
			break;
		}
	}
	
	private MyListFragment<User> mCurrentFragment;
	
	/** 初始加载 */
	private void initFirstFragment(int index){
		mCurrentItem = index;
		setState(mCurrentItem);
		mCurrentFragment = fragments.get(mCurrentItem);
		getSupportFragmentManager().beginTransaction().
			setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
			add(R.id.rl_content, mCurrentFragment).commit();
	}
	
	/** fragment切换 */
	public void switchFragment(int fromId, int toId) {
		MyListFragment<User> from = null;
		from = fragments.get(fromId);
		MyListFragment<User> to = fragments.get(toId);
		if (mCurrentFragment != to) {
			mCurrentFragment = to;
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			if(from != null){
				transaction.hide(from);
			}
			if (!to.isAdded()) { // 先判断是否被add过
				transaction.add(R.id.rl_content, to).commit(); // 隐藏当前的BaseFragment，add下一个到Activity中
			} else {
				transaction.show(to).commit(); // 隐藏当前的BaseFragment，显示下一个
			}
		}
	}

}
