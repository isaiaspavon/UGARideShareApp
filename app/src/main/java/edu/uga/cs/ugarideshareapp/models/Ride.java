package edu.uga.cs.ugarideshareapp.models;

/**
 * Ride represents a ride offer or request in the UGA Ride Share App.
 * It stores information about the ride such as origin, destination, time,
 * whether it is a ride offer or request, the creator, participants, and status.
 */
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

    /**
     * Empty constructor required by Firebase.
     */
    public Ride() {}

    /**
     * Constructs a Ride instance with the given parameters.
     *
     * @param fromLocation starting location of the ride
     * @param toLocation   destination location of the ride
     * @param dateTime     date and time of the ride
     * @param isOffer      true if it's a ride offer; false if it's a request
     * @param userId       UID of the user creating the ride
     */
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

    /** Ride origin */
    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    /** Ride destination */
    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }

    /** Date and time of the ride */
    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    /** True if ride is an offer; false if it's a request */
    public boolean isOffer() { return isOffer; }
    public void setOffer(boolean offer) { this.isOffer = offer; }  // Added setter for Firebase compatibility

    /** UID of the driver */
    public String getDriverUid() { return driverUid; }
    public void setDriverUid(String driverUid) { this.driverUid = driverUid; }

    /** UID of the rider */
    public String getRiderUid() { return riderUid; }
    public void setRiderUid(String riderUid) { this.riderUid = riderUid; }

    /** UID of the user who created the ride */
    public String getUserId() { return userId; } // User who created the ride
    public void setUserId(String userId) { this.userId = userId; }

    /** Status of the ride: "available", "accepted", or "completed" */
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /** Whether the driver has confirmed the ride */
    public boolean isConfirmDriver() { return confirmDriver; }
    public void setConfirmDriver(boolean confirmDriver) { this.confirmDriver = confirmDriver; }

    /** Whether the rider has confirmed the ride */
    public boolean isConfirmRider() { return confirmRider; }
    public void setConfirmRider(boolean confirmRider) { this.confirmRider = confirmRider; }
}
