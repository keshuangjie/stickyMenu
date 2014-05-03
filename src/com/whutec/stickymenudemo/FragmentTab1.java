package com.whutec.stickymenudemo;

import java.util.ArrayList;

import android.os.Bundle;

public class FragmentTab1 extends MyListFragment<User>{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void addHeaderView() {
		MyHeaderView mHeaderView = new MyHeaderView(getActivity());
		mHeaderView.initDefaultView();
		MyListView listView = (MyListView) mListView;
		listView.setHeaderView(mHeaderView);
		listView.setPullRefreshListener(this);
		if (mHeaderScrollListener != null) {
			listView.setOnHeadViewScrollerListener(mHeaderScrollListener);
		}
	}

	@Override
	protected ArrayList<User> makeData(int type) {
		User lastUser = null;
		if(type == MESSAGE_LOAD_MORE){
			lastUser = getLastItem();
		}
		return User.makeUers(lastUser, 1);
	}

}
