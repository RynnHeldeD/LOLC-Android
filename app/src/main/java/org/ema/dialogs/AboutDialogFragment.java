package org.ema.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.ema.lolcompanion.R;

public class AboutDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater and set the view with custom layout
        View dialogLayout = getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);

        //View dialogLayout = new View(this.getActivity());
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/lol.ttf");
        TextView advice_title = (TextView) dialogLayout.findViewById(R.id.dialog_title);
        advice_title.setTypeface(font);

        builder.setView(dialogLayout);

        builder.setNegativeButton(R.string.close_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //close dialog
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
