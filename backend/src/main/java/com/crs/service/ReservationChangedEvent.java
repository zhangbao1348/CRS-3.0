package com.crs.service;

import com.crs.entity.Reservation;
import org.springframework.context.ApplicationEvent;

public class ReservationChangedEvent extends ApplicationEvent {

    private final Reservation reservation;
    private final String oldStatus;
    private final String newStatus;

    public ReservationChangedEvent(Object source, Reservation reservation, String oldStatus, String newStatus) {
        super(source);
        this.reservation = reservation;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Reservation reservation() { return reservation; }
    public String getOldStatus() { return oldStatus; }
    public String getNewStatus() { return newStatus; }
}
