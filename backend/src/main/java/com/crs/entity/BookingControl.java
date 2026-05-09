package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking_controls", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "hotel_code", "dimension_type", "dimension_code", "control_date"})
})
public class BookingControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;

    @Column(name = "dimension_type", nullable = false, length = 20)
    private String dimensionType;

    @Column(name = "dimension_code", nullable = false, length = 50)
    private String dimensionCode = "";

    @Column(name = "control_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date controlDate;

    @Column(name = "cancellation_policy_code", length = 50)
    private String cancellationPolicyCode;

    @Column(name = "advance_booking_days")
    private Integer advanceBookingDays = 0;

    @Column(name = "min_stay")
    private Integer minStay = 1;

    @Column(name = "max_stay")
    private Integer maxStay = 30;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
