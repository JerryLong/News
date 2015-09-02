package com.dralong.bean;

import java.util.ArrayList;

/**
 * Created by Jerry on 2015/9/1.
 */
public class NewsDetailBean {
    private String body;
//    private String users;
    private String replyCount;
    private String title;
    private String digest;
    private String ptime;
    private String source;
    private ArrayList<ImgList> img=new ArrayList<ImgList>();
//    private ArrayList<Search> keyword_search;
//    private ArrayList<Relative> relative_sys;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public ArrayList<ImgList> getImg() {
        return img;
    }

    public void setImg(ArrayList<ImgList> img) {
        this.img = img;
    }

    class Relative {
        private String id;
        private String title;
        private String source;
        private String type;
        private String ptime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPtime() {
            return ptime;
        }

        public void setPtime(String ptime) {
            this.ptime = ptime;
        }


    }

    class Search {
        private String word;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }

    public class ImgList {
        private String alt;
        private String src;

        public String getAlt() {
            return alt;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }
    }
}
