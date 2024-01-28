package com.example.ppp.repo;


import com.example.ppp.model.Notice;
import com.example.ppp.model.QNotice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommonRepoCustomlmpl implements CommonRepoCustom  {
    private final JPAQueryFactory jpaQueryFactory;

    public CommonRepoCustomlmpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
}
