package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("""
    SELECT u 
    FROM User u 
    WHERE (:keyword IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:theaterId IS NULL OR u.theater.id = :theaterId) 
      AND u.theater.id IS NOT NULL
""")
    Page<User> findByEmailAndTheater(@Param("keyword") String keyword,
                                     @Param("theaterId") Integer theaterId,
                                     Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (:keyword IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND u.theater IS NULL " +
            "AND (:excludedEmail IS NULL OR u.email != :excludedEmail)")
    Page<User> findByEmail(@Param("keyword") String keyword, @Param("excludedEmail") String excludedEmail, Pageable pageable);




}
