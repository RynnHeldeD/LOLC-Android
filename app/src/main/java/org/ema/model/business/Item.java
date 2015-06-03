package org.ema.model.business;

import android.graphics.Bitmap;

public class Item {
    private String name;
    private Bitmap icon;

    public String getName() {
        return name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public Item() {
    }

    public Item(String name, Bitmap icon) {
        this.name = name;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", icon=" + icon +
                '}';
    }
}
