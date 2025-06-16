package com.ict.edu01.guestbook.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/guestbook")
public class GuestBookController {
    
    private static final Logger logger = LoggerFactory.getLogger(GuestBookController.class);
    
    @Autowired
    private GuestBookService guestBookService;

    // 방명록 목록 불러오기
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

    // 방명록 상세 정보 불러오기
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

    // 방명록 등록
    @PostMapping("/guestbookwrite")
    public DataVO guestBookWrite(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("guestbook") GuestBookVO guestbook) {

        logger.info("Guestbook write request received");
        logger.info("Guestbook: {}", guestbook);   
        logger.info("File: {}", file);
        
        DataVO dataVO = new DataVO();
        try {
            int result = guestBookService.guestbookwrite(guestbook, file);
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

    // 방명록 삭제
    @PostMapping("/guestbookdelete")
    public DataVO guestBookDelete(@RequestParam String gb_idx) {
        logger.info("=== Guestbook Delete Request Details ===");
        logger.info("Attempting to delete guestbook entry with ID: {}", gb_idx);
        
        DataVO dataVO = new DataVO();
        try {
            logger.info("Calling service layer for deletion");
            int result = guestBookService.guestbookdelete(gb_idx);
            
            if (result > 0) {
                logger.info("Guestbook deletion successful - ID: {}, Rows affected: {}", gb_idx, result);
                dataVO.setSuccess(true);
                dataVO.setMessage("방명록이 삭제되었습니다.");
            } else {
                logger.info("Guestbook deletion failed - ID: {}, No rows affected", gb_idx);
                dataVO.setSuccess(false);
                dataVO.setMessage("방명록 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            logger.error("Error while deleting guestbook - ID: {}, Exception details:", gb_idx, e);
            dataVO.setSuccess(false);
            dataVO.setMessage("서버 오류: " + e.getMessage());
        }
        logger.info("=== End of Guestbook Delete Request ===");
        return dataVO;
    }

    // 방명록 업데이트
    @PostMapping("/guestbookupdate")
    public DataVO guestBookUpdate(@RequestPart("gb_idx") String gb_idx, @RequestPart("guestbook") GuestBookVO guestbook,
    @RequestPart(value = "file", required = false) MultipartFile file) {

        logger.info("=== Guestbook Update Request Details ===");
        logger.info("Attempting to update guestbook entry with ID: {}", gb_idx);
        logger.info("File: {}", file);
        
        DataVO dataVO = new DataVO();
        try {
        
            int result = guestBookService.guestbookupdate(gb_idx, guestbook, file);
            if (result > 0) {
                dataVO.setSuccess(true);
                dataVO.setMessage("방명록이 수정되었습니다.");
            } else {
                dataVO.setSuccess(false);
                dataVO.setMessage("방명록 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            dataVO.setSuccess(false);
            dataVO.setMessage("서버 오류: " + e.getMessage());
        }
        logger.info("=== End of Guestbook Update Request ===");
        return dataVO;
    }

    // 방명록 사진 미리보기
    @GetMapping("guestbookimage/{gb_f_name}")
    public ResponseEntity<UrlResource> guestBookImage(@PathVariable String gb_f_name) {
        try {
            Path filePath = Paths.get("C:/workspaces/springboot/edu01/upload/guestbook/", gb_f_name);
            UrlResource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            // Content-Type 자동 추론 (이미지면 image/jpeg 등으로 반환)
            String filename = resource.getFilename();
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 방명록 사진 다운로드
    @GetMapping("guestbookdownload/{gb_f_name}")
    public ResponseEntity<Resource> guestBookDownload(@PathVariable String gb_f_name) {
        try {
            Path filePath = Paths.get("C:/workspaces/springboot/edu01/upload/guestbook/", gb_f_name);
            UrlResource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String filename = resource.getFilename();
            String contentType = Files.probeContentType(filePath);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "application/octet-stream")
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
