package org.ema.lolcompanion;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ema.model.business.Summoner;
import org.ema.utils.LoLStatActivity;
import org.ema.utils.VerticalProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class AlliesActivity extends LoLStatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allies);

        //setting the font for title
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");
        TextView allies = (TextView) findViewById(R.id.allies);
        allies.setTypeface(font);

        //keeping screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //TO-DO - Foreach ListSummoner - Do FillSummonerInfo
    }

}
