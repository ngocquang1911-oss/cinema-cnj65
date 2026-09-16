package com.cnj65.cinema.controller.customer;

import com.cnj65.cinema.dao.MovieDAO;
import com.cnj65.cinema.model.Movie;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final MovieDAO movieDAO = new MovieDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Movie> nowShowing = movieDAO.findByStatus("NOW_SHOWING");
        List<Movie> comingSoon = movieDAO.findByStatus("COMING_SOON");

        req.setAttribute("nowShowing", nowShowing);
        req.setAttribute("comingSoon", comingSoon);
        req.getRequestDispatcher("/WEB-INF/views/customer/home.jsp").forward(req, resp);
    }
}
