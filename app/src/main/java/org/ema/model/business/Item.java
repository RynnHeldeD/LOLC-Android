package org.ema.model.business;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.ema.model.interfaces.ISettableIcon;

public class Item implements ISettableIcon, Parcelable {
    private String iconName;
    private Bitmap icon;

    @Override
    public String getIconName() {
        return iconName;
    }

    @Override
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public Item() {
    }

    public Item(String iconName, Bitmap icon) {
        this.iconName = iconName;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + iconName + '\'' +
                ", icon=" + icon +
                '}';
    }

    // Parcelling part
    public Item(Parcel in){
        this.iconName = in.readString();
        this.icon = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iconName);
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
