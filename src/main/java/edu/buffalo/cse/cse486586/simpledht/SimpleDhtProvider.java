package edu.buffalo.cse.cse486586.simpledht;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimpleDhtProvider extends ContentProvider {
    private static final String AUTHORITY = "edu.buffalo.cse.cse486586.simpledht.SimpleDhtProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static MySQLiteHelper db = null;
    public static String myport;
    public static String succ_port = "";
    public static String pred_port = "";
    public Context context = this.getContext();


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db.delete(selection);

        // TODO Auto-generated method stub
        return 0;
    }

    public int delete_from_all_dht(DeleteObject deleteObject){
        db.delete_from_all_dht(deleteObject);
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

Log.d("Logger" , "I am emulator " + SimpleDhtProvider.myport + " and my succ is " + SimpleDhtProvider.succ_port + " and my pred is " + SimpleDhtProvider.pred_port);
        String this_node_id = "";
        String this_node_succ_id = "";
        String this_node_pred_id = "";
        String keyhash = "";


        try {
            keyhash = genHash((String) values.get("key"));
            this_node_id = genHash(Make_it_Half(SimpleDhtProvider.myport));
            if(succ_port != "") {
                this_node_succ_id = genHash(Make_it_Half(SimpleDhtProvider.succ_port));
            }
            if(pred_port != ""){
                this_node_pred_id = genHash(Make_it_Half(SimpleDhtProvider.pred_port));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }



        if (SimpleDhtProvider.succ_port.equals("") && SimpleDhtProvider.pred_port.equals("")) {
            Log.d("Logger", " Insert " + values.get("key") + " first Condition Insert in this node");
            db.insert(values);
        }

        else if(keyhash.compareTo(this_node_pred_id) > 0 && this_node_id.compareTo(keyhash) > 0){
            Log.d("Logger", " Insert " + values.get("key") + "  into 2nd condition");
            db.insert(values);
        }

        else if(this_node_pred_id.compareTo(this_node_id) > 0 && keyhash.compareTo(this_node_pred_id) > 0){
            Log.d("Logger" , "I know I am the first guy and  " + values.get("key") + "  the keyhash is gereater than largest one");
            db.insert(values);

        }

        else if(this_node_pred_id.compareTo(this_node_id) > 0 && keyhash.compareTo(this_node_id) < 0){
            Log.d("Logger" , "I know I am the first guy and  " + values.get("key") + "  the keyhash is smallest than small one");
            db.insert(values);

        }

        else {
            Log.d("Logger", " Insert into 3rd condition  " + values.get("key") + "  at " + myport);
            Content_Values_Wrapper content_Values_Wrapper;
            content_Values_Wrapper = new Content_Values_Wrapper(values.getAsString("key"),values.getAsString("value"),SimpleDhtProvider.myport);
            new ClientTask_Insert_Data().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, content_Values_Wrapper, null);
        }



        return null;
    }


    public String Make_it_Half(String str){
        return Integer.toString(Integer.parseInt(str)/2);
    }

    @Override
    public boolean onCreate() {


        myport = get_my_port();

        try {
            Log.d("Logger", "Here Begins a Server Task");
            ServerSocket serverSocket = new ServerSocket(10000);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e("E", "Can't create a ServerSocket");
        }


        //New Node Join Request
        if (!myport.equals("11108")) {
            Log.d("Logger", "Port is " + myport);
            Joining_Node obj_joinnode = new Joining_Node();
            obj_joinnode.port = myport;
            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, obj_joinnode, null);
        }

        db = new MySQLiteHelper(getContext());
        db.getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        //db = new MySQLiteHelper(getContext());
        return db.query(selection);
        // TODO Auto-generated method stub

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    public Cursor Local_Dump(String selection){
        return db.query(selection);
    }

    private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public String get_my_port() {
        TelephonyManager tel = (TelephonyManager) this.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        return String.valueOf((Integer.parseInt(portStr) * 2));
    }


    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            Log.d("Logger", "In servertask doInBackground");
            ServerSocket serverSocket = sockets[0];
            Socket socket = null;
            ObjectInputStream inputstream = null;
            Object object = null;

            while (true) {
                try {
                    Log.d("Logger", "Entered servertask try block");
                    socket = serverSocket.accept();
                    inputstream = new ObjectInputStream(socket.getInputStream());
                    object = inputstream.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


                if (object instanceof Joining_Node) {
                    Log.d("Logger", "Instance of Joining Node");
                    Joining_Node joining_node = (Joining_Node) object;
                    Log.d("Logger", "Port of joining node " + joining_node.port);

                    String joining_node_id = genHash(Make_it_Half(joining_node.port));
                    String this_node_id = genHash(Make_it_Half(SimpleDhtProvider.myport));
                    String this_node_succ_id = "";
                    String this_node_pred_id = "";

                    if (SimpleDhtProvider.succ_port != "") {
                        this_node_succ_id = genHash(Make_it_Half(SimpleDhtProvider.succ_port));
                    }
                    if (SimpleDhtProvider.pred_port != "") {
                        this_node_pred_id = genHash(Make_it_Half(SimpleDhtProvider.pred_port));
                    }


                    if (SimpleDhtProvider.succ_port.equals("") && SimpleDhtProvider.pred_port.equals("")) {

                        Log.d("Logger", "Both Succ and pred are null");
                        SimpleDhtProvider.succ_port = joining_node.port;
                        HashMap map = new HashMap();
                        map.put("S", SimpleDhtProvider.myport);
                        map.put("P", SimpleDhtProvider.myport);
                        map.put("C", joining_node.port);
                        new Node_Join_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, map, null);

                    } else if (joining_node_id.compareTo(this_node_id) > 0 && this_node_succ_id.compareTo(joining_node_id) > 0) {

                        Log.d("Logger", "We found correct position of " + joining_node.port + " in 2nd cond");
                        HashMap map = new HashMap();
                        map.put("S", SimpleDhtProvider.succ_port);
                        map.put("P", SimpleDhtProvider.myport);
                        map.put("C", joining_node.port);
                        SimpleDhtProvider.succ_port = joining_node.port;
                        new Node_Join_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, map, null);

                    } else if (this_node_id.compareTo(this_node_succ_id) > 0) {
                        Log.d("Logger", "At the curvature");
                        if(joining_node_id.compareTo(this_node_id) > 0){

                            Log.d("Logger","this is the highest joining node");
                            HashMap map = new HashMap();
                            map.put("S", SimpleDhtProvider.succ_port);
                            map.put("P", SimpleDhtProvider.myport);
                            map.put("C", joining_node.port);
                            SimpleDhtProvider.succ_port = joining_node.port;
                            new Node_Join_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, map, null);

                        }else if(this_node_succ_id.compareTo(joining_node_id) > 0){

                            Log.d("Logger","this is the smallest joining node");
                            HashMap map = new HashMap();
                            map.put("S", SimpleDhtProvider.succ_port);
                            map.put("P", SimpleDhtProvider.myport);
                            map.put("C", joining_node.port);
                            SimpleDhtProvider.succ_port = joining_node.port;
                            new Node_Join_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, map, null);

                        }else{

                            Log.d("Logger","Pass on from curvature");
                            new Node_Join_Clienttask_Temp().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, joining_node, null);

                        }
                    } else {

                        Log.d("Logger", " Didnt find the right position to enter node join ");
                        new Node_Join_Clienttask_Temp().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, joining_node, null);
                    }

                } else if (object instanceof HashMap) {
                    Log.d("Logger", "In Instance of Hashmap");
                    HashMap map = (HashMap) object;
                    if (map.containsKey("P")) {

                        Log.d("Logger", "Hashmap contains P");
                        String predecessor = (String) map.get("P");
                        SimpleDhtProvider.pred_port = predecessor;
                        Log.d("Logger", "Predecessor of node " + SimpleDhtProvider.myport + " is " + SimpleDhtProvider.pred_port);

                    }

                    if (map.containsKey("S")) {

                        Log.d("Logger", "Hashmap contains S");
                        String successor = (String) map.get("S");
                        SimpleDhtProvider.succ_port = successor;
                        Log.d("Logger", "Successor of node " + SimpleDhtProvider.myport + " is " + SimpleDhtProvider.succ_port);
                        map = new HashMap();
                        map.put("P", SimpleDhtProvider.myport);
                        map.put("C", SimpleDhtProvider.succ_port);
                        new Node_Join_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, map, null);

                    }

                } else if (object instanceof Content_Values_Wrapper) {

                    Log.d("Logger", "Object is of type Content_Values_Wrapper");
                    Content_Values_Wrapper content_Values_Wrapper = (Content_Values_Wrapper) object;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("key", content_Values_Wrapper.key);
                    contentValues.put("value", content_Values_Wrapper.value);
                    contentValues.put("originator", content_Values_Wrapper.originator);
                    Log.d("Logger", "Object is of type Content_Values_Wrapper " + content_Values_Wrapper.key);
                    SimpleDhtProvider simpleDhtProvider = new SimpleDhtProvider();
                    simpleDhtProvider.insert(SimpleDhtProvider.CONTENT_URI, contentValues);

                } else if (object instanceof DeleteObject) {

                    Log.d("Logger","In instance of deleteobject " );
                    MySQLiteHelper.globalcursor = new MatrixCursor(new String[]{"key","value"});
                    DeleteObject deleteObject = (DeleteObject) object;
                    SimpleDhtProvider simpleDhtProvider = new SimpleDhtProvider();
                    simpleDhtProvider.delete_from_all_dht(deleteObject);

                } else if (object instanceof SelectObject) {

                    SelectObject selectObject = (SelectObject) object;
                    if (selectObject.originport.equals(SimpleDhtProvider.myport)) {
                        Log.d("Logger","I am returning the global cursor and I am " + SimpleDhtProvider.myport);
                        Iterator it = selectObject.map.entrySet().iterator();
                        Log.d("Logger" , "The size of map is " + selectObject.map.size());
                        if(MySQLiteHelper.globalcursor.getCount()!= 0 ){
                            MySQLiteHelper.globalcursor = new MatrixCursor(new String[]{"key","value"});
                        }
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            MySQLiteHelper.globalcursor.addRow(new String[]{(String)pair.getKey(),(String)pair.getValue()});
                            it.remove(); // avoids a ConcurrentModificationException
                        }
                        Log.d("Logger" , "The size of globalcursor is " + MySQLiteHelper.globalcursor.getCount());
                        //MySQLiteHelper.globalcursor = selectObject.cursor;
                        MySQLiteHelper.status = false;
                    } else {

                        Log.d("Logger","I am not returning global cursor and I am  " + SimpleDhtProvider.myport);
                        Cursor cursor;
                        if(selectObject.selection.equals("")) {
                            cursor = db.special_selection("*");
                        }else{
                            cursor = db.special_selection(selectObject.selection);
                        }
                        if (cursor.moveToFirst()) {
                            do {
                                Log.d("Logger" , "In server task and we got sumthing");
                                selectObject.map.put(cursor.getString(0), cursor.getString(1));
                            } while (cursor.moveToNext());
                        }else{
                            Log.d("Logger" , "In server task and we got nothin");
                        }
                        new Data_Select_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, selectObject, null);

                    }
                }
            }


            //return null;
        }


        private String genHash(String input) {
            MessageDigest sha1 = null;
            try {
                sha1 = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            byte[] sha1Hash = sha1.digest(input.getBytes());
            Formatter formatter = new Formatter();
            for (byte b : sha1Hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }


    }


}
