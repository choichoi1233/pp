package com.example.ppp.service;

import com.example.ppp.model.Notice;
import com.example.ppp.model.School;
import com.example.ppp.model.*;
import com.example.ppp.repo.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final CommonRepo commonRepo;
    private final SchoolRepo schoolRepo;
    private final UserRepo userRepo;

    public User GetUserValidation(User user) {
        user = userRepo.findAllByUserIdAndUserPwd(user.getUserId(),user.getUserPwd());

        return user;
    }

    public User GetUserInfoByUserSeq(long userSeq) {
        return userRepo.findAllByUserSeq(userSeq);
    }
}