package com.filling.good.domain.issue.entity;

import com.filling.good.domain.issue.enumerate.IssueCategory;
import com.filling.good.domain.issue.enumerate.IssueStatus;
import com.filling.good.domain.issue.enumerate.IssueTag;
import com.filling.good.domain.supporter.entity.Supporter;
import com.filling.good.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.filling.good.domain.issue.enumerate.IssueStatus.OPEN;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @Enumerated(EnumType.STRING)
    private IssueStatus status = OPEN;

    @Enumerated(EnumType.STRING)
    private IssueCategory category;

    @Enumerated(EnumType.STRING)
    private IssueTag tag;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer")
    private User writer;

    @OneToMany
    @JoinColumn(name = "supporters")
    private List<Supporter> supporters = new ArrayList<>();

    public Issue(String title, String content, IssueCategory category, IssueTag tag, User writer) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.tag = tag;
        this.writer = writer;
    }

    public void changeStatus(IssueStatus status) {
        this.status = status;
    }

}
