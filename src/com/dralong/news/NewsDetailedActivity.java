package com.dralong.news;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dralong.bean.NewsBean;
import com.dralong.bean.NewsDetailBean;
import com.dralong.news.R;
import com.dralong.util.ImageLoadUtils;
import com.dralong.util.Logs;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jerry on 2015/8/31.
 */
public class NewsDetailedActivity extends FragmentActivity {
    private WebView mWebView;
    private String url, Docid;
    private RequestQueue mRequestQueue;
    private ImageView mImageView, mBackImage, mImageViewTwo;
    private EditText mEditText;
    private Context mContext;
    private ScrollView mLayout;
    private ProgressBar mProgressBar;
    private ImageLoadUtils mImageLoadUtils;
    private TextView mContextTxt, mReplyTxt, mTitleTxt, mTimeTxt, mImageOneTxt, mImageTwoTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_fragment_detail);
        initView();
        mContext = this;
        mImageLoadUtils = ImageLoadUtils.getInstanceAsycnImage();
        mImageLoadUtils.setThreadPoolExecutor();
        Intent intent = getIntent();
        Docid = intent.getStringExtra("docid");
        url = "http://c.m.163.com/nc/article/" + Docid + "/full.html";

        mRequestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONObject jsonObject = jsonObj.getJSONObject(Docid);
                    Gson gson = new Gson();
                    NewsDetailBean mDetailBean = gson.fromJson(jsonObject.toString(), NewsDetailBean.class);
                    mProgressBar.setVisibility(View.GONE);
                    mLayout.setVisibility(View.VISIBLE);
                    setContext(mDetailBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.VISIBLE);
                mLayout.setVisibility(View.GONE);
            }
        });
        stringRequest.setShouldCache(false);
        mRequestQueue.add(stringRequest);
    }

    public void initView() {
        mReplyTxt = (TextView) findViewById(R.id.detail_news_reply_text);
        mContextTxt = (TextView) findViewById(R.id.detail_news_context);
        mTitleTxt = (TextView) findViewById(R.id.detail_news_title);
        mTimeTxt = (TextView) findViewById(R.id.detail_news_time);
        mEditText = (EditText) findViewById(R.id.news_deltail_reply);
        mImageView = (ImageView) findViewById(R.id.detail_news_image_one);
        mImageOneTxt = (TextView) findViewById(R.id.detail_news_image_one_alt);
        mImageViewTwo = (ImageView) findViewById(R.id.detail_news_image_two);
        mImageTwoTxt = (TextView) findViewById(R.id.detail_news_image_two_alt);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLayout = (ScrollView) findViewById(R.id.detail_news_layout);
        mBackImage = (ImageView) findViewById(R.id.detail_news_back);
        mBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void setContext(NewsDetailBean mDetailBean) {
        mReplyTxt.setText(mDetailBean.getReplyCount() + "跟帖");
        mTitleTxt.setText(mDetailBean.getTitle());
        mTimeTxt.setText(mDetailBean.getSource() + "  " + mDetailBean.getPtime());
        mContextTxt.setText(Html.fromHtml(mDetailBean.getBody()));
        ArrayList<NewsDetailBean.ImgList> list = mDetailBean.getImg();
        if (!list.isEmpty()) {
            mImageOneTxt.setText(list.get(0).getAlt());
            mImageLoadUtils.loadBitmap(mContext.getResources(), list.get(0).getSrc(), mImageView, R.drawable.base_list_default_icon, 0, 0);
            if (list.size() > 1) {
                mImageTwoTxt.setText(list.get(1).getAlt());
                mImageLoadUtils.loadBitmap(mContext.getResources(), list.get(1).getSrc(), mImageViewTwo, R.drawable.base_list_default_icon, 0, 0);
            } else {
                mImageTwoTxt.setVisibility(View.GONE);
                mImageViewTwo.setVisibility(View.GONE);
            }
        }
    }
}
