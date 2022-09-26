package it.bz.beacon.api.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class IssueCommentCreation {

    @NotEmpty
    @NotNull
    private String comment;

    private boolean closeOnComment = false;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isCloseOnComment() {
        return closeOnComment;
    }

    public void setCloseOnComment(boolean closeOnComment) {
        this.closeOnComment = closeOnComment;
    }
}
