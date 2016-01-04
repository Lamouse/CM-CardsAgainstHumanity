package com.example.asus.cardsagainsthumanity.game;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.cardsagainsthumanity.MainActivity;
import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;
import com.example.asus.cardsagainsthumanity.game.utils.Game;
import com.example.asus.cardsagainsthumanity.game.utils.ScoreTableArrayAdapter;
import com.example.asus.cardsagainsthumanity.router.Receiver;

import java.util.ArrayList;

public class FinalGame extends AppCompatActivity implements ManagerInterface {
    private ArrayList<String> playerNames;
    private ArrayList<Integer> playerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_game);

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
        String[] blackCardText = Game.getBlackCardText(Game.questionID);
        questionTextView.setText("[" + blackCardText[1] + "] " + blackCardText[0]);
        Game.numAnswers = Integer.parseInt(blackCardText[1]);

        TextView a1 = (TextView) findViewById(R.id.white1);
        TextView a2 = (TextView)findViewById(R.id.white2);
        TextView winner = (TextView) findViewById(R.id.winner_player);

        String whiteCardText = Game.getWhiteCardText(Integer.parseInt(separated[0]));
        a1.setText("[1] " + whiteCardText);
        if (separated.length == 2)
        {
            String whiteCard2Text = Game.getWhiteCardText(Integer.parseInt(separated[1]));
            a2.setText("[2] " + whiteCard2Text);
        }
        else
        {
            a2.setVisibility(View.GONE);
        }
        winner.setText(winnerMac);

        playerNames = new ArrayList<String>(Game.scoreTable.keySet());
        playerPoints = new ArrayList<Integer>(Game.scoreTable.values());

        ListView listView = (ListView) findViewById(R.id.scoreTableList);
        ScoreTableArrayAdapter adapter = new ScoreTableArrayAdapter(this, playerNames, playerPoints);
        listView.setAdapter(adapter);
    }

    @Override
    public String getActivityName() {
        return "FinalGame";
    }

    public void goToMainMenu(View view) {
        Game.RoomActivity.finish();
        Game.RoomActivity = null;

        Intent intent = new Intent(FinalGame.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // User used back but nothing happened
    }
}
