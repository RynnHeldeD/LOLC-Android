package org.ema.lolcompanion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

import org.ema.model.DAO.CurrentGameDAO;
import org.ema.model.business.Champion;
import org.ema.model.business.Item;
import org.ema.model.business.Summoner;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LoLStatActivity;
import org.ema.utils.VerticalProgressBar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AdvancedStatsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_stats);
        Intent intent = getIntent();
        Summoner summonerToShow = (Summoner) GlobalDataManager.get("summonerForAdvStats");
        Summoner current = (Summoner) GlobalDataManager.get("user");

        //background dynamic color
        if (summonerToShow.getTeamId() != current.getTeamId()) {
            LinearLayout globalContainer = (LinearLayout) findViewById(R.id.global_container);
            globalContainer.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_red));
        }

        //Title is summoner name
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/lol.ttf");
        TextView advTitle = (TextView) findViewById(R.id.advstats_title);
        advTitle.setTypeface(font);
        advTitle.setText(summonerToShow.getChampion().getName());

        //set fonts
        TextView advBuild = (TextView) findViewById(R.id.advstats_build);
        advBuild.setTypeface(font);
        TextView advWinrate = (TextView) findViewById(R.id.advstats_winrate);
        advWinrate.setTypeface(font);
        TextView advMPC = (TextView) findViewById(R.id.advstats_mpc);
        advMPC.setTypeface(font);

        //summary of previous view (line champion)
        LinearLayout rootView = (LinearLayout) findViewById(R.id.root_advstats);
        fillSummonerInformations(rootView, 1, summonerToShow, 0, 100);

        //Load detailed statistics directly in the summoner
        CurrentGameDAO.loadStatisticsDetailed(summonerToShow);

        //Item build
        Item[] listOfItems = summonerToShow.getChampion().getBuild();
        LinearLayout rootItemBuildView = (LinearLayout) findViewById(R.id.advstats_build_container);
        for (int i = 0; i < listOfItems.length; i++) {
            addFavoriteItemToFavoriteBuild(rootItemBuildView, i, listOfItems[i]);
        }

        //Winrates
        DecimalFormat df = new DecimalFormat("00.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        DecimalFormat dfsmall = new DecimalFormat("0.0");
        dfsmall.setRoundingMode(RoundingMode.HALF_UP);

        //AVG WINRATE GLOBAL
        TextView advWinRateGlobal = (TextView) findViewById(R.id.advstats_winrate_global_value);
        float winRate = 0;
        //Log.v("MIC", "summoner wins : " + summonerToShow.getWins() + " summoner looses : " + summonerToShow.getLooses());
        if ((float) summonerToShow.getWins() + (float) summonerToShow.getLooses() != 0) {
            winRate = (float) (summonerToShow.getWins() / ((float) summonerToShow.getWins() + (float) (float) summonerToShow.getLooses())) * 10;
        }
        //Log.v("MIC", "summoner Winrate : " + winRate);
        String winRateFormatted = String.valueOf( winRate > 0 && winRate < 10 ? dfsmall.format(winRate) : df.format(winRate));
        advWinRateGlobal.setText(winRateFormatted + getResources().getString(R.string.purcent));

        //AVG WINRATE WITH CHAMPION
        TextView advWinRateChampTitle = (TextView) findViewById(R.id.advstats_winrate_champion);
        advWinRateChampTitle.setText(getResources().getString(R.string.advstats_winrates_champion_title) + " " + summonerToShow.getChampion().getName());

        TextView advWinRateChampion = (TextView) findViewById(R.id.advstats_winrate_champion_value);
        float winRateWithChampion = 0;
        if ((float) summonerToShow.getWins() + (float) summonerToShow.getLooses() != 0) {
            winRateWithChampion = (float) (summonerToShow.getChampion().getStatistic().getWin()/((float)summonerToShow.getChampion().getStatistic().getWin()+(float)summonerToShow.getChampion().getStatistic().getLoose()) * 10);
        }
        String winRateChampionFormatted = String.valueOf( winRateWithChampion > 0 && winRateWithChampion < 10 ? dfsmall.format(winRateWithChampion) : df.format(winRateWithChampion));
        advWinRateChampion.setText(String.valueOf(winRateChampionFormatted) + getResources().getString(R.string.purcent));

        //most played champion
        Champion[] champions = summonerToShow.getMostChampionsPlayed();
        BarChart chart_mpc = (BarChart) findViewById(R.id.advstats_chart_mpc);
        chart_mpc.setBackgroundColor(getResources().getColor(R.color.bg_black_transparent));
        chart_mpc.setDescriptionTextSize(getResources().getDimension(R.dimen.font_advstats_chart_mpc_description));

    }

    //this function handles the addition of favorite items
    private void addFavoriteItemToFavoriteBuild(LinearLayout containerView, int idForLine, Item item) {
        View rootview = this.getLayoutInflater().inflate(R.layout.advstats_build_item_box, containerView, false);
        //Item icon
        ImageView itemIcon = (ImageView) rootview.findViewById(R.id.item);
        Bitmap bitmap = item.getIcon();
        itemIcon.setImageBitmap(bitmap);
        itemIcon.setId(idForLine);

        //Item name
        //TextView itemName = (TextView) rootview.findViewById(R.id.item_name);
        //itemName.setText(item.getName());

        containerView.addView(rootview);
    }


    //This function will fill the statistics of one summoner - has to be called in the foreach using the summoner list retrieve by DAO
    private void fillSummonerInformations(LinearLayout containerView, int idForLine, Summoner summoner, int minPerformance, int maxPerformance) {

        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/lol.ttf");
        View rootview = this.getLayoutInflater().inflate(R.layout.line_champion, containerView, false);

        //########################## Performance
        VerticalProgressBar summonerPerf = (VerticalProgressBar) rootview.findViewById(R.id.s1Perf);
        summonerPerf.setMax(maxPerformance);
        int performance;
        if (summoner.getChampion().getStatistic() != null) {
            performance = summoner.getChampion().getStatistic().getIntPerformance();
        } else {
            performance = 0;
        }

        summonerPerf.setProgress(performance);
        int graduation = Math.round(maxPerformance / 3);
        int low = graduation;
        int mid = 2 * graduation;

        //Setting the color and value of the Performance BAr
        if (performance < low) {
            summonerPerf.setProgressDrawable(getResources().getDrawable(R.drawable.progress_green));
            //summonerPerf.getProgressDrawable().setColorFilter(getResources().getColor(R.color.performance_bar_normal), PorterDuff.Mode.DST);
        } else if (performance >= low && performance < mid) {
            summonerPerf.setProgressDrawable(getResources().getDrawable(R.drawable.progress_yellow));
            //summonerPerf.getProgressDrawable().setColorFilter(getResources().getColor(R.color.performance_bar_avg), PorterDuff.Mode.DST);
        } else if (performance >= mid) {
            summonerPerf.setProgressDrawable(getResources().getDrawable(R.drawable.progress_red));
            //summonerPerf.getProgressDrawable().setColorFilter(getResources().getColor(R.color.performance_bar_danger), PorterDuff.Mode.DST);
        }
        //We set a new ID so the progressBar is Unique for each summoner and don't reset
        summonerPerf.setId(idForLine + 100);

        //######################### Image of the champ
        ImageView img = (ImageView) rootview.findViewById(R.id.s1Img);
        Bitmap bitmap = summoner.getChampion().getIcon();
        img.setImageBitmap(bitmap);
        img.setId(idForLine);

        //######################### is main champion
        ImageView isMain = (ImageView) rootview.findViewById(R.id.s1Main);
        if (summoner.getChampion().isMain()) {
            isMain.setVisibility(View.VISIBLE);
        }

        //######################### tips
        ImageView tip = (ImageView) rootview.findViewById(R.id.s1tips);
        tip.setId(idForLine);

        //######################### Name of the summoner
        TextView name = (TextView) rootview.findViewById(R.id.s1name);
        name.setText(summoner.getName().substring(0, Math.min(summoner.getName().length(), getResources().getInteger(R.integer.max_champion_name_character_lenght))));

        //######################## TEAM PREMADE
        if (summoner.getPremade() != 0) {
            ImageView premade = (ImageView) rootview.findViewById(R.id.s1Team);
            switch (summoner.getPremade()) {
                case 1:
                    premade.setImageResource(R.drawable.team_icon_1);
                    break;
                case 2:
                    premade.setImageResource(R.drawable.team_icon_2);
                    break;
                case 3:
                    premade.setImageResource(R.drawable.team_icon_3);
                    break;
                default:
                    premade.setImageResource(R.drawable.team_icon_3);
            }
            premade.setVisibility(View.VISIBLE);
        }

        if (summoner.getChampion().getStatistic() != null) {
            //decimals handling
            DecimalFormat df = new DecimalFormat("00.0");
            df.setRoundingMode(RoundingMode.HALF_UP);
            DecimalFormat dfsmall = new DecimalFormat("0.0");
            dfsmall.setRoundingMode(RoundingMode.HALF_UP);

            //KDA
            TextView k = (TextView) rootview.findViewById(R.id.s1Kv);
            k.setText(String.valueOf(Math.round(summoner.getChampion().getStatistic().getKill())));

            TextView d = (TextView) rootview.findViewById(R.id.s1Dv);
            d.setText(String.valueOf(Math.round(summoner.getChampion().getStatistic().getDeath())));

            TextView a = (TextView) rootview.findViewById(R.id.s1Av);
            a.setText(String.valueOf(Math.round(summoner.getChampion().getStatistic().getAssist())));

            //DMD DMT
            TextView dmd = (TextView) rootview.findViewById(R.id.s1DmDv);
            if (summoner.getChampion().getStatistic().getDamageDealtPercentage() == 0) {
                dmd.setText("0" + getResources().getString(R.string.purcent));
            } else if (summoner.getChampion().getStatistic().getDamageDealtPercentage() < 10) {
                dmd.setText(String.valueOf(dfsmall.format(summoner.getChampion().getStatistic().getDamageDealtPercentage())) + getResources().getString(R.string.purcent));
            } else {
                dmd.setText(String.valueOf(df.format(summoner.getChampion().getStatistic().getDamageDealtPercentage())) + getResources().getString(R.string.purcent));
            }

            TextView dmt = (TextView) rootview.findViewById(R.id.s1DmRv);
            if (summoner.getChampion().getStatistic().getDamageTakenPercentage() == 0) {
                dmt.setText("0" + getResources().getString(R.string.purcent));
            } else if (summoner.getChampion().getStatistic().getDamageTakenPercentage() < 10) {
                dmt.setText(String.valueOf(dfsmall.format(summoner.getChampion().getStatistic().getDamageTakenPercentage())) + getResources().getString(R.string.purcent));
            } else {
                dmt.setText(String.valueOf(df.format(summoner.getChampion().getStatistic().getDamageTakenPercentage())) + getResources().getString(R.string.purcent));
            }

            //WIN / LOSES
            TextView w = (TextView) rootview.findViewById(R.id.s1Wins);
            w.setText(String.valueOf(summoner.getChampion().getStatistic().getWin()) + getResources().getString(R.string.wins));

            TextView l = (TextView) rootview.findViewById(R.id.s1Defs);
            l.setText(String.valueOf(summoner.getChampion().getStatistic().getLoose()) + getResources().getString(R.string.looses));
        } else {
            //we hide everything
            View layoutToHide1 = rootview.findViewById(getResources().getIdentifier("s1WnD", "id", rootview.getContext().getPackageName()));
            View layoutToHide2 = rootview.findViewById(getResources().getIdentifier("s1KDA_Damages", "id", rootview.getContext().getPackageName()));
            layoutToHide1.setVisibility(View.INVISIBLE);
            layoutToHide2.setVisibility(View.INVISIBLE);
        }

        //################################ RANK
        ImageView rank = (ImageView) rootview.findViewById(R.id.s1Rank);
        switch (summoner.getLeague().getDivision().split(" ")[0].toString()) {
            case "BRONZE":
                rank.setImageResource(R.drawable.rank_bronze);
                break;
            case "SILVER":
                rank.setImageResource(R.drawable.rank_silver);
                break;
            case "GOLD":
                rank.setImageResource(R.drawable.rank_gold);
                break;
            case "PLATINUM":
                rank.setImageResource(R.drawable.rank_platinum);
                break;
            case "DIAMOND":
                rank.setImageResource(R.drawable.rank_diamond);
                break;
            case "MASTER":
                rank.setImageResource(R.drawable.rank_master);
                break;
            case "CHALLENGER":
                rank.setImageResource(R.drawable.rank_challenger);
                break;
            default: //unranked
                rank.setVisibility(View.GONE);
                break;
        }

        //RANK / LP OR LEVEL
        TextView lp = (TextView) rootview.findViewById(R.id.s1LP);
        if (summoner.getLeague().getDivision().toLowerCase().contains("unranked")) {
            lp.setText(getResources().getString(R.string.summoner_level) + " " + String.valueOf(summoner.getLevel()));
        } else {
            lp.setText(summoner.getLeague().getDivision().toUpperCase() + " " + String.valueOf(summoner.getLeague().getLeaguePoints()) + " LP");
        }

        //add the line to the rootview as the FIRST child
        containerView.addView(rootview, 0);
    }

}
