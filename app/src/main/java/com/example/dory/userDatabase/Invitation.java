package com.example.dory.userDatabase;

public class Invitation {
    public int invitationID;
    public int eventID;
    public int attendeeID;
    public String status;
    public String creationTime;
    public String responseTime;

    public Invitation(int invitationID, int eventID, int attendeeID, String status, String creationTime, String responseTime) {
        this.invitationID = invitationID;
        this.eventID = eventID;
        this.attendeeID = attendeeID;
        this.status = status;
        this.creationTime = creationTime;
        this.responseTime = responseTime;
    }

    public int getInvitationID() {
        return invitationID;
    }

    public void setInvitationID(int invitationID) {
        this.invitationID = invitationID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getAttendeeID() {
        return attendeeID;
    }

    public void setAttendeeID(int attendeeID) {
        this.attendeeID = attendeeID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }
}
