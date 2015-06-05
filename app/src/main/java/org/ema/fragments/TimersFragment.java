package org.ema.fragments;

import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.ema.lolcompanion.CompanionActivity;
import org.ema.lolcompanion.R;
import org.ema.lolcompanion.TimerActivity;
import org.ema.model.business.Summoner;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LoLStatActivity;
import org.ema.utils.SecureDialogFragment;
import org.ema.utils.SettingsManager;
import org.ema.utils.SortSummonerId;
import org.ema.utils.WebSocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TimersFragment extends LoLStatActivity implements SecureDialogFragment.NoticeDialogListener {

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
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>)GlobalDataManager.get("summonersList");

        //WebSocket.connectWebSocket();
        return rootView;
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String passphrase) {
        // Websocket - secure channel
        CompanionActivity.settingsManager.set(this.getActivity(), "passphrase", passphrase);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }
}
