package com.wasteless.backend.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "expiration_settings")
public class ExpirationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    //Days before expiry to send first alert (default: 3 days)
    @Column(name = "days_before_expiry_first_alert")
    private Integer  daysBeforeExpiryFirstAlert = 3;

    //Days before expiry to send second alert (default: 1 day)
    @Column(name = "days_before_expiry_second_alert")
    private Integer  daysBeforeExpirySecondAlert = 1;

    //Enable/disable email notifications
    @Column(name = "email_notifications_enabled")
    private Boolean emailNotificationsEnabled = true;

    //Enable/disable push notifications
    @Column(name = "push_notifications_enabled")
    private Boolean pushNotificationsEnabled = true;

    //Alert on the same day of expiry
    @Column(name = "alert_on_expiry_day")
    private Boolean alertOnExpiryDay = true;
}
