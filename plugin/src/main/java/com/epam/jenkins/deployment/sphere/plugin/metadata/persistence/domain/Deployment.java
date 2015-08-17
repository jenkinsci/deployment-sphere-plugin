package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain;


import org.joda.time.DateTime;
import lombok.Data;

@Data
public class Deployment {
    private String key;
    private Build build;
    private Environment environment;
    private DateTime deployedAt;
}
