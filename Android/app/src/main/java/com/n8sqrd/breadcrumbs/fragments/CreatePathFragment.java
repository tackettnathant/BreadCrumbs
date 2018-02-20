package com.n8sqrd.breadcrumbs.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.EditText;

import com.n8sqrd.breadcrumbs.R;

/**
 * Created by ntackett on 1/23/2018.
 */

public class CreatePathFragment extends DialogFragment {
    CreatePathListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.create_path,null))
        .setPositiveButton(R.string.begin, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogPositiveClick(CreatePathFragment.this);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogNegativeClick(CreatePathFragment.this);
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
            listener = (CreatePathListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement CreatePathListener");
        }
    }

    public interface CreatePathListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
}
