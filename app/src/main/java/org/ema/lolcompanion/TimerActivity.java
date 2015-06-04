package org.ema.lolcompanion;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.ema.utils.Timer;
import org.ema.utils.TimerButton;

import java.util.TimerTask;


public class TimerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");
        TextView timers = (TextView) findViewById(R.id.timers);
        timers.setTypeface(font);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //This functions adds dynamically a player icon in the channel summary so user can know who is connected
    protected void appendPlayerIconToChannelSummary(Bitmap playerIcon){
        LinearLayout channelSummary = (LinearLayout) findViewById(R.id.channel_summary);
        RoundedImageView riv = new RoundedImageView(this);
        riv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        riv.setCornerRadius((float) 25);
        riv.setBorderWidth((float) 1);
        riv.setBorderColor(Color.WHITE);
        riv.mutateBackground(true);
        riv.setImageBitmap(playerIcon);
        riv.setOval(true);
        riv.setTileModeX(Shader.TileMode.CLAMP);
        riv.setTileModeY(Shader.TileMode.CLAMP);

        //adding a icon to the channel summary
        channelSummary.addView(riv, 25, 25);
    }


    //This function handle the onclick (short) events for all buttons on the timer view
    public void timerListener(View tbt){
        TimerButton tbtn = (TimerButton) tbt;
        //Name of the clicked button => example : b21
        String IDButton = getResources().getResourceName(tbtn.getId());
        //loading the league of legend equiv fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");

        //Timer is null and has never been instancied
        if(tbtn.getTimer() == null){
            //Setting the TextView so the timer update the countdown in FO
            int timerTextViewID = getResources().getIdentifier(IDButton.concat("t"), "id", getBaseContext().getPackageName());
            //settings the textView with the font
            TextView txtv = (TextView) findViewById(timerTextViewID);
            txtv.setTypeface(font);
            tbtn.setTimer(new Timer(0, 0, txtv));
        }

        //Timer isn't ticking, we can lauch the countdown
        if(tbtn.getTimer() != null && !tbtn.getTimer().isTicking()){
            //To-DO : send the signal to websocket with timestamps and button id
            //TO-do : retrieve the good time in the LOL champion array
            long timeToCount = 120000; // a modifier par le bon temps
            tbtn.setTimer(new Timer(timeToCount,1000,tbtn.getTimer().getTimerTextView()));
            tbtn.getTimer().start();
            tbtn.getTimer().setVisible(true);
        }
    }
}
