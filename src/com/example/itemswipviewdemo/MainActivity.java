package com.example.itemswipviewdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_main);
    if (paramBundle != null)
      return;
    getSupportFragmentManager().beginTransaction().add(2131034172, new PlaceholderFragment()).commit();
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(2131492864, paramMenu);
    return true;
  }


  public static class PlaceholderFragment extends Fragment
  {
    private CarlendarListAdapter mAdapter;
    private List<String> mList;
    private CarlendarListView mListView;

    private void init(View paramView)
    {
      this.mList = new ArrayList();
      for (int i = 0; i < 26; ++i)
      {
        String str = ""+(char)(i + 65);
        this.mList.add(str);
        this.mList.add(str);
        this.mList.add(str);
      }
      this.mListView = ((CarlendarListView)paramView.findViewById(R.id.lv));
      this.mAdapter = new CarlendarListAdapter();
      this.mAdapter.update(this.mList, getActivity());
      this.mListView.setAdapter(this.mAdapter);
      this.mListView.setTitle(LayoutInflater.from(getActivity()).inflate(R.layout.title, this.mListView, false));
      this.mListView.setAdapter(this.mAdapter);
      this.mListView.setOnScrollListener(this.mAdapter);
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      View localView = paramLayoutInflater.inflate(R.layout.fragment_main, paramViewGroup, false);
      init(localView);
      return localView;
    }
  }
}