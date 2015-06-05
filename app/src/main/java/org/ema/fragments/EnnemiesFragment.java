package org.ema.fragments;

import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ema.lolcompanion.MainActivity;
import org.ema.lolcompanion.R;
import org.ema.model.business.Summoner;
import org.ema.utils.ChampionTipDialogFragment;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LoLStatActivity;

import java.util.ArrayList;

public class EnnemiesFragment extends LoLStatActivity implements ChampionTipDialogFragment.NoticeDialogListener{

    ArrayList<Summoner> summonersOpponentsList = new ArrayList<Summoner>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.activity_ennemies, container, false);
        //setting the font for title
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");
        TextView ennemies = (TextView) rootView.findViewById(R.id.ennemies);
        ennemies.setTypeface(font);

        //parsing summoners list to retrieve only allies
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>) GlobalDataManager.get("summonersList");
        Summoner current = (Summoner)GlobalDataManager.get("user");

        summonersOpponentsList = new ArrayList<Summoner>();
        for(Summoner summoner : summonersList){
            if(summoner.getTeamId() != current.getTeamId()){
                summonersOpponentsList.add(summoner);
            }
        }

        fillSummonerInformations(rootView, textRessourcesSummoner1,imageRessourcesSummoner1, summonersOpponentsList.get(0), 0, 100);
        fillSummonerInformations(rootView, textRessourcesSummoner2,imageRessourcesSummoner2, summonersOpponentsList.get(1), 0, 100);
        fillSummonerInformations(rootView, textRessourcesSummoner3,imageRessourcesSummoner3, summonersOpponentsList.get(2), 0, 100);
        fillSummonerInformations(rootView, textRessourcesSummoner4,imageRessourcesSummoner4, summonersOpponentsList.get(3), 0, 100);
        fillSummonerInformations(rootView, textRessourcesSummoner5,imageRessourcesSummoner5, summonersOpponentsList.get(4), 0, 100);

        return rootView;
    }

    public void showChampionTips(View v) {
        DialogFragment dialog = new ChampionTipDialogFragment();
        dialog.setTargetFragment(this, 0);
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

    public void launchMainActivity(){
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        startActivity(intent);
    }
}