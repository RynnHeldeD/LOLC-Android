package org.ema.model.business;

/**
 * Created by romain on 08/06/2015.
 */
public class LaneProbability {
    private String role;
    private String lane;
    private int spellId1;
    private int spellId2;
    private int nbOccurences;
    private float proba;

    public float getProba() {
        return proba;
    }

    public void setProba(float proba) {
        this.proba = proba;
    }

    public int getNbOccurences() {
        return nbOccurences;
    }

    public void setNbOccurences(int nbOccurences) {
        this.nbOccurences = nbOccurences;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public int getSpellId1() {
        return spellId1;
    }

    public void setSpellId1(int spellId1) {
        this.spellId1 = spellId1;
    }

    public int getSpellId2() {
        return spellId2;
    }

    public void setSpellId2(int spellId2) {
        this.spellId2 = spellId2;
    }

    public LaneProbability(String role, String lane, int itemId1, int itemId2) {
        this.role = role;
        this.lane = lane;
        this.spellId1 = itemId1;
        this.spellId2 = itemId2;
    }

    public LaneProbability() {
    }
}
