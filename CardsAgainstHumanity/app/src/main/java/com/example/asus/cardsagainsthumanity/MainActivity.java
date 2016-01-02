package com.example.asus.cardsagainsthumanity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.asus.cardsagainsthumanity.database.DatabaseHelper;
import com.example.asus.cardsagainsthumanity.wifi.WifiDirectBroadcastReceiver;


public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*CODIGO DE TESTE DA DATABASE - 10 CARTAS ALEATORIAS, 5 PRETAS E 5 RESPOSTAS*/
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Cursor result;
        for(int i=0;i<10;i++){
            if(i%2==0)
                result = db.getCard("white");
            else
                result = db.getCard("black");
            result.moveToFirst();
            System.out.println(DatabaseUtils.dumpCursorToString(result));
        }
        /*FIM DO CODIGO DE TESTE DA DATABASE*/
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

        return super.onOptionsItemSelected(item);
    }

    public void joinGame(View view)
    {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    public void createGame(View view)
    {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()){
            Toast.makeText(MainActivity.this, "Enable P2P", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
        else
        {
            Intent intent = new Intent(this, RoomActivity.class);
            intent.putExtra("Type", "Owner");
            startActivity(intent);
        }
    }
}
