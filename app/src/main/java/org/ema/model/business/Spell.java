/* Copyright � 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette �uvre est prot�g�e par le droit d�auteur et strictement r�serv�e � l�usage priv� du
 * client. Toute reproduction ou diffusion au profit de tiers, � titre
 * gratuit ou on�reux, de
 * tout ou partie de cette �uvre est strictement interdite et constitue une contrefa�on pr�vue
 * par les articles L 335-2 et suivants du Code de la propri�t�
 * intellectuelle. Les ayants-droits se
 * r�servent le droit de poursuivre toute atteinte � leurs droits de
 * propri�t� intellectuelle devant les
 * juridictions civiles ou p�nales.
 */

package org.ema.model.business;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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

    public Spell (Spell s){
        this.id = s.id;
        this.iconName = s.iconName;
        this.icon = s.icon;
        this.cooldowns = s.getCooldown();
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

    public boolean isImageLoaded(){
        boolean isImageLoaded = false;

        if(this.getIcon() != null && this.getIcon() instanceof Bitmap){
            isImageLoaded = true;
        }

        return isImageLoaded;
    }
}
