package org.ema.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.ema.lolcompanion.AdvancedStatsActivity;
import org.ema.lolcompanion.MainActivity;
import org.ema.lolcompanion.R;
import org.ema.model.DAO.CurrentGameDAO;
import org.ema.model.business.Summoner;
import org.ema.dialogs.ChampionTipDialogFragment;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LoLStatActivity;
import org.ema.utils.SortSummonerId;

import java.util.ArrayList;
import java.util.Collections;

public class EnnemiesFragment extends LoLStatActivity implements ChampionTipDialogFragment.NoticeDialogListener{

    ArrayList<Summoner> summonersOpponentsList = new ArrayList<Summoner>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.activity_ennemies, null, false);
        //setting the font for title
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");
        TextView ennemies = (TextView) rootView.findViewById(R.id.ennemies);
        ennemies.setTypeface(font);

        //parsing summoners list to retrieve only ennemies
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>) GlobalDataManager.get("summonersList");
        Summoner current = (Summoner)GlobalDataManager.get("user");

        summonersOpponentsList = new ArrayList<Summoner>();
        for(Summoner summoner : summonersList){
            if(summoner.getTeamId() != current.getTeamId()){
                summonersOpponentsList.add(summoner);
            }
        }

        //Collections.sort(summonersOpponentsList, new SortSummonerId());

        //We get the container where we are going to add all the champion lines
        LinearLayout ennemies_container = (LinearLayout) rootView.findViewById(R.id.root_ennemies);
        //Create summoner lines
        for(int idForLine = 0; idForLine < summonersOpponentsList.size(); idForLine++) {
            fillSummonerInformations(ennemies_container,  idForLine, summonersOpponentsList.get(idForLine), 0, 100);
        }

        return rootView;
    }

    public void showChampionTips(View v) {
        DialogFragment dialog = new ChampionTipDialogFragment();
        Bundle args = new Bundle();
        //We give to the dialog the summoners info to display
        args.putString("name", summonersOpponentsList.get(v.getId()).getChampion().getName());
        args.putString("tips", summonersOpponentsList.get(v.getId()).getChampion().getAllyTips());
        //the next value for the (Next) Button of the dialog. If it's the last item which is clicked, go back to first item, else go tho next
        args.putInt("next", (v.getId() == (summonersOpponentsList.size() - 1)) ? 0 : v.getId() + 1);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "tips");
    }

    //This function handle the advanced statistic goto
    public void showAdvancedStatistics(View v, Boolean isEnnemy) {
        Summoner summonerToShow = summonersOpponentsList.get(v.getId());
        //Load detailed statistics directly in the summoner
        CurrentGameDAO.loadStatisticsDetailed(summonerToShow);
        GlobalDataManager.add("summonerForAdvStats", summonerToShow);
        Intent intent = new Intent(this.getActivity(), AdvancedStatsActivity.class);
        startActivity(intent);
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    // Handle the (Next) Button to show the next tip
    @Override
    public void onDialogNeutralClick(DialogFragment dialog, int next) {
        // User touched the dialog's positive button
        DialogFragment dialog_next = new ChampionTipDialogFragment();
        Bundle args = new Bundle();
        args.putString("name", summonersOpponentsList.get(next).getChampion().getName());
        args.putString("tips", summonersOpponentsList.get(next).getChampion().getEnemyTips());
        //If it's the last item which is clicked, go back to first item, else go tho next
        args.putInt("next", next == (summonersOpponentsList.size() - 1) ? 0 : next + 1);
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


    public ArrayList<Summoner> getSummonersOpponentsList() {
        return summonersOpponentsList;
    }

    public void setSummonersOpponentsList(ArrayList<Summoner> summonersOpponentsList) {
        this.summonersOpponentsList = summonersOpponentsList;
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        ScrollView view_ally = (ScrollView) getActivity().findViewById(R.id.root_scroll_ennemies);
        LinearLayout loader = (LinearLayout) getActivity().findViewById(R.id.loading_advstats);
        loader.setVisibility(View.GONE);
        view_ally.setVisibility(View.VISIBLE);
    }*/
}