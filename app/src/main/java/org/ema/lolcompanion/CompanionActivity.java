package org.ema.lolcompanion;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
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
import org.ema.utils.LoLStatActivity;
import org.ema.utils.SecureDialogFragment;
import org.ema.utils.SettingsManager;

import java.util.ArrayList;
import java.util.HashMap;


public class CompanionActivity extends FragmentActivity implements ChampionTipDialogFragment.NoticeDialogListener, SecureDialogFragment.NoticeDialogListener {

    static final int NUMBER_OF_TABS = 3;
    private String[] tabs = { "Ennemies", "Timers", "Allies" };
    CompanionAdapter cpAdapter;
    ViewPager mPager;
    PagerTabStrip tab_strp;
    public static SettingsManager settingsManager = null;
    public static TimerActivity instance = null;

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
                    //return new TimersFragment();
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

    public void showChampionTips(View v) {
        ennemiesFragment.showChampionTips(v);
    }


    @Override
    public void onDialogNeutralClick(DialogFragment dialog, int idRessource) {
        ennemiesFragment.onDialogNeutralClick(dialog,idRessource);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }


    @Override
    public void onDialogPositiveClick(android.app.DialogFragment dialog, String passphrase) {

    }

    @Override
    public void onDialogNegativeClick(android.app.DialogFragment dialog) {

    }
}
