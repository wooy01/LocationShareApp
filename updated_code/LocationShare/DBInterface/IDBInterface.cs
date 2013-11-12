using NLog;
using System;
using System.Collections.Generic;
using System.Data.SQLite;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DBInterface
{
    public class IDBInterface
    {
        private static Logger logger = LogManager.GetCurrentClassLogger();

        private const string DB_FILE_NAME = "dbfile.db3";
        private const string CONNECTION_STRING = "Data Source=dbfile.db3";

        static IDBInterface()
        {
            //Verify database exists
            if (File.Exists(DB_FILE_NAME))
            {
                logger.Info("DB file exists, skipping setup.");
                return;
            }

            //Create database file
            try
            {
                logger.Info("DB file does not exist, setting up now.");
                SQLiteConnection.CreateFile(DB_FILE_NAME);
            }
            catch(Exception err)
            {
                logger.Error(err.Message);
                return;
            }

            //Create database tables
            try
            {
                using (SQLiteConnection connection = GetConnection())
                {
                    //Create messages table 
                    logger.Info("Creating Messages table.");
                    string sql = @"CREATE TABLE Messages (
                               ID            integer PRIMARY KEY AUTOINCREMENT NOT NULL,
                               SenderUID     varchar(50) NOT NULL,
                               RecipientUID  varchar(50) NOT NULL,
                               Content        varchar(50) NOT NULL,
                               Longitude     float(10,6) NOT NULL,
                               Latitude      float(10,6) NOT NULL,
                               SendDT        datetime NOT NULL,
                               ReceiveDT     datetime);";

                    using (SQLiteCommand command = new SQLiteCommand(sql, connection))
                    {
                        command.ExecuteNonQuery();
                    }

                    //Create index on ID field
                    logger.Info("Creating indices on Messages table.");
                    sql = @"CREATE INDEX Messages_ID_IDX ON Messages(ID);";
                    using (SQLiteCommand command = new SQLiteCommand(sql, connection))
                    {
                        command.ExecuteNonQuery();
                    }

                    //Create index on SenderUID field
                    sql = @"CREATE INDEX Messages_SenderUID_IDX ON Messages(SenderUID);";
                    using (SQLiteCommand command = new SQLiteCommand(sql, connection))
                    {
                        command.ExecuteNonQuery();
                    }

                    //Create index on Recipient field
                    sql = @"CREATE INDEX Messages_RecipientUID_IDX ON Messages(RecipientUID);";
                    using (SQLiteCommand command = new SQLiteCommand(sql, connection))
                    {
                        command.ExecuteNonQuery();
                    }
                }
            }
            catch (Exception err)
            {
                logger.Error(err.Message);
                return;
            }
        }

        public static SQLiteConnection GetConnection()
        {
            try
            {
                SQLiteConnection connection = new SQLiteConnection(CONNECTION_STRING);
                connection.Open();

                return connection;
            }
            catch(Exception err)
            {
                logger.Error(err.Message);

                return null;
            }
        }
    }
}
