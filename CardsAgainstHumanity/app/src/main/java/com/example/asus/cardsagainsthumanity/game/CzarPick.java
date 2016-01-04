package com.example.asus.cardsagainsthumanity.game;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.cardsagainsthumanity.MainActivity;
import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;
import com.example.asus.cardsagainsthumanity.game.utils.Game;
import com.example.asus.cardsagainsthumanity.router.AllEncompasingP2PClient;
import com.example.asus.cardsagainsthumanity.router.MeshNetworkManager;
import com.example.asus.cardsagainsthumanity.router.Packet;
import com.example.asus.cardsagainsthumanity.router.Receiver;
import com.example.asus.cardsagainsthumanity.game.utils.ViewAnswerArrayAdapter;
import com.example.asus.cardsagainsthumanity.router.Sender;
import com.example.asus.cardsagainsthumanity.wifi.WifiDirectBroadcastReceiver;

import java.util.ArrayList;

public class CzarPick extends AppCompatActivity implements ManagerInterface
{
    private ArrayList<String> playerNames;
    private ArrayList<Integer> playerPoints;
    private ArrayList<String> answers1;
    private ArrayList<String> answers2;
    private ArrayList<String> answersMacAddress;
    private ViewAnswerArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_czar_pick);

        Receiver.setActivity(this);

        // initializeGame(savedInstanceState);

        Button button = (Button) findViewById(R.id.show_dialog_box);
        button.setText(Game.deviceName + " - " + Game.scoreTable.get(Game.deviceName) + " pts");

        TextView textView = (TextView) findViewById(R.id.textRound);
        textView.setText("Round "+Game.roundNumber);

        TextView questionTextView = (TextView) findViewById(R.id.blackcard_czar);
        String[] blackCardText = Game.getBlackCardText(Game.questionID);
        questionTextView.setText("[" + Game.numAnswers + "] " + blackCardText[0]);

        playerNames = new ArrayList<String>(Game.scoreTable.keySet());
        playerPoints = new ArrayList<Integer>(Game.scoreTable.values());

        answers1 = new ArrayList<>();
        answers2 = new ArrayList<>();
        answersMacAddress = new ArrayList<>();
        updateList();
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

    public void addPlayerResponse(String whiteCard, String senderMacAddress)
    {
        if(Game.numAnswers > 1) {
            String[] splitWhiteCard = whiteCard.split(",");
            answers1.add(splitWhiteCard[0]);
            answers2.add(splitWhiteCard[1]);
        } else
            answers1.add(whiteCard);
        answersMacAddress.add(senderMacAddress);
        updateList();
    }
    
    public void openScoreTable(View view) {
        ScoreTable scoreTable = ScoreTable.newInstance(playerNames, playerPoints);
        scoreTable.show(getFragmentManager(), "ScoreTableFragment");
    }

    public void vote(View view)
    {
        int winnerIndex = adapter.getClickedItem(); // FIXME: Get from list view
        if(winnerIndex != -1) {
            String winnerAnswer = answersMacAddress.get(winnerIndex);
            winnerAnswer += ";";
            winnerAnswer += answers1.get(winnerIndex);
            if(Game.numAnswers > 1) {
                winnerAnswer += ";";
                winnerAnswer += answers2.get(winnerIndex);
            }

            for (AllEncompasingP2PClient c : MeshNetworkManager.routingTable.values())
            {
                if (c.getMac().equals(MeshNetworkManager.getSelf().getMac()))
                {
                    continue;
                }
                Log.wtf("SENDING WHITE CARD TO: ", " " + c.getMac());
                Sender.queuePacket(new Packet(Packet.TYPE.WINNER, winnerAnswer.getBytes(), c.getMac(),
                        WifiDirectBroadcastReceiver.MAC));
            }

            Intent intent = new Intent(CzarPick.this, FinalRound.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("winnerMac", answersMacAddress.get(winnerIndex));
            if(Game.numAnswers==1)
                intent.putExtra("winnerCardsID", answers1.get(winnerIndex));
            else
                intent.putExtra("winnerCardsID", answers1.get(winnerIndex) + "," + answers2.get(winnerIndex));
            startActivity(intent);
            finish();
        }
    }

    @Override
    public String getActivityName()
    {
        return "CzarPick";
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

        adapter = new ViewAnswerArrayAdapter(this, answersArray1, answersArray2);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
            adapter.itemClicked(position);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            if(adapter.getClickedItem() == -1) {
                fab.setClickable(false);
            } else {
                fab.setClickable(true);
            }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // User used back and nothing happened
    }
}
