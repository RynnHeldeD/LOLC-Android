package org.ema.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ema.fragments.EnnemiesFragment;
import org.ema.lolcompanion.AdvancedStatsActivity;
import org.ema.lolcompanion.CompanionActivity;
import org.ema.lolcompanion.MainActivity;
import org.ema.lolcompanion.PendingRoomActivity;
import org.ema.lolcompanion.R;
import org.ema.model.DAO.SummonerDAO;
import org.ema.model.business.Summoner;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class LoLStatActivity extends Fragment {

    //Ressources (name) of all summoners Views
    protected List<String> textRessourcesSummoner = Arrays.asList("s1name", "s1K", "s1Kv", "s1D", "s1Dv", "s1A", "s1Av", "s1DmDv", "s1DmRv", "s1Wins", "s1Defs", "s1LP");
    protected List<String> imageRessourcesSummoner = Arrays.asList("s1tips", "s1Img", "s1Perf", "s1Main", "s1Team", "s1Rank");

    //This function will fill the statistics of one summoner - has to be called in the foreach using the summoner list retrieve by DAO
    public void fillSummonerInformations(LinearLayout containerView, int idForLine, Summoner summoner, int minPerformance, int maxPerformance) {

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");
        View rootview = getActivity().getLayoutInflater().inflate(R.layout.line_champion, containerView, false);

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
        if (summoner.getChampion().getStatistic() == null) {
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));
                    TextView text = (TextView) layout.findViewById(R.id.text);
                    Toast toast = new Toast(getActivity());
                    toast.setGravity(Gravity.BOTTOM, 0, 40);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    text.setText(getResources().getString(R.string.ranked_statistics_restriction_click_advanced_stats));
                    toast.show();
                }
            });
        }


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
