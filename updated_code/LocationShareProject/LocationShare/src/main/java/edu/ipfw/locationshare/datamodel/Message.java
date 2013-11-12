package edu.ipfw.locationshare.datamodel;

import java.util.Date;

public class Message {
    private int ID;
    private String SenderUID;
    private String RecipientUID;
    private String Content;
    private Location Location;
    private Date SendDT;
    private Date ReceiveDT;

    public Message(){

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSenderUID() {
        return SenderUID;
    }

    public void setSenderUID(String senderUID) {
        SenderUID = senderUID;
    }

    public String getRecipientUID() {
        return RecipientUID;
    }

    public void setRecipientUID(String recipientUID) {
        RecipientUID = recipientUID;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Location getLocation() {
        return Location;
    }

    public void setLocation(Location location) {
        Location = location;
    }

    public Date getSendDT() {
        return SendDT;
    }

    public void setSendDT(Date sendDT) {
        SendDT = sendDT;
    }

    public Date getReceiveDT() {
        return ReceiveDT;
    }

    public void setReceiveDT(Date receiveDT) {
        ReceiveDT = receiveDT;
    }
}
