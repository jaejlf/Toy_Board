package com.filling.good.domain.issue.repository;

import com.filling.good.domain.issue.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}
