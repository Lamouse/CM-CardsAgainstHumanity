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
import com.example.asus.cardsagainsthumanity.router.Receiver;

import java.util.ArrayList;

public class CzarPick extends AppCompatActivity implements ManagerInterface
{
    private ArrayList<String> playerNames;
    private ArrayList<Integer> playerPoints;
    private ArrayList<String> answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_czar_pick);

        Receiver.setActivity(this);

        String question;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                question = null;
            } else {
                question = extras.getString("Question");
            }
        } else {
            question = (String) savedInstanceState.getSerializable("Question");
        }

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

        answers = new ArrayList<>();

        updateList();
    }

    public void addPlayerResponse(String whiteCard)
    {
        answers.add(whiteCard);
        updateList();
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

    private void updateList()
    {
        final ListView listView = (ListView) findViewById(R.id.answerList);
        String[] answersArray = answers.toArray(new String[answers.size()]);

        final AnswerArrayAdapter adapter = new AnswerArrayAdapter(this, answersArray);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                adapter.itemClicked(position);
            }
        });
    }
}
