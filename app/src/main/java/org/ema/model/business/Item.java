package org.ema.model.business;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
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

    // Parcelling part
    public Item(Parcel in){
        this.name = in.readString();
        this.icon = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeValue(this.icon);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
