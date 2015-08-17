package com.epam.jenkins.deployment.sphere.plugin.metadata.collector;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;

public interface Collector<T> {

    T collect(AbstractBuild<?, ?> build, TaskListener taskListener);
}
