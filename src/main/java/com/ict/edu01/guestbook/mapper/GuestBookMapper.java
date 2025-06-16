package com.ict.edu01.guestbook.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ict.edu01.guestbook.vo.GuestBookVO;
import com.nimbusds.jose.util.Resource;

@Mapper
public interface GuestBookMapper {

    List<GuestBookVO> guestbooklist();    
    
    GuestBookVO guestbookdetail(String gb_idx);   
    // 파일 업로드 처리  
    int guestbookwrite(GuestBookVO gVO);    

    int guestbookdelete(String gb_idx);

    int guestbookupdate(String gb_idx, GuestBookVO gVO);
    // 이미지 미리보기용
    Resource getGuestBookImage(String gb_f_name);
    // 파일 다운로드용
    Resource downloadGuestBookFile(String gb_f_name);
}
