package org.ema.lolcompanion;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.TimerTask;


public class TimerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);


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

        channelSummary.addView(riv, 25, 25);
    }

    //This function handle the onclick events for all buttons on the timer view
    protected void timerListener(View v){

        //Name of the clicked button => example : b21
        String IDButton = getResources().getResourceName(v.getId());

        /* TO-DO
         - find how to display timer on the imageview
         - find how to work 1 timer for 1 button
         - check if button is allready clicked / on timing
         - foreach btn, update timer each seconds by finding the appropriate view and changing the text
         -
        */

    }


}
