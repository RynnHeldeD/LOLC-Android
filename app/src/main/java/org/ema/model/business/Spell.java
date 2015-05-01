package org.ema.model.business;

import android.graphics.Bitmap;

/**
 * Created by romain on 01/05/2015.
 */
public class Spell {
    private Bitmap icon;
    private float cooldown;


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

    public Spell(Bitmap icon, float cooldown) {
        this.icon = icon;
        this.cooldown = cooldown;
    }
}
