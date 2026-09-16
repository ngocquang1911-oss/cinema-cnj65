package com.cnj65.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private int id;
    private int ticketId;
    private BigDecimal amount;
    private String method;    // CASH, CARD, MOMO, VNPAY, ZALOPAY
    private String status;    // SUCCESS, FAILED
    private LocalDateTime paidAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
