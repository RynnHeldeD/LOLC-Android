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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.ema.lolcompanion.R;

public class CooldownTimersDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int cooldown, String ennemy);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String ennemy_button_id = (String) this.getArguments().get("ennemy");

        //CDR argument recuperation
        int cdr  = (Integer) this.getArguments().get("cdr");
        String textCdr = String.valueOf(cdr);
        //An integer between 0 to 8 to feed the seekbar
        int numberOfBarIncrement;
        if (cdr == 0){
            numberOfBarIncrement = 0;
        } else {
            //if the CDR is divisable by 5
            if(cdr % 5 == 0){
                numberOfBarIncrement = cdr/5;
            } else {
                //Else we substract the modulo to get a number divisable by 5
                numberOfBarIncrement = cdr - (cdr % 5) == 0 ? 0 : cdr - (cdr % 5) / 5 ;
            }
        }

        // Get the layout inflater and set the view with custom layout
        View dialogLayout = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null);

        //View dialogLayout = new View(this.getActivity());
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/lol.ttf");
        TextView advice_title = (TextView) dialogLayout.findViewById(R.id.dialog_title);
        advice_title.setText(((String) this.getArguments().get("name")));
        advice_title.setTypeface(font);

        LinearLayout layout = (LinearLayout) dialogLayout.findViewById(R.id.dialog_container);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //Description of the dialog
        TextView tv = new TextView(this.getActivity());
        tv.setText("Set the cooldown reduction of this champion");
        tv.setPadding(5, 10, 5, 10);
        tv.setTextSize(getResources().getDimension(R.dimen.font_cooldown_text));
        tv.setTextColor(getResources().getColor(R.color.black_font));
        layout.addView(tv, tvParams);

        //Seekbar
        final SeekBar sk = new SeekBar(this.getActivity());
        //Seekbar cannot increment by 5 to 5, Solution : for example max is 40 and step is 5 ==> We set the max at 8 and step 1 and then multiply by 5 the % given by the seekbar
        int seekBarMaximum = getResources().getInteger(R.integer.max_cooldown_reduction) / getResources().getInteger(R.integer.cooldown_reduction_steps);
        sk.setMax(seekBarMaximum);
        sk.setPadding(50, 5, 50, 5);
        sk.setProgress(0);
        sk.incrementProgressBy(numberOfBarIncrement);
        layout.addView(sk, tvParams);

        //Seekbar result
        final TextView seekBarValue = new TextView(this.getActivity());
        seekBarValue.setPadding(5, 50, 5, 0);
        seekBarValue.setText(textCdr + getResources().getString(R.string.purcent));
        seekBarValue.setTextSize(getResources().getDimension(R.dimen.font_cooldown_seekbar_value));
        seekBarValue.setTextColor(getResources().getColor(R.color.black_font));
        seekBarValue.setGravity(Gravity.CENTER_HORIZONTAL);
        seekBarValue.setTypeface(font);
        layout.addView(seekBarValue, tvParams);

        //Seekbar listener to handle result display
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                //multiply the progress by the step to get the right value in the display
                int step = getResources().getInteger(R.integer.cooldown_reduction_steps);
                seekBarValue.setText(String.valueOf(progress * step) + getResources().getString(R.string.purcent));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        builder.setView(dialogLayout);

        builder.setPositiveButton(R.string.confirm_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int step = getResources().getInteger(R.integer.cooldown_reduction_steps);
                mListener.onDialogPositiveClick(CooldownTimersDialogFragment.this, sk.getProgress() *step , ennemy_button_id);
            }
        });

        builder.setNegativeButton(R.string.close_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //close dialog
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}
