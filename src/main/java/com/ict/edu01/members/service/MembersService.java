package com.ict.edu01.members.service;

import java.util.Date;

import com.ict.edu01.members.vo.MembersVO;

public interface MembersService {
    MembersVO getLogin(MembersVO mvo);   //  로그인

    int getRegister(MembersVO mvo);   //  회원가입

    MembersVO getMyPage(String m_idx);   //  나의 정보 조회

    void saveRefreshToken(String m_id, String refreshToken, Date expiry_date);   //  리프레시 토큰 저장
 
}
