package org.ema.lolcompanion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.ema.model.business.Summoner;
import org.ema.utils.CallbackMatcher;
import org.ema.utils.Utils;
import org.ema.utils.Constant;
import org.ema.model.DAO.*;

import java.util.ArrayList;


public class MainActivity extends Activity {

    public Summoner user;
    public ArrayList<Summoner> summonerList;
    public final static String SUMMONER_NAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable async code on main
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        user = SummonerDAO.getSummoner("HolyPhoénix");

        if(user != null) {
            Log.v("DAO", user.toString());
            loadData();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        //Intent intent = new Intent(this, PendingRoomActivity.class);
        Intent intent = new Intent(this, TimerActivity.class);
        EditText summoner_name = (EditText) findViewById(R.id.summoner_name);
        String message = summoner_name.getText().toString();
        intent.putExtra(SUMMONER_NAME, message);
        startActivity(intent);
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
    public void loadData(){
        int id = user.getId();
        boolean isInGame = SummonerDAO.isInGame(id);
        Log.v("DAO", "Is in game: " + isInGame);

        if(isInGame) {
            summonerList = CurrentGameDAO.getSummunerListInGameFromCurrentUser(user);
            if(summonerList != null) {
                Log.v("DAO", "SummonerList: " + summonerList.toString());
            }
        }



    }
}
