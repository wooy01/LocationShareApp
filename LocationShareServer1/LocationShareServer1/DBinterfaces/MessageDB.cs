using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using MySql.Data.MySqlClient;

namespace LocationShareServer.DBinterfaces
{
    public class MessageDB
    {
        private const string CONNECTION_STRING = @"Server=127.0.0.1;Database=LocationShare;Uid=root;Pwd=abcde";

        public static MySqlConnection GetConnection()
        {
            MySqlConnection connection = new MySqlConnection(CONNECTION_STRING);

            connection.Open();

            return connection;
        }

        public static bool InsertMessage(Message message)
        {
            try
            {
                using (MySqlConnection connection = GetConnection())
                {
                    using (MySqlCommand command = new MySqlCommand("INSERT INTO location.messages (FromName, ToName, Contents, Longitude, Latitude, New, TimeStamp) VALUES(@fromName, @toName, @contents, @longitude, @latitude, @isNew, @timeStamp);", connection))
                    {
                        command.Parameters.AddWithValue("@fromName", message.FromName);
                        command.Parameters.AddWithValue("@toName", message.ToName);
                        command.Parameters.AddWithValue("@contents", message.Contents);
                        command.Parameters.AddWithValue("@longitude", message.Longitude);
                        command.Parameters.AddWithValue("@latitude", message.Latitude);
                        command.Parameters.AddWithValue("@isNew", message.IsNew);
                        command.Parameters.AddWithValue("@timeStamp", message.TimeStamp);

                        command.ExecuteNonQuery();
                    }
                }
            }
            catch
            {
                return false;
            }

            return true;
        }

        public static List<Message> GetMessages(string userName)
        {
            List<Message> messages = new List<Message>();

            try
            {
                using (MySqlConnection connection = GetConnection())
                {
                    using (MySqlCommand command = new MySqlCommand("SELECT * FROM location.messages WHERE FromName = @userName OR ToName = @userName;", connection))
                    {
                        command.Parameters.AddWithValue("@userName", userName);

                        using (MySqlDataReader reader = command.ExecuteReader())
                        {
                            while (reader.Read())
                            {
                                Message message = new Message()
                                {
                                    MessageID = reader.GetInt32("idMessages"),
                                    FromName = reader.GetString("FromName"),
                                    ToName = reader.GetString("ToName"),
                                    Contents = reader.GetString("Contents"),
                                    Longitude = reader.GetFloat("Longitude"),
                                    Latitude = reader.GetFloat("Latitude"),
                                    IsNew = reader.GetBoolean("New"),
                                    TimeStamp = reader.GetDateTime("TimeStamp")
                                };

                                messages.Add(message);
                            }
                        }
                    }
                }
            }
            catch { }

            return messages;
        }

        public static bool DeleteMessage(int messageID)
        {
            try
            {
                using (MySqlConnection connection = GetConnection())
                {
                    using (MySqlCommand command = new MySqlCommand("DELETE FROM location.messages WHERE idMessages = @messageID", connection))
                    {
                        command.Parameters.AddWithValue("@messageID", messageID);

                        command.ExecuteNonQuery();
                    }
                }
            }
            catch
            {
                return false;
            }

            return true;
        }

        public static bool DeleteAllMessages(string userName)
        {
            try
            {
                using (MySqlConnection connection = GetConnection())
                {
                    using (MySqlCommand command = new MySqlCommand("DELETE FROM location.messages WHERE FromName = @userName OR ToName = @userName", connection))
                    {
                        command.Parameters.AddWithValue("@userName", userName);

                        command.ExecuteNonQuery();
                    }
                }
            }
            catch
            {
                return false;
            }

            return true;
        }
    }
}
