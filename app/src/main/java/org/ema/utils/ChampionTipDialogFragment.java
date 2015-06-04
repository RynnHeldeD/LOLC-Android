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


        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_champion_tips, null);
        //View dialogLayout = new View(this.getActivity());
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/lol.ttf");

        /*TextView advice_title = (TextView) dialogLayout.findViewById(R.id.dialog_advice_title);
        advice_title.setText(((String) this.getArguments().get("name")));
        advice_title.setTypeface(font);*/
        /*
        //Parent layout for title
        LinearLayout layoutParent = new LinearLayout(this.getActivity());
        LinearLayout.LayoutParams paramsParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParent.setOrientation(LinearLayout.VERTICAL);
        layoutParent.setBackgroundResource(R.drawable.gold_border_background);
        layoutParent.setLayoutParams(paramsParent);

        TextView title = new TextView(this.getActivity());
        title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        title.setText((String) this.getArguments().get("name"));
        title.setPadding(R.dimen.activity_vertical_margin, R.dimen.activity_horizontal_margin, R.dimen.activity_vertical_margin, R.dimen.activity_horizontal_margin);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(R.dimen.tips_champion_font_title);
        title.setTextColor(getResources().getColor(R.color.black_font));
        title.setTypeface(font);
        layoutParent.addView(title);

        //Scrollview parent
        ScrollView scrollView = new ScrollView(this.getActivity());
        ScrollView.LayoutParams paramsScrollView = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(paramsScrollView);

        //Linear layout of the advices
        LinearLayout layout = new LinearLayout(this.getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);
        */
        LinearLayout layout = (LinearLayout) dialogLayout.findViewById(R.id.dialog_advices);
        String[] advices = ((String) this.getArguments().get("tips")).replace("\"", "").split(",");
        for(String advice : advices) {
            TextView tv = new TextView(this.getActivity());
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            tv.setText(advice);
            tv.setPadding(R.dimen.activity_vertical_margin, R.dimen.activity_horizontal_margin, R.dimen.activity_vertical_margin, R.dimen.activity_horizontal_margin);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(R.dimen.tips_champion_font);
            tv.setTextColor(getResources().getColor(R.color.black_font));
            layout.addView(tv);
            Log.v("MIC", tv.toString());
        }

        //scrollView.addView(layout);
        //layoutParent.addView(scrollView);

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
