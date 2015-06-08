package org.ema.model.business;

/**
 * Created by romain on 07/06/2015.
 */
public class DataProcessed {
    private boolean stats = false;
    private boolean premades = false;
    private boolean damages = false;
    private boolean detailedStats = false;


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

    public boolean isDetailedStats() {
        return detailedStats;
    }

    public void setDetailedStats(boolean detailedStats) {
        this.detailedStats = detailedStats;
    }

    public DataProcessed() {
    }
}
