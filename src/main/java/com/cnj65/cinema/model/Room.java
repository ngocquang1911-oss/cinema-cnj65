package com.cnj65.cinema.model;

public class Room {
    private int id;
    private String name;
    private String roomType;   // 2D, 3D, IMAX
    private int totalSeats;
    private String status;     // ACTIVE, MAINTENANCE

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
