package com.chinesemobile.chinesemobile.models;

public class ModelVocabulary {
    //variables
    String uid, id, english, chinese, pinyin, categoryId, url;
    long timestamp, viewsCount;
    boolean save;

    //empty constructor, required for firebase
    public ModelVocabulary() {
    }

    //constructor with all params

    public ModelVocabulary(String uid, String id, String english, String chinese, String pinyin, String categoryId, String url, long timestamp, long viewsCount, boolean save) {
        this.uid = uid;
        this.id = id;
        this.english = english;
        this.chinese = chinese;
        this.pinyin = pinyin;
        this.categoryId = categoryId;
        this.url = url;
        this.timestamp = timestamp;
        this.viewsCount = viewsCount;
        this.save = save;
    }


    //Getter an Setter

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }
}
