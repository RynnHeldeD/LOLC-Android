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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
    private ArrayList<Mastery> masteries = new ArrayList<>();
    private ArrayList<Rune> runes = new ArrayList<>();
    private double cooldownsRatioPerLevel = -1;

    public ArrayList<Mastery> getMasteries() {
        return masteries;
    }

    public void setMasteries(ArrayList<Mastery> masteries) {
        this.masteries = masteries;
    }

    public ArrayList<Rune> getRunes() {
        return runes;
    }

    public void setRunes(ArrayList<Rune> runes) {
        this.runes = runes;
    }

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
        if(this.getMostChampionsPlayed() == null) {
            return true;
        }

        for(Champion champion : this.getMostChampionsPlayed()) {
            if(champion.getIcon() == null) {
                Log.v("IMAGES_MOST", champion.getIconName() + " not loaded");
                return false;
            }
        }
        Log.v("IMAGES_MOST", "loaded");
        return true;
    }

    public Double getCooldownPerLevelAndCalculCooldowns() {
        if(cooldownsRatioPerLevel == -1) {
            double cooldownRatio = 1;
            double cooldownRatioPerLevel = 1;
            double cooldownSummunerSpells = 1;

            for(int i = 0; i < this.getRunes().size(); i++) {
                Rune rune = this.getRunes().get(i);

                if(rune.getStats().get("rPercentCooldownMod") != null) {
                    cooldownRatio += Double.parseDouble(rune.getStats().get("rPercentCooldownMod"));
                }
                else if(rune.getStats().get("rPercentCooldownModPerLevel") != null) {
                    cooldownRatioPerLevel += Double.parseDouble(rune.getStats().get("rPercentCooldownModPerLevel"));
                }
            }

            for(int i = 0; i < this.getMasteries().size(); i++) {
                Mastery mastery = this.getMasteries().get(i);

                //Cooldown of champion spells
                if(mastery.getDescription().contains("Cooldown Reduction")){
                    String stat = mastery.getDescription().replaceAll("\\+","").split("\\%")[0];
                    cooldownRatio -= (Double.parseDouble(stat) / 100);
                }
                //Cooldowns of summuner spells
                else if(mastery.getDescription().contains("cooldown")) {
                    String stats[] = mastery.getDescription().replaceAll("\\%","").split(" ");
                    String stat = stats[stats.length-1];
                    cooldownSummunerSpells -= (Double.parseDouble(stat) / 100);
                }
            }

            //Update summoner spells
            for(Spell spell : this.getSpells()) {
                float cooldowns[] = spell.getCooldown();
                for(int x = 0; x < cooldowns.length; x++) {
                        cooldowns[x] = Math.round(cooldowns[x] * cooldownSummunerSpells);
                }
            }

            //Update ultimate spell
            Spell spell = this.getChampion().getSpell();
            float cooldowns[] = spell.getCooldown();
            for(int x = 0; x < cooldowns.length; x++) {
                cooldowns[x] = Math.round(cooldowns[x] * cooldownRatio);
            }

            cooldownsRatioPerLevel = cooldownRatioPerLevel;
        }

        return cooldownsRatioPerLevel;
    }
}
