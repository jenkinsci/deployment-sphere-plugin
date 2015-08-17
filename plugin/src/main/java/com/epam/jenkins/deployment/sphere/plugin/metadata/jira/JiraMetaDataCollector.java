package com.epam.jenkins.deployment.sphere.plugin.metadata.jira;

import static java.lang.String.format;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.plugins.jira.JiraProjectProperty;
import hudson.plugins.jira.JiraSite;
import hudson.scm.ChangeLogSet.Entry;
import jenkins.model.Jenkins;
import lombok.extern.java.Log;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.epam.jenkins.deployment.sphere.plugin.PluginInjector;
import com.epam.jenkins.deployment.sphere.plugin.metadata.collector.Collector;
import com.epam.jenkins.deployment.sphere.plugin.metadata.collector.InvolvedBuildChangesCollector;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.JiraIssueIdComparator;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.JiraIssueMetaData;
import com.epam.jenkins.deployment.sphere.plugin.utils.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Log
@Singleton
public class JiraMetaDataCollector implements Collector<Set<JiraIssueMetaData>> {

    @Inject
    private JiraIssueIdComparator jiraIssueIdComparator;

    @Inject
    private InvolvedBuildChangesCollector buildChangesCollector;

    //
    public JiraMetaDataCollector() {
        PluginInjector.injectMembers(this);
    }

    @Override
    public Set<JiraIssueMetaData> collect(final AbstractBuild<?, ?> build,
                                          final TaskListener taskListener) {
        // We can obtain JIRA connection details from JIRA Plugin only.
        return collectJiraIssuesMetaData(build, taskListener);
    }

    private Set<JiraIssueMetaData> collectJiraIssuesMetaData(
            final AbstractBuild<?, ?> build, final TaskListener taskListener) {
        Set<String> jiraIssuesIds = getJiraIssuesIds(build, taskListener);
        Set<JiraIssueMetaData> jiraIssues = new TreeSet<JiraIssueMetaData>(
                jiraIssueIdComparator);

        for (JiraSite jiraSite : getJiraSites(taskListener)) {
            populateJiraIssues(jiraSite, jiraIssuesIds, taskListener,
                    jiraIssues);
        }

        return jiraIssues;
    }

    private Set<String> getJiraIssuesIds(final AbstractBuild<?, ?> build,
                                         final TaskListener taskListener) {
        Set<String> jiraIssuesIds = new TreeSet<>();

        for (Entry buildChange : buildChangesCollector.collect(build,
                taskListener)) {
            Set<String> issuesIds = extractJiraIssuesIdsFrom(buildChange,
                    taskListener);
            if (issuesIds != null) {
                jiraIssuesIds.addAll(issuesIds);
            }
        }
        return jiraIssuesIds;
    }

    private Set<String> extractJiraIssuesIdsFrom(final Entry buildChange,
                                                 final TaskListener taskListener) {
        Set<String> issuesIds = new TreeSet<>();

        for (Pattern pattern : getAllIssuePatterns(taskListener)) {
            Matcher matcher = pattern.matcher(buildChange.getMsg());

            while (matcher.find()) {
                if (matcher.groupCount() >= 1) {
                    String content = StringUtils.upperCase(matcher.group(1));
                    issuesIds.add(content);
                }
            }
        }
        return issuesIds;
    }

    private List<Pattern> getAllIssuePatterns(final TaskListener taskListener) {
        List<Pattern> issuePatterns = new ArrayList<>();
        for (JiraSite jiraSite : getJiraSites(taskListener)) {
            issuePatterns.add(jiraSite.getIssuePattern());
        }
        return issuePatterns;
    }

    private JiraSite[] getJiraSites(final TaskListener taskListener) {
        Jenkins jenkins = Jenkins.getInstance();
        JiraProjectProperty.DescriptorImpl descriptor = (JiraProjectProperty.DescriptorImpl) jenkins
                .getDescriptor(hudson.plugins.jira.JiraProjectProperty.class);
        descriptor.load();
        return descriptor.getSites();
    }

    private void populateJiraIssues(final JiraSite jiraSite,
                                    final Set<String> jiraIssuesIds, final TaskListener taskListener,
                                    final Set<JiraIssueMetaData> jiraIssues) {

        try (JiraRestClient restClient = getJiraRestClient(jiraSite,
                taskListener)) {
            for (String jiraIssueId : jiraIssuesIds) {
                Issue issue = restClient.getIssueClient().getIssue(jiraIssueId)
                        .claim();
                jiraIssues.add(new JiraIssueMetaData(issue.getKey(), issue
                        .getSummary(), issue.getStatus().getName()));
            }
        } catch (IOException ex) {
            String cannotCloseClientMsg = "Cannot close the JIRA Rest Client instanse.";
            log.log(Level.SEVERE, cannotCloseClientMsg);
            taskListener.getLogger().append(cannotCloseClientMsg + "\n" + ex);
        }
    }

    private JiraRestClient getJiraRestClient(final JiraSite jiraSite,
                                             final TaskListener taskListener) {
        AuthenticationHandler authenticationHandler = new BasicHttpAuthenticationHandler(
                jiraSite.userName, jiraSite.password);
        JiraRestClientFactory restClientFactory = new AsynchronousJiraRestClientFactory();
        return restClientFactory.create(getJiraUri(jiraSite, taskListener),
                authenticationHandler);
    }

    private URI getJiraUri(final JiraSite jiraSite,
                           final TaskListener taskListener) {
        URI jiraUri = null;
        try {
            jiraUri = jiraSite.url.toURI();
        } catch (URISyntaxException ex) {
            String uriSyntaxExceptionMsg = format(
                    "JIRA site URL %s cannot be converted to a URI.\n",
                    jiraSite.url);
            log.log(Level.SEVERE, uriSyntaxExceptionMsg);
            taskListener.getLogger().append(uriSyntaxExceptionMsg + "\n" + ex);
        }
        return jiraUri;
    }
}