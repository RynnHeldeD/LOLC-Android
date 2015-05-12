package org.ema.model.business;

import android.graphics.Bitmap;

/**
 * Created by romain on 01/05/2015.
 */
public class Spell {
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

    @Override
    public String toString() {
        return "Spell{" +
                "id=" + id +
                "iconName=" + iconName +
                ", icon=" + icon +
                ", cooldown=" + cooldowns +
                '}';
    }
}
