package com.filling.good.domain.issue.repository;

import com.filling.good.domain.issue.entity.Issue;
import com.filling.good.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    Optional<Issue> findByIssueIdAndWriter(Long issueId, User writer);
}
