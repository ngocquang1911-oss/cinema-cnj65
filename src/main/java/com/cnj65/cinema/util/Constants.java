package com.cnj65.cinema.util;

/**
 * GOM TOAN BO HANG SO DUNG CHUNG TOAN HE THONG vao 1 noi duy nhat.
 * Moi Servlet/Filter/JSP/DAO deu phai tham chieu qua day thay vi go tay
 * chuoi ky tu ("user", "PAID", "ADMIN"...) de tranh loi chinh ta ram tro
 * (vi du go "Paid" thay vi "PAID" se khien so sanh sai ma khong bao loi bien dich).
 */
public class Constants {

    private Constants() {}

    // ==== SESSION ATTRIBUTE KEYS ====
    public static final String SESSION_USER = "user";
    public static final String SESSION_REDIRECT_AFTER_LOGIN = "redirectAfterLogin";

    // ==== USER ROLES ====
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_STAFF = "STAFF";
    public static final String ROLE_ADMIN = "ADMIN";

    // ==== USER STATUS ====
    public static final String USER_ACTIVE = "ACTIVE";
    public static final String USER_LOCKED = "LOCKED";

    // ==== MOVIE STATUS ====
    public static final String MOVIE_NOW_SHOWING = "NOW_SHOWING";
    public static final String MOVIE_COMING_SOON = "COMING_SOON";
    public static final String MOVIE_ENDED = "ENDED";

    // ==== ROOM STATUS ====
    public static final String ROOM_ACTIVE = "ACTIVE";
    public static final String ROOM_MAINTENANCE = "MAINTENANCE";

    // ==== SEAT TYPE ====
    public static final String SEAT_NORMAL = "NORMAL";
    public static final String SEAT_VIP = "VIP";
    public static final String SEAT_COUPLE = "COUPLE";

    // ==== SHOWTIME_SEAT STATUS (dung cho co che chong trung ghe) ====
    public static final String SEAT_AVAILABLE = "AVAILABLE";
    public static final String SEAT_LOCKED = "LOCKED";
    public static final String SEAT_BOOKED = "BOOKED";

    // ==== SHOWTIME STATUS ====
    public static final String SHOWTIME_ACTIVE = "ACTIVE";
    public static final String SHOWTIME_CANCELLED = "CANCELLED";

    // ==== TICKET STATUS ====
    public static final String TICKET_PENDING = "PENDING";
    public static final String TICKET_PAID = "PAID";
    public static final String TICKET_CANCELLED = "CANCELLED";
    public static final String TICKET_CHECKED_IN = "CHECKED_IN";

    // ==== PAYMENT METHOD ====
    public static final String PAY_CASH = "CASH";
    public static final String PAY_CARD = "CARD";
    public static final String PAY_MOMO = "MOMO";
    public static final String PAY_VNPAY = "VNPAY";
    public static final String PAY_ZALOPAY = "ZALOPAY";

    // ==== CAU HINH NGHIEP VU ====
    /** So phut toi da duoc giu ghe truoc khi he thong tu dong nha ra */
    public static final int SEAT_LOCK_TIMEOUT_MINUTES = 5;
    /** So gio toi thieu truoc gio chieu de duoc phep huy ve */
    public static final int CANCEL_TICKET_MIN_HOURS_BEFORE = 2;
}
