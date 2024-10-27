package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie,Integer> {
    boolean existsByTitle(String title);

    List<Movie> findByIsActiveTrue();


    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Movie> findByTitleOrDescriptionContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);


}
