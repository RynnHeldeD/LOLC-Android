/* Copyright � 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette �uvre est prot�g�e par le droit d�auteur et strictement r�serv�e � l�usage priv� du
 * client. Toute reproduction ou diffusion au profit de tiers, � titre
 * gratuit ou on�reux, de
 * tout ou partie de cette �uvre est strictement interdite et constitue une contrefa�on pr�vue
 * par les articles L 335-2 et suivants du Code de la propri�t�
 * intellectuelle. Les ayants-droits se
 * r�servent le droit de poursuivre toute atteinte � leurs droits de
 * propri�t� intellectuelle devant les
 * juridictions civiles ou p�nales.
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
