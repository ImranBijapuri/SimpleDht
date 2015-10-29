package edu.buffalo.cse.cse486586.simpledht;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by imransay on 3/28/15.
 */
public class ClientTask_Insert_Data extends AsyncTask<Content_Values_Wrapper, Void, Void> {

    @Override
    protected Void doInBackground(Content_Values_Wrapper... content_Values_Wrapper) {
        try {
            Log.d("Logger", "Started a new ClientTask_Insert_Data and being flushed to " + SimpleDhtProvider.succ_port + "and the key is " + content_Values_Wrapper[0].key);
            Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(SimpleDhtProvider.succ_port));
            ObjectOutputStream outputstream = new ObjectOutputStream(socket.getOutputStream());
            outputstream.writeObject(content_Values_Wrapper[0]);
            outputstream.flush();
            socket.close();
        } catch (UnknownHostException e) {
            Log.e("E", "ClientTask UnknownHostException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("E", "ClientTask socket IOException");
        } catch (Exception e){
            Log.e("E", "Some Exception Occured in Node_Join_ClientTask");
        }

        return null;
    }
}
