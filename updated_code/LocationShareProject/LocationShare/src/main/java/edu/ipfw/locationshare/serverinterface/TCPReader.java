package edu.ipfw.locationshare.serverinterface;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TCPReader implements Runnable {
    //Reference to the TCPClient that started this reader
    private TCPClient Client;

    //Input stream reader
    private BufferedReader InputReader;

    //Thread termination flag
    private volatile boolean Running;

    public TCPReader(TCPClient client) {
        this.Client = client;

        try {
            //Create an input stream reader
            this.InputReader = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
        } catch (IOException e) {
            Log.e("TCPReader:constructor", e.toString());
        }
    }

    /*
     * Shuts down reader
     */
    public void Terminate() {
        //Set running flag to false
        this.Running = false;

        try {
            //Close the input stream reader
            this.InputReader.close();
        } catch (Exception err) {
            Log.e("TCPReader:Terminate", err.toString());
        }
    }

    /*
     * Communications logic, will be executed in a separate thread
     */
    @Override
    public void run() {
        //Set the running flag to true
        this.Running = true;

        while (this.Running) {
            try {
                //Get next line from input stream
                String nextLine = this.InputReader.readLine();

                if (nextLine == null) {
                    continue;
                }

                //Send the text to the AppClient for processing
                AppClient.getInstance().ProcessData(nextLine);
            } catch (IOException e) {
                Log.e("TCPReader:run", e.toString());
            }
        }
    }
}
