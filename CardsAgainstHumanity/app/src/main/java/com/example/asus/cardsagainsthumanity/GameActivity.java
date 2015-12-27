package com.example.asus.cardsagainsthumanity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

public class GameActivity extends AppCompatActivity {
    private HashMap<String, Integer> playerScores = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        playerScores.put("Player1", 3);
        playerScores.put("Player2", 2);
        playerScores.put("Player3", 1);

        final ListView listView = (ListView) findViewById(R.id.answerList);
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };

        final AnswerArrayAdapter adapter = new AnswerArrayAdapter(this, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.itemClicked(position);
            }
        });

        TextView textView = (TextView) findViewById(R.id.show_dialog_box);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScoreTable scoreTable = ScoreTable.newInstance(playerScores);
                scoreTable.show(getFragmentManager(), "NoticeDialogFragment");
            }
        });
    }
}
