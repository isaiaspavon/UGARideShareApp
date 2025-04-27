package edu.uga.cs.ugarideshareapp.models;

public class User {
    private String userId;
    private String email;
    private int points;

    // Required empty constructor for Firebase
    public User() {
    }

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
