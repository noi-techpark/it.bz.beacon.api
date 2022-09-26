package it.bz.beacon.api.model;

import io.swagger.annotations.ApiModelProperty;
import it.bz.beacon.api.db.model.Beacon;
import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueComment;

import java.util.Date;

public class BeaconIssue {

    private long id;
    private Beacon beacon;

    private String problem;
    private String problemDescription;
    private String reporter;

    @ApiModelProperty(dataType = "java.lang.Long")
    private Date reportDate;

    private boolean resolved;
    private String solution;
    private String solutionDescription;

    @ApiModelProperty(dataType = "java.lang.Long")
    private Date resolveDate;

    private String resolver;

    public static BeaconIssue fromIssue(Issue issue, Beacon beacon, IssueComment issueComment) {
        BeaconIssue beaconIssue = new BeaconIssue();
        beaconIssue.setId(issue.getId());
        beaconIssue.setBeacon(beacon);
        beaconIssue.setProblem(issue.getProblem());
        beaconIssue.setProblemDescription(issue.getProblemDescription());
        beaconIssue.setReportDate(issue.getReportDate());
        beaconIssue.setReporter(issue.getReporter());
        beaconIssue.setResolved(issue.isResolved());
        if (issue.isResolved()) {
            beaconIssue.setResolveDate(issue.getResolvedAt());
            if (issueComment != null) {
                beaconIssue.setSolution(issueComment.getComment());
                beaconIssue.setSolutionDescription(null);
                beaconIssue.setResolver(issueComment.getUserUsername());
            }
        }

        return beaconIssue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
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
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getSolutionDescription() {
        return solutionDescription;
    }

    public void setSolutionDescription(String solutionDescription) {
        this.solutionDescription = solutionDescription;
    }

    public Date getResolveDate() {
        return resolveDate;
    }

    public void setResolveDate(Date resolveDate) {
        this.resolveDate = resolveDate;
    }

    public String getResolver() {
        return resolver;
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }
}
