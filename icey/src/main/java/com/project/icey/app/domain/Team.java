package com.project.icey.app.domain;

//테스트용으로 만듬

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity // ← 이게 꼭 있어야 함!
public class Team {
    @Id
    private Long id;

    public Team() {}
    public Team(Long id) {
        this.id = id;
    }

    // getter/setter도 필요하면 추가!
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
