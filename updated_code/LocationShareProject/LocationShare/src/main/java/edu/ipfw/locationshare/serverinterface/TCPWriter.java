package edu.ipfw.locationshare.serverinterface;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;

public class TCPWriter implements Runnable {
    //Reference to the TCPClient that started this writer
    private TCPClient Client;

    //Output stream writer
    private PrintWriter OutputWriter;

    //Thread termination flag
    private volatile boolean Running;

    /*
     * Constructor
     */
    public TCPWriter(TCPClient client) {
        this.Client = client;

        try {
            //Create a output stream writer
            this.OutputWriter = new PrintWriter(this.Client.getSocket().getOutputStream());
        } catch (IOException e) {
            Log.e("TCPWriter:constructor", e.toString());
        }
    }

    /*
     * Shuts down writer
     */
    public void Terminate() {
        //Set running flag to false
        this.Running = false;

        //Flush and close the output stream writer
        this.OutputWriter.flush();
        this.OutputWriter.close();
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
                //Check if any text is in the output buffer
                if (this.Client.getWriteBuffer().peek() != null) {
                    //Get the text from the buffer
                    String nextLine = this.Client.getWriteBuffer().take();

                    //Write the text to the output stream
                    if (nextLine != null) {
                        OutputWriter.println(nextLine);
                        OutputWriter.flush();
                    }
                }

                //Sleep for 100 milliseconds
                Thread.sleep(100);
            } catch (Exception err) {
                Log.e("TCPWriter:run", err.toString());
            }
        }
    }
}
