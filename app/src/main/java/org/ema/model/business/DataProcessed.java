package org.ema.model.business;

/**
 * Created by romain on 07/06/2015.
 */
public class DataProcessed {
    private boolean stats = false;
    private boolean premades = false;
    private boolean damages = false;


    public boolean isStats() {
        return stats;
    }

    public void setStats(boolean stats) {
        this.stats = stats;
    }

    public boolean isPremades() {
        return premades;
    }

    public void setPremades(boolean premades) {
        this.premades = premades;
    }

    public boolean isDamages() {
        return damages;
    }

    public void setDamages(boolean damages) {
        this.damages = damages;
    }

    public DataProcessed() {
    }

    public DataProcessed(boolean stats, boolean premades, boolean damages) {
        this.stats = stats;
        this.premades = premades;
        this.damages = damages;
    }
}
