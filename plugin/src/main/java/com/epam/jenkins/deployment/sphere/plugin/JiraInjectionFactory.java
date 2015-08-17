package com.epam.jenkins.deployment.sphere.plugin;

import jenkins.model.Jenkins;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.metadata.jira.JiraMetaDataCollector;
import com.google.inject.Singleton;

@Log
@Singleton
public class JiraInjectionFactory {

    private JiraMetaDataCollector jiraMetaDataCollector;

    public JiraMetaDataCollector getJiraMetaDataCollector() {
        if (Jenkins.getInstance().getPlugin(PluginConstants.JENKINS_JIRA_PLUGIN_NAME) != null) {
            jiraMetaDataCollector = jiraMetaDataCollector == null ? new JiraMetaDataCollector() : null;
        } else {
            log.warning("JIRA Plugin, which should provide connection to JIRA, is not installed, so JIRA metadata cannot be obtained.");
        }
        return jiraMetaDataCollector;
    }
}
