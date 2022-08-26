package com.filling.good.domain.supporter.entity;

import com.filling.good.domain.issue.entity.Issue;
import com.filling.good.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Supporter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supporterId;

    @ManyToOne
    @JoinColumn(name = "supporter")
    private User supporter;

    @ManyToOne
    @JoinColumn(name = "issue")
    private Issue issue;

    public Supporter(User supporter, Issue issue) {
        this.supporter = supporter;
        this.issue = issue;
    }

}
