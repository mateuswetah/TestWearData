package com.example.mateus.testweardata;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by mateus on 10/1/15.
 */

public class UDP_Client {

    private AsyncTask<Void, Void, Void> async_cient;
    private String SERVER_ADD = "";
    private int SERVER_PORT = 25000;


    public void setServer(String address, int port) {
        SERVER_ADD = address;
        SERVER_PORT = port;
    }

    @SuppressLint("NewApi")
    public void sendMessage(final String word)
    {
        async_cient = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {

                DatagramSocket ds = null;

                try
                {
                    ds = new DatagramSocket();
                    DatagramPacket dp;
                    dp = new DatagramPacket(word.getBytes(), word.length(), InetAddress.getByName(SERVER_ADD), SERVER_PORT);
                    ds.setBroadcast(false);
                    ds.send(dp);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {

                    if (ds != null)
                    {

                        ds.close();
                    }
                }
                return null;
            }

            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }
        };

        if (Build.VERSION.SDK_INT >= 11) async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_cient.execute();
    }
}
