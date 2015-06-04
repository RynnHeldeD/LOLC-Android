package org.ema.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.ema.lolcompanion.R;
import org.ema.model.business.Summoner;

public class ChampionTipDialogFragment extends DialogFragment {
    private Summoner summoner;

    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it.
    * */
    public interface NoticeDialogListener {
        public void onDialogNeutralClick(DialogFragment dialog, int idRessource);

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
        View dialogLayout = getActivity().getLayoutInflater().inflate(R.layout.dialog_champion_tips, null);

        //View dialogLayout = new View(this.getActivity());
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/lol.ttf");
        TextView advice_title = (TextView) dialogLayout.findViewById(R.id.dialog_advice_title);
        advice_title.setText(((String) this.getArguments().get("name")));
        advice_title.setTypeface(font);

        LinearLayout layout = (LinearLayout) dialogLayout.findViewById(R.id.dialog_advices);
        String[] advices = ((String) this.getArguments().get("tips")).split("\",\"");
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for(String advice : advices) {
            TextView tv = new TextView(this.getActivity());
            tv.setText(advice.replace("\"", ""));
            tv.setPadding(10,5,10,5);
            tv.setTextSize(getResources().getDimension(R.dimen.tips_champion_font));
            tv.setTextColor(getResources().getColor(R.color.black_font));
            layout.addView(tv, tvParams);
        }

        builder.setView(dialogLayout);

        final int next = (int)this.getArguments().get("next");

        builder.setNeutralButton(R.string.nex_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Go to next champion tips
                mListener.onDialogNeutralClick(ChampionTipDialogFragment.this, next);
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
