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
	private Context context;
	private List<String> mList;
	private String[] sections;

	public int getCount() {
		return this.mList.size();
	}

	public Object getItem(int paramInt) {
		return this.mList.get(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public int getPositionForSection(int paramInt) {
		String str = this.sections[paramInt];
		return this.mList.indexOf(str);
	}

	public int getSectionForPosition(int firstVisiblePosition) {
		String str = (String) this.mList.get(firstVisiblePosition);
		for (int i = 0; i < this.sections.length; ++i) {
			if (this.sections[i] == str)
				return i;
		}
		return 0;
	}

	public Object[] getSections() {
		return this.sections;
	}

	public int getTitleState(int firstVisiblePosition) {
		int j = 0;
		if ((firstVisiblePosition >= 0) && (getCount() != 0)) {
			int i = getSectionForPosition(firstVisiblePosition);
			if ((i != -1) && (i <= this.sections.length)) {
				i = getPositionForSection(1 + getSectionForPosition(firstVisiblePosition));
				if ((i == -1) || (firstVisiblePosition != i - 1))
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
			convertView = LayoutInflater.from(this.context).inflate(
					R.layout.item, null);
			viewHolder.title = ((TextView) convertView
					.findViewById(R.id.item_title));
			viewHolder.content = ((CarlendarSwipeView) convertView
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

	public void onScroll(AbsListView paramAbsListView, int paramInt1,
			int paramInt2, int paramInt3) {
		System.out.println("onScroll");
		((CarlendarListView) paramAbsListView).layoutTitle(paramInt1);
	}

	public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
	}

	public void setTitleText(View paramView, int paramInt) {
		String str = (String) this.mList.get(paramInt);
		((TextView) paramView).setText(str);
	}

	public void update(List<String> paramList, Context paramContext) {
		this.context = paramContext;
		this.sections = new String[26];
		Collections.sort(paramList);
		this.mList = paramList;
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
			this.sections[i] = str2;
			++i;
		}
		notifyDataSetChanged();
	}

	class ViewHolder {
		CarlendarSwipeView content;
		TextView title;
	}
}