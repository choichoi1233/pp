package com.example.ppp.controller;


import com.example.ppp.model.*;
import com.example.ppp.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

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
@RequestMapping(value ="/api/shard/")
@RequiredArgsConstructor
@Log4j2
public class ShardRestController {
    private final CommonService commonService;
    private final UserService userService;
    private final UserJwtService userJwtService =  new UserJwtService();
    private final StudentJwtService studentJwtService =  new StudentJwtService();

    private final StringRedisTemplate redisTemplate;

    @Value("${userAccessTokenTime}")
    int userAccessTokenTime;

    @Value("${userRefreshTokenTime}")
    int userRefreshTokenTime;

    @PostMapping("/TokenCheck")
    public ResponseEntity<ResultVo<Long>> TokenCheck(@RequestParam String Token ){
        //엑세스 토큰을 검증해서 리프레시토큰 체크하고 리프레시 토큰의 남은 시간을 반환
        ResultVo<Long> result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if(Token.contains("MTPUser")){
                Jws<Claims> claims = userJwtService.verifyToken(Token.replace("MTPUser ","").replaceAll("[\\\\\"]", ""));
                User user = objectMapper.readValue(claims.getBody().getSubject(), User.class);
                result = new ResultVo<Long>(user.getUserName(),"S", userJwtService.getRemainingSeconds(Token.replace("MTPUser ","").replaceAll("[\\\\\"]", "")));
            } else if(Token.contains("MTPStudent")){
                result = new ResultVo<Long>("","S", studentJwtService.getRemainingSeconds(Token.replace("MTPStudent ","").replaceAll("[\\\\\"]", "")));
            }
            return new ResponseEntity<ResultVo<Long>>(result, HttpStatus.OK);
        } catch (JwtException e) {
           result = new ResultVo<Long>(e.getLocalizedMessage(),"F",0L);
           return new ResponseEntity<ResultVo<Long>>(result, HttpStatus.UNAUTHORIZED);
        } catch (Exception e0){
            result = new ResultVo<Long>(e0.getLocalizedMessage(),"F",0L);
            return new ResponseEntity<ResultVo<Long>>(result, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ResultVo<String>> refreshToken(@RequestParam String Token ){
        //엑세스 토큰을 받고, 해당 엑세스 토큰이 정상적토큰 + 리프레시토큰이 정상인 경우 새로운 엑세스 토큰 발행.
        ResultVo<String> result = null;
        ObjectMapper mapper = new ObjectMapper();
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        try {
            if(Token.startsWith("MTPUser")){
                //사장님 전용 토큰
                Token = Token.replace("MTPUser ","").replaceAll("[\\\\\"]", "");
                Jws<Claims> claims = userJwtService.verifyToken(Token);
                User user = mapper.readValue(claims.getBody().getSubject(), User.class);

                if(!Token.equals(ops.get("AcUserToken:"+user.getUserSeq()))){
                    // JJWT 최신화 된것이 아님. 어디선가 탈취한 토큰을 사용 했을 경우 임.
                    // JJWT 최신화 된것이 아니면 얄짤없이 401 에러임.
                    throw new UnauthorizedCustomException("엑세스 토큰 최신 검사 실패","401","/Admin/Login");
                }

                if(ops.get("ReUserToken:"+user.getUserSeq()) == null ){
                    // JJWT 최신화 된것이 아님. 어디선가 탈취한 토큰을 사용 했을 경우 임.
                    // JJWT 최신화 된것이 아니면 얄짤없이 401 에러임.
                    throw new UnauthorizedCustomException("리프레시 토큰 만료","401","/Admin/Login");
                }

                JSONObject jsonObject = new JSONObject(mapper.writeValueAsString(user));

                String token = userJwtService.createToken(jsonObject.toString() , userAccessTokenTime); // 재생성
                ops.set("AcUserToken:"+user.getUserSeq(), token,userAccessTokenTime, TimeUnit.SECONDS);
                result = new ResultVo<String>("","S", token);
                return new ResponseEntity<ResultVo<String>>(result, HttpStatus.OK);
            } else if(Token.contains("MTPStudent")){

            }
            return new ResponseEntity<ResultVo<String>>(result, HttpStatus.OK);
        } catch (JwtException e) {
            result = new ResultVo<String>(e.getLocalizedMessage(),"F","");
            return new ResponseEntity<ResultVo<String>>(result, HttpStatus.UNAUTHORIZED);
        } catch (Exception e0){
            result = new ResultVo<String>(e0.getLocalizedMessage(),"F","");
            return new ResponseEntity<ResultVo<String>>(result, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/userLoginCheck")
    public ResponseEntity<ResultVo<String>> userLoginCheck(@RequestBody User user){
        ResultVo<String> result = null;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        try {
            User temp = userService.GetUserValidation(user);
            if(temp == null )  {
                result = new ResultVo<String>("아이디와 비밀번호를 다시 확인해주세요","F","");
                return new ResponseEntity<ResultVo<String>>(result, HttpStatus.UNAUTHORIZED);
            }
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(temp);
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject.remove("userPwd");

            String token = userJwtService.createToken(jsonObject.toString() , userAccessTokenTime);
            result = new ResultVo<String>("","S", token);

            //Accecc토큰과 Refresh토큰을 redis에 저장
            ops.set("AcUserToken:"+temp.getUserSeq(), token,userAccessTokenTime, TimeUnit.SECONDS); //30분
            ops.set("ReUserToken:"+temp.getUserSeq(), userJwtService.createRefreshToken(jsonObject.toString(), userRefreshTokenTime ),userRefreshTokenTime, TimeUnit.SECONDS ); //2시간
            return new ResponseEntity<ResultVo<String>>(result, HttpStatus.OK);
        } catch (Exception e){
            result = new ResultVo<String>(e.getLocalizedMessage(),"F","");
            return new ResponseEntity<ResultVo<String>>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
