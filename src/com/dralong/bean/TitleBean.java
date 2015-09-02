package com.dralong.bean;

/**
 * Created by Jerry on 2015/8/27.
 */
public class TitleBean {
    public Integer id;
    public String type;
    public String title;
    public Boolean is_myinterest;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIs_myinterest() {
        return is_myinterest;
    }

    public void setIs_myinterest(Boolean is_myinterest) {
        this.is_myinterest = is_myinterest;
    }
}
