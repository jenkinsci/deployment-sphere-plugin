package com.epam.jenkins.deployment.sphere.plugin.metadata.scm;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogSet.Entry;

import com.epam.jenkins.deployment.sphere.plugin.metadata.collector.Collector;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.CommitMetaData;
import com.google.inject.Singleton;

@Singleton
public class CommitMetaDataCollector implements Collector<Set<CommitMetaData>> {

    @Override
    public Set<CommitMetaData> collect(AbstractBuild<?, ?> build, TaskListener taskListener) {
        AbstractBuild<?, ?> tmpBuild = build;
        return collectCommits(tmpBuild);
    }

    private Set<CommitMetaData> collectCommits(final AbstractBuild<?, ?> build) {
        Set<CommitMetaData> commits = Collections.emptySet();
        if (Result.SUCCESS == build.getResult()) {
            commits.addAll(getCommits(build));
        } else {
            commits.addAll(getFailedBuildCommits(build));
        }
        return commits;

    }

    private Set<CommitMetaData> getFailedBuildCommits(AbstractBuild<?, ?> tmpBuild) {
        Set<CommitMetaData> commits = Collections.emptySet();
        while (tmpBuild != null && Result.SUCCESS != tmpBuild.getResult()) {
            commits.addAll(getCommits(tmpBuild));
            tmpBuild = tmpBuild.getPreviousBuild();
        }
        return commits;
    }

    private Set<CommitMetaData> getCommits(AbstractBuild<?, ?> build) {
        Set<CommitMetaData> commits = Collections.emptySet();
        Iterator<? extends Entry> iterator = build.getChangeSet().iterator();
        while (iterator.hasNext()) {
            commits.add(buildCommit(iterator.next()));
        }
        return commits;
    }

    private CommitMetaData buildCommit(final Entry commitEntry) {
        CommitMetaData commit = new CommitMetaData();
        commit.setAuthor(commitEntry.getAuthor().getFullName());
        commit.setId(commitEntry.getCommitId());
        commit.setMessage(commitEntry.getMsg());
        return commit;
    }

}
