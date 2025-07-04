package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTeamManager {

    @Id
    @GeneratedValue
    @Column(name = "TEAMMANAGER_ID")
    private long id;

    @ManyToOne
    private Team team;

    @ManyToOne
    private User user;

    //nickname은 추후 card와 연결
    private String nickname;

    @Enumerated(EnumType.STRING)  // enum을 문자열로 DB 저장
    private UserRole role;


}
