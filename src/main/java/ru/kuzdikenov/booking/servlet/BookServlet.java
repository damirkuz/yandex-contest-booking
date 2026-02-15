package ru.kuzdikenov.booking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kuzdikenov.booking.dao.BookingDao;
import ru.kuzdikenov.booking.model.Booking;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.format.DateTimeParseException;

public class BookServlet extends HttpServlet {
    private final BookingDao dao = new BookingDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Чтение параметров
            String placeIdStr = req.getParameter("place_id");
            String userIdStr = req.getParameter("user_id");
            String fromStr = req.getParameter("from");
            String toStr = req.getParameter("to");

            if (placeIdStr == null || userIdStr == null || fromStr == null || toStr == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int placeId;
            int userId;
            Instant from;
            Instant to;

            try {
                placeId = Integer.parseInt(placeIdStr);
                userId = Integer.parseInt(userIdStr);
                from = Instant.parse(fromStr);
                to = Instant.parse(toStr);
            } catch (NumberFormatException | DateTimeParseException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (!from.isBefore(to)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (dao.hasConflict(placeId, from, to)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }

            dao.create(new Booking(userId, placeId, from, to));
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}