package com.zeezaglobal.posresturant.Dialogues;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class SuccessDialogFragment extends DialogFragment {

    private String successMessage;

    public static SuccessDialogFragment newInstance(String successMessage) {
        SuccessDialogFragment frag = new SuccessDialogFragment();
        Bundle args = new Bundle();
        args.putString("success_message", successMessage);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        if (getArguments() != null) {
            successMessage = getArguments().getString("success_message");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(successMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}