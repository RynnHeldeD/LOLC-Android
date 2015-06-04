package org.ema.lolcompanion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import android.util.Log;
import org.ema.model.DAO.CurrentGameDAO;
import org.ema.model.DAO.SummonerDAO;
import org.ema.model.business.Summoner;
import org.ema.utils.Constant;
import org.ema.utils.Region;
import org.ema.utils.GlobalDataManager;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class PendingRoomActivity extends Activity {

    public ArrayList<Summoner> summonersList;
    public Thread waitingThread;
    //public Handler handlerWaitingThread;
    public int count = 0;
    public boolean shouldContinue = true;
    public Summoner user;
    public String summonerNameFromPreviousView;
    private int IDRessource;
    private boolean isAllowedToBack = true;
    public final Object signal = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the summoner_name from the intent
        Intent intent = getIntent();
        setContentView(R.layout.activity_pending_room);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");

        //setting summoner name
        summonerNameFromPreviousView = intent.getStringExtra(MainActivity.SUMMONER_NAME);
        TextView summonerName = (TextView) findViewById(R.id.pending_summoner_name);
        summonerName.setTypeface(font);
        summonerName.setText(summonerNameFromPreviousView);

        //settings the textViews with the font
        TextView pending = (TextView) findViewById(R.id.pending);
        pending.setTypeface(font);
        TextView pendingDescription = (TextView) findViewById(R.id.pending_description);
        pendingDescription.setTypeface(font);
        //to set the loading off : findViewById(R.id.loadin_panel).setVisibility(View.GONE);

        //Launch pending task
        Constant.setRegion(Region.EUW);
        user = SummonerDAO.getSummoner(summonerNameFromPreviousView);

        //Creating thread
        waitingThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if(!shouldContinue)
                    {
                        try {
                            Log.v("DAO", "Waiting");
                            synchronized(signal){ signal.wait();}
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                            Log.v("Erreur stats", e.getMessage());
                        }
                    }
                    else {
                        //Waiting 10 seconds before make a new request to the server
                        Log.v("DAO", "Loading data");
                        if (!loadData()) {
                            SystemClock.sleep(10000);
                        }
                    }
                }
                //Log.v("DAO", "On a recupere les , on affiche la vue timer");
                //launchTimerActivity();
            }
        });


    }
    public void stopThread() {
        Log.v("DAO", "Stopping thread");
        shouldContinue = false;
        //synchronized(signal){ signal.notify();}
    }
    public void resumeThread() {
        Log.v("DAO", "Resuming thread");
        shouldContinue = true;
        synchronized(signal){ signal.notify();}
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //Launch thread if the user login is good
        if (user != null) {
            GlobalDataManager.add("user", user);
            Log.v("DAO", user.toString());
            resumeThread();
            if (waitingThread.getState() == Thread.State.NEW) {
                waitingThread.start();
            }
        }
    }
    public void launchTimerActivity() {
        Intent intent = new Intent(this, TimerActivity.class);
        GlobalDataManager.add("summonersList", summonersList);

        //TODO : mettre le channel enregistre dans la case channel
        // MainActivity.settingsManager.set(this, "summonerName", message);
        startActivity(intent);
    }
    public void launchMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public boolean loadData() {

        count ++;
        if(count == 10)
        {
            Log.v("Error", "Interrupted");
            waitingThread.interrupt();
        }
        int id = user.getId();
        boolean isInGame = SummonerDAO.isInGame(id);
        Log.v("DAO", "Is in game: " + isInGame);

        if (isInGame) {
            //TODO : impossible d'acceder aux ressources via un thread, voir comment recuperer et setter la ressource
            //On change le texte "Waiting signal game" en "Loading your data"
            //int IDRessourceInitialText = getResources().getIdentifier("pending_initial_state", "id", getBaseContext().getPackageName());
            //int IDRessourceNewText = getResources().getIdentifier("pending_initial_state", "id", getBaseContext().getPackageName());
            //TextView pendingRoomText = (TextView) findViewById(IDRessourceInitialText);
            //pendingRoomText.setText("Loading game data...");
            //Fin TODO

            resumeThread();
            isAllowedToBack = false;
            summonersList = CurrentGameDAO.getSummunerListInGameFromCurrentUser(user);
            if (summonersList != null) {
                Log.v("DAO", "SummonerList: " + summonersList.toString());
                while(!this.areAllImagesLoaded(summonersList)){
                    SystemClock.sleep(500);
                }
                launchTimerActivity();
            }

            stopThread();
            isAllowedToBack = true;
            return true;
        } else {
            Log.v("Error", "User not in game");
            shouldContinue = true;
        }
        return false;
    }

    public ArrayList<Summoner> getSummonersList() {
        return summonersList;
    }

    public void setSummonersList(ArrayList<Summoner> summonersList) {
        this.summonersList = summonersList;
    }

    public boolean areAllImagesLoaded(ArrayList<Summoner> notReadySummoners){
        boolean areImagesLoaded = true;

        for (Iterator<Summoner> it = notReadySummoners.iterator() ; it.hasNext();) {
            Summoner s = it.next();
            if (!s.areImagesLoaded()) {
                areImagesLoaded = false;
                break;
            }
        }

        return areImagesLoaded;
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(isAllowedToBack) {
            Log.v("Back", "Going back");
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                shouldContinue = false;
                launchMainActivity();
                return true;
            }

            return super.onKeyDown(keyCode, event);
        }
        return true;
    }
}
