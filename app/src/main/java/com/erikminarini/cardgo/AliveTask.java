package com.erikminarini.cardgo;


import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AliveTask extends AsyncTask<Void, Void, Boolean> {

    String messageStr="_GPHD_:0:0:2:0.000000\\n";

    @Override
    protected void onPreExecute(){
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        try{
            int server_port = 8554;
            DatagramSocket s = new DatagramSocket();
            InetAddress local = InetAddress.getByName("10.5.5.100");
            int msg_length=messageStr.length();
            byte[] message = messageStr.getBytes();
            DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
            s.send(p);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
    }
}
