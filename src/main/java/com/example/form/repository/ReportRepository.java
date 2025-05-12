package com.example.form.repository;

import com.example.form.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByUpdatedDateBetweenOrderByUpdatedDateDesc(Date start, Date end);
    List<Report> findAllByOrderByUpdatedDateDesc(); // ★ 追加
}
