package com.example.ppp.repo;

import com.example.ppp.model.Notice;
import com.example.ppp.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.*;

import java.util.List;

@Repository
public interface SchoolRepo extends JpaRepository<School, Long> {

    School findByschoolSeq(long schoolIdx);

    Page<School> findAllByuseYn(String Y, Pageable pageable);

    School findAllByschoolSeqAndUserSeq(long schoolSeq, long userSeq)  ;
}
