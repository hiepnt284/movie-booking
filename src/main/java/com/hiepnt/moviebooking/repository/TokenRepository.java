package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.Token;
import com.hiepnt.moviebooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {
    Optional<Token> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByUser(User user);
}
