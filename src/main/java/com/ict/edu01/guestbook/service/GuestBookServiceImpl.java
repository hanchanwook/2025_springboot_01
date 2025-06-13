package com.ict.edu01.guestbook.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ict.edu01.guestbook.mapper.GuestBookMapper;
import com.ict.edu01.guestbook.vo.GuestBookVO;

@Service
public class GuestBookServiceImpl implements GuestBookService {

    @Autowired
    private GuestBookMapper guestBookMapper;

    @Override
    public List<GuestBookVO> guestbooklist() {
        return guestBookMapper.guestbooklist();
    }

    @Override
    public GuestBookVO guestbookdetail(String gb_idx) {
        
        return guestBookMapper.guestbookdetail(gb_idx);
        
    }

    @Override
    public int guestbookwrite(GuestBookVO gVO) {
        // 파일 업로드 처리
        String fileName = gVO.getGb_f_name();
        if(fileName != null && !fileName.isEmpty()) {
            String uploadDir = "C:/upload/guestbook";
            File uploadPath = new File(uploadDir);
        }
        return guestBookMapper.guestbookwrite(gVO);
    }

    @Override
    public int guestbookdelete(String gb_idx) {
        // 해당 방명록 데이터 삭제 처리
        return guestBookMapper.guestbookdelete(gb_idx);
    }

    @Override
    public int guestbookupdate(String gb_idx, GuestBookVO gVO) {
        // 업데이트 처리
        return guestBookMapper.guestbookupdate(gb_idx, gVO);
    }

    
    


    
}
