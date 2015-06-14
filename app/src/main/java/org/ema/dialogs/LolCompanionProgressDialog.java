/* Copyright © 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette œuvre est protégée par le droit d’auteur et strictement réservée à l’usage privé du
 * client. Toute reproduction ou diffusion au profit de tiers, à titre
 * gratuit ou onéreux, de
 * tout ou partie de cette œuvre est strictement interdite et constitue une contrefaçon prévue
 * par les articles L 335-2 et suivants du Code de la propriété
 * intellectuelle. Les ayants-droits se
 * réservent le droit de poursuivre toute atteinte à leurs droits de
 * propriété intellectuelle devant les
 * juridictions civiles ou pénales.
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
