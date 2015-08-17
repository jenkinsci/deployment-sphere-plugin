package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain;

import org.joda.time.DateTime;
import lombok.Data;

import com.epam.jenkins.deployment.sphere.plugin.metadata.model.MetaData;

@Data
public class Build {
    private String applicationName;
    private String buildVersion;
    private String buildUrl;
    private Long buildNumber;
    private DateTime builtAt;
    private MetaData metaData;
}
