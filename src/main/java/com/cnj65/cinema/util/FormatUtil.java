package com.cnj65.cinema.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * NOI DUY NHAT dinh nghia dinh dang ngay/gio/tien te cho TOAN HE THONG.
 * Moi Model co truong ngay/gio/tien can hien thi tren JSP deu goi qua day
 * (thong qua getter *Formatted()) thay vi tu viet DateTimeFormatter rieng
 * o tung noi — dam bao doi dinh dang o 1 cho la doi dong loat toan web.
 *
 * LY DO CAN FILE NAY: JSTL <fmt:formatDate> CHI ho tro java.util.Date,
 * khong dung duoc voi LocalDate/LocalTime/LocalDateTime (Java 8 time API)
 * ma du an nay dung xuyen suot -> phai tu format trong Java roi tra ve
 * String cho JSP hien thi truc tiep.
 */
public class FormatUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    private static final Locale VN = new Locale("vi", "VN");

    private FormatUtil() {}

    public static String date(LocalDate d) {
        return d == null ? "" : d.format(DATE_FMT);
    }

    public static String time(LocalTime t) {
        return t == null ? "" : t.format(TIME_FMT);
    }

    public static String dateTime(LocalDateTime dt) {
        return dt == null ? "" : dt.format(DATETIME_FMT);
    }

    public static String currency(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return NumberFormat.getInstance(VN).format(amount) + " đ";
    }
}
