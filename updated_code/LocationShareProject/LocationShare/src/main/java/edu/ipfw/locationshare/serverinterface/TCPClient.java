package edu.ipfw.locationshare.serverinterface;

import android.util.Log;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPClient {
    //Connection parameters for TCP client
    private static final String SERVER_IP = "172.16.10.101";
    private static final int SERVER_PORT = 10000;

    //TCP communication objects
    private Socket Socket;
    private TCPReader Reader;
    private TCPWriter Writer;

    //Threads to provide async communications
    private Thread MainThread;
    private Thread ReaderThread;
    private Thread WriterThread;

    //Output buffer
    private BlockingQueue<String> WriteBuffer;

    //Flag to check if socket is running
    private boolean Running;

    /*
     * Constructor
     */
    public TCPClient() {
        this.WriteBuffer = new LinkedBlockingQueue<String>();
    }

    /*
     * Connects to server and starts async communication threads
     */
    public void Connect() {
        //Check if socket is already running
        if (this.Running) {
            //Terminate threads and close the socket
            try {
                this.Reader.Terminate();
                this.Writer.Terminate();

                this.Socket.close();
            } catch (Exception err) {
                Log.e("TCPClient:Connect", err.toString());
            }
        }

        //Set running flag to true
        this.Running = true;

        //Get a reference to the client to pass to the threads
        final TCPClient ref = this;

        //Start threads for async communications
        try {
            MainThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket = new Socket(SERVER_IP, SERVER_PORT);

                        Reader = new TCPReader(ref);
                        ReaderThread = new Thread(Reader);
                        ReaderThread.start();

                        Writer = new TCPWriter(ref);
                        WriterThread = new Thread(Writer);
                        WriterThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            MainThread.start();

        } catch (Exception err) {
            Log.e("TCPClient:Connect", err.toString());
        }
    }

    /*
     * Appends the text to the output buffer
     */
    public void Write(String text) {
        try {
            this.WriteBuffer.put(text);

            Log.d("TCPClient:Write", text);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * Getters / setters
     */
    public Socket getSocket() {
        return Socket;
    }

    public BlockingQueue<String> getWriteBuffer() {
        return WriteBuffer;
    }
}
