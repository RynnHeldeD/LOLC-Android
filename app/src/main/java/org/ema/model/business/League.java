package org.ema.model.business;

import android.graphics.Bitmap;

/**
 * Created by romain on 01/05/2015.
 */
public class League {
    private String division;
    private Bitmap icon;
    private int LeaguePoints;

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
        return LeaguePoints;
    }

    public void setLeaguePoints(int leaguePoints) {
        LeaguePoints = leaguePoints;
    }

    public League() {
    }

    public League(String division, Bitmap icon, int leaguePoints) {
        this.division = division;
        this.icon = icon;
        LeaguePoints = leaguePoints;
    }
}
