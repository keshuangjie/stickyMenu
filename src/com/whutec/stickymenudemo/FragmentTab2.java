package com.whutec.stickymenudemo;

import java.util.ArrayList;

import android.os.Bundle;

public class FragmentTab2 extends MyListFragment<User>{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public static FragmentTab2 newInstance(){
		FragmentTab2 fragment = new FragmentTab2();
		return fragment;
	}
	
	@Override
	protected void addHeaderView() {
		MyHeaderView mHeaderView = new MyHeaderView(getActivity());
		mHeaderView.initDefaultView();
		MyListView listView = (MyListView) mListView;
		listView.setHeaderView(mHeaderView);
		listView.setPullRefreshListener(this);
		if(mHeaderScrollListener != null){
			listView.setOnHeadViewScrollerListener(mHeaderScrollListener);
		}
	}
	
	@Override
	protected ArrayList<User> makeData(int type) {
		User lastUser = null;
		if(type == MESSAGE_LOAD_MORE){
			lastUser = getLastItem();
		}
		return User.makeUers(lastUser, 0);
	}
}
