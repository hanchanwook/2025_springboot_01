package com.ict.edu01.members.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ict.edu01.members.mapper.MembersMapper;
import com.ict.edu01.members.vo.MembersVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor    // final 또는 @NonNull 필드를 대상으로 생성자를 자동 생성
public class MyUserDetailService implements UserDetailsService {
    private final MembersMapper membersMapper;

    //  String username 은 ID이다.
    //  ID를 받아서 members 테이블에 해당 ID가 있는지 확인
    //  사용자 정보 넘기기 (보안인증에 필요한 정보)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MembersVO member = membersMapper.findUserById(username);
        
        return new User(member.getM_id(), member.getM_pw(),new ArrayList<>());
    }

    //  정보를 받아서 회원 가입



    
}
