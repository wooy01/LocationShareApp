using DataModel;
using NLog;
using System;
using System.Collections.Generic;
using System.Data.SQLite;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DBInterface
{
    public class MessageDB : IDBInterface
    {
        private static Logger logger = LogManager.GetCurrentClassLogger();

        public static bool Add(Message message)
        {
            //Insert message into database
            try
            {
                logger.Info("Inserting message into database.");

                using (SQLiteConnection connection = GetConnection())
                {
                    string sql = @"INSERT INTO Messages(SenderUID, RecipientUID, Content, Longitude, Latitude, SendDT)
                                   VALUES(@SenderUID, @RecipientUID, @Content, @Longitude, @Latitude, @SendDT)";

                    using (SQLiteCommand command = new SQLiteCommand(sql, connection))
                    {
                        command.Parameters.AddWithValue("@SenderUID", message.SenderUID);
                        command.Parameters.AddWithValue("@RecipientUID", message.RecipientUID);
                        command.Parameters.AddWithValue("@Content", message.Content);
                        command.Parameters.AddWithValue("@Longitude", message.Location.Longitude);
                        command.Parameters.AddWithValue("@Latitude", message.Location.Latitude);
                        command.Parameters.AddWithValue("@SendDT", message.SendDT);

                        command.ExecuteNonQuery();
                    }

                    //Get the ID used and update the message
                    sql = "SELECT last_insert_rowid()";
                    using (SQLiteCommand command = new SQLiteCommand(sql, connection))
                    {
                        message.ID = Convert.ToInt32(command.ExecuteScalar());
                    }
                }

                logger.Info("Successfully inserted message into database.");
            }
            catch (Exception err)
            {
                logger.Error(err.Message);

                return false;
            }

            return true;
        }

        public static List<Message> GetUnreadMessages(string recipientUID)
        {
            List<Message> messages = new List<Message>();

            //Get unread messages for specified user from database
            try
            {
                using (SQLiteConnection connection = GetConnection())
                {
                    string sql = @"SELECT * FROM Messages WHERE ReceiveDT IS NULL AND RecipientUID = @RecipientUID";

                    using (SQLiteCommand command = new SQLiteCommand(sql, connection))
                    {
                        command.Parameters.AddWithValue("@RecipientUID", recipientUID);

                        using (SQLiteDataReader reader = command.ExecuteReader())
                        {
                            while (reader.Read())
                            {
                                Message message = new Message()
                                {
                                    ID = reader.GetInt32(0),
                                    SenderUID = reader.GetString(1),
                                    RecipientUID = reader.GetString(2),
                                    Content = reader.GetString(3),
                                    Location = new Location()
                                    {
                                        Longitude = reader.GetFloat(4),
                                        Latitude = reader.GetFloat(5)
                                    },
                                    SendDT = reader.GetDateTime(6)
                                };

                                messages.Add(message);
                            }
                        }
                    }
                }
            }
            catch (Exception err)
            {
                logger.Error(err.Message);
            }

            return messages;
        }

        public static bool MarkAsRead(int messageID)
        {
            //Update message record to show the received timestamp
            try
            {
                logger.Info("Marking message '{0}' as read.", messageID);

                using (SQLiteConnection connection = GetConnection())
                {
                    string sql = @"UPDATE Messages SET ReceiveDT = @ReceivedDT WHERE ID=@ID";

                    using (SQLiteCommand command = new SQLiteCommand(sql, connection))
                    {
                        command.Parameters.AddWithValue("@ReceivedDT", DateTime.Now);
                        command.Parameters.AddWithValue("@ID", messageID);

                        command.ExecuteNonQuery();
                    }
                }

                logger.Info("Successfully marked message '{0}' as read.", messageID);
            }
            catch (Exception err)
            {
                logger.Error(err.Message);

                return false;
            }

            return true;
        }

        public static bool Delete(int messageID)
        {
            //Delete message with matching ID
            try
            {
                using (SQLiteConnection connection = GetConnection())
                {
                    string sql = @"DELETE FROM Messages WHERE ID = @ID";

                    using (SQLiteCommand command = new SQLiteCommand(sql, connection))
                    {
                        command.Parameters.AddWithValue("@ID", messageID);

                        command.ExecuteNonQuery();
                    }
                }
            }
            catch (Exception err)
            {
                logger.Error(err.Message);

                return false;
            }

            return true;
        }

        public static bool DeleteAll(string recipientUID)
        {
            //Delete all messages sent to recipient
            try
            {
                using (SQLiteConnection connection = GetConnection())
                {
                    string sql = @"DELETE FROM Messages WHERE RecipientUID = @RecipientUID";

                    using (SQLiteCommand command = new SQLiteCommand(sql, connection))
                    {
                        command.Parameters.AddWithValue("@RecipientUID", recipientUID);

                        command.ExecuteNonQuery();
                    }
                }
            }
            catch (Exception err)
            {
                logger.Error(err.Message);

                return false;
            }

            return true;
        }
    }
}
