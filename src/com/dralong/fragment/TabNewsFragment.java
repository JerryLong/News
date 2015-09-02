package com.dralong.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import com.dralong.adapter.TitleFragmentAdapter;
import com.dralong.bean.TitleBean;
import com.dralong.fragment.NewsFragment;
import com.dralong.news.R;
import com.dralong.slidingmenu.lib.SlidingMenu;
import com.dralong.tool.BaseTools;
import com.dralong.tool.Constants;
import com.dralong.util.Logs;
import com.dralong.view.TopTitleScrollView;

import java.util.ArrayList;

/**
 * Created by Jerry on 2015/8/29.
 */
public class TabNewsFragment extends Fragment {
    private TopTitleScrollView mTopTitle;
    LinearLayout mRadioGroup_content;
    LinearLayout more_columns;
    RelativeLayout title_column;
    private ViewPager mViewPager;
    /**
     * 新闻分类列表
     */
    private ArrayList<TitleBean> listColumn = new ArrayList<TitleBean>();
    /**
     * 当前选中的栏目
     */
    private int columnSelectIndex = 0;
    /**
     * 左阴影部分
     */
    public ImageView shade_left;
    /**
     * 右阴影部分
     */
    public ImageView shade_right;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth = 0;
    /**
     * Item宽度
     */
    private int mItemWidth = 0;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_news, null, false);
        mScreenWidth = BaseTools.getWindowsWidth(getActivity());
        // 一个Item宽度为屏幕的1/7
        mItemWidth = mScreenWidth / 7;
        mTopTitle = (TopTitleScrollView) view.findViewById(R.id.mTopTitleScrollView);
        mRadioGroup_content = (LinearLayout) view.findViewById(R.id.mRadioGroup_content);
        more_columns = (LinearLayout) view.findViewById(R.id.add_more_columns);
        title_column = (RelativeLayout) view.findViewById(R.id.top_title_column);
        shade_left = (ImageView) view.findViewById(R.id.shade_left);
        shade_right = (ImageView) view.findViewById(R.id.shade_right);
        mViewPager = (ViewPager) view.findViewById(R.id.main_viewpager);
        setChangelView();
        return view;
    }


    /**
     * 当栏目项发生变化时候调用
     */
    private void setChangelView() {
        getColumn();
        initColumn();
        initFragment();
    }

    /**
     * 获取Column栏目数据
     */
    private void getColumn() {
        listColumn = Constants.getTitle();
    }

    /**
     * 初始化Column栏目项
     */
    private void initColumn() {
        mRadioGroup_content.removeAllViews();
        int count = listColumn.size();
        mTopTitle.setParam(getActivity(), mScreenWidth, mRadioGroup_content, shade_left, shade_right, more_columns, title_column);
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            TextView columnTextView = new TextView(getActivity());
            columnTextView.setTextAppearance(getActivity(), R.style.top_column_item_txt);
            columnTextView.setBackgroundResource(R.drawable.top_column_btn_bg);
            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setPadding(5, 5, 5, 5);
            columnTextView.setId(i);
            columnTextView.setText(listColumn.get(i).getTitle());
            columnTextView.setTextColor(R.drawable.top_column_select_color);
            if (columnSelectIndex == i) {
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v)
                            localView.setSelected(false);
                        else {
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                        }
                    }
                }
            });
            mRadioGroup_content.addView(columnTextView, i, params);
        }
    }

    /**
     * 选择的Column里面的一项
     */
    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
            View checkView = mRadioGroup_content.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            mTopTitle.smoothScrollTo(i2, 0);
        }
        //判断是否选中
        for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
            View checkView = mRadioGroup_content.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                ischeck = true;
            } else {
                ischeck = false;
            }
            checkView.setSelected(ischeck);
        }
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        int count = listColumn.size();
        for (int i = 0; i < count; i++) {
            Bundle data = new Bundle();
            data.putString("type", listColumn.get(i).getType());

            NewsFragment newsAdapter = new NewsFragment();
            newsAdapter.setArguments(data);
            fragments.add(newsAdapter);
        }
        TitleFragmentAdapter mAdapetr = new TitleFragmentAdapter(getActivity().getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapetr);
        mViewPager.setOnPageChangeListener(pageListener);
    }

    /**
     * ViewPager切换监听方法
     */
    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
            selectTab(position);
        }
    };

}
