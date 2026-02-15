package ru.kuzdikenov.booking.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kuzdikenov.booking.dao.BookingDao;
import ru.kuzdikenov.booking.model.Booking;

import java.io.IOException;
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
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
                return;
            }

            int placeId = Integer.parseInt(placeIdStr);
            int userId = Integer.parseInt(userIdStr);
            Instant from = Instant.parse(fromStr);
            Instant to = Instant.parse(toStr);

            if (!from.isBefore(to)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid time interval");
                return;
            }

            if (dao.hasConflict(placeId, from, to)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }

            dao.create(new Booking(userId, placeId, from, to));
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException | DateTimeParseException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid format");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
