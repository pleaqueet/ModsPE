package com.example.modspe;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Items {
    @SerializedName("hxs6_list")
    private List<Item> mods;

    public List<Item> getMods() {
        return mods;
    }

    public void setMods(List<Item> mods) {
        this.mods = mods;
    }
}
