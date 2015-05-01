package org.ema.model.business;

/**
 * Created by romain on 01/05/2015.
 */
public class Summoner {
    private int id;
    private String name;
    private Spell[] spells;
    private Champion champion;
    private League league;
    private int teamId;
    private int premade;
    private float winPercentage;
    private float loosePercentage;

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

    public float getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(float winPercentage) {
        this.winPercentage = winPercentage;
    }

    public float getLoosePercentage() {
        return loosePercentage;
    }

    public void setLoosePercentage(float loosePercentage) {
        this.loosePercentage = loosePercentage;
    }

    public Summoner() {
    }

    public Summoner(int id, String name, Spell[] spells, Champion champion, League league, int teamId, int premade, float winPercentage, float loosePercentage) {
        this.id = id;
        this.name = name;
        this.spells = spells;
        this.champion = champion;
        this.league = league;
        this.teamId = teamId;
        this.premade = premade;
        this.winPercentage = winPercentage;
        this.loosePercentage = loosePercentage;
    }
}
