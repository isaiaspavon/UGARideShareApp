package edu.uga.cs.ugarideshareapp.models;

/**
 * User represents a registered user of the UGA Ride Share App.
 * Each user has a unique ID, an email address, and a points balance
 * used to manage ride credits.
 */
public class User {
    private String userId;
    private String email;
    private int points;

    // Required empty constructor for Firebase
    public User() {
    }

    /**
     * Constructs a User with the given ID, email, and points.
     *
     * @param userId unique Firebase user ID
     * @param email  email address of the user
     * @param points initial ride points balance
     */
    public User(String userId, String email, int points) {
        this.userId = userId;
        this.email = email;
        this.points = points;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
