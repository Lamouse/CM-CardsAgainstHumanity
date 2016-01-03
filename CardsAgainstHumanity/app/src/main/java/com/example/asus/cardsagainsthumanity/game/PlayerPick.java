package com.example.asus.cardsagainsthumanity.game;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;
import com.example.asus.cardsagainsthumanity.game.utils.AnswerArrayAdapter;
import com.example.asus.cardsagainsthumanity.game.utils.Game;
import com.example.asus.cardsagainsthumanity.router.AllEncompasingP2PClient;
import com.example.asus.cardsagainsthumanity.router.MeshNetworkManager;
import com.example.asus.cardsagainsthumanity.router.Packet;
import com.example.asus.cardsagainsthumanity.router.Receiver;
import com.example.asus.cardsagainsthumanity.router.Sender;
import com.example.asus.cardsagainsthumanity.wifi.WifiDirectBroadcastReceiver;

import java.util.ArrayList;

public class PlayerPick extends AppCompatActivity implements ManagerInterface
{
    private ArrayList<String> playerNames;
    private ArrayList<Integer> playerPoints;
    private AnswerArrayAdapter adapter;
    private int maxCards = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_pick);

        Receiver.setActivity(this);

        initializeGame(savedInstanceState);

        TextView questionTextView = (TextView) findViewById(R.id.black_card);
        String[] blackCardText = Game.getBlackCardText(Game.questionID);
        questionTextView.setText("[" + blackCardText[1] + "] " + blackCardText[0]);
        //questionTextView.setText("[" + Game.numAnswers + "] " + blackCardText[0]); //does not work

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
        int[] ids = Game.getWhiteCardId(maxCards);
        String[] values = new String[maxCards];
        for(int i=0; i<maxCards; i++){
            values[i] = Game.getWhiteCardText(ids[i]);
        }
        /*String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };*/

        adapter = new AnswerArrayAdapter(this, values, Integer.parseInt(blackCardText[1]));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.itemClicked(position);

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                String[] blackCardText = Game.getBlackCardText(Game.questionID);
                if(adapter.getClickedItensSize() == Integer.parseInt(blackCardText[1])) {
                    fab.setClickable(true);
                } else {
                    fab.setClickable(false);
                }
            }
        });
    }

    public void openScoreTable(View view) {
        ScoreTable scoreTable = ScoreTable.newInstance(playerNames, playerPoints);
        scoreTable.show(getFragmentManager(), "ScoreTableFragment");
    }

    @Override
    public String getActivityName()
    {
        return "PlayerPick";
    }

    public void sendAnswer(View view) {
        ArrayList<String> arrayList = adapter.getClickedItens();

        String whiteCardMessage = "0";
        if(arrayList.size() > 1) {
            whiteCardMessage += ",";
            whiteCardMessage += "1";
        }

        Log.wtf("Sending white card: ", " " + whiteCardMessage);
        Log.wtf("Number of Connections: ", " " + Integer.toString(MeshNetworkManager.routingTable.size()));

        for (AllEncompasingP2PClient c : MeshNetworkManager.routingTable.values())
        {
            if (c.getMac().equals(MeshNetworkManager.getSelf().getMac()))
            {
                Game.responsesID.add(1);
                continue;
            }
            Log.wtf("SENDING WHITE CARD TO: ", " " + c.getMac());
            Sender.queuePacket(new Packet(Packet.TYPE.WHITECARD, whiteCardMessage.getBytes(), c.getMac(),
                    WifiDirectBroadcastReceiver.MAC));
        }

        Intent intent = new Intent(PlayerPick.this, PlayerWait.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Question", Game.questionID);
        intent.putExtra("RoundNumber", Game.roundNumber);
        intent.putExtra("isCzar", Game.isCzar);
        intent.putExtra("numAnswers", Game.numAnswers);
        startActivity(intent);
        finish();
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
