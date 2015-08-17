package com.epam.jenkins.deployment.sphere.plugin.metadata.collector.jira;

import hudson.model.TaskListener;
import hudson.model.AbstractBuild;

import java.io.PrintStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epam.jenkins.deployment.sphere.plugin.PluginJenkinsRule;
import com.epam.jenkins.deployment.sphere.plugin.metadata.collector.InvolvedBuildChangesCollector;
import com.epam.jenkins.deployment.sphere.plugin.metadata.jira.JiraMetaDataCollector;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.JiraIssueMetaData;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class JiraMetaDataCollectorTest {

    @Rule
    public PluginJenkinsRule jenkinsRule = new PluginJenkinsRule();

    @Mock
    private InvolvedBuildChangesCollector buildChangesCollector;

    @Mock
    private AbstractBuild<?, ?> currentBuild;

    @Mock
    private TaskListener taskListener;

    @InjectMocks
    private JiraMetaDataCollector jiraMetaDataCollector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(taskListener.getLogger()).thenReturn(new PrintStream(System.out));
    }

    @Test
    public void shouldReturnAnEmptyListWhenJiraPluginIsNotInstalled() {

        Set<JiraIssueMetaData> jiraIssues = jiraMetaDataCollector.collect(currentBuild, taskListener);

        assertTrue("The list of Jira issues from involved builds should be empty.", jiraIssues.isEmpty());
    }

}
