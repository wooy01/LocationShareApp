using DataModel;
using DBInterface;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server
{
    class Program
    {
        static void Main(string[] args)
        {
            TCPServer tcpServer = new TCPServer();

            Console.WriteLine("Server running.");

            Console.ReadKey();
        }
    }
}
