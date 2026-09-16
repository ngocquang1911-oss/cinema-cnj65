package com.cnj65.cinema.controller.customer;

import com.cnj65.cinema.util.Constants;

import com.cnj65.cinema.dao.ShowtimeSeatDAO;
import com.cnj65.cinema.model.ShowtimeSeat;
import com.cnj65.cinema.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AJAX Servlet: xu ly GIU GHE / NHA GHE khi khach dang chon ghe tren so do.
 * Day la trai tim cua LOGIC 2 (chong trung ghe) trong ban nghiep vu goc.
 *
 * POST action=lock   -> khach chon 1 ghe
 * POST action=unlock -> khach bo chon 1 ghe
 * GET  (khong action) -> polling lay trang thai moi nhat cua ca so do (de cap nhat
 *                          real-time khi nguoi khac vua dat mat 1 ghe nao do)
 */
@WebServlet("/booking/seat-lock")
public class SeatLockServlet extends HttpServlet {

    private final ShowtimeSeatDAO showtimeSeatDAO = new ShowtimeSeatDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_USER);

        JsonObject result = new JsonObject();

        if (user == null) {
            result.addProperty("success", false);
            result.addProperty("message", "Ban can dang nhap de chon ghe.");
            writeJson(resp, result);
            return;
        }

        try {
            int showtimeId = Integer.parseInt(req.getParameter("showtimeId"));
            int seatId = Integer.parseInt(req.getParameter("seatId"));
            String action = req.getParameter("action");

            boolean ok;
            if ("unlock".equals(action)) {
                ok = showtimeSeatDAO.unlockSeat(showtimeId, seatId, user.getId());
                result.addProperty("success", ok);
                result.addProperty("message", ok ? "Da bo chon ghe." : "Khong the bo chon ghe nay.");
            } else {
                ok = showtimeSeatDAO.lockSeat(showtimeId, seatId, user.getId());
                result.addProperty("success", ok);
                result.addProperty("message", ok ? "Da giu ghe (5 phut de thanh toan)."
                        : "Ghe nay vua duoc nguoi khac chon, vui long chon ghe khac.");
            }
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Yeu cau khong hop le.");
        }

        writeJson(resp, result);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            int showtimeId = Integer.parseInt(req.getParameter("showtimeId"));
            List<ShowtimeSeat> seats = showtimeSeatDAO.findByShowtime(showtimeId);

            // Chi tra ve du lieu can thiet cho FE cap nhat luoi ghe (khong lo thong tin nhay cam)
            List<SeatStatusDTO> dto = seats.stream().map(s -> {
                SeatStatusDTO d = new SeatStatusDTO();
                d.seatId = s.getSeatId();
                d.seatCode = s.getSeatCode();
                d.status = s.getStatus();
                return d;
            }).collect(Collectors.toList());

            resp.getWriter().write(gson.toJson(dto));
        } catch (Exception e) {
            resp.getWriter().write("[]");
        }
    }

    private void writeJson(HttpServletResponse resp, JsonObject obj) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            out.write(gson.toJson(obj));
        }
    }

    private static class SeatStatusDTO {
        int seatId;
        String seatCode;
        String status;
    }
}
