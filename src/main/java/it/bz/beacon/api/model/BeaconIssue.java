package it.bz.beacon.api.model;

import it.bz.beacon.api.db.model.Issue;

import java.util.Date;

public class BeaconIssue {

    private long id;
    private Beacon beacon;

    private String problem;
    private String problemDescription;
    private String reporter;
    private Date reportDate;

    private boolean resolved = false;
    private String solution;
    private String solutionDescription;
    private Date resolveDate;

    public static BeaconIssue fromIssue(Issue issue, Beacon beacon) {
        BeaconIssue beaconIssue = new BeaconIssue();
        beaconIssue.setId(issue.getId());
        beaconIssue.setBeacon(beacon);
        beaconIssue.setProblem(issue.getProblem());
        beaconIssue.setProblemDescription(issue.getProblemDescription());
        beaconIssue.setReportDate(issue.getReportDate());
        if (issue.getSolution() != null) {
            beaconIssue.setResolved(true);
            beaconIssue.setSolution(issue.getSolution().getSolution());
            beaconIssue.setSolutionDescription(issue.getSolution().getSolution());
            beaconIssue.setResolveDate(issue.getSolution().getCreatedAt());
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
}
