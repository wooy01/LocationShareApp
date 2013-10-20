using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

//Using Mysql Namespace
using MySql.Data.MySqlClient;
using LocationShareServer.DBinterfaces;

namespace LocationShareServer
{
    public class Program
    {
        private static List<string> names = new List<string>();
        private static Random random = new Random();

        static void Main(String[] args)
        {
            names.Add("Sun");
            names.Add("Tree");
            names.Add("Moon");
            names.Add("Hi");
            names.Add("Bye");
            names.Add("Wind");

            for (int i = 0; i < 1000; i++)
            {
                if (i % 100 == 0 && i != 0)
                {
                    Console.WriteLine("Message #{0}", i);
                }

                string from = GetName();
                string to = GetName(from);
                Message message = new Message()
                {
                    FromName = from,
                    ToName = to,
                    Contents = "Random text here alhfjlsjdlfjsldj",
                    Latitude = (float)random.NextDouble() * 100,
                    Longitude = (float)random.NextDouble() * 100,
                    IsNew = true,
                    TimeStamp = DateTime.Now
                };

                bool result = MessageDB.InsertMessage(message);

                if (!result)
                {
                    Console.WriteLine("Failed to insert message!");
                }
            }

           
            string randomName = GetName();

            Console.WriteLine("Getting messages for {0}", randomName);
            foreach (Message message in MessageDB.GetMessages(randomName))
            {
                Console.WriteLine("Message for {0}, id: {1}", randomName, message.MessageID);

                MessageDB.DeleteMessage(message.MessageID);
            }

            Console.ReadKey();
            
            return;
           
        }

        public static string GetName(string name = null)
        {
            string result = null;

            while (result == null)
            {
                result = names[random.Next(names.Count)];

                if (name != null && result == name)
                {
                    result = null;
                }
            }

            return result;
        }
    }
}