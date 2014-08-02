package com.example.itemswipviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class CarlendarListView extends ListView {
	private int mHeight, mWidth;
	private CarlendarListAdapter mAdapter;
	private View mTitle;
	private boolean isVisible;

	public CarlendarListView(Context context) {
		super(context);
	}

	public CarlendarListView(Context context,
			AttributeSet attrs) {
		super(context, attrs);
	}

	public CarlendarListView(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (!this.isVisible)
			return;
		drawChild(canvas, this.mTitle, getDrawingTime());
	}
	
	protected void onLayout(boolean changed, int left, int top,
			int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (this.mTitle == null)
			return;
		this.mTitle.layout(0, 0, this.mWidth, this.mHeight);
		layoutTitle(getFirstVisiblePosition());
	}

	protected void onMeasure(int width, int heigth) {
		super.onMeasure(width, heigth);
		if (this.mTitle == null)
			return;
		measureChild(this.mTitle, width, heigth);
		this.mWidth = this.mTitle.getMeasuredWidth();
		this.mHeight = this.mTitle.getMeasuredHeight();
	}

	public void setAdapter(ListAdapter adapter) {
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
		switch (this.mAdapter.getTitleState(firstVisiblePosition)) {
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
				this.mTitle.layout(0, firstViewBottom, this.mWidth, firstViewBottom + this.mHeight);
			this.isVisible = true;
		default:
			break;
		}
	}
}