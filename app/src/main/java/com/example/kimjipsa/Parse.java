package com.example.kimjipsa;

import java.util.ArrayList;
import java.util.HashMap;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by User on 2017-06-01.
 */
public class Parse {
    private HashMap<String, Integer> Items=new HashMap<String, Integer>();
    private ArrayList<Item> parsedItems=new ArrayList<Item>();

    public ArrayList<Item> Parsing(String parameter, HashMap<String,Integer> pHash){
        String[] array,smallArray;
        array=parameter.split(" ");
        for(int i=0;i<array.length;i++){
            smallArray=array[i].split(",");
            for(int j=0;j<smallArray.length;j++){
                String gotcha=null;
                for(int k=0;k<smallArray[j].length();k++){
                    for(int q=k+1;q<=smallArray[j].length();q++) {
                        String findString = smallArray[j].substring(k, q);
                        if(pHash.get(findString) != null){
                            gotcha = findString.toString();
                            int newWeight=pHash.remove(findString)+1;
                            Item newItem=new Item();
                            newItem.setBool_item(true);
                            newItem.setSub_name("");
                            newItem.setItem_name(gotcha);
                            newItem.setWeight(newWeight);
                            parsedItems.add(newItem);
                        }
                    }
                }
            }
        }
        return parsedItems;
    }
}