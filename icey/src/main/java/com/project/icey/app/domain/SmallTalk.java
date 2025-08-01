package com.project.icey.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmallTalk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;
    private String tip;
    private String answer; // 사용자가 작성하는 답변

    private QuestionType questionType;

    @Column(name = "is_show", nullable = false)
    private boolean show;

    @Column(nullable = false)
    private boolean wasShown = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "small_talk_list_id")
    private SmallTalkList smallTalkList;
}