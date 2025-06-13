package com.ict.edu01.guestbook.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ict.edu01.guestbook.vo.GuestBookVO;

@Mapper
public interface GuestBookMapper {

    List<GuestBookVO> guestbooklist();    
    
    GuestBookVO guestbookdetail(String gb_idx);   
    // 파일 업로드 처리 메서드    
    int guestbookwrite(GuestBookVO gVO);    

    int guestbookdelete(String gb_idx);

    int guestbookupdate(String gb_idx, GuestBookVO gVO);
}
