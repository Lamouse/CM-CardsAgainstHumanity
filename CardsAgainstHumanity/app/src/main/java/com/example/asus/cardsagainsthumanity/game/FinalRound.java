package com.example.asus.cardsagainsthumanity.game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;

import java.util.ArrayList;

import com.example.asus.cardsagainsthumanity.game.utils.Game;
import com.example.asus.cardsagainsthumanity.router.MeshNetworkManager;
import com.example.asus.cardsagainsthumanity.router.Receiver;

public class FinalRound extends AppCompatActivity implements ManagerInterface
{
    private ArrayList<String> playerNames;
    private ArrayList<Integer> playerPoints;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_round);

        Receiver.setActivity(this);

        Intent intent = getIntent();
        String winnerCardsID = intent.getStringExtra("winnerCardsID");
        String winnerMac = intent.getStringExtra("winnerMac");
        String[] separated = winnerCardsID.split(",");

        Button button = (Button) findViewById(R.id.show_dialog_box);
        button.setText(Game.deviceName + " - " + Game.scoreTable.get(Game.deviceName) + " pts");

        TextView textView = (TextView) findViewById(R.id.textRound);
        textView.setText("Round "+Game.roundNumber);

        TextView questionTextView = (TextView) findViewById(R.id.black_card);
        questionTextView.setText("" + Game.questionID);

        playerNames = new ArrayList<String>(Game.scoreTable.keySet());
        playerPoints = new ArrayList<Integer>(Game.scoreTable.values());

        TextView a1 = (TextView) findViewById(R.id.white1);
        TextView a2 = (TextView)findViewById(R.id.white2);
        TextView winner = (TextView) findViewById(R.id.winner_player);

        a1.setText(separated[0]); //FIXME: Ir à BD
        if (separated.length == 2)
        {
            a2.setText(separated[1]); //FIXME: Ir à BD
        }
        else
        {
            a2.setVisibility(View.GONE);
        }
        winner.setText(winnerMac);
    }

    @Override
    public String getActivityName()
    {
        return "FinalRound";
    }

    public void openScoreTable(View view) {
        ScoreTable scoreTable = ScoreTable.newInstance(playerNames, playerPoints);
        scoreTable.show(getFragmentManager(), "ScoreTableFragment");
    }

    @Override
    public void onBackPressed() {
        // User used back and nothing happened
    }
}
