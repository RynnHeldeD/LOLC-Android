package org.ema.model.business;

import android.graphics.Bitmap;

public class League {
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
}
