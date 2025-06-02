package com.ict.edu01.members.mapper;


import org.apache.ibatis.annotations.Mapper;
import com.ict.edu01.members.vo.MembersVO;
import com.ict.edu01.members.vo.RefreshVO;

@Mapper
public interface MembersMapper {
    
    MembersVO getLogin(MembersVO mvo);   //  로그인
    
    int getRegister(MembersVO mvo);   //  회원가입
    
    MembersVO getMyPage(String m_id);   //  나의 정보 조회

    MembersVO findUserById(String m_id) ;   //  아이디 조회

    void saveRefreshToken(RefreshVO refreshVO);   //  리프레시 토큰 저장

    RefreshVO getRefreshToken(String m_id);
}