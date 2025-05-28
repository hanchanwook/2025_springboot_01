package com.ict.edu01.jwt;

import java.util.Map;
import com.ict.edu01.members.vo.MembersVO;


public interface JwtService {

    Map<String, String> login(MembersVO mvo);

    String getuserIdFromToken(String token);
}
