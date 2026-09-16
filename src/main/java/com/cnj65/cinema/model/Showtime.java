package com.cnj65.cinema.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import com.cnj65.cinema.util.FormatUtil;

public class Showtime {
    private int id;
    private int movieId;
    private String movieTitle;   // JOIN
    private int roomId;
    private String roomName;     // JOIN
    private LocalDate showDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal priceNormal;
    private BigDecimal priceVip;
    private BigDecimal priceCouple;
    private String status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public LocalDate getShowDate() { return showDate; }
    public void setShowDate(LocalDate showDate) { this.showDate = showDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public BigDecimal getPriceNormal() { return priceNormal; }
    public void setPriceNormal(BigDecimal priceNormal) { this.priceNormal = priceNormal; }
    public BigDecimal getPriceVip() { return priceVip; }
    public void setPriceVip(BigDecimal priceVip) { this.priceVip = priceVip; }
    public BigDecimal getPriceCouple() { return priceCouple; }
    public void setPriceCouple(BigDecimal priceCouple) { this.priceCouple = priceCouple; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ==== Getter tien ich cho JSP (xem giai thich trong Movie.java) ====
    public String getShowDateFormatted() { return FormatUtil.date(showDate); }
    public String getStartTimeFormatted() { return FormatUtil.time(startTime); }
    public String getEndTimeFormatted() { return FormatUtil.time(endTime); }
    public String getPriceNormalFormatted() { return FormatUtil.currency(priceNormal); }
    public String getPriceVipFormatted() { return FormatUtil.currency(priceVip); }
    public String getPriceCoupleFormatted() { return FormatUtil.currency(priceCouple); }
}
