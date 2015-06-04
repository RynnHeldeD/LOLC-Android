package org.ema.lolcompanion;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ema.model.business.Summoner;
import org.ema.utils.ChampionTipDialogFragment;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LoLStatActivity;
import org.ema.utils.SummonerList;
import org.ema.utils.VerticalProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class AlliesActivity extends LoLStatActivity implements ChampionTipDialogFragment.NoticeDialogListener{

    ArrayList<Summoner> summonersAlliesList = new ArrayList<Summoner>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allies);

        //setting the font for title
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");
        TextView allies = (TextView) findViewById(R.id.allies);
        allies.setTypeface(font);

        //keeping screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //parsing summoners list to retrieve only allies
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>) GlobalDataManager.get("summonersList");
        Summoner current = (Summoner)GlobalDataManager.get("user");

        summonersAlliesList = new ArrayList<Summoner>();
        for(Summoner summoner : summonersList){
            if(summoner.getTeamId() == current.getTeamId()){
                summonersAlliesList.add(summoner);
            }
        }

        fillSummonerInformations(textRessourcesSummoner1,imageRessourcesSummoner1, summonersAlliesList.get(0), 0, 100);
        fillSummonerInformations(textRessourcesSummoner2,imageRessourcesSummoner2, summonersAlliesList.get(1), 0, 100);
        fillSummonerInformations(textRessourcesSummoner3,imageRessourcesSummoner3, summonersAlliesList.get(2), 0, 100);
        fillSummonerInformations(textRessourcesSummoner4,imageRessourcesSummoner4, summonersAlliesList.get(3), 0, 100);
        fillSummonerInformations(textRessourcesSummoner5,imageRessourcesSummoner5, summonersAlliesList.get(4), 0, 100);


    }

    public void showChampionTips(View v) {
        DialogFragment dialog = new ChampionTipDialogFragment();
        Bundle args = new Bundle();
        if(getResources().getResourceName(v.getId()).contains("1")){
            args.putString("name", summonersAlliesList.get(0).getChampion().getName());
            args.putString("tips", summonersAlliesList.get(0).getChampion().getAllyTips());
            args.putInt("next", 1);
        }
        else if(getResources().getResourceName(v.getId()).contains("2")){
            args.putString("name", summonersAlliesList.get(1).getChampion().getName());
            args.putString("tips", summonersAlliesList.get(1).getChampion().getAllyTips());
            args.putInt("next", 2);
        }
        else if(getResources().getResourceName(v.getId()).contains("3")){
            args.putString("name", summonersAlliesList.get(2).getChampion().getName());
            args.putString("tips", summonersAlliesList.get(2).getChampion().getAllyTips());
            args.putInt("next", 3);
        }
        else if(getResources().getResourceName(v.getId()).contains("4")){
            args.putString("name", summonersAlliesList.get(3).getChampion().getName());
            args.putString("tips", summonersAlliesList.get(3).getChampion().getAllyTips());
            args.putInt("next", 4);
        }
        else if(getResources().getResourceName(v.getId()).contains("5")){
            args.putString("name", summonersAlliesList.get(4).getChampion().getName());
            args.putString("tips", summonersAlliesList.get(4).getChampion().getAllyTips());
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
        args.putString("name", summonersAlliesList.get(next).getChampion().getName());
        args.putString("tips", summonersAlliesList.get(next).getChampion().getAllyTips());
        args.putInt("next", next == 4 ? 0 : next+1);
        dialog_next.setArguments(args);
        dialog_next.show(getFragmentManager(), "tips");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }


}
