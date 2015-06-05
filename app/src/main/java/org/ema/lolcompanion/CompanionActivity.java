package org.ema.lolcompanion;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import org.ema.fragments.AlliesFragment;
import org.ema.fragments.EnnemiesFragment;
import org.ema.fragments.TimersFragment;
import org.ema.model.business.Summoner;
import org.ema.utils.ChampionTipDialogFragment;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LoLStatActivity;
import org.ema.utils.SecureDialogFragment;
import org.ema.utils.SettingsManager;
import org.ema.utils.SortSummonerId;
import org.ema.utils.TimerButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class CompanionActivity extends FragmentActivity implements ChampionTipDialogFragment.NoticeDialogListener, SecureDialogFragment.NoticeDialogListener {

    static final int NUMBER_OF_TABS = 3;
    private String[] tabs = { "Ennemies", "Timers", "Allies" };
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
        tab_strp=(PagerTabStrip)findViewById(R.id.companion_title_strip);
        tab_strp.setTextColor(Color.WHITE);

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
                default: return ennemiesFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position){
            return tabs[position];
        }
    }

    //On créer une deuxième fonction avec un paramètre en plus car on ne peut pas passer de paramètres depuis la vue
    public void timerListener(View tbt){
        timerListener(tbt, false, 0);
    }


    //This function handle the onclick (short) events for all buttons on the timer view
    public void timerListener(View tbt, boolean fromWebSocket, long delayOfTransfert){
        TimerButton tbtn = (TimerButton) tbt;
        //Name of the clicked button => example : b21
        String IDButton = getResources().getResourceName(tbtn.getId());
        //button ID formated like "b12"
        String buttonID = IDButton.substring(IDButton.lastIndexOf("/") + 1);

        java.util.Date date= new java.util.Date();
        long now = date.getTime();
        long btnTimestp = tbtn.getClickedTimestamp();
        tbtn.setClickedTimestamp(now);

        if(!tbtn.isTriggered()){
            tbtn.setTriggered(true);

            class PostponedClick implements Runnable {
                public TimerButton tbtn;
                public String buttonID;

                public PostponedClick(TimerButton tbtn, String buttonID){
                    this.tbtn = tbtn;
                    this.buttonID = buttonID;
                }

                public void run(){
                    if (this.tbtn.isTriggered()) {
                        Log.v("DAO", this.buttonID + " simple click postponed");
                        timerFragment.simpleClickTimer(buttonID, 0, false);
                        this.tbtn.setTriggered(false);
                    }
                }
            }
            tbtn.postDelayed(new PostponedClick(tbtn, buttonID), 200);
        } else {
            if((now <= btnTimestp + TimerButton.DELAY)){
                Log.v("DAO", buttonID + " double click");
            }
            tbtn.setTriggered(false);
        }
    }


    // DIALOG HANDLERS FOR ALL FRAGMENTS
    public void secureAppSharing(View v){
        timerFragment.secureAppSharing(v);
    }

    public void showChampionTips(View v) {
        switch (mPager.getCurrentItem()){
            case 0 : alliesFragment.showChampionTips(v);
                break;
            case 2 : ennemiesFragment.showChampionTips(v);
                break;
        }
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog, int idRessource) {
        switch (mPager.getCurrentItem()){
            case 0 : alliesFragment.onDialogNeutralClick(dialog, idRessource);
                break;
            case 2 : ennemiesFragment.onDialogNeutralClick(dialog, idRessource);
                break;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String passphrase) {
        timerFragment.onDialogPositiveClick(dialog,passphrase);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        switch (mPager.getCurrentItem()){
            case 0 : alliesFragment.onDialogNegativeClick(dialog);
                break;
            case 2 : ennemiesFragment.onDialogNegativeClick(dialog);
                break;
        }
    }

    //KeyDown event handler
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            launchMainActivity();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void launchMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        instance = timerFragment;
        instanceCompanion = this;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        instance = null;
        instanceCompanion = null;
    }
}
