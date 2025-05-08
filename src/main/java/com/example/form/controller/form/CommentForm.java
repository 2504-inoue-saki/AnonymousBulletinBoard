package com.example.form.controller.form;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentForm {

    private int id;
    private int reportId;
    private String comment;
    private LocalDateTime updatedDate;
}
