package com.example.asus.cardsagainsthumanity.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.asus.cardsagainsthumanity.LobbyActivity;
import com.example.asus.cardsagainsthumanity.MainActivity;
import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;

/**
 * Created by jbsimoes on 28/12/15.
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver
{
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Activity activity;

    public WifiDirectBroadcastReceiver(WifiP2pManager pManager, WifiP2pManager.Channel pChannel, Activity pActivity) {
        super();
        manager = pManager;
        channel = pChannel;
        activity = pActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                ((ManagerInterface) activity).setIsWifiP2pEnabled(true);

                /*manager.createGroup(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.d(WiFiDirectActivity.TAG, "P2P Group created");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(WiFiDirectActivity.TAG, "P2P Group failed");
                    }
                });*/
            } else {
                ((ManagerInterface) activity).setIsWifiP2pEnabled(false);
                //activity.resetData();

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null && "LobbyActivity".equals(((ManagerInterface) activity).getActivityName())) {
                manager.requestPeers(channel,
                        (WifiP2pManager.PeerListListener) activity.getFragmentManager().findFragmentById(R.id.frag_list));
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP
                /*DeviceDetailFragment fragment = (DeviceDetailFragment) activity.getFragmentManager().findFragmentById(
                        R.id.frag_detail);
                manager.requestConnectionInfo(channel, fragment);*/
            } else {
                // It's a disconnect
                //activity.resetData();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            /*DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager().findFragmentById(
                    R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

            MAC = ((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)).deviceAddress;

            //Set yourself on connection
            MeshNetworkManager.setSelf(new AllEncompasingP2PClient(((WifiP2pDevice) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)).deviceAddress, Configuration.GO_IP,
                    ((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)).deviceName,
                    ((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)).deviceAddress));

            //Launch receiver and sender once connected to someone
            if (!Receiver.running) {
                Receiver r = new Receiver(this.activity);
                new Thread(r).start();
                Sender s = new Sender();
                new Thread(s).start();
            }

            manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null) {
                        // clients require these
                        String ssid = group.getNetworkName();
                        String passphrase = group.getPassphrase();

                        Log.d(WiFiDirectActivity.TAG, "GROUP INFO AVALABLE");
                        Log.d(WiFiDirectActivity.TAG, " SSID : " + ssid + "\n Passphrase : " + passphrase);

                    }
                }
            });*/
        }
    }
}
