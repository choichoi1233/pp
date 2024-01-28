package com.example.ppp.repo;

import com.example.ppp.model.School;
import com.example.ppp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findAllByUserIdAndUserPwd(String userId, String userPwd);

    User findAllByUserSeq(long userSeq);
}
