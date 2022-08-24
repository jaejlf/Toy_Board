package com.filling.good.domain.issue.repository;

import com.filling.good.domain.issue.entity.Issue;
import com.filling.good.domain.issue.enumerate.IssueCategory;
import com.filling.good.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByWriter(User writer);
    List<Issue> findByCategory(IssueCategory category);
}
