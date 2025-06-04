package com.ict.edu01.members.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ict.edu01.jwt.JwtUtil;
import com.ict.edu01.members.service.MembersService;
import com.ict.edu01.members.service.MyUserDetailService;
import com.ict.edu01.members.vo.DataVO;
import com.ict.edu01.members.vo.MembersVO;
import com.ict.edu01.members.vo.RefreshVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
// 비밀 키 선언
    // JWT 토큰을 서명(sign)하고 검증(verify)하는데 사용되는 비밀 키입니다.
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j 
@RestController
@RequestMapping("/api/members")
public class MembersController {

    @Autowired
    private MembersService membersService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    MyUserDetailService userDetailService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/hello")
    public String getHello() {
        return "Hello, SpringBoot!";
    }

    @PostMapping("/login")  // 로그인 처리
    public DataVO getLogin(@RequestBody MembersVO mvo) {

        DataVO dataVO = new DataVO();

        try {
            // mvo에 담긴 아이디와 비밀번호로 로그인 처리
            // DB에 가서 m_id 와 m_pw가 맞는지 확인한다.
            
            // membersVO가 null이면 로그인 실패            
            // DataVO dataVO = new DataVO();
            // 만약에 맞으면
            // dataVO.setSuccess(true); // 성공
            // dataVO.setMessage("로그인 성공"); // 성공 메시지
            // 만약 정보 전달할 data가 하나면
            // dataVO.setData(정보);
            
            // 만약 정보 전달할 data가 여러 개면
            // Map<String, Object> result = new HashMap<>();
            // result.put("list", list);
            // result.put("members", mvo);
            // result.put("totalCount", totalCount);
            
            // 맞지 않으면
            // dataVO.setSuccess(false); // 실패
            // dataVO.setMessage("로그인 실패"); // 실패 메시지
            /* 
            MembersVO membersVO = membersService.getLogin(mvo);
            if (membersVO == null) {
                dataVO.setSuccess(false); // 로그인 실패
                dataVO.setMessage("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
            } else {
                // 로그인 성공 시 toeken 생성 로직 추가
                dataVO.setSuccess(true); // 로그인 성공
                dataVO.setMessage("로그인 성공");
                dataVO.setData(membersVO); // 로그인한 회원 정보 반환
            }
            */
            // jwt를 활용한 로그인 처리 (암호화 해서 없어진다.)
         
            UserDetails userDetails = userDetailService.loadUserByUsername(mvo.getM_id());  // 입력된 ID를 가지고 유저 정보를 가져 온다.
            //  입력된 비밀번호와 유저 정보 일치/불일치 확인        
            if (! passwordEncoder.matches(mvo.getM_pw(), userDetails.getPassword())) {
                return new DataVO(false, "비밀번호 불일치", null );
                }    // 비밀번호 맞으면 id 가지고, accesstoken, refreshToken 생성
            
            //  ID를 가지고 accessToken, refreshToken 생성    
            String accessToken = jwtUtil.gererateAccessToken(mvo.getM_id());
            String refreshToken = jwtUtil.gererateRefreshToken(mvo.getM_id());
            
            //  회원 id에 해당하는 refreshToken 과 만료 시간을 저장
            membersService.saveRefreshToken(mvo.getM_id(), refreshToken, jwtUtil.extractExpiration(refreshToken));

            // 토큰 정보를 담아서 HashMap 생성 및 반환
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            
            // 성공 시 데이터 반환
            dataVO.setSuccess(true);    // 성공
            dataVO.setData(tokens);             // 토큰 정보
            dataVO.setMessage("로그인 성공"); // 성공 메시지

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지 반환
            dataVO.setSuccess(false); // 로그인 실패
            dataVO.setMessage("서버 오류: " + e.getMessage());
        
        }
        return dataVO; // DataVO 객체 반환 
    }

    @PostMapping("/register")
    public DataVO getRegister(@RequestBody MembersVO mvo) {    //  회원가입
        DataVO dataVO = new DataVO();
        try {
            // 비밀번호 암호화
            mvo.setM_pw(passwordEncoder.encode(mvo.getM_pw()));

            int result = membersService.getRegister(mvo);
            if(result > 0) {
                dataVO.setSuccess(true); // 회원가입 성공
                dataVO.setMessage("회원가입 성공");
            } else {
                dataVO.setSuccess(false); // 회원가입 실패
                dataVO.setMessage("회원가입 실패: 이미 존재하는 아이디입니다.");
            }
            
        } catch (Exception e) {
            dataVO.setSuccess(false); // 로그인 실패
            dataVO.setMessage("서버 오류: " + e.getMessage());
        }
        return dataVO; // DataVO 객체 반환
    }

    @GetMapping("/mypage")
    public DataVO getMyPage(HttpServletRequest request) {    //  나의 정보 조회
        DataVO dataVO = new DataVO();
        try {
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            String m_id = jwtUtil.validateAndExtractUserId(token);
            MembersVO mvo = membersService.getMyPage(m_id);
            
            if (mvo == null) {
                dataVO.setSuccess(false); // 회원 정보 조회 실패
                dataVO.setMessage("회원 정보 조회 실패: 해당 회원이 존재하지 않습니다.");
            } else {
                dataVO.setSuccess(true); // 회원 정보 조회 성공
                dataVO.setMessage("회원 정보 조회 성공");
                dataVO.setData(mvo); // 회원 정보 반환
            }
        } catch (Exception e) {
            dataVO.setSuccess(false); // 서버 오류
            dataVO.setMessage("서버 오류: " + e.getMessage());
        }
        return dataVO;
    }

    @PostMapping("/refresh")
    public DataVO getRefresh(@RequestBody Map<String, String> map) {
        try {
            //  log.info("refresh 들어왓네요");
            String refreshToken = map.get("refreshToken");
            
            //  1. 만료 여부 검사
            if(jwtUtil.isTokenExpired(refreshToken)){
                return new DataVO(false, "refreshToken 만료", null);
            }

            //  2. 사용자 ID 추출
            String m_id = jwtUtil.validateAndExtractUserId(refreshToken);

            //  DB에 m_id 가지고 refresh token 을 확인(체크)
            RefreshVO refreshVO = membersService.getRefreshToken(m_id); 

            //  DB의 refreshToken과 유저가 보낸 refreshToken이 같아야 accessToken 발급
            if(refreshVO == null || refreshToken.equals((refreshVO.getRefresh_token()))){
                return new DataVO(false, "refreshToken 불일치", null);
            }

            //  새로운 accessToken, refreshToken 발급
            String newAccessToken = jwtUtil.gererateAccessToken(m_id);
            String newRefreshToken = jwtUtil.gererateRefreshToken(m_id);

            //  newRefreshToken을 DB에 갱신하자, 만기
            membersService.saveRefreshToken(m_id, newRefreshToken, jwtUtil.extractExpiration(newRefreshToken));
            /* 
            Map<String, String> map2 = new HashMap<>();
            map2.put("accessToken", newAccessToken);
            map2.put("refreshToken", newRefreshToken);
            return new DataVO(true, "재발급 성공",  map2);
            */
            //  위의 주석과 같음
            return new DataVO(true, "재발급 성공", Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
            ));
        } catch (Exception e) {
            return new DataVO(false, "재발급 실패", null);
        }
        
    }
    
    
}
    