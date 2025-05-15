package com.example.form.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "comment_table")
@Getter
@Setter
public class Comment {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_report")
    private int reportId;

    @Column
    private String comments;

    @Column(name = "created_date", insertable = true, updatable = false)
    private Date createdDate;

    @Column(name = "updated_date", updatable = true)
    private Date updatedDate;
}
