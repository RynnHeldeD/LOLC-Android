package org.ema.lolcompanion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.ValueFormatter;

import org.ema.dialogs.ChampionTipAdvStatsDialogFragment;
import org.ema.dialogs.ChampionTipDialogFragment;
import org.ema.dialogs.LolCompanionProgressDialog;
import org.ema.model.DAO.CurrentGameDAO;
import org.ema.model.DAO.SummonerDAO;
import org.ema.model.business.Champion;
import org.ema.model.business.Item;
import org.ema.model.business.Summoner;
import org.ema.utils.Constant;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.VerticalProgressBar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdvancedStatsActivity extends FragmentActivity implements OnChartValueSelectedListener, ChampionTipAdvStatsDialogFragment.NoticeDialogListener{

    private BarChart chartMPC;
    private BarChart chartCPM;
    private Summoner summonerToShow;
    private Summoner current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_stats);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = getIntent();
        summonerToShow = (Summoner) GlobalDataManager.get("summonerForAdvStats");
        current = (Summoner) GlobalDataManager.get("user");

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
        TextView advCPM = (TextView) findViewById(R.id.advstats_cpm);
        advCPM.setTypeface(font);

        //summary of previous view (line champion)
        LinearLayout rootView = (LinearLayout) findViewById(R.id.root_advstats);
        fillSummonerInformations(rootView, 1, summonerToShow, 0, 100);

        //Item build
        Item[] listOfItems = summonerToShow.getChampion().getBuild();
        LinearLayout rootItemBuildView = (LinearLayout) findViewById(R.id.advstats_build_container);
        for (int i = 0; i < listOfItems.length; i++) {
            addFavoriteItemToFavoriteBuild(rootItemBuildView, i, listOfItems[i]);
        }


        addWinRates();
        addMPCChart();
        addCPMChart();
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

    //handles the winrates in the layout
    public void addWinRates(){
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
            winRate = (float) (summonerToShow.getWins() / ((float) summonerToShow.getWins() + (float) (float) summonerToShow.getLooses())) * 100;
        }
        //Log.v("MIC", "summoner Winrate : " + winRate);
        String winRateFormatted = String.valueOf( winRate > 0 && winRate < 10 ? dfsmall.format(winRate) : df.format(winRate));
        advWinRateGlobal.setText(winRateFormatted + getResources().getString(R.string.purcent));

        //AVG WINRATE WITH CHAMPION
        TextView advWinRateChampTitle = (TextView) findViewById(R.id.advstats_winrate_champion);
        advWinRateChampTitle.setText(getResources().getString(R.string.advstats_winrates_champion_title) + " " + summonerToShow.getChampion().getName());

        TextView advWinRateChampion = (TextView) findViewById(R.id.advstats_winrate_champion_value);
        float winRateWithChampion = 0;
        if ((float) summonerToShow.getChampion().getStatistic().getWin() + (float) summonerToShow.getChampion().getStatistic().getLoose() != 0) {
            winRateWithChampion = (float) (summonerToShow.getChampion().getStatistic().getWin()/((float)summonerToShow.getChampion().getStatistic().getWin()+(float)summonerToShow.getChampion().getStatistic().getLoose()) * 100);
        }
        String winRateChampionFormatted = String.valueOf( winRateWithChampion > 0 && winRateWithChampion < 10 ? dfsmall.format(winRateWithChampion) : df.format(winRateWithChampion));
        advWinRateChampion.setText(String.valueOf(winRateChampionFormatted) + getResources().getString(R.string.purcent));

    }

    //handles the MostPlayedChampion chart
    public void addMPCChart(){
        //most played champion
        Champion[] champions = summonerToShow.getMostChampionsPlayed();
        chartMPC = (BarChart) findViewById(R.id.advstats_chart_mpc);
        chartMPC.setDescriptionTextSize(getResources().getDimension(R.dimen.font_advstats_chart_mpc_description));
        chartMPC.setDescription("");
        //Axix X
        chartMPC.getXAxis().setEnabled(true);
        chartMPC.getXAxis().setDrawAxisLine(true);
        chartMPC.getXAxis().setDrawGridLines(false);
        chartMPC.getXAxis().setDrawLabels(true);
        chartMPC.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartMPC.getXAxis().setAxisLineColor(Color.WHITE);
        chartMPC.getXAxis().setTextSize(getResources().getDimension(R.dimen.font_advstats_chart_mpc_champion_name));
        chartMPC.getXAxis().setTextColor(getResources().getColor(R.color.grey_font));

        //Axux y - left
        chartMPC.getAxisLeft().setEnabled(true);
        chartMPC.getAxisLeft().setDrawAxisLine(true);
        chartMPC.getAxisLeft().setDrawGridLines(false);
        chartMPC.getAxisLeft().setDrawLabels(true);
        chartMPC.getAxisLeft().setAxisLineColor(Color.WHITE);
        chartMPC.getAxisLeft().setTextColor(getResources().getColor(R.color.grey_font));

        //display as int
        class IntValueFormatter implements ValueFormatter {

            @Override
            public String getFormattedValue(float value) {
                return " " + (int)value; // append a dollar-sign
            }
        }

        chartMPC.getAxisLeft().setValueFormatter(new IntValueFormatter());

        //Axis y -right
        chartMPC.getAxisRight().setEnabled(false);

        chartMPC.setPinchZoom(false);
        chartMPC.setDrawValueAboveBar(true);
        chartMPC.setDrawBarShadow(false);
        chartMPC.setDrawGridBackground(false);
        chartMPC.setOnChartValueSelectedListener(this);

        //legend
        Legend l = chartMPC.getLegend();
        l.setFormSize(10); // set the size of the legend forms/shapes
        l.setForm(Legend.LegendForm.SQUARE); // set what type of form/shape should be used
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setTextSize(12);
        l.setTextColor(Color.WHITE);
        l.setXEntrySpace(5); // set the space between the legend entries on the x-axis
        l.setYEntrySpace(5); // set the space between the legend entries on the y-axis


        //ArrayList<Bitmap> xChampions = new ArrayList<Bitmap>();
        ArrayList<String> xChampions = new ArrayList<String>();
        ArrayList<BarEntry> yPlayedGames = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yWinGames = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yLoseGames = new ArrayList<BarEntry>();

        for(int i = 0; i < champions.length; i++) {
            xChampions.add(champions[i].getName());
            Log.v("MIC", champions[i].toString());
            Log.v("MIC", String.valueOf(champions[i].getStatistic().getWin() + champions[i].getStatistic().getLoose()));
            yPlayedGames.add(new BarEntry((champions[i].getStatistic().getWin() + champions[i].getStatistic().getLoose()), i));
            yWinGames.add(new BarEntry(champions[i].getStatistic().getWin(), i));
            yLoseGames.add(new BarEntry(champions[i].getStatistic().getLoose(), i));
        }

        BarDataSet set1 = new BarDataSet(yPlayedGames, getResources().getString(R.string.advstats_chart_mpc_played_game));
        // set1.setColors(ColorTemplate.createColors(getApplicationContext(),
        // ColorTemplate.FRESH_COLORS));
        set1.setColor(getResources().getColor(R.color.advstats_mpc_total_games));
        set1.setValueTextColor(Color.WHITE);
        set1.setValueFormatter(new IntValueFormatter());
        BarDataSet set2 = new BarDataSet(yWinGames, getResources().getString(R.string.advstats_chart_mpc_winned_games));
        set2.setColor(getResources().getColor(R.color.advstats_mpc_total_wins));
        set2.setValueTextColor(Color.WHITE);
        set2.setValueFormatter(new IntValueFormatter());
        BarDataSet set3 = new BarDataSet(yLoseGames, getResources().getString(R.string.advstats_chart_mpc_losed_games));
        set3.setColor(getResources().getColor(R.color.advstats_mpc_total_loses));
        set3.setValueTextColor(Color.WHITE);
        set3.setValueFormatter(new IntValueFormatter());

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);

        BarData data = new BarData(xChampions, dataSets);
        // add space between the dataset groups in percent of bar-width
        data.setGroupSpace(40f);

        chartMPC.setData(data);
        chartMPC.invalidate();
    }

    //handles the CreepsPerMinute chart
    public void addCPMChart(){
        //most played champion
        // 2 lines - 4 column
        // creep killed : 0-10m | 10-20m | 20-30m | 30-end
        double[][] creepsInfo = summonerToShow.getChampion().getStatistic().getCreepChartInfo();
        chartCPM = (BarChart) findViewById(R.id.advstats_chart_cpm);
        chartCPM.setDescriptionTextSize(getResources().getDimension(R.dimen.font_advstats_chart_mpc_description));
        chartCPM.setDescription("");
        //Axix X
        chartCPM.getXAxis().setEnabled(true);
        chartCPM.getXAxis().setDrawAxisLine(true);
        chartCPM.getXAxis().setDrawGridLines(false);
        chartCPM.getXAxis().setDrawLabels(true);
        chartCPM.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartCPM.getXAxis().setAxisLineColor(Color.BLACK);
        chartCPM.getXAxis().setTextSize(getResources().getDimension(R.dimen.font_advstats_chart_cpm_x));
        chartCPM.getXAxis().setTextColor(getResources().getColor(R.color.black_font));

        //Axux y - left
        chartCPM.getAxisLeft().setEnabled(true);
        chartCPM.getAxisLeft().setDrawAxisLine(true);
        chartCPM.getAxisLeft().setDrawGridLines(false);
        chartCPM.getAxisLeft().setDrawLabels(true);
        chartCPM.getAxisLeft().setAxisLineColor(Color.BLACK);
        chartCPM.getAxisLeft().setTextColor(getResources().getColor(R.color.black_font));

        //display as int
        class IntValueFormatter implements ValueFormatter {

            @Override
            public String getFormattedValue(float value) {
                return " " + (int)value; // append a dollar-sign
            }
        }

        chartCPM.getAxisLeft().setValueFormatter(new IntValueFormatter());

        //Axis y -right
        chartCPM.getAxisRight().setEnabled(false);

        chartCPM.setPinchZoom(false);
        chartCPM.setDrawValueAboveBar(true);
        chartCPM.setDrawBarShadow(false);
        chartCPM.setDrawGridBackground(false);
        chartCPM.setOnChartValueSelectedListener(this);

        //legend
        Legend l = chartCPM.getLegend();
        l.setFormSize(10); // set the size of the legend forms/shapes
        l.setForm(Legend.LegendForm.SQUARE); // set what type of form/shape should be used
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setTextSize(12);
        l.setTextColor(Color.BLACK);
        l.setXEntrySpace(5); // set the space between the legend entries on the x-axis
        l.setYEntrySpace(5); // set the space between the legend entries on the y-axis


        //ArrayList<Bitmap> xChampions = new ArrayList<Bitmap>();
        ArrayList<String> xTimeIntervals = new ArrayList<String>();
        ArrayList<BarEntry> yCreepsKilled = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yCreepsMissed = new ArrayList<BarEntry>();

        xTimeIntervals.add("10m");
        xTimeIntervals.add("20m");
        xTimeIntervals.add("30m");
        xTimeIntervals.add("end");

        for(int i = 0; i < 4; i++) {
            yCreepsKilled.add(new BarEntry((int)(creepsInfo[0][i]), i));
            yCreepsMissed.add(new BarEntry((int)(creepsInfo[1][i]), i));
        }

        BarDataSet set1 = new BarDataSet(yCreepsKilled, getResources().getString(R.string.advstats_chart_cpm_killed_creeps));
        // set1.setColors(ColorTemplate.createColors(getApplicationContext(),
        // ColorTemplate.FRESH_COLORS));
        set1.setColor(getResources().getColor(R.color.advstats_cpm_killed_creeps));
        set1.setValueTextColor(Color.BLACK);
        set1.setValueFormatter(new IntValueFormatter());
        BarDataSet set2 = new BarDataSet(yCreepsMissed, getResources().getString(R.string.advstats_chart_cpm_missed_creeps));
        set2.setColor(getResources().getColor(R.color.advstats_cpm_missed_creeps));
        set2.setValueTextColor(Color.BLACK);
        set2.setValueFormatter(new IntValueFormatter());

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(xTimeIntervals, dataSets);
        // add space between the dataset groups in percent of bar-width
        data.setGroupSpace(40f);

        chartCPM.setData(data);
        chartCPM.invalidate();
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
            summonerPerf.setProgressDrawable(getResources().getDrawable(R.drawable.progress_red));
            //summonerPerf.getProgressDrawable().setColorFilter(getResources().getColor(R.color.performance_bar_normal), PorterDuff.Mode.DST);
        } else if (performance >= low && performance < mid) {
            summonerPerf.setProgressDrawable(getResources().getDrawable(R.drawable.progress_yellow));
            //summonerPerf.getProgressDrawable().setColorFilter(getResources().getColor(R.color.performance_bar_avg), PorterDuff.Mode.DST);
        } else if (performance >= mid) {
            summonerPerf.setProgressDrawable(getResources().getDrawable(R.drawable.progress_green));
            //summonerPerf.getProgressDrawable().setColorFilter(getResources().getColor(R.color.performance_bar_danger), PorterDuff.Mode.DST);
        }
        //We set a new ID so the progressBar is Unique for each summoner and don't reset
        summonerPerf.setId(idForLine + 100);

        //######################### Image of the champ
        ImageView img = (ImageView) rootview.findViewById(R.id.s1Img);
        Bitmap bitmap = summoner.getChampion().getIcon();
        img.setImageBitmap(bitmap);
        img.setId(idForLine);
        img.setClickable(false);

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
        name.setId(idForLine);
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

    public void showChampionTips(View v) {
        DialogFragment dialog = new ChampionTipAdvStatsDialogFragment();
        Bundle args = new Bundle();
        //We give to the dialog the summoners info to display
        if (summonerToShow.getTeamId() != current.getTeamId()) {
            args.putString("name", summonerToShow.getChampion().getName());
            args.putString("tips", summonerToShow.getChampion().getEnemyTips());
        }
        else{
            args.putString("name", summonerToShow.getChampion().getName());
            args.putString("tips", summonerToShow.getChampion().getAllyTips());
        }

        //the next value for the (Next) Button of the dialog. If it's the last item which is clicked, go back to first item, else go tho next
        args.putInt("next",  0);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "advstats_tips");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

}

