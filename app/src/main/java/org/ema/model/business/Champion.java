package org.ema.model.business;

import android.graphics.Bitmap;

import java.util.Arrays;

/**
 * Created by romain on 01/05/2015.
 */
public class Champion {
    private int id;
    private String name;
    private Spell spell;
    private Bitmap icon;
    private String tips;
    private Statistic statistic;
    private boolean isMain;
    private Item[] build;

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

    public Spell getSpell() {
        return spell;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    public Item[] getBuild() {
        return build;
    }

    public void setBuild(Item[] build) {
        this.build = build;
    }

    public Champion() {
    }

    public Champion(int id, String name, Spell spell, Bitmap icon, String tips, Statistic statistic, boolean isMain, Item[] build) {
        this.id = id;
        this.name = name;
        this.spell = spell;
        this.icon = icon;
        this.tips = tips;
        this.statistic = statistic;
        this.isMain = isMain;
        this.build = build;
    }

    @Override
    public String toString() {
        return "Champion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", spell=" + spell +
                ", icon=" + icon +
                ", tips='" + tips + '\'' +
                ", statistic=" + statistic +
                ", isMain=" + isMain +
                ", build=" + Arrays.toString(build) +
                '}';
    }
}
