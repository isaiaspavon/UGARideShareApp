package edu.uga.cs.ugarideshareapp.models;

public class Ride {
    private String id;
    private String fromLocation;
    private String toLocation;
    private String dateTime;
    private boolean isOffer; // true = offer, false = request
    private String driverUid; // Only applicable for offers
    private String riderUid; // Only applicable for requests
    private String userId; // User who created the ride (Offer or Request)
    private String status; // "available", "accepted", "completed"
    private boolean confirmDriver; // Used for confirmation by the driver
    private boolean confirmRider; // Used for confirmation by the rider

    // Required by Firebase
    public Ride() {}

    public Ride(String fromLocation, String toLocation, String dateTime, boolean isOffer, String userId) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.dateTime = dateTime;
        this.isOffer = isOffer;
        this.status = "available";
        this.confirmDriver = false;
        this.confirmRider = false;
        this.userId = userId; // The user who created the ride
        this.driverUid = isOffer ? userId : null; // Only the driver has this
        this.riderUid = isOffer ? null : userId; // Only the rider has this
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public boolean isOffer() { return isOffer; }
    public void setOffer(boolean offer) { this.isOffer = offer; }  // Added setter for Firebase compatibility

    public String getDriverUid() { return driverUid; }
    public void setDriverUid(String driverUid) { this.driverUid = driverUid; }

    public String getRiderUid() { return riderUid; }
    public void setRiderUid(String riderUid) { this.riderUid = riderUid; }

    public String getUserId() { return userId; } // User who created the ride
    public void setUserId(String userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isConfirmDriver() { return confirmDriver; }
    public void setConfirmDriver(boolean confirmDriver) { this.confirmDriver = confirmDriver; }

    public boolean isConfirmRider() { return confirmRider; }
    public void setConfirmRider(boolean confirmRider) { this.confirmRider = confirmRider; }
}
