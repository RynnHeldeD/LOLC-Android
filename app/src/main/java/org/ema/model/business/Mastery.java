package org.ema.model.business;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by romain on 27/06/2015.
 */
public class Mastery {
    private int id;
    private String name;
    private int rank;
    private String description;
    private String iconUrl;
    private Bitmap icon;
    private HashMap<String,String> stats = new HashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public HashMap<String, String> getStats() {
        return stats;
    }

    public void setStats(HashMap<String, String> stats) {
        this.stats = stats;
    }


}
