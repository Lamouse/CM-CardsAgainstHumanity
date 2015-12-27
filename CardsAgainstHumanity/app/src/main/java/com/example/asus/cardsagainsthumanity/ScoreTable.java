package com.example.asus.cardsagainsthumanity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.HashMap;

public class ScoreTable extends DialogFragment {

    public static ScoreTable newInstance(HashMap<String, Integer> hashMap)	{
        ScoreTable scoreTable = new ScoreTable();
        Bundle args = new Bundle();
        args.putSerializable("playerScores", hashMap);
        scoreTable.setArguments(args);

        return scoreTable;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Fire missles?")
                .setPositiveButton("fire", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}