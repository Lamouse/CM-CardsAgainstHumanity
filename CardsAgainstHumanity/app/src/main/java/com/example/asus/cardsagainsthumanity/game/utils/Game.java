package com.example.asus.cardsagainsthumanity.game.utils;

import android.database.Cursor;
import android.database.DatabaseUtils;

import com.example.asus.cardsagainsthumanity.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jbsimoes on 02/01/16.
 */
public class Game
{
    public static String deviceName;
    public static boolean isCzar;
    public static int roundNumber;
    public static int questionID;
    public static int numAnswers;
    public static Map<String, Integer> scoreTable;
    public static ConcurrentLinkedQueue<Integer> responsesID;
    public static DatabaseHelper db;

    public static String getWhiteCardText(int id){
        Cursor cursor = db.getWhiteCard(id);
        cursor.moveToFirst();
        return cursor.getString(1);
    }

    public static String[] getBlackCardText(int id){
        Cursor cursor = db.getBlackCard(id);
        cursor.moveToFirst();
        String[] array = new String[2];
        //System.out.println(DatabaseUtils.dumpCursorToString(cursor));
        array[0] = cursor.getString(1);
        array[1] = cursor.getInt(2)+"";
        return array;
    }

    public static int[] getWhiteCardId(int num){
        Cursor cursor = db.getWhiteCards(num);
        int[] array = new int[num];
        int i = 0;
        while(cursor.moveToNext()){
            array[i] = cursor.getInt(0);
            i++;
        }
        return array;
    }

    public static int getBlackCardId(){
        Cursor cursor = db.getBlackCards(1);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
