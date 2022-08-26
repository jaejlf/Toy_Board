package com.filling.good.domain.keep.entity;

import com.filling.good.domain.issue.entity.Issue;
import com.filling.good.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keepId;

    @ManyToOne
    @JoinColumn(name = "keeper")
    private User keeper;

    @ManyToOne
    @JoinColumn(name = "issue")
    private Issue issue;

    private String keepTitle;
    private String keepContent;

}
