package com.project.icey.app.service;


import com.project.icey.app.domain.User;
import com.project.icey.app.domain.Card;
import com.project.icey.app.repository.CardRepository;
import com.project.icey.app.dto.CardResponse;
import com.project.icey.app.dto.CardRequest;
import com.project.icey.app.domain.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//오류들 어떻게 관리하는지 질문하기

@Service
@RequiredArgsConstructor // final 필드를 자동으로 생성자에 넣어줌
@Transactional // 트랜잭션 관리(오류 발생 시 롤백)
public class CardService {

    private final CardRepository cardRepo; // 명함 레포지토리

    /* 템플릿 목록 */
    @Transactional(readOnly = true)
    public List<CardResponse> listTemplates(Long userId) {
        // 1) DB에서 내 명함(Card)들을 리스트로 가져옴
        // 2) 각각을 CardResponse(응답 DTO)로 바꿔서
        // 3) 새 리스트로 반환
        return cardRepo.findByUserIdAndTeamIsNull(userId)
                .stream().map(this::toDto).toList(); //== .map(card -> this.toDto(card))
    }

    // 팀 전체 명함 목록 조회 (팀 아이디로 명함들 조회)
    @Transactional(readOnly = true)
    public List<CardResponse> listTeamCards(Long teamId) {
        return cardRepo.findByTeamId(teamId)
                .stream()
                .map(this::toDto)
                .toList();
    }


    /* 새 템플릿 생성 */
    public CardResponse createTemplate(User user, CardRequest req) {
        Card c = toEntity(req);
        c.setUser(user);
        return toDto(cardRepo.save(c));
    }

    /* 명함 정보 수정 */
    // 명함 아이디, 내 유저 아이디, 입력값 받아서 내 명함이면 수정
    public CardResponse update(Long cardId, Long userId, CardRequest req) {
        Card c = cardRepo.findById(cardId).orElseThrow();
        if (!c.getUser().getId().equals(userId))
            throw new IllegalArgumentException("내 명함이 아닙니다");
        apply(c, req);                      // 입력값으로 명함 정보 덮어쓰기
        c.regenerateNickname();
        return toDto(c);
    }

    /* 명함 삭제 */
    public void delete(Long cardId, Long userId) {
        Card c = cardRepo.findById(cardId).orElseThrow();
        if (!c.getUser().getId().equals(userId))
            throw new IllegalArgumentException("내 명함이 아닙니다");
        cardRepo.delete(c);
    }

    /* 1. 템플릿을 팀에 적용(교체) : 정확히는 팀 명함으로 복사 */
    // 1) 기존 팀 명함 있으면 삭제, 2) 템플릿 복사해서 팀 명함으로 저장
    public CardResponse applyTemplate(Long teamId, Long templateId, User user){
        Card template = cardRepo.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("템플릿이 없습니다"));
        // 1) 기존 카드 삭제
        cardRepo.findByTeamIdAndUserId(teamId, user.getId())
                .ifPresent(cardRepo::delete);
        // 2) 복제
        Card clone = cloneFromTemplate(template, user, teamId);
        return toDto(cardRepo.save(clone));
    }

    /* 2. 새 템플릿 만든 뒤 즉시 팀에 적용 */
    public CardResponse createAndApply(Long teamId, CardRequest req, User user){
        Card template = toEntity(req);
        template.setUser(user);
        cardRepo.save(template);                    // 전역 템플릿 저장
        return applyTemplate(teamId, template.getId(), user);
    }


    /* 유틸 입력값 → Card 엔티티로 변환 */
    // CardRequest의 각 값을 Card에 하나씩 옮겨담기
    private Card toEntity(CardRequest r){
        return Card.builder()
                .adjective(r.getAdjective())
                .animal(r.getAnimal())
                .mbti(r.getMbti())
                .hobby(r.getHobby())
                .secretTip(r.getSecretTip())
                .tmi(r.getTmi())
                .profileColor(r.getProfileColor())
                .build();
    }
    /* 유틸 Card 엔티티에 입력값들 한꺼번에 덮어씌워서 적용 */
    private void apply(Card c, CardRequest r){
        c.setAdjective(r.getAdjective());
        c.setAnimal(r.getAnimal());
        c.setMbti(r.getMbti());
        c.setHobby(r.getHobby());
        c.setSecretTip(r.getSecretTip());
        c.setTmi(r.getTmi());
        c.setProfileColor(r.getProfileColor());
    }

    /* 유틸 Card 엔티티 → 응답 DTO로 변환 */
    private CardResponse toDto(Card c){
        return new CardResponse(c.getId(), c.getNickname(),
                c.getAnimal(), c.getProfileColor());
    }

    /* 유틸 템플릿 명함을 복사해서 새 Card 만들기 */
    private Card cloneFromTemplate(Card tpl, User user, Long teamId){
        Card c = Card.builder()
                .user(user)
                .team(new Team(teamId))
                .adjective(tpl.getAdjective())
                .animal(tpl.getAnimal())
                .mbti(tpl.getMbti())
                .hobby(tpl.getHobby())
                .secretTip(tpl.getSecretTip())
                .tmi(tpl.getTmi())
                .profileColor(tpl.getProfileColor())
                .build();
        c.setOrigin(tpl);     // 원본 템플릿을 origin으로 설정(누군지 기록용. 안 쓰면 나중에 빼도 됨)
        c.regenerateNickname();
        return c;
    }

}
