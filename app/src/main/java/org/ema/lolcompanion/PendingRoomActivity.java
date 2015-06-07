package org.ema.lolcompanion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import org.ema.model.DAO.CurrentGameDAO;
import org.ema.model.DAO.SummonerDAO;
import org.ema.model.business.Summoner;
import org.ema.utils.Constant;
import org.ema.utils.Region;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
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

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
            TextView text = (TextView) layout.findViewById(R.id.text);
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            switch(msg.arg1) {
                case 2:
                    text.setText("waiting for game signal...");
                    break;
                case 3:
                    toast.setDuration(Toast.LENGTH_LONG);
                    text.setText("loading game data...");
                    break;
                case 4:
                    text.setText("loading game icons...");
                    break;
            }
            toast.setGravity(Gravity.BOTTOM, 0, 40);
            toast.setView(layout);
            toast.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Clear cache
        Utils.cache.clear();
        Utils.nbRequests = 0;
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

        user = (Summoner)intent.getExtras().get("USER");;

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
                        Message msg = handler.obtainMessage();
                        
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
        //Clear cache
        Utils.cache.clear();
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
        Intent intent = new Intent(this, CompanionActivity.class);

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
        if(count == 60)
        {
            Log.v("Error", "Interrupted");
            stopThread();
            launchMainActivity();
            return false;
        }
        int id = user.getId();
        boolean isInGame = SummonerDAO.isInGame(id);
        Message msgInGame = handler.obtainMessage();
        msgInGame.arg1 = 2;
        handler.sendMessage(msgInGame);
        Log.v("DAO", "Is in game: " + isInGame);

        if (isInGame) {

            resumeThread();
            isAllowedToBack = false;
			Message msgLoadData = handler.obtainMessage();
            msgLoadData.arg1 = 3;
            handler.sendMessage(msgLoadData);

            summonersList = CurrentGameDAO.getSummunerListInGameFromCurrentUser(user);
            if (summonersList != null) {
                Log.v("DAO", "SummonerList: " + summonersList.toString());
                while(!this.areAllImagesLoaded(summonersList)){
                    SystemClock.sleep(500);
                }
                launchTimerActivity();
            }
			else {
                Log.v("DAO", "FATAL : summonersList is NULL. Summoner :" + summonerNameFromPreviousView);
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
