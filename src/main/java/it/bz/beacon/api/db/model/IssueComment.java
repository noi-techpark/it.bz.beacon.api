package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class IssueComment extends AuditModel {
    private static final long serialVersionUID = -980500448424475750L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;

    @JsonIgnore
    @ManyToOne
    private Issue issue;

    @NotEmpty
    @NotNull
    private String userUsername;

    @NotEmpty
    @NotNull
    private String userName;

    @NotNull
    @ManyToOne
    private User user;

    @NotEmpty
    @NotNull
    private String comment;

    public static IssueComment create(IssueSolution issueSolution, Issue issue, User authorizedUser) {
        IssueComment issueComment = new IssueComment();
        issueComment.issue = issue;
        issueComment.userUsername = authorizedUser.getUsername();
        issueComment.userName = authorizedUser.getName() + " " + authorizedUser.getSurname();
        issueComment.setUser(authorizedUser);

        issueComment.comment = issueSolution.getSolution() +
                (issueSolution.getSolutionDescription() != null
                        && !issueSolution.getSolutionDescription().equals(issueSolution.getSolution())
                        && !issueSolution.getSolutionDescription().trim().isEmpty() ?
                        "\n\n" + issueSolution.getSolutionDescription() : "");

        return issueComment;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Long getIssueId() {
        return issue != null ? issue.getId() : null;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
