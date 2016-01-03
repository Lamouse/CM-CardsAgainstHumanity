package com.example.asus.cardsagainsthumanity.game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;
import com.example.asus.cardsagainsthumanity.game.utils.Game;
import com.example.asus.cardsagainsthumanity.game.utils.ViewAnswerArrayAdapter;
import com.example.asus.cardsagainsthumanity.router.Receiver;

import java.util.ArrayList;

public class PlayerWait extends AppCompatActivity implements ManagerInterface
{
    private ArrayList<String> playerNames;
    private ArrayList<Integer> playerPoints;
    private ArrayList<String> answers1;
    private ArrayList<String> answers2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_wait);

        Receiver.setActivity(this);

        // initializeGame(savedInstanceState);

        Button button = (Button) findViewById(R.id.show_dialog_box);
        button.setText(Game.deviceName + " - " + Game.scoreTable.get(Game.deviceName) + " pts");

        TextView textView = (TextView) findViewById(R.id.textRound);
        textView.setText("Round "+Game.roundNumber);

        TextView questionTextView = (TextView) findViewById(R.id.black_card);
        String[] blackCardText = Game.getBlackCardText(Game.questionID);
        questionTextView.setText("[" + blackCardText[1] + "] " + blackCardText[0]);
        //questionTextView.setText("[" + Game.numAnswers + "] " + blackCardText[0]); //does not work

        playerNames = new ArrayList<String>(Game.scoreTable.keySet());
        playerPoints = new ArrayList<Integer>(Game.scoreTable.values());

        answers1 = new ArrayList<>();
        answers2 = new ArrayList<>();

        Log.wtf("NUM", ": "+Game.numAnswers);
        Log.wtf("NUM", ": "+Game.responsesID.size());

        for (ArrayList<Integer> c : Game.responsesID)
        {
            answers1.add(Integer.toString(c.get(0)));
            if(Game.numAnswers > 1)
                answers2.add(Integer.toString(c.get(1)));
        }
        Game.responsesID.clear();

        updateList();
    }

    public void addPlayerResponse(String whiteCard)
    {
        if(Game.numAnswers > 1) {
            String[] splitWhiteCard = whiteCard.split(",");
            answers1.add(splitWhiteCard[0]);
            answers2.add(splitWhiteCard[1]);
        }
        else {
            answers1.add(whiteCard);
        }
        updateList();
    }

    @Override
    public String getActivityName()
    {
        return "PlayerWait";
    }

    public void openScoreTable(View view) {
        ScoreTable scoreTable = ScoreTable.newInstance(playerNames, playerPoints);
        scoreTable.show(getFragmentManager(), "ScoreTableFragment");
    }

    private void updateList()
    {
        final ListView listView = (ListView) findViewById(R.id.answerList);
        String[] answersArray1 = answers1.toArray(new String[answers1.size()]);
        String[] answersArray2;
        if(Game.numAnswers > 1)
            answersArray2 = answers2.toArray(new String[answers2.size()]);
        else
            answersArray2 = null;

        final ViewAnswerArrayAdapter adapter = new ViewAnswerArrayAdapter(this, answersArray1, answersArray2);
        listView.setAdapter(adapter);
    }

    private void initializeGame(Bundle savedInstanceState) {
        int question, round, numAnswers;
        boolean isCzar;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                question = -1;
                round = -1;
                isCzar = false;
                numAnswers = -1;
            } else {
                question = extras.getInt("Question");
                round = extras.getInt("RoundNumber");
                isCzar = extras.getBoolean("isCzar");
                numAnswers = extras.getInt("numAnswers");
            }
        } else {
            question = (int) savedInstanceState.getSerializable("Question");
            round = (int) savedInstanceState.getSerializable("RoundNumber");
            isCzar = (boolean) savedInstanceState.getSerializable("isCzar");
            numAnswers = (int) savedInstanceState.getSerializable("numAnswers");
        }
        Game.questionID = question;
        Game.roundNumber = round;
        Game.isCzar = isCzar;
        Game.numAnswers = numAnswers;
    }

    @Override
    public void onBackPressed() {
        // User used back and nothing happened
    }
}
