package com.ict.edu01.guestbook.service;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public int guestbookwrite(GuestBookVO gVO, MultipartFile file) {
        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            String uuid = java.util.UUID.randomUUID().toString();
            String ext = org.springframework.util.StringUtils.getFilenameExtension(oldFileName);
            String newFileName = uuid + (ext != null ? "." + ext : "");
            String uploadPath = "C:/workspaces/springboot/edu01/upload/guestbook/";
            java.nio.file.Path savePath = java.nio.file.Paths.get(uploadPath, newFileName);
            try {
                java.nio.file.Files.createDirectories(savePath.getParent());
                file.transferTo(savePath.toFile());
                gVO.setGb_f_name(newFileName);
                gVO.setGb_old_f_name(oldFileName);
            } catch (Exception e) {
                throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
            }
        }
        
        return guestBookMapper.guestbookwrite(gVO);
    }

    // 해당 방명록 데이터 삭제 처리
    @Override
    public int guestbookdelete(String gb_idx) {
        return guestBookMapper.guestbookdelete(gb_idx);
    }

    @Override
    public int guestbookupdate(String gb_idx, GuestBookVO gVO, MultipartFile file) {
        // 업데이트 처리
        if(file != null && !file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            String uuid = java.util.UUID.randomUUID().toString();
            String ext = org.springframework.util.StringUtils.getFilenameExtension(oldFileName);
            String newFileName = uuid + (ext != null ? "." + ext : "");
            String uploadPath = "C:/workspaces/springboot/edu01/upload/guestbook/";
            java.nio.file.Path savePath = java.nio.file.Paths.get(uploadPath, newFileName);
            try {
                java.nio.file.Files.createDirectories(savePath.getParent());
                file.transferTo(savePath.toFile());
                gVO.setGb_f_name(newFileName);
                gVO.setGb_old_f_name(oldFileName);
            } catch (Exception e) {
                throw new RuntimeException("파일 업데이트 중 오류가 발생했습니다.", e);
            }
        } 
        return guestBookMapper.guestbookupdate(gb_idx, gVO);
    }

    // 이미지 미리보기용
    @Override
    public Resource getGuestBookImage(String gb_f_name) {
        String uploadPath = "C:/workspaces/springboot/edu01/upload/guestbook/";
        File file = new File(uploadPath + gb_f_name);
        try {
            if (file.exists() && file.isFile()) {
                return new UrlResource(file.toURI());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 파일 다운로드용
    @Override
    public Resource downloadGuestBookFile(String gb_f_name) {
        String uploadPath = "C:/workspaces/springboot/edu01/upload/guestbook/";
        File file = new File(uploadPath + gb_f_name);
        try {
            if (file.exists() && file.isFile()) {
                return new UrlResource(file.toURI());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }



    
    


    
}
