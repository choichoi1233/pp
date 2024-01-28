package com.example.ppp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@Table(name="tb_school_detail")
@NoArgsConstructor
public class SchoolDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long schoolDetailSeq;

    private long schoolSeq;

    private String schoolDetailName;

    private long grade;

    @Column(name = "use_yn")
    private String useYn; // 사용여부

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        if(useYn == null) useYn ="Y";
        updatedAt = createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
