package com.dralong.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Jerry on 2015/8/27.
 */
public class TitleFragmentAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mList;
    private FragmentManager mFragment;

    public TitleFragmentAdapter(FragmentManager fragment) {
        super(fragment);
        mFragment = fragment;
    }

    public TitleFragmentAdapter(FragmentManager fragment, ArrayList<Fragment> list) {
        super(fragment);
        mFragment = fragment;
        mList = list;
    }

    @Override
    public Fragment getItem(int i) {
        return mList.get(i);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setmFragment(ArrayList<Fragment> list) {
        if (mList != null) {
            FragmentTransaction ft = mFragment.beginTransaction();
            for (Fragment f : mList) {
                ft.remove(f);
            }
            ft.commit();
            ft = null;
            mFragment.executePendingTransactions();
        }
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        return super.instantiateItem(container, position);
    }
}
