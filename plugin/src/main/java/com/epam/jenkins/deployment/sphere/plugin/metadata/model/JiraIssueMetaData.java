package com.epam.jenkins.deployment.sphere.plugin.metadata.model;

import lombok.Data;

@Data
public class JiraIssueMetaData {

    private final String id;

    private final String title;

    private final String status;

}
