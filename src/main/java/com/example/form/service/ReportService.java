
package com.example.form.service;

import com.example.form.controller.form.ReportForm;
import com.example.form.repository.ReportRepository;
import com.example.form.repository.entity.Report;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;

    /*
     * レコード全件取得処理
     */
    public List<ReportForm> findAllReport(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date endDate = null;

        try {
            if (!StringUtils.isBlank(start)) {
                start = start + " 00:00:00";
                startDate = dateFormat.parse(start);
            }
            if (!StringUtils.isBlank(end)) {
                end = end + " 23:59:59";
                endDate = dateFormat.parse(end);
            }

            List<Report> results;
            if (startDate != null && endDate != null) {
                results = reportRepository.findByUpdatedDateBetweenOrderByUpdatedDateDesc(startDate, endDate);
            } else {
                results = reportRepository.findAllByOrderByUpdatedDateDesc();
            }
            return setReportForm(results);
        } catch (ParseException e) {
            e.printStackTrace();
            return setReportForm(reportRepository.findAllByOrderByUpdatedDateDesc());
        }
    }

    /*
     * レコード1件取得処理
     */
    public ReportForm editReport(Integer id) {
        List<Report> results = new ArrayList<>();
        results.add((Report) reportRepository.findById(id).orElse(null));
        List<ReportForm> reports = setReportForm(results);
        return reports.get(0);
    }

    /*
     * DBから取得したデータをFormに設定
     */
    private List<ReportForm> setReportForm(List<Report> reports) {
        return reports.stream().map(report -> {
            ReportForm form = new ReportForm();
            form.setId(report.getId());
            form.setContent(report.getContent());
            form.setCreatedDate(report.getCreatedDate());
            form.setUpdatedDate(report.getUpdatedDate());
            return form;
        }).collect(Collectors.toList());
    }

    /*
     * レコード追加
     */
    public void saveReport(ReportForm reqReport) {
        Report saveReport = setReportEntity(reqReport);
        reportRepository.save(saveReport);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Report setReportEntity(ReportForm reqReport) {
        Report report = new Report();
        report.setId(reqReport.getId());
        report.setContent(reqReport.getContent());
        report.setCreatedDate(reqReport.getCreatedDate());
        report.setUpdatedDate(reqReport.getUpdatedDate());
        return report;
    }

    /*
     * レコード削除
     */
    public void deleteReport(Integer id) {
        reportRepository.deleteById(id);
    }
    @Transactional
    public void updateReportUpdatedDate(Integer reportId, Date updatedDate) {
        Report report = reportRepository.findById(reportId).orElse(null);
        if (report != null) {
            report.setUpdatedDate(updatedDate);
            reportRepository.save(report);
        }
    }
}
