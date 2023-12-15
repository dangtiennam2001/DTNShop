package com.tiennam.application.repository;

import com.tiennam.application.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    Comment findByContent(String content);

    @Query(value = "SELECT * " +
            "FROM comment u WHERE u.content LIKE CONCAT('%',?1,'%') "
            ,nativeQuery = true)
    Page<Comment> adminListCommentPages(String content, Pageable pageable);
}
