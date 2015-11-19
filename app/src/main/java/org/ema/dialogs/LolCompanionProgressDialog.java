/* Copyright  2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette oeuvre est protegee par le droit d auteur et strictement reservee a l usage prive du
 * client. Toute reproduction ou diffusion au profit de tiers, a titre
 * gratuit ou onereux, de
 * tout ou partie de cette oeuvre est strictement interdite et constitue une contrefacon prevue
 * par les articles L 335-2 et suivants du Code de la propriete
 * intellectuelle. Les ayants-droits se
 * reservent le droit de poursuivre toute atteinte a leurs droits de
 * propriete intellectuelle devant les
 * juridictions civiles ou penales.
 */

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
        dialog.getWindow().setBackgroundDrawable(context.getApplicationContext().getResources().getDrawable(R.color.transparent));
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
