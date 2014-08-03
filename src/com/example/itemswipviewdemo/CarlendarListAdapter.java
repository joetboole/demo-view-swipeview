package com.example.itemswipviewdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

public class CarlendarListAdapter extends BaseAdapter implements
		SectionIndexer, AbsListView.OnScrollListener {
	private Context mContext;
	private List<String> mList;
	private String[] mSections;

	public int getCount() {
		return this.mList.size();
	}

	public Object getItem(int position) {
		return this.mList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getPositionForSection(int index) {
		String str = this.mSections[index];
		return this.mList.indexOf(str);
	}

	public int getSectionForPosition(int firstVisiblePosition) {
		String str = (String) this.mList.get(firstVisiblePosition);
		for (int sectionPosition = 0; sectionPosition < this.mSections.length; ++sectionPosition) {
			if (this.mSections[sectionPosition] == str)
				return sectionPosition;
		}
		return 0;
	}

	public Object[] getSections() {
		return this.mSections;
	}

	public int getTitleState(int firstVisiblePosition) {
		int j = 0;
		if ((firstVisiblePosition >= 0) && (getCount() != 0)) {
			int listPosition = getSectionForPosition(firstVisiblePosition);
			if ((listPosition != -1) && (listPosition <= this.mSections.length)) {
				listPosition = getPositionForSection(1 + getSectionForPosition(firstVisiblePosition));
				if ((listPosition == -1) || (firstVisiblePosition != listPosition - 1))
					j = 1;
				else
					j = 2;
			}
		}
		return j;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.item, null);
			viewHolder.title = ((TextView) convertView
					.findViewById(R.id.item_title));
			viewHolder.carlendarSwipeView = ((CarlendarSwipeView) convertView
					.findViewById(R.id.item_content));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// viewHolder.content.setText((CharSequence)this.mList.get(position));
		String currentStr = (String) this.mList.get(position);
		String lastStr;
		if (position - 1 < 0)
			lastStr = "";
		else
			lastStr = (String) this.mList.get(position - 1);
		if (lastStr.equals(currentStr)) {
			viewHolder.title.setVisibility(View.GONE);
		} else {
			viewHolder.title.setVisibility(View.VISIBLE);
			viewHolder.title.setText(currentStr);
		}
		return convertView;
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
		((CarlendarListView) view).layoutTitle(firstVisibleItem);
	}

	public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
	}

	public void setTitleText(View titleView, int index) {
		String str = (String) this.mList.get(index);
		((TextView) titleView).setText(str);
	}

	public void update(List<String> list, Context context) {
		this.mContext = context;
		this.mSections = new String[26];
		Collections.sort(list);
		this.mList = list;
		int i = 0;
		for (int j = 0; j < this.mList.size(); ++j) {
			String str2 = (String) this.mList.get(j);
			String str1;
			if (j - 1 < 0)
				str1 = "";
			else
				str1 = (String) this.mList.get(j - 1);
			if (str1.equals(str2))
				continue;
			this.mSections[i] = str2;
			++i;
		}
		notifyDataSetChanged();
	}

	class ViewHolder {
		CarlendarSwipeView carlendarSwipeView;
		TextView title;
	}
}