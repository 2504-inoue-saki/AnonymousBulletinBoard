package com.example.form.repository;

import com.example.form.repository.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByReportIdOrderByUpdatedDateDesc(int reportId);
}
