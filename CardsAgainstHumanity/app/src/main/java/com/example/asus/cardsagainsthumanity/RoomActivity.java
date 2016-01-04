package com.example.asus.cardsagainsthumanity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.asus.cardsagainsthumanity.game.CzarPick;
import com.example.asus.cardsagainsthumanity.game.utils.Game;
import com.example.asus.cardsagainsthumanity.router.AllEncompasingP2PClient;
import com.example.asus.cardsagainsthumanity.router.MeshNetworkManager;
import com.example.asus.cardsagainsthumanity.router.Packet;
import com.example.asus.cardsagainsthumanity.router.Receiver;
import com.example.asus.cardsagainsthumanity.router.Sender;
import com.example.asus.cardsagainsthumanity.wifi.WifiDirectBroadcastReceiver;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RoomActivity extends AppCompatActivity implements ManagerInterface
{
    private WifiP2pManager manager;
    private String userType;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    public boolean isVisible = true;

    @Override
    public String getActivityName() {
        return "RoomActivity";
    }

    public String getUserType() {
        return userType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Receiver.setActivity(RoomActivity.this);
        Game.RoomActivity = this;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userType = null;
            } else {
                userType = extras.getString("Type");
            }
        } else {
            userType = (String) savedInstanceState.getSerializable("Type");
        }

        // Set peer2peer actions
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        // removeGroup();

        if (userType.equals("Owner"))
        {
            Log.d("Is owner:", "Creating room");
            // isto depois e tratado o WiFiDirectBroadcastReceiver
        }
        else if (userType.equals("Player"))
        {
            String deviceAddress;
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    deviceAddress = null;
                } else {
                    deviceAddress = extras.getString("Device");
                }
            } else {
                deviceAddress = (String) savedInstanceState.getSerializable("Device");
            }

            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = deviceAddress;
            config.wps.setup = WpsInfo.PBC;

            connect(config);
        }

        Game.roundNumber = 1;
        Game.responsesID = new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                // Toast.makeText(RoomActivity.this, "Connect Successful.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                // Toast.makeText(RoomActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updatePeersList()
    {
        Log.d("updatePeersList", "Enter");
        RoomPeersList fragment = (RoomPeersList) getSupportFragmentManager().findFragmentById(R.id.room_peers_list);
        fragment.updateRoomPeers();

        if (Game.isCzar) //FIXME
        {
            Button b = (Button) findViewById(R.id.button);
            b.setText("Start Game!");
            b.setEnabled(true);
            b.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String czarMessage = MeshNetworkManager.getSelf().getMac();
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
                    String[] questionText = Game.getBlackCardText(questionId);  //[0] has text, [1] has number of answers
                    Game.numAnswers = Integer.parseInt(questionText[1]);
                    TreeMap<String, Integer> hashResults = new TreeMap<String, Integer>();
                    for (AllEncompasingP2PClient c : MeshNetworkManager.routingTable.values()) {
                        hashResults.put(c.getMac(), 0);
                    }
                    Game.scoreTable = Game.sortByValue(hashResults);
                    Game.deviceName = MeshNetworkManager.getSelf().getMac();

                    Intent intent = new Intent(RoomActivity.this, CzarPick.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Question", Game.questionID);
                    intent.putExtra("RoundNumber", Game.roundNumber);
                    intent.putExtra("isCzar", Game.isCzar);
                    intent.putExtra("numAnswers", Game.numAnswers);
                    startActivity(intent);
                    // finish();
                }
            });
        }
        else
        {
            Button b = (Button) findViewById(R.id.button);
            b.setEnabled(false);
        }
    }

    private void removeGroup() {
        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
            if (group != null) {
                manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Success");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            }
        });
    }

    public void sendFirstPacket() {
        Sender.queuePacket(new Packet(Packet.TYPE.HELLO, new byte[0], null, WifiDirectBroadcastReceiver.MAC));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (manager != null && channel != null) {
            receiver = new WifiDirectBroadcastReceiver(manager, channel, this);
            registerReceiver(receiver, intentFilter);
        }
        this.isVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (receiver != null)
            unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (manager != null && channel != null) {
            Sender.queuePacket(new Packet(Packet.TYPE.BYE, new byte[0], null, WifiDirectBroadcastReceiver.MAC));
            removeGroup();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
