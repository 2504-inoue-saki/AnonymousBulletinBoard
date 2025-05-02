package com.example.form.controller.form;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CommentForm {

    private int id;;
    private int reportId;
    private String comments;
    private Timestamp updated_date;
}
