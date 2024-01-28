package com.example.ppp.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Data
@Table(name = "tb_notice")
@Entity
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long noticeSeq;
    private String contents;
    private Date creatDt;

    @Column(name = "use_yn")
    private String useYn;
    private String sort;
    private String boldYn;
}
