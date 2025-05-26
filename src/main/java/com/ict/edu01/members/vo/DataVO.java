package com.ict.edu01.members.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataVO {

    // 클라이언트에게 결과를 보낼 때 사용하는 VO
    private boolean success; // 성공 여부
    private String message; // 메시지
    private Object data; // 데이터 (예: MembersVO 객체)

    // 생성자


}