package com.example.asus.cardsagainsthumanity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.asus.cardsagainsthumanity.R;
import com.example.asus.cardsagainsthumanity.router.Packet;
import com.example.asus.cardsagainsthumanity.router.Sender;
import com.example.asus.cardsagainsthumanity.wifi.WifiDirectBroadcastReceiver;

public class RoomActivity extends AppCompatActivity implements ManagerInterface
{
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private final IntentFilter wifiIntentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    WifiManager wifiManager;
    private boolean isWifiConnected;
    public boolean isVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // Set peer2peer actions
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        removeGroup();

        Intent intent = getIntent();
        String userType = intent.getStringExtra("Type");
        if (userType.equals("Owner")) // If owner, create a group
        {
            createGameRoom();
        }
        else if (userType.equals("Player")) // If player, send a hello message to get routing table from owner
        {
            Sender.queuePacket(new Packet(Packet.TYPE.HELLO, new byte[0], null, WifiDirectBroadcastReceiver.macAddress));
        }
    }

    @Override
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled)
    {

    }

    @Override
    public String getActivityName() {
        return "RoomActivity";
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeGroup() {
        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener()
        {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group)
            {
                if (group != null)
                {
                    manager.removeGroup(channel, new WifiP2pManager.ActionListener()
                    {
                        @Override
                        public void onSuccess()
                        {
                            System.out.println("Success");
                        }

                        @Override
                        public void onFailure(int reason)
                        {
                            System.out.println("Failure " + reason);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener()
        {

            @Override
            public void onSuccess()
            {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason)
            {
                Toast.makeText(RoomActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();

        if (manager != null && channel != null) {
            receiver = new WifiDirectBroadcastReceiver(manager, channel, this);
            registerReceiver(receiver, intentFilter);

            removeGroup();
        }
        this.isVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (receiver != null)
            unregisterReceiver(receiver);

        if (manager != null && channel != null) {
            removeGroup();
        }
    }

    public void createGameRoom()
    {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()){
            Toast.makeText(RoomActivity.this, "Enable P2P", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
        else {
            manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener()
            {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group)
                {
                    if (group != null)
                    {
                        manager.removeGroup(channel, new WifiP2pManager.ActionListener()
                        {
                            @Override
                            public void onSuccess()
                            {
                                System.out.println("Success");

                                manager.createGroup(channel, new WifiP2pManager.ActionListener()
                                {
                                    @Override
                                    public void onSuccess()
                                    {
                                        Log.wtf("Create Game: ", "P2P Group created");

                                        Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                                        intent.putExtra("Type", "Owner");
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int reason)
                                    {
                                        Log.wtf("Create Game: ", "P2P Group failed");
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int reason)
                            {
                                System.out.println("Failure " + reason);
                            }
                        });
                    } else
                    {
                        manager.createGroup(channel, new WifiP2pManager.ActionListener()
                        {
                            @Override
                            public void onSuccess()
                            {
                                Log.wtf("Create Game: ", "P2P Group created");

                                Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(int reason)
                            {
                                Log.wtf("Create Game: ", "P2P Group failed");
                            }
                        });

                    }
                }
            });
        }
    }
}
