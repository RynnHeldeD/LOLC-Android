package org.ema.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ema.lolcompanion.R;
import org.ema.model.business.Summoner;
import org.ema.utils.GlobalDataManager;

import java.util.ArrayList;

public class AlliesFragment extends SummonersListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.list_champion, container, false);

        LinearLayout ennemiesBackground = (LinearLayout) rootView.findViewById(R.id.root_activity_list_champion);
        ennemiesBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_blue));
        //setting the font for title
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");
        TextView allies = (TextView) rootView.findViewById(R.id.list_champion_title);
        allies.setText(R.string.allies);
        allies.setTypeface(font);

        //parsing summoners list to retrieve only allies
        ArrayList<Summoner> summonersListFromPreviousActivity = (ArrayList<Summoner>) GlobalDataManager.get("summonersList");
        Summoner current = (Summoner)GlobalDataManager.get("user");

        summonersList = new ArrayList<Summoner>();
        for(Summoner summoner : summonersListFromPreviousActivity){
            if(summoner.getTeamId() == current.getTeamId()){
                summonersList.add(summoner);
            }
        }

        //We get the container where we are going to add all the champion lines
        LinearLayout allies_container = (LinearLayout) rootView.findViewById(R.id.root_list_champion);
        //Create summoner lines
        for(int idForLine = 0; idForLine < summonersList.size(); idForLine++) {
            fillSummonerInformations(allies_container, idForLine, summonersList.get(idForLine), 0, 100);
        }

        return rootView;
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        ScrollView view_ally = (ScrollView) getActivity().findViewById(R.id.root_scroll_allies);
        LinearLayout loader = (LinearLayout) getActivity().findViewById(R.id.loading_advstats);
        loader.setVisibility(View.GONE);
        view_ally.setVisibility(View.VISIBLE);
    }*/
}