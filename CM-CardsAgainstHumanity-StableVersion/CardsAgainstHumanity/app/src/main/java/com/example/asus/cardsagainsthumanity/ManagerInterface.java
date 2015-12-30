package com.example.asus.cardsagainsthumanity;

import android.net.wifi.p2p.WifiP2pConfig;

public interface ManagerInterface {
    /**
     * @param isWifiP2pEnabled
     *            the isWifiP2pEnabled to set
     */
    void setIsWifiP2pEnabled(boolean isWifiP2pEnabled);
    String getActivityName();
    void connect(WifiP2pConfig config);
}
