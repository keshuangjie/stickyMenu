package com.whutec.stickymenudemo;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Jimmy
 * @mail keshuangjie@gmail.com
 * @date 2014-5-1 下午9:13:10
 * @description List布局封装基类
 */
public abstract class MyListFragment<T> extends Fragment implements
		OnScrollListener, IPullRefresh {
	protected static final String TAG = MyListFragment.class.getName();

	// private static final long REFRESH_INTERVAL_TIME = 1000;// 刷新间隔，防止多长点击刷新

	protected static final int MESSAGE_LOAD_FIRST = 0;
	protected static final int MESSAGE_PULL_REFRESH = 1; // 下拉刷新
	protected static final int MESSAGE_LOAD_MORE = 2; // 加载更多

	protected ListView mListView;
	protected View mRootView;
	protected MyFooterView mMoreView;
	protected View netErrorLayout;
	protected ViewGroup noDataLayout;
	protected ArrayList<T> mContents;
	protected int requestIndex = 0;
	
	protected MyAdapter<T> mAdapter;
	
	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContents = new ArrayList<T>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = onCreateView(inflater, container);

			initView();
		}

		return mRootView;
	}

	/**
	 * listView初始化
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	protected void initView() {
		mListView = (ListView) mRootView.findViewById(R.id.listView);
		if(mListView instanceof MyListView){
			((MyListView)mListView).setPullRefreshEnable(isPullRefreshEnable());
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		}
		addHeaderView();
		addFooterView();
		mAdapter = initAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(this);
	}

	protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.listview, container,
				false);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		initData();
	}

	private boolean mIsFirstLoad = true;

	public void initData() {
		if (mIsFirstLoad) {
			mIsFirstLoad = false;
			firstLoad();
		}
	}

	/**
	 * TODO 重新加载数据
	 */
	protected void reInitData() {
		mIsFirstLoad = true;
		mContents.clear();
		initData();
	}

	protected void firstLoad() {
		loadData(MESSAGE_LOAD_FIRST);
	}

	protected void loadData(int type) {
		ArrayList<T> list = makeData(type);
		switch (type) {
		case MESSAGE_LOAD_FIRST:
			mContents = list;
			break;
		case MESSAGE_PULL_REFRESH:
			mContents = list;
			if(mListView instanceof MyListView){
				((MyListView)mListView).stopRefresh();
			}
			break;
		case MESSAGE_LOAD_MORE:
			mContents.addAll(list);
			mMoreView.setDisplayType(MyFooterView.TYPE_NORMAL);
			break;
		default:
			break;
		}
		if(mContents.size() >= 100){
			mHasMore = false;
			mMoreView.setDisplayType(MyFooterView.TYPE_HIDE);
		}else{
			mHasMore = true;
			mMoreView.setDisplayType(MyFooterView.TYPE_SHOW);
		}
		mAdapter.setContents(mContents);
	}
	
	protected abstract ArrayList<T> makeData(int type);

	// 是否还有更多页
	private boolean mHasMore = true;

	protected void addHeaderView() {
	}

	protected void addFooterView() {
		if (mMoreView == null) {
			mMoreView = new MyFooterView(getActivity());
			mMoreView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					loadData(MESSAGE_LOAD_MORE);
				}
			});
			mMoreView.setDisplayType(MyFooterView.TYPE_HIDE);
			mListView.addFooterView(mMoreView, null, true);
		}
	}

	protected MyAdapter<T> initAdapter(){
		MyAdapter<T> adapter = new MyAdapter<T>(getActivity());
		return adapter;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.i(TAG, "onScroll()");
		if (firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
			if (mContents != null && !mContents.isEmpty()
			/* && totalItemCount >= mContents.size() */) {
				if (mHasMore
						&& mMoreView.getDisplayType() != MyFooterView.TYPE_LOADING) {
					mMoreView.setDisplayType(MyFooterView.TYPE_LOADING);
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							loadData(MESSAGE_LOAD_MORE);
						}
					}, 3000);
				}
			}
		}

		if (mHeaderScrollListener != null) {
			onHeaderScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount, mTabIndex);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (mHeaderScrollListener != null && scrollState == SCROLL_STATE_IDLE) {
			onHeaderScroll(view, view.getFirstVisiblePosition(), 0,
					view.getLastVisiblePosition(), mTabIndex);
		}

		Log.i(TAG, "onScrollStateChanged() -> scrollState: " + scrollState);
	}
	
	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				loadData(MESSAGE_PULL_REFRESH);
			}
		}, 3000);
	}

	/**
	 * 是否支持下拉刷新，默认支持
	 * 
	 * @return
	 */
	protected boolean isPullRefreshEnable() {
		return true;
	}

	protected T getLastItem() {
		if (mContents != null && mContents.size() > 0) {
			return mContents.get(mContents.size() - 1);
		}
		return null;
	}

	protected OnHeadViewScrollerListener mHeaderScrollListener;

	protected int mTabIndex;// 当前ViewPager的tab值

	protected void onHeaderScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount, int index) {
		if (mHeaderScrollListener != null) {
			mHeaderScrollListener.onHeaderScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount, mTabIndex);
		}
	}

	public void setOnHeadViewScrollerListener(
			OnHeadViewScrollerListener mHeaderListener, int index) {
		this.mHeaderScrollListener = mHeaderListener;
		this.mTabIndex = index;
	}

	/**
	 * @author Jimmy
	 * @mail keshuangjie@gmail.com
	 * @date 2014-5-1 下午9:35:59
	 * @description 用于控制stickyMenu
	 */
	public static interface OnHeadViewScrollerListener {
		public void onHeaderScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount, int index);

		public void onHeaderPull(int height);

		public void setState(int state);
	}
	
	static class MyAdapter<T> extends BaseAdapter{
		
		protected ArrayList<T> mContent = new ArrayList<T>();

		protected final Context mContext;
		
		protected final LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this(context, null);
		}

		public MyAdapter(Context context, ArrayList<T> content) {
			if (context == null) {
				throw new IllegalArgumentException("Context must not be null");
			}
			mContext = context;
			mInflater = LayoutInflater.from(context);
			setContents(content);
		}

		public ArrayList<T> getContents() {
			return mContent;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (mContent != null && !mContent.isEmpty()) {
				count = mContent.size();
			}
			return count;
		}

		@Override
		public T getItem(int position) {
			T result = null;
			if (mContent != null && !mContent.isEmpty()) {
				result = mContent.get(position);
			}
			return result;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setContents(ArrayList<T> contents) {
			if (contents == null) {
				contents = new ArrayList<T>();
			}
			mContent = contents;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder vh;
			if(view == null){
				view = LayoutInflater.from(mContext).inflate(R.layout.item_list, null);
				vh = new ViewHolder();
				vh.tv_id = (TextView) view.findViewById(R.id.tv_id);
				vh.tv_name = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(vh);
			}
			User user = (User) getItem(position);
			vh = (ViewHolder) view.getTag();
			if(user != null && vh != null){
				vh.tv_id.setText(String.valueOf(user.id));
				vh.tv_name.setText(user.name);
			}
			return view;
		}
		
		static class ViewHolder{
			TextView tv_id;
			TextView tv_name;
		}
	}

}
