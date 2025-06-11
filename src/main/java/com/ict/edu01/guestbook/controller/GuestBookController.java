package com.ict.edu01.guestbook.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

import com.ict.edu01.guestbook.service.GuestBookService;
import com.ict.edu01.guestbook.vo.GuestBookVO;
import com.ict.edu01.members.vo.DataVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/guestbook")
public class GuestBookController {
    
    private static final Logger logger = LoggerFactory.getLogger(GuestBookController.class);
    
    @Autowired
    private GuestBookService guestBookService;

    @GetMapping("/guestbooklist")
    public DataVO searchBookName() {
        logger.info("Guestbook list request received");
        DataVO dataVO = new DataVO();
        try {
            List<GuestBookVO> list = guestBookService.guestbooklist();
            if(list == null || list.isEmpty()){
                logger.info("No guestbook data found");
                dataVO.setSuccess(true);
                dataVO.setMessage("데이터가 존재하지 않습니다.");
            } else {
                logger.info("Found {} guestbook entries", list.size());
                dataVO.setSuccess(true);
                dataVO.setMessage("데이터가 존재합니다.");
                dataVO.setData(list);
            }
        } catch (Exception e) {
            logger.error("Error while fetching guestbook list", e);
            dataVO.setSuccess(false);
            dataVO.setMessage("서버 오류: " + e.getMessage());
        }
        return dataVO;
    }

    // 이름이 일치하면 생략 가능
    @GetMapping("/guestbookdetail")
    public DataVO guestBookDetail(@RequestParam String gb_idx) {
        logger.info("Guestbook detail request received for id: {}", gb_idx);
        DataVO dataVO = new DataVO();
        try {
            GuestBookVO gvo = guestBookService.guestbookdetail(gb_idx);
            // 여러 개 있는 것 중 하나를 선택했는데 없다는 결과는 오류
            if (gvo == null){
                logger.info("No guestbook entry found for id: {}", gb_idx);
                dataVO.setSuccess(false);
                dataVO.setMessage("데이터가 존재하지 않습니다.");
            } else {
                logger.info("Found guestbook entry for id: {}", gb_idx);
                dataVO.setSuccess(true);
                dataVO.setMessage("데이터가 존재합니다.");
                dataVO.setData(gvo);
            }
            
        } catch (Exception e) {
            logger.error("Error while fetching guestbook detail for id: " + gb_idx, e);
            dataVO.setSuccess(false);
            dataVO.setMessage("서버 오류: " + e.getMessage());
        }
        return dataVO;
    }

    @PostMapping("/guestbookwrite")
    public DataVO guestBookWrite(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("guestbook") GuestBookVO guestbook) {
        logger.info("Guestbook write request received");
        DataVO dataVO = new DataVO();
        try {
            // 파일이 있는 경우 처리
            if (file != null && !file.isEmpty()) {
                logger.info("File received: {}", file.getOriginalFilename());
                // TODO: 파일 처리 로직 추가
            }
            
            // 방명록 데이터 처리
            int result = guestBookService.guestbookwrite(guestbook);
            if (result > 0) {
                logger.info("Guestbook write successful");
                dataVO.setSuccess(true);
                dataVO.setMessage("방명록이 등록되었습니다.");
            } else {
                logger.info("Guestbook write failed");
                dataVO.setSuccess(false);
                dataVO.setMessage("방명록 등록에 실패했습니다.");
            }

        } catch (Exception e) {
            logger.error("Error while writing guestbook", e);
            dataVO.setSuccess(false);
            dataVO.setMessage("서버 오류: " + e.getMessage());
        }
        return dataVO;
    }



}
