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
import android.widget.Toast;

import org.ema.dialogs.CooldownTimersDialogFragment;
import org.ema.fragments.AlliesFragment;
import org.ema.fragments.EnnemiesFragment;
import org.ema.fragments.TimersFragment;
import org.ema.dialogs.ChampionTipDialogFragment;
import org.ema.dialogs.SecureDialogFragment;
import org.ema.utils.TimerButton;
import org.ema.utils.WebSocket;


public class CompanionActivity extends FragmentActivity implements ChampionTipDialogFragment.NoticeDialogListener, SecureDialogFragment.NoticeDialogListener, CooldownTimersDialogFragment.NoticeDialogListener {

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

    //On cr�er une deuxi�me fonction avec un param�tre en plus car on ne peut pas passer de param�tres depuis la vue
    public void timerListener(View tbt) {
        timerListener(tbt, false, 0);
    }


    //This function handle the onclick (short) events for all buttons on the timer view
    public void timerListener(View tbt, boolean fromWebSocket, long delayOfTransfert) {
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

    //Show the dialod to set the cooldown reduction for a champion
    public void showCooldownReducers(View v) {
        Bundle args = new Bundle();
        switch (v.getId()) {
            case R.id.b11:
                args.putString("name", ennemiesFragment.getSummonersOpponentsList().get(0).getChampion().getName());
                args.putString("ennemy", "b11");
                break;
            case R.id.b21:
                args.putString("name", ennemiesFragment.getSummonersOpponentsList().get(1).getChampion().getName());
                args.putString("ennemy", "b21");
                break;
            case R.id.b31:
                args.putString("name", ennemiesFragment.getSummonersOpponentsList().get(2).getChampion().getName());
                args.putString("ennemy", "b31");
                break;
            case R.id.b41:
                args.putString("name", ennemiesFragment.getSummonersOpponentsList().get(3).getChampion().getName());
                args.putString("ennemy", "b41");
                break;
            case R.id.b51:
                args.putString("name", ennemiesFragment.getSummonersOpponentsList().get(4).getChampion().getName());
                args.putString("ennemy", "b51");
                break;
        }
        timerFragment.showCooldownReducers(v, args);
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
    public void onDialogPositiveClick(DialogFragment dialog, int cooldown, String ennemy_button_id) {
        timerFragment.onDialogPositiveClick(dialog, cooldown, ennemy_button_id);
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

            disconnectThread.start();
            launchMainActivity();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void handleDisconnection() {
        this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
               if (!WebSocket.alreadyDisconnected) {
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
