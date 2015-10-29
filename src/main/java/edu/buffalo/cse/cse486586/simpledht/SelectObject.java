package edu.buffalo.cse.cse486586.simpledht;

import android.database.Cursor;
import android.database.MatrixCursor;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by imransay on 3/29/15.
 */
public class SelectObject implements Serializable {
    public String originport;
    public HashMap map;
    public String selection;
}
