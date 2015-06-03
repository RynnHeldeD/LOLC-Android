package org.ema.model.business;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by romain on 01/05/2015.
 */
public class Summoner extends Parcelable {
    private int id;
    private String name;
    private Spell[] spells;
    private Champion champion;
    private League league;
    private int teamId;
    private int premade;
    private int level;
    private float wins;
    private float looses;

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

    public float getLooses() {
        return looses;
    }

    public void setLooses(float looses) {
        this.looses = looses;
    }

    public Summoner() {
    }

    public Summoner(int id, String name,int level, Spell[] spells, Champion champion, League league, int teamId, int premade, float wins, float looses) {
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
    public Summoner(Parcel in){

        this.id = in.readInt();
        this.name = in.readString();
        this.level = in.readInt();
        this.spells = (Spell[]) in.createTypedArray(Spell.CREATOR);;
        this.champion = (Champion) in.readParcelable(Champion.class.getClassLoader());
        this.league = (League) in.readParcelable(League.class.getClassLoader());
        this.teamId = in.readInt();
        this.premade = in.readInt();
        this.wins = in.readFloat();
        this.looses = in.readFloat();
    }

    public int describeContents(){
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
}
