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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_pick);

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

        TextView questionTextView = (TextView) findViewById(R.id.black_card);
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

        adapter = new AnswerArrayAdapter(this, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.itemClicked(position);

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                if(adapter.getClickedItensSize() == 2) {
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

        String whiteCardMessage = MeshNetworkManager.getSelf().getMac();
        whiteCardMessage += ",";
        // whiteCardMessage += arrayList.get(0);
        whiteCardMessage += "0";
        if(arrayList.size() > 1) {
            whiteCardMessage += ",";
            // whiteCardMessage += arrayList.get(1);
            whiteCardMessage += "1";
        }

        for (AllEncompasingP2PClient c : MeshNetworkManager.routingTable.values())
        {
            if (c.getMac().equals(MeshNetworkManager.getSelf().getMac()))
                Game.responsesID.add(1);
            Sender.queuePacket(new Packet(Packet.TYPE.WHITECARD, whiteCardMessage.getBytes(), c.getMac(),
                    WifiDirectBroadcastReceiver.MAC));
        }

        Intent intent = new Intent(PlayerPick.this, PlayerWait.class);
        startActivity(intent);
    }
}
