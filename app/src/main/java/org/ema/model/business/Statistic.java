package org.ema.model.business;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by romain on 01/05/2015.
 */
public class Statistic implements Parcelable {
    private float kill;
    private float death;
    private float assist;
    private int win;
    private int loose;
    private float damageDealtPercentage;
    private float damageTakenPercentage;
    private float performance;
    private double[][] creepChartInfo;

    public float getKill() {
        return kill;
    }

    public void setKill(float kill) {
        this.kill = kill;
    }

    public float getDeath() {
        return death;
    }

    public void setDeath(float death) {
        this.death = death;
    }

    public float getAssist() {
        return assist;
    }

    public void setAssist(float assist) {
        this.assist = assist;
    }

    public int getWin() {
        return win;
    }

    public int getIntPerformance() {
        return Math.round(performance * 100);
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLoose() {
        return loose;
    }

    public void setLoose(int loose) {
        this.loose = loose;
    }

    public float getDamageDealtPercentage() {
        return damageDealtPercentage;
    }

    public void setDamageDealtPercentage(float damageDealtPercentage) {
        this.damageDealtPercentage = damageDealtPercentage;
    }

    public float getDamageTakenPercentage() {
        return damageTakenPercentage;
    }

    public void setDamageTakenPercentage(float damageTakenPercentage) {
        this.damageTakenPercentage = damageTakenPercentage;
    }

    public float getPerformance() {
        return performance;
    }

    public void setPerformance(float performance) {
        this.performance = performance;
    }

    public double[][] getCreepChartInfo() {
        return creepChartInfo;
    }

    public void setCreepChartInfo(double[][] creepChartInfo) {
        this.creepChartInfo = creepChartInfo;
    }

    public Statistic() {
    }

    public Statistic(float kill, float death, float assist, int win, int loose, float damageDealtPercentage, float damageTakenPercentage, float performance, double[][] creepChartInfo) {
        this.kill = kill;
        this.death = death;
        this.assist = assist;
        this.win = win;
        this.loose = loose;
        this.damageDealtPercentage = damageDealtPercentage;
        this.damageTakenPercentage = damageTakenPercentage;
        this.performance = performance;
        this.creepChartInfo = creepChartInfo;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "kill=" + kill +
                ", death=" + death +
                ", assist=" + assist +
                ", win=" + win +
                ", loose=" + loose +
                ", damageDealtPercentage=" + damageDealtPercentage +
                ", damageTakenPercentage=" + damageTakenPercentage +
                ", performance=" + performance +
                ", creepChartInfo=" + Arrays.toString(creepChartInfo) +
                '}';
    }

    // Parcelling part
    public Statistic(Parcel in){
        this.kill = in.readFloat();
        this.death = in.readFloat();
        this.assist = in.readFloat();
        this.win = in.readInt();
        this.loose = in.readInt();
        this.damageDealtPercentage = in.readFloat();
        this.damageTakenPercentage = in.readFloat();
        this.performance = in.readFloat();
        this.creepChartInfo = in.createIntArray();
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.kill);
        dest.writeFloat(this.death);
        dest.writeFloat(this.assist);
        dest.writeInt(this.win);
        dest.writeInt(this.loose);
        dest.writeFloat(this.damageDealtPercentage);
        dest.writeFloat(this.damageTakenPercentage);
        dest.writeFloat(this.performance);
        dest.writeIntArray(this.creepChartInfo);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Statistic createFromParcel(Parcel in) {
            return new Statistic(in);
        }

        public Statistic[] newArray(int size) {
            return new Statistic[size];
        }
    };
}
