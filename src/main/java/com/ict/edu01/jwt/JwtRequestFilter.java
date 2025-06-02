package com.ict.edu01.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

// JWT 기반 인증을 처리하는 필터
// HTTP 요청이 올 때 마다 딱 한 번 실행되며, JWT 토큰을 감시하고, 인증 처리 해줌
@Slf4j          // Security log4j - 로깅을 위한 어노테이션
@Component      // Spring Bean으로 등록
public class JwtRequestFilter extends OncePerRequestFilter{

    // JWT 토큰 생성 및 검증을 위한 유틸리티
    @Autowired
    private JwtUtil jwtUtil;
    
    // 사용자 정보를 로드하는 서비스
    @Autowired
    private UserDetailsService userDetailsService;

    // 모든 HTTP 요청에 대해 실행되는 필터 메소드
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
             FilterChain filterChain) throws ServletException, IOException {

        log.info("JwtRequestFilter Call ok");

        // 토큰 검사 예외 처리 : refresh 요청 통과
        String path = request.getRequestURI();
        if("api/member/refresh".equals(path)){
            filterChain.doFilter(request, response);
            return;
        }

        // 들어오는 HTTP 요청마다 Authorization 이 있고 Authorization 는 JWT 검증하기 위해서 토큰을 추출
        final String authorizationHeader = request.getHeader("Authorization");
        String userId = null;
        String jwtToken = null;

        // Authorization 헤더가 존재하고 "Bearer "로 시작하는지 확인
        // Bearer는 JWT 토큰의 표준 인증 방식
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            // "Bearer " 접두사를 제거하고 실제 토큰 값만 추출
            jwtToken = authorizationHeader.substring(7);
           
            try {
                log.info("jwtToken : " + jwtToken);
                //  토큰 만료 검사
                if(jwtUtil.isTokenExpired(jwtToken)){
                    log.info("token expire error");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write("{\"success\":false, \"message\":\"token expired\"}");
                    return;
                }
                userId = jwtUtil.validateAndExtractUserId(jwtToken);
                log.info("userId : " + userId);
                
            } catch (Exception e) {
                log.info("token error");
                // 토큰 처리 중 오류 발생 시 401 Unauthorized 응답
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write("{\"success\":false, \"message\":\"token expired\"}");
                    return;            
                }
        } else {
            log.info("Authorization empty Bearer token empty ");
        }

        //  사용자 ID가 존재하고 SecurityContext에 인증정보가 없는 경우 등록하기 위해서
        if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //  등록하자
            //  DB에서 사용자 정보 가져오기
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            // JWT 검증 및 SpringSecurity 인증객체에 사용자 정보를 등록
            if(jwtUtil.validateToken(jwtToken, userDetails)){
                // SpringSecurity 표준 인증 객체 (인증 주체, 자격증명(null=jwt), 권한정보(ROLE))
                UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
                // SecurityContext에 등록
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("JWT token ok");
            }else{
                log.info("JWT token error");
            }
        
        }
        
        // 필터 체인 실행 (다음 필터로 요청 전달)
        filterChain.doFilter(request, response);
    }
}
