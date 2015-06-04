package org.ema.lolcompanion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.ema.model.DAO.CurrentGameDAO;
import org.ema.model.DAO.SummonerDAO;
import org.ema.model.business.Summoner;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.SortSummonerId;
import org.ema.utils.Timer;
import org.ema.utils.TimerButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;


public class TimerActivity extends Activity {

    public HashMap<String,Long> timerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerMap = new HashMap<String,Long>();
        setContentView(R.layout.activity_timer);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");
        TextView timers = (TextView) findViewById(R.id.timers);
        timers.setTypeface(font);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ArrayList<Summoner> summonersList = (ArrayList<Summoner>)GlobalDataManager.get("summonersList");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Summoner user = (Summoner)GlobalDataManager.get("user");
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

    //This functions adds dynamically a player icon in the channel summary so user can know who is connected
    protected void appendPlayerIconToChannelSummary(Bitmap playerIcon){
        LinearLayout channelSummary = (LinearLayout) findViewById(R.id.channel_summary);
        RoundedImageView riv = new RoundedImageView(this);
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


    //This function handle the onclick (short) events for all buttons on the timer view
    public void timerListener(View tbt){
        TimerButton tbtn = (TimerButton) tbt;
        //Name of the clicked button => example : b21
        String IDButton = getResources().getResourceName(tbtn.getId());
        //loading the league of legend equiv fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");

        //Timer is null and has never been instancied
        if(tbtn.getTimer() == null){
            //Setting the TextView so the timer update the countdown in FO
            int timerTextViewID = getResources().getIdentifier(IDButton.concat("t"), "id", getBaseContext().getPackageName());
            //settings the textView with the font
            TextView txtv = (TextView) findViewById(timerTextViewID);
            txtv.setTypeface(font);
            tbtn.setTimer(new Timer(0, 0, txtv));
        }

        //Timer isn't ticking, we can lauch the countdown
        if(tbtn.getTimer() != null && !tbtn.getTimer().isTicking()){
            //To-DO : send the signal to websocket with timestamps and button id
            //TO-do : retrieve the good time in the LOL champion array
            String toDelete = IDButton.substring(IDButton.lastIndexOf("/")+1);
            long timeToCount = timerMap.get(IDButton.substring(IDButton.lastIndexOf("/")+1)) * 1000;
            tbtn.setTimer(new Timer(timeToCount,1000,tbtn.getTimer().getTimerTextView()));
            tbtn.getTimer().start();
            tbtn.getTimer().setVisible(true);
        }
    }

    private void setTimerButtonsImage(ArrayList<Summoner> teamSummonersList){
        this.setChampionTimerButtonsImage(teamSummonersList);
        this.setUltimateTimerButtonsImage(teamSummonersList);
        this.setSpellsTimerButtonsImage(teamSummonersList);
    }

    private void setChampionTimerButtonsImage(ArrayList<Summoner> summonersList){
        List<String> ids = Arrays.asList("b11", "b21", "b31", "b41", "b51");
        RoundedImageView tb;
        int IDRessource;

        int i = 0;
        for (String s : ids){
            IDRessource = getResources().getIdentifier(s, "id", getBaseContext().getPackageName());
            tb = (RoundedImageView) findViewById(IDRessource);
            Bitmap bm = summonersList.get(i).getChampion().getIcon();
            tb.setImageBitmap(bm);
            i++;
        }
    }

    private void setUltimateTimerButtonsImage(ArrayList<Summoner> summonersList){
        List<String> ids = Arrays.asList("b12", "b22", "b32", "b42", "b52");
        TimerButton tb;
        int IDRessource;

        int i = 0;
        for (String s : ids){
            IDRessource = getResources().getIdentifier(s, "id", getBaseContext().getPackageName());
            tb = (TimerButton) findViewById(IDRessource);
            tb.setImageBitmap(summonersList.get(i).getChampion().getSpell().getIcon());
            i++;
        }
    }

    private void setSpellsTimerButtonsImage(ArrayList<Summoner> summonersList){
        List<String> ids = Arrays.asList("b13", "b23", "b33", "b43", "b53");
        TimerButton tb;
        int IDRessource;

        int i = 0;
        for (String s : ids){
            IDRessource = getResources().getIdentifier(s, "id", getBaseContext().getPackageName());
            tb = (TimerButton) findViewById(IDRessource);
            tb.setImageBitmap(summonersList.get(i).getSpells()[0].getIcon());
            i++;
        }

        ids = Arrays.asList("b14", "b24", "b34", "b44", "b54");
        i = 0;
        for (String s : ids){
            IDRessource = getResources().getIdentifier(s, "id", getBaseContext().getPackageName());
            tb = (TimerButton) findViewById(IDRessource);
            tb.setImageBitmap(summonersList.get(i).getSpells()[1].getIcon());
            i++;
        }
    }

    private void buildTimerTable(ArrayList<Summoner> teamSummonersList){
        List<String> summonerSpellButtons = Arrays.asList("b13", "b14", "b23","b24","b33","b34","b43","b44","b53","b54");
        List<String> ultimateButtons = Arrays.asList("b12", "b22","b32","b42","b52");

        timerMap.put("b01",(long)300);
        timerMap.put("b02",(long)500);

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
            timerMap.put(s,(long)cdSummonerSpell);
            summonerIndex++;
        }


        Log.v("DAO", "Timer des tableau chargement termine");
    }
}
