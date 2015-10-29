package edu.buffalo.cse.cse486586.simpledht;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by imransay on 3/26/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Messenger.db";
    public static boolean status = true;
    public static MatrixCursor globalcursor = new MatrixCursor(new String[]{"key","value"});


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("Logger","I am in Mysqllitehelper constructor after super call");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Logger","call to oncreate from constructor");
        String CREATE_MESSENGER_TABLE = "CREATE TABLE messenger (key varchar(500) UNIQUE,value varchar(500))";
        db.execSQL(CREATE_MESSENGER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(ContentValues values){
        Log.d("Logger","Inserting into db at " + SimpleDhtProvider.myport + " values " + values.get("key") + "  " + values.get("value"));
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT or replace INTO messenger(key,value) VALUES('"+values.get("key")+"','"+values.get("value")+"');");
    }

    public Cursor query(String selection) {
        SQLiteDatabase db = this.getReadableDatabase();

        if(selection.equals("\"@\"")){
            Cursor cursor ;
            cursor = db.rawQuery("SELECT key,value FROM messenger", null);
            Log.d("Logger" , " Total now of rows " + cursor.getCount());
            return cursor;
        }else if(selection.equals("\"*\"")){
            Cursor cursor ;
            cursor =  db.rawQuery("SELECT key,value FROM messenger", null);
            if(SimpleDhtProvider.pred_port=="" && SimpleDhtProvider.succ_port==""){
                Log.d("Logger" , " Total now of rows I got in * operatian and I am alone" + cursor.getCount());
                return cursor;
            }else{
                //MatrixCursor c = new MatrixCursor(new String[2]) ;
                HashMap map = new HashMap<String,String>();
                if(cursor.moveToFirst()){
                    do{
                        map.put(cursor.getString(0),cursor.getString(1));
                    }while(cursor.moveToNext());
                }
                SelectObject selectObject = new SelectObject();
                selectObject.originport = SimpleDhtProvider.myport;
                selectObject.map = map;
                selectObject.selection = "";
                new Data_Select_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, selectObject, null);
                while (status) {
                }
                status = true;
                Log.d("Logger" , " Total now of rows I got in * operation and I am not alone " + globalcursor.getCount());
                try {
                    if(globalcursor.moveToFirst()){
                        do{
                            Log.d("Logger","Globalcursor receveied " + globalcursor.getString(0) + "    "  + globalcursor.getString(1));
                        }while(globalcursor.moveToNext());
                    }
                }catch(Exception e ){
                    Log.d("Logger" , e.getMessage());
                    Log.d("Logger" , "In excep globcurs");
                }

                Log.d("Logger" , " aftr print globalcurs" );
                return globalcursor;

            }



        }else{
            Cursor cursor ;
            cursor = db.rawQuery("SELECT key,value FROM messenger where key='"+selection + "'", null);
            Log.d("Logger" , " Total now of rows " + cursor.getCount());

            if(cursor.getCount() == 0){
                Log.d("Logger" , " I got a row count of 0 and hence passing it on " + cursor.getCount());
                MatrixCursor c = new MatrixCursor(new String[2]) ;
                HashMap map = new HashMap<String,String>();
                SelectObject selectObject = new SelectObject();
                selectObject.originport = SimpleDhtProvider.myport;
                selectObject.map = map;
                selectObject.selection = selection;
                new Data_Select_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, selectObject, null);
                while (status) {
                }
                status = true;
                Log.d("Logger" , " Total now of rows I got in * operation and I am not alone" + globalcursor.getCount());
                try {
                    if(globalcursor.moveToFirst()){
                        do{
                            Log.d("Logger","Globalcursor receveied " + globalcursor.getString(0) + "    "  + globalcursor.getString(1));
                        }while(globalcursor.moveToNext());
                    }
                }catch(Exception e ){
                    Log.d("Logger" , e.getMessage());
                    Log.d("Logger" , "In excep globcurs");
                }

                Log.d("Logger" , " aftr print globalcurs" );

                return globalcursor;

            }else{
                return cursor;
            }


        }


    }

    public Cursor special_selection(String selection){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Logger" , "I am in special selection");
        Cursor cursor;
        if(selection.equals("*")) {
            cursor = db.rawQuery("SELECT key,value FROM messenger", null);
            Log.d("Logger" , "I got somthing in  special selection " + cursor.getCount());
        }else{
            cursor = db.rawQuery("SELECT key,value FROM messenger where key='"+selection + "'", null);
            Log.d("Logger" , "I got somthing in special  special selection " + cursor.getCount());
        }
        return cursor;
    }

    public void query_from_all_dht(SelectObject selectObject) {
        SQLiteDatabase db = this.getReadableDatabase();

    }




    public void delete(String selection) {
        SQLiteDatabase db = this.getReadableDatabase();
        if(selection.equals("\"@\"")){
            Log.d("Logger","deleting @ " );
            //db.rawQuery("Delete from messenger'", null);
            db.execSQL("Delete from messenger'");
        }else if (selection.equals("\"*\"")){
            Log.d("Logger","Deleting " + selection  +" everything on " + SimpleDhtProvider.myport);
            //db.rawQuery("Delete from messenger'", null);
            db.execSQL("Delete from messenger'");
            DeleteObject deleteObject = new DeleteObject();
            deleteObject.originport = SimpleDhtProvider.myport;
            deleteObject.selection="";
            new Data_Delete_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, deleteObject, null);
        }else{
            Cursor cursor = db.rawQuery("SELECT key,value FROM messenger where key='"+selection + "'", null);
            if(cursor.getCount() == 0){
                Log.d("Logger","not found specific deletion on origin and passing on " + selection );
                //forward del req
                DeleteObject deleteObject = new DeleteObject();
                deleteObject.originport = SimpleDhtProvider.myport;
                deleteObject.selection = selection;
                new Data_Delete_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, deleteObject, null);

            }else{
                Log.d("Logger","found specific on origin and done "  + selection);
                //db.rawQuery("Delete from messenger where key='"+selection + "'", null);
                db.execSQL("Delete from messenger where key='"+selection + "'");
            }



        }


    }

    public void delete_from_all_dht(DeleteObject deleteObject){
        SQLiteDatabase db = this.getReadableDatabase();

        if(deleteObject.originport.equals(SimpleDhtProvider.myport)){
            Log.d("Logger","DeleTED everything and back to origin "  + deleteObject.selection);
        }

        else if (deleteObject.selection.equals("")){
            Log.d("Logger","DeleTED everything and passing on " + deleteObject.selection);
            //db.rawQuery("Delete from messenger'", null);
            db.execSQL("Delete from messenger'");
            new Data_Delete_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, deleteObject, null);
        }

        else if (!deleteObject.selection.equals("")){
            Cursor cursor = db.rawQuery("SELECT key,value FROM messenger where key='"+ deleteObject.selection + "'", null);

            if(cursor.getCount() == 0){
                Log.d("Logger","not found specific and passing on "+deleteObject.selection );
                new Data_Delete_ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, deleteObject, null);
            }else {
                Log.d("Logger","found specific and ending " + deleteObject.selection );
                //db.rawQuery("Delete from messenger where key='"+deleteObject.selection + "'", null);
                db.execSQL("Delete from messenger where key='"+deleteObject.selection + "'");
            }
        }


    }

}
