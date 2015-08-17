package com.epam.jenkins.deployment.sphere.plugin.metadata.collector;

import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epam.jenkins.deployment.sphere.plugin.mock.EmptyChangeLogSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class InvolvedBuildChangesCollectorTest {

    private InvolvedBuildChangesCollector involvedBuildChangesCollector = new InvolvedBuildChangesCollector();

    @Mock
    private AbstractBuild<?, ?> currentBuild;

    @Mock
    private TaskListener taskListener;

    @Mock
    ChangeLogSet<? extends Entry> currentChangeLogSet;

    @Mock
    Iterator<? extends Entry> currentIterator;

    @Mock
    Entry entry;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(taskListener.getLogger()).thenReturn(new PrintStream(System.out));
    }

    @Test
    public void shouldReturnAnEmptyListWhenNoChangesWereMade() {
        doReturn(new EmptyChangeLogSet(currentBuild)).when(currentBuild).getChangeSet();

        List<? extends Entry> entries = involvedBuildChangesCollector.collect(currentBuild, taskListener);

        assertTrue("The list of changes from involved builds should be empty.", entries.isEmpty());
    }

    @Test
    public void shouldReturnTheChangesWhereMade() {
        doReturn(currentChangeLogSet).when(currentBuild).getChangeSet();
        doReturn(currentIterator).when(currentChangeLogSet).iterator();
        when(currentIterator.hasNext()).thenReturn(true, false);
        doReturn(entry).when(currentIterator).next();

        List<? extends Entry> entries = involvedBuildChangesCollector.collect(currentBuild, taskListener);

        assertFalse("The list of changes should contain the entry.", entries.isEmpty());
        assertTrue("The list of changes should contain only a single entry.", entries.size() == 1);
        assertTrue("The list of changes should contain the passed entry.", entries.contains(entry));
    }

}
