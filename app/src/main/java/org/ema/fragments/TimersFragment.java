package org.ema.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
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
import org.ema.utils.GameTimestamp;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LoLStatActivity;
import org.ema.utils.SecureDialogFragment;
import org.ema.utils.SettingsManager;
import org.ema.utils.SortSummonerId;
import org.ema.utils.Timer;
import org.ema.utils.TimerButton;
import org.ema.utils.WebSocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TimersFragment extends LoLStatActivity implements SecureDialogFragment.NoticeDialogListener {
    public static TimersFragment instance = null;
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
        TimersFragment.instance = this;
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
    }

    public void cleanChannelSummary(){
        LinearLayout channelSummary = (LinearLayout) getActivity().findViewById(R.id.channel_summary);
        channelSummary.removeAllViewsInLayout();
    }

    //This functions adds dynamically a player icon in the channel summary so user can know who is connected
    public void appendPlayerIconToChannelSummary(Bitmap playerIcon){
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
        channelSummary.addView(riv, 50, 50);
    }

    public void secureAppSharing(View v){
        DialogFragment dialog = new SecureDialogFragment();
        dialog.show(getFragmentManager(), "tips");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String passphrase) {
        // Websocket - secure channel
        String oldChannel = this.settingsManager.get(this.getActivity(),"passphrase");

        //Si la nouvelle passphrase est differente de l'ancienne
        if (!oldChannel.equals(passphrase)) {
            this.settingsManager.set(this.getActivity(), "passphrase", passphrase);
            WsEventHandling.switchChannel(passphrase);
        }
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

        timerMap.put("b01",(long)700);
        timerMap.put("b02",(long)600);

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

    //Fonctions pour les évènements WS
    public void simpleClickTimer(String buttonID,long delayOfTransfert, boolean fromWebSocket, boolean doTimerActivation){

        if (timerMap.get(buttonID) * 1000 > delayOfTransfert) {
            TimerButton tbtn = getButtonFromIdString(buttonID);

            //Name of the clicked button => example : b21
            String IDButton = getResources().getResourceName(tbtn.getId());
            //loading the league of legend equiv fonts
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");

            //Timer is null and has never been instancied
            if (tbtn.getTimer() == null) {
                //Setting the TextView so the timer update the countdown in FO
                int timerTextViewID = getResources().getIdentifier(IDButton.concat("t"), "id", getActivity().getBaseContext().getPackageName());
                //settings the textView with the font
                TextView txtv = (TextView) getActivity().findViewById(timerTextViewID);
                txtv.setTypeface(font);
                tbtn.setTimer(new Timer(0, 0, txtv, tbtn));

                if (!fromWebSocket) {
                    WsEventHandling.timerActivation(buttonID, Long.toString(GameTimestamp.getServerTimestamp()));
                }
                //On active le time
                //Si on a des cooldown a 0 ou inférieur au temps de transfert, on met pas de timer
                tbtn.setTimer(new Timer((timerMap.get(buttonID) * 1000) - delayOfTransfert, 1000, tbtn.getTimer().getTimerTextView(), tbtn));
                tbtn.getTimer().start();
                tbtn.getTimer().setVisible(true);
            } else if (tbtn.getTimer() != null && doTimerActivation != true) {
                //On transmet le message
                if (!fromWebSocket) {
                    WsEventHandling.timerDelay(buttonID);
                }
                //On fait l'action sur le timerbutton
                tbtn.timerDelay(5000);
            }
        }
    }

    /**
     * FUNCTION DISABLED
     */
    /*
    public void restartTimer(String buttonID, long timestamp, boolean fromWebSocket) {
        TimerButton tbtn = getButtonFromIdString(buttonID);
        Timestamp tstmp = new Timestamp(new Date().getTime());

        if (tbtn.getTimer() != null && tbtn.getTimer().isTicking()) {
            //On transmet le message
            if (!fromWebSocket) {
                WsEventHandling.restartTimer(buttonID, tstmp.toString());
            }
            //On fait l'action sur le timerbutton
            tbtn.getTimer().onFinish();
            simpleClickTimer(buttonID, timestamp, true, false);
    }
    */

    public void stopTimer(String buttonID, boolean fromWebSocket){
        class TimerAction implements Runnable {
            String buttonID;
            TimerButton tbtn;
            boolean fromWebSocket;

            public TimerAction(String buttonID, boolean fromWebSocket){
                this.buttonID = buttonID;
                this.tbtn = getButtonFromIdString(buttonID);
                this.fromWebSocket = fromWebSocket;
            }

            @Override
            public void run(){
                if (tbtn.getTimer() != null && tbtn.getTimer().isTicking()) {
                    //On transmet le message
                    if(!fromWebSocket){
                        WsEventHandling.stopTimer(buttonID);
                    }
                    //On fait l'action sur le timerbutton
                    tbtn.getTimer().onFinish();
                }
            }
        }

        Handler h = new Handler();
        h.post(new TimerAction(buttonID, fromWebSocket));
    }

    public TimerButton getButtonFromIdString(String buttonID){
        return (TimerButton) getActivity().findViewById(getResources().getIdentifier(buttonID, "id", getActivity().getPackageName()));
    }

    public String[][] shareTimers(){
        List<String> timerButtons = Arrays.asList("b12","b13", "b14", "b22","b23", "b24", "b32","b33", "b34","b42", "b43", "b44", "b52","b53", "b54");

        String[][] timersTableToShare = new String[15][2];
        int count = 0;

        for(int i = 0; i < 15;i++){
            String buttonID = timerButtons.get(i);
            TimerButton tbtn = getButtonFromIdString(buttonID);

            //Si le timer est présent et qu'il est en marche
            if (tbtn.getTimer() != null && tbtn.getTimer().isTicking() ) {
                String timerCurrentTime = tbtn.getTimer().getTimerTextView().getText().toString();
                long currentTimestamp = Long.parseLong(timerCurrentTime) * 1000;
                timersTableToShare[count][0] = buttonID;
                timersTableToShare[count][1] = Long.toString(currentTimestamp);
                count++;
            }
        }

        String[][] trimmedTable = new String[count][2];

        for(int k = 0; k< count;k++){
            trimmedTable[k][0] = timersTableToShare[k][0];
            trimmedTable[k][1] = timersTableToShare[k][1];
            Log.v("Websocket","On remplis le tableau avec [" + timersTableToShare[k][0] + "][" +  timersTableToShare[k][1] + "]" );
        }

        return trimmedTable;
    }

}
