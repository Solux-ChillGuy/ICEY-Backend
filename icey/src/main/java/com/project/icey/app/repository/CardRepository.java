package com.project.icey.app.repository;

import com.project.icey.app.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    // 팀ID 없으면 템플릿 목록
    List<Card> findByUserIdAndTeamIsNull(Long userId);

    // 팀 명함 목록
    List<Card> findByTeamId(Long teamId);

    Optional<Card> findByTeamIdAndUserId(Long teamId, Long userId);

}
