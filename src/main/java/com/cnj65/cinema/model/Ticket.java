package com.cnj65.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.cnj65.cinema.util.FormatUtil;

public class Ticket {
    private int id;
    private String ticketCode;
    private Integer userId;
    private String customerName;     // JOIN
    private int showtimeId;
    private String movieTitle;       // JOIN
    private String roomName;         // JOIN
    private java.time.LocalDate showDate;
    private java.time.LocalTime startTime;
    private Integer staffId;
    private String guestName;
    private String guestPhone;
    private BigDecimal totalAmount;
    private String status;           // PENDING, PAID, CANCELLED, CHECKED_IN
    private LocalDateTime createdAt;
    private LocalDateTime checkinAt;
    private List<TicketDetail> details;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public java.time.LocalDate getShowDate() { return showDate; }
    public void setShowDate(java.time.LocalDate showDate) { this.showDate = showDate; }
    public java.time.LocalTime getStartTime() { return startTime; }
    public void setStartTime(java.time.LocalTime startTime) { this.startTime = startTime; }
    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCheckinAt() { return checkinAt; }
    public void setCheckinAt(LocalDateTime checkinAt) { this.checkinAt = checkinAt; }
    public List<TicketDetail> getDetails() { return details; }
    public void setDetails(List<TicketDetail> details) { this.details = details; }

    // ==== Getter tien ich cho JSP (xem giai thich trong Movie.java) ====
    public String getShowDateFormatted() { return FormatUtil.date(showDate); }
    public String getStartTimeFormatted() { return FormatUtil.time(startTime); }
    public String getCreatedAtFormatted() { return FormatUtil.dateTime(createdAt); }
    public String getTotalAmountFormatted() { return FormatUtil.currency(totalAmount); }
}
