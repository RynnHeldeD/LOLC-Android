package org.ema.lolcompanion;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class EnnemiesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ennemies);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");
        TextView ennemies = (TextView) findViewById(R.id.ennemies);
        ennemies.setTypeface(font);
    }
}
