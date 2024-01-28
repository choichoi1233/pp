package com.example.ppp.repo;


import com.example.ppp.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonRepo extends JpaRepository<Notice, Long>, CommonRepoCustom {

    List<Notice> findByuseYn(String useYn);
}
