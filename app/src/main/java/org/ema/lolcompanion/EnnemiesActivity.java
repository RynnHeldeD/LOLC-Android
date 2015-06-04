package org.ema.lolcompanion;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.ema.model.business.Summoner;
import org.ema.utils.ChampionTipDialogFragment;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LoLStatActivity;

import java.util.ArrayList;


public class EnnemiesActivity extends LoLStatActivity implements ChampionTipDialogFragment.NoticeDialogListener{
    ArrayList<Summoner> summonersOpponentsList = new ArrayList<Summoner>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ennemies);

        //setting the font for title
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");
        TextView allies = (TextView) findViewById(R.id.ennemies);
        allies.setTypeface(font);

        //keeping screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //parsing summoners list to retrieve only allies
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>) GlobalDataManager.get("summonersList");
        Summoner current = (Summoner)GlobalDataManager.get("user");

        summonersOpponentsList = new ArrayList<Summoner>();
        for(Summoner summoner : summonersList){
            if(summoner.getTeamId() != current.getTeamId()){
                summonersOpponentsList.add(summoner);
            }
        }

        fillSummonerInformations(textRessourcesSummoner1,imageRessourcesSummoner1, summonersOpponentsList.get(0), 0, 100);
        fillSummonerInformations(textRessourcesSummoner2,imageRessourcesSummoner2, summonersOpponentsList.get(1), 0, 100);
        fillSummonerInformations(textRessourcesSummoner3,imageRessourcesSummoner3, summonersOpponentsList.get(2), 0, 100);
        fillSummonerInformations(textRessourcesSummoner4,imageRessourcesSummoner4, summonersOpponentsList.get(3), 0, 100);
        fillSummonerInformations(textRessourcesSummoner5,imageRessourcesSummoner5, summonersOpponentsList.get(4), 0, 100);


    }

    public void showChampionTips(View v) {
        DialogFragment dialog = new ChampionTipDialogFragment();
        Bundle args = new Bundle();
        if(getResources().getResourceName(v.getId()).contains("1")){
            args.putString("name", summonersOpponentsList.get(0).getChampion().getName());
            args.putString("tips", summonersOpponentsList.get(0).getChampion().getAllyTips());
            args.putInt("next", 1);
        }
        else if(getResources().getResourceName(v.getId()).contains("2")){
            args.putString("name", summonersOpponentsList.get(1).getChampion().getName());
            args.putString("tips", summonersOpponentsList.get(1).getChampion().getAllyTips());
            args.putInt("next", 2);
        }
        else if(getResources().getResourceName(v.getId()).contains("3")){
            args.putString("name", summonersOpponentsList.get(2).getChampion().getName());
            args.putString("tips", summonersOpponentsList.get(2).getChampion().getAllyTips());
            args.putInt("next", 3);
        }
        else if(getResources().getResourceName(v.getId()).contains("4")){
            args.putString("name", summonersOpponentsList.get(3).getChampion().getName());
            args.putString("tips", summonersOpponentsList.get(3).getChampion().getAllyTips());
            args.putInt("next", 4);
        }
        else if(getResources().getResourceName(v.getId()).contains("5")){
            args.putString("name", summonersOpponentsList.get(4).getChampion().getName());
            args.putString("tips", summonersOpponentsList.get(4).getChampion().getAllyTips());
            args.putInt("next", 0);
        }
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "tips");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogNeutralClick(DialogFragment dialog, int next) {
        // User touched the dialog's positive button
        DialogFragment dialog_next = new ChampionTipDialogFragment();
        Bundle args = new Bundle();
        args.putString("name", summonersOpponentsList.get(next).getChampion().getName());
        args.putString("tips", summonersOpponentsList.get(next).getChampion().getAllyTips());
        args.putInt("next", next == 4 ? 0 : next+1);
        dialog_next.setArguments(args);
        dialog_next.show(getFragmentManager(), "tips");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }
}
