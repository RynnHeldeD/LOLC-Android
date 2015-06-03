package org.ema.utils;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ema.lolcompanion.R;
import org.ema.model.business.Summoner;

import java.util.Arrays;
import java.util.List;

public class LoLStatActivity extends Activity {


    //Ressources (name) of all summoners Views
    protected List<String> textRessourcesSummoner1 = Arrays.asList("s1name", "s1K", "s1Kv", "s1D", "s1Dv", "s1A", "s1Av", "s1DmDv", "s1DmRv", "s1Wins", "s1Defs", "s1LP");
    protected List<String> imageRessourcesSummoner1 = Arrays.asList("s1Img", "s1Perf", "s1Main", "s1Team", "s1Rank");
    protected List<String> textRessourcesSummoner2 = Arrays.asList("s2name", "s2K", "s2Kv", "s2D", "s2Dv", "s2A", "s2Av", "s2DmDv", "s2DmRv", "s2Wins", "s2Defs", "s2LP");
    protected List<String> imageRessourcesSummoner2 = Arrays.asList("s2Img", "s2Perf", "s2Main", "s2Team", "s2Rank");
    protected List<String> textRessourcesSummoner3 = Arrays.asList("s3name", "s3K", "s3Kv", "s3D", "s3Dv", "s3A", "s3Av", "s3DmDv", "s3DmRv", "s3Wins", "s3Defs", "s3LP");
    protected List<String> imageRessourcesSummoner3 = Arrays.asList("s3Img", "s3Perf", "s3Main", "s3Team", "s3Rank");
    protected List<String> textRessourcesSummoner4 = Arrays.asList("s4name", "s4K", "s4Kv", "s4D", "s4Dv", "s4A", "s4Av", "s4DmDv", "s4DmRv", "s4Wins", "s4Defs", "s4LP");
    protected List<String> imageRessourcesSummoner4 = Arrays.asList("s4Img", "s4Perf", "s4Main", "s4Team", "s4Rank");
    protected List<String> textRessourcesSummoner5 = Arrays.asList("s5name", "s5K", "s5Kv", "s5D", "s5Dv", "s5A", "s5Av", "s5DmDv", "s5DmRv", "s5Wins", "s5Defs", "s5LP");
    protected List<String> imageRessourcesSummoner5 = Arrays.asList("s5Img", "s5Perf", "s5Main", "s5Team", "s5Rank");

    //This function will fill the statistics of one summoner - has to be called in the foreach using the summoner list retrieve by DAO
    protected void fillSummonerInformations(List<String> textRessources, List<String> imgRessources, Summoner summoner, int minPerformance, int maxPerformance) {

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");

        //setting the fonts for the ressources
        for (String s : textRessources) {
            int IDRessource = getResources().getIdentifier(s, "id", getBaseContext().getPackageName());
            TextView ressource = (TextView) findViewById(IDRessource);
            ressource.setTypeface(font);

            if (s.contains("name"))
                ressource.setText(summoner.getChampion().getName());
            //if Statistics object is set
            if(summoner.getChampion().getStatistic() != null) {
                if (s.contains("Kv"))
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getKill()));
                else if (s.contains("Dv"))
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getDeath()));
                else if (s.contains("Av"))
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getAssist()));
                else if (s.contains("DmDv"))
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getDamageDealtPercentage()));
                else if (s.contains("DmRv"))
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getDamageTakenPercentage()));
                else if (s.contains("Wins"))
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getWin()));
                else if (s.contains("Defs"))
                    ressource.setText(String.valueOf(summoner.getChampion().getStatistic().getLoose()));
            }
            else{
                //we hide everything
                if(s.contains("K") || s.contains("D") || s.contains("W") || s.contains("A")){
                    ressource.setVisibility(View.INVISIBLE);
                }
            }

            if (s.contains("LP")) {
                if (summoner.getLeague().getDivision().toLowerCase().contains("unrancked"))
                    ressource.setText(R.string.unrancked);
                else
                    ressource.setText(summoner.getLeague().getDivision().toUpperCase() + " " + String.valueOf(summoner.getLeague().getLeaguePoints()) + " LP");
            }
        }

        for (String s : imgRessources) {
            int IDRessource = getResources().getIdentifier(s, "id", getBaseContext().getPackageName());
            ImageView ressource = (ImageView) findViewById(IDRessource);

            if (s.contains("Image"))
                ressource.setImageBitmap(summoner.getChampion().getIcon());
            else if (s.contains("Main") && summoner.getChampion().isMain())
                ressource.setVisibility(View.VISIBLE);
            else if (s.contains("Team") && summoner.getPremade() != 0) {
                if (summoner.getPremade() == 1)
                    ressource.setImageResource(R.drawable.team_icon_1);
                else ressource.setImageResource(R.drawable.team_icon_2);
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
            } else if (s.contains("Perf")){
                VerticalProgressBar summonerPerf = (VerticalProgressBar) findViewById(IDRessource);
                summonerPerf.setMax(maxPerformance - minPerformance);
                int performance;
                if(summoner.getChampion().getStatistic() != null) {
                    performance = Math.round(summoner.getChampion().getStatistic().getPerformance());
                }
                else performance = 0;

                int zoomPerformance = performance - minPerformance;
                summonerPerf.setProgress(zoomPerformance);

                //Setting the color and value of the Performance BAr
                if(performance < 34)
                    summonerPerf.getProgressDrawable().setColorFilter(R.color.performance_bar_normal, PorterDuff.Mode.SRC_IN);
                else if(performance >= 35 && performance < 67)
                    summonerPerf.getProgressDrawable().setColorFilter(R.color.performance_bar_avg, PorterDuff.Mode.SRC_IN);
                else if(performance >= 67)
                    summonerPerf.getProgressDrawable().setColorFilter(R.color.performance_bar_danger, PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
