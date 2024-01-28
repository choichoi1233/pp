package com.example.ppp.repo;

import com.example.ppp.model.School;
import com.example.ppp.model.SchoolDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolDetailRepo extends JpaRepository<SchoolDetail, Long> {

    List<SchoolDetail> findAllByschoolSeq(long schoolSeq);
}
