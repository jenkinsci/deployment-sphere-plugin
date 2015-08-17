package com.epam.jenkins.deployment.sphere.plugin.metadata.model;

import java.util.Comparator;

import com.google.inject.Singleton;

@Singleton
public class JiraIssueIdComparator implements Comparator<JiraIssueMetaData> {

    @Override
    public int compare(JiraIssueMetaData issue1, JiraIssueMetaData issue2) {
        return issue1.getId().compareTo(issue2.getId());
    }
}
