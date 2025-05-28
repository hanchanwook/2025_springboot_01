package com.ict.edu01.members.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ict.edu01.members.mapper.MembersMapper;
import com.ict.edu01.members.vo.MembersVO;

@Service
public class MembersServiceImpl implements MembersService {

    // MemberMapper를 주입받아야 한다.
    // @Autowired
    // private MemberMapper memberMapper;
    @Autowired
    private MembersMapper memberMapper;

    @Override
    public MembersVO getLogin(MembersVO mvo) {
        // DB에서 mvo에 해당하는 회원 정보를 조회한다.
        // return memberMapper.getLogin(mvo);    
        // 임시로 null을 반환 (실제 DB 연동 시에는 위의 주석을 해제하고 사용)
        return memberMapper.getLogin(mvo);
    }

    @Override
    public int getRegister(MembersVO mvo) {
        return memberMapper.getRegister(mvo);
    }

    @Override
    public MembersVO getMyPage(String m_idx) {
        return memberMapper.getMyPage(m_idx);
    }

    


    
}
