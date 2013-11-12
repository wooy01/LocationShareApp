package edu.ipfw.locationshare.serverinterface;

import edu.ipfw.locationshare.datamodel.Message;

abstract class MessageListener {
    public abstract void MessageReceived(Message message);
}
