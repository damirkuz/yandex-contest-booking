package ru.kuzdikenov.booking.model;

import java.time.Instant;

public class Booking {
    private Integer id;
    private int userId;
    private int placeId;
    private Instant timeFrom;
    private Instant timeTo;

    public Booking(int userId, int placeId, Instant timeFrom, Instant timeTo) {
        this.userId = userId;
        this.placeId = placeId;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    public Booking() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPlaceId() { return placeId; }
    public void setPlaceId(int placeId) { this.placeId = placeId; }

    public Instant getTimeFrom() { return timeFrom; }
    public void setTimeFrom(Instant timeFrom) { this.timeFrom = timeFrom; }

    public Instant getTimeTo() { return timeTo; }
    public void setTimeTo(Instant timeTo) { this.timeTo = timeTo; }
}
