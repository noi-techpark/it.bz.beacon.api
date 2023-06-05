// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.bz.beacon.api.model.IssueCreation;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Issue extends AuditModel {
    private static final long serialVersionUID = -1129634525820294443L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @ManyToOne
    private BeaconData beaconData;

    private String problem;

    @Type(type="org.hibernate.type.StringType")
    private String problemDescription;
    private String reporter;

    private boolean resolved;
    private String resolver;

    private Long ticketId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date resolveDate;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    private IssueSolution solution;

    @Temporal(TemporalType.TIMESTAMP)
    @Formula("(select issue_comment.updated_at from issue_comment where issue_comment.issue_id = id order by updated_at desc limit 1)")
    private Date lastCommentDate;

    public static Issue create(BeaconData beaconData, IssueCreation issueCreation) {
        Issue issue = new Issue();
        issue.setBeaconData(beaconData);
        issue.setProblem(issueCreation.getProblem());
        issue.setProblemDescription(issueCreation.getProblemDescription());
        issue.setReporter(issueCreation.getReporter());

        return issue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BeaconData getBeaconData() {
        return beaconData;
    }

    public void setBeaconData(BeaconData beaconData) {
        this.beaconData = beaconData;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public Date getReportDate() {
        return getCreatedAt();
    }

    public IssueSolution getSolution() {
        return solution;
    }

    public void setSolution(IssueSolution solution) {
        this.solution = solution;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getResolver() {
        return resolver;
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }

    public Date getResolveDate() {
        return resolveDate;
    }

    public void setResolveDate(Date resolveDate) {
        this.resolveDate = resolveDate;
    }

    public Date getLastCommentDate() {
        return lastCommentDate;
    }

    public void setLastCommentDate(Date lastCommentDate) {
        this.lastCommentDate = lastCommentDate;
    }
}
