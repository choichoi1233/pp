package com.example.ppp.config;

import com.example.ppp.model.UnauthorizedCustomException;
import com.example.ppp.model.User;
import com.example.ppp.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class AuthWebConfig implements WebMvcConfigurer {
    private final UserJwtService userJwtService =  new UserJwtService();

    private final StringRedisTemplate redisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserJwtInterceptor())
                .addPathPatterns("/api/admin/**");  // 인터셉터를 적용할 URL 패턴을 지정합니다.
        /*registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/api/user/**");  // 인터셉터를 적용할 URL 패턴을 지정합니다.
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/api/teacher/**");  // 인터셉터를 적용할 URL 패턴을 지정합니다.*/
    }

    class UserJwtInterceptor implements HandlerInterceptor {
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            // JWT 토큰을 가져옵니다.
            String token = request.getHeader("Authorization");
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            log.info("token = {}  || url = {}" ,token, request.getRequestURI());
            if(token.contains("MTPUser")){
                //유저의 경우 해당 로직을 이용함.
                token = token.replace("MTPUser ","").replaceAll("[\\\\\"]", "");
                Jws<Claims> claims = null;
                User user = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    claims = userJwtService.verifyToken(token);
                    user = objectMapper.readValue(claims.getBody().getSubject(), User.class);
                    if(!token.equals(ops.get("AcUserToken:"+user.getUserSeq()))){
                        // JJWT 최신화 된것이 아님. 어디선가 탈취한 토큰을 사용 했을 경우 임.
                        // JJWT 최신화 된것이 아니면 얄짤없이 401 에러임.
                        throw new UnauthorizedCustomException("엑세스 토큰 최신 검사 실패","401","/Admin/Login");
                    }
                } catch (ExpiredJwtException exjwt){
                    throw new UnauthorizedCustomException("엑세스 토큰 만료","401","/Admin/Login");
                } catch (JwtException e){
                    throw new UnauthorizedCustomException("엑세스 토큰 복호화 실패","401","/Admin/Login");
                }
                request.setAttribute("user", user);
            } else if(token.contains("MTPStudent")){


            } else {
                return false;
            }
            return true;
        }
    }
}
