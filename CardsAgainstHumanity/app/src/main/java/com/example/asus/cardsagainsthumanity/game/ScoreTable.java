package com.example.asus.cardsagainsthumanity.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.asus.cardsagainsthumanity.R;
import com.example.asus.cardsagainsthumanity.game.utils.ScoreTableArrayAdapter;

import java.util.ArrayList;

public class ScoreTable extends DialogFragment {

    public static ScoreTable newInstance(ArrayList<String> playerNames, ArrayList<Integer> playerPoints)	{
        ScoreTable scoreTable = new ScoreTable();
        Bundle args = new Bundle();
        args.putSerializable("playerNames", playerNames);
        args.putSerializable("playerPoints", playerPoints);
        scoreTable.setArguments(args);

        return scoreTable;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_score_table, null);

        builder.setView(view);

        final ListView listView = (ListView) view.findViewById(R.id.scoreTableList);
        ArrayList<String> playerNames = (ArrayList<String>) getArguments().getSerializable("playerNames");
        ArrayList<Integer> playerPoints = (ArrayList<Integer>) getArguments().getSerializable("playerPoints");
        final ScoreTableArrayAdapter adapter = new ScoreTableArrayAdapter(getActivity(), playerNames, playerPoints);
        listView.setAdapter(adapter);

        final Dialog dialog = builder.create();
        Button btn = (Button) view.findViewById(R.id.okay);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.cancel();
                }
            }
        });

        // Create the AlertDialog object and return it
        return dialog;
    }
}