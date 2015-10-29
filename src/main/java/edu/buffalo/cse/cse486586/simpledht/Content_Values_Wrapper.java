package edu.buffalo.cse.cse486586.simpledht;

import android.content.ContentValues;

import java.io.Serializable;

/**
 * Created by imransay on 3/29/15.
 */
public class Content_Values_Wrapper implements Serializable{
    public String key;
    public String value;
    public String originator = "";

    public Content_Values_Wrapper(String key,String value,String originator){
        this.key = key;
        this.value = value;
        this.originator = originator;
    }

}
