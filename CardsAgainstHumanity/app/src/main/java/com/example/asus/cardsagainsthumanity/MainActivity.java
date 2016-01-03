package com.example.asus.cardsagainsthumanity;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.asus.cardsagainsthumanity.database.DatabaseHelper;
import com.example.asus.cardsagainsthumanity.game.utils.Game;


public class MainActivity extends AppCompatActivity
{
    private DatabaseHelper db=null;
    private Cursor cursor=null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startDatabase();
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

    public void startDatabase(){
        if(cursor==null){
            db = new DatabaseHelper(getApplicationContext());
            Game.db = db;
        }

        /*
        System.out.println(Game.getWhiteCardId(3));
        System.out.println(Game.getBlackCardId());
        System.out.println(Game.getWhiteCardText(6));
        System.out.println(Game.getBlackCardText(500));
        */

        /*
        //retornar carta branca com id-
        cursor = db.getWhiteCard(6);
        cursor.moveToFirst();
        System.out.println(DatabaseUtils.dumpCursorToString(cursor));
        //retornar carta preta com id-
        cursor = db.getBlackCard(500);
        cursor.moveToFirst();
        System.out.println(DatabaseUtils.dumpCursorToString(cursor));
        //retornar carta preta aleatoria-
        cursor = db.getBlackCards(1);
        cursor.moveToFirst();
        System.out.println(DatabaseUtils.dumpCursorToString(cursor));
        System.out.println("Answers required: "+cursor.getInt(2));
        //retornar 7 cartas brancas-
        cursor = db.getWhiteCards(7);
        while(cursor.moveToNext()){
            System.out.println("[Card] Id="+cursor.getInt(0)+" Text="+cursor.getString(1));
        }
        */
    }

    @Override
    public void onPause() {
        if (cursor!=null)
            cursor.close();

        db.close();
        super.onPause();
    }

    @Override
    public void onResume() {
        db=new DatabaseHelper(getApplicationContext());
        Game.db = db;
        super.onResume();
    }

    @Override
    public void onDestroy() {
        db.close();

        super.onDestroy();
    }
}
