package com.epam.jenkins.deployment.sphere.plugin;

import static java.lang.String.format;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.logging.Level;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import jenkins.model.Jenkins;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.action.DynamicVariablesStoringAction;
import com.epam.jenkins.deployment.sphere.plugin.metadata.Constants;
import com.epam.jenkins.deployment.sphere.plugin.metadata.collector.Collector;
import com.epam.jenkins.deployment.sphere.plugin.metadata.collector.DeployVersionMetaDataCollector;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.DeploymentMetaData;
import com.google.common.base.Strings;

@Log
public class DeployVersionMetaDataPublisher extends hudson.tasks.Notifier {

    @DataBoundSetter
    private String deployedAppName;

    @DataBoundConstructor
    public DeployVersionMetaDataPublisher() {
        Jenkins.getInstance().getInjector().injectMembers(this);
    }

    public DeployVersionMetaDataPublisher(String deployedAppName) {
        this.deployedAppName = deployedAppName;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public String getDeployedAppName() {
        return deployedAppName;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
            throws InterruptedException, IOException {
        log.log(Level.FINE, "Is about to collect deploy metadata");

        checkArgument(null != listener, "Listener is null, something was wrong.");
        checkArgument(null != build, "Current build is null, something was wrong.");

        listener.getLogger().append("[deployment-sphere] Collecting deploy metadata\n");
        repopulateEnvValues(build);
        Collector<DeploymentMetaData> collector = new DeployVersionMetaDataCollector();
        DeploymentMetaData metaData = collector.collect(build, listener);

        log.log(Level.FINEST, format("New deploy metadata has been collected %s", metaData));

        return true;
    }

    private void repopulateEnvValues(final AbstractBuild<?, ?> build) {
        getDescriptor().load();
        final String appName = getDeployedAppName();
        checkState(!Strings.isNullOrEmpty(appName), String.format("Invalid application name %s", appName));
        build.addAction(new DynamicVariablesStoringAction(Constants.BUILD_APP_NAME, appName));
    }

}
