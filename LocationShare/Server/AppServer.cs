using DataModel;
using DBInterface;
using NLog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server
{
    public class AppServer
    {
        private static Logger logger = LogManager.GetCurrentClassLogger();

        private TCPServer tcpServer;

        public AppServer(TCPServer tcpServer)
        {
            this.tcpServer = tcpServer;
        }

        public ProcedureResult HandleProcedureCall(ProcedureCall procedureCall)
        {
            switch (procedureCall.Command.ToUpper())
            {
                case "LOGIN":
                    return HandleLogin(procedureCall);
                case "SEND_MESSAGE":
                    return HandleSendMessage(procedureCall);
                case "GET_UNREAD_MESSAGES":
                    return HandleGetUnreadMessages(procedureCall);
                case "MARK_AS_READ":
                    return HandleMarkAsRead(procedureCall);
                case "DELETE_MESSAGE":
                    return HandleDeleteMessage(procedureCall);
                case "DELETE_ALL_MESSAGES":
                    return HandleDeleteAllMessages(procedureCall);
            }

            return new ProcedureResult() { ResultCode = "ERROR" };
        }

        private ProcedureResult HandleLogin(ProcedureCall procedureCall)
        {
            logger.Info("Received LOGIN request");

            ProcedureResult procedureResult = new ProcedureResult();

            try
            {
                //Get the user's UID from the request
                string uid = procedureCall.Parameters["UID"];

                //Add / update the client's socket in the dictionary
                tcpServer.Clients.AddOrUpdate(uid, procedureCall.ClientSocket, (key, oldValue) => procedureCall.ClientSocket);

                //Set result code to OK
                procedureResult.ResultCode = "OK";
            }
            catch (Exception err)
            {
                logger.Error(err);

                procedureResult.ResultCode = "ERROR";
            }

            return procedureResult;
        }

        private ProcedureResult HandleSendMessage(ProcedureCall procedureCall)
        {
            logger.Info("Received SEND_MESSAGE request");

            ProcedureResult procedureResult = new ProcedureResult();

            try
            {
                //Get the sender's UID from the request
                string uid = procedureCall.Parameters["UID"];

                //Get the recipients
                string[] recipients = procedureCall.Parameters["Recipients"].Split(',');

                //Get the message's content
                string content = procedureCall.Parameters["Content"];

                //Get the longitude
                float longitude = float.Parse(procedureCall.Parameters["Longitude"]);

                //Get the latitude
                float latitude = float.Parse(procedureCall.Parameters["Latitude"]);

                //Send a message to each of the recipients
                foreach (string recipient in recipients)
                {
                    Message message = new Message()
                    {
                        SenderUID = uid,
                        RecipientUID = recipient,
                        Content = content,
                        Location = new Location() { Longitude = longitude, Latitude = latitude },
                        SendDT = DateTime.Now
                    };

                    //Store message in database
                    MessageDB.Add(message);

                    //Set the message
                    this.tcpServer.SendMessage(message);
                }

                //Set result code to OK
                procedureResult.ResultCode = "OK";
            }
            catch (Exception err)
            {
                logger.Error(err);

                procedureResult.ResultCode = "ERROR";
            }

            return procedureResult;
        }

        private ProcedureResult HandleGetUnreadMessages(ProcedureCall procedureCall)
        {
            logger.Info("Received GET_UNREAD_MESSAGES request");

            ProcedureResult procedureResult = new ProcedureResult();

            try
            {
                //Get the user's UID from the request
                string uid = procedureCall.Parameters["UID"];

                //Get unread messages from database
                List<Message> messages = MessageDB.GetUnreadMessages(uid);

                //Add messages to result
                procedureResult.Results.Add("Messages", messages);

                //Set result code to OK
                procedureResult.ResultCode = "OK";
            }
            catch (Exception err)
            {
                logger.Error(err);

                procedureResult.ResultCode = "ERROR";
            }

            return procedureResult;
        }

        private ProcedureResult HandleMarkAsRead(ProcedureCall procedureCall)
        {
            logger.Info("Received MARK_AS_READ request");

            ProcedureResult procedureResult = new ProcedureResult();

            try
            {
                //Get the message ID from the request
                int id = Convert.ToInt32(procedureCall.Parameters["ID"]);

                //Delete the specified message
                if (MessageDB.MarkAsRead(id))
                {
                    procedureResult.ResultCode = "OK";
                }
                else
                {
                    procedureResult.ResultCode = "ERROR";
                }
            }
            catch (Exception err)
            {
                logger.Error(err);

                procedureResult.ResultCode = "ERROR";
            }

            return procedureResult;
        }

        private ProcedureResult HandleDeleteMessage(ProcedureCall procedureCall)
        {
            logger.Info("Received DELETE_MESSAGE request");

            ProcedureResult procedureResult = new ProcedureResult();

            try
            {
                //Get the message ID from the request
                int id = Convert.ToInt32(procedureCall.Parameters["ID"]);

                //Delete the specified message
                if (MessageDB.Delete(id))
                {
                    procedureResult.ResultCode = "OK";
                }
                else
                {
                    procedureResult.ResultCode = "ERROR";
                }
            }
            catch (Exception err)
            {
                logger.Error(err);

                procedureResult.ResultCode = "ERROR";
            }

            return procedureResult;
        }

        private ProcedureResult HandleDeleteAllMessages(ProcedureCall procedureCall)
        {
            logger.Info("Received DELETE_ALL_MESSAGES request");

            ProcedureResult procedureResult = new ProcedureResult();

            try
            {
                //Get the user's UID from the request
                string uid = procedureCall.Parameters["UID"];

                //Delete all messages for the specified user
                if (MessageDB.DeleteAll(uid))
                {
                    procedureResult.ResultCode = "OK";
                }
                else
                {
                    procedureResult.ResultCode = "ERROR";
                }
            }
            catch (Exception err)
            {
                logger.Error(err);

                procedureResult.ResultCode = "ERROR";
            }

            return procedureResult;
        }
    }
}
