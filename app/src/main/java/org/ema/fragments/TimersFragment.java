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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.ema.dialogs.SecureDialogFragment;
import org.ema.lolcompanion.R;
import org.ema.lolcompanion.WsEventHandling;
import org.ema.model.business.Summoner;
import org.ema.utils.GameTimestamp;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LogUtils;
import org.ema.utils.SettingsManager;
import org.ema.utils.Timer;
import org.ema.utils.TimerButton;
import org.ema.utils.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TimersFragment extends SummonersListFragment implements SecureDialogFragment.NoticeDialogListener {
    public HashMap<String, Long> timerMap;
    public HashMap<String, Integer> timerCdrMap;
    public HashMap<String, Integer> timerUltiLvlMap;
    public static SettingsManager settingsManager = null;

    public static Integer numberOfPlayersPerTeam;
    public static Integer numberOfTimers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.activity_timer, container, false);
        timerMap = new HashMap<String, Long>();

        //Say to the websocket that we are not disconnected from the server
        WebSocket.alreadyDisconnected = false;

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");
        TextView timers = (TextView) rootView.findViewById(R.id.timers);
        timers.setTypeface(font);

        //we hide nashor and drake because of Riot update (nashor / drake timers are now available on game)
        //we keep the code here, in order to enable them again if Riot changes its mind.
        rootView.findViewById(R.id.b01c).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.b02c).setVisibility(View.INVISIBLE);

        TimersFragment.settingsManager = new SettingsManager();
        PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());

        //adding a default passphrase
        TimersFragment.settingsManager.set(this.getActivity(), "passphrase", "myteam");

        //Connect the websocket and send the picked champion to the server
        WebSocket.connectWebSocket();

        //Get the summoner list
        Summoner user = (Summoner) GlobalDataManager.get("user");
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>) GlobalDataManager.get("summonersList");

        //Filtering to get the list of the opponent team of the player
        // Recuperation et tri des summoners de l'equipe du joueur
        ArrayList<Summoner> teamSummonersList = new ArrayList<Summoner>();
        for (Summoner s : summonersList) {
            if (s.getTeamId() != user.getTeamId()) {
                teamSummonersList.add(s);
            }
        }

        //We get the container where we are going to add all the champion lines
        LinearLayout ennemies_container = (LinearLayout) rootView.findViewById(R.id.root_list_timers);
        //Create summoner lines
        for (int idForLine = 0; idForLine < teamSummonersList.size(); idForLine++) {
            fillTimersInformations(ennemies_container, idForLine+1, teamSummonersList.get(idForLine));
        }


        return rootView;
    }

    //THREAD METHODS
    @Override
    public void onStart() {
        super.onStart();

        //Get the summoner list
        Summoner user = (Summoner) GlobalDataManager.get("user");
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>) GlobalDataManager.get("summonersList");

        //Filtering to get the list of the opponent team of the player
        // Recuperation et tri des summoners de l'equipe du joueur
        ArrayList<Summoner> teamSummonersList = new ArrayList<Summoner>();
        for (Summoner s : summonersList) {
            if (s.getTeamId() != user.getTeamId()) {
                teamSummonersList.add(s);
            }
        }

        //Used to initialize the good number of timers on the timer page
        numberOfPlayersPerTeam = teamSummonersList.size();

        //Set the cooldown to 0 and the ultimate level to 6 for all champ in the two hashmap
        InitializeTimerCdrMapAndTimerUltiLvlMap();

        //Used to initialize timers
        numberOfTimers = numberOfPlayersPerTeam * 3;

        //We had 2 more timer if we are in 5v5 for the nash and drake, and 1 timer in 3v3 for the spider
        if (numberOfPlayersPerTeam == 5) {
            numberOfTimers += 2;
        } else if (numberOfPlayersPerTeam == 3) {
            numberOfTimers += 1;
        }

        // Bitmap loading
        this.setTimerButtonsImage(teamSummonersList);

        //ACHTUNG !!! Du bist eine grosse Wurst.
        //For the moment, if the number of the player is different of 10 we don't loading timers because the DAO is not ready
        if (summonersList.size() == 10) {
            //Creation of the hashmap where the index of the timer (ex: b12) and the cooldown are listed. (Ex : b12:23sec, b13:45sec...)
            this.buildTimerTable(teamSummonersList);
        }


    }

    public void InitializeTimerCdrMapAndTimerUltiLvlMap() {

        //Theses hashmap are used to calculate the timer cooldown.
        //To calculate the timer cooldown we take the cooldown from the timerMap , and we retranch the CDR from timerCdrMap.
        timerCdrMap = new HashMap<String, Integer>();

        //In this hashmap we store the ultimate level of the champ. It's used to get the good cooldown from the DAO.
        timerUltiLvlMap = new HashMap<String, Integer>();

        //For each players in the enemy team we add the
        for (int i = 1; i < numberOfPlayersPerTeam + 1; i++) {
            timerCdrMap.put("b" + i + "2", 0);
            timerUltiLvlMap.put("b" + i + "2", 6);
        }
    }

    //Clean the view where there is the icon list of the mates in the same channel
    public void cleanChannelSummary() {
        LinearLayout channelSummary = (LinearLayout) getActivity().findViewById(R.id.channel_summary);
        channelSummary.removeAllViewsInLayout();
        channelSummary.refreshDrawableState();
    }

    //This functions adds dynamically a player icon in the channel summary so user can know who is connected
    public void appendPlayerIconToChannelSummary(Bitmap playerIcon) {
        LogUtils.LOGV("Websocket", "On passe dans la fonction AppendPlayer");
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
        channelSummary.addView(riv,
                Math.round(getResources().getDimension(R.dimen.timers_summoner_channel_icon_size)),
                Math.round(getResources().getDimension(R.dimen.timers_summoner_channel_icon_size)));
    }

    public void secureAppSharing(View v) {
        DialogFragment dialog = new SecureDialogFragment();
        dialog.show(getFragmentManager(), "tips");
    }

    //Function that handles passphrase dialog returned vars
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String passphrase) {
        // Websocket - secure channel
        String oldChannel = TimersFragment.settingsManager.get(this.getActivity(), "passphrase");

        //If the old passphrase is different, we save the new
        if (!oldChannel.equals(passphrase)) {
            TimersFragment.settingsManager.set(this.getActivity(), "passphrase", passphrase);
            //Switching channel
            WsEventHandling.switchChannel(passphrase);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }

    //Loading images for all timers
    public void setTimerButtonsImage(ArrayList<Summoner> teamSummonersList) {
        //ACHTUNG !!! Du bist eine grosse Wurst.
        //For the moment, if the number of the player in a team is different of 5 we don't loading timers because the DAO is not ready
        //if (teamSummonersList.size() == 5) {
            this.setChampionTimerButtonsImage(teamSummonersList);
            this.setUltimateTimerButtonsImage(teamSummonersList);
            this.setSpellsTimerButtonsImage(teamSummonersList);
        //}
    }

    //Load the champions icons
    public void setChampionTimerButtonsImage(ArrayList<Summoner> summonersList) {
        List<String> ids = new ArrayList<>();
        RoundedImageView tb;
        int IDRessource;

        //For each players in the enemy team, we had the reference to the champion icon (ex: b11,b21,b31...)
        for (int i = 1; i < numberOfPlayersPerTeam + 1; i++) {
            ids.add("b" + i + "1");
        }

        int i = 0;
        //For each references we had before, we load the images in the referenced element in the view
        for (String s : ids) {
            IDRessource = getResources().getIdentifier(s, "id", getActivity().getBaseContext().getPackageName());
            tb = (RoundedImageView) getActivity().findViewById(IDRessource);
            Bitmap bm = summonersList.get(i).getChampion().getIcon();
            tb.setImageBitmap(bm);
            i++;
        }
    }

    //Load the champions ultimate icons
    public void setUltimateTimerButtonsImage(ArrayList<Summoner> summonersList) {
        List<String> ids = new ArrayList<>();

        //For each players in the enemy team, we had the reference to the ultimate icon (ex: b12,b22,b32...)
        for (int i = 1; i < numberOfPlayersPerTeam + 1; i++) {
            ids.add("b" + i + "2");
        }

        TimerButton tb;
        int IDRessource;

        int i = 0;
        //For each references we had before, we load the images in the referenced element in the view
        for (String s : ids) {
            IDRessource = getResources().getIdentifier(s, "id", getActivity().getBaseContext().getPackageName());
            tb = (TimerButton) getActivity().findViewById(IDRessource);
            tb.setImageBitmap(summonersList.get(i).getChampion().getSpell().getIcon());
            i++;
        }
    }

    //Load the champions summoner spells icons
    public void setSpellsTimerButtonsImage(ArrayList<Summoner> summonersList) {
        List<String> ids = new ArrayList<>();

        //For each players in the enemy team, we had the reference to the first summoner spell icon (ex: b13,b23,b33...)
        for (int i = 1; i < numberOfPlayersPerTeam + 1; i++) {
            ids.add("b" + i + "3");
        }

        TimerButton tb;
        int IDRessource;

        int i = 0;
        //For each references we had before, we load the images in the referenced element in the view
        for (String s : ids) {
            IDRessource = getResources().getIdentifier(s, "id", getActivity().getBaseContext().getPackageName());
            tb = (TimerButton) getActivity().findViewById(IDRessource);
            tb.setImageBitmap(summonersList.get(i).getSpells()[0].getIcon());
            i++;
        }

        ids.removeAll(ids);

        //For each players in the enemy team, we had the reference to the second summoner spell icon (ex: b14,b24,b34...)
        for (int j = 1; j < numberOfPlayersPerTeam + 1; j++) {
            ids.add("b" + j + "4");
        }

        i = 0;
        //For each references we had before, we load the images in the referenced element in the view
        for (String s : ids) {
            IDRessource = getResources().getIdentifier(s, "id", getActivity().getBaseContext().getPackageName());
            tb = (TimerButton) getActivity().findViewById(IDRessource);
            tb.setImageBitmap(summonersList.get(i).getSpells()[1].getIcon());
            i++;
        }
    }

    //Build the hashMap where cooldown will be stored. This hashmap will be used each time we push a timer
    public void buildTimerTable(ArrayList<Summoner> teamSummonersList) {
        List<String> summonerSpellButtons = new ArrayList<>();
        List<String> ultimateButtons = new ArrayList<>();

        //For each line, we reference three buttons in a List (1 for ulti, 2 for summoners spells)
        for (int i = 1; i < numberOfPlayersPerTeam + 1; i++) {
            summonerSpellButtons.add("b" + i + "3");
            summonerSpellButtons.add("b" + i + "4");
            ultimateButtons.add("b" + i + "2");
        }

        //Depending of the number of players, we had a cooldown the drake / nashor or the spider
        if (numberOfPlayersPerTeam == 5) {
            timerMap.put("b01", (long) 420);
            timerMap.put("b02", (long) 360);
        } else if (numberOfPlayersPerTeam == 3) {
            timerMap.put("b01", (long) 300);
        }

        int spellIndex = 0;
        int summonerIndex = 0;

        //For each button referenced before, we get the cooldown from the DAO

        //First we start with the two summoners spells
        for (String s : summonerSpellButtons) {
            float cdSummonerSpell = teamSummonersList.get(summonerIndex).getSpells()[spellIndex].getCooldown()[0];
            timerMap.put(s, (long) cdSummonerSpell);

            if (spellIndex == 1) {
                summonerIndex++;
                spellIndex = 0;
            } else {
                spellIndex++;
            }
        }

        //Then with the ultimates spells
        summonerIndex = 0;
        for (String s : ultimateButtons) {
            float cdSummonerSpell = teamSummonersList.get(summonerIndex).getChampion().getSpell().getCooldown()[0];
            timerMap.put(s, (long) cdSummonerSpell);
            summonerIndex++;
        }
    }

    //Fonctions pour les évènements WS
    public void simpleClickTimer(String buttonID, long delayOfTransfert, boolean fromWebSocket, boolean doTimerActivation) {
        TimerButton tbtn = getButtonFromIdString(buttonID);

        //Name of the clicked button => example : b21
        String IDButton = getResources().getResourceName(tbtn.getId());
        //loading the league of legend equiv fonts
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");

        //Timer is null and has never been instancied
        if (tbtn.getTimer() == null && doTimerActivation) {
            long timerDelayToUse;
            //If the cooldown is lower than the delay of transfert, we set the timer to 1 second
            if (timerMap.get(buttonID) * 1000 > delayOfTransfert) {
                //If it's an utimate, we use the CDR
                if (timerCdrMap.containsKey(buttonID)) {
                    //Calculation of the timer time by applying the CDR
                    try {
                        //Divided by 100 to get a number between 0 and 1 for the next multiplication
                        Double cdr = ((double) timerCdrMap.get(buttonID)) / 100;

                        //Spell cooldown without cdr% of this spell
                        Double timerDelayWithCdr = timerMap.get(buttonID) * 1000 - (timerMap.get(buttonID) * 1000) * cdr;
                        timerDelayToUse = Math.round(timerDelayWithCdr);

                        LogUtils.LOGV("Websocket", "Cdr:" + Double.toString(cdr) + ".TimerSpell:" + Double.toString(timerMap.get(buttonID)));
                    } catch (Exception e) {
                        timerDelayToUse = timerMap.get(buttonID) * 1000 - delayOfTransfert;
                    }
                } else {
                    //Else if it's a summoner spell, we get the cooldown from hashmap without CDR
                    timerDelayToUse = timerMap.get(buttonID) * 1000 - delayOfTransfert;
                }
            } else {
                timerDelayToUse = 1000;
            }

            //Setting the TextView so the timer update the countdown in FO
            int timerTextViewID = getResources().getIdentifier(IDButton.concat("t"), "id", getActivity().getBaseContext().getPackageName());
            //settings the textView with the font
            TextView txtv = (TextView) getActivity().findViewById(timerTextViewID);
            txtv.setTypeface(font);
            tbtn.setTimer(new Timer(0, 0, txtv, tbtn));

            if (timerDelayToUse > 1000) {
                if (!fromWebSocket) {
                    WsEventHandling.timerActivation(buttonID, Long.toString(GameTimestamp.getServerTimestamp()));
                }
            }

            //On active le time
            //Si on a des cooldown a 0 ou inférieur au temps de transfert, on met pas de timer
            tbtn.setTimer(new Timer(timerDelayToUse, 1000, tbtn.getTimer().getTimerTextView(), tbtn));
            tbtn.getTimer().start();
            tbtn.getTimer().setVisible(true);
        } else if (tbtn.getTimer() != null && !doTimerActivation) {
            //On transmet le message
            if (!fromWebSocket) {
                WsEventHandling.timerDelay(buttonID);
            }
            //On fait l'action sur le timerbutton
            tbtn.timerDelay(5000);
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

    //Called from the WsEventHandling to stop a timer
    public void stopTimer(String buttonID, boolean fromWebSocket) {
        class TimerAction implements Runnable {
            String buttonID;
            TimerButton tbtn;
            boolean fromWebSocket;

            public TimerAction(String buttonID, boolean fromWebSocket) {
                this.buttonID = buttonID;
                this.tbtn = getButtonFromIdString(buttonID);
                this.fromWebSocket = fromWebSocket;
            }

            @Override
            public void run() {
                if (tbtn.getTimer() != null && tbtn.getTimer().isTicking()) {
                    //On transmet le message
                    if (!fromWebSocket) {
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

    //Return a TimerButton from the button ID
    public TimerButton getButtonFromIdString(String buttonID) {
        return (TimerButton) getActivity().findViewById(getResources().getIdentifier(buttonID, "id", getActivity().getPackageName()));
    }

    //Return a TextView from the button ID
    public TextView getTextViewFromIdString(String textViewID) {
        return (TextView) getActivity().findViewById(getResources().getIdentifier(textViewID, "id", getActivity().getPackageName()));
    }

    //Called from the WsEventHandling to activate a timer
    public void activateTimer(final String buttonID, final long timerDelay) {
        this.getActivity().runOnUiThread(new Runnable() {
            public void run() {

                TimerButton tbtn = getButtonFromIdString(buttonID);
                //loading the league of legend equiv fonts
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");

                if (tbtn.getTimer() == null) {
                    //Setting the TextView so the timer update the countdown in FO
                    int timerTextViewID = getResources().getIdentifier(buttonID.concat("t"), "id", getActivity().getBaseContext().getPackageName());
                    //settings the textView with the font
                    TextView txtv = (TextView) getActivity().findViewById(timerTextViewID);
                    txtv.setTypeface(font);
                    tbtn.setTimer(new Timer(0, 0, txtv, tbtn));
                }

                //On active le timer
                tbtn.setTimer(new Timer(timerDelay, 1000, tbtn.getTimer().getTimerTextView(), tbtn));
                tbtn.getTimer().start();
                tbtn.getTimer().setVisible(true);
            }
        });
    }

    //Initialize the timerButton table. This table is used in an other function to share timers with new mate that joined the channel or to cancel all timers
    public ArrayList<String> initializeTimerButtonsTable() {

        ArrayList<String> timerButtons = new ArrayList<>();

        if (numberOfPlayersPerTeam == 5) {
            timerButtons.add("b01");
            timerButtons.add("b02");
        } else if (numberOfPlayersPerTeam == 3) {
            timerButtons.add("b01");
        }

        for (int i = 1; i < numberOfPlayersPerTeam + 1; i++) {
            timerButtons.add("b" + i + "2");
            timerButtons.add("b" + i + "3");
            timerButtons.add("b" + i + "4");
        }

        return timerButtons;
    }

    //Called from the WsEventHandling to shareAlLTimers to a new mate that joined the game
    public String[][] shareTimers() {
        List<String> timerButtons = initializeTimerButtonsTable();

        String[][] timersTableToShare = new String[numberOfTimers][2];
        int count = 0;

        //For each timers in the view
        for (int i = 0; i < numberOfTimers; i++) {
            //Get the timer ID
            String buttonID = timerButtons.get(i);
            TimerButton tbtn = getButtonFromIdString(buttonID);

            //If the timer is ticking, we had it to the share table
            if (tbtn.getTimer() != null && tbtn.getTimer().isTicking()) {
                String timerCurrentTime = tbtn.getTimer().getTimerTextView().getText().toString();
                long currentTimestamp = Long.parseLong(timerCurrentTime) * 1000;
                timersTableToShare[count][0] = buttonID;
                timersTableToShare[count][1] = Long.toString(currentTimestamp);
                count++;
            }
        }

        //When the loop before is finish, the table have some blank cases, so we trim the table to get a well formed table
        String[][] trimmedTable = new String[count][2];

        for (int k = 0; k < count; k++) {
            trimmedTable[k][0] = timersTableToShare[k][0];
            trimmedTable[k][1] = timersTableToShare[k][1];
        }

        return trimmedTable;
    }

    //Called from the WsEventHandling to cancelAllTimers when you received the shared timers from your mates
    public void cancelAllTimers() {

        this.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                List<String> timerButtons = initializeTimerButtonsTable();

                for (int i = 0; i < numberOfTimers; i++) {
                    String buttonID = timerButtons.get(i);
                    TimerButton tbtn = getButtonFromIdString(buttonID);
                    //Si le timer est présent et qu'il est en marche
                    if (tbtn.getTimer() != null && tbtn.getTimer().isTicking()) {
                        tbtn.getTimer().onFinish();
                    }
                }
            }
        });
    }

    //Called from the WsEventHandling to set the new cdr to a champion
    public void setCdr(String buttonId, Integer cdr) {
        //The button bx0 is sended from the other device, but we neeed to put in timerUltiLvlMap a button with this form bx2. So we substring
        timerCdrMap.put(buttonId.substring(0,2) + "2", cdr);

        final String cdrString =  cdr.toString() + "%";
        final String buttonIdFinal = buttonId;

        this.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                //We add "t" because the % of the button is named bxxt
                TextView tbtn = getTextViewFromIdString(buttonIdFinal + "t");
                tbtn.setText(cdrString);
            }});

    }

    //Called from the WsEventHandling to set the new ultimate level to a champion
    public void setUltimateLevel(String buttonId, Integer ultiLvl) {

        //The button bx1 is sended from the other device, but we neeed to put in timerUltiLvlMap a button with this form bx2. So we substring
        timerUltiLvlMap.put(buttonId.substring(0,2) + "2", ultiLvl);
        updateCooldownWithNewUltimateLevel(buttonId.substring(0,2) + "2", ultiLvl);

        final String niveauString =  ultiLvl.toString();
        final String buttonIdFinal = buttonId;

        this.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                //We add "t" because the % of the button is named bxxt
                TextView tbtn = getTextViewFromIdString(buttonIdFinal + "t");
                tbtn.setText(niveauString);
            }});
    }

    //When a ultimate level is update, we need to update the timerMap who contain the cooldown for all timers
    public void updateCooldownWithNewUltimateLevel(String buttonId, Integer ultiLevel) {
        // Summoner user = (Summoner) GlobalDataManager.get("user");
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>) GlobalDataManager.get("summonersList");

        //Parsing button name to get the summoner index. We retrench 1 because the index start at 0 whereas buttons start at 1
        int summonerIndex = Integer.parseInt(buttonId.substring(1, 2)) - 1;

        //Parsing ultiLevel to get the index in cooldown list
        int indexCooldown;

        //We get the index of the cooldown ultimate in the DAO
        switch (ultiLevel) {
            case 6:
                indexCooldown = 0;
                break;
            case 11:
                indexCooldown = 1;
                break;
            case 16:
                indexCooldown = 2;
                break;
            default:
                indexCooldown = 0;
                break;
        }

        //get the cooldown from the DAO
        float cooldownRatioByLevel = (float) 1 - (float) ((float) ((float) 1 - (new Float(summonersList.get(summonerIndex).getCooldownPerLevelAndCalculCooldowns()))) * (float) ultiLevel);
        float cdSummonerSpell = summonersList.get(summonerIndex).getChampion().getSpell().getCooldown()[indexCooldown] * cooldownRatioByLevel;

        LogUtils.LOGV("Websocket", "Update champ " + summonerIndex + " to level " + ultiLevel + " so index cooldown " + indexCooldown);

        timerMap.put(buttonId, (long) cdSummonerSpell);
    }


    //This function will generate one timers champ line - has to be called in the foreach using the summoner list retrieve by DAO
    public void fillTimersInformations(LinearLayout containerView, int idForLine, Summoner summoner) {

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lol.ttf");
        View rootview = getActivity().getLayoutInflater().inflate(R.layout.line_timers, containerView, false);
        int IDRessource;

        //for each ids in the layouts, we need to set the good ID to bind all events on the timers !
        RelativeLayout champion_container = (RelativeLayout) rootview.findViewById(R.id.b11c);
        IDRessource = getResources().getIdentifier("b"+idForLine+"1c", "id", getActivity().getBaseContext().getPackageName());
        champion_container.setId(IDRessource);
        RoundedImageView champion_image =  (RoundedImageView) rootview.findViewById(R.id.b11);
        IDRessource = getResources().getIdentifier("b"+idForLine+"1", "id", getActivity().getBaseContext().getPackageName());
        champion_image.setId(IDRessource);
        TextView champion_lvl = (TextView) rootview.findViewById(R.id.b11t);
        IDRessource = getResources().getIdentifier("b"+idForLine+"1t", "id", getActivity().getBaseContext().getPackageName());
        champion_lvl.setId(IDRessource);

        TimerButton cdr_button = (TimerButton) rootview.findViewById(R.id.b10);
        IDRessource = getResources().getIdentifier("b"+idForLine+"0", "id", getActivity().getBaseContext().getPackageName());
        cdr_button.setId(IDRessource);
        TextView cdr_value = (TextView) rootview.findViewById(R.id.b10t);
        IDRessource = getResources().getIdentifier("b"+idForLine+"0t", "id", getActivity().getBaseContext().getPackageName());
        cdr_value.setId(IDRessource);

        RelativeLayout champion_ult_container = (RelativeLayout) rootview.findViewById(R.id.b12c);
        IDRessource = getResources().getIdentifier("b"+idForLine+"2c", "id", getActivity().getBaseContext().getPackageName());
        champion_ult_container.setId(IDRessource);
        TimerButton champion_ult_btn =  (TimerButton) rootview.findViewById(R.id.b12);
        IDRessource = getResources().getIdentifier("b"+idForLine+"2", "id", getActivity().getBaseContext().getPackageName());
        champion_ult_btn.setId(IDRessource);
        TextView champion_ult_txt = (TextView) rootview.findViewById(R.id.b12t);
        IDRessource = getResources().getIdentifier("b"+idForLine+"2t", "id", getActivity().getBaseContext().getPackageName());
        champion_ult_txt.setId(IDRessource);

        RelativeLayout champion_sk1_container = (RelativeLayout) rootview.findViewById(R.id.b13c);
        IDRessource = getResources().getIdentifier("b"+idForLine+"3c", "id", getActivity().getBaseContext().getPackageName());
        champion_sk1_container.setId(IDRessource);
        TimerButton champion_sk1_btn =  (TimerButton) rootview.findViewById(R.id.b13);
        IDRessource = getResources().getIdentifier("b"+idForLine+"3", "id", getActivity().getBaseContext().getPackageName());
        champion_sk1_btn.setId(IDRessource);
        TextView champion_sk1_txt = (TextView) rootview.findViewById(R.id.b13t);
        IDRessource = getResources().getIdentifier("b"+idForLine+"3t", "id", getActivity().getBaseContext().getPackageName());
        champion_sk1_txt.setId(IDRessource);

        RelativeLayout champion_sk2_container = (RelativeLayout) rootview.findViewById(R.id.b14c);
        IDRessource = getResources().getIdentifier("b"+idForLine+"4c", "id", getActivity().getBaseContext().getPackageName());
        champion_sk2_container.setId(IDRessource);
        TimerButton champion_sk2_btn =  (TimerButton) rootview.findViewById(R.id.b14);
        IDRessource = getResources().getIdentifier("b"+idForLine+"4", "id", getActivity().getBaseContext().getPackageName());
        champion_sk2_btn.setId(IDRessource);
        TextView champion_sk2_txt = (TextView) rootview.findViewById(R.id.b14t);
        IDRessource = getResources().getIdentifier("b"+idForLine+"4t", "id", getActivity().getBaseContext().getPackageName());
        champion_sk2_txt.setId(IDRessource);

        //add the line to the rootview
        containerView.addView(rootview);
    }

    public void logTimerMap(){

        LogUtils.LOGV("DEBUGT","LOG TIMER MAP");
        for (String champ: timerMap.keySet()){
            String key =champ.toString();
            String value = timerMap.get(champ).toString();
            if(key.endsWith("2")){
                LogUtils.LOGV("DEBUGT",key + ":" + value);
            }
        }

        LogUtils.LOGV("DEBUGT","LOG TIMER MAP CDR");
        for (String champ: timerCdrMap.keySet()){
            String key =champ.toString();
            String value = timerCdrMap.get(champ).toString();
            LogUtils.LOGV("DEBUGT",key + ":" + value);
        }

        LogUtils.LOGV("DEBUGT","LOG ULTI LVL MAP");
        for (String champ: timerUltiLvlMap.keySet()){
            String key =champ.toString();
            String value = timerUltiLvlMap.get(champ).toString();
            LogUtils.LOGV("DEBUGT",key + ":" + value);
        }
    }
}
