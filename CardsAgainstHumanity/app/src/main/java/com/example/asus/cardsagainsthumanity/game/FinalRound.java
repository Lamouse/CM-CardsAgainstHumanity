package com.example.asus.cardsagainsthumanity.game;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.example.asus.cardsagainsthumanity.RoomActivity;
import com.example.asus.cardsagainsthumanity.game.utils.Game;
import com.example.asus.cardsagainsthumanity.router.AllEncompasingP2PClient;
import com.example.asus.cardsagainsthumanity.router.MeshNetworkManager;
import com.example.asus.cardsagainsthumanity.router.Packet;
import com.example.asus.cardsagainsthumanity.router.Receiver;
import com.example.asus.cardsagainsthumanity.router.Sender;
import com.example.asus.cardsagainsthumanity.wifi.WifiDirectBroadcastReceiver;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(!Game.isCzar) {
            fab.setVisibility(View.GONE);
        }

        Button button = (Button) findViewById(R.id.show_dialog_box);
        button.setText(Game.deviceName + " - " + Game.scoreTable.get(Game.deviceName) + " pts");

        TextView textView = (TextView) findViewById(R.id.textRound);
        textView.setText("Round "+Game.roundNumber);

        TextView questionTextView = (TextView) findViewById(R.id.black_card);
        String[] blackCardText = Game.getBlackCardText(Game.questionID);
        questionTextView.setText("[" + blackCardText[1] + "] " + blackCardText[0]);
        Game.numAnswers = Integer.parseInt(blackCardText[1]);

        playerNames = new ArrayList<String>(Game.scoreTable.keySet());
        playerPoints = new ArrayList<Integer>(Game.scoreTable.values());

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

        Game.roundNumber += 1;
    }

    public void goToNextRound(View view)
    {
        if (Game.isCzar)
        {
            int randomNum;
            boolean czarFound = false;
            Random rand = new Random();
            List<AllEncompasingP2PClient> peers = Collections.list(MeshNetworkManager.routingTable.elements());
            AllEncompasingP2PClient choosenCzar = null;
            while (!czarFound)
            {
                randomNum = rand.nextInt(MeshNetworkManager.routingTable.size());
                if (!peers.get(randomNum).getMac().equals(MeshNetworkManager.getSelf().getMac()))
                {
                    czarFound = true;
                    choosenCzar = peers.get(randomNum);
                }
            }

            String czarMessage = choosenCzar.getMac();
            int questionId = Game.getBlackCardId();
            czarMessage += ",";
            czarMessage += questionId;
            for (AllEncompasingP2PClient c : MeshNetworkManager.routingTable.values())
            {
                if (c.getMac().equals(MeshNetworkManager.getSelf().getMac()))
                    continue;
                Sender.queuePacket(new Packet(Packet.TYPE.CZAR, czarMessage.getBytes(), c.getMac(),
                        WifiDirectBroadcastReceiver.MAC));
            }
            Game.questionID = questionId;
            Game.isCzar = false;
            String[] questionText = Game.getBlackCardText(questionId);  //[0] has text, [1] has number of answers
            Game.numAnswers = Integer.parseInt(questionText[1]);

            Intent intent = new Intent(FinalRound.this, PlayerPick.class);
            intent.putExtra("Question", Game.questionID);
            intent.putExtra("RoundNumber", Game.roundNumber);
            intent.putExtra("isCzar", Game.isCzar);
            startActivity(intent);
        }
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
        // User used back but nothing happened
    }
}
