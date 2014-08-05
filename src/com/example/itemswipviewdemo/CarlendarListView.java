package com.example.itemswipviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class CarlendarListView extends ListView implements OnScrollListener {
	private int mHeight, mWidth;
	private CarlendarListAdapter mAdapter;
	private View mTitle;
	private boolean isVisible;
	private boolean isTitleViewNeed = true;

	private float mLastY = -1;
	private Scroller mScroller;
	private OnScrollListener mScrollListener;

	private IXListViewListener mListViewListener;

	private RefreshListViewHeader mHeaderView;
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight;
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false;

	private RefreshListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;

	private int mTotalItemCount;

	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 200;
	private final static int PULL_LOAD_MORE_DELTA = 50;
	private final static float OFFSET_RADIO = 1.8f;

	private boolean isFirst = false;

	public CarlendarListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public CarlendarListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public CarlendarListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (!this.isVisible)
			return;
		Log.e("debug", "listview@@@isdrawchild:"+isTitleViewNeed);
		if(isTitleViewNeed)
			drawChild(canvas, this.mTitle, getDrawingTime());
	}

	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (this.mTitle == null)
			return;
		if(isTitleViewNeed){
			this.mTitle.layout(0, 0, this.mWidth, this.mHeight);
			layoutTitle(getFirstVisiblePosition());
		}
	}

	protected void onMeasure(int width, int heigth) {
		super.onMeasure(width, heigth);
		if (this.mTitle == null)
			return;
		if(isTitleViewNeed)
		measureChild(this.mTitle, width, heigth);
		this.mWidth = this.mTitle.getMeasuredWidth();
		this.mHeight = this.mTitle.getMeasuredHeight();
	}

	public void setAdapter(ListAdapter adapter) {
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		this.mAdapter = ((CarlendarListAdapter) adapter);
		super.setAdapter(adapter);
	}

	public void setTitle(View titleView) {
		this.mTitle = titleView;
		if (this.mTitle != null)
			setFadingEdgeLength(0);
		requestLayout();
	}

	public void layoutTitle(int firstVisiblePosition) {
		if ((this.mTitle == null) || (this.mAdapter == null))
			return;
		int titleState=this.mAdapter.getTitleState(firstVisiblePosition);
		Log.i("debug", "listview@@@firstvisible:"+firstVisiblePosition+"[titlestate:"+titleState);
		if(firstVisiblePosition%3==0){
			titleState=1;
		}
		switch (titleState) {
		case 0:
			this.isVisible = false;
			break;
		case 1:
			if (this.mTitle.getTop() != 0)
				this.mTitle.layout(0, 0, this.mWidth, this.mHeight);
			this.mAdapter.setTitleText(this.mTitle, firstVisiblePosition);
			this.isVisible = true;
			break;
		case 2:
			View localView = getChildAt(0);
			if (localView == null)
				return;
			int firstViewBottom = localView.getBottom();
			int titleHeight = this.mTitle.getHeight();
			if (firstViewBottom >= titleHeight)
				firstViewBottom = 0;
			else
				firstViewBottom -= titleHeight;
			this.mAdapter.setTitleText(this.mTitle, firstVisiblePosition);
			if (this.mTitle.getTop() != firstViewBottom)
				this.mTitle.layout(0, firstViewBottom, this.mWidth,
						firstViewBottom + this.mHeight);
			this.isVisible = true;
		default:
			break;
		}
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		this.setHeaderDividersEnabled(false);
		this.setFooterDividersEnabled(false);
		super.setOnScrollListener(this);

		mHeaderView = new RefreshListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);

		mFooterView = new RefreshListViewFooter(context);
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
	}

	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {

			mFooterView.show();
			mPullLoading = false;
			mFooterView.setState(RefreshListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(RefreshListViewFooter.STATE_NORMAL);
		}
	}

	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { //
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(RefreshListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(RefreshListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0);
	}

	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (!isFirst) {
			if (height == 0)
				return;
			if (mPullRefreshing && height <= mHeaderViewHeight) {
				return;
			}
		}
		int finalHeight = 0;
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				mFooterView.setState(RefreshListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(RefreshListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(RefreshListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				isTitleViewNeed = false;
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				if (getLastVisiblePosition() != 1)
					updateFooterHeight(-deltaY / OFFSET_RADIO);
			} else {
				isTitleViewNeed = true;
			}
			break;
		default:
			mLastY = -1;
			if (getFirstVisiblePosition() == 0) {
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView
							.setState(RefreshListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void startRefresh(boolean isFirst) {
		this.isFirst = isFirst;
		float deltaY = 500.0f;
		updateHeaderHeight(deltaY / OFFSET_RADIO);
		invokeOnScrolling();
		mPullRefreshing = true;
		mHeaderView.setState(RefreshListViewHeader.STATE_REFRESHING);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	public void setXListViewListener(IXListViewListener listener) {
		mListViewListener = listener;
	}

	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	public interface IXListViewListener {
		public void onRefresh();
		public void onLoadMore();
	}
}