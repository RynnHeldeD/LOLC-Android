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
