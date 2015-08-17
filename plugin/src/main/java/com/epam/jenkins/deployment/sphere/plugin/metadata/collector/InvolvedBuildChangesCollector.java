package com.epam.jenkins.deployment.sphere.plugin.metadata.collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import lombok.extern.java.Log;

import com.google.inject.Singleton;

@Log
@Singleton
public class InvolvedBuildChangesCollector implements Collector<List<? extends Entry>> {

    @Override
    public List<? extends Entry> collect(final AbstractBuild<?, ?> build, final TaskListener taskListener) {
        List<Entry> allInvolvedChanges = new ArrayList<>();

        for (AbstractBuild<?, ?> involvedBuild : getAllInvolvedBuilds(build, taskListener)) {
            ChangeLogSet<? extends Entry> involvedBuildChanges = involvedBuild.getChangeSet();

            for (Entry buildChange : involvedBuildChanges) {
                allInvolvedChanges.add(buildChange);
            }
        }

        return allInvolvedChanges;
    }

    // We need all builds from current build (inclusively) up to last successful build (exclusively)
    private Set<AbstractBuild<?, ?>> getAllInvolvedBuilds(final AbstractBuild<?, ?> build,
                                                          final TaskListener taskListener) {

        Set<AbstractBuild<?, ?>> involvedBuilds = new HashSet<>();
        involvedBuilds.add(build); // We need current build

        AbstractBuild<?, ?> theBuild = build;
        while (isBuildExistingAndUnsuccessful(theBuild)) {
            involvedBuilds.add(theBuild);
            theBuild = theBuild.getPreviousBuild();
        }

        return involvedBuilds;
    }

    private boolean isBuildExistingAndUnsuccessful(final AbstractBuild<?, ?> build) {
        return build != null && (build.getResult() != Result.SUCCESS);
    }
}