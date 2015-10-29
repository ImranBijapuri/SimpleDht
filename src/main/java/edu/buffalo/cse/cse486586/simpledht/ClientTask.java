package edu.buffalo.cse.cse486586.simpledht;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by imransay on 3/28/15.
 */
public class ClientTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... node) {
        try {
            Log.d("Logger", "Started a new ClientTask");
            Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), 11108);
            ObjectOutputStream outputstream = new ObjectOutputStream(socket.getOutputStream());
            outputstream.writeObject(node[0]);
            outputstream.flush();
            socket.close();
        } catch (UnknownHostException e) {
            Log.e("E", "ClientTask UnknownHostException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("E", "ClientTask socket IOException");
        }

        return null;
    }
}
