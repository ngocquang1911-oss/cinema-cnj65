package com.cnj65.cinema.model;

public class Seat {
    private int id;
    private int roomId;
    private String seatRow;
    private int seatNumber;
    private String seatCode;
    private String seatType;   // NORMAL, VIP, COUPLE

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getSeatRow() { return seatRow; }
    public void setSeatRow(String seatRow) { this.seatRow = seatRow; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
}
