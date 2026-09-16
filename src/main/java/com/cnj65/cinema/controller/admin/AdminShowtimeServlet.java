package com.cnj65.cinema.controller.admin;

import com.cnj65.cinema.dao.MovieDAO;
import com.cnj65.cinema.dao.RoomDAO;
import com.cnj65.cinema.dao.ShowtimeDAO;
import com.cnj65.cinema.model.Showtime;
import com.cnj65.cinema.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/admin/showtimes")
public class AdminShowtimeServlet extends HttpServlet {

    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private final MovieDAO movieDAO = new MovieDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("form".equals(action)) {
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                req.setAttribute("showtime", showtimeDAO.findById(Integer.parseInt(idParam)));
            }
            req.setAttribute("movies", movieDAO.search(null, null, null));
            req.setAttribute("rooms", roomDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/showtime-form.jsp").forward(req, resp);
            return;
        }

        String dateParam = req.getParameter("date");
        LocalDate date = ValidationUtil.isBlank(dateParam) ? null : LocalDate.parse(dateParam);
        req.setAttribute("showtimes", showtimeDAO.findAll(date));
        req.setAttribute("selectedDate", dateParam);
        req.getRequestDispatcher("/WEB-INF/views/admin/showtime-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("cancel".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            if (showtimeDAO.hasSoldTickets(id)) {
                resp.sendRedirect(req.getContextPath() + "/admin/showtimes?error=has_sold_tickets");
                return;
            }
            showtimeDAO.cancel(id);
            resp.sendRedirect(req.getContextPath() + "/admin/showtimes");
            return;
        }

        String idParam = req.getParameter("id");
        int movieId = Integer.parseInt(req.getParameter("movieId"));
        int roomId = Integer.parseInt(req.getParameter("roomId"));
        LocalTime startTime = LocalTime.parse(req.getParameter("startTime"));
        LocalTime endTime = LocalTime.parse(req.getParameter("endTime"));
        BigDecimal priceNormal = new BigDecimal(req.getParameter("priceNormal"));
        BigDecimal priceVip = new BigDecimal(req.getParameter("priceVip"));
        BigDecimal priceCouple = new BigDecimal(req.getParameter("priceCouple"));

        Integer excludeId = (idParam != null && !idParam.isEmpty()) ? Integer.parseInt(idParam) : null;
        String mode = req.getParameter("mode"); // "single" hoac "range" — chi co y nghia khi TAO MOI

        if (!endTime.isAfter(startTime)) {
            forwardFormWithError(req, resp, "Giờ kết thúc phải sau giờ bắt đầu.");
            return;
        }

        // ===== SUA suat chieu da co: luon la 1 ngay duy nhat =====
        if (excludeId != null) {
            LocalDate showDate = LocalDate.parse(req.getParameter("showDate"));
            if (showtimeDAO.hasConflict(roomId, showDate, startTime, endTime, excludeId)) {
                forwardFormWithError(req, resp, "Phòng chiếu đã có suất chiếu khác trùng khung giờ này.");
                return;
            }
            Showtime st = buildShowtime(movieId, roomId, showDate, startTime, endTime, priceNormal, priceVip,
                    priceCouple);
            st.setId(excludeId);
            showtimeDAO.update(st);
            resp.sendRedirect(req.getContextPath() + "/admin/showtimes");
            return;
        }

        // ===== TAO MOI, che do 1 ngay (mac dinh) =====
        if (!"range".equals(mode)) {
            LocalDate showDate = LocalDate.parse(req.getParameter("showDate"));
            if (showtimeDAO.hasConflict(roomId, showDate, startTime, endTime, null)) {
                forwardFormWithError(req, resp,
                        "Phòng chiếu đã có suất chiếu khác trùng khung giờ này. Vui lòng chọn giờ hoặc phòng khác.");
                return;
            }
            Showtime st = buildShowtime(movieId, roomId, showDate, startTime, endTime, priceNormal, priceVip,
                    priceCouple);
            showtimeDAO.insert(st);
            resp.sendRedirect(req.getContextPath() + "/admin/showtimes");
            return;
        }

        // ===== TAO MOI, che do NHIEU NGAY (lap lai theo khoang ngay) =====
        LocalDate rangeStart = LocalDate.parse(req.getParameter("rangeStart"));
        LocalDate rangeEnd = LocalDate.parse(req.getParameter("rangeEnd"));

        if (rangeEnd.isBefore(rangeStart)) {
            forwardFormWithError(req, resp, "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
            return;
        }
        if (rangeStart.plusDays(62).isBefore(rangeEnd)) {
            forwardFormWithError(req, resp, "Khoảng ngày quá dài (tối đa 62 ngày cho 1 lần tạo hàng loạt).");
            return;
        }

        String[] weekdayParams = req.getParameterValues("weekday");
        Set<Integer> allowedWeekdays = new HashSet<>();
        if (weekdayParams != null)
            allowedWeekdays.addAll(Arrays.asList(toIntArray(weekdayParams)));

        int createdCount = 0;
        int skippedCount = 0;

        for (LocalDate d = rangeStart; !d.isAfter(rangeEnd); d = d.plusDays(1)) {
            if (!allowedWeekdays.isEmpty() && !allowedWeekdays.contains(d.getDayOfWeek().getValue())) {
                continue;
            }
            if (showtimeDAO.hasConflict(roomId, d, startTime, endTime, null)) {
                skippedCount++;
                continue;
            }
            Showtime st = buildShowtime(movieId, roomId, d, startTime, endTime, priceNormal, priceVip, priceCouple);
            showtimeDAO.insert(st);
            createdCount++;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/showtimes?success=batch&created=" + createdCount
                + "&skipped=" + skippedCount);
    }

    private Integer[] toIntArray(String[] strs) {
        Integer[] result = new Integer[strs.length];
        for (int i = 0; i < strs.length; i++)
            result[i] = Integer.parseInt(strs[i]);
        return result;
    }

    private Showtime buildShowtime(int movieId, int roomId, LocalDate date, LocalTime start, LocalTime end,
            BigDecimal priceNormal, BigDecimal priceVip, BigDecimal priceCouple) {
        Showtime st = new Showtime();
        st.setMovieId(movieId);
        st.setRoomId(roomId);
        st.setShowDate(date);
        st.setStartTime(start);
        st.setEndTime(end);
        st.setPriceNormal(priceNormal);
        st.setPriceVip(priceVip);
        st.setPriceCouple(priceCouple);
        return st;
    }

    private void forwardFormWithError(HttpServletRequest req, HttpServletResponse resp, String error)
            throws ServletException, IOException {
        req.setAttribute("error", error);
        req.setAttribute("movies", movieDAO.search(null, null, null));
        req.setAttribute("rooms", roomDAO.findAll());
        req.getRequestDispatcher("/WEB-INF/views/admin/showtime-form.jsp").forward(req, resp);
    }
}