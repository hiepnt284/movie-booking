package com.hiepnt.moviebooking.entity;

import com.hiepnt.moviebooking.entity.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "token")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int tokenId;
    @Column(length = 500)
    String token;

    @Enumerated(EnumType.ORDINAL)
    TokenType tokenType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
