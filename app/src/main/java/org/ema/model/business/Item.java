/* Copyright © 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette œuvre est protégée par le droit d’auteur et strictement réservée à l’usage privé du
 * client. Toute reproduction ou diffusion au profit de tiers, à titre
 * gratuit ou onéreux, de
 * tout ou partie de cette œuvre est strictement interdite et constitue une contrefaçon prévue
 * par les articles L 335-2 et suivants du Code de la propriété
 * intellectuelle. Les ayants-droits se
 * réservent le droit de poursuivre toute atteinte à leurs droits de
 * propriété intellectuelle devant les
 * juridictions civiles ou pénales.
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
