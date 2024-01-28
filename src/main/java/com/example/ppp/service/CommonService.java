package com.example.ppp.service;
import org.springframework.data.domain.*;
import com.example.ppp.model.*;
import com.example.ppp.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Log4j2
public class CommonService {


    private final CommonRepo commonRepo;
    private final SchoolRepo schoolRepo;
    private final SchoolDetailRepo schoolDetailRepo;

    public List<Notice> GetNoticeList() {
        return commonRepo.findByuseYn("Y");
    }

    public Page<School> GetSchoolList(Pageable pageable, long userSeq) {


        return schoolRepo.findAllByuseYn("Y", pageable);
    }
    @Transactional
    public School InsSchool(School data) {
        School school = schoolRepo.findByschoolSeq(data.getSchoolSeq());
        if(school == null ){
            //신규 입력
            return schoolRepo.save(data);
        }else {
            //위에서 조회한 클래스 그대로 내가 수정하고싶은것만 수정함.
            school.setSchoolNm(data.getSchoolNm() == null ? school.getSchoolNm() : data.getSchoolNm());
            school.setUseYn(data.getUseYn() == null ? school.getUseYn():data.getUseYn());
            return schoolRepo.save(school);
        } 
    }

    public School GetSchoolInfo(long schoolSeq, User user) {

        return schoolRepo.findAllByschoolSeqAndUserSeq(schoolSeq, user.getUserSeq());
    }

    public List<SchoolDetail> GetSchoolDetail(long schoolSeq, User user) {

        return schoolDetailRepo.findAllByschoolSeq(schoolSeq );
    }
}