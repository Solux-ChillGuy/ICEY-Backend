package com.project.icey.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CardResponse {
    private Long id;
    private String nickname;
    private String animal;
    private String profileColor;
}