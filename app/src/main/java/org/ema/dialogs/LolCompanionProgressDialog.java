package org.ema.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import org.ema.lolcompanion.R;

/**
 * Custom progress dialog for loader between activities
 */
public class LolCompanionProgressDialog extends ProgressDialog {

    private AnimationDrawable animation;

    public LolCompanionProgressDialog(Context context) {
        super(context);
    }

    public static ProgressDialog getCompanionProgressDialog(Context context) {
        LolCompanionProgressDialog dialog = new LolCompanionProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);

        //setting the animation list for the GIF animation
        ImageView poroImage = (ImageView) findViewById(R.id.animation);
        poroImage.setBackgroundResource(R.drawable.custom_progress_dialog_animation);
        animation = (AnimationDrawable) poroImage.getBackground();
    }

    @Override
    public void show() {
        super.show();
        animation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        animation.stop();
    }
}
