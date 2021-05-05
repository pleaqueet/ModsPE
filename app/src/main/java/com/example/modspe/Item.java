package com.example.modspe;

import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("hxs6t3")
    private String title;
    @SerializedName("hxs6i1")
    private String image;
    @SerializedName("hxs6d4")
    private String desc;
    @SerializedName("hxs6f2")
    private String file;
    private int _id;
    private int favorite;

    public Item(String title, String image, String desc, String file, int _id, int favorite) {
        this.title = title;
        this.image = image;
        this.desc = desc;
        this.file = file;
        this._id = _id;
        this.favorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
