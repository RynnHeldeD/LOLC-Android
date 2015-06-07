package org.ema.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ema.lolcompanion.R;
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
    protected void fillSummonerInformations(LinearLayout containerView, int idForLine, Summoner summoner, int minPerformance, int maxPerformance) {

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");
        View rootview = getActivity().getLayoutInflater().inflate(R.layout.line_champion, containerView, false);
        //setting the fonts for the ressources
        for (String s : textRessourcesSummoner) {
            int IDRessource = getResources().getIdentifier(s, "id", getActivity().getBaseContext().getPackageName());
            TextView ressource = (TextView) rootview.findViewById(IDRessource);
            ressource.setTypeface(font);

            if (s.contains("name")) {
                ressource.setText(summoner.getName().substring(0, Math.min(summoner.getName().length(),getResources().getInteger(R.integer.max_champion_name_character_lenght))));
                ressource.setId(idForLine);
            }

            DecimalFormat df = new DecimalFormat("00.0");
            df.setRoundingMode(RoundingMode.HALF_UP);
            DecimalFormat dfsmall = new DecimalFormat("0.0");
            dfsmall.setRoundingMode(RoundingMode.HALF_UP);

            //if Statistics object is set
            if (summoner.getChampion().getStatistic() != null) {
                if (s.contains("Kv")) {
                    ressource.setText(String.valueOf(Math.round(summoner.getChampion().getStatistic().getKill())));
                } else if (s.contains("DmDv")) {
                    if(summoner.getChampion().getStatistic().getDamageDealtPercentage() == 0) {
                        ressource.setText("0" + getResources().getString(R.string.purcent));
                    }
                    else if (summoner.getChampion().getStatistic().getDamageDealtPercentage() < 10){
                        ressource.setText(String.valueOf(dfsmall.format(summoner.getChampion().getStatistic().getDamageDealtPercentage())) + getResources().getString(R.string.purcent));
                    }
                    else {
                        ressource.setText(String.valueOf(df.format(summoner.getChampion().getStatistic().getDamageDealtPercentage())) + getResources().getString(R.string.purcent));
                    }

                } else if (s.contains("DmRv")) {
                    if (summoner.getChampion().getStatistic().getDamageTakenPercentage() == 0) {
                        ressource.setText("0" + getResources().getString(R.string.purcent));
                    }
                    else if(summoner.getChampion().getStatistic().getDamageTakenPercentage() < 10){
                        ressource.setText(String.valueOf(dfsmall.format(summoner.getChampion().getStatistic().getDamageTakenPercentage())) + getResources().getString(R.string.purcent));
                    }
                    else {
                        ressource.setText(String.valueOf(df.format(summoner.getChampion().getStatistic().getDamageTakenPercentage())) + getResources().getString(R.string.purcent));
                    }
                } else if (s.contains("Wins")) {
                    Log.v("MIC", String.valueOf(String.valueOf(summoner.getChampion().getStatistic().getWin())));
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getWin()) + getResources().getString(R.string.wins));
                } else if (s.contains("Defs")) {
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getLoose())  + getResources().getString(R.string.looses));
                } else if (s.contains("Dv")) {
                    ressource.setText(String.valueOf(Math.round(summoner.getChampion().getStatistic().getDeath())));
                } else if (s.contains("Av")) {
                    ressource.setText(String.valueOf(Math.round(summoner.getChampion().getStatistic().getAssist())));
                }
            } else {
                //we hide everything
                View layoutToHide1 = rootview.findViewById(getResources().getIdentifier(s.substring(0, 2) + "WnD", "id", rootview.getContext().getPackageName()));
                View layoutToHide2 = rootview.findViewById(getResources().getIdentifier(s.substring(0, 2) + "KDA_Damages", "id", rootview.getContext().getPackageName()));
                layoutToHide1.setVisibility(View.INVISIBLE);
                layoutToHide2.setVisibility(View.INVISIBLE);
            }

            if (s.contains("LP")) {
                if (summoner.getLeague().getDivision().toLowerCase().contains("unranked")) {
                    ressource.setText(getResources().getString(R.string.summoner_level) + " " + String.valueOf(summoner.getLevel()));
                }else {
                    ressource.setText(summoner.getLeague().getDivision().toUpperCase() + " " + String.valueOf(summoner.getLeague().getLeaguePoints()) + " LP");
                }
            }
        }

        for (String s : imageRessourcesSummoner) {
            int IDRessource = getResources().getIdentifier(s, "id", rootview.getContext().getPackageName());

            if (!s.contains("Perf")) {
                ImageView ressource = (ImageView) rootview.findViewById(IDRessource);

                if (s.contains("tips")) {
                    ressource.setId(idForLine);
                }

                if (s.contains("Img")) {
                    Bitmap bitmap = summoner.getChampion().getIcon();
                    ressource.setImageBitmap(bitmap);
                } else if (s.contains("Main") && summoner.getChampion().isMain()) {
                    ressource.setVisibility(View.VISIBLE);
                } else if (s.contains("Team") && summoner.getPremade() != 0) {
                    switch (summoner.getPremade()) {
                        case 1:
                            ressource.setImageResource(R.drawable.team_icon_1);
                            break;
                        case 2:
                            ressource.setImageResource(R.drawable.team_icon_2);
                            break;
                        case 3:
                            ressource.setImageResource(R.drawable.team_icon_3);
                            break;
                        default:
                            ressource.setImageResource(R.drawable.team_icon_3);
                    }
                    ressource.setVisibility(View.VISIBLE);
                } else if (s.contains("Rank")) {
                    switch (summoner.getLeague().getDivision().split(" ")[0].toString()) {
                        case "BRONZE":
                            ressource.setImageResource(R.drawable.rank_bronze);
                            break;
                        case "SILVER":
                            ressource.setImageResource(R.drawable.rank_silver);
                            break;
                        case "GOLD":
                            ressource.setImageResource(R.drawable.rank_gold);
                            break;
                        case "PLATINUM":
                            ressource.setImageResource(R.drawable.rank_platinum);
                            break;
                        case "DIAMOND":
                            ressource.setImageResource(R.drawable.rank_diamond);
                            break;
                        case "MASTER":
                            ressource.setImageResource(R.drawable.rank_master);
                            break;
                        case "CHALLENGER":
                            ressource.setImageResource(R.drawable.rank_challenger);
                            break;
                        default: //unranked
                            ressource.setVisibility(View.GONE);
                            break;
                    }
                }
            } else {
                VerticalProgressBar summonerPerf = (VerticalProgressBar) rootview.findViewById(IDRessource);
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
                int mid =  2 * graduation;

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
                summonerPerf.setId(idForLine+100);
            }
        }

        //add the line to the rootview
            containerView.addView(rootview);
    }
}
