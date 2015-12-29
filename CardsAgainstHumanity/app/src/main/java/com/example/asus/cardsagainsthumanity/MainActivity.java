package com.example.asus.cardsagainsthumanity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.asus.cardsagainsthumanity.wifi.WifiDirectBroadcastReceiver;


public class MainActivity extends AppCompatActivity implements ManagerInterface {

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
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public String getActivityName() {
        return "MainActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set peer2peer actions
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

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
                            System.out.println("Failure " + reason);
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createGame(View view)
    {
        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
            if (group != null) {
                manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Success");

                        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.wtf("Create Game: ", "P2P Group created");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.wtf("Create Game: ", "P2P Group failed");
                            }
                        });
                    }

                    @Override
                    public void onFailure(int reason) {
                        System.out.println("Failure " + reason);
                    }
                });
            }
            else {
                manager.createGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.wtf("Create Game: ", "P2P Group created");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.wtf("Create Game: ", "P2P Group failed");
                    }
                });

            }
            }
        });


        Intent intent = new Intent(this, RoomActivity.class);
        startActivity(intent);
    }

    public void joinGame(View view)
    {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    /** register the BroadcastReceiver with the intent values to be matched */
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

        if (manager != null && channel != null) {
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
                            System.out.println("Failure " + reason);
                        }
                    });
                }
                }
            });
        }
    }
}
