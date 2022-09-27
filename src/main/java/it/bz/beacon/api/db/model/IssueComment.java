package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.bz.beacon.api.model.IssueCommentCreation;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class IssueComment extends AuditModel {
    private static final long serialVersionUID = -980500448424475750L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    @JsonIgnore
    private User user;

    @NotEmpty
    @NotNull
    private String comment;

    public static IssueComment create(IssueCommentCreation issueCommentCreation, Issue issue, User authorizedUser) {
        IssueComment issueComment = new IssueComment();
        issueComment.issue = issue;
        issueComment.userUsername = authorizedUser.getUsername();
        issueComment.userName = authorizedUser.getName() + " " + authorizedUser.getSurname();
        issueComment.setUser(authorizedUser);
        issueComment.comment = issueCommentCreation.getComment();

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

    public Date getCreateDate() {
        return getCreatedAt();
    }

    public Date getUpdateDate() {
        return !getCreatedAt().equals(getUpdatedAt()) ? getUpdatedAt() : null;
    }
}
