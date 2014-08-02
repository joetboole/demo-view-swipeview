package com.example.itemswipviewdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ItemSwipeView extends LinearLayout {
	private Context mContext;
	private int mLastX;
	private LinearLayout mBackView,mFrontView;
	private int mBackViewWidth;
	private boolean isBackViewShow;
	public ItemSwipeView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public ItemSwipeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public ItemSwipeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public boolean isBackViewShow() {
		return isBackViewShow;
	}

	public void setBackViewShow(boolean isBackViewShow) {
		this.isBackViewShow = isBackViewShow;
	}

	private void init() {
		View contentView = LayoutInflater.from(mContext).inflate(
				R.layout.calendar_swipeitem, null);
		mFrontView = (LinearLayout) contentView
				.findViewById(R.id.front);
		mFrontView.setOnTouchListener(mFrontViewTouchListener);

		mBackView = (LinearLayout) contentView
				.findViewById(R.id.back);
		Button btn_delete = (Button) contentView.findViewById(R.id.delete);
		Button btn_edit = (Button) contentView.findViewById(R.id.edit);

		LayoutParams contentParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		this.addView(contentView, contentParams);
	}
	private int mFrontRight,mFrontLeft;
	private OnTouchListener mFrontViewTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action=event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mLastX = (int) event.getRawX();
				mBackViewWidth=mBackView.getWidth();
				mFrontRight=mFrontView.getRight();
				mFrontLeft=mFrontView.getLeft();
				break;
			case MotionEvent.ACTION_MOVE:
				int frontDx=mLastX-(int)event.getRawX();
				int currentFrontLeft=mFrontLeft-frontDx;
				int currentFrontRight=mFrontRight-frontDx;
				if(currentFrontLeft<0)
					mFrontView.layout(currentFrontLeft,mFrontView.getTop(),currentFrontRight, mFrontView.getBottom());
				break;
			case MotionEvent.ACTION_UP:
				int frontDxUp=mLastX-(int)event.getRawX();
				if(frontDxUp>mBackViewWidth/2){
					mFrontView.layout(-mBackViewWidth,mFrontView.getTop(),mBackView.getLeft(), mFrontView.getBottom());
					isBackViewShow=true;
				}else{
					mFrontView.layout(0,mFrontView.getTop(),mBackView.getRight(), mFrontView.getBottom());
					isBackViewShow=false;
				}
				break;

			default:
				break;
			}
			return true;
		}
	};

}
