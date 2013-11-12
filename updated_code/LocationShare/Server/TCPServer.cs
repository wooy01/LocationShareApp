using DataModel;
using DBInterface;
using Newtonsoft.Json;
using NLog;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Server
{
    public class TCPServer
    {
        private static Logger logger = LogManager.GetCurrentClassLogger();

        private const int BUFFER_SIZE = 4096;
        private byte[] buffer = new byte[BUFFER_SIZE];
        private Socket tcpSocket;

        public int Port { get; private set; }
        public ConcurrentDictionary<string, Socket> Clients { get; private set; }
        private AppServer appServer;

        public TCPServer(int port = 10000)
        {
            this.Clients = new ConcurrentDictionary<string, Socket>();
            this.appServer = new AppServer(this);

            //Setup the tcp server
            this.Port = port;
            tcpSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            tcpSocket.Bind(new IPEndPoint(IPAddress.Any, this.Port));

            //Start the TCP server
            tcpSocket.Listen(10);
            tcpSocket.BeginAccept(new AsyncCallback(AcceptCallback), null);
        }

        private void AcceptCallback(IAsyncResult iar)
        {
            //End the accept handler
            Socket socket = tcpSocket.EndAccept(iar);

            logger.Info("Client connected.");

            //Receive data and accept connection
            socket.BeginReceive(buffer, 0, BUFFER_SIZE, SocketFlags.None, new AsyncCallback(ReceiveCallback), socket);
            tcpSocket.BeginAccept(new AsyncCallback(AcceptCallback), null);
        }

        private void ReceiveCallback(IAsyncResult iar)
        {
            Socket socket = null;
            int received = 0;

            try
            {
                //Get socket
                socket = (Socket)iar.AsyncState;

                //Get number of bytes received
                received = socket.EndReceive(iar);

                //Create a temporary buffer to hold the received data
                byte[] dataBuf = new byte[received];

                //Copy the incomfing buffer data into the temp buffer
                Array.Copy(buffer, dataBuf, received);

                //Extract the text from the buffer
                string text = Encoding.ASCII.GetString(dataBuf);

                if (!string.IsNullOrEmpty(text))
                {
                    Console.WriteLine(text);

                    //Convert the JSON into a procedure call
                    ProcedureCall procedureCall = JsonConvert.DeserializeObject<ProcedureCall>(text);

                    if (procedureCall == null) { return; }

                    //Associate the socket with the procedure call
                    procedureCall.ClientSocket = socket;

                    //Handle the procedure call
                    ProcedureResult procedureResult = this.appServer.HandleProcedureCall(procedureCall);

                    //Serialize the result to json
                    string response = JsonConvert.SerializeObject(procedureResult) + "\n";

                    Console.WriteLine(response);

                    //Convert the response json to byte array
                    byte[] data = Encoding.ASCII.GetBytes(response);

                    //Set the data to the client
                    socket.BeginSend(data, 0, data.Length, SocketFlags.None, new AsyncCallback(SendCallback), socket);
                }
            }
            catch (Exception err)
            {
                logger.Error(err.Message);
            }
            finally
            {
                if (socket != null && received > 0)
                {
                    //Start receiving more data from client
                    try
                    {
                        socket.BeginReceive(buffer, 0, BUFFER_SIZE, SocketFlags.None, new AsyncCallback(ReceiveCallback), socket);
                    }
                    catch (Exception err)
                    {
                        logger.Error(err.Message);
                    }
                }
            }
        }

        private static void SendCallback(IAsyncResult iar)
        {
            Socket socket = (Socket)iar.AsyncState;
            socket.EndSend(iar);
        }

        public bool SendMessage(Message message)
        {
            try
            {
                //Get recipient from message
                string uid = message.RecipientUID;

                //Try to find socket for recipient
                Socket clientSocket = null;

                if (Clients.TryGetValue(uid, out clientSocket))
                {
                    logger.Info("Sending message to '{0}'.", uid);

                    //Client socket found, convert message to parsable format
                    string messageStr = JsonConvert.SerializeObject(message) + "\n";
                    byte[] data = Encoding.ASCII.GetBytes(messageStr);

                    //Set the data to the client
                    clientSocket.BeginSend(data, 0, data.Length, SocketFlags.None, new AsyncCallback(SendCallback), clientSocket);
                }
                else
                {
                    //Client socket not found
                    logger.Info("User '{0}' not connected.", uid);
                }
            }
            catch (Exception err)
            {
                logger.Error(err);

                return false;
            }

            return true;
        }
    }
}
