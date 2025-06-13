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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor    // final 또는 @NonNull 필드를 대상으로 생성자를 자동 생성
public class MyUserDetailService implements UserDetailsService {    //  사용자 정보 넘기기 (보안인증에 필요한 정보)
    private final MembersMapper membersMapper;

    //  String username 은 ID이다.
    //  ID를 받아서 members 테이블에 해당 ID가 있는지 확인
    //  사용자 정보 넘기기 (보안인증에 필요한 정보)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {    //  사용자 정보 넘기기 (보안인증에 필요한 정보)
      try {
          log.info(username);
          MembersVO member = membersMapper.findUserById(username);        
          log.info(member +"");
          return new User(member.getM_id(), member.getM_pw(),new ArrayList<>());
      } catch (Exception e) {
       System.out.println(e);
       return null;
      }
    }





    
}
