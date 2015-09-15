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

package org.ema.lolcompanion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.ema.dialogs.ChampionTipDialogFragment;
import org.ema.dialogs.SecureDialogFragment;
import org.ema.fragments.AlliesFragment;
import org.ema.fragments.EnnemiesFragment;
import org.ema.fragments.TimersFragment;
import org.ema.utils.TimerButton;
import org.ema.utils.WebSocket;


public class CompanionActivity extends FragmentActivity implements ChampionTipDialogFragment.NoticeDialogListener, SecureDialogFragment.NoticeDialogListener {

    static final int NUMBER_OF_TABS = 3;
    private String[] tabs = {"Ennemies", "Timers", "Allies"};
    CompanionAdapter cpAdapter;
    ViewPager mPager;
    PagerTabStrip tab_strp;
    public static TimersFragment instance = null;
    public static CompanionActivity instanceCompanion = null;

    EnnemiesFragment ennemiesFragment = new EnnemiesFragment();
    AlliesFragment alliesFragment = new AlliesFragment();
    TimersFragment timerFragment = new TimersFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_companion);

        cpAdapter = new CompanionAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.companion_pager);
        mPager.setAdapter(cpAdapter);
        tab_strp = (PagerTabStrip) findViewById(R.id.companion_title_strip);
        tab_strp.setTextColor(Color.WHITE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        instance = timerFragment;
        instanceCompanion = this;

    }

    //Permet de Switcher entre les fragments
    public class CompanionAdapter extends FragmentPagerAdapter {
        public CompanionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ennemiesFragment;
                case 1:
                    return timerFragment;
                case 2:
                    return alliesFragment;
                default:
                    return ennemiesFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }

    //This function handle the onclick (short) events for all buttons on the timer view
    public void timerListener(View tbt) {
        TimerButton tbtn = (TimerButton) tbt;
        //Name of the clicked button => example : b21
        String IDButton = getResources().getResourceName(tbtn.getId());
        //button ID formated like "b12"
        String buttonID = IDButton.substring(IDButton.lastIndexOf("/") + 1);

        if(!tbtn.isTriggered()) {
            tbtn.setTriggered(true);
            timerFragment.simpleClickTimer(buttonID, 0, false, true);
        } else {
            timerFragment.simpleClickTimer(buttonID, 0, false, false);
        }

        // FUNCTION FOR DOUBLE CLICK (DISABLED)
        /*
        java.util.Date date= new java.util.Date();
        long now = date.getTime();
        long btnTimestp = tbtn.getClickedTimestamp();
        tbtn.setClickedTimestamp(now);

        if (!tbtn.isTriggered()) {
            tbtn.setTriggered(true);

            class PostponedClick implements Runnable {
                public TimerButton tbtn;
                public String buttonID;

                public PostponedClick(TimerButton tbtn, String buttonID) {
                    this.tbtn = tbtn;
                    this.buttonID = buttonID;
                }

                public void run() {
                    if (this.tbtn.isTriggered()) {
                        Log.v("DAO", this.buttonID + " simple click postponed");
                        timerFragment.simpleClickTimer(buttonID, 0, false, false);
                        this.tbtn.setTriggered(false);
                    }
                }
            }
            tbtn.postDelayed(new PostponedClick(tbtn, buttonID), 200);
        } else {
            if ((now <= btnTimestp + TimerButton.DELAY)) {
                //TODO : nettoyer le double clic
            }
            tbtn.setTriggered(false);
        }
        */
    }


    // DIALOG HANDLERS FOR ALL FRAGMENTS
    public void secureAppSharing(View v) {
        timerFragment.secureAppSharing(v);
    }

    public void showChampionTips(View v) {
        switch (mPager.getCurrentItem()) {
            case 0:
                ennemiesFragment.showChampionTips(v);
                break;
            case 2:
                alliesFragment.showChampionTips(v);
                break;
        }
    }

    //Handles the changing of lvl champs
    public void setChampionLVL(View v) {
        TextView champTxt;
        int champLvl;
        switch (v.getId()) {
            case R.id.b11:
                champTxt = (TextView) findViewById(R.id.b11t);
                champLvl = Integer.parseInt(champTxt.getText().toString()) + 5;
                if(champLvl > 16){
                    champLvl = 6;
                }
                timerFragment.timerUltiLvlMap.put("b12", champLvl);
                WsEventHandling.sendUltiLevel("b11", champLvl);
                champTxt.setText(String.valueOf(champLvl));
                break;
            case R.id.b21:
                champTxt = (TextView) findViewById(R.id.b21t);
                champLvl = Integer.parseInt(champTxt.getText().toString()) + 5;
                if(champLvl > 16){
                    champLvl = 6;
                }
                timerFragment.timerUltiLvlMap.put("b22", champLvl);
                WsEventHandling.sendUltiLevel("b21", champLvl);
                champTxt.setText(String.valueOf(champLvl));
                break;
            case R.id.b31:
                champTxt = (TextView) findViewById(R.id.b31t);
                champLvl = Integer.parseInt(champTxt.getText().toString()) + 5;
                if(champLvl > 16){
                    champLvl = 6;
                }
                timerFragment.timerUltiLvlMap.put("b32", champLvl);
                WsEventHandling.sendUltiLevel("b31", champLvl);
                champTxt.setText(String.valueOf(champLvl));
                break;
            case R.id.b41:
                champTxt = (TextView) findViewById(R.id.b41t);
                champLvl = Integer.parseInt(champTxt.getText().toString()) + 5;
                if(champLvl > 16){
                    champLvl = 6;
                }
                timerFragment.timerUltiLvlMap.put("b42", champLvl);
                WsEventHandling.sendUltiLevel("b41", champLvl);
                champTxt.setText(String.valueOf(champLvl));
                break;
            case R.id.b51:
                champTxt = (TextView) findViewById(R.id.b51t);
                champLvl = Integer.parseInt(champTxt.getText().toString()) + 5;
                if(champLvl > 16){
                    champLvl = 6;
                }
                timerFragment.timerUltiLvlMap.put("b52", champLvl);
                WsEventHandling.sendUltiLevel("b51", champLvl);
                champTxt.setText(String.valueOf(champLvl));
                break;
        }
    }

    //Handles the changing of lvl champs
    public void setChampionCDR(View v) {
        TextView champTxt;
        int champCDR;
        switch (v.getId()) {
            case R.id.b10:
                champTxt = (TextView) findViewById(R.id.b10t);
                champCDR = Integer.parseInt(champTxt.getText().subSequence(0, champTxt.length() - 1).toString()) + 5;
                if(champCDR > 40){
                    champCDR = 0;
                }
                timerFragment.timerCdrMap.put("b12", champCDR);
                WsEventHandling.sendCdr("b10", champCDR);
                champTxt.setText(String.valueOf(champCDR) + "%");
                break;
            case R.id.b20:
                champTxt = (TextView) findViewById(R.id.b20t);
                champCDR = Integer.parseInt(champTxt.getText().subSequence(0,champTxt.length()-1).toString()) + 5;
                if(champCDR > 40){
                    champCDR = 0;
                }
                timerFragment.timerCdrMap.put("b22", champCDR);
                WsEventHandling.sendCdr("b20", champCDR);
                champTxt.setText(String.valueOf(champCDR) +"%");
                break;
            case R.id.b30:
                champTxt = (TextView) findViewById(R.id.b30t);
                champCDR = Integer.parseInt(champTxt.getText().subSequence(0, champTxt.length()-1).toString()) + 5;
                if(champCDR > 40){
                    champCDR = 0;
                }
                timerFragment.timerCdrMap.put("b32", champCDR);
                WsEventHandling.sendCdr("b30", champCDR);
                champTxt.setText(String.valueOf(champCDR)+"%");
                break;
            case R.id.b40:
                champTxt = (TextView) findViewById(R.id.b40t);
                champCDR = Integer.parseInt(champTxt.getText().subSequence(0, champTxt.length()-1).toString()) + 5;
                if(champCDR > 40){
                    champCDR = 0;
                }
                timerFragment.timerCdrMap.put("b42", champCDR);
                WsEventHandling.sendCdr("b40", champCDR);
                champTxt.setText(String.valueOf(champCDR)+"%");
                break;
            case R.id.b50:
                champTxt = (TextView) findViewById(R.id.b50t);
                champCDR = Integer.parseInt(champTxt.getText().subSequence(0,champTxt.length()-1).toString()) + 5;
                if(champCDR > 40){
                    champCDR = 0;
                }
                timerFragment.timerCdrMap.put("b52", champCDR);
                WsEventHandling.sendCdr("b50", champCDR);
                champTxt.setText(String.valueOf(champCDR)+"%");
                break;
        }
    }

    //Go to AdvancedStat Activity
    public void showAdvancedStatistics(View v) {
        switch (mPager.getCurrentItem()) {
            case 0:
                ennemiesFragment.showAdvancedStatistics(v, true);
                break;
            case 2:
                alliesFragment.showAdvancedStatistics(v, false);
                break;
        }
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog, int idRessource) {

        switch (mPager.getCurrentItem()) {
            case 0:
                ennemiesFragment.onDialogNeutralClick(dialog, idRessource);
                break;
            case 2:
                alliesFragment.onDialogNeutralClick(dialog, idRessource);
                break;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String passphrase) {
        timerFragment.onDialogPositiveClick(dialog, passphrase);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        switch (mPager.getCurrentItem()) {
            case 0:
                ennemiesFragment.onDialogNegativeClick(dialog);
                break;
            case 2:
                alliesFragment.onDialogNegativeClick(dialog);
                break;
        }
    }

    //KeyDown event handler
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Thread disconnectThread = new Thread(new Runnable() {
                public void run() {
                    WsEventHandling.disconnect();
                }
            });

            //Used to not show the toast with "deconnected" and "reconnection" as long as we stay on the home page
            WebSocket.onCompanionActivity = false;

            disconnectThread.start();
            launchMainActivity();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //Called when the websocket is disconnected.
    //This function clean the channel summary and display a message to the user
    public void handleDisconnection() {
        this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
               if (!WebSocket.alreadyDisconnected && WebSocket.onCompanionActivity) {
                   Toast.makeText(CompanionActivity.this, "Disconnected from server", Toast.LENGTH_SHORT).show();
               }
               try{
                   CompanionActivity.instance.cleanChannelSummary();
               } catch (Exception e){
                   Log.v("Websocket", "Erreur dans handleDisconnection au moment du CleanChannel:" + e.getMessage());
               }

               //On dis au websocket que maintenant ça ne sert plus a rien d'afficher des messages d'erreur car l'utilisteur sais qu'il est déconnecté
               WebSocket.alreadyDisconnected = true;
           }
       }
        );
    }

    //Display a message to say that the user is reconnected
    public void reconnectionNotification() {
        this.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(CompanionActivity.this, "You've been reconnected", Toast.LENGTH_SHORT).show();
                   WebSocket.alreadyDisconnected = false;
               }
           }
        );
    }

    public void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
       // instance = timerFragment;
       // instanceCompanion = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        //instance = null;
       // instanceCompanion = null;
    }
}
