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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import org.ema.model.interfaces.ISettableIcon;
import org.ema.utils.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;

public class Champion implements ISettableIcon, Parcelable {
    private int id;
    private String name;
    private Spell spell;
    private String iconName;
    private Bitmap icon;
    private String allyTips;
    private String enemyTips;
    private Statistic statistic;
    private boolean isMain;
    private Item[] build;
    private ArrayList<LaneProbability> lanesProbabilities = new ArrayList<LaneProbability>();
    private LanesEnum lane = LanesEnum.UNKNOWN;
    private LaneProbabilitySummary[] summary = new LaneProbabilitySummary[5];

    public LaneProbabilitySummary[] getSummary() {
        return summary;
    }

    public void setSummary(LaneProbabilitySummary[] summary) {
        this.summary = summary;
    }

    public LanesEnum getLane() {
        return lane;
    }

    public void setLane(LanesEnum lane) {
        this.lane = lane;
    }

    public ArrayList<LaneProbability> getLanesProbabilities() {
        return lanesProbabilities;
    }

    public void setLanesProbabilities(ArrayList<LaneProbability> lanesProbabilities) {
        this.lanesProbabilities = lanesProbabilities;
    }

    public String getEnemyTips() {
        return enemyTips;
    }

    public void setEnemyTips(String enemyTips) {
        this.enemyTips = enemyTips;
    }

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

    public String getAllyTips() {
        return allyTips;
    }

    public void setAllyTips(String tips) {
        this.allyTips = tips;
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
        summary[0] = new LaneProbabilitySummary(LanesEnum.TOP,0);
        summary[1] = new LaneProbabilitySummary(LanesEnum.JUNGLER,0);
        summary[2] = new LaneProbabilitySummary(LanesEnum.MID,0);
        summary[3] = new LaneProbabilitySummary(LanesEnum.ADC,0);
        summary[4] = new LaneProbabilitySummary(LanesEnum.SUPPORT,0);
    }

    public Champion(int id, String name, Spell spell, String iconName, Bitmap icon, String allyTips, String enemyTips, Statistic statistic, boolean isMain, Item[] build) {
        this.id = id;
        this.name = name;
        this.spell = spell;
        this.iconName = iconName;
        this.icon = icon;
        this.allyTips = allyTips;
        this.enemyTips = enemyTips;
        this.statistic = statistic;
        this.isMain = isMain;
        this.build = build;

        summary[0] = new LaneProbabilitySummary(LanesEnum.TOP,0);
        summary[1] = new LaneProbabilitySummary(LanesEnum.JUNGLER,0);
        summary[2] = new LaneProbabilitySummary(LanesEnum.MID,0);
        summary[3] = new LaneProbabilitySummary(LanesEnum.ADC,0);
        summary[4] = new LaneProbabilitySummary(LanesEnum.SUPPORT,0);
    }

    public Champion(Champion c){
        this.id = c.id;
        this.name = c.name;
        this.spell = new Spell(c.spell);
        this.iconName = c.iconName;
        this.icon = c.icon;
        this.allyTips = c.allyTips;
        this.enemyTips = c.enemyTips;
        this.statistic = c.statistic;
        this.isMain = c.isMain;
        this.build = c.build;
    }

    @Override
    public String toString() {
        return "Champion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", spell=" + spell +
                ", iconName='" + iconName + '\'' +
                ", icon=" + icon +
                ", allyTips='" + allyTips + '\'' +
                ", enemyTips='" + enemyTips + '\'' +
                ", statistic=" + statistic +
                ", isMain=" + isMain +
                ", build=" + Arrays.toString(build) +
                '}';
    }

    // Parcelling part
    public Champion(Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.spell = in.readParcelable(Spell.class.getClassLoader());
        this.iconName = in.readString();
        this.icon = (Bitmap) in.readValue(Bitmap.class.getClassLoader());;
        this.allyTips = in.readString();
        this.enemyTips = in.readString();
        this.statistic = in.readParcelable(Statistic.class.getClassLoader());
        this.isMain = in.readByte() != 0;
        this.build = (Item[]) in.createTypedArray(Item.CREATOR);
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.spell, flags);
        dest.writeString(this.iconName);
        dest.writeValue(this.icon);
        dest.writeString(this.allyTips);
        dest.writeString(this.enemyTips);
        dest.writeParcelable(this.statistic, flags);
        dest.writeByte((byte) (this.isMain ? 1 : 0));
        dest.writeParcelableArray(this.build, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Champion createFromParcel(Parcel in) {
            return new Champion(in);
        }

        public Champion[] newArray(int size) {
            return new Champion[size];
        }
    };

    public boolean areImagesLoaded(){
        boolean areImagesLoaded = false;

        if(this.getIcon() != null && this.getIcon() instanceof  Bitmap && this.getSpell().isImageLoaded()){
            areImagesLoaded = true;
        }

        return areImagesLoaded;
    }

    public boolean areImagesBuildLoaded() {
        if(this.getBuild() == null) {
            return true;
        }

        for(int i = 0; i < this.getBuild().length; i++) {
            if(this.getBuild()[i].getIcon() == null) {
                LogUtils.LOGV("IMAGES_ITEMS", this.getBuild()[i].getIconName() + " not loaded");
                return false;
            }
        }

        LogUtils.LOGV("IMAGES_ITEMS", "all images loaded");
        return true;
    }

    public void addLaneProbability(LaneProbability lane) {
        lanesProbabilities.add(lane);
    }

}
