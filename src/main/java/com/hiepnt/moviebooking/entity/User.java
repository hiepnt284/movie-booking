package com.hiepnt.moviebooking.entity;

import com.hiepnt.moviebooking.entity.enums.Gender;
import com.hiepnt.moviebooking.entity.enums.Role;
import com.hiepnt.moviebooking.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "user")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(unique = true)
    String email;

    String password;

    String phoneNumber;

    String fullName;

    @Enumerated(EnumType.STRING)
    Gender gender;

    LocalDate dob;

    @Enumerated(EnumType.STRING)
    Role role;

    String avt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    Status isActive = Status.PENDING;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    String otp;

    LocalDateTime otpGeneratedTime;

    @Builder.Default
    boolean isGgAccount = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Token> tokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Booking> bookings;

}
