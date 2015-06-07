package org.ema.model.business;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by romain on 01/05/2015.
 */
public class Summoner implements Parcelable {
    private int id;
    private String name;
    private Spell[] spells;
    private Champion champion;
    private League league;
    private int teamId;
    private int gameId;
    //0 not, 1 or 2 == premade
    private int premade;
    private int level;
    private float wins;
    private float looses;
    //size between 1 and 3
    private Champion[] mostChampionsPlayed;
    private DataProcessed dataProcessed = new DataProcessed();

    public DataProcessed getDataProcessed() {
        return dataProcessed;
    }

    public void setDataProcessed(DataProcessed dataProcessed) {
        this.dataProcessed = dataProcessed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Spell[] getSpells() {
        return spells;
    }

    public void setSpells(Spell[] spells) {
        this.spells = spells;
    }

    public Champion getChampion() {
        return champion;
    }

    public void setChampion(Champion champion) {
        this.champion = champion;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getPremade() {
        return premade;
    }

    public void setPremade(int premade) {
        this.premade = premade;
    }

    public float getWins() {
        return wins;
    }

    public void setWins(float wins) {
        this.wins = wins;
    }

    public Champion[] getMostChampionsPlayed() {
        return mostChampionsPlayed;
    }

    public void setMostChampionsPlayed(Champion[] mostChampionsPlayed) {
        this.mostChampionsPlayed = mostChampionsPlayed;
    }

    public float getLooses() {
        return looses;
    }

    public void setLooses(float looses) {
        this.looses = looses;
    }


    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Summoner() {
    }

    public Summoner(int id, String name, int level, Spell[] spells, Champion champion, League league, int teamId, int premade, float wins, float looses, Champion[] mostChampionsPlayed,int gameId,DataProcessed data) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.spells = spells;
        this.champion = champion;
        this.league = league;
        this.teamId = teamId;
        this.premade = premade;
        this.wins = wins;
        this.looses = looses;
        this.mostChampionsPlayed = mostChampionsPlayed;
        this.gameId = gameId;
        this.dataProcessed = data;
    }

    public Summoner(Summoner s) {
        this.id = s.id;
        this.name = s.name;
        this.level = s.level;
        Spell[] spells = new Spell[2];
        int i = 0;
        for (Spell sp : s.spells) {
            spells[i] = new Spell(sp);
            i++;
        }
        this.spells = spells;
        this.champion = new Champion(s.getChampion());
        this.league = s.league;
        this.teamId = s.teamId;
        this.premade = s.premade;
        this.wins = s.wins;
        this.looses = s.looses;
        this.gameId = s.gameId;
        this.dataProcessed = s.dataProcessed;
    }

    @Override
    public String toString() {
        return "Summoner{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", spells=" + Arrays.toString(spells) +
                ", champion=" + champion +
                ", league=" + league +
                ", teamId=" + teamId +
                ", premade=" + premade +
                ", level=" + level +
                ", wins=" + wins +
                ", looses=" + looses +
                '}';
    }

    // Parcelling part
    public Summoner(Parcel in) {

        this.id = in.readInt();
        this.name = in.readString();
        this.level = in.readInt();
        this.spells = (Spell[]) in.createTypedArray(Spell.CREATOR);
        this.champion = (Champion) in.readParcelable(Champion.class.getClassLoader());
        this.league = (League) in.readParcelable(League.class.getClassLoader());
        this.teamId = in.readInt();
        this.premade = in.readInt();
        this.wins = in.readFloat();
        this.looses = in.readFloat();
        this.gameId = in.readInt();
        this.dataProcessed = (DataProcessed) in.readParcelable(DataProcessed.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.level);
        dest.writeParcelableArray(this.spells, flags);
        dest.writeParcelable(this.champion, flags);
        dest.writeParcelable(this.league, flags);
        dest.writeInt(this.teamId);
        dest.writeInt(this.premade);
        dest.writeFloat(this.wins);
        dest.writeFloat(this.looses);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Summoner createFromParcel(Parcel in) {
            return new Summoner(in);
        }

        public Summoner[] newArray(int size) {
            return new Summoner[size];
        }
    };

    public boolean areImagesLoaded() {
        boolean areImagesLoaded = false;

        if (this.getChampion().areImagesLoaded() && this.getSpells()[0].isImageLoaded() && this.getSpells()[1].isImageLoaded()) {
            areImagesLoaded = true;
        }

        return areImagesLoaded;
    }

    public boolean areImagesMostPlayedChampionsLoaded() {
        for(Champion champion : this.getMostChampionsPlayed()) {
            if(champion.getIcon() == null) {
                Log.v("IMAGES_MOST", champion.getIconName() + " not loaded");
                return false;
            }
        }
        Log.v("IMAGES_MOST", "loaded");
        return true;
    }
}
