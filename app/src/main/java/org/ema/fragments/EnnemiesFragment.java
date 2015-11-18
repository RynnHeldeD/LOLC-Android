/* Copyright � 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette �uvre est prot�g�e par le droit d�auteur et strictement r�serv�e � l�usage priv� du
 * client. Toute reproduction ou diffusion au profit de tiers, � titre
 * gratuit ou on�reux, de
 * tout ou partie de cette �uvre est strictement interdite et constitue une contrefa�on pr�vue
 * par les articles L 335-2 et suivants du Code de la propri�t�
 * intellectuelle. Les ayants-droits se
 * r�servent le droit de poursuivre toute atteinte � leurs droits de
 * propri�t� intellectuelle devant les
 * juridictions civiles ou p�nales.
 */

package org.ema.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.ema.lolcompanion.R;
import org.ema.model.business.Summoner;
import org.ema.utils.GlobalDataManager;

import java.util.ArrayList;

public class EnnemiesFragment extends SummonersListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.list_champion, null, false);
        //We get the container where we are going to add all the champion lines
        LinearLayout ennemiesBackground = (LinearLayout) rootView.findViewById(R.id.root_activity_list_champion);
        ImageView tutorial_view = (ImageView) rootView.findViewById(R.id.tutorial_view);
        tutorial_view.setImageDrawable(getResources().getDrawable(R.drawable.tutorial_stats_ennemies_t));
        ennemiesBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_red));
        //setting the font for title
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");
        TextView ennemies = (TextView) rootView.findViewById(R.id.list_champion_title);
        ennemies.setText(R.string.ennemies);
        ennemies.setTypeface(font);

        //parsing summoners list to retrieve only ennemies
        ArrayList<Summoner> summonersListFromPreviousActivity = (ArrayList<Summoner>) GlobalDataManager.get("summonersList");
        Summoner current = (Summoner)GlobalDataManager.get("user");

        summonersList = new ArrayList<Summoner>();
        for(Summoner summoner : summonersListFromPreviousActivity){
            if(summoner.getTeamId() != current.getTeamId()){
                summonersList.add(summoner);
            }
        }

        //We get the container where we are going to add all the champion lines
        LinearLayout ennemies_container = (LinearLayout) rootView.findViewById(R.id.root_list_champion);
        //Create summoner lines
        for(int idForLine = 0; idForLine < summonersList.size(); idForLine++) {
            fillSummonerInformations(ennemies_container,  idForLine, summonersList.get(idForLine), 0, 100);
        }

        return rootView;
    }

    public void showChampionTips(View v) {
        super.showChampionTips(v, true);
    }

    //show the android tutorial of the application on the Timer View
    public void showTutorial(View view){
        ScrollView root_scroll_allies = (ScrollView) getView().findViewById(R.id.root_scroll_allies);
        ImageView tutorial_view = (ImageView) getView().findViewById(R.id.tutorial_view);
        ImageView show_tutorial_button = (ImageView) getView().findViewById(R.id.show_tutorial_button);
        if(root_scroll_allies.getVisibility() == View.GONE){
            root_scroll_allies.setVisibility(View.VISIBLE);
            tutorial_view.setVisibility(View.GONE);
            show_tutorial_button.setImageDrawable(getResources().getDrawable(R.drawable.open_tutorial));
        }
        else{
            root_scroll_allies.setVisibility(View.GONE);
            tutorial_view.setVisibility(View.VISIBLE);
            show_tutorial_button.setImageDrawable(getResources().getDrawable(R.drawable.close_tutorial));
        }
    }
}