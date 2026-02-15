package ru.kuzdikenov.booking.dao;

import ru.kuzdikenov.booking.model.Booking;
import ru.kuzdikenov.booking.util.DatabaseConfig;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BookingDao {

    public boolean hasConflict(int placeId, Instant newFrom, Instant newTo) throws SQLException {
        String sql = "SELECT count(*) FROM bookings WHERE place_id = ? AND time_from < ? AND time_to > ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, placeId);
            stmt.setTimestamp(2, Timestamp.from(newTo));
            stmt.setTimestamp(3, Timestamp.from(newFrom));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void create(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (user_id, place_id, time_from, time_to) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getPlaceId());
            stmt.setTimestamp(3, Timestamp.from(booking.getTimeFrom()));
            stmt.setTimestamp(4, Timestamp.from(booking.getTimeTo()));

            stmt.executeUpdate();
        }
    }

    public List<Booking> findByUserId(int userId) throws SQLException {
        return findBy("SELECT * FROM bookings WHERE user_id = ? ORDER BY time_from, id", userId);
    }

    public List<Booking> findByPlaceId(int placeId) throws SQLException {
        return findBy("SELECT * FROM bookings WHERE place_id = ? ORDER BY time_from, id", placeId);
    }

    private List<Booking> findBy(String sql, int param) throws SQLException {
        List<Booking> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking();
                    b.setId(rs.getInt("id"));
                    b.setUserId(rs.getInt("user_id"));
                    b.setPlaceId(rs.getInt("place_id"));
                    b.setTimeFrom(rs.getTimestamp("time_from").toInstant());
                    b.setTimeTo(rs.getTimestamp("time_to").toInstant());
                    list.add(b);
                }
            }
        }
        return list;
    }
}
