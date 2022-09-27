package it.bz.beacon.api.service.issue;

import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueComment;
import it.bz.beacon.api.db.model.IssueSolution;
import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.db.repository.IssueCommentRepository;
import it.bz.beacon.api.exception.db.IssueCommentNotFoundException;
import it.bz.beacon.api.model.BaseMessage;
import it.bz.beacon.api.model.IssueCommentCreation;
import it.bz.beacon.api.model.IssueCommentUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class IssueCommentService implements IIssueCommentService {

    @Autowired
    private IssueCommentRepository repository;

    @Override
    @Transactional
    public List<IssueComment> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Map<Long, IssueComment> findLastCommentByIssuesMap(List<Issue> issues) {
        return issues.stream()
                .map(issue -> repository.findFirstByIssueOrderByCreatedAtDesc(issue))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(IssueComment::getIssueId, Function.identity()));
    }

    @Override
    @Transactional
    public IssueComment findLastCommentByIssue(Issue issue) {
        return repository.findFirstByIssueOrderByCreatedAtDesc(issue);
    }

    @Override
    @Transactional
    public IssueComment find(long id) {
        return repository.findById(id).orElseThrow(IssueCommentNotFoundException::new);
    }

    @Override
    public IssueComment create(Issue issue, IssueSolution issueSolution) {
        IssueCommentCreation issueCommentCreation = new IssueCommentCreation();
        issueCommentCreation.setComment(issueSolution.getSolution() +
                (issueSolution.getSolutionDescription() != null
                        && !issueSolution.getSolutionDescription().equals(issueSolution.getSolution())
                        && !issueSolution.getSolutionDescription().trim().isEmpty() ?
                        "\n\n" + issueSolution.getSolutionDescription() : ""));

        return create(issue, issueCommentCreation);
    }

    @Override
    public List<IssueComment> findAllComments(Issue issue) {
        return repository.findAllByIssueOrderByCreatedAt(issue);
    }

    @Override
    public IssueComment create(Issue issue, IssueCommentCreation issueCommentCreation) {
        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        IssueComment issueComment = IssueComment.create(issueCommentCreation, issue, authorizedUser);
        return repository.save(issueComment);
    }

    @Override
    public IssueComment update(Issue issue, long commentId, IssueCommentUpdate issueCommentUpdate) {
        IssueComment issueComment = repository.findByIdAndIssue(commentId, issue).orElseThrow(IssueCommentNotFoundException::new);
        issueComment.setComment(issueCommentUpdate.getComment());
        return repository.save(issueComment);
    }

    @Override
    public BaseMessage delete(Issue issue, long commentId) {
        return repository.findByIdAndIssue(commentId, issue).map(
                issueComment -> {
                    repository.delete(issueComment);

                    return new BaseMessage("Issue comment deleted");
                }
        ).orElseThrow(IssueCommentNotFoundException::new);
    }

    @Override
    public List<String> findAllUserEmailsByIssue(Issue issue) {
        return repository.findAllUserEmailsByIssue(issue);
    }


}
