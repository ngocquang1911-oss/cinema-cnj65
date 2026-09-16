package com.cnj65.cinema.model;

import java.time.LocalDateTime;

/**
 * Trang thai cua 1 ghe trong 1 suat chieu cu the.
 * Day la bang lam nen tang cho logic "chong trung ghe" va "giu ghe tam thoi".
 */
public class ShowtimeSeat {
    private int id;
    private int showtimeId;
    private int seatId;
    private String seatCode;   // JOIN tu bang seats
    private String seatType;   // JOIN tu bang seats
    private String status;     // AVAILABLE, LOCKED, BOOKED
    private Integer lockedBy;
    private LocalDateTime lockedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }
    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }
    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getLockedBy() { return lockedBy; }
    public void setLockedBy(Integer lockedBy) { this.lockedBy = lockedBy; }
    public LocalDateTime getLockedAt() { return lockedAt; }
    public void setLockedAt(LocalDateTime lockedAt) { this.lockedAt = lockedAt; }
}
