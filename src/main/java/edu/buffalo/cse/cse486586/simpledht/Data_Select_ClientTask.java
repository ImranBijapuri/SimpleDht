package edu.buffalo.cse.cse486586.simpledht;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by imransay on 3/29/15.
 */
public class Data_Select_ClientTask extends AsyncTask<SelectObject, Void, Void> {

    @Override
    protected Void doInBackground(SelectObject... seleteObject) {
        try {

            Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(SimpleDhtProvider.succ_port));
            ObjectOutputStream outputstream = new ObjectOutputStream(socket.getOutputStream());
            outputstream.writeObject(seleteObject[0]);
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