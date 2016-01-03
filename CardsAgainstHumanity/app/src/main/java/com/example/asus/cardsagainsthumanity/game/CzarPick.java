package com.example.asus.cardsagainsthumanity.game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;
import com.example.asus.cardsagainsthumanity.game.utils.Game;
import com.example.asus.cardsagainsthumanity.router.Receiver;
import com.example.asus.cardsagainsthumanity.game.utils.ViewAnswerArrayAdapter;

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

        initializeGame(savedInstanceState);

        TextView questionTextView = (TextView) findViewById(R.id.blackcard_czar);
        questionTextView.setText(""+ Game.questionID);

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

    private void initializeGame(Bundle savedInstanceState) {
        int question, round;
        boolean isCzar;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                question = -1;
                round = -1;
                isCzar = false;
            } else {
                question = extras.getInt("Question");
                round = extras.getInt("RoundNumber");
                isCzar = extras.getBoolean("isCzar");
            }
        } else {
            question = (int) savedInstanceState.getSerializable("Question");
            round = (int) savedInstanceState.getSerializable("RoundNumber");
            isCzar = (boolean) savedInstanceState.getSerializable("isCzar");
        }
        Game.questionID = question;
        Game.roundNumber = round;
        Game.isCzar = isCzar;
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
    public String getActivityName()
    {
        return "CzarPick";
    }

    private void updateList()
    {
        final ListView listView = (ListView) findViewById(R.id.answerList);
        String[] answersArray = answers.toArray(new String[answers.size()]);

        final ViewAnswerArrayAdapter adapter = new ViewAnswerArrayAdapter(this, answersArray, answersArray);
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
