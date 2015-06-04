package org.ema.lolcompanion;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.*;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.ema.model.business.Summoner;
import org.ema.utils.CallbackMatcher;
import org.ema.utils.SettingsManager;
import org.ema.utils.Utils;
import org.ema.utils.Constant;
import org.ema.model.DAO.*;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity {
    public final static String SUMMONER_NAME = "";

    // Preferences
    public static SettingsManager settingsManager = null;
    public boolean isFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable async code on main
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialize PreferencesManager
        MainActivity.settingsManager = new SettingsManager();
        PreferenceManager.getDefaultSharedPreferences(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting summoner name if saved in preferences
        EditText summoner_name = (EditText) findViewById(R.id.summoner_name);
        summoner_name.setText(MainActivity.settingsManager.get(this, "summonerName"));

        //loading the league of legend equiv fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");
        //settings the textViews with the font
        TextView login = (TextView) findViewById(R.id.login);
        login.setTypeface(font);
        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setTypeface(font);
        /*TextView cooldown_tip = (TextView) findViewById(R.id.cooldown_tip);
        cooldown_tip.setTypeface(font);
        TextView analysis_tip = (TextView) findViewById(R.id.analysis_tip);
        analysis_tip.setTypeface(font);
        TextView synchro_tip = (TextView) findViewById(R.id.synchro_tip);
        synchro_tip.setTypeface(font);
        TextView security_tip = (TextView) findViewById(R.id.security_tip);
        security_tip.setTypeface(font);
        TextView login_submit = (TextView) findViewById(R.id.login_submit);
        login_submit.setTypeface(font);
        TextView summoner_name = (TextView) findViewById(R.id.summoner_name);
        summoner_name.setTypeface(font);*/

    }


    public void lauchSummonerSearch(View view) {
        Intent intent = new Intent(this, PendingRoomActivity.class);
        EditText summoner_name = (EditText) findViewById(R.id.summoner_name);
        String message = summoner_name.getText().toString();

        if (!message.matches("([a-zA-Z0-9]){3,20}")){
            //TODO pop up erreur
            Log.v("DAO", "Erreur dans le pseudo: " + message);
        } else {
            intent.putExtra(SUMMONER_NAME, message);
            MainActivity.settingsManager.set(this, "summonerName", message);
            startActivity(intent);
        }
    }

    public boolean checkNickname(String nickName) {



        Summoner user = SummonerDAO.getSummoner(nickName);

        if(user == null){
            //TODO message d'erreur
        }

        return false;
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    */

}
