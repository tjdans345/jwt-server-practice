package com.meteor.jwt.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meteor.jwt.auth.PrincipalDetails;
import com.meteor.jwt.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있음
// /login 요청해서 username, password 전송하면 (post)
// UsernamePasswordAuthenticationFilter 동작을 함
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        System.out.println("JwtAuthenticationFilter 로그인 시도중");

        // 1. 유저네임, 패스워드 받아서
        try { // 스트림안에 유저정보가 담겨있다
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(request.getInputStream(), User.class);
            System.out.println(user);

            // 토큰을 만들어줘야함
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // PrincipalDetailsService 의 loadUserByUsername() 함수가 실행이 된다. 실행된 후 정상이면 authentication 이 리턴된다.
            // authentication 에 로그인 정보가 담긴다.
            // DB에 있는 username 과 password 가 일치한다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // authentication 객체가 session 영역에 저장됨. => 로그인이 되었다는 뜻
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료됨 : " + principalDetails.getUser().getUsername());
            // authentication 객체가 session 영역에 저장을 해야하고 그 방법이 return 을 해주면 된다 => 즉 시큐리티 세션영역에 저장이된다.
            // 리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는거임
            // 굳이 jwt 토큰을 사용하면서 세션을 만들 이유가 없음. 근데 단지 권한 처리때문에 session 에 넣어 줌.
            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. 정상인지 로그인 시도를 해보는 거임 authenticationManager 로 로그인 시도를 하면!!
        // PrincipalDetailsService 가 호출이 된다.
        // 3. PrincipalDetails 를 세션에 담고 -> 권한 관리를 위해서 세션에 유저 정보를 담는다.
        // 4. JWT 토큰을 만들어서 응답해주면 된다.
        return null;
    }

    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행이 된다.
    // JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 된다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨 : 인증이 완료되었다는 것");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // RSA 방식은 아니고 Hash 암호 방식
        String jwtToken = JWT.create()
                .withSubject("meteor토큰")
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
                        .withClaim("id", principalDetails.getUser().getId()) // withClaim 은 비공개 클레임 -> 넣고 싶은 값 아무거나 넣어도 된다.
                                .withClaim("username", principalDetails.getUsername())
                                        .sign(Algorithm.HMAC512("meteor"));

        response.addHeader("Authorization", "Bearer " + jwtToken);

        // 유저네임, 패스워드 로그인 정상 -> 서버 쪽 세션 ID생성
        // 클라이언트 쿠키 세션 ID 를 응답,
        // 요청할 때마다 쿠키 값 세션ID를 항상 들고 서버쪽으로 요청하기 때문에 서버는 세셔ID가 유효한지 판단해서 유효하면 인증이 필요한 페이지로 접근하게 하면 됨.

        // 유저네임, 패스워드 로그인 정상
        // JWT 토큰을 생성
        // 클라이언트 쪽으로 JWT토큰을 응답

        // 요청할 때 마다 JWT 토큰을 가지고 요청한다.
        // 서버는 JWT 토큰이 유효한지를 판단해야한다. ( => 직접 필터를 만들어줘야한다.)

    }
}


