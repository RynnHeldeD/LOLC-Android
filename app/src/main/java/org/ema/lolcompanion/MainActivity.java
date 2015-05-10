package org.ema.lolcompanion;

import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.ema.model.business.Summoner;
import org.ema.utils.CallbackMatcher;
import org.ema.utils.Utils;
import org.ema.utils.Constant;
import org.ema.model.DAO.*;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public Summoner user;
    public ArrayList<Summoner> summonerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable async code on main
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        user = SummonerDAO.getSummoner("Kxng");

        if(user != null) {
            Log.v("DAO", user.toString());
            loadData();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


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
