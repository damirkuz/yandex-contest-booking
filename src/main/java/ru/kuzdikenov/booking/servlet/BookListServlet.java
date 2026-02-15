package ru.kuzdikenov.booking.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kuzdikenov.booking.dao.BookingDao;
import ru.kuzdikenov.booking.model.Booking;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookListServlet extends HttpServlet {
    private final BookingDao dao = new BookingDao();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userIdStr = req.getParameter("user_id");
        String placeIdStr = req.getParameter("place_id");

        List<Booking> results;

        try {
            if (userIdStr != null) {
                results = dao.findByUserId(Integer.parseInt(userIdStr));
            } else if (placeIdStr != null) {
                results = dao.findByPlaceId(Integer.parseInt(placeIdStr));
            } else {
                results = Collections.emptyList();
            }

            Map<String, List<Booking>> responseMap = new HashMap<>();
            responseMap.put("bookings", results);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(responseMap));

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
