/* Copyright © 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette œuvre est protégée par le droit d’auteur et strictement réservée à l’usage privé du
 * client. Toute reproduction ou diffusion au profit de tiers, à titre
 * gratuit ou onéreux, de
 * tout ou partie de cette œuvre est strictement interdite et constitue une contrefaçon prévue
 * par les articles L 335-2 et suivants du Code de la propriété
 * intellectuelle. Les ayants-droits se
 * réservent le droit de poursuivre toute atteinte à leurs droits de
 * propriété intellectuelle devant les
 * juridictions civiles ou pénales.
 */

package org.ema.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ema.dialogs.ChampionTipDialogFragment;
import org.ema.dialogs.LolCompanionProgressDialog;
import org.ema.lolcompanion.AdvancedStatsActivity;
import org.ema.lolcompanion.MainActivity;
import org.ema.lolcompanion.R;
import org.ema.model.DAO.CurrentGameDAO;
import org.ema.model.business.Summoner;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.VerticalProgressBar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class EnnemiesFragment extends SummonersListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.list_champion, null, false);
        //We get the container where we are going to add all the champion lines
        LinearLayout ennemiesBackground = (LinearLayout) rootView.findViewById(R.id.root_activity_list_champion);
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
}