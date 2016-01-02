package com.example.asus.cardsagainsthumanity.game;

import android.net.wifi.p2p.WifiP2pConfig;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asus.cardsagainsthumanity.ManagerInterface;
import com.example.asus.cardsagainsthumanity.R;

public class PlayerWait extends AppCompatActivity implements ManagerInterface
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_wait);
    }

    @Override
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled)
    {

    }

    @Override
    public String getActivityName()
    {
        return "PlayerWait";
    }

    @Override
    public void connect(WifiP2pConfig config)
    {

    }
}
