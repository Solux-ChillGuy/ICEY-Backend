package com.project.icey.app.dto;

//사용자가 카드 생성 시 입력하는 정보를 담는 DTO!
import lombok.Data;

@Data
public class CardRequest {
    private String adjective;
    private String animal;
    private String mbti;
    private String hobby;
    private String secretTip;
    private String tmi;
    private String profileColor;
}
