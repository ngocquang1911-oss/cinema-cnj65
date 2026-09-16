package com.cnj65.cinema.controller.admin;

import com.cnj65.cinema.dao.RoomDAO;
import com.cnj65.cinema.dao.SeatDAO;
import com.cnj65.cinema.model.Room;
import com.cnj65.cinema.model.Seat;
import com.cnj65.cinema.util.Constants;
import com.cnj65.cinema.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Quan ly Phong chieu + So do ghe.
 *   GET  /admin/rooms                        -> danh sach phong
 *   GET  /admin/rooms?action=form[&id=X]      -> form them/sua thong tin phong
 *   GET  /admin/rooms?action=seats&id=X       -> xem/sinh lai so do ghe cua phong X
 *   POST /admin/rooms (action=save)           -> luu thong tin phong
 *   POST /admin/rooms (action=generate-seats) -> sinh ghe tu dong theo hang/cot
 *   POST /admin/rooms (action=delete)         -> xoa phong (chi khi chua co suat chieu)
 */
@WebServlet("/admin/rooms")
public class AdminRoomServlet extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();
    private final SeatDAO seatDAO = new SeatDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("form".equals(action)) {
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                req.setAttribute("room", roomDAO.findById(Integer.parseInt(idParam)));
            }
            req.getRequestDispatcher("/WEB-INF/views/admin/room-form.jsp").forward(req, resp);
            return;
        }

        if ("seats".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Room room = roomDAO.findById(id);
            List<Seat> seats = seatDAO.findByRoom(id);

            // Nhom ghe theo hang (dung logic giong SeatSelectServlet ben Customer
            // de JSP render dang luoi ma khong can xu ly trong JSTL)
            java.util.Map<String, List<Seat>> seatsByRow = new java.util.LinkedHashMap<>();
            for (Seat s : seats) {
                seatsByRow.computeIfAbsent(s.getSeatRow(), k -> new java.util.ArrayList<>()).add(s);
            }

            req.setAttribute("room", room);
            req.setAttribute("seatsByRow", seatsByRow);
            req.getRequestDispatcher("/WEB-INF/views/admin/room-seats.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("rooms", roomDAO.findAll());
        req.getRequestDispatcher("/WEB-INF/views/admin/room-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            if (roomDAO.hasShowtimes(id)) {
                resp.sendRedirect(req.getContextPath() + "/admin/rooms?error=in_use");
                return;
            }
            roomDAO.delete(id);
            resp.sendRedirect(req.getContextPath() + "/admin/rooms");
            return;
        }

        if ("generate-seats".equals(action)) {
            int roomId = Integer.parseInt(req.getParameter("roomId"));
            int rows = Integer.parseInt(req.getParameter("rows"));
            int cols = Integer.parseInt(req.getParameter("cols"));
            String coupleRowParam = req.getParameter("coupleRow"); // "-1" neu khong co ghe doi
            int coupleRow = ValidationUtil.isBlank(coupleRowParam) ? -1 : Integer.parseInt(coupleRowParam);

            int total = seatDAO.generateSeatsForRoom(roomId, rows, cols, coupleRow);
            roomDAO.updateTotalSeats(roomId, total);

            resp.sendRedirect(req.getContextPath() + "/admin/rooms?action=seats&id=" + roomId + "&success=generated");
            return;
        }

        // action=save
        String idParam = req.getParameter("id");
        String name = req.getParameter("name");
        String roomType = req.getParameter("roomType");
        String status = req.getParameter("status");

        if (ValidationUtil.isBlank(name)) {
            req.setAttribute("error", "Vui lòng nhập tên phòng.");
            req.getRequestDispatcher("/WEB-INF/views/admin/room-form.jsp").forward(req, resp);
            return;
        }

        Room r = new Room();
        r.setName(name.trim());
        r.setRoomType(roomType);
        r.setStatus(ValidationUtil.isBlank(status) ? Constants.ROOM_ACTIVE : status);

        if (idParam != null && !idParam.isEmpty()) {
            r.setId(Integer.parseInt(idParam));
            roomDAO.update(r);
            resp.sendRedirect(req.getContextPath() + "/admin/rooms");
        } else {
            r.setTotalSeats(0);
            int newId = roomDAO.insert(r);
            // Phong moi chua co ghe -> dua thang toi man hinh sinh so do ghe
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?action=seats&id=" + newId);
        }
    }
}
