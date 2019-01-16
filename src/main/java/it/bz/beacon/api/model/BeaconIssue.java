package it.bz.beacon.api.model;

import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueSolution;

import java.util.Date;

public class BeaconIssue {

    private long id;
    private Beacon beacon;

    private String problem;
    private String problemDescription;
    private String reporter;
    private Date reportDate;

    private IssueSolution solution;

    public static BeaconIssue fromIssue(Issue issue, Beacon beacon) {
        BeaconIssue beaconIssue = new BeaconIssue();
        beaconIssue.setId(issue.getId());
        beaconIssue.setBeacon(beacon);
        beaconIssue.setProblem(issue.getProblem());
        beaconIssue.setProblemDescription(issue.getProblemDescription());
        beaconIssue.setReportDate(issue.getReportDate());
        beaconIssue.setSolution(issue.getSolution());

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

    public IssueSolution getSolution() {
        return solution;
    }

    public void setSolution(IssueSolution solution) {
        this.solution = solution;
    }
}
