package com.project.icey.app.controller;

import com.project.icey.app.domain.User;
import com.project.icey.app.dto.CustomUserDetails;
import com.project.icey.app.service.CardService;
import com.project.icey.app.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal; //
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    /* GET /api/cards  전역 템플릿 목록(원본)
    @GetMapping
    public List<CardResponse> list(@AuthenticationPrincipal User loginUser){
        return cardService.listTemplates(loginUser.getId());
    }

    */

    /* GET /api/cards  전역 템플릿 목록 */ //얘에서 문제 생겼는데 확인!!!!!!! 지피티가 알려준 방법인데 여전히 오류!!!!!!
    @GetMapping
    public List<CardResponse> list(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getUser().getId();
        return cardService.listTemplates(userId);
    }

    /* POST /api/cards   템플릿 생성 */
    @PostMapping
    public CardResponse create(@RequestBody CardRequest req,
                               @AuthenticationPrincipal User loginUser){
        return cardService.createTemplate(loginUser, req);
    }


    /* PATCH /api/cards/{id}  템플릿 수정 */
    @PatchMapping("/{id}")
    public CardResponse update(@PathVariable Long id,
                               @RequestBody CardRequest req,
                               @AuthenticationPrincipal User loginUser){
        return cardService.update(id, loginUser.getId(), req);
    }

    /* DELETE /api/cards/{id}  템플릿 삭제 */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal User loginUser){
        cardService.delete(id, loginUser.getId());
    }


    /* GET /api/teams/{teamId}/cards */
    @GetMapping("/teams/{teamId}/cards")
    public List<CardResponse> teamCards(@PathVariable Long teamId){
        return cardService.listTeamCards(teamId);
    }

    /* PUT /api/teams/{teamId}/cards/my-card?templateId=123 */
    @PutMapping("/teams/{teamId}/cards/my-card")
    public CardResponse changeMyCard(@PathVariable Long teamId,
                                     @RequestParam Long templateId,
                                     @AuthenticationPrincipal User me){
        return cardService.applyTemplate(teamId, templateId, me);
    }

    /* 예시: POST /api/cards?teamId=42  → 새로 만들고 곧장 팀 42에 적용 */
    @PostMapping(params = "teamId")
    public CardResponse createAndApply(@RequestParam Long teamId,
                                       @RequestBody CardRequest req,
                                       @AuthenticationPrincipal User me){
        return cardService.createAndApply(teamId, req, me);
    }
}
