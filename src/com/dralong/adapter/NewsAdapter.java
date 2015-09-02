package com.dralong.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.dralong.bean.NewsBean;
import com.dralong.news.R;
import com.dralong.util.ImageLoadUtils;
import com.dralong.util.Logs;

import java.util.ArrayList;

/**
 * Created by Jerry on 2015/8/26.
 */
public class NewsAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater mInflater;
    private static final int ITEM_TOP = 0;
    private static final int ITEM_COMMON = 1;
    private static final int ITEM_PHOTO = 2;
    private static final int ITEM_SPECIAL = 3;
    private static final int ITEM_COUNT = 5;
    private ImageLoadUtils mImageLoadUtils;
    private ArrayList<NewsBean> mList = new ArrayList<NewsBean>();

    public NewsAdapter(Context context) {
        mContext = context;
        mImageLoadUtils = ImageLoadUtils.getInstanceAsycnImage();
        mImageLoadUtils.setThreadPoolExecutor();
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<NewsBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<NewsBean> list) {
//        mList=list;
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_COUNT;
    }

    @Override
    public int getItemViewType(int position) {

        NewsBean nbean = (NewsBean) getItem(position);
        String skipType = nbean.getSkipType();
        if (position == 0) {
            return ITEM_TOP;
        } else if (skipType == null || "".equals(skipType)) {
            return ITEM_COMMON;
        } else if (skipType.equals("photoset")) {
            return ITEM_PHOTO;
        }

        return ITEM_COMMON;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case ITEM_TOP:
                return convertView = getTopView(position, convertView, parent);
            case ITEM_PHOTO:
                return convertView = getTwoView(position, convertView, parent);
            case ITEM_COMMON:
                return convertView = getOneView(position, convertView, parent);

        }
        return convertView;
    }

    public View getOneView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_list_txt, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.news_listview_txt_imageview);
            viewHolder.mTitleTxt = (TextView) convertView.findViewById(R.id.news_listview_txt_title);
            viewHolder.mDigestTxt = (TextView) convertView.findViewById(R.id.news_listview_txt_digest);
            viewHolder.mReplyCountTxt = (TextView) convertView.findViewById(R.id.news_listview_txt_replyCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NewsBean newsBean = (NewsBean) getItem(position);
        viewHolder.mTitleTxt.setText(newsBean.getTitle());
        viewHolder.mDigestTxt.setText(newsBean.getDigest());
        viewHolder.mReplyCountTxt.setText(newsBean.getReplyCount() + "跟帖");
        mImageLoadUtils.loadBitmap(mContext.getResources(), newsBean.getImgsrc(), viewHolder.mImageView, R.drawable.base_list_default_icon, 0, 0);
        return convertView;
    }

    public View getTopView(int position, View convertView, ViewGroup parent) {
//        NewsBean newsBean = (NewsBean) getItem(position);
        SliderLayout mSlider;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.img_slider_layout, null);

            mSlider = (SliderLayout) convertView.findViewById(R.id.my_slider);
            //动画切换效果

            mSlider.setPresetTransformer(SliderLayout.Transformer.Default);
            //指示器位置
            mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
            mSlider.setCustomAnimation(new DescriptionAnimation());
            mSlider.setDuration(2000);
            convertView.setTag(mSlider);
        } else {
            mSlider = (SliderLayout) convertView.getTag();
        }
        mSlider.removeAllSliders();
        for (int i = 0; i < 3; i++) {
            TextSliderView textSlider = new TextSliderView(mContext);
            NewsBean newsBean = (NewsBean) getItem(i);
            textSlider
                    .description(newsBean.getTitle())
                    .image(newsBean.getImgsrc())
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);

            //add your extra information
//            textSlider.getBundle().putString("extra", name);

            mSlider.addSlider(textSlider);
        }

        return convertView;
    }

    public View getTwoView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_list_txt_two, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageViewOne = (ImageView) convertView.findViewById(R.id.news_listview_txt_imageone);
            viewHolder.mImageViewTwo = (ImageView) convertView.findViewById(R.id.news_listview_txt_imagetwo);
            viewHolder.mImageViewThree = (ImageView) convertView.findViewById(R.id.news_listview_txt_imagethree);
            viewHolder.mTitleTxt = (TextView) convertView.findViewById(R.id.news_listview_txt_imagetitle);
            viewHolder.mReplyCountTxt = (TextView) convertView.findViewById(R.id.news_listview_txt_imagereply);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.news_listview_one_image);
            viewHolder.mTitleTxtTwo = (TextView) convertView.findViewById(R.id.news_listview_one_image_title);
            viewHolder.mReplyCountTxtTwo = (TextView) convertView.findViewById(R.id.news_listview_one_image_reply);
            viewHolder.mDigestTxtTwo = (TextView) convertView.findViewById(R.id.news_listview_one_image_digest);
            viewHolder.mLayoutOne = (LinearLayout) convertView.findViewById(R.id.layout_one);
            viewHolder.mLayoutTwo = (RelativeLayout) convertView.findViewById(R.id.layout_two);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NewsBean newsBean = (NewsBean) getItem(position);
        String skipType = newsBean.getSkipType();
        if (skipType.equals("photoset"))
            if (newsBean.getImgextra().size() > 0) {
                viewHolder.mLayoutOne.setVisibility(View.VISIBLE);
                viewHolder.mLayoutTwo.setVisibility(View.GONE);
                viewHolder.mTitleTxt.setText(newsBean.getTitle());
                viewHolder.mReplyCountTxt.setText(newsBean.getReplyCount() + "跟帖");
                mImageLoadUtils.loadBitmap(mContext.getResources(), newsBean.getImgsrc(), viewHolder.mImageViewOne, R.drawable.base_list_default_icon, 0, 0);
                mImageLoadUtils.loadBitmap(mContext.getResources(), newsBean.getImgextra().get(0).getImgsrc(), viewHolder.mImageViewTwo, R.drawable.base_list_default_icon, 0, 0);
                mImageLoadUtils.loadBitmap(mContext.getResources(), newsBean.getImgextra().get(1).getImgsrc(), viewHolder.mImageViewThree, R.drawable.base_list_default_icon, 0, 0);
            } else {
                viewHolder.mLayoutOne.setVisibility(View.GONE);
                viewHolder.mLayoutTwo.setVisibility(View.VISIBLE);
                viewHolder.mTitleTxtTwo.setText(newsBean.getTitle());
                viewHolder.mDigestTxtTwo.setText(newsBean.getDigest());
                viewHolder.mReplyCountTxtTwo.setText(newsBean.getReplyCount() + "跟帖");
                mImageLoadUtils.loadBitmap(mContext.getResources(), newsBean.getImgsrc(), viewHolder.mImageView, R.drawable.base_list_default_icon, 0, 0);
            }


        return convertView;
    }


    class ViewHolder {
        ImageView mImageView;
        ImageView mImageViewOne;
        ImageView mImageViewTwo;
        ImageView mImageViewThree;
        TextView mTitleTxt;
        TextView mDigestTxt;
        TextView mReplyCountTxt;
        TextView mTitleTxtTwo;
        TextView mDigestTxtTwo;
        TextView mReplyCountTxtTwo;
        LinearLayout mLayoutOne;
        RelativeLayout mLayoutTwo;
    }
}
