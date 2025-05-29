package com.ict.edu01.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ict.edu01.jwt.JwtRequestFilter;
import com.ict.edu01.jwt.JwtUtil;
import com.ict.edu01.members.service.MembersService;
import com.ict.edu01.members.service.MyUserDetailService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration  // Configuration : 설정 클래스 ( Spring Boot 가 시작될 때 실행 된다. )
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;
    private final JwtUtil jwtUtil;
    private final MyUserDetailService userDetailService;
    private final MembersService membersService;

    // 생성자
    public SecurityConfig(JwtRequestFilter jwtRequestFilter, JwtUtil jwtUtil, 
                        MyUserDetailService userDetailService, MembersService membersService){
                            log.info("SecurityConfig 생성자");
        this.jwtRequestFilter = jwtRequestFilter;
        this.jwtUtil = jwtUtil;
        this.userDetailService = userDetailService;
        this.membersService = membersService;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                            log.info("securityFilterChain 호출");
        // 빌더 패턴(Builder Pattern) : 객체 생성 과정을 단계별로 분리하여, 동일한 생성 절차에서 서로 다른 객체를 만들 수 있게 해주는 패턴
        
        /*

        현재 import로 HttpSecurity 객체를 생성하였기에 http를 빌드 패턴으로 사용 중 이며 http.cors() 메서드는 빌더 패턴으로 사용되며, 
        이 메서드는 빌더 패턴을 사용하여 메서드 체인을 통해 설정을 추가할 수 있도록 합니다.
        즉, http.cors() 메서드는 빌더 패턴을 사용하여 메서드 체인을 통해 설정을 추가할 수 있도록 합니다.
        
        HttpSecurity는 Spring Security의 웹 보안을 구성하는 핵심 클래스로, 다음과 같은 기능들을 제공

        1. CORS(Cross-Origin Resource Sharing) 설정
            a. 다른 도메인에서의 요청을 허용할 지 설정
            b. 특정 도메인, 메서드, 헤더 등을 허용할 수 있음
            c. 보안상 필요한 경우 특정 Origin만 허용 가능

        2. CSRF(Cross-Site Request Forgery) 보호
            a. CSRF 공격 방지
            b. JWT 사용 시에는 일반적으로 비활성화
            c. REST API에서는 일반적으로 비활성화

        3. URL 권한 설정
            a. URL별 권한 설정 
            b. permitAll() : 모든 접근 허용
            c. authenticated() : 인증된 사용자만 접근 가능
            d. hasRole("ADMIN") : 특정 역할 가진 사용자만 접근 가능
            e. hasAuthority("READ") : 특정 권한을 가진 사용자만 접근 가능

        4. 세션(Session) 관리
            a. 세션 생성 정책 설정
            b. 세션 타임 아웃 설정
            c. 동시 로그인 제어
            d. 세션 고정 공격 방지 설정
            e. 세션 무효화 설정

        5. 인증(Authentication) 설정
            a. 커스텀 인증 필터 추가
            b. 필터 체인 순서 설정
            c. 인증 제공자(AuthenticationProvider) 설정
            d. 인증 매니저(AuthenticationManager) 설정

        6. HTTP 기본 보안 인증
            a. 기본 인증 활성/비활성화
            b. 기본 인증 헤더 설정
            c. 기본 인증 실패 처리

        7. 폼 로그인 설정
            a. 로그인 페이지 설정
            b. 로그인 성공/실패 URL(핸들러/페이지) 설정
            c. 로그인 처리 URL 설정
            d. 로그인 폼 커스터마이징

        8. 로그아웃 설정
            a. 로그아웃 URL 설정
            b. 로그아웃 후 리다이렉트 URL 설정
            c. 쿠키 삭제 결정
            d. 세션 무효화 설정
            e. 로그아웃 핸들러 설정

        9. 예외 처리
            a. 인증/인가 실패 시 처리 방법 설정
            b. 커스텀 예외 처리기 등록
            c. 접근 거부 핸들러 설정
            d. 인증 진입점 설정

        10. 헤더 설정
            a. XSS 방지
            b. 클릭재킹 방지
            c. 기타 보안 헤더 설정
            d. HSTS 설정
            e. Content Security Policy 설정

        11. 필터 체인 구성
            a. 필터 체인 순서를 변경
            b. 필터 체인 추가/제거
            c. 필터 체인 설정 변경
            d. 커스텀 필터 등록
            e. 필터 간 의존성 관리

        12. 권한(Authorization) 설정
            a. 권한 부여 방식 선택
            b. URL별 접근 권한 설정
            c. 메서드별 접근 권한 설정
            d. 역할 기반 접근 제어(RBAC)
            e. 동적 권한 설정

         */

        http   
        // CORS 설정
        // ( a -> b : a를 받아서 b를 실행하라 (람다식 표현))
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
       
        // CSRF 보호 비활성화 (JWT 사용시 일반적으로 비활성화)
        // CSRF란? - 사용자가 로그인 된 상태를 악용하여, 악의적인 사이트가 사용자의 권한으로 요청을 보내도록 만드는 공격
        // JWT는 세션을 사용하지 않고, Authorization 헤더로 인증(CSRF의 위험이 없음)
        .csrf(csrf -> csrf.disable())
       
        // 요청별 권한 설정
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/members/login").permitAll()      // 허용하는 url만 작성(로그인), permitAll() : 허용하다.
            .requestMatchers("/api/members/register").permitAll()   // (회원 가입)
            .anyRequest().authenticated())

        // oauth2Login 설정     -  소셜 로그인 규격 추후 추가 예정정

        //  사용자 요청이 오면 먼저 jwtRequestFilter가 실행되어, JWT 토큰을 검증 한 후
        //  이상이 없으면 SpringSecurity의 인증된 사용자로 처리된다.
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();    // 빌드 후 반환
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        // 원래 WebConfig.java에서 했던 것
        
        /*
        @Configuration
        public class WebConfig implements WebMvcConfigurer {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // 모든 엔드포인트에 대한 CORS 허용
                .allowedOrigins("http://localhost:3000") // React app URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);    // 인증 정보 포함
            }
        }   
            아래의 내용<CorsConfiguration corsConfig>과 동일하다 
        */

    CorsConfiguration corsConfig = new CorsConfiguration();
        // 허용할 Origin 설정, 메서드, 헤더, 인증 정보
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://studyjava.shop","http://43.203.201.193"));  // 배열을 받아서 리스트로 한다.
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
        // WebConfig.java 삭제
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //  인증 관리자 등록
    //  AuthenticationConfiguration : Spring Security가 자동으로 만들어주는 객체
    //                               UserDetailsService 와 PasswordEncoder 등을 포함하고 있는 객체

    
    // AuthenticationManager : 인증 관리자 - 인증 처리의 핵심 컴포넌트를 관리 
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration aConfig)throws Exception{   //  인증 관리자 등록
        return aConfig.getAuthenticationManager();
    }

}
