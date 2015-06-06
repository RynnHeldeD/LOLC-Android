package org.ema.fragments;

import android.support.v4.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.ema.lolcompanion.R;
import org.ema.lolcompanion.WsEventHandling;
import org.ema.model.business.Summoner;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LoLStatActivity;
import org.ema.utils.SecureDialogFragment;
import org.ema.utils.SettingsManager;
import org.ema.utils.SortSummonerId;
import org.ema.utils.Timer;
import org.ema.utils.TimerButton;
import org.ema.utils.WebSocket;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TimersFragment extends LoLStatActivity implements SecureDialogFragment.NoticeDialogListener {

    public HashMap<String,Long> timerMap;
    public static SettingsManager settingsManager = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.activity_timer, container, false);
        timerMap = new HashMap<String,Long>();
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");
        TextView timers = (TextView) rootView.findViewById(R.id.timers);
        timers.setTypeface(font);

        TimersFragment.settingsManager = new SettingsManager();
        PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>)GlobalDataManager.get("summonersList");

        WebSocket.connectWebSocket();
        return rootView;
    }

    //THREAD METHODS
    @Override
    public void onStart() {
        super.onStart();

        Summoner user = (Summoner) GlobalDataManager.get("user");
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>)GlobalDataManager.get("summonersList");

        // Recuperation et tri des summoners de l'equipe du joueur
        ArrayList<Summoner> teamSummonersList = new ArrayList<Summoner>();
        for(Summoner s : summonersList){
            if(s.getTeamId() != user.getTeamId()){
                teamSummonersList.add(s);
            }
        }

        Collections.sort(teamSummonersList, new SortSummonerId());

        // Changement des bitmap
        this.setTimerButtonsImage(teamSummonersList);

        //Chargement des timers
        this.buildTimerTable(teamSummonersList);

        //
        Handler timerHandler = new Handler();
        GlobalDataManager.add("timerHandler", timerHandler);
    }

    //This functions adds dynamically a player icon in the channel summary so user can know who is connected
    protected void appendPlayerIconToChannelSummary(Bitmap playerIcon){
        LinearLayout channelSummary = (LinearLayout) getActivity().findViewById(R.id.channel_summary);
        RoundedImageView riv = new RoundedImageView(getActivity());
        riv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        riv.setCornerRadius((float) 25);
        riv.setBorderWidth((float) 1);
        riv.setBorderColor(Color.WHITE);
        riv.mutateBackground(true);
        riv.setImageBitmap(playerIcon);
        riv.setOval(true);
        riv.setTileModeX(Shader.TileMode.CLAMP);
        riv.setTileModeY(Shader.TileMode.CLAMP);

        //adding a icon to the channel summary
        channelSummary.addView(riv, 25, 25);
    }

    public void secureAppSharing(View v){
        DialogFragment dialog = new SecureDialogFragment();
        dialog.show(getFragmentManager(), "tips");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String passphrase) {
        // Websocket - secure channel
        this.settingsManager.set(getActivity(), "passphrase", passphrase);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }

    public void setTimerButtonsImage(ArrayList<Summoner> teamSummonersList){
        this.setChampionTimerButtonsImage(teamSummonersList);
        this.setUltimateTimerButtonsImage(teamSummonersList);
        this.setSpellsTimerButtonsImage(teamSummonersList);
    }

    public void setChampionTimerButtonsImage(ArrayList<Summoner> summonersList){
        List<String> ids = Arrays.asList("b11", "b21", "b31", "b41", "b51");
        RoundedImageView tb;
        int IDRessource;

        int i = 0;
        for (String s : ids){
            IDRessource = getResources().getIdentifier(s, "id", getActivity().getBaseContext().getPackageName());
            tb = (RoundedImageView) getActivity().findViewById(IDRessource);
            Bitmap bm = summonersList.get(i).getChampion().getIcon();
            tb.setImageBitmap(bm);
            i++;
        }
    }

    public void setUltimateTimerButtonsImage(ArrayList<Summoner> summonersList){
        List<String> ids = Arrays.asList("b12", "b22", "b32", "b42", "b52");
        TimerButton tb;
        int IDRessource;

        int i = 0;
        for (String s : ids){
            IDRessource = getResources().getIdentifier(s, "id",  getActivity().getBaseContext().getPackageName());
            tb = (TimerButton)  getActivity().findViewById(IDRessource);
            tb.setImageBitmap(summonersList.get(i).getChampion().getSpell().getIcon());
            i++;
        }
    }

    public void setSpellsTimerButtonsImage(ArrayList<Summoner> summonersList){
        List<String> ids = Arrays.asList("b13", "b23", "b33", "b43", "b53");
        TimerButton tb;
        int IDRessource;

        int i = 0;
        for (String s : ids){
            IDRessource = getResources().getIdentifier(s, "id",  getActivity().getBaseContext().getPackageName());
            tb = (TimerButton)  getActivity().findViewById(IDRessource);
            tb.setImageBitmap(summonersList.get(i).getSpells()[0].getIcon());
            i++;
        }

        ids = Arrays.asList("b14", "b24", "b34", "b44", "b54");
        i = 0;
        for (String s : ids){
            IDRessource = getResources().getIdentifier(s, "id",  getActivity().getBaseContext().getPackageName());
            tb = (TimerButton)  getActivity().findViewById(IDRessource);
            tb.setImageBitmap(summonersList.get(i).getSpells()[1].getIcon());
            i++;
        }
    }

    public void buildTimerTable(ArrayList<Summoner> teamSummonersList){
        List<String> summonerSpellButtons = Arrays.asList("b13", "b14", "b23", "b24", "b33", "b34", "b43", "b44", "b53", "b54");
        List<String> ultimateButtons = Arrays.asList("b12", "b22", "b32", "b42", "b52");

        timerMap.put("b01",(long)420);
        timerMap.put("b02",(long)360);

        int spellIndex = 0;
        int summonerIndex = 0;

        for (String s : summonerSpellButtons){
            float cdSummonerSpell = teamSummonersList.get(summonerIndex).getSpells()[spellIndex].getCooldown()[0];
            timerMap.put(s,(long)cdSummonerSpell);

            if(spellIndex == 1) {
                summonerIndex++;
                spellIndex = 0;
            } else {
                spellIndex++;
            }
        }

        summonerIndex = 0;
        for (String s : ultimateButtons){
            float cdSummonerSpell = teamSummonersList.get(summonerIndex).getChampion().getSpell().getCooldown()[0];
            timerMap.put(s, (long) cdSummonerSpell);
            summonerIndex++;
        }


        Log.v("DAO", "Timer des tableau chargement termine");
    }

    public void timerCancel(View tbt){
        TimerButton tbtn = (TimerButton) tbt;
        String IDButton = getResources().getResourceName(tbtn.getId());
        String buttonID = IDButton.substring(IDButton.lastIndexOf("/") + 1);
    }

    //Fonctions pour les évènements WS
    public void simpleClickTimer(String buttonID,long delayOfTransfert, boolean fromWebSocket){
        TimerButton tbtn = getButtonFromIdString(buttonID);

        //SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.S Z");
        //formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp tstmp = new Timestamp(new Date().getTime());
        /*try {
            tstmp = new Timestamp(formatUTC.parse(formatUTC.format(new Date())).getTime());
        } catch (ParseException e) {
            tstmp = new Timestamp(new Date().getTime());
            Log.v("Websocket","Impossible de parser la date recue via le websocket");
        }*/

        Handler timerHandler = (Handler)GlobalDataManager.get("timerHandler");

        //Name of the clicked button => example : b21
        String IDButton = getResources().getResourceName(tbtn.getId());
        //loading the league of legend equiv fonts
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/lol.ttf");

        //Timer is null and has never been instancied
        if(tbtn.getTimer() == null){
            //Setting the TextView so the timer update the countdown in FO
            int timerTextViewID = getResources().getIdentifier(IDButton.concat("t"), "id", getActivity().getBaseContext().getPackageName());
            //settings the textView with the font
            TextView txtv = (TextView) getActivity().findViewById(timerTextViewID);
            txtv.setTypeface(font);
            tbtn.setTimer(new Timer(0, 0, txtv, tbtn));

            if(!fromWebSocket){
                WsEventHandling.timerActivation(buttonID, tstmp.toString());
            }
            //On active le timer
            long timeToCount = timerMap.get(buttonID) * 1000 - delayOfTransfert;
            tbtn.setTimer(new Timer(timeToCount,1000,tbtn.getTimer().getTimerTextView(), tbtn));
            tbtn.getTimer().start();
            tbtn.getTimer().setVisible(true);
        } else {
            //On transmet le message
            if(!fromWebSocket){
                WsEventHandling.timerDelay(buttonID);
            }
            //On fait l'action sur le timerbutton
            tbtn.timerDelay(5000);
        }
    }

    public void resetTimer(String buttonID, boolean fromWebSocket){
        TimerButton tbtn = getButtonFromIdString(buttonID);

        if (tbtn.getTimer() != null && tbtn.getTimer().isTicking()) {
            //On transmet le message
            if(!fromWebSocket){
                WsEventHandling.resetTimer(buttonID);
            }
            //On fait l'action sur le timerbutton
            tbtn.getTimer().cancel();
        }
    }

    public void stopTimer(String buttonID, boolean fromWebSocket){
        TimerButton tbtn = getButtonFromIdString(buttonID);

        if (tbtn.getTimer() != null && tbtn.getTimer().isTicking()) {
            //On transmet le message
            if(!fromWebSocket){
                WsEventHandling.stopTimer(buttonID);
            }
            //On fait l'action sur le timerbutton
            tbtn.getTimer().cancel();
            simpleClickTimer(buttonID,0,true);
        }
    }

    public TimerButton getButtonFromIdString(String buttonID){
        return (TimerButton) getActivity().findViewById(getResources().getIdentifier(buttonID, "id", getActivity().getPackageName()));
    }

}
