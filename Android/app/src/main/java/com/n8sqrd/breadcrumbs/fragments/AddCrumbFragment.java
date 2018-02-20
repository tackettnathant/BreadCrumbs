package com.n8sqrd.breadcrumbs.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.n8sqrd.breadcrumbs.R;

/**
 * Created by ntackett on 2/19/2018.
 */

public class AddCrumbFragment extends DialogFragment {
    AddCrumbListener listener;
    public String mLocationText;
    public String mPrompt;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.add_crumb_fragment,null);
        if (mPrompt!=null) {
            ((TextView) view.findViewById(R.id.crumb_prompt)).setText(mPrompt);
        } else {
            ((TextView) view.findViewById(R.id.crumb_prompt)).setText(getString(R.string.default_prompt));
        }
        if (mLocationText!=null) {
            ((EditText) view.findViewById(R.id.add_crumb_location)).setText(mLocationText);
        }
        builder.setView(view)
                .setPositiveButton(R.string.begin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogPositiveClick(AddCrumbFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogNegativeClick(AddCrumbFragment.this);
                    }
                })
        ;


        Dialog dialog = builder.create();
        //now, set to show the keyboard automatically
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (AddCrumbListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AddCrumbListener");
        }

    }

    public interface AddCrumbListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
}
