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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    public final static String SUMMONER_REGION = "";

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
        TextView go = (TextView) findViewById(R.id.login_submit);
        go.setTypeface(font);

        Spinner spinner = (Spinner) findViewById(R.id.region_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lol_regions_array, R.layout.custom_spinner);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        if(MainActivity.settingsManager.get(this, "summonerRegion") != "" )
            spinner.setSelection(Integer.parseInt(MainActivity.settingsManager.get(this, "summonerRegion")));

    }

    public void lauchSummonerSearch(View view) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText("entering pending room...");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 40);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

        Intent intent = new Intent(this, PendingRoomActivity.class);
        EditText summoner_name = (EditText) findViewById(R.id.summoner_name);
        String message = summoner_name.getText().toString();
        Spinner spinner = (Spinner) findViewById(R.id.region_spinner);
        int position = spinner.getSelectedItemPosition();
        if (!message.matches("([a-zA-Z0-9 ]){3,20}")) {
            //TODO pop up erreur
            Log.v("DAO", "Erreur dans le pseudo: " + message);
        } else {
            intent.putExtra(SUMMONER_NAME, message);
            intent.putExtra(SUMMONER_REGION, message);
            MainActivity.settingsManager.set(this, "summonerName", message);
            MainActivity.settingsManager.set(this, "summonerRegion", String.valueOf(position));
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
