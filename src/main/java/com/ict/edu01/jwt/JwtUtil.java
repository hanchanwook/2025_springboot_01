package com.ict.edu01.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {


    /* 
    JWT 토큰을 서명(sign)하고 검증(verify)하는데 사용되는 비밀 키입니다.
    final로 선언되어 있어 한 번 생성되면 변경할 수 없습니다.
    토큰의 무결성과 신뢰성을 보장하는 핵심 요소입니다.
    생성자에서 Keys.hmacShaKeyFor(secret.getBytes()) 메서드를 통해 초기화합니다.
     */
    private final Key secretKey;

    /*
    Access Token의 유효기간을 밀리초 단위로 저장합니다.
    일반적으로 짧은 시간(예: 30분~1시간)으로 설정됩니다.
    보안을 위해 자주 갱신되어야 하는 토큰의 수명을 제어합니다.
     */
    private long accessTokenValidity;

    /*
    Refresh Token의 유효기간을 밀리초 단위로 저장합니다.
    일반적으로 Access Token보다 긴 시간(예: 1주일~2주)으로 설정됩니다.
    Access Token이 만료되었을 때 새로운 Access Token을 발급 받는데 사용 됩니다.
     */
    private long refreshTokenValidity;

    /*
    이러한 변수들이 필요한 이유는 
    1. 보안 : secretKey 를 통해 토큰의 위조를 방지합니다.
    2. 세션 관리 : accessTokenValidity와 refreshTokenValidity를 통해 토큰의 수명을 제어합니다.
    3. 사용자 경험 : Access Token은 짧게, Refresh Token은 길게 설정하여 보안과 사용성의 균형을 맞춥니다.
    코드에서 이 값들은 생성자를 통해 초기화되며, 토큰 생성 시 사용됩니다. 
    */


    //  생성자
    public JwtUtil(String secret, long accessTokenValidity, long refreshTokenValidity) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());     // HMAC-SHA256 알고리즘을 사용하기 위한 키를 생성하는 메소드
        /*
        hmacShaKeyFor 는 HMAC-SHA256 알고리즘을 사용하기 위한 키를 생성하는 메소드입니다.
        HMAC-SHA256
        1. HMAC (Hash-based Message Authentication Code)- 메시지 인증 코드
         메시지의 무결성과 인증을 보장하는 암호화 방식
         비밀 키와 메시지를 조합하여 해시값을 생성
         메시지가 변조되지 않았는지 확인하는데 사용
        
        2. SHA256
         256비트(32바이트) 길이의 해시값을 생성하는 해시 함수
         입력 데이터를 고정된 길이의 해시값으로 변환
         일방향 함수로, 해시값에서 원본 데이터를 복원할 수 없음 

        HMAC-SHA256의 사용 이유
         1. 보안성 : 강력한 암호화 알고리즘으로 토큰의 위조를 방지
         2. 효율성 : 대칭키 암호화 방식으로 처리 속도가 빠름
         3. 검증 용이성 : 동일한 키로 서명과 검증이 가능
        */

        this.accessTokenValidity = accessTokenValidity;     // accessTokenValidity 클래스의 필드를 초기화 하는 코드
        this.refreshTokenValidity = refreshTokenValidity;   // refreshTokenValidity 클래스의 필드를 초기화 하는 코드
    }

    // Access Token 생성 메소드
    public String gererateAccessToken(String userId) {
        return Jwts.builder()                           // jwt 토큰을 생성하기 위한 빌더 패턴 시작, 토큰의 각 부분을 설정할 수 있음.
                .setSubject(userId)                     // 토큰의 주체(사용자ID)를 설정
                .setIssuedAt(new Date())                // 토큰이 발급된 시간을 현재 시간으로 설정
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity)) // 토큰의 만료 시간을 설정, System.currentTimeMillis() : 현재 시간을 밀리초 단위로 환산, accessTokenValidity : 생성자에서 설정한 유효기간간
                .signWith(secretKey,SignatureAlgorithm.HS256)   // 토큰에 서명을 추가, secretKey : 생성자에게 생성한 비밀키, SignatureAlgorithm.HS256 :HMAC-SHA256 알고리즘 사용
                .compact();                              // 설정된 모든 정보를 포함한 JWT 토큰을 문자열로 변환환
    }
    
    /*
    Access Token과 Refresh Token의 주요 차이점
    1. 유효 기간
        - Access Token : 짧은 시간
        - Refresh Token : 긴 시간
    2. 용도
        - Access Token : 인증 및 권한 부여
        - Refresh Token : 새로운 Access Token 발급
    3. 보안
        - Access Token : 더 높은 보안 요구
        - Refresh Token : 더 낮은 보안 요구
    */    

    // Refresh Token 생성 메소드
    public String gererateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(secretKey,SignatureAlgorithm.HS256)
                .compact();
    }

    /*
    Refresh Token의 유효기간을 조회하는 getter 메소드입니다.
    */

    // Refresh Token  유효기간 반환
    public long getRefreshTokenValidity() {
        return refreshTokenValidity;
    }


    // JWT 검증 및 사용자 ID 추출
    public String validateAndExtractUserId(String token) {
        try {
            // 넘어온 token 에는 "Bearer Token 내용"
            // Bearer 는 HTTP Authorization 헤더를 통해 토큰 전달 방식
            // Bearer 의미는 "이 토큰 소지한 자는 인증된 것으로 간주한다." 를 의미
            // Authorization: Bearer token
            token = token.substring(7);     // token 값만 가져오기, 보통은 'Bearer '와 함께 사용하기에 substring(7)으로 실제 토큰 값만 추출 
            Claims ckaims = Jwts.parserBuilder()       
                    .setSigningKey(secretKey)          // 토큰 서명을 검증할 키 설정 
                    .build()                           // JWT 파서 생성
                    .parseClaimsJws(token)             // 토큰을 파싱하고 서명 검증
                    .getBody();                        // 검증된 토큰의 내용 추출
            return ckaims.getSubject();                // 사용자 ID 반환
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Token");
        }
    }

    //  토큰 만료 되었는지 확인하는 메서드
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    //  만료 날짜 추출
    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    //  받은 토큰을 이용해서 모든 정보 반환
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    public boolean validateToken(String jwtToken, UserDetails userDetails){
        try {
            validateAndExtractUserId(jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
