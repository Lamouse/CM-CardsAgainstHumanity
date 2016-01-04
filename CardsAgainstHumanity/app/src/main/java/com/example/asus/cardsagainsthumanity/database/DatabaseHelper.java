package com.example.asus.cardsagainsthumanity.database;

/**
 * Created by itspm on 02/01/2016.
 */
import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context=null;
    private static final String DATABASE_NAME="cah.db";
    private static final int SCHEMA=1;

    //We could put those on a contract class container...
    static final String ID="id";
    static final String TEXT="text";
    static final String NUMANSWERS="numanswers";
    static final String TABLE1="white";
    static final String TABLE2="black";
    private static SQLiteDatabase db=null;

    //SCHEMA Version is 1
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
        this.context = context;
        //SQLiteDatabase.loadLibs(context);
    }

    //Called on db creation, thanks to getReadableDatabase or getWriteableDatabase
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Table creation !!!
        try {
            this.db = db;
            db.beginTransaction();
            db.execSQL("CREATE TABLE "+ TABLE1 +" ("+ ID +" INTEGER, "+ TEXT +" TEXT);");
            db.execSQL("CREATE TABLE "+ TABLE2 +" ("+ ID +" INTEGER, "+ TEXT +" TEXT, "+ NUMANSWERS +" INTEGER);");

            ContentValues cv=new ContentValues();

            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(readJson("cards.json"));
            for(Object object: array){
                JSONObject card = (JSONObject) object;
                int id = (int)(long) card.get("id");
                String cardType = (String) card.get("cardType");
                String text = (String) card.get("text");
                int numAnswers = (int)(long)  card.get("numAnswers");
                if(numAnswers>2){
                    continue;
                }
                String expansion = (String) card.get("expansion");

                cv.clear();
                System.out.println(cv.toString());
                if(cardType.equals("Q")){
                    cv.put(ID, id);
                    cv.put(TEXT, text);
                    cv.put(NUMANSWERS, numAnswers);
                    db.insert(TABLE2, null, cv);
                }else{
                    cv.put(ID, id);
                    cv.put(TEXT, text);
                    db.insert(TABLE1, null, cv);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        Log.d("Database","Repopulated the database");
    }

    public Cursor getWhiteCards(int num){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery( "SELECT * FROM "+TABLE1+" ORDER BY RANDOM() LIMIT "+num, null );
        return res;
    }

    public Cursor getBlackCards(int num){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery( "SELECT * FROM "+TABLE2+" ORDER BY RANDOM() LIMIT "+num, null );
        return res;
    }

    public Cursor getWhiteCard(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery( "SELECT * FROM "+TABLE1+" WHERE "+ ID +"="+id, null );
        return res;
    }

    public Cursor getBlackCard(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery( "SELECT * FROM "+TABLE2+" WHERE "+ ID +"="+id, null );
        return res;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        throw new RuntimeException("How did we get here?");
    }

    public SQLiteDatabase getReadableDatabase() {
        return(super.getReadableDatabase());
    }

    public SQLiteDatabase getWritableDatabase() {
        return(super.getWritableDatabase());
    }

    private String readJson(String path){
        try{
            InputStream json = context.getAssets().open(path);
            int size = json.available();
            byte[] buffer = new byte[size];
            json.read(buffer);
            json.close();
            String jsonString = new String(buffer, "UTF-8");
            return jsonString;
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }
}
