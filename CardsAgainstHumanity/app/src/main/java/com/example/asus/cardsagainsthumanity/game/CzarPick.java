package com.example.asus.cardsagainsthumanity.game;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;
import com.example.asus.cardsagainsthumanity.game.utils.AnswerArrayAdapter;

import java.util.ArrayList;

public class CzarPick extends AppCompatActivity implements ManagerInterface
{
    private ArrayList<String> playerNames;
    private ArrayList<Integer> playerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_czar_pick);

        Intent intent = getIntent();
        String question = intent.getStringExtra("Question");

        TextView questionTextView = (TextView) findViewById(R.id.blackcard_czar);
        questionTextView.setText(question);

        playerNames = new ArrayList<String>();
        playerNames.add("Player1");
        playerNames.add("Player2");
        playerNames.add("Player4");
        playerNames.add("Player3");
        playerNames.add("Player5");

        playerPoints = new ArrayList<Integer>();
        playerPoints.add(3);
        playerPoints.add(2);
        playerPoints.add(1);
        playerPoints.add(0);
        playerPoints.add(0);

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
    }

    public void openScoreTable(View view) {
        ScoreTable scoreTable = ScoreTable.newInstance(playerNames, playerPoints);
        scoreTable.show(getFragmentManager(), "ScoreTableFragment");
    }

    @Override
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled)
    {
    }

    @Override
    public String getActivityName()
    {
        return "CzarPick";
    }

    @Override
    public void connect(WifiP2pConfig config)
    {
    }
}
