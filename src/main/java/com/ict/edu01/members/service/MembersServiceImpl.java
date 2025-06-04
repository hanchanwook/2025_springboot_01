package com.ict.edu01.members.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ict.edu01.members.mapper.MembersMapper;
import com.ict.edu01.members.vo.MembersVO;
import com.ict.edu01.members.vo.RefreshVO;

@Service
public class MembersServiceImpl implements MembersService {

    // MemberMapper를 주입받아야 한다.
    // @Autowired
    // private MemberMapper memberMapper;
    @Autowired
    private MembersMapper membersMapper;

    @Override
    public MembersVO getLogin(MembersVO mvo) {   //  로그인
        return membersMapper.getLogin(mvo);
        // DB에서 mvo에 해당하는 회원 정보를 조회한다.
        // return memberMapper.getLogin(mvo);    
        // 임시로 null을 반환 (실제 DB 연동 시에는 위의 주석을 해제하고 사용)
    }

    @Override
    public int getRegister(MembersVO mvo) {   //  회원가입
        return membersMapper.getRegister(mvo);
    }

    @Override
    public MembersVO getMyPage(String m_id) {   //  나의 정보 조회
        return membersMapper.getMyPage(m_id);
    }

    @Override
    public void saveRefreshToken(String m_id, String refreshToken, Date expiry_date) {   //  리프레시 토큰 저장
        membersMapper.saveRefreshToken(new RefreshVO(m_id, refreshToken, expiry_date));
    }

    @Override
    public RefreshVO getRefreshToken(String m_id) {
        return membersMapper.getRefreshToken(m_id);
    }

    @Override
    public void getRegister2(MembersVO mvo) {
        membersMapper.getRegister2(mvo);
    }       


}
