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

public class League implements  Parcelable {
    private String division;
    private Bitmap icon;
    private int leaguePoints;

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public int getLeaguePoints() {
        return leaguePoints;
    }

    public void setLeaguePoints(int leaguePoints) {
        leaguePoints = leaguePoints;
    }

    public League() {
    }

    public League(String division, Bitmap icon, int leaguePoints) {
        this.division = division;
        this.icon = icon;
        this.leaguePoints = leaguePoints;
    }

    @Override
    public String toString() {
        return "League{" +
                "division='" + division + '\'' +
                ", icon=" + icon +
                ", LeaguePoints=" + leaguePoints +
                '}';
    }

    // Parcelling part
    public League(Parcel in){
        this.division = in.readString();
        this.icon = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        this.leaguePoints = in.readInt();
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.division);
        dest.writeValue(this.icon);
        dest.writeInt(this.leaguePoints);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public League createFromParcel(Parcel in) {
            return new League(in);
        }

        public League[] newArray(int size) {
            return new League[size];
        }
    };
}
