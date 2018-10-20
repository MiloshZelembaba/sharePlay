package com.miloshzelembaba.play.Activity.StartActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by miloshzelembaba on 2018-10-06.
 */

public class LoadingFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        this.setStyle(STYLE_NO_TITLE, getTheme()); // You can use styles or inflate a view
        dialog.setMessage("Loading.."); // set your messages if not inflated from XML

        dialog.setCancelable(false);

        return dialog;
    }
}
