/* Copyright  2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette oeuvre est protegee par le droit d auteur et strictement reservee a l usage prive du
 * client. Toute reproduction ou diffusion au profit de tiers, a titre
 * gratuit ou onereux, de
 * tout ou partie de cette oeuvre est strictement interdite et constitue une contrefacon prevue
 * par les articles L 335-2 et suivants du Code de la propriete
 * intellectuelle. Les ayants-droits se
 * reservent le droit de poursuivre toute atteinte a leurs droits de
 * propriete intellectuelle devant les
 * juridictions civiles ou penales.
 */

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
