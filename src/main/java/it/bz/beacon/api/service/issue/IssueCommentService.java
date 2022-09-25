package it.bz.beacon.api.service.issue;

import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueComment;
import it.bz.beacon.api.db.repository.IssueCommentRepository;
import it.bz.beacon.api.exception.db.IssueCommentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class IssueCommentService implements IIssueCommentService {

    @Autowired
    private IssueCommentRepository issueCommentRepository;


    @Override
    @Transactional
    public List<IssueComment> findAll() {
        return issueCommentRepository.findAll();
    }

    @Override
    @Transactional
    public Map<Long, IssueComment> findLastCommentByIssuesMap(List<Issue> issues) {
        return issues.stream()
                .map(issue -> issueCommentRepository.findFirstByIssueOrderByCreatedAtDesc(issue)).
                        collect(Collectors.toMap(IssueComment::getIssueId, Function.identity()));
    }

    @Override
    @Transactional
    public IssueComment findLastCommentByIssue(Issue issue) {
        return issueCommentRepository.findFirstByIssueOrderByCreatedAtDesc(issue);
    }

    @Override
    @Transactional
    public IssueComment find(long id) {
        return issueCommentRepository.findById(id).orElseThrow(IssueCommentNotFoundException::new);
    }
}
