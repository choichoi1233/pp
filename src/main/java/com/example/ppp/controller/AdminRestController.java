package com.example.ppp.controller;

import com.example.ppp.model.*;
import com.example.ppp.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;

import java.util.List;

/*
* 2XX Success
4.1. 200 OK
4.2. 201 Created
4.3. 202 Accepted
4.4. 204 No Content
4XX Client errors
5.1. 400 Bad Request
5.2. 401 Unauthorized
5.3. 403 Forbidden
5.4. 404 Not Found
5.5. 405 Method Not Allowd
5.6. 409 Conflict
5.7. 429 Too many Requests
* */
@RestController
@RequestMapping(value ="/api/admin/")
@RequiredArgsConstructor
@Log4j2
public class AdminRestController {
    private final CommonService commonService;
    private final UserService userService;
    private final UserJwtService userJwtService =  new UserJwtService();

    @GetMapping("/noticeList")
    public List<Notice> noticeList(){
        return commonService.GetNoticeList();
    }

    @GetMapping("/schoolList")
    public Page<School> schoolList(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable ,
            @RequestAttribute("user") User user){
        return commonService.GetSchoolList(pageable , user.getUserSeq());
    }

    @GetMapping("/schoolinfo")
    public ResponseEntity<ResultVo<School>>  schoolinfo(
            @RequestParam long schoolSeq,
            @RequestAttribute("user") User user){
        School data =   commonService.GetSchoolInfo(schoolSeq,user);

        ResultVo<School> result =   new  ResultVo<School>("","S",data);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/schoolDetails")
    public ResponseEntity<ResultVo<List<SchoolDetail>>>  schoolDetail(
            @RequestParam long schoolSeq,
            @RequestAttribute("user") User user){

        List<SchoolDetail> data =   commonService.GetSchoolDetail(schoolSeq,user);

        ResultVo<List<SchoolDetail>> result =   new  ResultVo<List<SchoolDetail>>("","S",data);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/schoolIns")
    public ResponseEntity<ResultVo<School>> schoolIns(@RequestBody School data , @RequestAttribute("user") User user ){
        data.setUserSeq(user.getUserSeq());
        log.info("data= {}", data.toString());
        data = commonService.InsSchool(data);
        ResultVo<School> result =   new  ResultVo<School>("","S",data);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/TokenCheck")
    public ResponseEntity<ResultVo<Long>> TokenCheck(@RequestParam String Token){
        ResultVo<Long> result = null;
        try {
            result = new ResultVo<Long>("","S", userJwtService.getRemainingSeconds(Token.replaceAll("[\\\\\"]", "")));
            return new ResponseEntity<ResultVo<Long>>(result, HttpStatus.OK);
        } catch (JwtException e) {
           result = new ResultVo<Long>(e.getLocalizedMessage(),"F",0L);
           return new ResponseEntity<ResultVo<Long>>(result, HttpStatus.UNAUTHORIZED);
        }
    }
}
