package com.example.asus.cardsagainsthumanity.game;

import android.net.wifi.p2p.WifiP2pConfig;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;
import com.example.asus.cardsagainsthumanity.game.utils.AnswerArrayAdapter;
import com.example.asus.cardsagainsthumanity.game.utils.ViewAnswerArrayAdapter;

import java.util.ArrayList;

public class PlayerWait extends AppCompatActivity implements ManagerInterface
{
    private ArrayList<String> playerNames;
    private ArrayList<Integer> playerPoints;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_wait);
    }

    @Override
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled)
    {

    }

    @Override
    public String getActivityName()
    {
        return "PlayerWait";
    }

    @Override
    public void connect(WifiP2pConfig config)
    {


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

        final ViewAnswerArrayAdapter adapter = new ViewAnswerArrayAdapter(this, values, null);
        listView.setAdapter(adapter);
    }

    public void openScoreTable(View view) {
        ScoreTable scoreTable = ScoreTable.newInstance(playerNames, playerPoints);
        scoreTable.show(getFragmentManager(), "ScoreTableFragment");
    }
}
