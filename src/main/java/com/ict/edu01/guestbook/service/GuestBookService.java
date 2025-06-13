package com.ict.edu01.guestbook.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ict.edu01.guestbook.vo.GuestBookVO;

public interface GuestBookService {

    List<GuestBookVO> guestbooklist();

    GuestBookVO guestbookdetail(String gb_idx); 
    // 파일 업로드 처리 메서드
    int guestbookwrite(GuestBookVO gVO, MultipartFile file);    

    int guestbookdelete(String gb_idx);

    int guestbookupdate(String gb_idx, GuestBookVO gVO, MultipartFile file);

    
} 