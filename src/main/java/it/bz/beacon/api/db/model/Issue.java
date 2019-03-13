package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.bz.beacon.api.model.IssueCreation;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Issue extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @ManyToOne
    private BeaconData beaconData;

    private String problem;

    @Lob
    private String problemDescription;
    private String reporter;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    private IssueSolution solution;

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
}
