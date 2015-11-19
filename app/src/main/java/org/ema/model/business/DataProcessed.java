/* Copyright  2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette oeuvre est protegee par le droit d auteur et strictement reservee a l usage prive du
 * client. Toute reproduction ou diffusion au profit de tiers, a titre
 * gratuit ou onereux, de
 * tout ou partie de cette oeuvre est strictement interdite et constitue une contrefacon prevue
 * par les articles L 335-2 et suivants du Code de la propriete
 * intellectuelle. Les ayants-droits se
 * reservent le droit de poursuivre toute atteinte a leurs droits de
 * propriete intellectuelle devant les
 * juridictions civiles ou penales.
 */

package org.ema.model.business;

/**
 * Created by romain on 07/06/2015.
 */
public class DataProcessed {
    private boolean stats = false;
    private boolean premades = false;
    private boolean damages = false;
    private boolean detailedStats = false;
    private boolean runes = false;
    private boolean masteries = false;

    public boolean isRunes() {
        return runes;
    }

    public void setRunes(boolean runes) {
        this.runes = runes;
    }

    public boolean isMasteries() {
        return masteries;
    }

    public void setMasteries(boolean masteries) {
        this.masteries = masteries;
    }

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
