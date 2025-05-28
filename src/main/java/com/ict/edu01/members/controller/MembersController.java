package com.ict.edu01.members.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ict.edu01.members.service.MembersService;
import com.ict.edu01.members.vo.DataVO;
import com.ict.edu01.members.vo.MembersVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/members")
public class MembersController {

    @Autowired
    private MembersService membersService;

    @GetMapping("/hello")
    public String getHello() {
        return "Hello, SpringBoot!";
    }

    @PostMapping("/login")
    public DataVO getLogin(@RequestBody MembersVO mvo) {

        DataVO dataVO = new DataVO();

        try {
            // mvo에 담긴 아이디와 비밀번호로 로그인 처리
            // DB에 가서 m_id 와 m_pw가 맞는지 확인한다.
            MembersVO membersVO = membersService.getLogin(mvo);
            
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

            if (membersVO == null) {
                dataVO.setSuccess(false); // 로그인 실패
                dataVO.setMessage("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
            } else {
                dataVO.setSuccess(true); // 로그인 성공
                dataVO.setMessage("로그인 성공");
                dataVO.setData(membersVO); // 로그인한 회원 정보 반환
            }

            
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지 반환
            dataVO.setSuccess(false); // 로그인 실패
            dataVO.setMessage("서버 오류: " + e.getMessage());
        
        }
        return dataVO; // DataVO 객체 반환 
    }

    @PostMapping("/register")
    public DataVO getRegister(@RequestBody MembersVO mvo) {
        DataVO dataVO = new DataVO();
        try {
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

    @PostMapping("/mypage")
    public DataVO getMyPage(@RequestBody String m_idx) {
        DataVO dataVO = new DataVO();
        try {
            // m_idx에 해당하는 회원 정보를 가져온다.
            MembersVO mvo = membersService.getMyPage(m_idx);
            
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
    
}
    