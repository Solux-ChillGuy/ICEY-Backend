package com.project.icey.app.domain;

import com.project.icey.app.domain.User;
import com.project.icey.app.domain.Team;
import jakarta.persistence.*;
import lombok.*;
//아래는 Hibernate 관련 어노테이션인데 import 하는 게 나은지 고유로 만드는 게 나은지 물어보기
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자 접근 제한을 protected로 설정하여 외부에서 직접 생성할 수 없도록 함
@Table(name = "CARD")
public class Card {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARD_ID")
    private Long id;

    // FK : USER_ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    // FK : TEAM_ID (NULL = 템플릿)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    // FK : ORIGIN_CARD_ID (자기참조, NULL 허용). 혹시 나중에 카드 리스트에서 어떤 팀에서 사용되는지 알고 싶을 때 사용하기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORIGIN_CARD_ID")
    private Card origin;

    @Column(name = "ADJECTIVE", length = 30)
    private String adjective;

    @Column(name = "ANIMAL", length = 30)
    private String animal;

    @Column(name = "MBTI", length = 4)
    private String mbti;

    @Column(name = "HOBBY", length = 100)
    private String hobby;

    @Column(name = "SECRET_TIP", length = 100)
    private String secretTip;

    @Column(name = "TMI", length = 100)
    private String tmi;

    @Column(name = "PROFILE_COLOR", length = 20)
    private String profileColor;

    @Column(name = "NICKNAME", length = 60)
    private String nickname;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;


    public void regenerateNickname() {
        this.nickname = String.join(" ", adjective, profileColor, animal);
    }

    @Builder
    private Card(User user, Team team, String adjective, String animal,
                 String mbti, String hobby, String secretTip, String tmi,
                 String profileColor) {
        this.user = user;
        this.team = team;
        this.adjective = adjective;
        this.animal = animal;
        this.mbti = mbti;
        this.hobby = hobby;
        this.secretTip = secretTip;
        this.tmi = tmi;
        this.profileColor = profileColor;
        regenerateNickname();
    }
}
