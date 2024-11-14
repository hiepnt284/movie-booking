package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.Carousel;
import com.hiepnt.moviebooking.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByIsActiveTrue();
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> findByTitleOrDescriptionContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);
}
