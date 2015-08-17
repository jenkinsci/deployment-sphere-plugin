package com.epam.jenkins.deployment.sphere.plugin.metadata.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JiraIssueIdComparatorTest {

    private JiraIssueIdComparator jiraIssueIdComparator = new JiraIssueIdComparator();

    @Test
    public void shouldReturnNegativeValueWhenTheFirstIdPrecedesTheSecondId() {
        JiraIssueMetaData issue_1 = new JiraIssueMetaData("ABC-2", null, null);
        JiraIssueMetaData issue_2 = new JiraIssueMetaData("BBC-1", null, null);

        int actual = jiraIssueIdComparator.compare(issue_1, issue_2);

        assertTrue("Returned value should be negative", actual < 0);
    }

    @Test
    public void shouldReturnZeroWhenBothIdsAreEqual() {
        JiraIssueMetaData issue_1 = new JiraIssueMetaData("ABC-1", null, null);
        JiraIssueMetaData issue_2 = new JiraIssueMetaData("ABC-1", null, null);
        int expected = 0;

        int actual = jiraIssueIdComparator.compare(issue_1, issue_2);

        assertEquals("Returned value should be zero", expected, actual);
    }

    @Test
    public void shouldReturnPositiveValueWhenTheFirstIdFollowsTheSecondId() {
        JiraIssueMetaData issue_1 = new JiraIssueMetaData("ABC-2", null, null);
        JiraIssueMetaData issue_2 = new JiraIssueMetaData("ABC-1", null, null);

        int actual = jiraIssueIdComparator.compare(issue_1, issue_2);

        assertTrue("Returned value should be positive", actual > 0);
    }

}
