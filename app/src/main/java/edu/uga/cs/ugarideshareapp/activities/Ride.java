package edu.uga.cs.ugarideshareapp.activities;

public class Ride {
    private String fromLocation;
    private String toLocation;
    private String dateTime;
    private boolean isOffer; // true = driver offering, false = rider requesting

    public Ride() {}

    public Ride(String fromLocation, String toLocation, String dateTime, boolean isOffer) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.dateTime = dateTime;
        this.isOffer = isOffer;
    }

    // getters and setters
    public String getFromLocation() { return fromLocation; }
    public String getToLocation() { return toLocation; }
    public String getDateTime() { return dateTime; }
    public boolean isOffer() { return isOffer; }

    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public void setIsOffer(boolean offer) { isOffer = offer; }
}
