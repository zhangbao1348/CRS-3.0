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
@Table(name = "reservation_guest")
public class ReservationGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reservation_id", nullable = false)
    private Integer reservationId;

    @Column(name = "guest_type", nullable = false, length = 20)
    private String guestType = "guest";

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "id_type", length = 30)
    private String idType;

    @Column(name = "id_number", length = 100)
    private String idNumber;

    @Column(name = "member_no", length = 50)
    private String memberNo;

    @Column(name = "member_level", length = 30)
    private String memberLevel;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(name = "pms_account", length = 100)
    private String pmsAccount;

    @Column(name = "pms_status", length = 30)
    private String pmsStatus;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
