package it.bz.beacon.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Configuration
@ConfigurationProperties(prefix = "it.bz.beacon")
public class BeaconSuedtirolConfiguration {

    @NotBlank
    private String allowedOrigins;

    @NotBlank
    private String issueEmailFrom;

    @NotBlank
    private String issueEmailTo;

    @NotBlank
    private String uuid;

    @NotBlank
    private String namespace;

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

    public UUID getUuid() {
        if (uuid == null) {
            return null;
        }
        return UUID.fromString(uuid);
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
