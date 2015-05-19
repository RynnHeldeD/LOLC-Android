package org.ema.lolcompanion;

import android.content.res.Resources;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.ema.model.business.Summoner;
import org.ema.model.DAO.*;
import org.ema.utils.WebSocket;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public Summoner user;
    public ArrayList<Summoner> summonerList;
    WsEventHandling wsEventHandling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //on récupère l'activité et on la parse
        RelativeLayout layout = (RelativeLayout) RelativeLayout.inflate(this, R.layout.activity_main, null);

        Resources res;
        res = getResources();

        setContentView(layout);



        //Enable async code on main
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        user = SummonerDAO.getSummoner("matblob");

        if(user != null) {
            Log.v("DAO", user.toString());
            loadData();
        }

        WebSocket webSocket = new WebSocket();
        webSocket.connectWebSocket();
        wsEventHandling = new WsEventHandling(webSocket.mWebSocketClient);

        Button startGame = (Button) findViewById(R.id.start);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vue) {
                wsEventHandling.pickedChampion(1,100,"vayne.png","");
            }
        });


        Button goToChan = (Button) findViewById(R.id.button2);

        goToChan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vue) {
                wsEventHandling.switchChannel("chan");
            }
        });

        Button goToGland = (Button) findViewById(R.id.button3);

        goToGland.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vue) {
                wsEventHandling.switchChannel("gland");
            }
        });

        Button timerActiv = (Button) findViewById(R.id.button4);

        timerActiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vue) {
                wsEventHandling.timerActivation(10,"12:55");
            }
        });

        Button timerDelay = (Button) findViewById(R.id.button5);

        timerDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vue) {
                wsEventHandling.timerDelay(10);
            }
        });


        /*super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);*/
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
