package com.example.forum.controller.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

    private int id;
    private int report_id;
    private String comments;
}
