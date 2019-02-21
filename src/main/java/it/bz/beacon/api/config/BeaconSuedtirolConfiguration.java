package it.bz.beacon.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "it.bz.beacon")
public class BeaconSuedtirolConfiguration {

    @NotBlank
    private String allowedOrigins;

    @NotBlank
    private String issueEmailFrom;

    @NotBlank
    private String issueEmailTo;

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String getIssueEmailFrom() {
        return issueEmailFrom;
    }

    public void setIssueEmailFrom(String issueEmailFrom) {
        this.issueEmailFrom = issueEmailFrom;
    }

    public String getIssueEmailTo() {
        return issueEmailTo;
    }

    public void setIssueEmailTo(String issueEmailTo) {
        this.issueEmailTo = issueEmailTo;
    }
}
