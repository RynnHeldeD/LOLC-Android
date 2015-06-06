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

import java.util.Arrays;
import java.util.List;

public class LoLStatActivity extends Fragment {


    //Ressources (name) of all summoners Views
    protected List<String> textRessourcesSummoner = Arrays.asList("s1name", "s1K", "s1Kv", "s1D", "s1Dv", "s1A", "s1Av", "s1DmDv", "s1DmRv", "s1Wins", "s1Defs", "s1LP");
    protected List<String> imageRessourcesSummoner = Arrays.asList("s1tips", "s1Img", "s1Perf", "s1Main", "s1Team", "s1Rank");

    //This function will fill the statistics of one summoner - has to be called in the foreach using the summoner list retrieve by DAO
    protected void fillSummonerInformations(LinearLayout containerView, ViewGroup inflate_container, int idForLine, Summoner summoner, int minPerformance, int maxPerformance) {

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");
        View rootview = getActivity().getLayoutInflater().inflate(R.layout.line_champion, null, false);
        //setting the fonts for the ressources
        for (String s : textRessourcesSummoner) {
            int IDRessource = getResources().getIdentifier(s, "id", getActivity().getBaseContext().getPackageName());
            TextView ressource = (TextView) rootview.findViewById(IDRessource);
            ressource.setTypeface(font);

            //ImageView tips = (ImageView) rootview.findViewById(R.id.s1tips);

            if (s.contains("name")) {
                ressource.setText(summoner.getChampion().getName());
                ressource.setId(idForLine);
            }
            //if Statistics object is set
            if (summoner.getChampion().getStatistic() != null) {
                if (s.contains("Kv")) {
                    ressource.setText(String.valueOf(Math.round(summoner.getChampion().getStatistic().getKill())));
                } else if (s.contains("DmDv")) {
                    ressource.setText(String.valueOf(Math.round(summoner.getChampion().getStatistic().getDamageDealtPercentage())));
                } else if (s.contains("DmRv")) {
                    ressource.setText(String.valueOf(Math.round(summoner.getChampion().getStatistic().getDamageTakenPercentage())));
                } else if (s.contains("Wins")) {
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getWin()));
                } else if (s.contains("Defs")) {
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getLoose()));
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
                if (summoner.getLeague().getDivision().toLowerCase().contains("unrancked"))
                    ressource.setText(R.string.unrancked);
                else
                    ressource.setText(summoner.getLeague().getDivision().toUpperCase() + " " + String.valueOf(summoner.getLeague().getLeaguePoints()) + " LP");
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
            }
        }

        //add the line to the rootview
        containerView.addView(rootview);
    }
}
