package com.dralong.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dralong.adapter.NewsAdapter;
import com.dralong.bean.NewsBean;
import com.dralong.news.NewsDetailedActivity;
import com.dralong.news.R;
import com.dralong.util.Logs;
import com.dralong.util.NetConnUtils;
import com.dralong.util.PreferenceCache;
import com.dralong.xlistview.XListView;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jerry on 2015/8/26.
 */
public class NewsFragment extends Fragment implements XListView.IXListViewListener {
    private Context mContext;
    private ArrayList<NewsBean> mList = new ArrayList<NewsBean>();
    private XListView mXListView;
    private NewsAdapter mAdapter;
    private String text;
    private ImageView mPageLoadImage;
    public final static int SET_NEWSLIST = 0;
    public final static int REFRESH_DATA = 1;
    public final static int ERROR_DATA = 2;

    private static final int PAGE_SIZE = 20; // 第页20条数据
    private int pageNo = 0; // 页号 ，表示第几页,第一页从0开始
    private int pageSize = PAGE_SIZE; // 页大小，显示每页多少条数据
    private String Type = "T1348647909107"; // 新闻类型标识, 默认为头条新闻
    private int mTotalCount = 0;// 默认0页
    private int mCurrentPager = pageNo;
    private NetConnUtils mNetConnUtils;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_NEWSLIST:
                    mAdapter = new NewsAdapter(mContext);
                    mXListView.setAdapter(mAdapter);
                    mAdapter.setData(mList);
                    break;
                case REFRESH_DATA:
                    String respone = (String) msg.obj;
                    getCacheData(respone, Type);
                    break;
                case ERROR_DATA:
                    String errorback = (String) msg.obj;
                    Toast.makeText(getActivity(), errorback,
                            Toast.LENGTH_SHORT).show();
                    stopRefreshAndLoadMore();
                    break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        Type = args != null ? args.getString("type") : "";
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
        mXListView = (XListView) view.findViewById(R.id.news_fragment_xlistview);
        TextView item_textview = (TextView) view.findViewById(R.id.news_fragment_item_txt);
        item_textview.setText(text);
        mNetConnUtils = new NetConnUtils(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mXListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getAdapter().getItem(position).toString();

                NewsBean mb = (NewsBean) parent.getAdapter().getItem(position);
                String docid = mb.getDocid();
                Intent intent = new Intent(getActivity(), NewsDetailedActivity.class);
                intent.putExtra("docid", docid);
                startActivity(intent);
            }
        });
        getData(Type, mCurrentPager, pageSize);
        mXListView.setXListViewListener(this);
        mXListView.setPullLoadEnable(true);
    }

    public void getData(String type, int pageNo, int pageSize) {
        String url;
        if (type.equals("T1348647909107")) {
            url = "http://c.m.163.com/nc/article/headline/" + type + "/" + pageNo * pageSize + "-" + pageSize + ".html";
        } else {
            url = "http://c.m.163.com/nc/article/list/" + type + "/" + pageNo * pageSize + "-" + pageSize + ".html";

        }
        mNetConnUtils.getResult(url, url, new NetConnUtils.NetInterface() {
            @Override
            public void onCallBack(String result) {
                if (result != null && !result.equals("")) {
                    Message msg = Message.obtain();
                    msg.what = REFRESH_DATA;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onErrorBack(String errorBack) {
                Message msg = Message.obtain();
                msg.what = ERROR_DATA;
                msg.obj = errorBack;
                mHandler.sendMessage(msg);

                String respone = PreferenceCache.getInstance(getActivity()).getCache(url);
                if (respone != null) {
                    msg = Message.obtain();
                    msg.what = REFRESH_DATA;
                    msg.obj = respone;
                    mHandler.sendMessage(msg);
                }
            }
        });

    }

    public void getCacheData(String response, String type) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray array = jsonObj.getJSONArray(type);
            for (int i = 0; i < array.length(); i++) {
                Gson gson = new Gson();
                NewsBean nbean = gson.fromJson(array.get(i).toString(), NewsBean.class);
                mList.add(nbean);
            }

            stopRefreshAndLoadMore();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 设置fragment用户是否可见
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            //fragment可见时加载数据
            if (mList != null && mList.size() != 0) {
                mHandler.obtainMessage(SET_NEWSLIST).sendToTarget();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.obtainMessage(SET_NEWSLIST).sendToTarget();
                    }
                }).start();

            }
        } else {
            //fragment不可见时不执行操作
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    public void stopRefreshAndLoadMore() {
        mXListView.stopRefresh(); //XListView停止刷新
        mXListView.setRefreshTime("刚刚");
        mXListView.stopLoadMore();
    }

    @Override
    public void onRefresh() {
        mXListView.setPullLoadEnable(true);
        getData(Type, mCurrentPager, pageSize);
//        mAdapter.setData(mList);
        mXListView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
        mTotalCount = mCurrentPager;
        if (mCurrentPager == mTotalCount) {
            mXListView.setPullLoadEnable(true);
            getData(Type, ++mCurrentPager, pageSize);
            mAdapter.setData(mList);
            mXListView.stopLoadMore();
        }
    }
}
