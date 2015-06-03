package org.ema.model.business;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.ema.model.interfaces.ISettableIcon;

public class Spell implements ISettableIcon, Parcelable {
    private int id;
    private String iconName;
    private Bitmap icon;
    private float[] cooldowns;

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public float[] getCooldown() {
        return cooldowns;
    }

    public void setCooldown(float[] cooldown) {
        this.cooldowns = cooldown;
    }

    public Spell() {
    }

    public Spell(int id, String iconName, Bitmap icon, float[] cooldown) {
        this.id = id;
        this.iconName = iconName;
        this.icon = icon;
        this.cooldowns = cooldown;
    }

    @Override
    public String toString() {
        return "Spell{" +
                "id=" + id +
                "iconName=" + iconName +
                ", icon=" + icon +
                ", cooldown=" + cooldowns +
                '}';
    }

    // Parcelling part
    public Spell(Parcel in){
        this.id = in.readInt();
        this.iconName = in.readString();
        this.icon = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        this.cooldowns = in.createFloatArray();
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.iconName);
        dest.writeValue(this.icon);
        dest.writeFloatArray(this.cooldowns);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Spell createFromParcel(Parcel in) {
            return new Spell(in);
        }

        public Spell[] newArray(int size) {
            return new Spell[size];
        }
    };
}
