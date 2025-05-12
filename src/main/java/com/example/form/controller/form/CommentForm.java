package com.example.form.controller.form;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class CommentForm {

    private int id;
    private int reportId;
    @NotBlank(message = "コメントを入力してください")
    private String comment;
    private Date updatedDate;
}
