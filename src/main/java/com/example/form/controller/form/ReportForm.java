package com.example.form.controller.form;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReportForm {

    private int id;
    private String content;
    private LocalDateTime updatedDate;
}
