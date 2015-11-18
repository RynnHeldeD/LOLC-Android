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

package org.ema.lolcompanion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ema.model.DAO.CurrentGameDAO;
import org.ema.model.DAO.SummonerDAO;
import org.ema.model.business.Summoner;
import org.ema.utils.LogUtils;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.Utils;

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
    public int websocketSleepTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Clear cache
        Utils.cache.clear();
        Utils.nbRequests = 0;
        websocketSleepTime = getResources().getInteger(R.integer.pending_room_websocket_waiting_time_seconds);
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

        //set the poro animation
        //setting the animation list for the GIF animation
        ImageView poroImage = (ImageView) findViewById(R.id.animation);
        poroImage.setBackgroundResource(R.drawable.custom_progress_dialog_animation);
        AnimationDrawable animation = (AnimationDrawable) poroImage.getBackground();
        animation.start();

        user = (Summoner) intent.getExtras().get("USER");

        //Creating thread
        waitingThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (!shouldContinue) {
                        try {
                            LogUtils.LOGV("DAO", "Waiting");
                            synchronized (signal) {
                                signal.wait();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.LOGV("Erreur stats", e.getMessage());
                        }
                    } else {
                        //Waiting 10 seconds before make a new request to the server
                        LogUtils.LOGV("DAO", "Loading data");

                        if (!loadData()) {
                            SystemClock.sleep(websocketSleepTime * 1000); //sleep and wait for game detection
                        }
                    }
                }
            }
        });


    }

    public void stopThread() {
        LogUtils.LOGV("DAO", "Stopping thread");
        shouldContinue = false;
    }

    public void resumeThread() {
        LogUtils.LOGV("DAO", "Resuming thread");
        shouldContinue = true;
        synchronized (signal) {
            signal.notify();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //Clear cache
        Utils.cache.clear();
        //Launch thread if the user login is good
        if (user != null) {
            GlobalDataManager.add("user", user);
            LogUtils.LOGV("DAO", user.toString());
            resumeThread();
            if (waitingThread.getState() == Thread.State.NEW) {
                waitingThread.start();
            }
        }
    }

    public void launchTimerActivity() {
        Intent intent = new Intent(this, CompanionActivity.class);
        GlobalDataManager.add("summonersList", summonersList);
        startActivity(intent);
    }

    public void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean loadData() {

        count++;
        // divided by 10 ==> 600 (seconde given by xml) / 10 = 60 tours ;
        // 60 tours de boucle x 10000( 10 secondes) = 600 000 milliseconde;
        // 600 000 / 60 000 (1 minute)=  10min
        if (count > (getResources().getInteger(R.integer.pending_room_countdown_seconds) / 10)) {
            //LogUtils.LOGV("Error", "Interrupted");
            stopThread();
            launchMainActivity();
            count = 0; //count begins at -1 to make the good number of loops (10 loops so count from 0 to 10)
            return false;
        }
        int id = user.getId();
        boolean isInGame = SummonerDAO.isInGame(id);

        //affichage du statut
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView pending_status = (TextView) findViewById(R.id.pending_status);
                pending_status.setText(R.string.pending_waiting_signal);
            }
        });


        //LogUtils.LOGV("DAO", "Is in game: " + isInGame);

        if (isInGame) {

            resumeThread();
            isAllowedToBack = false;
            //affichage du statut
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView pending_status = (TextView) findViewById(R.id.pending_status);
                    pending_status.setText(R.string.pending_loading_game);
                }
            });


            summonersList = CurrentGameDAO.getSummunerListInGameFromCurrentUser(user);
            if (summonersList != null) {
                //LogUtils.LOGV("DAO", "SummonerList: " + summonersList.toString());
                while (!this.areAllImagesLoaded(summonersList)) {
                    SystemClock.sleep(500);
                }
                launchTimerActivity();
            } else {
                //LogUtils.LOGV("DAO", "FATAL : summonersList is NULL. Summoner :" + summonerNameFromPreviousView);
                shouldContinue = false;
                launchMainActivity();
            }

            stopThread();
            isAllowedToBack = true;
            return true;
        } else {
            //LogUtils.LOGV("Error", "User not in game");
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

    public boolean areAllImagesLoaded(ArrayList<Summoner> notReadySummoners) {
        boolean areImagesLoaded = true;

        for (Iterator<Summoner> it = notReadySummoners.iterator(); it.hasNext(); ) {
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
        if (isAllowedToBack) {
            //LogUtils.LOGV("Back", "Going back");
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
