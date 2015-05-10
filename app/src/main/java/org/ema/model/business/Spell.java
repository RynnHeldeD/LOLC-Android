package org.ema.model.business;

import android.graphics.Bitmap;

/**
 * Created by romain on 01/05/2015.
 */
public class Spell {
    private int id;
    private Bitmap icon;
    private float cooldown;

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

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public Spell() {
    }

    public Spell(int id, Bitmap icon, float cooldown) {
        this.id = id;
        this.icon = icon;
        this.cooldown = cooldown;
    }
}
