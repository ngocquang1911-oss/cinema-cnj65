package com.cnj65.cinema.model;

import java.math.BigDecimal;
import com.cnj65.cinema.util.FormatUtil;

public class TicketDetail {
    private int id;
    private int ticketId;
    private int seatId;
    private String seatCode;   // JOIN
    private BigDecimal price;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }
    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getPriceFormatted() { return FormatUtil.currency(price); }
}
