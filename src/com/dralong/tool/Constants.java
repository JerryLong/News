package com.dralong.tool;

import com.dralong.bean.NewsBean;
import com.dralong.bean.TitleBean;

import java.util.ArrayList;

/**
 * Created by Jerry on 201
 */
public class Constants {
    public static ArrayList<TitleBean> getTitle() {
        ArrayList<TitleBean> title = new ArrayList<TitleBean>();
        TitleBean item = new TitleBean();
        item.setId(0);
        item.setType("T1348647909107");
        item.setTitle("头条");
        title.add(item);
        item = new TitleBean();
        item.setId(1);
        item.setType("T1348648517839");
        item.setTitle("娱乐");
        title.add(item);
        item = new TitleBean();
        item.setId(2);
        item.setType("T1348649079062");
        item.setTitle("体育");
        title.add(item);
        item = new TitleBean();
        item.setId(3);
        item.setType("T1348648756099");
        item.setTitle("财经");
        title.add(item);
        item = new TitleBean();
        item.setId(4);
        item.setType("T1348649580692");
        item.setTitle("科技");
        title.add(item);
        item = new TitleBean();
        item.setId(5);
        item.setType("T1348648517839");
        item.setTitle("热点");
        title.add(item);
        item = new TitleBean();
        item.setId(6);
        item.setType("T1348648517839");
        item.setTitle("成都");
        title.add(item);
        item = new TitleBean();
        item.setId(7);
        item.setType("T1348648517839");
        item.setTitle("图片");
        title.add(item);
        return title;
    }

    public static ArrayList<NewsBean> getNews() {
        ArrayList<NewsBean> list = new ArrayList<NewsBean>();

        return list;
    }
}
