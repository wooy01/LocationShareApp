using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;

namespace Server
{
    class Program
    {
        private static byte[] Buffer = new byte[1024];
        //private static List<Socket> ClientSockets = new List<Socket>();
        private static Socket ServerSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);


        static void Main(string[] args)
        {
            Console.Title = "Server";
            SetupServer();
            Console.ReadLine();
            /*
            Friend friend = new Friend();
            friend.User_Name = "welctb01";
            Console.WriteLine("User Name: {0}", friend.User_Name);


            //Friend friend2 = new Friend("welctb02", "", "");
            var friend2 = new Friend { User_Name = "welctb02" };
            Console.WriteLine("User Name: " + friend2.User_Name);
            */
        }


        private static void SetupServer() 
        {
            Console.WriteLine("Setting up server...");
            ServerSocket.Bind(new IPEndPoint(IPAddress.Any, 9876));
            ServerSocket.Listen(5);
            ServerSocket.BeginAccept(new AsyncCallback(AcceptCallback), null);
        }


        private static void AcceptCallback(IAsyncResult AR)
        {
            Socket socket = ServerSocket.EndAccept(AR);
            //ClientSockets.Add(socket);
            Console.WriteLine("Client Connected...");
            socket.BeginReceive(Buffer, 0, Buffer.Length, SocketFlags.None, new AsyncCallback(ReceiveCallback), socket);
            ServerSocket.BeginAccept(new AsyncCallback(AcceptCallback), null);
        }

        private static void ReceiveCallback(IAsyncResult AR)
        {
            try
            {
                Socket socket = (Socket)AR.AsyncState;
                int received = socket.EndReceive(AR);
                byte[] dataBuf = new byte[received];
                Array.Copy(Buffer, dataBuf, received);

                string text = Encoding.ASCII.GetString(dataBuf);
                Console.WriteLine("Text received: " + text);

                string response = string.Empty;

                if (text.ToLower() == "get time")
                {
                    response = DateTime.Now.ToLongTimeString();
                }
                else
                {
                    response = "Invalid Request";
                }

                byte[] data = Encoding.ASCII.GetBytes(response);
                socket.BeginSend(data, 0, data.Length, SocketFlags.None, new AsyncCallback(SendCallback), socket);
                socket.BeginReceive(Buffer, 0, Buffer.Length, SocketFlags.None, new AsyncCallback(ReceiveCallback), socket);

            }
            catch (Exception)
            {
                Console.WriteLine("Connection Lost");
            }
        }


        private static void SendCallback(IAsyncResult AR) 
        {
            Socket socket = (Socket)AR.AsyncState;
            socket.EndSend(AR);
        }
    }
}
