/* Copyright � 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette �uvre est prot�g�e par le droit d�auteur et strictement r�serv�e � l�usage priv� du
 * client. Toute reproduction ou diffusion au profit de tiers, � titre
 * gratuit ou on�reux, de
 * tout ou partie de cette �uvre est strictement interdite et constitue une contrefa�on pr�vue
 * par les articles L 335-2 et suivants du Code de la propri�t�
 * intellectuelle. Les ayants-droits se
 * r�servent le droit de poursuivre toute atteinte � leurs droits de
 * propri�t� intellectuelle devant les
 * juridictions civiles ou p�nales.
 */

package org.ema.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ema.fragments.TimersFragment;
import org.ema.lolcompanion.R;
import org.ema.model.business.Summoner;

public class SecureDialogFragment extends DialogFragment {
    private Summoner summoner;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String passphrase);

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

        // Get the layout inflater and set the view with custom layout
        final View dialogLayout = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null);

        //Building the Dialog
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/lol.ttf");
        TextView advice_title = (TextView) dialogLayout.findViewById(R.id.dialog_title);
        advice_title.setText(R.string.secure_timers);
        advice_title.setTypeface(font);

        LinearLayout layout = (LinearLayout) dialogLayout.findViewById(R.id.dialog_container);
        layout.setPadding(15,0,15,0);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(this.getActivity());
        tv.setText(R.string.secure_timers_text);
        tv.setPadding(5, 10, 5, 10);
        tv.setTextSize(getResources().getDimension(R.dimen.tips_champion_font));
        tv.setTextColor(getResources().getColor(R.color.black_font));
        layout.addView(tv, tvParams);

        EditText et = new EditText(this.getActivity());
        et.setId(R.id.secure_channel_edit);

        PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        if(!TimersFragment.settingsManager.get(this.getActivity(), "passphrase").equals("")) {
            et.setText(TimersFragment.settingsManager.get(this.getActivity(), "passphrase"));
        }
        tv.setPadding(5, 10, 5, 10);
        tv.setTextSize(getResources().getDimension(R.dimen.tips_champion_font));
        et.setTextColor(getResources().getColor(R.color.grey_font));
        layout.addView(et, tvParams);

        builder.setView(dialogLayout);

        builder.setNeutralButton(R.string.confirm_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Go to next champion tips
                String passphrase = ((EditText) dialogLayout.findViewById(R.id.secure_channel_edit)).getText().toString();
                mListener.onDialogPositiveClick(SecureDialogFragment.this, passphrase);
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


    public Summoner getSummoner() {
        return summoner;
    }

    public void setSummoner(Summoner summoner) {
        this.summoner = summoner;
    }

}
